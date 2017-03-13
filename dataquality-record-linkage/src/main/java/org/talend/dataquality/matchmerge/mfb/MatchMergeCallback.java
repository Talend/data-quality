// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback;
import org.talend.dataquality.matchmerge.Record;

public class MatchMergeCallback extends LoggerCallback {

    private int maxGroupSize = 50;

    private long matchCandidateNumberCount = 0;

    private long currentMatchCount = 0;

    private Callback previous;

    public MatchMergeCallback(int maxGroupSize, long matchCandidateNumberCount) {
        this.maxGroupSize = maxGroupSize;
        this.matchCandidateNumberCount = matchCandidateNumberCount;
    }

    public void setPrevious(Callback previous) {
        this.previous = previous;
    }

    @Override
    public void onMatch(Record record1, Record record2, MatchResult matchResult) {
        if (previous != null) {
            previous.onMatch(record1, record2, matchResult);
        }
        super.onMatch(record1, record2, matchResult);
        // This assumes record1 and record2 are not both golden record (should not occur).
        if (record1.getId().equals(record1.getGroupId())) {
            currentMatchCount += record2.getRelatedIds().size();
        } else if (record2.getId().equals(record2.getGroupId())) {
            currentMatchCount += record1.getRelatedIds().size();
        }
    }

    @Override
    public void onNewMerge(Record record) {
        if (previous != null) {
            previous.onNewMerge(record);
        }
        super.onNewMerge(record);
        if (record.getRelatedIds().size() > maxGroupSize) {
            throw new RuntimeException("Too many records in group (" + record.getRelatedIds().size() + " > " + maxGroupSize
                    + "). Please review configuration.");
        }
    }

    @Override
    public boolean isInterrupted() {
        return currentMatchCount >= matchCandidateNumberCount;
    }
}
