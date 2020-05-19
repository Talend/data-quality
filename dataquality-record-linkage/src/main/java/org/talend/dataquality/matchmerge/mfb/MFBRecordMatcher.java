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

import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.record.AbstractRecordMatcher;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MFBRecordMatcher extends AbstractRecordMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MFBRecordMatcher.class);

    private final double minConfidenceValue;

    private static double worstConfidenceValue;

    //Added TDQ-18347,20200515 , one key <-> one survivorship function
    private static String[] survivorshipFunctions;

    public MFBRecordMatcher(double minConfidenceValue) {
        this.minConfidenceValue = minConfidenceValue;
    }

    @Override
    public double getMatchingWeight(String[] record1, String[] record2) {
        return getMatchingWeight(buildRecord(record1), buildRecord(record2)).getNormalizedConfidence();
    }

    private static Record buildRecord(String[] values) {
        Record record = new Record(null, 0, StringUtils.EMPTY);
        int i = 0;
        for (String value : values) {
            Attribute attribute = new Attribute(String.valueOf(i++));
            attribute.setValue(value);
            record.getAttributes().add(attribute);
        }
        return record;
    }

    @Override
    public MatchResult getMatchingWeight(Record record1, Record record2) {
        Iterator<Attribute> mergedRecordAttributes = record1.getAttributes().iterator();
        Iterator<Attribute> currentRecordAttributes = record2.getAttributes().iterator();
        List<Double> leftWorstConfidenceValueScoreList = record1.getWorstConfidenceValueScoreList();
        List<Double> rightWorstConfidenceValueScoreList = record2.getWorstConfidenceValueScoreList();
        double confidence = 0;
        int matchIndex = 0;
        MatchResult result = new MatchResult(record1.getAttributes().size());
        int maxWeight = 0;
        double finalWorstConfidenceValue = 0.0d;
        while (mergedRecordAttributes.hasNext()) {
            Attribute left = mergedRecordAttributes.next();
            Attribute right = currentRecordAttributes.next();
            IAttributeMatcher matcher = attributeMatchers[matchIndex];
            Double leftWorstScore = getWorstScore(leftWorstConfidenceValueScoreList, matchIndex);
            Double rightWorstScore = getWorstScore(rightWorstConfidenceValueScoreList, matchIndex);
            // Find the first score to exceed threshold (if any).
            // use record1.getWorstConfidenceValueScoreList() to instead of some while in matchScore method
            double score = matchScore(left, right, matcher, leftWorstScore, rightWorstScore, matchIndex);
            attributeMatchingWeights[matchIndex] = score;
            result.setScore(matchIndex, matcher.getMatchType(), score, record1.getId(), left.getValue(),
                    record2.getId(), right.getValue());
            result.setThreshold(matchIndex, matcher.getThreshold());
            result.storeWorstScore(matchIndex, worstConfidenceValue);
            confidence += score * matcher.getWeight();
            finalWorstConfidenceValue += worstConfidenceValue * matcher.getWeight();
            maxWeight += matcher.getWeight();
            matchIndex++;
        }
        double normalizedConfidence = confidence > 0 && maxWeight != 0 ? confidence / maxWeight : confidence; // Normalize
                                                                                                              // to 0..1
        finalWorstConfidenceValue = finalWorstConfidenceValue > 0 && maxWeight != 0
                ? finalWorstConfidenceValue / maxWeight : finalWorstConfidenceValue; // Normalize
        // to 0..1
        result.setConfidence(normalizedConfidence);
        result.setFinalWorstConfidenceValue(finalWorstConfidenceValue);

        if (normalizedConfidence < minConfidenceValue) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cannot match record: merged record has a too low confidence value ("
                        + normalizedConfidence + " < " + minConfidenceValue + ")");
            }
            return MFB.NonMatchResult.wrap(result);
        }
        return result;
    }

    private Double getWorstScore(List<Double> leftWorstConfidenceValueScoreList, int index) {
        if (leftWorstConfidenceValueScoreList.size() > index) {
            return leftWorstConfidenceValueScoreList.get(index);
        }
        return 1.0d;
    }

    /**
     * DOC zshen Comment method "synRecord2ConFidence".
     * 
     * @param record2
     * @param normalizedConfidence
     */
    protected void synRecord2ConFidence(Record record2, double normalizedConfidence) {
        record2.setConfidence(normalizedConfidence);
    }

    @SuppressWarnings("unchecked")
    private static double matchScore(Attribute leftAttribute, Attribute rightAttribute, IAttributeMatcher matcher,
            Double leftWorstScore, Double rightWorstScore, int matchIndex) {
        // Find the best score in values
        // 1- Try first values
        // 2- Compare using values that build attribute value (if any)
        Iterator<String> leftValues = getAllComparedValues(leftAttribute, matchIndex);

        double maxScore = 0;
        double score = 0;
        if (leftWorstScore > rightWorstScore) {
            worstConfidenceValue = rightWorstScore;
        } else {
            worstConfidenceValue = leftWorstScore;
        }

        while (leftValues.hasNext()) {
            String leftValue = leftValues.next();
            Iterator<String> rightValues = getAllComparedValues(rightAttribute, matchIndex);
            while (rightValues.hasNext()) {
                String rightValue = rightValues.next();
                score = matcher.getMatchingWeight(leftValue, rightValue);
                if (worstConfidenceValue > score) {
                    worstConfidenceValue = score;
                }
                if (score > maxScore) {
                    maxScore = score;
                }
            }
        }
        return maxScore;
    }

    /**
     * Getter for worstConfidenceValue.
     * 
     * @return the worstConfidenceValue
     */
    protected double getWorstConfidenceValue() {
        return this.worstConfidenceValue;
    }

    /**
     * keep survivorFunction in the matcher, maybe still need it in future
     * @param survivorShipFunctions
     */
    public void setSurvivorShipFunction(String[] survivorShipFunctions) {
        survivorshipFunctions = survivorShipFunctions;
    }

    /**
     * TDQ-18347 when using CONCATENATE in match key, then no need to compare its concatednated value for generated masters.
     * but for the first time, need to use its original value.
     * @param comparedAttribute
     * @return
     */
    protected static IteratorChain getAllComparedValues(Attribute comparedAttribute, int matchIndex) {
        if (survivorshipFunctions != null && survivorshipFunctions.length > matchIndex
                && SurvivorShipAlgorithmEnum.CONCATENATE
                        .getValue()
                        .equalsIgnoreCase(survivorshipFunctions[matchIndex])) {
            if (comparedAttribute.getValues().size() > 0) {
                return new IteratorChain(comparedAttribute.getValues().iterator());
            }
        }
        return new IteratorChain(Collections.singleton(comparedAttribute.getValue()).iterator(),
                comparedAttribute.getValues().iterator());
    }

    /**
     * TDQ-18347 , for : SurvivorShipAlgorithmEnum.CONCATENATE, the concatenated value of the master 
     * will not attend the matching anymore, only use the original values to match from this issue
     * This method is called from MFB.build, but not on DQ side
     * @param surAlgorithms
     */
    public void setSurvivorShipFunction(SurvivorShipAlgorithmEnum[] surAlgorithms) {
        if (surAlgorithms != null && surAlgorithms.length > 0) {
            survivorshipFunctions = new String[surAlgorithms.length];
            int index = 0;
            for (SurvivorShipAlgorithmEnum sAlgorithm : surAlgorithms) {
                survivorshipFunctions[index++] = sAlgorithm.getValue();
            }
        }

    }
}
