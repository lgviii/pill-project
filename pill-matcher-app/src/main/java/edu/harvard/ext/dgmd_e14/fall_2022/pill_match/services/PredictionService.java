package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.ImageModelOutput;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

@Validated
public interface PredictionService {

    String getPredictionResponseHtml(Path fileNameAndPath);

    public Map<Pill, Double> getPredictions(Collection<ImageModelOutput> formattedResponse);
    Collection<ImageModelOutput> getFormattedResponse(Path fileNameAndPath);
}
