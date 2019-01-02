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

import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.record.AbstractRecordMatcher;

public class MFBRecordMatcher extends AbstractRecordMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MFBRecordMatcher.class);

    private static final double MAX_SCORE = 1;

    private final double minConfidenceValue;

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
        double confidence = 0;
        int matchIndex = 0;
        MatchResult result = new MatchResult(record1.getAttributes().size());
        int maxWeight = 0;
        while (mergedRecordAttributes.hasNext()) {
            Attribute left = mergedRecordAttributes.next();
            Attribute right = currentRecordAttributes.next();
            IAttributeMatcher matcher = attributeMatchers[matchIndex];
            // Find the first score to exceed threshold (if any).
            double score = matchScore(left, right, matcher);
            attributeMatchingWeights[matchIndex] = score;
            result.setScore(matchIndex, matcher.getMatchType(), score, record1.getId(), left.getValue(), record2.getId(),
                    right.getValue());
            result.setThreshold(matchIndex, matcher.getThreshold());
            confidence += score * matcher.getWeight();
            maxWeight += matcher.getWeight();
            matchIndex++;
        }
        double normalizedConfidence = confidence > 0 && maxWeight != 0 ? confidence / maxWeight : confidence; // Normalize
                                                                                                              // to 0..1
                                                                                                              // value
        result.setConfidence(normalizedConfidence);
        if (normalizedConfidence < minConfidenceValue) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cannot match record: merged record has a too low confidence value (" + normalizedConfidence + " < "
                        + minConfidenceValue + ")");
            }
            return MFB.NonMatchResult.wrap(result);
        }
        synRecord2ConFidence(record2, normalizedConfidence);
        return result;
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

    private static double matchScore(Attribute leftAttribute, Attribute rightAttribute, IAttributeMatcher matcher) {
        // Find the best score in values
        // 1- Try first values
        String left = leftAttribute.getValue();
        String right = rightAttribute.getValue();
        double score = matcher.getMatchingWeight(left, right);
        // 2- Compare using values that build attribute value (if any)
        Iterator<String> leftValues = new IteratorChain(Collections.singleton(left).iterator(),
                leftAttribute.getValues().iterator());
        double maxScore = 0;
        while (leftValues.hasNext()) {
            String leftValue = leftValues.next();
            Iterator<String> rightValues = new IteratorChain(Collections.singleton(right).iterator(),
                    rightAttribute.getValues().iterator());
            while (rightValues.hasNext()) {
                String rightValue = rightValues.next();
                score = matcher.getMatchingWeight(leftValue, rightValue);
                if (score > maxScore) {
                    maxScore = score;
                }
                if (Double.compare(maxScore, MAX_SCORE) == 0) {
                    // Can't go higher, no need to perform other checks.
                    return maxScore;
                }
            }
        }
        return maxScore;
    }

}
