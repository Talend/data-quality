package org.talend.dataquality.record.linkage.grouping.callback;

import java.util.List;
import java.util.UUID;

import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.MatchResult;
import org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.utils.BidiMultiMap;

/**
 * 
 * Create by zshen This class is used to finish all kinds of action(match merge remove and so on) in match processing
 */
public class GroupingCallBack<T> implements MatchMergeAlgorithm.Callback {

    protected BidiMultiMap<String, String> oldGID2New = null;

    protected AbstractRecordGrouping<T> recordGrouping;

    public GroupingCallBack(BidiMultiMap<String, String> oldGID2New, AbstractRecordGrouping<T> recordGrouping) {
        this.oldGID2New = oldGID2New;
        this.recordGrouping = recordGrouping;
    }

    @Override
    public void onBeginRecord(Record record) {
        // Nothing todo
    }

    @Override
    public void onMatch(Record record1, Record record2, MatchResult matchResult) {

        // record1 and record2 must be RichRecord from DQ grouping implementation.
        RichRecord richRecord1 = (RichRecord) record1;
        RichRecord richRecord2 = (RichRecord) record2;

        richRecord1.setConfidence(richRecord1.getScore());
        richRecord2.setConfidence(richRecord2.getScore());

        String grpId1 = richRecord1.getGroupId();
        String grpId2 = richRecord2.getGroupId();
        if (grpId1 == null && grpId2 == null) {
            // Both records are original records.
            String gid = UUID.randomUUID().toString(); // Generate a new GID.
            richRecord1.setGroupId(gid);
            richRecord2.setGroupId(gid);
            // group size is 0 for none-master record
            richRecord1.setGrpSize(0);
            richRecord2.setGrpSize(0);

            richRecord1.setMaster(false);
            richRecord2.setMaster(false);

            output(richRecord1);
            output(richRecord2);

        } else if (grpId1 != null && grpId2 != null) {
            // Both records are merged records.
            richRecord2.setGroupId(grpId1);
            // Put into the map: <gid2,gid1>
            oldGID2New.put(grpId2, grpId1);
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
            richRecord1.setGroupId(richRecord2.getGroupId());
            // group size is 0 for none-master record
            richRecord1.setGrpSize(0);
            richRecord1.setMaster(false);

            output(richRecord1);

        } else {
            // richRecord2 is original record.
            // GID
            richRecord2.setGroupId(richRecord1.getGroupId());
            // group size is 0 for none-master record
            richRecord2.setGrpSize(0);
            richRecord2.setMaster(false);

            output(richRecord2);
        }
    }

    @Override
    public void onNewMerge(Record record) {
        // record must be RichRecord from DQ grouping implementation.
        RichRecord richRecord = (RichRecord) record;
        richRecord.setMaster(true);
        richRecord.setScore(1.0);
        if (record.getGroupId() != null) {
            richRecord.setMerged(true);
            richRecord.setGrpSize(richRecord.getRelatedIds().size());
            if (Double.compare(richRecord.getGroupQuality(), 0.0d) == 0
                    || Double.compare(richRecord.getGroupQuality(), richRecord.getConfidence()) > 0) {
                // group quality will be the confidence (score) .
                richRecord.setGroupQuality(record.getConfidence());
            }
        }
    }

    @Override
    public void onRemoveMerge(Record record) {
        // record must be RichRecord from DQ grouping implementation.
        RichRecord richRecord = (RichRecord) record;
        if (richRecord.isMerged()) {
            richRecord.setOriginRow(null); // set null original row, won't be usefull anymore after another merge.
            richRecord.setGroupQuality(0);
        }
        richRecord.setMerged(false);
        richRecord.setMaster(false);
    }

    @Override
    public void onDifferent(Record record1, Record record2, MatchResult matchResult) {
        RichRecord currentRecord = (RichRecord) record2;
        currentRecord.setMaster(true);
        // The rest of group properties will be set in RichRecord$getOutputRow()
    }

    @Override
    public void onEndRecord(Record record) {
        // Nothing todo
    }

    @Override
    public boolean isInterrupted() {
        // Nothing todo
        return false;
    }

    @Override
    public void onBeginProcessing() {
        // Nothing todo
    }

    @Override
    public void onEndProcessing() {
        // Nothing todo
    }

    protected void output(RichRecord record) {
        recordGrouping.outputRichRecord(record);
    }

    @Override
    public void onSynResult(Record newRecord, Record originalRecord, MatchResult matchResult) {
        newRecord.setConfidence(matchResult.getFinalWorstConfidenceValue());
        newRecord.setWorstConfidenceValueScoreList(matchResult.getWorstConfidenceValueScoreList());
    }

}