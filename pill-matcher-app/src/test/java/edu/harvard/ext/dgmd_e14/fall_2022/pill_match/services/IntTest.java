package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.PillMatcherApplication;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories.PillRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = PillMatcherApplication.class)
@AutoConfigureMockMvc
public class IntTest {

    @Autowired
    private PillRepository pillRepository;

    @Autowired
    private PredictionService predictionService;

    HashMap<String, Integer> matchPillsByPredictedImprints() throws IOException {

        var br = new BufferedReader(new FileReader("C:\\dev\\Classes\\DGMD14\\pill-project\\pill-matcher-app\\src\\test\\resources\\split_splimage_all_PillSer_only.csv"));

        var fileNameToPillSerMap = new HashMap<String, Integer>();

        String line;

        while ((line = br.readLine()) != null) {
            String[] csvContent = line.split(",");
            fileNameToPillSerMap.put(csvContent[0].replace("\"",""), Integer.parseInt(csvContent[1]));
        }

        return fileNameToPillSerMap;
    }

    @Test
    public void testRandomPillSet() throws IOException {

        var fileNameToPillSerMap = matchPillsByPredictedImprints();

        var rand = new Random();
        var allPillEntries = fileNameToPillSerMap.entrySet().stream().toList();
        var selectedPillEntries = new ArrayList<Map.Entry<String, Integer>>();
        var usedIds = new ArrayList<Integer>();

        int numberRandSample = 5;

        for (int i = 0; i < numberRandSample; i++) {
            var randomIndex = rand.nextInt(allPillEntries.size());
            var randomPillEntry = allPillEntries.get(randomIndex);

            var randomPillEntryId =  randomPillEntry.getValue();
            if (!usedIds.contains(randomPillEntryId)) {
                usedIds.add(randomPillEntryId);
                selectedPillEntries.add(randomPillEntry);
            } else {
                i--;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<h1>Randomized Pill Prediction:</h1>");
        stringBuilder.append("</br>");
        stringBuilder.append("</br>");

        var pillCounter = 0;

        var totalPills = selectedPillEntries.stream().count();
        var totalCorrectMatches = 0;
        var totalNumberCorrectShapeMatches = 0;
        var totalNumberCorrectAtLeastPartialColorMatches = 0;
        var totalNumberCorrectAtLeastPartialImprintMatches = 0;
        var totalAvailableImprints = 0;

        for (var pillEntry: selectedPillEntries) {

            pillCounter++;

            var pillFromDb = pillRepository.findById(Integer.toUnsignedLong(pillEntry.getValue()));
            var filePath = "C:\\Users\\lgvii\\Desktop\\pills\\all_square\\" + pillEntry.getKey();

            System.out.println("Testing: " + pillEntry.getKey());

            var predictions = predictionService.getPredictions(Paths.get(filePath)).entrySet();


            stringBuilder.append("<h2>Pill #" + pillCounter + "</h3>");
            stringBuilder.append("</br>");
            stringBuilder.append("</br>");

            stringBuilder.append("<h3>Pill Database Properties</h3>");
            stringBuilder.append("</br>");
            stringBuilder.append("</br>");

            stringBuilder.append("<b>Photo File: </b>");
            stringBuilder.append("<i>");
            stringBuilder.append(pillEntry.getKey());
            stringBuilder.append("</i>");
            stringBuilder.append("</br>");
            stringBuilder.append("<img style=\"height: 300px;\" src=\"" + filePath + "\">");
            stringBuilder.append("</br>");

            stringBuilder.append("<b>Pill Proprietary Name: </b>");
            stringBuilder.append("<i>");
            stringBuilder.append(pillFromDb.get().getProprietaryName());
            stringBuilder.append("</i>");
            stringBuilder.append("</br>");

            stringBuilder.append("<b>Pill Generic Name: </b>");
            stringBuilder.append("<i>");
            stringBuilder.append(pillFromDb.get().getGenericName());
            stringBuilder.append("</i>");
            stringBuilder.append("</br>");

            if (pillFromDb.get().getImprint() != null) {
                totalAvailableImprints++;

                stringBuilder.append("<b>Pill Imprint: </b>");
                stringBuilder.append("<i>");
                stringBuilder.append(pillFromDb.get().getImprint());
                stringBuilder.append("</i>");
                stringBuilder.append("</br>");
            } else {
                stringBuilder.append("<b>NO IMPRINT</b>");
                stringBuilder.append("</br>");
            }

            stringBuilder.append("<b>Pill Shape: </b>");
            stringBuilder.append("<i>");
            stringBuilder.append(pillFromDb.get().getShape());
            stringBuilder.append("</i>");
            stringBuilder.append("</br>");

            stringBuilder.append("<b>Pill Color(s): </b>");
            stringBuilder.append("<i>");
            stringBuilder.append(String.join(", ", pillFromDb.get().getColors()));
            stringBuilder.append("</i>");
            stringBuilder.append("</br>");

            stringBuilder.append("</br>");
            stringBuilder.append("<h3>Pill Predictions</h3>");
            stringBuilder.append("</br>");
            stringBuilder.append("</br>");

            var pillPreNum = 0;
            var imprintFound = false;
            var colorFound = false;
            var shapeFound = false;

            for (var prediction : predictions) {

                pillPreNum++;

                stringBuilder.append("</br>");
                stringBuilder.append("<h4>Pill Prediction #" + pillPreNum + "</h4>");

                stringBuilder.append("</br>");

                stringBuilder.append("<b>Pill Match Probability: </b>");
                stringBuilder.append("<i>");
                stringBuilder.append(prediction.getKey().getProprietaryName());
                stringBuilder.append("</i>");
                stringBuilder.append("</br>");

                stringBuilder.append("<b>Pill Proprietary Name: </b>");
                stringBuilder.append("<i>");
                stringBuilder.append(prediction.getKey().getProprietaryName());
                stringBuilder.append("</i>");
                stringBuilder.append("</br>");

                stringBuilder.append("<b>Pill Generic Name: </b>");
                stringBuilder.append("<i>");
                stringBuilder.append(prediction.getKey().getGenericName());
                stringBuilder.append("</i>");
                stringBuilder.append("</br>");

                totalNumberCorrectShapeMatches = 0;
                totalNumberCorrectAtLeastPartialColorMatches = 0;
                totalNumberCorrectAtLeastPartialImprintMatches = 0;

                if (pillFromDb.get().getImprint() != null) {

                    stringBuilder.append("<b>Pill Imprint: </b>");
                    stringBuilder.append("<i>");
                    stringBuilder.append(prediction.getKey().getImprint());
                    stringBuilder.append("</i>");
                    stringBuilder.append("</br>");

                    if (prediction.getKey().getImprint() != null && pillFromDb.get().getImprint().contains(prediction.getKey().getImprint())) {
                        imprintFound = true;
                        stringBuilder.append("<i style=\"color: blue;\">**YES MATCH*** At least partial match found.</i>");
                        stringBuilder.append("</br>");
                    } else {
                        stringBuilder.append("<i style=\"color: orange;\">***NO MATCH***: No full or partial match found.</i>");
                        stringBuilder.append("</br>");
                    }
                } else {
                    stringBuilder.append("<b>NO IMPRINT</b>");
                    stringBuilder.append("</br>");
                }

                stringBuilder.append("<b>Pill Shape: </b>");
                stringBuilder.append("<i>");
                stringBuilder.append(prediction.getKey().getShape());
                stringBuilder.append("</i>");
                stringBuilder.append("</br>");

                if (pillFromDb.get().getShape().toLowerCase() == prediction.getKey().getShape().toLowerCase()) {
                    shapeFound = true;
                    stringBuilder.append("<i style=\"color: blue;\">**YES MATCH*** At least partial match found.</i>");
                    stringBuilder.append("</br>");
                } else {
                    stringBuilder.append("<i style=\"color: orange;\">***NO MATCH***: No full or partial match found.</i>");
                    stringBuilder.append("</br>");
                }


                    stringBuilder.append("<b>Pill Color: </b>");
                    stringBuilder.append("<i>");
                    stringBuilder.append(prediction.getKey().getColors());
                    stringBuilder.append("</i>");
                    stringBuilder.append("</br>");

                for (var color : prediction.getKey().getColors()) {
                    for (var expectedColor:pillFromDb.get().getColors()) {
                        if (expectedColor.toLowerCase().contains(color.toLowerCase())) {
                            colorFound = true;
                            stringBuilder.append("<i style=\"color: blue;\">**YES MATCH*** At least partial match found.</i>");
                            stringBuilder.append("</br>");
                        } else {
                            stringBuilder.append("<i style=\"color: orange;\">***NO MATCH***: No full or partial match found.</i>");
                            stringBuilder.append("</br>");
                        }
                    }
                }

                var isMatch = prediction.getKey().getId() == pillFromDb.get().getId();
                if (isMatch){
                    totalCorrectMatches++;
                }

                stringBuilder.append("</br>");
                stringBuilder.append("<b>Is this a correct match?</b>");
                stringBuilder.append("<i>");
                stringBuilder.append(isMatch ? "<i style=\"color: green;\"> YES</i>" : "<i style=\"color: red;\"> NO</i>");
                stringBuilder.append("</i>");
                stringBuilder.append("</br>");
            }
            if (imprintFound) {
                totalNumberCorrectAtLeastPartialImprintMatches++;
            }
            if (colorFound) {
                totalNumberCorrectAtLeastPartialColorMatches++;
            }
            if (shapeFound) {
                totalNumberCorrectShapeMatches++;
            }
        }

            stringBuilder.append("<h3>Pill Prediction Stats</h3>");
            stringBuilder.append("</br>");
            stringBuilder.append("</br>");

        stringBuilder.append("<b>Number of matched pills: </b>");
        stringBuilder.append("<i>");
        stringBuilder.append(totalCorrectMatches);
        stringBuilder.append(" of ");
        stringBuilder.append(totalPills);
        stringBuilder.append(" total pills in test set, percentage accuracy: ");
        stringBuilder.append(totalCorrectMatches/totalPills);
        stringBuilder.append("%</i>");
        stringBuilder.append("</br>");

        stringBuilder.append("<b>Number of at least partial imprint matches: </b>");
        stringBuilder.append("<i>");
        stringBuilder.append(totalNumberCorrectAtLeastPartialImprintMatches);
        stringBuilder.append(" of ");
        stringBuilder.append(totalAvailableImprints);
        stringBuilder.append("(pills that have imprints in test set), percentage: accuracy ");
        stringBuilder.append(totalNumberCorrectAtLeastPartialImprintMatches/totalAvailableImprints);
        stringBuilder.append("%</i>");
        stringBuilder.append("</br>");

        stringBuilder.append("<b>Number of at least partial color matches: </b>");
        stringBuilder.append("<i>");
        stringBuilder.append(totalNumberCorrectAtLeastPartialColorMatches);
        stringBuilder.append(" of ");
        stringBuilder.append(totalPills);
        stringBuilder.append("total pills in test set, percentage: accuracy ");
        stringBuilder.append(totalNumberCorrectAtLeastPartialColorMatches/totalPills);
        stringBuilder.append("%</i>");
        stringBuilder.append("</br>");

        stringBuilder.append("<b>Number of at least partial shape matches: </b>");
        stringBuilder.append("<i>");
        stringBuilder.append(totalNumberCorrectShapeMatches);
        stringBuilder.append(" of ");
        stringBuilder.append(totalPills);
        stringBuilder.append("total pills in test set, percentage: accuracy ");
        stringBuilder.append(totalNumberCorrectShapeMatches/totalPills);
        stringBuilder.append("%</i>");
        stringBuilder.append("</br>");

            BufferedWriter writer = new BufferedWriter(new FileWriter("Randomized_Test_Report.html"));
            writer.write(stringBuilder.toString());
            writer.close();

        assertThat(fileNameToPillSerMap.isEmpty(), is(false));
    }
}