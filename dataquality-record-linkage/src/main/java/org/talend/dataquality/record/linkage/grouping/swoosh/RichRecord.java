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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;

/**
 * A record with original information. (including the columns which is not designated to be match keys) Detailled
 * comment
 * 
 */
public class RichRecord extends Record {

    private static final Logger LOGGER = LoggerFactory.getLogger(RichRecord.class);

    private List<DQAttribute<?>> originRow = null;

    private boolean isMerged = false;

    private boolean isMaster = false;

    private int grpSize = 0;

    // By default for master, score always equals to 1. For the rest of records, it is the score of a record pairs
    // matching score.
    private double score = 0d;

    // The group quality is a indicator to measure matching quality in a group. It takes a value of the minimum matching
    // score among all record pair matching scores. Only the master (merged record) has this value.
    private double groupQuality = 0d;

    // Matching distance details. Only none-master records has this attribute.
    private String labeledAttributeScores = StringUtils.EMPTY;

    private int recordSize = 0;

    private DQAttribute<String> gId;

    private DQAttribute<Integer> gSize;

    private DQAttribute<Boolean> gMaster;

    private DQAttribute<Double> gScore;

    private DQAttribute<String> gQuality;

    private DQAttribute<?> gMergeInfo;

    private DQAttribute<?> gMatchingDistances;

    private DQAttribute<?> gOriginalRecord;

    private DQAttribute<String> gAttributeScore;

    // TDQ-12659 : added for multipass.
    private boolean isGrpSizeNotUpdated = false;

    /**
     * DOC zhao RichRecord constructor .
     * 
     * @param attributes
     * @param id
     * @param timestamp
     * @param source
     */
    public RichRecord(List<Attribute> attributes, String id, long timestamp, String source) {
        super(attributes, id, timestamp, source);
    }

    public RichRecord(String id, long timestamp, String source) {
        super(id, timestamp, source);
    }

    /**
     * Getter for originRow.
     * 
     * @return the originRow
     */
    public List<DQAttribute<?>> getOriginRow() {
        return this.originRow;
    }

    /**
     * Sets the originRow.
     * when it contains the additional columns, move them out.
     * 
     * @param originRow2 the originRow to set
     */
    public void setOriginRow(List<DQAttribute<?>> originRow2) {
        if (originRow2 != null && recordSize > 0 && originRow2.size() > recordSize) {

            this.gId = new DQAttribute<String>(SwooshConstants.GID, recordSize,
                    (String) originRow2.get(recordSize).getOriginalValue());
            Object gsize = originRow2.get(recordSize + 1).getOriginalValue();
            Integer vsize = originRow2.get(recordSize + 1).getOriginalValue() instanceof Integer ? (Integer) gsize
                    : Integer.valueOf((String) gsize);
            this.gSize = new DQAttribute<Integer>(SwooshConstants.GROUP_SIZE, recordSize + 1, vsize);
            this.grpSize = vsize;

            Object value = originRow2.get(recordSize + 2).getOriginalValue();
            Boolean isMasterInFrist = value instanceof Boolean ? (Boolean) value : Boolean.valueOf((String) value);
            this.gMaster = new DQAttribute<Boolean>(SwooshConstants.IS_MASTER, recordSize + 2, isMasterInFrist);

            Object value2 = originRow2.get(recordSize + 3).getOriginalValue();
            Double dvalue = value2 instanceof Double ? (Double) value2 : Double.valueOf((String) value2);
            this.gScore = new DQAttribute<Double>(SwooshConstants.SCORE2, recordSize + 3, dvalue);

            Object value3 = originRow2.get(recordSize + 4).getOriginalValue() == null ? ""
                    : originRow2.get(recordSize + 4).getOriginalValue();
            String gvalue = value3 instanceof Double ? String.valueOf(value3) : (String) value3;
            this.gQuality = new DQAttribute<String>(SwooshConstants.GROUP_QUALITY, recordSize + 4, gvalue);

            // get output details
            if (originRow2.size() > recordSize + 5 && !isMasterInFrist) {// only the not master in the first match will
                                                                             // contains
                                                                         // output details
                String details = originRow2.get(recordSize + 5).getValue();
                this.setLabeledAttributeScores(details);
                gAttributeScore = new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, recordSize + 5,
                        getLabeledAttributeScores());
            }

            if (this.originRow == null) {
                originRow = new ArrayList<DQAttribute<?>>();
            }
            for (int i = 0; i < recordSize; i++) {
                this.originRow.add(originRow2.get(i));
            }

        } else {
            this.originRow = originRow2;
        }
    }

    /**
     * Sets the recordSize.
     * 
     * @param recordSize the recordSize to set
     */
    public void setRecordSize(int recordSize) {
        this.recordSize = recordSize;
    }

    /**
     * Getter for recordSize.
     * 
     * @return the recordSize
     */
    public int getRecordSize() {
        return this.recordSize;
    }

    public void setMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Getter for isMerged.
     * 
     * @return the isMerged
     */
    public boolean isMerged() {
        return this.isMerged;
    }

    /**
     * Sets the isMerged.
     * 
     * @param isMerged the isMerged to set
     */
    public void setMerged(boolean isMerged) {
        this.isMerged = isMerged;
    }

    /**
     * Getter for grpSize.
     * 
     * @return the grpSize
     */
    public int getGrpSize() {
        return this.grpSize;
    }

    /**
     * Sets the grpSize.
     * 
     * @param grpSize the grpSize to set
     */
    public void setGrpSize(int grpSize) {
        this.grpSize = grpSize;
    }

    /**
     * Getter for isMaster.
     * 
     * @return the isMaster
     */
    public boolean isMaster() {
        return this.isMaster;
    }

    /**
     * Getter for score.
     * 
     * @return the score
     */
    public double getScore() {
        return this.score;
    }

    /**
     * Getter for groupQuality.
     * 
     * @return the groupQuality
     */
    public double getGroupQuality() {
        return this.groupQuality;
    }

    /**
     * Sets the groupQuality.
     * 
     * @param groupQuality the groupQuality to set
     */
    public void setGroupQuality(double groupQuality) {
        this.groupQuality = groupQuality;
    }

    /**
     * Getter for labeledAttributeScores.
     * 
     * @return the labeledAttributeScores
     */
    public String getLabeledAttributeScores() {
        return this.labeledAttributeScores;
    }

    /**
     * Sets the labeledAttributeScores.
     * 
     * @param labeledAttributeScores the labeledAttributeScores to set
     */
    public void setLabeledAttributeScores(String labeledAttributeScores) {
        this.labeledAttributeScores = labeledAttributeScores;
    }

    public List<DQAttribute<?>> getOutputRow(Map<String, String> oldGID2New) {
        if (originRow == null) {
            return null;
        }
        if (isMerged()) {
            // Update the matching key field by the merged attributes.
            List<Attribute> matchKeyAttrs = getAttributes();
            for (Attribute attribute : matchKeyAttrs) {
                originRow.get(attribute.getColumnIndex()).setValue(attribute.getValue());
            }
        }
        /**
         * Else The columns that are not maching keys will be merged at {@link DQMFBRecordMerger#createNewRecord()}
         */
        if (isMaster()) {
            if (isMerged) {// Master records
                // Update group id.
                String finalGID = computeGID(oldGID2New);
                if (recordSize == originRow.size()) {
                    // The output is wait until the matching finished. (e.g. chart display in match analysis)
                    addOtherAttributesForFinished(finalGID);
                } else {
                    setOtherAttributeForMerge(finalGID);
                }
            } else {// Unique records
                        // GID
                addRandomGIDAndOthers();
            }

        } else {// Matched records (with records regardless of group quality)
            addOtherAttributeForNotMaster(oldGID2New);
        }
        return originRow;

    }

    /**
     * DOC yyin Comment method "addOtherAttributeForNotMaster".
     * 
     * @param oldGID2New
     */
    private void addOtherAttributeForNotMaster(Map<String, String> oldGID2New) {
        int colIdx = originRow.size() + this.recordSize;
        this.gId = new DQAttribute<>(SwooshConstants.GID, colIdx, computeGID(oldGID2New));
        this.gSize = new DQAttribute<>(SwooshConstants.GROUP_SIZE, colIdx, 0);
        this.gMaster = new DQAttribute<>(SwooshConstants.IS_MASTER, colIdx, false);
        this.gScore = new DQAttribute<>(SwooshConstants.SCORE2, colIdx, getScore());
        this.gQuality = new DQAttribute<>(SwooshConstants.GROUP_QUALITY, colIdx, StringUtils.EMPTY);
        this.gAttributeScore = new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, colIdx, getLabeledAttributeScores());
    }

    /**
     * DOC yyin Comment method "addOtherAttributeForMerge".
     * 
     * @param finalGID
     */
    private void setOtherAttributeForMerge(String finalGID) {
        int colIdx = originRow.size() + this.recordSize;
        this.gId = new DQAttribute<>(SwooshConstants.GID, colIdx, finalGID);
        if (!isGrpSizeNotUpdated) {
            this.gSize = new DQAttribute<>(SwooshConstants.GROUP_SIZE, colIdx, grpSize);
        }
        this.gMaster = new DQAttribute<>(SwooshConstants.IS_MASTER, colIdx, true);
        this.gScore = new DQAttribute<>(SwooshConstants.SCORE2, colIdx, 1.0);
        this.gQuality = new DQAttribute<>(SwooshConstants.GROUP_QUALITY, colIdx, String.valueOf(groupQuality));
        this.gAttributeScore = new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, colIdx, StringUtils.EMPTY);

    }

    /**
     * DOC yyin Comment method "addOtherAttributesForFinished".
     * 
     * @param finalGID
     */
    private void addOtherAttributesForFinished(String finalGID) {
        this.gId = new DQAttribute<>(SwooshConstants.GID, originRow.size(), finalGID);
        if (!isGrpSizeNotUpdated) {
            this.gSize = new DQAttribute<>(SwooshConstants.GROUP_SIZE, originRow.size(), grpSize);
        }
        this.gMaster = new DQAttribute<>(SwooshConstants.IS_MASTER, originRow.size(), true);
        this.gScore = new DQAttribute<>(SwooshConstants.SCORE2, originRow.size(), 1.0);
        this.gQuality =
                new DQAttribute<>(SwooshConstants.GROUP_QUALITY, originRow.size(), String.valueOf(groupQuality));
        this.gAttributeScore = new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, originRow.size(), StringUtils.EMPTY);

    }

    /**
     * DOC yyin Comment method "addRandomGIDAndOthers".
     */
    private void addRandomGIDAndOthers() {
        this.gId = new DQAttribute<>(SwooshConstants.GID, originRow.size(), UUID.randomUUID().toString());
        if (this.grpSize > 0) {
            this.gSize = new DQAttribute<>(SwooshConstants.GROUP_SIZE, originRow.size(), this.grpSize);
        } else {
            this.gSize = new DQAttribute<>(SwooshConstants.GROUP_SIZE, originRow.size(), 1);
        }
        this.gMaster = new DQAttribute<>(SwooshConstants.IS_MASTER, originRow.size(), true);
        this.gScore = new DQAttribute<>(SwooshConstants.SCORE2, originRow.size(), 1.0);
        if (this.groupQuality > 0 && this.groupQuality < 1.0) {
            this.gQuality = new DQAttribute<>(SwooshConstants.GROUP_QUALITY, originRow.size(),
                    String.valueOf(this.groupQuality));
        } else {
            this.gQuality = new DQAttribute<>(SwooshConstants.GROUP_QUALITY, originRow.size(), "1.0");
        }
        this.gAttributeScore = new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, originRow.size(), StringUtils.EMPTY);

    }

    /**
     * DOC zhao Comment method "computeGID".
     * 
     * @param oldGID2New
     * @return
     */
    private String computeGID(Map<String, String> oldGID2New) {
        String groupId = getGroupId();
        String finalGID = oldGID2New.get(groupId) == null ? groupId : oldGID2New.get(groupId);
        return finalGID;
    }

    /**
     * for the merged master rows from multipass, no need to add, only replace. Add one fixed output column: MERGE_INFO
     * when there is original record from the 1st tmatchgroup, the EXT_SIZE should =7, if with details, should = 8.
     * 
     * @param oldGID2New
     * @param originalInputColumnSize
     * @return
     */
    public List<DQAttribute<?>> getOutputRow(Map<String, String> oldGID2New, boolean isMultipass) {
        if (!isMultipass) {
            return this.getOutputRow(oldGID2New);
        } else {

            return getoutputRowForMultipass(oldGID2New);
        }
    }

    private List<DQAttribute<?>> getoutputRowForMultipass(Map<String, String> oldGID2New) {
        if (isMerged()) {
            // Update the matching key field by the merged attributes.
            List<Attribute> matchKeyAttrs = getAttributes();
            for (Attribute attribute : matchKeyAttrs) {
                if (originRow.size() > attribute.getColumnIndex()) {// sometime attrubute maybe contain fixed column so
                                                                        // that do
                                                                    // this judge
                    originRow.get(attribute.getColumnIndex()).setValue(attribute.getValue());
                }
            }
        }
        if (isMerged || isMaster) {// Master records
            // Update group id.
            String finalGID = this.getGroupId();
            // The GID of master record should use recent GID too
            finalGID = computeGID(oldGID2New);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("#1 after compute oldID:" + this.getGroupId() + " finalGID:" + finalGID);
            }
            if (StringUtils.isBlank(finalGID)) {
                finalGID = UUID.randomUUID().toString();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("#2 after randomUUID oldID:" + this.getGroupId() + " finalGID:" + finalGID);
                }
            }
            if (getGrpSize() == 0) {
                setGrpSize(1);
            }
            setGroupId(finalGID);
            if (recordSize == originRow.size()) {
                this.gId = new DQAttribute<>(SwooshConstants.GID, originRow.size(), finalGID);
                if (!isGrpSizeNotUpdated) {
                    this.gSize = new DQAttribute<>(SwooshConstants.GROUP_SIZE, originRow.size(), getGrpSize());
                }
                this.gMaster = new DQAttribute<>(SwooshConstants.IS_MASTER, originRow.size(), true);
                this.gScore = new DQAttribute<>(SwooshConstants.SCORE2, originRow.size(), 1.0);

                // use the lowest value for group quality
                String finalQuality = "1.0";
                if (this.gQuality != null && gQuality.getValue() != null) {
                    if (Double.compare(this.groupQuality, 0.0) > 0) {
                        finalQuality = Double.parseDouble(gQuality.getValue()) > groupQuality
                                ? String.valueOf(groupQuality) : gQuality.getValue();
                    } else {
                        finalQuality = gQuality.getValue();
                    }
                } else if (Double.compare(this.groupQuality, 0.0) > 0) {
                    finalQuality = String.valueOf(groupQuality);
                }

                this.gQuality = new DQAttribute<>(SwooshConstants.GROUP_QUALITY, originRow.size(), finalQuality);

                this.gAttributeScore =
                        new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, originRow.size(), StringUtils.EMPTY);

            }
        } else {// for not master
            String finalGID = computeGID(oldGID2New);
            setGroupId(finalGID);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("#3 after compute oldID:" + this.getGroupId() + " finalGID:" + finalGID);
            }
            if (recordSize == originRow.size()) {
                if (gId == null) {
                    addOtherAttributeForNotMaster(oldGID2New);
                }
                gId.setValue(finalGID);
                gSize.setValue("0");
                gMaster.setValue(String.valueOf(false));
                if (Double.compare(score, 0.0) > 0) {
                    gScore.setValue(String.valueOf(score));
                }
                // for not master, grp quality=0
                gQuality.setValue("0.0");
                if (gAttributeScore != null) {

                    gAttributeScore.setValue(getLabeledAttributeScores());
                } else {
                    this.gAttributeScore = new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, originRow.size(),
                            getLabeledAttributeScores());
                }

            }
        }
        return originRow;
    }

    /**
     * TDQ-12659 : when it is a master and its group size >0 on the last result, means that the current record is an
     * intermediate
     * master record
     * 
     * @return
     */
    public boolean isInterMediateMaster() {
        if (this.gMaster == null || this.gSize == null) {
            return false;
        }
        if (this.isMaster) {
            return false;
        }
        return this.gMaster.getOriginalValue() && (this.gSize.getOriginalValue() > 1);
    }

    public DQAttribute<String> getGID() {
        return gId;
    }

    public DQAttribute<Integer> getGRP_SIZE() {
        return gSize;
    }

    public DQAttribute<Boolean> getMASTER() {
        return gMaster;
    }

    public DQAttribute<Double> getSCORE() {
        return gScore;
    }

    public DQAttribute<String> getGRP_QUALITY() {
        return gQuality;
    }

    public void setGRP_QUALITY(DQAttribute<String> gRP_QUALITY) {
        gQuality = gRP_QUALITY;
    }

    public DQAttribute<?> getMERGE_INFO() {
        return gMergeInfo;
    }

    public DQAttribute<?> getMATCHING_DISTANCES() {
        return gMatchingDistances;
    }

    public DQAttribute<?> getORIGINAL_RECORD() {
        return gOriginalRecord;
    }

    public DQAttribute<String> getATTRIBUTE_SCORE() {
        return gAttributeScore;
    }

    public void setGRP_SIZE(int newGRP_SIZE) {
        if (gSize == null) {
            this.gSize = new DQAttribute<Integer>(SwooshConstants.GROUP_SIZE, recordSize + 1, newGRP_SIZE);
        } else {
            gSize.setValue(String.valueOf(newGRP_SIZE));
        }
        isGrpSizeNotUpdated = true;
    }

    @Override
    public RichRecord clone() {
        RichRecord newRichRecord = new RichRecord(this.getId(), this.getTimestamp(), this.getSource());
        newRichRecord.getAttributes().addAll(this.getAttributes());
        newRichRecord.getForbiddenList().addAll(this.getForbiddenList());
        newRichRecord.getRelatedIds().addAll(this.getRelatedIds());
        newRichRecord.setOriginRow(this.getOriginRow());
        newRichRecord.isMaster = this.isMaster;
        newRichRecord.isMerged = this.isMerged;
        newRichRecord.grpSize = this.grpSize;
        newRichRecord.score = this.score;
        newRichRecord.groupQuality = this.groupQuality;
        newRichRecord.labeledAttributeScores = this.labeledAttributeScores;
        newRichRecord.recordSize = this.recordSize;
        if (newRichRecord.getRelatedIds().size() > 1) {
            newRichRecord.isClone = true;
        }
        return newRichRecord;
    }

}
