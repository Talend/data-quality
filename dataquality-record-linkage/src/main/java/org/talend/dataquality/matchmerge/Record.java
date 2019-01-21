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
package org.talend.dataquality.matchmerge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A record represents input data for the match & merge. It provides the following:
 * <ul>
 * <li>An {@link #getId() id} (a value that uniquely identifies the record in the system it was created from: it could
 * be a database PK for example).</li>
 * <li>A list of {@link #getRelatedIds() related} ids: a list of ids in the source system this record represents.</li>
 * <li>A group {@link #getGroupId() id}: a optional group id (something that identifies previously grouped records in
 * the source system).</li>
 * <li>An optional {@link #getSource() source}: this can be used during merge (in case a source is dimmed more trustful
 * than another).</li>
 * <li>An optional {@link #getTimestamp() timestamp}: this can be used during merge (to select the least or most
 * recently modified record).</li>
 * </ul>
 */
public class Record {

    private static final double MAX_CONFIDENCE = 1.0;

    private final List<Attribute> attributes;

    private final String id;

    private final long timestamp;

    private final String source;

    private String groupId;

    private Set<String> relatedIds = new HashSet<String>();

    private List<Double> worstConfidenceValueScoreList = new ArrayList<Double>();

    private double confidence = MAX_CONFIDENCE;

    /**
     * Creates a empty record (no {@link org.talend.dataquality.matchmerge.Attribute attributes}).
     * 
     * @param id Id of the record in the source system.
     * @param timestamp Last modification time (in milliseconds).
     * @param source A source name to indicate where the values come from.
     */
    public Record(String id, long timestamp, String source) {
        this.id = id;
        this.timestamp = timestamp;
        this.source = source;
        this.attributes = new ArrayList<Attribute>();
    }

    /**
     * Creates a record with {@link org.talend.dataquality.matchmerge.Attribute attributes}.
     * 
     * @param attributes Attributes for the new record.
     * @param id Id of the record in the source system.
     * @param timestamp Last modification time (in milliseconds).
     * @param source A source name to indicate where the values come from.
     */
    public Record(List<Attribute> attributes, String id, long timestamp, String source) {
        this.attributes = attributes;
        this.id = id;
        this.timestamp = timestamp;
        this.source = source;
    }

    /**
     * @return <p>
     * A group id that identifies the group this record belongs to. Match & merge algorithm considers records with a
     * group id as "already processed" and if 2 records match <b>but</b> group id differs, match is considered as
     * incorrect: this scenario happens when 2 records matches but were split into different groups by the user (in this
     * case, match shouldn't bring into same group the 2 similar records).
     * </p>
     * <p>
     * Returns <code>null</code> if no group is set.
     * </p>
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId The new group id for this record.
     * @see #getGroupId()
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return The id as it is in the source system. It is <b>always</b> a string (i.e. not dependent on the actual id
     * type).
     */
    public String getId() {
        return id;
    }

    /**
     * @return The {@link org.talend.dataquality.matchmerge.Attribute attributes} for this record.
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * @return A set of ids this record was built with.
     * @see #setRelatedIds(java.util.Set)
     */
    public Set<String> getRelatedIds() {
        return relatedIds;
    }

    /**
     * @param relatedIds A set of ids this record is linked to.
     * @see #getRelatedIds()
     */
    public void setRelatedIds(Set<String> relatedIds) {
        this.relatedIds = relatedIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Record record = (Record) o;
        return !(id != null ? !id.equals(record.id) : record.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String relatedId : relatedIds) {
            builder.append(relatedId).append(' ');
        }
        return id + " ( " + builder + ")";
    }

    /**
     * @return The "confidence" of the record. Confidence is always a double between 0 and 1. 1 indicates a high
     * confidence (a certain match) and 0 indicates a very unreliable match.
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * @param confidence The new confidence for this record. Confidence is always a double between 0 and 1. 1 indicates
     * a high confidence (a certain match) and 0 indicates a very unreliable match. This method may not <b>always</b>
     * change the confidence value, you can only lower the confidence, never improve it. Method only change confidence
     * if <code>confidence</code> is lower than current.
     * 
     * @throws java.lang.IllegalArgumentException If confidence > {@link #MAX_CONFIDENCE}.
     */
    public void setConfidence(double confidence) {
        if (confidence > MAX_CONFIDENCE) {
            throw new IllegalArgumentException("Confidence value '" + confidence + "' is incorrect (>" + MAX_CONFIDENCE + ".");
        }
        // TMDM-7833: A record can never gain confidence (especially in case of multiple merges, it's not because this
        // record perfectly match another that it becomes a sure match).
        this.confidence = Math.min(confidence, this.confidence);
    }

    /**
     * @return The last modification time for this record.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return The source associated with this record.
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the worstConfidenceValueScoreList.
     * 
     * @param worstConfidenceValueScoreList the worstConfidenceValueScoreList to set
     */
    public void setWorstConfidenceValueScoreList(List<Double> worstConfidenceValueScoreList) {
        this.worstConfidenceValueScoreList = worstConfidenceValueScoreList;
    }

    /**
     * Getter for worstConfidenceValueScoreList.
     * 
     * @return the worstConfidenceValueScoreList
     */
    public List<Double> getWorstConfidenceValueScoreList() {
        return this.worstConfidenceValueScoreList;
    }

}
