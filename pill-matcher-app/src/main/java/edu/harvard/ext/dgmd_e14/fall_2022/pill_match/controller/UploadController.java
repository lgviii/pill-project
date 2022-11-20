package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.controller;


import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadController {

//	public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/photos";

	public static String UPLOAD_DIRECTORY = "C:/Users/lgvii/Desktop/pills";



	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
		var fileNames = new StringBuilder();
		var fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
		fileNames.append(file.getOriginalFilename());
		Files.write(fileNameAndPath, file.getBytes());

		var ocrResponse = RequestOcrRun(fileNameAndPath.toString());

		return new ResponseEntity("Prediction: " + ocrResponse, HttpStatus.OK);
	}

	class Request {
		public Request(String input_file_location_in) {
			input_file_location = input_file_location_in;
		}
		String input_file_location;
	}

	private String RequestOcrRun(String fileLocation) {
		var formattedFileLocation = fileLocation.replace('\\', '/');

		RestTemplate restTemplate = new RestTemplate();

		var url = "http://localhost:7001/get_pill_imprint_predictions/";
		var requestJson = "{\"input_file_location\":\"" + formattedFileLocation + "\"}";
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		var entity = new HttpEntity(requestJson, headers);
		var result = restTemplate.postForObject(url, entity, String.class);

		System.out.println(result);

		return result;

	}



}