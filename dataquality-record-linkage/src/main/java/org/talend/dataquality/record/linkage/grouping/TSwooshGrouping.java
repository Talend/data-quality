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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.RecordGenerator;
import org.talend.dataquality.matchmerge.mfb.RecordIterator.ValueGenerator;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQMFB;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQMFBRecordMerger;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQRecordIterator;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.grouping.swoosh.SwooshConstants;
import org.talend.dataquality.record.linkage.grouping.swoosh.golden.DQGoldenRecordIterator;
import org.talend.dataquality.record.linkage.grouping.swoosh.golden.DQGoldenRecordMFB;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.utils.BidiMultiMap;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * Record grouping class with t-swoosh algorithm.
 * 
 */
public class TSwooshGrouping<TYPE> {

    List<RecordGenerator> rcdsGenerators = new ArrayList<>();

    int totalCount = 0;

    AbstractRecordGrouping<TYPE> recordGrouping;

    private BidiMultiMap<String, String> oldGID2New = new BidiMultiMap<>();

    // Added TDQ-9320: to use the algorithm handle the record one by one
    private DQMFB algorithm;

    // Added TDQ-12057
    private boolean hasPassedOriginal = false;

    // Added TDQ-14276 , used for format the date before toString
    private SimpleDateFormat sdf = new SimpleDateFormat("", java.util.Locale.US);

    /**
     * DOC zhao TSwooshGrouping constructor comment.
     */
    public TSwooshGrouping(AbstractRecordGrouping<TYPE> recordGrouping) {
        this.recordGrouping = recordGrouping;
    }

    /**
     * Getter for oldGID2New.
     * 
     * @return the oldGID2New
     */
    public Map<String, String> getOldGID2New() {
        return this.oldGID2New;
    }

    /**
     * Recording matching with t-swoosh algorithm. Used for tmatchgroup only
     * 
     * @param inputRow
     * @param matchingRule
     */
    public void addToList(final TYPE[] inputRow, List<List<Map<java.lang.String, java.lang.String>>> multiMatchRules) {
        totalCount++;
        String attributeName;
        Map<java.lang.String, ValueGenerator> rcdMap = new LinkedHashMap<>();
        for (List<Map<java.lang.String, java.lang.String>> matchRule : multiMatchRules) {
            for (final Map<java.lang.String, java.lang.String> recordMap : matchRule) {
                attributeName = recordMap.get(IRecordGrouping.ATTRIBUTE_NAME);
                if (attributeName == null) {
                    // Dummy matcher
                    continue;
                }
                rcdMap.put(attributeName, new ValueGenerator() {

                    @Override
                    public int getColumnIndex() {
                        return Integer.valueOf(recordMap.get(IRecordGrouping.COLUMN_IDX));
                    }

                    @Override
                    public java.lang.String newValue() {
                        Integer columnIndex = Integer.valueOf(recordMap.get(IRecordGrouping.COLUMN_IDX));
                        TYPE value = inputRow[columnIndex];
                        if (value != null && value instanceof Date) {
                            return getFormatDate((Date) value, columnIndex);
                        } else {
                            return value == null ? null : value.toString();
                        }
                    }

                    // Added TDQ-12057 : return the current column's values from the last original
                    // values.(multipass+swoosh+passOriginal)
                    // the original is the last one.
                    @Override
                    public Object getAttribute() {
                        TYPE type = inputRow[inputRow.length - 1];
                        if (type instanceof List) {
                            List<Attribute> attris = (List<Attribute>) inputRow[inputRow.length - 1];
                            Integer colIndex = Integer.valueOf(recordMap.get(IRecordGrouping.COLUMN_IDX));
                            for (Attribute att : attris) {
                                if (att.getColumnIndex() == colIndex) {

                                    return att.getValues();
                                }
                            }
                        }
                        return null;
                    }

                    @Override
                    public int getReferenceColumnIndex() {
                        String refColumnIndex = recordMap.get(IRecordGrouping.REFERENCE_COLUMN_IDX);
                        if (refColumnIndex == null) {
                            return getColumnIndex();
                        } else {
                            return Integer.valueOf(refColumnIndex);
                        }
                    }

                    @Override
                    public String getReferenceValue() {
                        String refColumnIndex = recordMap.get(IRecordGrouping.REFERENCE_COLUMN_IDX);
                        if (refColumnIndex == null) {
                            return newValue();
                        }
                        Integer columnIndex = Integer.valueOf(refColumnIndex);
                        TYPE value = inputRow[columnIndex];
                        if (value != null && value instanceof Date) {
                            return getFormatDate((Date) value, columnIndex);
                        } else {
                            return value == null ? null : value.toString();
                        }
                    }

                });
            }
        }
        RecordGenerator rcdGen = new RecordGenerator();
        rcdGen.setMatchKeyMap(rcdMap);
        List<DQAttribute<?>> rowList = new ArrayList<>();
        int colIdx = 0;

        for (TYPE attribute : inputRow) {
            DQAttribute<TYPE> attri;
            // Added TDQ-12057, when pass original & multipass, no need to pass it into OriginalRow.
            if (attribute instanceof List) {
                attri = new DQAttribute<>(SwooshConstants.ORIGINAL_RECORD, colIdx, null);
                hasPassedOriginal = true;
            } else {// ~
                attri = new DQAttribute<>(StringUtils.EMPTY, colIdx, attribute);
            }
            rowList.add(attri);
            colIdx++;
        }
        rcdGen.setOriginalRow(rowList);
        rcdsGenerators.add(rcdGen);
    }

    /**
     * Recording matching with t-swoosh algorithm. Used for matchAnalysis only
     * 
     * @param inputRow
     * @param matchingRule
     */
    public void addToList(final TYPE[] inputRow, List<List<Map<java.lang.String, java.lang.String>>> multiMatchRules,
            SurvivorShipAlgorithmParams parameter) {
        totalCount++;
        String attributeName;
        Map<java.lang.String, ValueGenerator> rcdMap = new LinkedHashMap<>();
        final Map<Integer, SurvivorshipFunction> defaultSurviorshipRules = parameter.getDefaultSurviorshipRules();
        for (List<Map<java.lang.String, java.lang.String>> matchRule : multiMatchRules) {
            for (final Map<java.lang.String, java.lang.String> recordMap : matchRule) {
                attributeName = recordMap.get(IRecordGrouping.ATTRIBUTE_NAME);
                if (attributeName == null) {
                    // Dummy matcher
                    continue;
                }
                rcdMap.put(attributeName, new ValueGenerator() {

                    @Override
                    public int getColumnIndex() {
                        return Integer.valueOf(recordMap.get(IRecordGrouping.COLUMN_IDX));
                    }

                    @Override
                    public java.lang.String newValue() {
                        Integer columnIndex = Integer.valueOf(recordMap.get(IRecordGrouping.COLUMN_IDX));
                        TYPE value = inputRow[columnIndex];
                        if (value != null && value instanceof Date) {
                            return getFormatDate((Date) value, columnIndex);
                        } else {
                            return value == null ? null : value.toString();
                        }
                    }

                    // Added TDQ-12057 : return the current column's values from the last original
                    // values.(multipass+swoosh+passOriginal)
                    // the original is the last one.
                    @Override
                    public Object getAttribute() {
                        TYPE type = inputRow[inputRow.length - 1];
                        if (type instanceof List) {
                            List<Attribute> attris = (List<Attribute>) inputRow[inputRow.length - 1];
                            Integer colIndex = Integer.valueOf(recordMap.get(IRecordGrouping.COLUMN_IDX));
                            for (Attribute att : attris) {
                                if (att.getColumnIndex() == colIndex) {

                                    return att.getValues();
                                }
                            }
                        }
                        return null;
                    }

                    @Override
                    public int getReferenceColumnIndex() {
                        Integer referenceColumnIndex = getColumnIndex();
                        String referenceColumnIndexStr = recordMap.get(IRecordGrouping.REFERENCE_COLUMN_IDX);

                        if (!StringUtils.isEmpty(referenceColumnIndexStr)) {
                            referenceColumnIndex = Integer.valueOf(referenceColumnIndexStr);
                            String matchType = recordMap.get("MATCHING_TYPE"); //$NON-NLS-1$
                            if (matchType != null && "dummy".equalsIgnoreCase(matchType)) { //$NON-NLS-1$
                                Integer inputColumnIndex = Integer.valueOf(recordMap.get(IRecordGrouping.COLUMN_IDX));
                                SurvivorshipFunction survivorshipFunction =
                                        defaultSurviorshipRules.get(inputColumnIndex);
                                if (survivorshipFunction != null
                                        && survivorshipFunction.getReferenceColumnIndex() != null) {
                                    referenceColumnIndex = survivorshipFunction.getReferenceColumnIndex();
                                }
                            }
                        }
                        return referenceColumnIndex;
                    }

                    @Override
                    public String getReferenceValue() {
                        String matchType = recordMap.get("MATCHING_TYPE"); //$NON-NLS-1$
                        Integer referenceColumnIndex = getReferenceColumnIndex();
                        if (matchType != null && "dummy".equalsIgnoreCase(matchType)) { //$NON-NLS-1$
                            Integer inputColumnIndex = Integer.valueOf(recordMap.get(IRecordGrouping.COLUMN_IDX));
                            SurvivorshipFunction survivorshipFunction = defaultSurviorshipRules.get(inputColumnIndex);
                            if (survivorshipFunction != null
                                    && survivorshipFunction.getReferenceColumnIndex() != null) {
                                referenceColumnIndex = survivorshipFunction.getReferenceColumnIndex();
                            }
                        }
                        TYPE value = inputRow[referenceColumnIndex];
                        if (value != null && value instanceof Date) {
                            return getFormatDate((Date) value, referenceColumnIndex);
                        } else {
                            return value == null ? null : value.toString();
                        }
                    }

                });
            }
        }
        RecordGenerator rcdGen = new RecordGenerator();
        rcdGen.setMatchKeyMap(rcdMap);
        List<DQAttribute<?>> rowList = new ArrayList<>();
        int colIdx = 0;

        for (TYPE attribute : inputRow) {
            DQAttribute<TYPE> attri;
            // Added TDQ-12057, when pass original & multipass, no need to pass it into OriginalRow.
            if (attribute instanceof List) {
                attri = new DQAttribute<>(SwooshConstants.ORIGINAL_RECORD, colIdx, null);
                hasPassedOriginal = true;
            } else {// ~
                attri = new DQAttribute<>(StringUtils.EMPTY, colIdx, attribute);
            }
            rowList.add(attri);
            colIdx++;
        }
        rcdGen.setOriginalRow(rowList);
        rcdsGenerators.add(rcdGen);
    }

    private String getFormatDate(Date obj, int index) {
        sdf.applyPattern(recordGrouping.getColumnDatePatternMap().get(index + ""));
        return sdf.format(obj);
    }

    public void swooshMatch(IRecordMatcher combinedRecordMatcher, SurvivorShipAlgorithmParams survParams) {
        swooshMatch(combinedRecordMatcher, survParams,
                new org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBack<>(this.oldGID2New,
                        this.recordGrouping));
    }

    /**
     * Used by tmatchgroup only.
     * 
     * @param combinedRecordMatcher
     * @param survParams
     */
    private void swooshMatch(IRecordMatcher combinedRecordMatcher, SurvivorShipAlgorithmParams survParams,
            org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBack callBack) {
        algorithm = (DQMFB) createTswooshAlgorithm(combinedRecordMatcher, survParams, callBack);

        Iterator<Record> iterator = new DQRecordIterator(totalCount, rcdsGenerators);
        ((DQRecordIterator) iterator).setOriginalColumnCount(this.recordGrouping.getOriginalInputColumnSize());
        while (iterator.hasNext()) {

            algorithm.matchOneRecord(iterator.next());
        }
    }

    public void swooshMatchWithGolden(IRecordMatcher combinedRecordMatcher, SurvivorShipAlgorithmParams survParams) {
        swooshMatchWithGolden(combinedRecordMatcher, survParams,
                new org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBackWithGoldenRecord<>(
                        this.oldGID2New, this.recordGrouping));
    }

    /**
     * Used by tmatchgroup only.
     * 
     * @param combinedRecordMatcher
     * @param survParams
     */
    private void swooshMatchWithGolden(IRecordMatcher combinedRecordMatcher, SurvivorShipAlgorithmParams survParams,
            org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBack callBack) {
        algorithm = (DQMFB) createTswooshAlgorithmWithGolden(combinedRecordMatcher, survParams, callBack);
        algorithm.setHandleGoldenRecord(true);
        Iterator<Record> iterator = new DQGoldenRecordIterator(totalCount, rcdsGenerators);
        ((DQRecordIterator) iterator).setOriginalColumnCount(this.recordGrouping.getOriginalInputColumnSize());
        while (iterator.hasNext()) {

            algorithm.matchOneRecord(iterator.next());
        }
    }

    /**
     * DOC yyin Comment method "createTswooshAlgorithm".
     * 
     * @param combinedRecordMatcher
     * @param survParams
     * @return
     */
    private MatchMergeAlgorithm createTswooshAlgorithm(IRecordMatcher combinedRecordMatcher,
            SurvivorShipAlgorithmParams survParams, MatchMergeAlgorithm.Callback callback) {
        DQMFBRecordMerger recordMerger = createRecordMerger(survParams);
        return new DQMFB(combinedRecordMatcher, recordMerger, callback);
    }

    /**
     * 
     * 
     * @param combinedRecordMatcher
     * @param survParams
     * @return
     */
    private MatchMergeAlgorithm createTswooshAlgorithmWithGolden(IRecordMatcher combinedRecordMatcher,
            SurvivorShipAlgorithmParams survParams, MatchMergeAlgorithm.Callback callback) {
        DQMFBRecordMerger recordMerger = createRecordMerger(survParams);
        return new DQGoldenRecordMFB(combinedRecordMatcher, recordMerger, callback);
    }

    private DQMFBRecordMerger createRecordMerger(SurvivorShipAlgorithmParams survParams) {
        SurvivorShipAlgorithmEnum[] surviorShipAlgos =
                new SurvivorShipAlgorithmEnum[survParams.getSurviorShipAlgos().length];
        String[] funcParams = new String[surviorShipAlgos.length];
        int idx = 0;
        for (SurvivorshipFunction func : survParams.getSurviorShipAlgos()) {
            surviorShipAlgos[idx] = func.getSurvivorShipAlgoEnum();
            funcParams[idx] = func.getParameter();
            idx++;
        }
        DQMFBRecordMerger recordMerger = new DQMFBRecordMerger("MFB", funcParams, surviorShipAlgos, survParams); //$NON-NLS-1$
        recordMerger.setColumnDatePatternMap(this.recordGrouping.getColumnDatePatternMap());
        return recordMerger;
    }

    // init the algorithm before do matching.
    public void initialMFBForOneRecord(IRecordMatcher combinedRecordMatcher, SurvivorShipAlgorithmParams survParams) {
        algorithm = (DQMFB) createTswooshAlgorithm(combinedRecordMatcher, survParams,
                new org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBack<>(this.oldGID2New,
                        this.recordGrouping));
    }

    // do match on one single record,used by analysis
    public void oneRecordMatch(RichRecord printRcd) {
        algorithm.matchOneRecord(printRcd);
    }

    // get and output all result after all records finished
    // used by both analysis and component
    public void afterAllRecordFinished() {
        List<Record> result = algorithm.getResult();
        outputResult(result);
    }

    /**
     * Output the result Result after all finished.
     * 
     * @param result
     */
    private void outputResult(List<Record> result) {
        for (Record rcd : result) {
            RichRecord printRcd = (RichRecord) rcd;
            output(printRcd);
        }
        totalCount = 0;
        rcdsGenerators.clear();
    }

    private void output(RichRecord record) {
        recordGrouping.outputRow(record);
    }

    /**
     * Before match, move the record(not master) out, only use the master to match. (but need to remember the old GID)
     * 
     * After match: if 1(1,2) combined with 3(3,4), and 3 is the new master,-> 3(1,3), and now we should merge the 2,
     * and 4 which didnot attend the second tMatchgroup,--> 3(1,2,3,4), and the group size also need to be changed from
     * 2 to 4.
     * 
     * And: TDQ-12659: 1(1,2) and 3(3,4) should also be removed, because they are intermedia masters.
     * 
     * @param indexGID
     */
    Map<String, List<List<DQAttribute<?>>>> groupRows;

    public void swooshMatchWithMultipass(CombinedRecordMatcher combinedRecordMatcher,
            SurvivorShipAlgorithmParams survivorShipAlgorithmParams, int indexGID2) {
        groupRows = new HashMap<>();
        // key:GID, value: list of rows in this group which are not master.
        List<RecordGenerator> notMasterRecords = new ArrayList<>();
        for (RecordGenerator record : rcdsGenerators) {
            List<DQAttribute<?>> originalRow = record.getOriginalRow();
            if (!StringUtils.equalsIgnoreCase("true", //$NON-NLS-1$
                    StringUtils.normalizeSpace(originalRow.get(indexGID2 + 2).getValue()))) {
                List<List<DQAttribute<?>>> list = groupRows.get(originalRow.get(indexGID2).getValue());
                if (list == null) {
                    list = new ArrayList<>();
                    list.add(originalRow);
                    groupRows.put(originalRow.get(indexGID2).getValue(), list);
                } else {
                    list.add(originalRow);
                }
                notMasterRecords.add(record);
            } else {
                resetMasterData(indexGID2, originalRow);
            }
        }

        // remove the not masters before match
        rcdsGenerators.removeAll(notMasterRecords);
        totalCount = totalCount - notMasterRecords.size();

        // match the masters
        org.talend.dataquality.record.linkage.grouping.callback.MultiPassGroupingCallBack multiPassGroupingCallBack =
                new org.talend.dataquality.record.linkage.grouping.callback.MultiPassGroupingCallBack<>(this.oldGID2New,
                        this.recordGrouping, this.groupRows);
        multiPassGroupingCallBack.setGIDindex(indexGID2);
        swooshMatch(combinedRecordMatcher, survivorShipAlgorithmParams, multiPassGroupingCallBack);

        // add the not masters again
        List<Record> result = algorithm.getResult();
        List<List<DQAttribute<?>>> remainNoMasterLs = new ArrayList<>();
        if (!result.isEmpty()) {
            for (Record master : result) {
                String groupId = StringUtils.isBlank(master.getGroupId()) ? ((RichRecord) master).getGID().getValue()
                        : master.getGroupId();
                List<List<DQAttribute<?>>> list = groupRows.get(groupId);

                restoreMasterData((RichRecord) master);
                addMembersIntoNewMaster(master, list, groupId);
                // remove the record already handled.
                groupRows.remove(groupId);

                // use the new GID to fetch some members of old GID-- which belong to a temp master in first pass, but
                // not a master after 2nd tMatchgroup.
                String tempGid = oldGID2New.get(master.getGroupId());
                if (!StringUtils.equals(groupId, tempGid)) {
                    list = groupRows.get(tempGid);
                    addMembersIntoNewMaster(master, list, groupId);
                    // remove the record already handled.
                    groupRows.remove(tempGid);
                }
            }
        } else {// the current block are all no masters,put them to remainNoMasterLs to output, TDQ-12851
            for (RecordGenerator record : notMasterRecords) {
                remainNoMasterLs.add(record.getOriginalRow());

            }
        }

        // the non-master of 1st tMatchGroup and still in groupRows will be output.
        if (!groupRows.isEmpty()) {
            Iterator<String> keyIterator = groupRows.keySet().iterator();
            while (keyIterator.hasNext()) {
                String keyGid = keyIterator.next();
                remainNoMasterLs.addAll(groupRows.get(keyGid));
            }
        }

        // TDQ-13225 TDQ-13490 output the remained none-master records, also update its GID by the map 'oldGID2New'
        if (!remainNoMasterLs.isEmpty()) {
            for (List<DQAttribute<?>> originalRow : remainNoMasterLs) {
                String gid = oldGID2New.get(originalRow.get(indexGID2).getValue());
                RichRecord createRecord =
                        createRecord(originalRow, gid != null ? gid : originalRow.get(indexGID2).getValue());
                output(createRecord);
            }
        }

    }

    /**
     * zshen after resetMasterData method some data has been changed to not master case here do the restore operation
     * 
     * @param indexGID
     * @param groupSize
     */
    private void restoreMasterData(RichRecord master) {
        DQAttribute<?> isMasterAttribute = master.getMASTER();
        if (master.getMASTER() != null && Double.compare(master.getGroupQuality(), 0.0d) == 0
                && "false".equals(isMasterAttribute.getValue())) { //$NON-NLS-1$
            isMasterAttribute.setValue("true"); //$NON-NLS-1$
            Double valueDQ = Double.valueOf(master.getGRP_QUALITY().getValue());
            master.setGroupQuality(valueDQ);
        }

    }

    /**
     * zshen reset master data make it become not master one
     * 
     * @param indexGID
     * @param originalRow
     */
    private void resetMasterData(int indexGID, List<DQAttribute<?>> originalRow) {
        DQAttribute<?> groupSize = originalRow.get(indexGID + 1);
        groupSize.setValue("0"); //$NON-NLS-1$
        DQAttribute<?> isMaster = originalRow.get(indexGID + 2);
        isMaster.setValue("false"); //$NON-NLS-1$
    }

    /**
     * DOC yyin Comment method "addMembersIntoNewMaster".
     * 
     * @param master
     * @param list
     * @param groupId
     */
    private void addMembersIntoNewMaster(Record master, List<List<DQAttribute<?>>> list, String groupId) {
        if (list == null) {
            return;
        }
        RichRecord record = (RichRecord) master;
        // TDQ-12659 add "-1" for the removed intermediate masters.
        // if(record.isMerged()){
        // record.setGrpSize(record.getGrpSize() + list.size() - 2);
        // }
        if (StringUtils.isBlank(master.getGroupId())) {
            record.setGroupId(groupId);
        }
        for (List<DQAttribute<?>> attri : list) {
            RichRecord createRecord = createRecord(attri, master.getGroupId());
            output(createRecord);
        }
    }

    private RichRecord createRecord(List<DQAttribute<?>> originalRow, String groupID) {
        List<Attribute> rowList = new ArrayList<>();
        for (DQAttribute<?> attr : originalRow) {
            rowList.add(attr);
        }

        RichRecord record = new RichRecord(rowList, originalRow.get(0).getValue(), 0, "MFB"); //$NON-NLS-1$

        record.setGroupId(groupID);
        record.setGrpSize(0);
        record.setMaster(false);
        record.setRecordSize(this.recordGrouping.getOriginalInputColumnSize());
        record.setOriginRow(originalRow);
        return record;
    }

    /**
     * move the records from old GID to new GID. (for multipass)
     * 
     * @param oldGID
     * @param newGID
     */
    private void updateNotMasteredRecords(String oldGID, String newGID) {
        List<List<DQAttribute<?>>> recordsInFirstGroup = groupRows.get(oldGID);
        List<List<DQAttribute<?>>> recordsInNewGroup = groupRows.get(newGID);
        if (recordsInFirstGroup != null) {
            if (recordsInNewGroup == null) {
                groupRows.put(newGID, recordsInFirstGroup);
                // grp-size +1
            } else {
                recordsInNewGroup.addAll(recordsInFirstGroup);
                // grp-size = sum of two list size
            }
            // remove the oldgid's list in: groupRows
            groupRows.remove(oldGID);
        }
    }

    public boolean isHasPassedOriginal() {
        return hasPassedOriginal;
    }

}
