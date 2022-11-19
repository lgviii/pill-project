package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;

@Validated
public interface PillMatcherService {

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
     * @param predictionGroups Collection containing predicted imprints, with each String including all the predictions
     *                         from a single source, separated by semicolons
     * @param pills            Collection of pills against which the imprints should be matched
     * @return Map linking each Pill that has at least some match with the accuracy of its imprint match - pills that
     *         have no imprint or no match to the predictions will NOT be included in the result
     */
    @NotNull
    Map<Pill, Double> matchPillsByPredictedImprints(@NotNull @NotEmpty Collection<String> predictionGroups,
                                                    @NotNull @NotEmpty Collection<Pill> pills);

}
