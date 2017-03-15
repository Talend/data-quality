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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.MFB;
import org.talend.dataquality.matchmerge.mfb.MatchMergeCallback;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer;
import org.talend.dataquality.record.linkage.grouping.TSwooshGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMerger;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * created by zhao on Jul 10, 2014 MFB algorithm adapted to DQ grouping API, which will continue matching two different
 * groups.
 * 
 */
public class DQMFB extends MFB {

    private Callback callback;

    private Queue<Record> queue;

    private List<Record> mergedRecords = new ArrayList<Record>();

    /**
     * DOC zhao DQMFB constructor comment.
     * 
     * @param matcher
     * @param merger
     */
    public DQMFB(IRecordMatcher matcher, IRecordMerger merger) {
        super(matcher, merger);
    }

    public DQMFB(IRecordMatcher matcher, IRecordMerger merger, Callback callback) {
        super(matcher, merger);
        this.callback = callback;
        queue = new ArrayDeque<Record>();
        if (callback != null) {
            callback.onBeginProcessing();
        }
    }

    @Override
    public List<Record> execute(Iterator<Record> sourceRecords) {
        return execute(sourceRecords, callback);
    }

    public static DQMFB build(RecordMatcherType recordLinkageAlgorithm, List<List<Map<String, String>>> multiMatchRules,
            List<SurvivorshipFunction[]> multiSurvivorshipFunctions, Map<Integer, SurvivorshipFunction> defaultSurviorshipRules,
            String mergedRecordSource, Callback callback, MatchGroupResultConsumer matchResultConsumer) throws Exception {
        AnalysisSwooshMatchRecordGrouping recordGrouping = new AnalysisSwooshMatchRecordGrouping(matchResultConsumer);
        recordGrouping.setRecordLinkAlgorithm(recordLinkageAlgorithm);
        for (List<Map<String, String>> matchRule : multiMatchRules) {
            recordGrouping.addMatchRule(matchRule);
        }
        recordGrouping.initialize();
        CombinedRecordMatcher combinedMatcher = recordGrouping.getCombinedRecordMatcher();
        List<IRecordMatcher> matchers = combinedMatcher.getMatchers();

        int size = multiSurvivorshipFunctions.get(0).length;
        String[] parameters = new String[size];
        SurvivorShipAlgorithmEnum[] typeMergeTable = new SurvivorShipAlgorithmEnum[size];

        SurvivorShipAlgorithmParams matchMergeParam = new SurvivorShipAlgorithmParams();
        matchMergeParam.setRecordMatcher(combinedMatcher);
        matchMergeParam.setDefaultSurviorshipRules(defaultSurviorshipRules);
        for (int i = 0; i < matchers.size(); i++) {
            matchMergeParam.getSurvivorshipAlgosMap().put(matchers.get(i), multiSurvivorshipFunctions.get(i));
        }
        DQMFBRecordMerger recordMerger = new DQMFBRecordMerger(mergedRecordSource, parameters, typeMergeTable, matchMergeParam);

        TSwooshGrouping<Object> tswooshGrouping = new TSwooshGrouping<Object>(recordGrouping);
        if (callback == null) {
            callback = tswooshGrouping.new GroupingCallBack();
        } else if (callback instanceof MatchMergeCallback) {
            ((MatchMergeCallback) callback).setPrevious(tswooshGrouping.new GroupingCallBack());
        }

        return new DQMFB(combinedMatcher, recordMerger, callback);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.matchmerge.mfb.MFB#isMatchDiffGroups()
     */
    @Override
    protected boolean isMatchDiffGroups() {
        return true;
    }

    /**
     * do the match on one record
     * 
     * @param oneRecord
     */
    public void matchOneRecord(Record oneRecord) {
        execute(oneRecord, mergedRecords, queue, callback);
    }

    public List<Record> getResult() {
        while (!queue.isEmpty() && !callback.isInterrupted()) {
            Record currentRecord = queue.poll();
            execute(currentRecord, mergedRecords, queue, callback);
        }
        callback.onEndProcessing();
        return this.mergedRecords;
    }

}
