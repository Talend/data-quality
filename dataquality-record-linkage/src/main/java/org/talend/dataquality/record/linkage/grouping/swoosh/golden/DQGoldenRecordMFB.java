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
package org.talend.dataquality.record.linkage.grouping.swoosh.golden;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.MatchResult;
import org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBackWithGoldenRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQMFB;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMerger;

public class DQGoldenRecordMFB extends DQMFB {

    /**
     * @param matcher
     * @param merger
     * @param callback
     */
    public DQGoldenRecordMFB(IRecordMatcher matcher, IRecordMerger merger, Callback callback) {
        super(matcher, merger, callback);
    }

    @Override
    protected boolean startDelayMatch(Record record, List<Record> mergedRecords, Queue<Record> queue, Callback callback,
            boolean hasCreatedNewMerge) {
        if (isHandleGoldenRecord() && !hasCreatedNewMerge && record.isGoldenRecord()) {
            // check new record is golden or not if yes do match with goldenRecordMapUnExist
            List<Record> needMatchList = goldenRecordMapUnExist.get(record.getId());
            if (needMatchList != null) {
                for (Record needMatchRecord : needMatchList) {
                    MatchResult matchResult = doMatch(needMatchRecord, record);
                    if (matchResult.isMatch()) {
                        // need get a map list from the matchResult if one one of record is golden record and they can
                        // match

                        // clone and match keep groupID is null
                        Record cloneRecord = needMatchRecord.clone();
                        executeSubMatchForGoldenRecord(cloneRecord, mergedRecords, queue, callback, record,
                                matchResult);
                        // reduce repeat match
                        if (record.getRelatedIds().contains(cloneRecord.getId())) {
                            continue;
                        }
                        matchTwoRecord(cloneRecord, mergedRecords, queue, callback, record, matchResult);
                        if (callback instanceof GroupingCallBackWithGoldenRecord) {
                            ((GroupingCallBackWithGoldenRecord<?>) callback).outputRelatedRow(cloneRecord);
                        }
                        hasCreatedNewMerge = true;
                        break;
                    } else {
                        callback.onDifferent(needMatchRecord, record, matchResult);
                    }
                }
            }
        }
        return hasCreatedNewMerge;
    }

    @Override
    protected boolean checkRecordValidation(Record record) {
        if (!isHandleGoldenRecord()) {
            return true;
        }
        Record theGoldenRecord = goldenRecordMapExist.get(record.getId());
        if (theGoldenRecord == null) {
            return true;
        } else {
            Set<String> newInputRelatedIds = record.getRelatedIds();
            Set<String> alreadyExistRelatedIds = theGoldenRecord.getRelatedIds();
            if (theGoldenRecord != record && alreadyExistRelatedIds.containsAll(newInputRelatedIds)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void executeSubMatchForGoldenRecord(Record record, List<Record> mergedRecords, Queue<Record> queue,
            Callback callback, Record mergedRecord, MatchResult matchResult) {
        if (!isHandleGoldenRecord()) {
            return;
        }
        Map<Record, List<String>> impactMatchMap = matchResult.getImpactMatchMap();
        List<String> matchIDs = new ArrayList<>();
        for (Record newRecord : impactMatchMap.keySet()) {
            List<String> needMatchGoldenRecordIDList = impactMatchMap.get(newRecord);
            for (String goldenRecordID : needMatchGoldenRecordIDList) {
                Record goldenRecord = goldenRecordMapExist.get(goldenRecordID);
                if (newRecord.getRelatedIds().contains(goldenRecordID)) {
                    continue;
                }

                if (goldenRecord == null) {
                    add2UnExistMap(newRecord, goldenRecordID);
                } else {
                    if (isGoldenRecordCase(newRecord, goldenRecord)) {
                        continue;
                    }
                    // reduce repeat match
                    if (goldenRecord.getRelatedIds().contains(newRecord.getId())) {
                        continue;
                    }
                    boolean skipThisLoop = false;
                    for (String goldenRelID : goldenRecord.getRelatedIds()) {
                        if (matchIDs.contains(goldenRelID)) {
                            skipThisLoop = true;
                            continue;
                        }
                    }
                    if (skipThisLoop) {
                        continue;
                    }
                    // clone and match keep groupID is null
                    newRecord = newRecord.clone();
                    matchTwoRecord(newRecord, mergedRecords, queue, callback, goldenRecord, matchResult);
                    if (callback instanceof GroupingCallBackWithGoldenRecord) {
                        ((GroupingCallBackWithGoldenRecord<?>) callback).outputRelatedRow(newRecord);
                    }
                    matchIDs.add(goldenRecordID);
                }
            }
        }
    }

    @Override
    protected void synGoldenRecordMap(Record newMergedRecord, Record record, Record mergedRecord) {
        if (!isHandleGoldenRecord() || !newMergedRecord.isGoldenRecord()) {
            return;
        }
        String newMergedRecordID = newMergedRecord.getId();
        String firstRecordID = record.getId();
        String secondRecordID = mergedRecord.getId();

        goldenRecordMapExist.put(newMergedRecord.getId(), newMergedRecord);

        if (newMergedRecordID.equals(firstRecordID)) {
            doSyn(newMergedRecord, secondRecordID);
        } else {
            doSyn(newMergedRecord, firstRecordID);
        }
    }

    @Override
    protected MatchResult maskImpactMatchMap(Record leftRecord, Record rightRecord, MatchResult matchResult) {
        if (!isHandleGoldenRecord()) {
            return matchResult;
        } else {
            boolean match = matchResult.isMatch();
            if (match) {
                if (isGoldenRecordCase(leftRecord, rightRecord)) {
                    matchResult = NonMatchResult.wrap(matcher.getMatchingWeight(leftRecord, rightRecord));
                } else {
                    if (leftRecord.isGoldenRecord()) {
                        List<String> leftForbiddenList = leftRecord.getForbiddenList();
                        matchResult.getImpactMatchMap().put(rightRecord, leftForbiddenList);
                    }
                    if (rightRecord.isGoldenRecord()) {
                        List<String> rightForbiddenList = rightRecord.getForbiddenList();
                        matchResult.getImpactMatchMap().put(leftRecord, rightForbiddenList);
                    }
                }
            }
            // else case will do nothing here
            updateGoldenMap(leftRecord, rightRecord, match);
        }
        return matchResult;
    }

    private void add2UnExistMap(Record leftRecord, String forbiddenID) {
        List<Record> needToMatchRecordlist = goldenRecordMapUnExist.get(forbiddenID);
        if (needToMatchRecordlist == null) {
            needToMatchRecordlist = new ArrayList<>();
            goldenRecordMapUnExist.put(forbiddenID, needToMatchRecordlist);
        }
        if (!needToMatchRecordlist.contains(leftRecord)) {
            needToMatchRecordlist.add(leftRecord);
        }
    }

    private void doSyn(Record newMergedRecord, String secondRecordID) {
        Record originalGoldenRecord = goldenRecordMapExist.get(secondRecordID);
        if (originalGoldenRecord != null) {
            goldenRecordMapExist.put(secondRecordID, newMergedRecord);
        }
        List<Record> needMoveRecodList = goldenRecordMapUnExist.get(secondRecordID);
        List<Record> newGoldenlist = goldenRecordMapUnExist.get(newMergedRecord.getId());
        if (newGoldenlist == null) {
            return;
        }
        if (needMoveRecodList != null) {
            // another record is golden record too
            for (Record moveRecord : needMoveRecodList) {
                boolean isFind = false;
                for (Record newGoldeRecord : newGoldenlist) {
                    if (newGoldeRecord.getId().equals(moveRecord.getId())) {
                        isFind = true;
                        break;
                    }
                }
                if (isFind) {
                    newGoldenlist.add(moveRecord);
                }
            }
            goldenRecordMapUnExist.remove(secondRecordID);
            goldenRecordMapExist.put(secondRecordID, newMergedRecord);
        }
        int removeIndex = -1;
        for (int index = 0; index < newGoldenlist.size(); index++) {
            if (newGoldenlist.get(index).getId().equals(secondRecordID)) {
                removeIndex = index;
                break;
            }
        }
        if (removeIndex >= 0) {
            newGoldenlist.remove(removeIndex);
        }
    }

    /**
     * case1 both record is golden record so that update directly(both condition1 is true)
     * case2 or case3 (condition2 is true)
     * step1: update the only one golden record
     * step2: update goldenRecordMapUnExist(step2)
     * If don't match we need to update goldenRecordMapExist too
     * 
     */
    private void updateGoldenMap(Record leftRecord, Record rightRecord, boolean isMatch) {
        doUpdate(leftRecord, rightRecord, isMatch);
        doUpdate(rightRecord, leftRecord, isMatch);
    }

    private void doUpdate(Record leftRecord, Record rightRecord, boolean isMatch) {
        if (leftRecord.isGoldenRecord()) {
            // condition1
            goldenRecordMapExist.put(leftRecord.getId(), leftRecord);
        } else if (rightRecord.isGoldenRecord() && isMatch) {
            // condition2 left false right true
            // step1
            goldenRecordMapExist.put(rightRecord.getId(), rightRecord);
            // step2
            for (String forbiddenID : rightRecord.getForbiddenList()) {
                add2UnExistMap(leftRecord, forbiddenID);
            }
        }
    }

}
