package edu.harvard.ext.dgmd_e14.fall_2022.pill_match;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Captures all the model output generated from a single image.  Should minimally contain imprint predictions, may
 * not have any color or shape model output if those models weren't run on this image.  However, if shape output is
 * provided, color output should also be provided - the assumption is that either both will be run, or neither will
 * be run.
 */
public class ImageModelOutput {

    /**
     * Map linking the SPL color value to the probability assigned by the model for that color.
     * May be null if the color model wasn't run on this image, but should be present if the shape model output is
     * present.
     */
    private Map<String, Double> colorModelMatches;

    /**
     * Map linking the SPL shape value to the probability assigned by the model for that shape.
     * May be null if the shape model wasn't run on this image, but should be present if the color model output is
     * present.
     */
    private Map<String, Double> shapeModelMatches;

    /**
     * List containing predicted imprints, with each String including all the predictions from a single
     * permutation, separated by semicolons.  That is, the OCR inference service can generate multiple possible
     * "words" for each image permutation - these are joined together into a single String, using a semicolon
     * delimiter, and the List contains all such Strings, one per image permutation run through the OCR library.
     * However, note that only image permutations that have at least one detected "word" will be included - if an
     * image permutation has no detected "words", that String will not be included.  If none of the image
     * permutations have any predicted words, the List will either be empty or contain a single empty String.
     */
    @NotNull
    @NotEmpty
    private List<String> imprintPredictions;

    public Map<String, Double> getColorModelMatches() {
        return colorModelMatches;
    }

    public void setColorModelMatches(Map<String, Double> colorModelMatches) {
        this.colorModelMatches = colorModelMatches;
    }

    public Map<String, Double> getShapeModelMatches() {
        return shapeModelMatches;
    }

    public void setShapeModelMatches(Map<String, Double> shapeModelMatches) {
        this.shapeModelMatches = shapeModelMatches;
    }

    public List<String> getImprintPredictions() {
        return imprintPredictions;
    }

    public void setImprintPredictions(List<String> imprintPredictions) {
        this.imprintPredictions = imprintPredictions;
    }
}
