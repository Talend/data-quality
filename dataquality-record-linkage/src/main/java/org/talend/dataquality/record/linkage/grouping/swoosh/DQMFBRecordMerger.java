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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.AttributeValues;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.MFBRecordMerger;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * created by zhao on Jul 9, 2014 the merger which adapt to incorporate DQ specific grouping information.
 * 
 */
public class DQMFBRecordMerger extends MFBRecordMerger {

    private SurvivorShipAlgorithmParams matchMergeParam = null;

    public DQMFBRecordMerger(String mergedRecordSource, String[] parameters, SurvivorShipAlgorithmEnum[] typeMergeTable) {
        super(mergedRecordSource, parameters, typeMergeTable);
    }

    public DQMFBRecordMerger(String mergedRecordSource, String[] parameters, SurvivorShipAlgorithmEnum[] typeMergeTable,
            SurvivorShipAlgorithmParams matchMergeParam) {
        super(mergedRecordSource, parameters, typeMergeTable);
        this.matchMergeParam = matchMergeParam;
    }

    public SurvivorShipAlgorithmParams getMatchMergeParam() {
        return matchMergeParam;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#merge(org.talend.dataquality.matchmerge.Record,
     * org.talend.dataquality.matchmerge.Record)
     */
    @Override
    public Record merge(Record record1, Record record2) {
        // Update the merge function given the current matcher.
        IRecordMatcher recordMatcher = matchMergeParam.getRecordMatcher();
        if (recordMatcher instanceof CombinedRecordMatcher) {
            IRecordMatcher lastMatcher = ((CombinedRecordMatcher) recordMatcher).getLastPositiveMatcher();
            // Update merge functions.
            SurvivorshipFunction[] survFuncs = matchMergeParam.getSurvivorshipAlgosMap().get(lastMatcher);
            int idx = 0;
            for (SurvivorshipFunction func : survFuncs) {
                typeMergeTable[idx] = func.getSurvivorShipAlgoEnum();
                parameters[idx++] = func.getParameter();
            }
        }

        return super.merge(record1, record2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#createNewRecord(org.talend.dataquality.matchmerge.Record,
     * long)
     */
    @Override
    protected Record createNewRecord(Record record1, Record record2, long mergedRecordTimestamp) {
        RichRecord richRecord1 = (RichRecord) record1;
        RichRecord richRecord2 = (RichRecord) record2;
        RichRecord mergedRecord = new RichRecord(record1.getId(), mergedRecordTimestamp, mergedRecordSource);
        DQAttribute<?>[] mergedRows = richRecord1.getOriginRow().toArray(new DQAttribute<?>[richRecord1.getOriginRow().size()]);

        // Merge columns which are not maching keys using default survior rules.
        List<Attribute> matchingAttributes = record1.getAttributes();
        for (int colIdx = 0; colIdx < richRecord1.getOriginRow().size(); colIdx++) {
            boolean isMatchKeyIndex = false;
            for (Attribute attribute : matchingAttributes) {
                if (attribute.getColumnIndex() == colIdx) {
                    isMatchKeyIndex = true;
                    break;
                }
            }
            // added merge function for all columns which don't contain survivorship function
            if (!isMatchKeyIndex) {
                Map<Integer, SurvivorshipFunction> defaultSurvivorshipFuncs = matchMergeParam.getDefaultSurviorshipRules();
                SurvivorshipFunction survivorshipFunc = defaultSurvivorshipFuncs.get(colIdx);
                if (survivorshipFunc == null || survivorshipFunc.getSurvivorShipAlgoEnum() == null) {
                    // No default survivorship function was set.
                    continue;
                }

                // attributes which are not matching keys
                // Keep values from original records (if any)
                String leftValue = richRecord1.getOriginRow().get(colIdx).getValue();
                String rightValue = richRecord2.getOriginRow().get(colIdx).getValue();
                Integer referenceColumnIndex = survivorshipFunc.getReferenceColumnIndex();
                // Get the merged value and update the merged row.
                mergedRows[colIdx] = new DQAttribute<>(StringUtils.EMPTY, colIdx, leftValue,
                        referenceColumnIndex == null ? colIdx : referenceColumnIndex.intValue());// No
                // label
                // for
                // the
                AttributeValues<String> leftValues = richRecord1.getOriginRow().get(colIdx).getValues();
                if (leftValues.size() > 0) {
                    mergedRows[colIdx].getValues().merge(leftValues);
                } else {
                    mergedRows[colIdx].getValues().get(leftValue).increment();
                }
                AttributeValues<String> rightValues = richRecord2.getOriginRow().get(colIdx).getValues();
                if (rightValues.size() > 0) {
                    mergedRows[colIdx].getValues().merge(rightValues);
                } else {
                    mergedRows[colIdx].getValues().get(rightValue).increment();
                }
                // Merge values
                if (leftValue == null && rightValue == null) {
                    mergedRows[colIdx].setValue(null);
                } else {
                    SurvivorShipAlgorithmEnum survAlgo = survivorshipFunc.getSurvivorShipAlgoEnum();
                    String parameter = survivorshipFunc.getParameter();
                    String mergedValue = null;
                    String mergedCompareValue = null;
                    switch (survAlgo) {
                    case MOST_RECENT:
                    case MOST_ANCIENT:
                        String leftCompareValue = leftValue;
                        String rightCompareValue = rightValue;

                        if (referenceColumnIndex != null && !referenceColumnIndex.equals(colIdx)
                                && (datePatternMap == null || datePatternMap.get(referenceColumnIndex.toString()) != null)) {
                            leftCompareValue = richRecord1.getOriginRow().get(colIdx).getReferenceValue();
                            rightCompareValue = richRecord2.getOriginRow().get(colIdx).getReferenceValue();
                            leftCompareValue = leftCompareValue == null
                                    ? richRecord1.getOriginRow().get(referenceColumnIndex).getValue() : leftCompareValue;
                            rightCompareValue = rightCompareValue == null
                                    ? richRecord2.getOriginRow().get(referenceColumnIndex).getValue() : rightCompareValue;
                        } else {
                            referenceColumnIndex = colIdx;
                        }
                        mergedCompareValue = compareAsDate(leftCompareValue, rightCompareValue, survAlgo,
                                String.valueOf(referenceColumnIndex), record1.getTimestamp(), record2.getTimestamp());
                        if (mergedCompareValue != null) {
                            if (mergedCompareValue.equals(leftCompareValue)) {
                                mergedValue = leftValue;
                            } else {
                                mergedValue = rightValue;
                            }
                        }
                        break;
                    default:
                        mergedValue = createMergeValue(record1.getSource(), record2.getSource(), parameter,
                                record1.getTimestamp(), record2.getTimestamp(), survAlgo, leftValue, rightValue,
                                mergedRows[colIdx].getValue(), mergedRows[colIdx].getValues());
                        break;
                    }
                    if (mergedValue != null) {
                        mergedRows[colIdx].setValue(mergedValue);
                        mergedRows[colIdx].setReferenceValue(mergedCompareValue);
                    }
                }

            }
            /**
             * Else the matching key's value will be udpated when call {@link RichRecord#getOutputRow()}
             */

        }
        List<DQAttribute<?>> originalRowList = new ArrayList<DQAttribute<?>>();
        CollectionUtils.addAll(originalRowList, mergedRows);
        mergedRecord.setRecordSize(richRecord1.getRecordSize());
        mergedRecord.setOriginRow(originalRowList);
        // Set the group quality
        double minQuality = getGrpqualityMinValue(richRecord1, richRecord2);

        if (Double.compare(minQuality, 0.0) != 0) {
            mergedRecord.setGroupQuality(minQuality);
        }
        // Added TDQ-12659: only when the GRP_SIZE is not null( = multipass), need to modify the group size
        if (richRecord1.getGRP_SIZE() != null && richRecord2.getGRP_SIZE() != null) {
            int newSize = Integer.valueOf(richRecord1.getGRP_SIZE().getValue())
                    + Integer.valueOf(richRecord2.getGRP_SIZE().getValue());
            if (newSize > 0) {
                mergedRecord.setGRP_SIZE(newSize);
            }
        }
        return mergedRecord;
    }

    private double getGrpqualityMinValue(RichRecord record1, RichRecord record2) {
        double gQuality1 = record1.getGroupQuality();
        double gQuality2 = record2.getGroupQuality();
        DQAttribute<String> grpQUALITY1 = record1.getGRP_QUALITY();
        DQAttribute<String> grpQUALITY2 = record2.getGRP_QUALITY();
        String value1 = null, value2 = null;
        if (grpQUALITY1 != null && grpQUALITY2 != null) {
            value1 = grpQUALITY1.getValue();
            value2 = grpQUALITY2.getValue();
        }

        if (StringUtils.isNotBlank(value1) && StringUtils.isNotBlank(value2)) {
            if (Double.compare(1.0, Double.parseDouble(value1)) == 0 && Double.compare(1.0, Double.parseDouble(value2)) == 0) {
                return gQuality1;
            }
            if (Double.compare(gQuality1, 0.0) > 0) {
                gQuality1 = Double.parseDouble(value1) > gQuality1 ? gQuality1 : Double.parseDouble(value1);
            } else {
                gQuality1 = Double.parseDouble(value1);
            }
            if (Double.compare(gQuality2, 0.0) > 0) {
                gQuality2 = Double.parseDouble(value2) > gQuality2 ? gQuality2 : Double.parseDouble(value2);
            } else {
                gQuality2 = Double.parseDouble(value2);
            }
        }
        return Math.min(gQuality1, gQuality2);

    }
}