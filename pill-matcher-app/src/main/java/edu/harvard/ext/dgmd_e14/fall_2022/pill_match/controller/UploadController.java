package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.controller;


import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.ImageModelOutput;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services.PillMatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

@Controller
public class UploadController {

//	public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/photos";

	@Autowired
	private PillMatcherService pillMatcherService;
	public static String UPLOAD_DIRECTORY = "C:/Users/lgvii/Desktop/pills";

	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
		var fileNames = new StringBuilder();
		var fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
		fileNames.append(file.getOriginalFilename());
		Files.write(fileNameAndPath, file.getBytes());

		var ocrResponse = RequestOcrRun(fileNameAndPath.toString());
		var colorResponse = RequestColorRun(fileNameAndPath.toString());
		var shapeResponse = RequestShapeRun(fileNameAndPath.toString());

		var formattedResponse = pillMatcherService.formatServiceResponse(ocrResponse, colorResponse, shapeResponse);
		var pillPredictions = pillMatcherService.findMatchingPills(formattedResponse);

		StringBuilder str = new StringBuilder();

		str.append("<b>Prediction:</b>");
		str.append("</br>");
		str.append("<b>Text:</b>");
		str.append("</br>");
		str.append("<i>");
		str.append(ocrResponse);
		str.append("</i>");
		str.append("</br>");
		str.append("<b>Color:</b>");
		str.append("</br>");
		str.append("<i>");
		str.append(colorResponse);
		str.append("</i>");
		str.append("</br>");
		str.append("<b>Shape:</b>");
		str.append("</br>");
		str.append("<i>");
		str.append(shapeResponse);
		str.append("</i>");
		str.append("</br>");
		str.append("</br>");
		str.append("<b>Final Pill Prediction (we believe it's one of the following):</b>");
		str.append("</br>");
		str.append("</br>");

		for (var entry: pillPredictions.entrySet()) {
			str.append("<i>Name: </i>");
			str.append("<b>");
			str.append(entry.getKey().getProprietaryName());
			str.append("</b>");
			str.append("</br>");

			str.append("<i>Labeling Originator: </i>");
			str.append("<b>");
			str.append(entry.getKey().getLabeledBy());
			str.append("</b>");
			str.append("</br>");

			str.append("<i>Prediction Value: </i>");
			str.append("<b>");
			str.append(entry.getValue());
			str.append("</b>");
			str.append("</br>");

			str.append("<i>Imprint: </i>");
			str.append("<b>");
			str.append(entry.getKey().getImprint());
			str.append("%</b>");
			str.append("</br>");

			str.append("<i>Shape: </i>");
			str.append("<b>");
			str.append(entry.getKey().getShape());
			str.append("%</b>");
			str.append("</br>");

			str.append("<i>Color: </i>");
			str.append("<b>");
			str.append(entry.getKey().getColors());
			str.append("%</b>");
			str.append("</br>");

			str.append("</br>");
		}

		return new ResponseEntity(str.toString(), HttpStatus.OK);
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

	class Request {
		public Request(String input_file_location_in) {
			input_file_location = input_file_location_in;
		}
		String input_file_location;
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