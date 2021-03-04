// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.record;

import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.MatchResult;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;

/**
 * @author scorreia
 * 
 * Interface of record matcher.
 */
public interface IRecordMatcher {

    /**
     * Method "setRecordSize".
     * 
     * @param numberOfAttributes the number of attributes of a record.
     */
    void setRecordSize(int numberOfAttributes);

    /**
     * Method "getRecordSize".
     * 
     * @return the number of attributes of a record.
     */
    int getRecordSize();

    /**
     * Method "setAttributeWeights".
     * 
     * @param weights the weight of each attribute of the records
     * @return false when the weights cannot be applied to the given records.
     */
    boolean setAttributeWeights(double[] weights);

    /**
     * Method "setAttributeGroups".
     * 
     * @param groups the indices of the attributes that can be compared.
     * @return false when the given groups are not applicable to the given records.
     */
    boolean setAttributeGroups(int[][] groups);

    /**
     * Method "setAttributeMatchers".
     * 
     * @param attributeMatchers a matcher for each attribute
     * @return false if the number of matcher is not appropriate (0 or the size of the array does not correspond to the
     * expected number of attributes)
     */
    boolean setAttributeMatchers(IAttributeMatcher[] attributeMatchers);

    /**
     * 
     * @return attribute matchers of this current record matcher.
     */
    IAttributeMatcher[] getAttributeMatchers();

    /**
     * Method "setBlockingAttributeMatchers".
     * 
     * @param attrMatcherIndices the indices of the attribute matcher to be used as blocking variables
     * @return false if a problem.
     */
    boolean setBlockingAttributeMatchers(int[] attrMatcherIndices);

    /**
     * Method "getMatchingWeight" compares the given records and returns the matching weight (between 0 - no match - and
     * 1 - exact match).
     * 
     * @param record1 the first record (array of string attributes)
     * @param record2 the second record to be compared to the first record.
     * @return the matching weight of the given two records
     * @see #getMatchingWeight(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)
     */
    double getMatchingWeight(String[] record1, String[] record2);

    /**
     * <p>
     * Method "getMatchingWeight" compares the given records and returns the matching weight (between 0 - no match - and
     * 1 - exact match).
     * </p>
     * <p>
     * The difference with {@link #getMatchingWeight(String[], String[])} lies in the structure of
     * {@link org.talend.dataquality.matchmerge.Record} since each column may have multiple values.
     * </p>
     * 
     * @param record1 the first record (array of string attributes)
     * @param record2 the second record to be compared to the first record.
     * @return the matching weight of the given two records
     */
    MatchResult getMatchingWeight(Record record1, Record record2);

    /**
     * Method "getCurrentAttributeMatchingWeights".
     * 
     * @return the list of attribute matching weights for the last couple of records given in the
     * {@link #getMatchingWeight(String[], String[])} method
     */
    double[] getCurrentAttributeMatchingWeights();

    /**
     * Method "getLabeledAttributeMatchWeights" returns the attribute names with the last matching weights (or simply
     * the last matching weights) concatenated in a a string for display.
     * 
     * @return the readable string giving the information about the last comparison.
     */
    public String getLabeledAttributeMatchWeights();

    /**
     * Method "setblockingThreshold" sets a threshold value. When the attribute matching weight is below this value when
     * comparing blocking attributes, then the records will not match.
     * 
     * @param threshold the value (default value should be 1)
     * @return true if ok
     */
    boolean setblockingThreshold(double threshold);

    /**
     * Getter for record match threshold.
     * 
     * @return the recordMatchThreshold
     */
    double getRecordMatchThreshold();

    /**
     * Sets the recordMatchThreshold sets a record matching threshold. When the computed matching weight is above the
     * threshold, the compared records match. When below, they don't.
     * 
     * @param recordMatchThreshold the recordMatchThreshold to set
     */
    void setRecordMatchThreshold(double recordMatchThreshold);

    /**
     * 
     * Set to display the distance details labels or not.
     * 
     * @param displayLabels
     */
    public void setDisplayLabels(boolean displayLabels);
}
