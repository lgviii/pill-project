package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.ImageModelOutput;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Validated
public interface PillMatcherService {

    /**
     * Factor used to reduce the accuracy when matching pills that should have an imprint, if no predicted imprint
     * was found in the photo.
     */
    double MISSING_IMPRINT_FACTOR = 0.75;

    /**
     * <p>
     * Attempts to find the top 10 pills matching the specified colors, shapes, and predicted imprint text groups.
     * </p><p>
     * This is the method that should be called by master service/REST controller once all the models have been run
     * on a pill image.  The returned Map will include ALL possible pill matches based on the specified colors,
     * shapes, and imprint text.
     * </p><p>
     * The provided Collection must contain at least one object with outputs from all models (shape, color, imprint).
     * It may contain additional objects with just the imprint model output, in case the pill has an imprint on both
     * sides.  If multiple elements in the Collection have shape/color output, only the first one in the Collection
     * will be used - the other shape/color outputs will be ignored.
     * </p></p>
     * Note that if a pill has no imprint but there is some predicted imprint text, that pill will NOT be included in
     * the results, as the presence of any predicted text is taken to mean that the pill has some imprint.<br>
     * However, pills that DO have an imprint will still be included in the results even if there's no predicted
     * imprint text, because the OCR prediction often fails to find any text even in pills with imprints.  These
     * pills will have their overall accuracy score multiplied by MISSING_IMPRINT_FACTOR to place them below matches
     * against pills that have no imprint.
     * </p><p>
     * Accuracy values will be in the range [0.0, 1.0].
     * </p>
     *
     * @param modelOutputs Collection containing model outputs for each image supplied for the pill to match, should
     *                     include at least one image with outputs from all models (shape, color, imprint), may
     *                     include an additional image with outputs from just the imprint model in case there's an
     *                     imprint on both sides of the pill
     * @return Map containing top 10 Pills that have at least some match across the assorted predictions, linked with
     *         the combined probability that the match is correct, ordered by probability from most to least
     */
    @NotNull
    LinkedHashMap<Pill, Double> findMatchingPills(@NotNull @NotEmpty Collection<ImageModelOutput> modelOutputs);


    /**
     * <p>
     * Attempts to match pills from the specified list using the specified imprints.
     * </p><p>
     * The assumption is that you've
     * already narrowed down the list of pills using shape and color, and now want to find the final match using the
     * specified collections of imprints with regular expression searching.  The predictions are assumed to be
     * gathered from multiple possible source variations, (such as different image orientations), with each set of
     * predictions from a single source concatenated together using a semicolon as delineation between each chunk of
     * text.
     * </p><p>
     * This method should ONLY be called if there was at least one character predicted - it assumes that at least one
     * of the provided prediction group strings will be non-blank.
     * </p>
     * The returned Map will include only pills that have at least some match to the specified predicted text.  Pills
     * with no imprint or with no matching imprints will not be included.
     * </p>
     *
     * @param predictionGroups Collection containing predicted imprints, with each String including all the predictions
     *                         from a single source, separated by semicolons
     * @param pills            Collection of pills against which the imprints should be matched
     * @return Map linking each Pill that has at least some match with the accuracy of its imprint match - pills that
     *         have no imprint or no match to the predictions will NOT be included in the result
     */
    @NotNull
    Map<Pill, Double> matchPillsByPredictedImprints(@NotNull @NotEmpty Collection<String> predictionGroups,
                                                    @NotNull @NotEmpty Collection<Pill> pills);

    /**
     * Checks if at least one of the specified groups of predictions exactly matches the specified pill imprint.
     *
     * @param predictionGroups Collection containing predicted imprints, with each String including all the predictions
     *                         from a single source, separated by semicolons, may be empty if no predictions
     * @param imprint          actual imprint text for a pill, containing the imprint sections separated by semicolons,
     *                         may be null/blank if no imprint
     * @return true if at least one of the specified groups of predictions exactly matches the specified pill imprint,
     *         including if both are empty/blank, false if they don't match exactly
     */
    boolean doesPredictionMatchImprint(Collection<String> predictionGroups, String imprint);

    /**
     * Calculates the highest accuracy of the match between any of the prediction groups and the actual specified
     * pill imprint.
     *
     * @param predictionGroups Collection containing predicted imprints, with each String including all the predictions
     *                         from a single source, separated by semicolons, may be empty if no predictions
     * @param imprint          actual imprint text for a pill, containing the imprint sections separated by semicolons,
     *                         may be null/blank if no imprint
     * @return highest accuracy of a match between any of the prediction groups and the actual specified pill imprint
     */
    double getHighestMatchAccuracy(Collection<String> predictionGroups, String imprint);
}
