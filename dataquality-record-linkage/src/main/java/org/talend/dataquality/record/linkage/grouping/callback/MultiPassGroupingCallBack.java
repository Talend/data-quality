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
package org.talend.dataquality.record.linkage.grouping.callback;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.MatchResult;
import org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SwooshConstants;
import org.talend.dataquality.record.linkage.utils.BidiMultiMap;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class MultiPassGroupingCallBack<type> extends GroupingCallBack<type> {

    private Map<String, List<List<DQAttribute<?>>>> groupRows = null;

    /**
     * DOC zshen MultiPassGroupingCallBack constructor comment.
     * 
     * @param oldGID2New
     * @param recordGrouping
     */
    public MultiPassGroupingCallBack(BidiMultiMap oldGID2New, AbstractRecordGrouping<type> recordGrouping,
            Map<String, List<List<DQAttribute<?>>>> groupRows) {
        super(oldGID2New, recordGrouping);
        this.groupRows = groupRows;

    }

    int indexGID = 0;

    @Override
    public void onDifferent(Record record1, Record record2, MatchResult matchResult) {
        // TODO Auto-generated method stub
        super.onDifferent(record1, record2, matchResult);
    }

    @Override
    public void onEndRecord(Record record) {
        // TODO Auto-generated method stub
        super.onEndRecord(record);
    }

    public void setGIDindex(int index) {
        indexGID = index;
    }

    /**
     * Getter for index of group id.
     * 
     * @return the index of group id
     */
    public int getIndexGID() {
        return this.indexGID;
    }

    /**
     * Getter for index of group quality.
     * 
     * @return the index of group quality
     */
    public int getIndexGQ() {
        return this.indexGID + 4;
    }

    @Override
    public void onMatch(Record record1, Record record2, MatchResult matchResult) {
        if (!matchResult.isMatch()) {
            return;
        }
        // record1 and record2 must be RichRecord from DQ grouping implementation.
        RichRecord richRecord1 = (RichRecord) record1;
        RichRecord richRecord2 = (RichRecord) record2;

        richRecord1.setConfidence(richRecord1.getScore());
        richRecord2.setConfidence(richRecord2.getScore());

        String grpId1 = richRecord1.getGroupId();
        String grpId2 = richRecord2.getGroupId();
        String oldgrpId1 = richRecord1.getGID() == null ? null : richRecord1.getGID().getValue(); // .getOriginRow().get(getIndexGID()).getValue();
        String oldgrpId2 = richRecord2.getGID() == null ? null : richRecord2.getGID().getValue();// .getOriginRow().get(getIndexGID()).getValue();
        uniqueOldGroupQuality(record1, record2);
        if (grpId1 == null && grpId2 == null) {
            // Both records are original records.
            richRecord1.setGroupId(oldgrpId1);
            richRecord2.setGroupId(oldgrpId1);
            // group size is 0 for none-master record
            richRecord1.setGrpSize(0);
            richRecord2.setGrpSize(0);

            richRecord1.setMaster(false);
            richRecord2.setMaster(false);
            // Put into the map: <gid2,gid1>, oldgrpId2 is not used any more, but can be found by oldgrpId1 which is
            // used
            oldGID2New.put(oldgrpId2, oldgrpId1);
            updateNotMasteredRecords(oldgrpId2, oldgrpId1);
            output(richRecord1);
            output(richRecord2);

        } else if (grpId1 != null && grpId2 != null) {
            // Both records are merged records.
            richRecord2.setGroupId(grpId1);
            updateNotMasteredRecords(grpId2, grpId1);
            // Put into the map: <gid2,gid1>
            oldGID2New.put(grpId1, grpId2);
            // Update map where value equals to gid2
            List<String> keysOfGID2 = oldGID2New.getKeys(grpId2);
            if (keysOfGID2 != null) {
                for (String key : keysOfGID2) {
                    oldGID2New.put(key, grpId1);
                }
            }

        } else if (grpId1 == null) {
            // richRecord1 is original record
            // GID is the gid of record 2.
            richRecord1.setGroupId(grpId2);
            // Put into the map: <gid2,gid1>
            oldGID2New.put(grpId2, oldgrpId1);
            updateNotMasteredRecords(oldgrpId1, grpId2);
            // group size is 0 for none-master record
            richRecord1.setGrpSize(0);
            richRecord1.setMaster(false);

            output(richRecord1);

        } else {
            // richRecord2 is original record.
            // GID
            richRecord2.setGroupId(richRecord1.getGroupId());
            updateNotMasteredRecords(oldgrpId2, richRecord1.getGroupId());
            oldGID2New.put(grpId2, oldgrpId1);
            // group size is 0 for none-master record
            richRecord2.setGrpSize(0);
            richRecord2.setMaster(false);

            output(richRecord2);
        }

    }

    /**
     * zshen Comment method "uniqueOldGroupQuality". unique group quality with min
     * 
     * @param record1
     * @param record2
     */
    private void uniqueOldGroupQuality(Record record1, Record record2) {
        RichRecord richRecord1 = (RichRecord) record1;
        RichRecord richRecord2 = (RichRecord) record2;
        Double oldGrpQualiry1 = getOldGrpQualiry(richRecord1);
        Double oldGrpQualiry2 = getOldGrpQualiry(richRecord2);
        if (oldGrpQualiry1 < oldGrpQualiry2) {
            setOldGrpQualiry(richRecord2, oldGrpQualiry1);
        } else if (oldGrpQualiry1 > oldGrpQualiry2) {
            setOldGrpQualiry(richRecord2, oldGrpQualiry2);
        }
        // oldGrpQualiry1 < oldGrpQualiry2 case we don't need do anything
    }

    /**
     * DOC zshen Comment method "setOldGrpQualiry".
     * 
     * @param double1
     */
    private void setOldGrpQualiry(RichRecord richRecord, Double value) {
        if (richRecord.getGRP_QUALITY() == null) {
            richRecord.setGRP_QUALITY(
                    new DQAttribute<>(SwooshConstants.GROUP_QUALITY, richRecord.getRecordSize(), StringUtils.EMPTY));
        } else {
            richRecord.getGRP_QUALITY().setValue(String.valueOf(value));
        }
        // richRecord.getOriginRow().get(getIndexGQ()).setValue(String.valueOf(value));

    }

    @Override
    public void onRemoveMerge(Record record) {
        // record must be RichRecord from DQ grouping implementation.
        RichRecord richRecord = (RichRecord) record;
        if (richRecord.isMerged()) {
            // removeOldValues(richRecord);
            richRecord.setGroupQuality(0);
        }
        richRecord.setMerged(false);
        richRecord.setMaster(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.grouping.TSwooshGrouping.GroupingCallBack#onNewMerge(org.talend.dataquality
     * .matchmerge.Record)
     */
    @Override
    public void onNewMerge(Record record) {
        // record must be RichRecord from DQ grouping implementation.
        RichRecord richRecord = (RichRecord) record;
        richRecord.setMaster(true);
        richRecord.setScore(1.0);
        if (record.getGroupId() != null) {
            richRecord.setMerged(true);
            richRecord.setGrpSize(richRecord.getRelatedIds().size());
            if (Double.compare(richRecord.getGroupQuality(), 0.0d) == 0) {
                // group quality will be the confidence (score) or old group quality decide that by who is minimum.
                Double oldGrpQuality = getOldGrpQualiry(richRecord);
                richRecord.setGroupQuality(getMergeGQ(oldGrpQuality, record.getConfidence()));
            }
        }
    }

    /**
     * DOC zshen Comment method "getOldGrpQualiry".
     * 
     * @param richRecord
     * @return
     */
    private Double getOldGrpQualiry(RichRecord richRecord) {
        // String value = richRecord.getOriginRow().get(getIndexGQ()).getValue();
        String value = richRecord.getGRP_QUALITY() == null ? null : richRecord.getGRP_QUALITY().getValue();
        return Double.valueOf(value == null ? "1.0" : value);
    }

    /**
     * DOC zshen Comment method "getMergeGQ".
     * 
     * @param oldGrpQuality
     * @param confidence
     * @return minimum one
     */
    private double getMergeGQ(Double oldGrpQuality, double confidence) {
        if (oldGrpQuality.compareTo(0.0d) == 0) {
            return confidence;
        }
        // get minimum one
        return confidence > oldGrpQuality ? oldGrpQuality : confidence;
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

}
