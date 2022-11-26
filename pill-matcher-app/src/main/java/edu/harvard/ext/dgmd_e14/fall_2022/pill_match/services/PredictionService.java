package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

@Validated
public interface PredictionService {

    String getPredictionResponseHtml(Path fileNameAndPath);

}
