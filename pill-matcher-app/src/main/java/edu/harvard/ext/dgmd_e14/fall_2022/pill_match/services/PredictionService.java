package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.ImageModelOutput;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;
import java.util.Collection;

@Validated
public interface PredictionService {

    /**
     * <p>
     * Get pill predictions and generated a formatted output list for the client to display
     * </p><p>
     * @param fileNameAndPath File path that services will use to locate camera image
     * @return Html content for the client to display
     */
    String getPredictionResponseHtml(Path fileNameAndPath);

    /**
     * <p>
     * Get pill predictions in an internal model format
     * </p><p>
     * @param fileNameAndPath File path that services will use to locate camera image
     * @return Model containing the prediction contents
     */
    Collection<ImageModelOutput> getFormattedResponse(Path fileNameAndPath);
}
