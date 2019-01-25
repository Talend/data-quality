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

import java.util.Iterator;
import java.util.List;

import org.talend.dataquality.matchmerge.mfb.MatchResult;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;

/**
 * Interface for Match & Merge algorithms.
 */
public interface MatchMergeAlgorithm {

    /**
     * Run match & merge on the <code>sourceRecords</code> and returns the result of the match & merge algorithm. This
     * method behaves same as
     * {@link #execute(java.util.Iterator, org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback)} but without
     * any callback.
     * 
     * @param sourceRecords An iterator that provides the input for the match & merge algorithm.
     * {@link org.talend.dataquality.matchmerge.Record record} instances can be created from any source as long as you
     * can provide key/value pairs.
     * @return A list of merged {@link org.talend.dataquality.matchmerge.Record records}.
     * @see org.talend.dataquality.matchmerge.Record
     */
    List<Record> execute(Iterator<Record> sourceRecords);

    /**
     * Run match & merge on the <code>sourceRecords</code> and returns the result of the match & merge algorithm. This
     * method offers an additional {@link org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback callback} that
     * allows client of this API to follow progress on the match & merge progress.
     * 
     * @param sourceRecords An iterator that provides the input for the match & merge algorithm.
     * {@link org.talend.dataquality.matchmerge.Record record} instances can be created from any source as long as you
     * can provide key/value pairs.
     * @param callback A {@link org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback callback} called during
     * progress of the match & merge execution.
     * @return A list of merged {@link org.talend.dataquality.matchmerge.Record records}.
     * @see org.talend.dataquality.matchmerge.Record
     */
    List<Record> execute(Iterator<Record> sourceRecords, Callback callback);

    /**
     * @return Returns the {@link org.talend.dataquality.record.linkage.record.IRecordMatcher matcher} used to match
     * records together.
     */
    IRecordMatcher getMatcher();

    interface Callback {

        /**
         * Called when a new {@link org.talend.dataquality.matchmerge.Record record} is about to be processed by match &
         * merge.
         * 
         * @param record The record being processed.
         */
        void onBeginRecord(Record record);

        /**
         * Called a match is found between 2 records.
         * 
         * @param record1 The first record of the 2 records.
         * @param record2 The other record that matched.
         * @param matchResult A {@link org.talend.dataquality.matchmerge.mfb.MatchResult result} that provides
         * additional information on how the 2 records matched.
         */
        void onMatch(Record record1, Record record2, MatchResult matchResult);

        /**
         * Called when a new merge is created (usually called after a
         * {@link #onMatch(Record, Record, org.talend.dataquality.matchmerge.mfb.MatchResult)}.
         * 
         * @param record The newly merged record.
         */
        void onNewMerge(Record record);

        /**
         * Called when a merged record is removed from the processing queue. This can happen when a match between a
         * merged record and another record is found: a new record is created and <code>record</code> is removed.
         * 
         * @param record The removed record.
         */
        void onRemoveMerge(Record record);

        /**
         * Similar to {@link #onMatch(Record, Record, org.talend.dataquality.matchmerge.mfb.MatchResult)} but when
         * records are different.
         * 
         * @param record1 The first record of the 2 records.
         * @param record2 The other record that matched.
         * @param matchResult A {@link org.talend.dataquality.matchmerge.mfb.MatchResult result} that provides
         * additional information on why the 2 records did not match.
         */
        void onDifferent(Record record1, Record record2, MatchResult matchResult);

        /**
         * Similar to {@link #onBeginProcessing()}, but called to indicate the record was processed.
         * 
         * @param record The record processed by the match & merge.
         */
        void onEndRecord(Record record);

        /**
         * @return Implementations returns <code>true</code> if the match & merge should stop as soon as possible the
         * algorithm. <code>false</code> to allow algorithm to continue.
         */
        boolean isInterrupted();

        /**
         * Called when match & merge algorithm starts.
         */
        void onBeginProcessing();

        /**
         * Called when match & merge algorithm stops.
         */
        void onEndProcessing();

        /**
         * Synchronize result between new merge record and original record
         * 
         * @param newRecord The new master record
         * @param originalRecord One of the record which will merge
         * @param matchResult The result of merging
         */
        void onSynResult(Record newRecord, Record originalRecord, MatchResult matchResult);

    }

}
