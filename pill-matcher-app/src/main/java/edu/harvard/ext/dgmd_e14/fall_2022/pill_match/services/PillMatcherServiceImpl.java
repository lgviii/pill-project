package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.ImageModelOutput;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories.PillRepository;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PillMatcherServiceImpl implements PillMatcherService {

    private static final int MODEL_OUTPUT_LIMIT = 2;

    private final LevenshteinDistance levenshteinDistance;
    private final PillRepository pillRepository;

    @Inject
    public PillMatcherServiceImpl(PillRepository pillRepository) {
        this.levenshteinDistance = new LevenshteinDistance();
        this.pillRepository = pillRepository;
    }

    @Override
    public Collection<ImageModelOutput> formatServiceResponse(String ocrResponse, String colorResponse, String shapeResponse) {
        var imageModelOutput = new ImageModelOutput();


        var ocrStringList = new ArrayList<String>();

        var ocrStringListSemiColonSplit = ocrResponse.split(";");
        for (var substring : ocrStringListSemiColonSplit) {
            ocrStringList.addAll(Arrays.asList(substring.split("\n")));
        }


        imageModelOutput.setImprintPredictions(ocrStringList);
        imageModelOutput.setColorModelMatches(getResponseMap(colorResponse));
        imageModelOutput.setShapeModelMatches(getResponseMap(shapeResponse));

        var imageModelOutputList = new ArrayList<ImageModelOutput>();
        imageModelOutputList.add(imageModelOutput);

        return imageModelOutputList;
    }

    private static HashMap<String, Double> getResponseMap(String response) {
        response = response.substring(1, response.length() - 1);
        var trimmedList = response.split("\\)\\(");

        var responseMap = new HashMap<String, Double>();

        for (String attribute: trimmedList) {
            var attributeSubstrings = attribute.replace("'","").replace(" ", "").split(",");
            responseMap.put(attributeSubstrings[0], Double.parseDouble(attributeSubstrings[1]));
        }
        return responseMap;
    }

    @Override
    public Map<Pill, Double> findMatchingPills(Collection<ImageModelOutput> modelOutputs) {
        // Pull the color and shape model outputs - just use the first set we find in the collection
        Map<String, Double> colorMatchMap = null;
        Map<String, Double> shapeMatchMap = null;
        for (ImageModelOutput modelOutput : modelOutputs) {
            // We'll assume that if color model output is present, shape is also
            if (modelOutput.getColorModelMatches() != null && !modelOutput.getColorModelMatches().isEmpty()) {
                colorMatchMap = modelOutput.getColorModelMatches();
                shapeMatchMap = modelOutput.getShapeModelMatches();
                break; // Question: Does this only loop once?
            }
        }

        assert colorMatchMap != null;
        assert shapeMatchMap != null;

        // For now we're just going to do single color matches
        // Use the model output limit to define how many matches we'll actually use from each color and shape model
        // outputs
        List<String> colors = limitModelOutput(colorMatchMap);
        List<String> shapes = limitModelOutput(shapeMatchMap);

        // Build the Map of all pills that are matched by color and shape, along with the accuracy of the match based
        // on the color/shape accuracy
        Map<Pill, Double> colorShapePillMatchMap = new HashMap<>();
        for (String color : colors) {
            for (String shape : shapes) {
                double accuracy = colorMatchMap.get(color) * shapeMatchMap.get(shape);
                List<Pill> pills = pillRepository.findAllByShapeAndSingleColor(shape, color);
                pills.forEach(pill -> colorShapePillMatchMap.put(pill, accuracy));
            }
        }

        // If there aren't any pills that match the provided color/shape combinations, stop here
        if (colorShapePillMatchMap.isEmpty()) {
            return colorShapePillMatchMap;
        }

        List<String> predictionGroups = combineImprintOutputs(modelOutputs);

        // Now whittle down the list by predicted imprint, if any
        if (predictionGroups.isEmpty() || predictionGroups.stream().allMatch(String::isBlank)) {
            // If no predictions were found for imprint text, just adjust the accuracy for all the pills in the early match
            // by reducing using the MISSING_IMPRINT_FACTOR.
            for (Map.Entry<Pill, Double> pillEntry : colorShapePillMatchMap.entrySet()) {
                if (pillEntry.getKey().hasImprint()) {
                    pillEntry.setValue(pillEntry.getValue() * MISSING_IMPRINT_FACTOR);
                }
            }
            return colorShapePillMatchMap;
        }
        else {
            // If predictions WERE found, first remove all pills that don't have an imprint
            var filteredColorShapePillMatchMap =  colorShapePillMatchMap.entrySet().stream()
                    .filter(entry -> entry.getKey().hasImprint())
                    .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

//            for (var entry : colorShapePillMatchMap.entrySet()) {
//                var pill = entry.getKey();
//                if (!pill.hasImprint()) {
//                    colorShapePillMatchMap.remove(pill);
//                }
//            }

            Map<Pill, Double> imprintMatches = matchPillsByPredictedImprints(predictionGroups,
                                                                             filteredColorShapePillMatchMap.keySet());
            // Build a new final match list using the text accuracy from the imprint matches and the color/shape
            // accuracy from the color/shape matches
            var finalMatchMap = new HashMap<Pill, Double>();
            for (var imprintEntry : imprintMatches.entrySet()) {
                Pill pill = imprintEntry.getKey();
                finalMatchMap.put(pill, imprintEntry.getValue() * filteredColorShapePillMatchMap.get(pill));
            }
            return finalMatchMap;
        }
    }

    List<String> limitModelOutput(Map<String, Double> modelOutputMap) {
        if (modelOutputMap.size() <= MODEL_OUTPUT_LIMIT) {
            return new ArrayList<>(modelOutputMap.keySet());
        }

        var sortedOutputMap = new ArrayList<>(modelOutputMap.entrySet());
        sortedOutputMap.sort(Map.Entry.comparingByValue());

        var endOfArray = sortedOutputMap.size() - 1;
        return sortedOutputMap.subList(endOfArray- MODEL_OUTPUT_LIMIT, endOfArray)
                              .stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    List<String> combineImprintOutputs(Collection<ImageModelOutput> imageModelOutputs) {
        // We'll assume that the prediction output is in the same order, so we'll just concatenate the strings together
        List<String> combinedImprints = new ArrayList<>();
        for (ImageModelOutput imageModelOutput : imageModelOutputs) {
            List<String> imprints = imageModelOutput.getImprintPredictions();
            if (combinedImprints.isEmpty()) {
                combinedImprints.addAll(imprints);
            }
            else {
                for (int i = 0; i < imprints.size(); i++) {
                    String imprint = imprints.get(i);
                    String current = combinedImprints.get(i);
                    // Only include this imprint if it's not blank
                    if (!imprint.isBlank()) {
                        String combined = imprint;
                        // Only concatenate if the current imprint also isn't blank - order doesn't matter since each
                        // section is compared individually
                        if (!current.isBlank()) {
                            combined += ";" + current;
                        }
                        combinedImprints.set(i, combined);
                    }
                }
            }
        }
        return combinedImprints;
    }

    @Override
    public Map<Pill, Double> matchPillsByPredictedImprints(Collection<String> predictionGroups,
                                                           Collection<Pill> pills) {
        Map<Pill, Double> pillMatchMap = new HashMap<>();

        for (Pill pill : pills) {
            if (pill.hasImprint()) {
                double accuracy = checkAllPredictionsAgainstImprint(predictionGroups, pill);
                if (accuracy > 0) {
                    pillMatchMap.put(pill, accuracy);
                }
            }
        }
        return pillMatchMap;
    }

    double checkAllPredictionsAgainstImprint(Collection<String> predictionGroups, Pill pill) {
        List<String> imprintSections = pill.getImprintSections();

        double highestAccuracy = 0.0;
        for (String predictionGroup : predictionGroups) {
            double accuracy = checkPredictionGroup(predictionGroup, imprintSections);
            if (accuracy > highestAccuracy) {
                highestAccuracy = accuracy;
            }
        }
        return highestAccuracy;
    }

    double checkPredictionGroup(String predictionGroup, List<String> imprintSections) {
        String[] predictions = predictionGroup.toLowerCase().split(";");

        // Make a copy of the imprint sections since we'll be removing elements as they're matched
        List<String> sections = new ArrayList<>(imprintSections);

        double totalAccuracy = 0.0;

        // First go through and try to match sections exactly
        // Track the predictions that don't have an exact match to check in the next round
        List<String> unmatchedPredictions = new ArrayList<>();
        for (String prediction : predictions) {
            boolean noMatch = true;
            for (int i = 0; i < sections.size(); i++) {
                String section = sections.get(i);
                if (section.equals(prediction)) {
                    noMatch = false;
                    totalAccuracy += 1.0;
                    sections.remove(i);
                    break;
                }
            }
            if (noMatch) {
                unmatchedPredictions.add(prediction);
            }
        }

        // Now try to match the remaining unmatched predictions using the Levenshtein distance, with a cutoff
        for (String prediction : unmatchedPredictions) {
            if (prediction.length() > 1) {
                ImprintMatch match = checkMatch(prediction, sections);
                if (match.accuracy > 0.5) {
                    sections.remove(match.index);
                    totalAccuracy += match.accuracy;
                }
            }
        }
        return totalAccuracy / imprintSections.size();
    }

    ImprintMatch checkMatch(String prediction, List<String> imprintSections) {
        int matchingIndex = -1;
        double bestAccuracy = 0.0;
        for (int i = 0; i < imprintSections.size(); i++) {
            String section = imprintSections.get(i).toLowerCase();

            if (section.contains(prediction)){
                var temp = "";
            }

            int max = Math.max(prediction.length(), section.length());
            // Only calculate the distance if either prediction or section has > 1 character, since both having only
            // 1 character can result in a misleadingly low distance score
            // If both have 1 character, skip this imprint section
            if (max > 1) {
                int distance = levenshteinDistance.apply(prediction, section);
                double accuracy = 1 - (double) distance / max;
                if (accuracy > bestAccuracy) {
                    bestAccuracy = accuracy;
                    matchingIndex = i;
                }
            }
        }
        return new ImprintMatch(matchingIndex, bestAccuracy);
    }

    static class ImprintMatch {

        final int index;
        final double accuracy;

        ImprintMatch(int index, double accuracy) {
            this.index = index;
            this.accuracy = accuracy;
        }

        @Override
        public String toString() {
            return "Index: " + index + ", accuracy: " + accuracy;
        }
    }
}
