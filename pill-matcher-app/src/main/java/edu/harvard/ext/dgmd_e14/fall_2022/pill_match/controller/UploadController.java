package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadController {

	public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/photos";

	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
		StringBuilder fileNames = new StringBuilder();
		Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
		fileNames.append(file.getOriginalFilename());
		Files.write(fileNameAndPath, file.getBytes());

		return new ResponseEntity<String>("This is where you put your pill id info...", HttpStatus.OK);
	}
}