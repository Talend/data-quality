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
package org.talend.dataquality.matchmerge.mfb;

import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.Record;

public class DefaultCallback implements MatchMergeAlgorithm.Callback {

    public static final MatchMergeAlgorithm.Callback INSTANCE = new DefaultCallback();

    private DefaultCallback() {
        // no need to implement
    }

    @Override
    public void onBeginRecord(Record record) {
        // no need to implement
    }

    @Override
    public void onMatch(Record record1, Record record2, MatchResult matchResult) {
        // no need to implement
    }

    @Override
    public void onNewMerge(Record record) {
        // no need to implement
    }

    @Override
    public void onRemoveMerge(Record record) {
        // no need to implement
    }

    @Override
    public void onDifferent(Record record1, Record record2, MatchResult matchResult) {
        // no need to implement
    }

    @Override
    public void onEndRecord(Record record) {
        // no need to implement
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

    @Override
    public void onBeginProcessing() {
        // no need to implement
    }

    @Override
    public void onEndProcessing() {
        // no need to implement
    }

    @Override
    public void onSynResult(Record newRecord, Record originalRecord, MatchResult matchResult) {
        newRecord.setConfidence(matchResult.getFinalWorstConfidenceValue());
        newRecord.setWorstConfidenceValueScoreList(matchResult.getWorstConfidenceValueScoreList());
    }

}
