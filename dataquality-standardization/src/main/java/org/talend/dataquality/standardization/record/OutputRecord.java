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
package org.talend.dataquality.standardization.record;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author scorreia SynonymRecordSearcher: A record (= set of fields) that has been found after a search in several
 * indexes.
 */
public class OutputRecord implements Comparable<OutputRecord> {

    /**
     * The output record (words that have matched).
     */
    String[] record;

    /**
     * the score of the matched record.
     */
    float score = 0;

    /**
     * the score details of each index, combine by "|"
     */
    String scores;

    /**
     * The number of matches (max is the size of the record).
     */
    int nbMatch = 0;

    /**
     * OutputRecord constructor..
     * 
     * @param size the size of the record
     */
    public OutputRecord(int size) {
        this.record = new String[size];
        this.scores = ""; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (String element : record) {
            buf.append(element).append(" ; ");//$NON-NLS-1$
        }
        buf.deleteCharAt(buf.length() - 2);
        buf.append(" | score= " + score);//$NON-NLS-1$
        buf.append(" ->" + scores).append("| nb match=").append(nbMatch).append("/").append(record.length);//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        return buf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        HashCodeBuilder b = new HashCodeBuilder();
        b.append(record);
        return b.toHashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof OutputRecord)) {
            return false;
        }
        OutputRecord other = (OutputRecord) obj;
        for (int i = 0; i < record.length; i++) {
            if (record[i] == null) {
                if (other.record[i] != null) {
                    return false;
                }
            } else { //
                if (!record[i].equals(other.record[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Getter for the output record.
     * 
     * @return the words that have matched
     */
    public String[] getRecord() {
        return this.record;
    }

    /**
     * Method "getRecord".
     * 
     * @param index the index of the record
     * @return the value at the given index
     */
    public String getRecord(int index) {
        assert index >= 0 && index < record.length;
        return this.record[index];
    }

    /**
     * Getter for nbMatch
     * 
     * @return the nbMatch
     */
    public int getNbMatch() {
        return this.nbMatch;
    }

    /**
     * Getter for score.
     * 
     * @return the score
     */
    public float getScore() {
        return this.score;
    }

    /**
     * Getter for scores.
     * 
     * @return the scores
     */
    public String getScores() {
        if (scores != null && scores.startsWith("|")) {//$NON-NLS-1$
            scores = scores.substring(1);
        }
        return this.scores;
    }

    /*
     * (non-Javadoc)
     * 
     * Sorts in descending order.
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(OutputRecord o) {
        return -(int) (100 * (this.score - o.score));
    }

}
