package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.controller;


import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class UploadController {

	@Autowired
	private PredictionService predictionService;
	public static String UPLOAD_DIRECTORY = "C:/Users/lgvii/Desktop/pills";

	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
		var fileNames = new StringBuilder();
		var fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
		fileNames.append(file.getOriginalFilename());
		Files.write(fileNameAndPath, file.getBytes());

		var str = predictionService.getPredictionResponseHtml(fileNameAndPath);

		return new ResponseEntity(str, HttpStatus.OK);
	}
}