package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PillMatcherServiceImpl implements PillMatcherService {

    private final LevenshteinDistance levenshteinDistance;

    public PillMatcherServiceImpl() {
        this.levenshteinDistance = new LevenshteinDistance();
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
