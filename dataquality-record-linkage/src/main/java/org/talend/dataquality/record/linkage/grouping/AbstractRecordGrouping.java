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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.matchmerge.mfb.MFBAttributeMatcher;
import org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher;
import org.talend.dataquality.record.linkage.attribute.AttributeMatcherFactory;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQMFBRecordMatcher;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.record.RecordMatcherFactory;
import org.talend.dataquality.record.linkage.utils.CustomAttributeMatcherClassNameConvert;
import org.talend.utils.classloader.TalendURLClassLoader;

/**
 * created by zhao on Jul 19, 2013 <br>
 * Abstract record grouping implementation without hadoop API included.
 * 
 */
public abstract class AbstractRecordGrouping<TYPE> implements IRecordGrouping<TYPE> {

    protected List<TYPE[]> masterRecords = new ArrayList<TYPE[]>();

    public static final float DEFAULT_THRESHOLD = 0.95f;

    private float acceptableThreshold = DEFAULT_THRESHOLD;

    // Output distance details or not.
    private boolean isOutputDistDetails = Boolean.FALSE;

    protected CombinedRecordMatcher combinedRecordMatcher = RecordMatcherFactory.createCombinedRecordMatcher();

    /**
     * @deprecated use {{@link #multiMatchRules}
     */
    @Deprecated
    private List<Map<String, String>> matchingColumns = new ArrayList<Map<String, String>>();

    protected List<List<Map<String, String>>> multiMatchRules = new ArrayList<List<Map<String, String>>>();

    protected SurvivorShipAlgorithmParams survivorShipAlgorithmParams = null;

    protected String columnDelimiter = null;

    protected Boolean isLinkToPrevious = Boolean.FALSE;

    protected Boolean isPassOriginalValue = Boolean.FALSE;

    protected int originalInputColumnSize;

    private Boolean isDisplayAttLabels = Boolean.TRUE;

    private Boolean isGIDStringType = Boolean.TRUE;

    // old tMatchGroup GID using Long type
    AtomicLong atomicLongGID = new AtomicLong();

    // VSR algorithm by default.
    protected RecordMatcherType matchAlgo = RecordMatcherType.simpleVSRMatcher;

    protected TSwooshGrouping<TYPE> swooshGrouping = new TSwooshGrouping<TYPE>(this);

    // The exthended column size.
    protected int extSize;

    // Allow compute Group Quality.
    private Boolean isComputeGrpQuality = Boolean.FALSE;

    protected Boolean isStoreOndisk = Boolean.FALSE;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.hadoop.group.IRecordGrouping#setColumnDelimiter(java.lang.String)
     */
    @Override
    public void setColumnDelimiter(String columnDelimiter) {
        this.columnDelimiter = columnDelimiter;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.hadoop.group.IRecordGrouping#SetIsLinkToPrevious(java.lang.Boolean)
     */
    @Override
    public void setIsLinkToPrevious(Boolean isLinkToPrevious) {
        this.isLinkToPrevious = isLinkToPrevious;
    }

    /**
     * @deprecated {@link IRecordMatcher#setRecordMatchThreshold(double)}
     */
    @Deprecated
    @Override
    public void setAcceptableThreshold(float acceptableThreshold) {
        this.acceptableThreshold = acceptableThreshold;
    }

    /**
     * Getter for isOutputDistDetails.
     * 
     * @return the isOutputDistDetails
     */
    public boolean isOutputDistDetails() {
        return this.isOutputDistDetails;
    }

    /**
     * Getter for isDisplayAttLabels.
     * 
     * @return the isDisplayAttLabels
     */
    public Boolean getIsDisplayAttLabels() {
        return this.isDisplayAttLabels;
    }

    @Override
    public void setIsOutputDistDetails(boolean isOutputDistDetails) {
        this.isOutputDistDetails = isOutputDistDetails;
    }

    /**
     * Set original input column size.except GID,MASTER,SCORE,GRP_SIZE,GRP_QUALITY,MATCHING_DISTANCES.
     * 
     * @param prevOrginalColumnSize the prevOrginalColumnSize to set
     */
    public void setOrginalInputColumnSize(int originalInputColumnSize) {
        this.originalInputColumnSize = originalInputColumnSize;
    }

    /**
     * Sets the isSeperateOutput.
     * 
     * @param isSeperateOutput the isSeperateOutput to set
     * @deprecated Use {@link #setIsComputeGrpQuality(Boolean)} instead.
     */
    @Deprecated
    @Override
    public void setSeperateOutput(boolean isSeperateOutput) {
        this.setIsComputeGrpQuality(isSeperateOutput);
    }

    public int getOriginalInputColumnSize() {
        return originalInputColumnSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.IRecordGrouping#setIsDisplayAttLabels(java.lang.Boolean)
     */
    @Override
    public void setIsDisplayAttLabels(Boolean isDisplayAttLabels) {
        this.isDisplayAttLabels = isDisplayAttLabels;

    }

    /*
     * 
     * Set GID data type. if it is import form old version,the data type is Long. or else it is String .
     */
    public void setIsGIDStringType(Boolean isGIDStringType) {
        this.isGIDStringType = isGIDStringType;

    }

    /*
     * set if need to compute group quality.
     */
    @Override
    public void setIsComputeGrpQuality(Boolean isComputeGrpQuality) {
        this.isComputeGrpQuality = isComputeGrpQuality;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.grouping.IRecordGrouping#setMatchingAlgorithm(org.talend.dataquality.record
     * .linkage.grouping.AbstractRecordGrouping.MatchAlgoithm)
     */
    @Override
    public void setRecordLinkAlgorithm(RecordMatcherType algorithm) {
        matchAlgo = algorithm;
    }

    @Override
    public void doGroup(TYPE[] inputRow) throws IOException, InterruptedException {

        extSize = isOutputDistDetails ? 5 : 4;
        // extSize + 1 when isSeperateOutput is enabled.
        extSize = isComputeGrpQuality ? extSize + 1 : extSize;

        if (multiMatchRules.isEmpty()) {
            // No rule defined.
            return;
        }
        switch (matchAlgo) {
        case simpleVSRMatcher:
            // if the inputRow size less than original column size,should set 'isLinkToPrevious' to false and work on
            // none-multi-pass.
            if (isLinkToPrevious && inputRow.length <= originalInputColumnSize) {
                isLinkToPrevious = false;
            }
            // In case of current component is linked to previous, and the record is NOT master, just put it to the
            // output and continue;
            if (isLinkToPrevious && !isMaster(inputRow[originalInputColumnSize + 2])) {
                TYPE[] inputRowWithExtColumns = createNewInputRowForMultPass(inputRow, originalInputColumnSize + extSize);
                outputRow(inputRowWithExtColumns);
                return;
            }
            // temporary array to store attributes to match
            List<Map<String, String>> matchingRule = multiMatchRules.get(0);
            String[] lookupDataArray = new String[matchingRule.size()];

            for (int idx = 0; idx < lookupDataArray.length; idx++) {
                Object inputObj = inputRow[Integer.parseInt(matchingRule.get(idx).get(IRecordGrouping.COLUMN_IDX))];
                lookupDataArray[idx] = inputObj == null ? null : String.valueOf(inputObj);
            }

            vsrMatch(inputRow, matchingRule, lookupDataArray);
            break;
        case T_SwooshAlgorithm:// used for "chart" in analysis
            swooshGrouping.addToList(inputRow, multiMatchRules);
            break;
        }
    }

    /**
     * Record matching with VSR algorithm.
     * 
     * @param inputRow the input row.
     * @param matchingRule mathcing rules.
     * @param lookupDataArray the array data (record) to be matched with.
     * @throws IOException
     * @throws InterruptedException
     */
    private void vsrMatch(TYPE[] inputRow, List<Map<String, String>> matchingRule, String[] lookupDataArray)
            throws IOException, InterruptedException {
        boolean isSimilar = false;
        for (TYPE[] masterRecord : masterRecords) {
            if (isLinkToPrevious) {
                int masterGRPSize = Integer.valueOf(String.valueOf(masterRecord[originalInputColumnSize + 1]));
                int inputGRPSize = Integer.valueOf(String.valueOf(inputRow[originalInputColumnSize + 1]));
                // Don't compare the records whose GRP_SIZE both > 1.
                if (masterGRPSize > 1 && inputGRPSize > 1) {
                    continue;
                }
            }

            String[] masterMatchRecord = new String[lookupDataArray.length];
            // Find the match record from master record.
            for (int idx = 0; idx < lookupDataArray.length; idx++) {
                Object masterObj = masterRecord[Integer.parseInt(matchingRule.get(idx).get(IRecordGrouping.COLUMN_IDX))];
                masterMatchRecord[idx] = masterObj == null ? null : String.valueOf(masterObj);
            }
            double matchingProba = combinedRecordMatcher.getMatchingWeight(masterMatchRecord, lookupDataArray);
            // Similar
            if (matchingProba >= combinedRecordMatcher.getRecordMatchThreshold()) {
                String distanceDetails = computeOutputDetails();
                isSimilar = true;
                // Master GRP_SIZE ++
                if (isLinkToPrevious) {
                    int masterGRPSize = Integer.valueOf(String.valueOf(masterRecord[originalInputColumnSize + 1]));
                    if (masterGRPSize == 1) {
                        inputRow[originalInputColumnSize + 1] = incrementGroupSize(inputRow[originalInputColumnSize + 1]);
                        TYPE[] inputRowWithExtColumns = createNewInputRowForMultPass(inputRow, originalInputColumnSize + extSize);
                        // since the 'masterRecord' will be output as a duplicate,if the masterRecord Gneed GRP_QUALITY
                        // is less than Input,should set masterRecord GRP_QUALITY to 'inputRow_with_extColumns' at here.
                        if (isComputeGrpQuality) {
                            double inputGRP = Double.valueOf(String.valueOf(inputRowWithExtColumns[originalInputColumnSize + 4]));
                            double masterGRP = Double.valueOf(String.valueOf(masterRecord[originalInputColumnSize + 4]));
                            if (masterGRP < inputGRP) {
                                inputRowWithExtColumns[originalInputColumnSize + 4] = masterRecord[originalInputColumnSize + 4];
                            }
                        }
                        updateWithExtendedColumn(masterRecord, inputRowWithExtColumns, matchingProba, distanceDetails,
                                columnDelimiter);
                        // Update master record from the temporary master list.
                        masterRecords.remove(masterRecord);
                        masterRecords.add(inputRowWithExtColumns);
                        break;
                    }
                }

                masterRecord[masterRecord.length - extSize + 1] = incrementGroupSize(
                        masterRecord[masterRecord.length - extSize + 1]);

                // Duplicated record
                updateWithExtendedColumn(inputRow, masterRecord, matchingProba, distanceDetails, columnDelimiter);
                break;
            }

        }
        if (!isSimilar) {
            // For the passes that linked to previous, the extension size (e.g: GID,GRP_SIZE) had already been set
            // before.

            // Master record
            int inputColumnLenth = isLinkToPrevious ? originalInputColumnSize : inputRow.length;
            TYPE[] masterRow = createTYPEArray(inputColumnLenth + extSize);
            for (int idx = 0; idx < inputRow.length; idx++) {
                masterRow[idx] = inputRow[idx];
            }
            int extIdx = 0;
            if (!isLinkToPrevious) {
                // GID
                if (this.isGIDStringType) {
                    masterRow[masterRow.length - extSize] = castAsType(UUID.randomUUID().toString());
                } else {
                    masterRow[masterRow.length - extSize] = castAsType(atomicLongGID.incrementAndGet());
                }
                // Group size
                masterRow[masterRow.length - extSize + 1] = castAsType(1);
                // Master
                masterRow[masterRow.length - extSize + 2] = castAsType(true);
                // Score
                masterRow[masterRow.length - extSize + 3] = castAsType(1.0);

            }
            if (isComputeGrpQuality) {
                // Group quality for multiple pass
                extIdx++;
                if (!isLinkToPrevious) {
                    masterRow[inputColumnLenth + 4] = castAsType(1.0);
                }
            }
            if (isOutputDistDetails) {
                // Match distance details for multiple pass
                masterRow[inputColumnLenth + 4 + extIdx] = castAsType(StringUtils.EMPTY);
            }

            masterRecords.add(masterRow);
        }
    }

    /**
     * This method is for 2nd tMatchGroup in multi-pass only.add the external columns into input schema.
     * 
     * @param inputRow the output of 1st tMatchGroup. Then will be the input of 2nd tMatchGroup.
     * @param extSizeWithLinkPrev
     * @return
     */
    private TYPE[] createNewInputRowForMultPass(TYPE[] inputRow, int newLength) {
        TYPE[] inputRowWithExtColumn = createTYPEArray(newLength);
        for (int idx = 0; idx < inputRow.length; idx++) {
            inputRowWithExtColumn[idx] = inputRow[idx];// == null ? null : (TYPE) String.valueOf(inputRow[idx]);
        }

        int extInd = 0;
        if (isComputeGrpQuality) {
            // In case of multi-pass, the value of index "originalInputColumnSize + 4" is the 1st tMatchGroup's
            // group quality. should propagate it to next tMatchGroup.or else set default value 0.0.
            TYPE inputGRP = inputRowWithExtColumn[originalInputColumnSize + 4];
            if (inputGRP == null) {
                inputRowWithExtColumn[originalInputColumnSize + 4] = castAsType(0.0);
            }
            extInd++;
        }

        if (isOutputDistDetails) {
            // In case of multi-pass, the value of index "originalInputColumnSize + 4+ extInd" is the 1st tMatchGroup's
            // distance details. should propagate it to next tMatchGroup.or else set the Empty to Match distance.
            TYPE inputDistance = inputRowWithExtColumn[originalInputColumnSize + 4 + extInd];
            if (inputDistance == null) {
                inputRowWithExtColumn[originalInputColumnSize + 4 + extInd] = castAsType(StringUtils.EMPTY);
            }
        }
        return inputRowWithExtColumn;
    }

    /**
     * DOC zhao Comment method "computeGroupQuality".
     * 
     * @param matchingProba
     * @return
     */
    private double computeGroupQuality(TYPE[] masterRecord, double matchingProba, int idx) {
        double groupQuality = Double.valueOf(String.valueOf(masterRecord[masterRecord.length - extSize + idx]));
        if (matchingProba < groupQuality) {
            // Use the minimal match distance as the group score.
            groupQuality = matchingProba;
        }
        return groupQuality;
    }

    /**
     * DOC zhao Comment method "computeOutputDetails".
     * 
     * @param distanceDetails
     * @return
     */
    private String computeOutputDetails() {
        String distanceDetails = StringUtils.EMPTY;
        if (isOutputDistDetails) {
            combinedRecordMatcher.setDisplayLabels(isDisplayAttLabels);
            distanceDetails = combinedRecordMatcher.getLabeledAttributeMatchWeights();
        }
        return distanceDetails;
    }

    @Override
    public void end() throws IOException, InterruptedException {
        // output the masters
        for (TYPE[] mst : masterRecords) {
            outputRow(mst);
        }
        clear();
    }

    protected void clear() {
        multiMatchRules.clear();
    }

    private void updateWithExtendedColumn(TYPE[] inputRow, TYPE[] masterRecord, double matchingProba, String distanceDetails,
            String delimiter) throws IOException, InterruptedException {
        TYPE[] duplicateRecord = createTYPEArray(masterRecord.length);
        for (int idx = 0; idx < inputRow.length; idx++) {
            duplicateRecord[idx] = inputRow[idx];
        }
        // GID
        duplicateRecord[duplicateRecord.length - extSize] = masterRecord[masterRecord.length - extSize];
        // Group size

        duplicateRecord[duplicateRecord.length - extSize + 1] = castAsType(0);
        // Master
        duplicateRecord[duplicateRecord.length - extSize + 2] = castAsType(false);
        // Score
        duplicateRecord[duplicateRecord.length - extSize + 3] = castAsType(matchingProba);

        int extIdx = 3;
        // Group quality
        if (isComputeGrpQuality) {
            extIdx++;
            double groupQuality = computeGroupQuality(masterRecord, matchingProba, extIdx);
            masterRecord[duplicateRecord.length - extSize + extIdx] = castAsType(groupQuality);
            // change the duplicate group quality to 0.0 .
            duplicateRecord[duplicateRecord.length - extSize + extIdx] = castAsType(0.0);
        }
        if (isOutputDistDetails) {
            extIdx++;
            // Match distance details
            duplicateRecord[duplicateRecord.length - extSize + extIdx] = castAsType(distanceDetails);
        }
        // output the duplicate record
        outputRow(duplicateRecord);
    }

    /**
     * @deprecated use {{@link #addMatchRule}
     */
    @Deprecated
    @Override
    public void add(Map<String, String> matchingColumn) {
        matchingColumns.add(matchingColumn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.hadoop.group.IRecordGrouping#addMatchRule(java.util.List)
     */
    @Override
    public void addMatchRule(List<Map<String, String>> matchRule) {
        multiMatchRules.add(matchRule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.grouping.IRecordGrouping#setSurvivorShipAlgorithmParams(org.talend.dataquality
     * .record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams)
     */
    @Override
    public void setSurvivorShipAlgorithmParams(SurvivorShipAlgorithmParams survivorShipAlgorithmParams) {
        this.survivorShipAlgorithmParams = survivorShipAlgorithmParams;
    }

    public List<List<Map<String, String>>> getMultiMatchRules() {
        return multiMatchRules;
    }

    /**
     * 
     * Output one row
     * 
     * @param row
     */
    protected abstract void outputRow(TYPE[] row);

    protected abstract void outputRow(RichRecord row);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.hadoop.group.IRecordGrouping#initialize()
     */
    @Override
    public void initialize() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        masterRecords.clear();
        combinedRecordMatcher = RecordMatcherFactory.createCombinedRecordMatcher();
        for (List<Map<String, String>> matchRule : multiMatchRules) {
            createRecordMatcher(matchRule);
        }
    }

    private void createRecordMatcher(List<Map<String, String>> matchRule)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final int recordSize = matchRule.size();
        double[] arrAttrWeights = new double[recordSize];
        double[] attrThresholds = new double[recordSize];
        String[] attributeNames = new String[recordSize];
        String[][] algorithmName = new String[recordSize][2];
        String[] arrMatchHandleNull = new String[recordSize];
        String[] customizedJarPath = new String[recordSize];
        TokenizedResolutionMethod[] tokenMethod = new TokenizedResolutionMethod[recordSize];
        double recordMatchThreshold = acceptableThreshold;// keep compatibility to older version.
        boolean isSwoosh = matchAlgo == RecordMatcherType.T_SwooshAlgorithm;
        int keyIdx = 0;
        for (Map<String, String> recordMap : matchRule) {
            algorithmName[keyIdx][0] = recordMap.get(IRecordGrouping.MATCHING_TYPE);
            tokenMethod[keyIdx] = getTokenMethod(recordMap);
            if (StringUtils.equalsIgnoreCase(AttributeMatcherType.DUMMY.name(), algorithmName[keyIdx][0])) {
                // Set confidence weight if exist
                if (null != recordMap.get(IRecordGrouping.CONFIDENCE_WEIGHT)) {
                    arrAttrWeights[keyIdx] = Double.parseDouble(recordMap.get(IRecordGrouping.CONFIDENCE_WEIGHT));
                }
                keyIdx++;
                continue;
            }
            arrAttrWeights[keyIdx] = Double.parseDouble(recordMap.get(IRecordGrouping.CONFIDENCE_WEIGHT));
            algorithmName[keyIdx][1] = recordMap.get(IRecordGrouping.CUSTOMER_MATCH_CLASS);
            attributeNames[keyIdx] = recordMap.get(IRecordGrouping.ATTRIBUTE_NAME);
            arrMatchHandleNull[keyIdx] = recordMap.get(IRecordGrouping.HANDLE_NULL);
            if (isSwoosh) {
                // Set attribute threshold
                String thresholdValue = recordMap.get(IRecordGrouping.ATTRIBUTE_THRESHOLD);
                if (thresholdValue == null || thresholdValue.trim().length() == 0) {
                    // default value when the algorithm switch from vsr to swooth
                    attrThresholds[keyIdx] = 1.0D;
                } else {
                    attrThresholds[keyIdx] = Double.parseDouble(thresholdValue);
                }

            }
            String rcdMathThresholdEach = recordMap.get(IRecordGrouping.RECORD_MATCH_THRESHOLD);
            customizedJarPath[keyIdx] = recordMap.get(IRecordGrouping.JAR_PATH);
            if (!StringUtils.isEmpty(rcdMathThresholdEach)) {
                recordMatchThreshold = Double.valueOf(rcdMathThresholdEach);

            }
            keyIdx++;
        }
        IAttributeMatcher[] attributeMatcher = new IAttributeMatcher[recordSize];

        for (int indx = 0; indx < recordSize; indx++) {
            AttributeMatcherType attrMatcherType = AttributeMatcherType.get(algorithmName[indx][0]);

            if (attrMatcherType == AttributeMatcherType.CUSTOM && customizedJarPath[indx] != null) {
                // Put the jar into class path so that the class can be loaded.
                TalendURLClassLoader cl = new TalendURLClassLoader(
                        CustomAttributeMatcherClassNameConvert.changeJarPathToURLArray(customizedJarPath[indx]));
                attributeMatcher[indx] = AttributeMatcherFactory.createMatcher(attrMatcherType,
                        CustomAttributeMatcherClassNameConvert.getClassName(algorithmName[indx][1]), cl);
            } else {
                // Use the default class loader to load the class.
                attributeMatcher[indx] = AttributeMatcherFactory.createMatcher(attrMatcherType, algorithmName[indx][1]);
                ((AbstractAttributeMatcher) attributeMatcher[indx]).setTokenMethod(tokenMethod[indx]);
            }
            // TDQ-11949 msjian : for the match rule which use the custom type algorithm, we will use the threshold
            // and weight from UI to match rule too
            if (isSwoosh) {
                attributeMatcher[indx] = MFBAttributeMatcher.wrap(attributeMatcher[indx], arrAttrWeights[indx],
                        attrThresholds[indx], SubString.NO_SUBSTRING);
            }
            // TDQ-11949~
            attributeMatcher[indx].setNullOption(arrMatchHandleNull[indx]);
            attributeMatcher[indx].setAttributeName(attributeNames[indx]);
        }

        IRecordMatcher recordMatcher = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);
        if (isSwoosh) {
            recordMatcher = new DQMFBRecordMatcher(recordMatchThreshold);
        }
        recordMatcher.setRecordSize(recordSize);
        recordMatcher.setAttributeWeights(arrAttrWeights);
        recordMatcher.setAttributeMatchers(attributeMatcher);
        recordMatcher.setRecordMatchThreshold(recordMatchThreshold);
        combinedRecordMatcher.add(recordMatcher);
    }

    private TokenizedResolutionMethod getTokenMethod(Map<String, String> recordMap) {
        if (IRecordGrouping.TOKENIZATION_TYPE == null) {
            return TokenizedResolutionMethod.NO;
        }
        return TokenizedResolutionMethod.getTypeByValueWithDefault(recordMap.get(IRecordGrouping.TOKENIZATION_TYPE));
    }

    /**
     * Getter for combinedRecordMatcher.
     * 
     * @return the combinedRecordMatcher
     */
    public CombinedRecordMatcher getCombinedRecordMatcher() {
        return this.combinedRecordMatcher;
    }

    protected abstract boolean isMaster(TYPE col);

    protected abstract TYPE incrementGroupSize(TYPE oldGroupSize);

    protected abstract TYPE[] createTYPEArray(int size);

    protected abstract TYPE castAsType(Object objectValue);

    /**
     * Getter for survivorShipAlgorithmParams.
     * 
     * @return the survivorShipAlgorithmParams
     */
    protected SurvivorShipAlgorithmParams getSurvivorShipAlgorithmParams() {
        return this.survivorShipAlgorithmParams;
    }

    @Override
    public void setIsPassOriginalValue(Boolean isPassOriginal) {

        this.isPassOriginalValue = isPassOriginal;
    }

    public void setIsStoreOndisk(Boolean isStoreOndisk) {
        this.isStoreOndisk = isStoreOndisk;
    }

}
