package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.ImageModelOutput;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;
import java.util.*;

@Service
public class PredictionServiceImpl implements PredictionService {

    private PillMatcherService pillMatcherService;

    public PredictionServiceImpl(PillMatcherService pillMatcherService) {
        this.pillMatcherService = pillMatcherService;
    }

    // Convert the service responses to an internal model
    public Collection<ImageModelOutput> formatServiceResponse(String ocrResponse, String colorResponse, String shapeResponse) {
        var imageModelOutput = new ImageModelOutput();

        // The OCR response contains the predictions from all the image permutations, with each one separated by a line
        // break, so split it back into the individual predictions based on the line breaks
        var ocrStringList = Arrays.asList(ocrResponse.split("\n"));

        imageModelOutput.setImprintPredictions(ocrStringList);
        imageModelOutput.setColorModelMatches(getResponseMap(colorResponse));
        imageModelOutput.setShapeModelMatches(getResponseMap(shapeResponse));

        var imageModelOutputList = new ArrayList<ImageModelOutput>();
        imageModelOutputList.add(imageModelOutput);

        return imageModelOutputList;
    }

    private static HashMap<String, Double> getResponseMap(String response) {
        // map Python list string to Java
        response = response.substring(1, response.length() - 1);
        var trimmedList = response.split("\\)\\(");

        var responseMap = new HashMap<String, Double>();

        for (String attribute: trimmedList) {
            var attributeSubstrings = attribute.replace("'","").replace(" ", "").split(",");
            responseMap.put(attributeSubstrings[0], Double.parseDouble(attributeSubstrings[1]));
        }
        return responseMap;
    }

    public String getPredictionResponseHtml(Path fileNameAndPath) {
        var ocrResponse = RequestOcrRun(fileNameAndPath.toString());
        var colorResponse = RequestColorRun(fileNameAndPath.toString());
        var shapeResponse = RequestShapeRun(fileNameAndPath.toString());

        var formattedResponse = formatServiceResponse(ocrResponse, colorResponse, shapeResponse);
        var pillPredictions = pillMatcherService.findMatchingPills(formattedResponse);

        StringBuilder stringBuilder = getDisplayHtml(ocrResponse, colorResponse, shapeResponse, pillPredictions);

        return stringBuilder.toString();
    }

    private static StringBuilder getDisplayHtml(String ocrResponse, String colorResponse, String shapeResponse, LinkedHashMap<Pill, Double> pillPredictions) {
        StringBuilder stringBuilder = new StringBuilder();

        // show the inferences that were created
        stringBuilder.append("<b>Prediction:</b>");
        stringBuilder.append("</br>");
        stringBuilder.append("<b>Text:</b>");
        stringBuilder.append("</br>");
        stringBuilder.append("<i>");
        stringBuilder.append(ocrResponse);
        stringBuilder.append("</i>");
        stringBuilder.append("</br>");
        stringBuilder.append("<b>Color:</b>");
        stringBuilder.append("</br>");
        stringBuilder.append("<i>");
        stringBuilder.append(colorResponse);
        stringBuilder.append("</i>");
        stringBuilder.append("</br>");
        stringBuilder.append("<b>Shape:</b>");
        stringBuilder.append("</br>");
        stringBuilder.append("<i>");
        stringBuilder.append(shapeResponse);
        stringBuilder.append("</i>");
        stringBuilder.append("</br>");
        stringBuilder.append("</br>");
        stringBuilder.append("<b>Final Pill Prediction (we believe it's one of the following):</b>");
        stringBuilder.append("</br>");
        stringBuilder.append("</br>");

        // show the list of predictions
        for (var entry: pillPredictions.entrySet()) {
            stringBuilder.append("<i>Name: </i>");
            stringBuilder.append("<b>");
            stringBuilder.append(entry.getKey().getProprietaryName());
            stringBuilder.append("</b>");
            stringBuilder.append("</br>");

            stringBuilder.append("<i>Labeling Originator: </i>");
            stringBuilder.append("<b>");
            stringBuilder.append(entry.getKey().getLabeledBy());
            stringBuilder.append("</b>");
            stringBuilder.append("</br>");

            stringBuilder.append("<i>Prediction Value: </i>");
            stringBuilder.append("<b>");
            stringBuilder.append(entry.getValue());
            stringBuilder.append("</b>");
            stringBuilder.append("</br>");

            stringBuilder.append("<i>Imprint: </i>");
            stringBuilder.append("<b>");
            stringBuilder.append(entry.getKey().getImprint());
            stringBuilder.append("%</b>");
            stringBuilder.append("</br>");

            stringBuilder.append("<i>Shape: </i>");
            stringBuilder.append("<b>");
            stringBuilder.append(entry.getKey().getShape());
            stringBuilder.append("%</b>");
            stringBuilder.append("</br>");

            stringBuilder.append("<i>Color: </i>");
            stringBuilder.append("<b>");
            stringBuilder.append(entry.getKey().getColors());
            stringBuilder.append("%</b>");
            stringBuilder.append("</br>");

            stringBuilder.append("</br>");
        }
        return stringBuilder;
    }

    public Collection<ImageModelOutput> getFormattedResponse(Path fileNameAndPath) {
        var ocrResponse = RequestOcrRun(fileNameAndPath.toString());
        var colorResponse = RequestColorRun(fileNameAndPath.toString());
        var shapeResponse = RequestShapeRun(fileNameAndPath.toString());

        var formattedResponse = formatServiceResponse(ocrResponse, colorResponse, shapeResponse);
        return formattedResponse;
    }

    private String RequestOcrRun(String fileLocation) {

        var endpoint = "get_pill_imprint_predictions/";

        return getResponseInferenceString(fileLocation, endpoint);
    }

    private String RequestShapeRun(String fileLocation) {

        var endpoint = "get_pill_shape_predictions/";

        return getResponseInferenceString(fileLocation, endpoint);
    }

    private String RequestColorRun(String fileLocation) {

        var endpoint = "get_pill_color_predictions/";

        return getResponseInferenceString(fileLocation, endpoint);
    }

    private static String getResponseInferenceString(String fileLocation, String endpoint) {
        var formattedFileLocation = fileLocation.replace('\\', '/');

        RestTemplate restTemplate = new RestTemplate();

        var url = "http://localhost:7001/" + endpoint;
        var requestJson = "{\"input_file_location\":\"" + formattedFileLocation + "\"}";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var entity = new HttpEntity(requestJson, headers);
        var result = restTemplate.postForObject(url, entity, String.class);

        System.out.println(result);

        return result;
    }
}
