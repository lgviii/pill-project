package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.controller;


import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class UploadController {

	@Autowired
	private PredictionService predictionService;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {

		String path = "pill-matcher-app/src/main/resources";
		var resourcesPath = new File(path);
		String resourcesAbsolutePath = resourcesPath.getAbsolutePath();

		var fileNameAndPath = Paths.get(resourcesAbsolutePath, file.getOriginalFilename());

		// Write the incoming file to local storage
		Files.write(fileNameAndPath, file.getBytes());

		// Get the result for the client to display
		var clientResponseContent = predictionService.getPredictionResponseHtml(fileNameAndPath);

		return new ResponseEntity(clientResponseContent, HttpStatus.OK);
	}
}