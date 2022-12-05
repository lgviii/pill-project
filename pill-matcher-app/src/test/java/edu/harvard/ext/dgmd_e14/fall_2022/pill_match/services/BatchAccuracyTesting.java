package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.ImageModelOutput;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.PillMatcherApplication;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories.PillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = PillMatcherApplication.class)
@AutoConfigureMockMvc
public class BatchAccuracyTesting {

    @Autowired
    private PillRepository pillRepository;

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private PillMatcherService pillMatcherService;

    private String testFilePath = "C:\\Users\\lgvii\\Desktop\\pills\\all_square\\";

    // Tests a random subset of input data
    // To run all data change numberRandSample to represent the complete data size
    @Test
    public void testRandomPillSet() throws IOException {

        var fileNameToPillSerMap = matchPillsByPredictedImprints(
                "split_splimage_all_PillSer_only.csv"
        );

        // get a random sample
        var rand = new Random();
        var allPillEntries = fileNameToPillSerMap.entrySet().stream().toList();
        var selectedPillEntries = new ArrayList<Map.Entry<String, Integer>>();
        var usedIds = new ArrayList<Integer>();

        int numberRandSample = 5;

        // make sure to skip any missing image files
        for (int i = 0; i < numberRandSample; i++) {
            var randomIndex = rand.nextInt(allPillEntries.size());
            var randomPillEntry = allPillEntries.get(randomIndex);

            File f = new File(testFilePath + randomPillEntry.getKey());

            if (f.exists() && !f.isDirectory()) {

                var randomPillEntryId = randomPillEntry.getValue();

                if (!usedIds.contains(randomPillEntryId)) {
                    usedIds.add(randomPillEntryId);
                    selectedPillEntries.add(randomPillEntry);
                } else {
                    i--;
                }
            } else {
                i--;
            }
        }

        runPillTestsHtmlReport(selectedPillEntries, "Randomized Pill Prediction: " + numberRandSample + " Pills",
                               "test_results/Randomized_Test_Report.html");

        assertThat(fileNameToPillSerMap.isEmpty(), is(false));
    }

    @Test
    public void testAllPillsInCsv() throws IOException {

        var fileNameToPillSerMap = matchPillsByPredictedImprints(
                "split_splimage_front_PillSer_only.csv"
        );

        var allPillEntries = fileNameToPillSerMap.entrySet().stream().toList();
        var selectedPillEntries = new ArrayList<Map.Entry<String, Integer>>();
        var usedIds = new ArrayList<Integer>();

        int nPills = allPillEntries.size();

        // make sure to skip any missing image files
        for (int i = 0; i < nPills; i++) {
            var pillEntry = allPillEntries.get(i);

            File f = new File(testFilePath + pillEntry.getKey());

            if (f.exists() && !f.isDirectory()) {

                var pillEntryId = pillEntry.getValue();

                if (!usedIds.contains(pillEntryId)) {
                    usedIds.add(pillEntryId);
                    selectedPillEntries.add(pillEntry);
                }
            }
        }
        runPillTestsCsvReport(selectedPillEntries,
                              "test_results/split_spl_front_predictions_top"
                              + PillMatcherService.PILL_MATCH_LIMIT + ".csv");
        assertThat(fileNameToPillSerMap.isEmpty(), is(false));
    }

    HashMap<String, Integer> matchPillsByPredictedImprints(String csvFilePath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        try (var inputStream = classLoader.getResourceAsStream(csvFilePath);
             var br = new BufferedReader(new InputStreamReader(inputStream))) {
            var fileNameToPillSerMap = new LinkedHashMap<String, Integer>();

            String line;

            while ((line = br.readLine()) != null) {
                String[] csvContent = line.split(",");
                fileNameToPillSerMap.put(csvContent[0].replace("\"", ""), Integer.parseInt(csvContent[1]));
            }

            return fileNameToPillSerMap;
        }
    }

    private void runPillTestsHtmlReport(List<Map.Entry<String, Integer>> selectedPillEntries, String title,
                                        String outputFile) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<h1>" + title + "</h1>");
        stringBuilder.append("</br>");
        stringBuilder.append("</br>");

        var pillCounter = 0;

        var totalPills = selectedPillEntries.stream().count();
        var totalCorrectMatches = 0;
        var totalNumberCorrectShapeMatches = 0;
        var totalNumberCorrectAtLeastPartialColorMatches = 0;
        var totalNumberCorrectAtLeastPartialImprintMatches = 0;
        var totalAvailableImprints = 0;

        var topShapeMatch = 0;
        var topColorMatch = 0;
        var imprintExactMatch = 0;

        for (var pillEntry: selectedPillEntries) {

            pillCounter++;

            var pillFromDb = pillRepository.findById(Integer.toUnsignedLong(pillEntry.getValue()));
            var filePath = testFilePath + pillEntry.getKey();
            var fileName = pillEntry.getKey();

            System.out.println("Testing: " + pillEntry.getKey());

            var formattedResponse = predictionService.getFormattedResponse(Paths.get(filePath));

            var predictionResponse = formattedResponse.stream().findFirst().get();

            var colorList = new ArrayList<>(predictionResponse.getColorModelMatches().entrySet().stream().toList());
            colorList.sort(Comparator.comparingDouble(Map.Entry::getValue));
            Collections.reverse(colorList);

            var topColorPrediction =colorList.stream().findFirst().get();

            var shapeList = new ArrayList<>(predictionResponse.getShapeModelMatches().entrySet().stream().toList());
            shapeList.sort(Comparator.comparingDouble(Map.Entry::getValue));
            Collections.reverse(shapeList);

            var topShapePrediction = shapeList.stream().findFirst().get();

            var predictions = pillMatcherService.findMatchingPills(formattedResponse).entrySet();

            pillInfoHtmlBlock(stringBuilder, pillCounter, pillEntry, pillFromDb, fileName);

            totalAvailableImprints = predictionsHeaderHtmlBlock(stringBuilder, totalAvailableImprints, pillFromDb);

            var imprintPredictionsList = predictionResponse.getImprintPredictions().stream().toList();
            var textOcrPredicted = String.join("", imprintPredictionsList);
            stringBuilder.append("<b>Text Predicted: </b>");
            stringBuilder.append("<i>");
            stringBuilder.append(textOcrPredicted);
            stringBuilder.append("</i>");
            stringBuilder.append("</br>");


            stringBuilder.append("<b>Does imprint predicted imprint match actual exactly?: </b>");
            if (pillMatcherService.doesPredictionMatchImprint(imprintPredictionsList, pillFromDb.get().getImprint())) {
                imprintExactMatch++;
                stringBuilder.append("<i style=\"color: blue;\">**YES MATCH*** </i>");
                stringBuilder.append("</br>");
            } else {
                stringBuilder.append("<i style=\"color: orange;\">***NO TARGET MATCH***</i>");
                stringBuilder.append("</br>");
            }

            predictedHtmlBlock(stringBuilder, "<b>Top Shape Predicted: </b>", topShapePrediction);

            var matchesTopShape = topShapePrediction.getKey().equalsIgnoreCase(pillFromDb.get().getShape());

            stringBuilder.append("<b>Does top shape prediction match actual?: </b>");
            if (matchesTopShape) {
                topShapeMatch++;
                stringBuilder.append("<i style=\"color: blue;\">**YES MATCH*** </i>");
                stringBuilder.append("</br>");
            } else {
                stringBuilder.append("<i style=\"color: orange;\">***NO TARGET MATCH***</i>");
                stringBuilder.append("</br>");
            }

            predictedHtmlBlock(stringBuilder, "<b>Top Color Predicted: </b>", topColorPrediction);

            var topColor = topColorPrediction.getKey();
            var pillFromDbList = pillFromDb.get().getColors().stream().toList();
            var firstColor = pillFromDbList.size() > 0 ? pillFromDbList.get(0) : "";
            var secondColor = pillFromDbList.size() > 1 ? pillFromDbList.get(1) : "";
            var matchesTopColor = topColor.equalsIgnoreCase(firstColor) || topColor.equalsIgnoreCase(secondColor);

            stringBuilder.append("<b>Does top shape prediction match actual at least partially?: </b>");
            if (matchesTopColor) {
                topColorMatch++;
                stringBuilder.append("<i style=\"color: blue;\">**YES MATCH*** </i>");
                stringBuilder.append("</br>");
            } else {
                stringBuilder.append("<i style=\"color: orange;\">***NO TARGET MATCH***</i>");
                stringBuilder.append("</br>");
            }

            stringBuilder.append("</div>");

            stringBuilder.append("<div style=\"border:2px solid Orange;\">");

            stringBuilder.append("</br>");
            stringBuilder.append("<h3><u>Pill Predictions</u></h3>");
            stringBuilder.append("</br>");

            var pillPreNum = 0;
            var imprintFound = false;
            var colorFound = false;
            var shapeFound = false;

            if (predictions.isEmpty())
            {
                stringBuilder.append("<b style=\"color: white;background-color: purple\">*NO PREDICTION can be made*</b>");
            }

            for (var prediction : predictions) {

                pillPreNum++;
                totalNumberCorrectShapeMatches = 0;
                totalNumberCorrectAtLeastPartialColorMatches = 0;
                totalNumberCorrectAtLeastPartialImprintMatches = 0;

                getPredictionHtml(stringBuilder, pillPreNum, prediction);

                if (pillFromDb.get().getImprint() != null) {

                    stringBuilder.append("<b>Pill Imprint: </b>");
                    stringBuilder.append("<i>");
                    stringBuilder.append(prediction.getKey().getImprint());
                    stringBuilder.append("</i>");
                    stringBuilder.append("</br>");

                    if (prediction.getKey().getImprint() != null && pillFromDb.get().getImprint().contains(prediction.getKey().getImprint())) {
                        imprintFound = true;
                        stringBuilder.append("<i style=\"color: blue;\">***TARGET MATCH***</i>");
                        stringBuilder.append("</br>");
                    } else {
                        stringBuilder.append("<i style=\"color: orange;\">***NOT TARGET MATCH***</i>");
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

                if (pillFromDb.get().getShape().equalsIgnoreCase(prediction.getKey().getShape())) {
                    shapeFound = true;
                    stringBuilder.append("<i style=\"color: blue;\">***TARGET MATCH***</i>");
                    stringBuilder.append("</br>");
                } else {
                    stringBuilder.append("<i style=\"color: orange;\">***NOT TARGET MATCH***</i>");
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
                            stringBuilder.append("<i style=\"color: blue;\">***TARGET MATCH***</i>");
                            stringBuilder.append("</br>");
                        } else {
                            stringBuilder.append("<i style=\"color: orange;\">***NOT TARGET MATCH***</i>");
                            stringBuilder.append("</br>");
                        }
                    }
                }

                var isMatch = prediction.getKey().getId().equals(pillFromDb.get().getId());
                if (isMatch){
                    totalCorrectMatches++;
                }

                stringBuilder.append("</br>");
                stringBuilder.append("<b>Is this a correct match?</b>");
                stringBuilder.append("<i>");
                stringBuilder.append(isMatch ? "<i style=\"color: white;background-color:green;\"> PILL MATCH</i>" : "<i style=\"color: white;background-color:red;\"> NO PILL MATCH</i>");
                stringBuilder.append("</i>");
                stringBuilder.append("</br>");
                stringBuilder.append("</div>");
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

        endHtmlReport(stringBuilder, totalPills, totalCorrectMatches, topShapeMatch, topColorMatch, imprintExactMatch);

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(stringBuilder.toString());
        writer.close();
    }

    private static int predictionsHeaderHtmlBlock(StringBuilder stringBuilder, int totalAvailableImprints, Optional<Pill> pillFromDb) {
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

        stringBuilder.append("</div>");

        stringBuilder.append("<div style=\"border:2px solid Orange;\">");

        stringBuilder.append("<h3><u>Model Predictions</u></h3>");
        stringBuilder.append("</br>");
        return totalAvailableImprints;
    }

    private static void pillInfoHtmlBlock(StringBuilder stringBuilder, int pillCounter, Map.Entry<String, Integer> pillEntry, Optional<Pill> pillFromDb, String fileName) {
        stringBuilder.append("</br>");
        stringBuilder.append("<h2 style=\"background-color:DodgerBlue; color:white;\">Pill #" + pillCounter + "</h3>");

        stringBuilder.append("<div style=\"border:2px solid Tomato;\">");
        stringBuilder.append("<h3><u>Pill Database Properties</u></h3>");
        stringBuilder.append("</br>");

        stringBuilder.append("<b>Photo File: </b>");
        stringBuilder.append("<i>");
        stringBuilder.append(pillEntry.getKey());
        stringBuilder.append("</i>");
        stringBuilder.append("</br>");
        stringBuilder.append("<img style=\"height: 300px;\" src=\"http://127.0.0.1:7001/static/" + fileName +
                             "\">");
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
    }

    private static void predictedHtmlBlock(StringBuilder stringBuilder, String str, Map.Entry<String, Double> topColorPrediction) {
        stringBuilder.append("</br>");
        stringBuilder.append(str);
        stringBuilder.append("<i>");
        stringBuilder.append(topColorPrediction.getKey());
        stringBuilder.append(", percentage: ");
        stringBuilder.append(topColorPrediction.getValue() * 100);
        stringBuilder.append("%");
        stringBuilder.append("</i>");
        stringBuilder.append("</br>");
    }

    private static void endHtmlReport(StringBuilder stringBuilder, long totalPills, int totalCorrectMatches, int topShapeMatch, int topColorMatch, int imprintExactMatch) {
        stringBuilder.append("</br>");
        stringBuilder.append("</br>");
        stringBuilder.append("<h3 style=\"background-color:Orange; color:white;\">Pill Prediction Stats</h3>");

        stringBuilder.append("<b>Number of matched pills: </b>");
        stringBuilder.append("<i>");
        stringBuilder.append(totalCorrectMatches);
        stringBuilder.append(" of ");
        stringBuilder.append(totalPills);
        stringBuilder.append(" total pills in test set, percentage accuracy: ");
        stringBuilder.append(((float) totalCorrectMatches / totalPills)*100);
        stringBuilder.append("%</i>");
        stringBuilder.append("</br>");

        stringBuilder.append("<b>Number of exact imprint matches: </b>");
        stringBuilder.append("<i>");
        stringBuilder.append(imprintExactMatch);
        stringBuilder.append(" of ");
        stringBuilder.append(totalPills);
        stringBuilder.append(" total pills in test set, percentage accuracy: ");
        stringBuilder.append(((float) imprintExactMatch / totalPills)*100);
        stringBuilder.append("%</i>");
        stringBuilder.append("</br>");

        stringBuilder.append("<b>Number of top predicted shape matches: </b>");
        stringBuilder.append("<i>");
        stringBuilder.append(topShapeMatch);
        stringBuilder.append(" of ");
        stringBuilder.append(totalPills);
        stringBuilder.append(" total pills in test set, percentage accuracy: ");
        stringBuilder.append(((float) topShapeMatch / totalPills)*100);
        stringBuilder.append("%</i>");
        stringBuilder.append("</br>");

        stringBuilder.append("<b>Number of top predicted color matches: </b>");
        stringBuilder.append("<i>");
        stringBuilder.append(topColorMatch);
        stringBuilder.append(" of ");
        stringBuilder.append(totalPills);
        stringBuilder.append(" total pills in test set, percentage accuracy: ");
        stringBuilder.append(((float) topColorMatch / totalPills)*100);
        stringBuilder.append("%</i>");
        stringBuilder.append("</br>");
    }

    private static void getPredictionHtml(StringBuilder stringBuilder, int pillPreNum, Map.Entry<Pill, Double> prediction) {
        stringBuilder.append("</br>");
        stringBuilder.append("<h4 style=\"background-color:Grey; color:white;\">Pill Prediction #" + pillPreNum + "</h4>");

        stringBuilder.append("</br>");

        stringBuilder.append("<b>Pill Match Probability: </b>");
        stringBuilder.append("<i>");
        stringBuilder.append(prediction.getValue());
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
    }

    private void runPillTestsCsvReport(List<Map.Entry<String, Integer>> pillImageToPillSerMap,
                                       String outputFile) throws IOException {
        var maxMatches = PillMatcherService.PILL_MATCH_LIMIT;
        List<String[]> csvLines = new ArrayList<>();
        // Add the headers
        List<String> headers = new ArrayList<>(Arrays.asList("FileName", "Ndc11", "Part", "GenericName",
                                                             "ActualColor", "ActualShape", "ActualImprint",
                                                             "PredictedColors", "PredictedShapes", "PredictedImprints",
                                                             "PredictedImprint_Match"));
        for (int i = 0; i < maxMatches; i++) {
            var pillNum = i + 1;
            headers.addAll(Arrays.asList("PillMatch" + pillNum + "_Ndc11",
                                         "PillMatch" + pillNum + "_Part",
                                         "PillMatch" + pillNum + "_GenericName",
                                         "PillMatch" + pillNum + "Percent"));
        }
        csvLines.add(headers.toArray(new String[0]));

        var totalPills = pillImageToPillSerMap.size();
        var shapeMatchTop1 = 0;
        var shapeMatchTop2 = 0;
        var colorMatchTop1 = 0;
        var colorMatchTop2 = 0;
        var imprintExactMatch = 0;
        var imprintMatch80 = 0;

        var pillMatchTop1 = 0;
        var pillMatchAny = 0;

        var totalAvailableImprints = 0;

        var imagesWithPillPredictions = 0;

        for (var pillEntry: pillImageToPillSerMap) {

            var csvIndex = 0;

            var pillFromDb = pillRepository.findById(Integer.toUnsignedLong(pillEntry.getValue()))
                                           .orElseThrow(() -> new RuntimeException("Error retrieving Pill entry with "
                                                                                   + "PillSer " + pillEntry.getValue()));
            var filePath = testFilePath + pillEntry.getKey();
            var fileName = pillEntry.getKey();

            System.out.println("Testing: " + pillEntry.getKey());

            Collection<ImageModelOutput> formattedResponse = predictionService.getFormattedResponse(
                    Paths.get(filePath));

            var predictionResponse = formattedResponse.stream().findFirst().get();

            var colorList = new ArrayList<>(predictionResponse.getColorModelMatches().entrySet().stream().toList());
            colorList.sort(Comparator.comparingDouble(Map.Entry::getValue));
            Collections.reverse(colorList);


            var shapeList = new ArrayList<>(predictionResponse.getShapeModelMatches().entrySet().stream().toList());
            shapeList.sort(Comparator.comparingDouble(Map.Entry::getValue));
            Collections.reverse(shapeList);

            var pillMatches = pillMatcherService.findMatchingPills(formattedResponse).entrySet();

            String[] csvRow = new String[headers.size()];
            csvRow[csvIndex++] = pillEntry.getKey();
            csvRow[csvIndex++] = pillFromDb.getNdc11();
            csvRow[csvIndex++] = Integer.toString(pillFromDb.getPart());
            csvRow[csvIndex++] = pillFromDb.getGenericName();
            csvRow[csvIndex++] = String.join(", ", pillFromDb.getColors());
            csvRow[csvIndex++] = pillFromDb.getShape();
            csvRow[csvIndex++] = pillFromDb.getImprint();

            csvRow[csvIndex++] = "{" +
                                 colorList.stream()
                                          .map(mapEntry -> "\"" + mapEntry.getKey() + "\": " + mapEntry.getValue())
                                          .collect(Collectors.joining(", ")) + "}";

            var topColorPrediction = colorList.get(0).getKey();
            if (pillFromDb.getColors().contains(topColorPrediction.toUpperCase())) {
                colorMatchTop1++;
                colorMatchTop2++;
            }
            else {
                var secondColorPrediction = colorList.get(1).getKey();
                if (pillFromDb.getColors().contains(secondColorPrediction.toUpperCase())) {
                    colorMatchTop2++;
                }
            }

            csvRow[csvIndex++] = "{" +
                                 shapeList.stream()
                                          .map(mapEntry -> "\"" + mapEntry.getKey() + "\": " + mapEntry.getValue())
                                          .collect(Collectors.joining(", ")) + "}";

            var topShapePrediction = shapeList.get(0).getKey();
            if (pillFromDb.getShape().equalsIgnoreCase(topShapePrediction)) {
                shapeMatchTop1++;
                shapeMatchTop2++;
            }
            else {
                var secondShapePrediction = shapeList.get(1).getKey();
                if (pillFromDb.getShape().equalsIgnoreCase(secondShapePrediction)) {
                    shapeMatchTop2++;
                }
            }

            var imprintPredictionsList = predictionResponse.getImprintPredictions();
            var textOcrPredicted = String.join(", ", imprintPredictionsList);
            csvRow[csvIndex++] = textOcrPredicted;

            if (pillFromDb.getImprint() != null) {
                totalAvailableImprints++;
            }

            double imprintMatchAccuracy = pillMatcherService.getHighestImprintMatchAccuracy(imprintPredictionsList,
                                                                                            pillFromDb.getImprint());
            csvRow[csvIndex++] = Double.toString(imprintMatchAccuracy);

            if (Math.abs(imprintMatchAccuracy - 1.0) < 1e-5) {
                imprintExactMatch++;
                imprintMatch80++;
            }
            else if (imprintMatchAccuracy >= 0.8) {
                imprintMatch80++;
            }


            // If there are no matching pills, fill in all the pill match entries with an empty String
            if (pillMatches.isEmpty()) {
                for (int i = 0; i < maxMatches * 4; i++) {
                    csvRow[csvIndex++] = "";
                }
            }
            else {
                var pillPreNum = 0;
                imagesWithPillPredictions++;
                for (var pillMatch : pillMatches) {
                    if (pillPreNum >= maxMatches) {
                        break;
                    }

                    Pill predictedPill = pillMatch.getKey();
                    csvRow[csvIndex++] = predictedPill.getNdc11();
                    csvRow[csvIndex++] = Integer.toString(predictedPill.getPart());
                    csvRow[csvIndex++] = predictedPill.getGenericName();
                    csvRow[csvIndex++] = Double.toString(pillMatch.getValue());

                    if (predictedPill.equals(pillFromDb)) {
                        if (pillPreNum == 0) {
                            pillMatchTop1++;
                            pillMatchAny++;
                        }
                        else {
                            pillMatchAny++;
                        }
                    }

                    pillPreNum++;
                }
            }

            csvLines.add(csvRow);
        }

        // Add combined stats as bottom rows in CSV
        csvLines.add(new String[]{"Total number images", Integer.toString(totalPills)});
        csvLines.add(new String[]{"Total color match",
                                  "Top 1", Integer.toString(colorMatchTop1), "Percent Top 1",
                                  Double.toString((double) colorMatchTop1 / totalPills),
                                  "Top 2", Integer.toString(colorMatchTop2), "Percent Top 2",
                                  Double.toString((double) colorMatchTop2 / totalPills)});
        csvLines.add(new String[]{"Total shape match",
                                  "Top 1", Integer.toString(shapeMatchTop1), "Percent Top 1",
                                  Double.toString((double) shapeMatchTop1 / totalPills),
                                  "Top 2", Integer.toString(shapeMatchTop2), "Percent Top 2",
                                  Double.toString((double) shapeMatchTop2 / totalPills)});
        csvLines.add(new String[]{"Total imprint match", "Pills with imprint",
                                  Integer.toString(totalAvailableImprints),
                                  "Exact match", Integer.toString(imprintExactMatch), "Percent exact",
                                  Double.toString((double) imprintExactMatch / totalPills),
                                  "80% accuracy match", Integer.toString(imprintMatch80),
                                  Double.toString((double) imprintMatch80 / totalPills),});
        csvLines.add(new String[]{"Pill match", "Images with any pill predictions",
                                  Integer.toString(imagesWithPillPredictions),
                                  "Top 1", Integer.toString(pillMatchTop1), "Percent Top 1",
                                  Double.toString((double) pillMatchTop1 / totalPills),
                                  "Top " + PillMatcherService.PILL_MATCH_LIMIT,
                                  Integer.toString(pillMatchAny),
                                  "Percent Top " + PillMatcherService.PILL_MATCH_LIMIT,
                                  Double.toString((double) pillMatchAny / totalPills)});


        try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(outputFile)).build()) {
            writer.writeAll(csvLines);
        }
    }
}
