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
package org.talend.dataquality.record.linkage.grouping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;

/**
 * <strike> feature TDQ-1707, record comparison algorithm with blocking key for component tMatchGroupHadoop.</strike>
 * The record grouping interface handles record grouping
 */
public interface IRecordGrouping<T> {

    /**
     * Key for retrieving the matching type.
     */
    public static final String MATCHING_TYPE = "MATCHING_TYPE"; //$NON-NLS-1$

    /**
     * Key used for retrieving the custom matching class when the user implements his own matching algorithm.
     */
    public static final String CUSTOMER_MATCH_CLASS = "CUSTOMER_MATCH_CLASS"; //$NON-NLS-1$

    /**
     * Key for retrieving the confidence weight.
     */
    public static final String CONFIDENCE_WEIGHT = "CONFIDENCE_WEIGHT"; //$NON-NLS-1$

    /**
     * Attribute threshold
     */
    public static final String ATTRIBUTE_THRESHOLD = "ATTRIBUTE_THRESHOLD"; //$NON-NLS-1$

    /**
     * Key for Record match threshold (match interval).
     */
    public static final String RECORD_MATCH_THRESHOLD = "RECORD_MATCH_THRESHOLD"; //$NON-NLS-1$

    // zero based
    public static final String COLUMN_IDX = "COLUMN_IDX"; //$NON-NLS-1$

    public static final String HANDLE_NULL = "HANDLE_NULL"; //$NON-NLS-1$

    public static final String JAR_PATH = "JAR_PATH"; //$NON-NLS-1$

    // Key for the the matcher's attribute name.
    public static final String ATTRIBUTE_NAME = "ATTRIBUTE_NAME"; //$NON-NLS-1$

    public static final String MATCH_KEY_NAME = "MATCH_KEY_NAME"; //$NON-NLS-1$

    public static final String TOKENIZATION_TYPE = "TOKENIZATION_TYPE"; //$NON-NLS-1$ The Value should keep same with MatchAnalysisConstant.TOKENIZATION_TYPE

    /**
     * 
     * Prepare the parameters (key definition) of the matching algorithm.
     * 
     * @param matchingSettings <br>
     * Input matching attributes with the algorithms. e.g<br>
     * Map<String,String> recordMap = new HashMap<String,String>(); <br>
     * recordMap.put(INPUT_KEY_ATTRIBUTE,"name");<br>
     * recordMap.put(MATCHING_TYPE,"Exact");<br>
     * recordMap.put(CONFIDENCE_WEIGHT,"1.0");<br>
     * attributes.add(recordMap); <br>
     * ......<br>
     * attributes.add(recordMap1);
     * 
     * When all matching settings are passed to this class, call the {@link #initialize()} method.
     * @deprecated use {{@link #addMatchRule(List)}
     */
    @Deprecated
    public void add(Map<String, String> matchingSettings);

    /**
     * 
     * Initialize parameters before grouping.
     */
    public void initialize() throws InstantiationException, IllegalAccessException, ClassNotFoundException;

    /**
     * 
     * Groups similar records together according to the matching definitions.
     * 
     * @param inputRow
     * @param context the hadoop context
     * @param column delimiter
     */
    public void doGroup(T[] inputRow) throws IOException, InterruptedException;

    /**
     * 
     * Set acceptable threshold.
     * 
     * @param acceptableThreshold
     * @deprecated {@link IRecordMatcher#setRecordMatchThreshold(double)}
     */
    @Deprecated
    public void setAcceptableThreshold(float acceptableThreshold);

    /**
     * 
     * End of matching.
     */
    public void end() throws IOException, InterruptedException;

    /**
     * 
     * Set output distance details.
     * 
     * @param isOutputDistDetails
     */
    public void setIsOutputDistDetails(boolean isOutputDistDetails);

    /**
     * Set the column delimiter.
     * 
     * @param columnDelimiter
     */
    public void setColumnDelimiter(String columnDelimiter);

    /**
     * 
     * If current matching pass is based on a previous matching result.
     * 
     * @param isLinkToPrevious
     */
    public void setIsLinkToPrevious(Boolean isLinkToPrevious);

    /**
     * add one match rule , the match key in the matcher should be refined with dummy matcher and order by column index
     * when there are more than one matchers defined.
     * 
     * @param matchRule the matcher to be added.
     */
    public void addMatchRule(List<Map<String, String>> matchRule);

    /**
     * 
     * set if display attribute labels.
     * 
     * @param isDisplayAttLabels
     */
    public void setIsDisplayAttLabels(Boolean isDisplayAttLabels);

    public void setSurvivorShipAlgorithmParams(SurvivorShipAlgorithmParams survivorShipAlgorithmParams);

    public void setRecordLinkAlgorithm(RecordMatcherType algorithm);

    /**
     * 
     * set if compute group quality.
     * 
     * @param isComputeGrpQuality.if true,it will compute group quality and output value as column "GRP_QUALITY".
     */
    @Deprecated
    public void setIsComputeGrpQuality(Boolean isComputeGrpQuality);

    public void setIsPassOriginalValue(Boolean isPassOriginalValue);

}
