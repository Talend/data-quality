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

package org.talend.dataquality.matchmerge.mfb.mdm.utils;

import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.LoggerCallback;
import org.talend.dataquality.matchmerge.mfb.MatchResult;
import org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBack;

public class GroupingLoggerCallback extends LoggerCallback {

    protected GroupingCallBack<Object> groupingCallback;

    public void setGroupingCallback(GroupingCallBack<Object> groupingCallback) {
        this.groupingCallback = groupingCallback;
    }

    @Override
    public void onBeginRecord(Record record) {
        if (groupingCallback != null) {
            groupingCallback.onBeginRecord(record);
        }
        super.onBeginRecord(record);
    }

    @Override
    public void onMatch(Record record1, Record record2, MatchResult matchResult) {
        if (groupingCallback != null) {
            groupingCallback.onMatch(record1, record2, matchResult);
        }
        super.onMatch(record1, record2, matchResult);
    }

    @Override
    public void onNewMerge(Record record) {
        if (groupingCallback != null) {
            groupingCallback.onNewMerge(record);
        }
        super.onNewMerge(record);
    }

    @Override
    public void onRemoveMerge(Record record) {
        if (groupingCallback != null) {
            groupingCallback.onRemoveMerge(record);
        }
        super.onRemoveMerge(record);
    }

    @Override
    public void onDifferent(Record record1, Record record2, MatchResult matchResult) {
        if (groupingCallback != null) {
            groupingCallback.onDifferent(record1, record2, matchResult);
        }
        super.onDifferent(record1, record2, matchResult);
    }

    @Override
    public void onEndRecord(Record record) {
        if (groupingCallback != null) {
            groupingCallback.onEndRecord(record);
        }
        super.onEndRecord(record);
    }

    @Override
    public boolean isInterrupted() {
        return super.isInterrupted();
    }

    @Override
    public void onBeginProcessing() {
        if (groupingCallback != null) {
            groupingCallback.onBeginProcessing();
        }
        super.onBeginProcessing();
    }

    @Override
    public void onEndProcessing() {
        if (groupingCallback != null) {
            groupingCallback.onEndProcessing();
        }
        super.onEndProcessing();
    }

}