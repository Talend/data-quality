// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.matchmerge.mfb;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

public class MatchResult {

    private final List<Score> scores;

    private final List<Float> thresholds;

    private double normalizedConfidence;

    private double finalWorstConfidenceValue;

    private final List<Double> worstConfidenceValueScoreList;

    public MatchResult(int size) {
        scores = new ArrayList<Score>(size + 1);
        thresholds = new ArrayList<Float>(size + 1);
        worstConfidenceValueScoreList = new ArrayList<Double>(size + 1);
    }

    public static class Score {

        public final String[] ids = new String[2];

        public final String[] values = new String[2];

        public AttributeMatcherType algorithm;

        public double score;
    }

    public void setConfidence(double normalizedConfidence) {
        this.normalizedConfidence = normalizedConfidence;
    }

    public List<Score> getScores() {
        return scores;
    }

    public List<Float> getThresholds() {
        return thresholds;
    }

    public double getNormalizedConfidence() {
        return normalizedConfidence;
    }

    public void setScore(int index, AttributeMatcherType algorithm, double score, String recordId1, String value1,
            String recordId2, String value2) {
        while (index >= scores.size()) {
            scores.add(new Score());
        }
        Score currentScore = scores.get(index);
        currentScore.algorithm = algorithm;
        currentScore.score = score;
        currentScore.ids[0] = recordId1;
        currentScore.ids[1] = recordId2;
        currentScore.values[0] = value1;
        currentScore.values[1] = value2;
    }

    public void setThreshold(int index, float threshold) {
        while (index >= thresholds.size()) {
            thresholds.add(0f);
        }
        thresholds.set(index, threshold);
    }

    public boolean isMatch() {
        int i = 0;
        for (Score score : scores) {
            if (score.score < getThresholds().get(i++)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Getter for finalWorstConfidenceValue.
     * 
     * @return the finalWorstConfidenceValue
     */
    public double getFinalWorstConfidenceValue() {
        return this.finalWorstConfidenceValue;
    }

    /**
     * Sets the finalWorstConfidenceValue.
     * 
     * @param finalWorstConfidenceValue the finalWorstConfidenceValue to set
     */
    protected void setFinalWorstConfidenceValue(double finalWorstConfidenceValue) {
        this.finalWorstConfidenceValue = finalWorstConfidenceValue;
    }

    /**
     * Store worst score for every attribute
     * */
    protected void storeWorstScore(int index, double score) {
        this.worstConfidenceValueScoreList.add(index, score);
    }

    /**
     * Getter for worstConfidenceValueScoreList.
     * 
     * @return the worstConfidenceValueScoreList
     */
    public List<Double> getWorstConfidenceValueScoreList() {
        return this.worstConfidenceValueScoreList;
    }

}
