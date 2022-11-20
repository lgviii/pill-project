package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories.PillRepository;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Map<Pill, Double> findMatchingPills(Map<String, Double> colorMatchMap, Map<String, Double> shapeMatchMap,
                                               Collection<String> predictionGroups) {
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

        if (colorShapePillMatchMap.isEmpty()) {
            return colorShapePillMatchMap;
        }

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
            for (Pill pill : colorShapePillMatchMap.keySet()) {
                if (!pill.hasImprint()) {
                    colorShapePillMatchMap.remove(pill);
                }
            }
            Map<Pill, Double> imprintMatches = matchPillsByPredictedImprints(predictionGroups,
                                                                             colorShapePillMatchMap.keySet());
            // Build a new final match list using the text accuracy from the imprint matches and the color/shape
            // accuracy from the color/shape matches
            Map<Pill, Double> finalMatchMap = new HashMap<>();
            for (Map.Entry<Pill, Double> imprintEntry : imprintMatches.entrySet()) {
                Pill pill = imprintEntry.getKey();
                finalMatchMap.put(pill, imprintEntry.getValue() * colorShapePillMatchMap.get(pill));
            }
            return finalMatchMap;
        }
    }

    List<String> limitModelOutput(Map<String, Double> modelOutputMap) {
        if (modelOutputMap.size() <= MODEL_OUTPUT_LIMIT) {
            return new ArrayList<>(modelOutputMap.keySet());
        }

        List<Map.Entry<String, Double>> sortedOutputMap = new ArrayList<>(modelOutputMap.entrySet());
        sortedOutputMap.sort(Map.Entry.comparingByValue());

        return sortedOutputMap.subList(0, MODEL_OUTPUT_LIMIT)
                              .stream().map(Map.Entry::getKey).collect(Collectors.toList());
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
            String section = imprintSections.get(i);
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
