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
package org.talend.dataquality.record.linkage.analyzer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.AttributeValues;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.matchmerge.mfb.MFB;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.genkey.BlockingKeyHandler;
import org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * String clustering analyzer.
 * 
 * @author mzhao
 *
 */
public class StringsClusterAnalyzer implements Serializable {

    private static final long serialVersionUID = -3359232597093558703L;

    private static final BlockingKeyAlgorithmEnum blockKeyAlgorithm = BlockingKeyAlgorithmEnum.FINGERPRINTKEY;

    private final StringClusters stringClusters = new StringClusters();

    private List<Record> records = new ArrayList<>();

    private BlockingKeyHandler blockKeyHandler = null;

    private int blockSizeThreshold = 1000;

    private int currentBlockIndex = 0;

    private PostMerge[] postMerges = new PostMerge[0];

    private static List<Record> postMerge(List<Record> records, AttributeMatcherType matchAlgorithm, float threshold) {
        List<Record> mergeResult;
        MatchMergeAlgorithm crossBlockMatch = MFB.build(new AttributeMatcherType[] { matchAlgorithm }, //
                new String[] { StringUtils.EMPTY }, //
                new float[] { threshold }, //
                0.95d, //
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_COMMON }, //
                new String[] { StringUtils.EMPTY }, //
                new double[] { 1.0 }, //
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchNull }, //
                new SubString[] { SubString.NO_SUBSTRING }, //
                StringUtils.EMPTY);
        mergeResult = crossBlockMatch.execute(records.iterator());
        return mergeResult;
    }

    /**
     * Sets the maximum allowed size for a block. When block exceeds this threshold, analyzer match & merge block values
     * to limit memory usage.
     * 
     * @param blockSizeThreshold A positive value higher than 1.
     */
    public void setBlockSizeThreshold(int blockSizeThreshold) {
        if (blockSizeThreshold < 1) {
            throw new IllegalArgumentException("Threshold must be greater than 1.");
        }
        this.blockSizeThreshold = blockSizeThreshold;
    }

    /**
     * Configures merges to execute once all blocks are matched & merged.
     *
     * @param postMerges Any number of {@link PostMerge merges} to be performed between blocks.
     */
    public void withPostMerges(PostMerge... postMerges) {
        this.postMerges = postMerges;
    }

    public void init() {
        // Blocking the data given fingerprint key
        String columnName = "NAME";
        List<Map<String, String>> blockKeySchema = new ArrayList<>();
        Map<String, String> blockKeyDefMap = new HashMap<>();

        blockKeyDefMap.put(MatchAnalysisConstant.PRECOLUMN, columnName);
        blockKeyDefMap.put(MatchAnalysisConstant.KEY_ALGO, blockKeyAlgorithm.getValue());
        blockKeySchema.add(blockKeyDefMap);

        Map<String, String> colName2IndexMap = new HashMap<>();
        colName2IndexMap.put(columnName, String.valueOf(0));
        blockKeyHandler = new BlockingKeyHandler(blockKeySchema, colName2IndexMap);

        records.clear();
        currentBlockIndex = 0;
    }

    public boolean analyze(String... record) {
        if (record == null || record.length != 1) {
            return false;
        }
        String block = blockKeyHandler.process(record);
        int blockSize = blockKeyHandler.getBlockSize(block);
        if (blockSize > blockSizeThreshold) {
            // Run the match and merge
            Map<String, List<String[]>> resultOfBlock = blockKeyHandler.getResultDatas();
            doMatchMerge(currentBlockIndex++, resultOfBlock.get(block));
            // Empty the data of this key.
            resultOfBlock.get(block).clear();
        }
        return true;
    }

    public void end() {
        // Match & merge block values
        Map<String, List<String[]>> resultData = blockKeyHandler.getResultDatas();

        for (List<String[]> blockValues : resultData.values()) {
            doMatchMerge(currentBlockIndex, blockValues);
            currentBlockIndex++;
        }
        // Match & merge block values together (with higher thresholds)
        for (PostMerge postMerge : postMerges) {
            records = postMerge(records, postMerge.matcher, postMerge.threshold);
        }
        // Build string cluster based on successive match & merges
        Map<String, String[]> masterToValues = new HashMap<>();
        for (Record record : records) {
            if (record.getRelatedIds().size() > 1) { // Merged record (and not a single record)
                final Attribute attribute = record.getAttributes().get(0);
                final AttributeValues<String> values = attribute.getValues();
                // AttributeValues' iterator may return twice same value (see TDQ-10512).
                final Set<String> originalValues = new HashSet<>();
                for (String value : values) {
                    originalValues.add(value);
                }
                final int size = originalValues.size();
                if (values.hasMultipleValues() && size > 1) {
                    masterToValues.put(attribute.getValue(), originalValues.toArray(new String[size]));
                }
            }
        }
        for (Map.Entry<String, String[]> current : masterToValues.entrySet()) {
            final StringClusters.StringCluster cluster = new StringClusters.StringCluster();
            cluster.survivedValue = current.getKey();
            cluster.originalValues = current.getValue();
            stringClusters.addCluster(cluster);
        }
    }

    private void doMatchMerge(int currentBlockIndex, List<String[]> blockValues) {
        MatchMergeAlgorithm matchMergeAlgorithm = MFB.build(new AttributeMatcherType[] { AttributeMatcherType.DUMMY }, //
                new String[] { StringUtils.EMPTY }, //
                new float[] { 0.8f }, //
                0.8d, //
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_COMMON }, //
                new String[] { StringUtils.EMPTY }, //
                new double[] { 1.0 }, //
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchNull }, //
                new SubString[] { SubString.NO_SUBSTRING }, //
                StringUtils.EMPTY);
        final Iterator<String[]> iterator = blockValues.iterator();
        final List<Record> blockResults = matchMergeAlgorithm.execute(new RecordIterator(currentBlockIndex, iterator));
        records.addAll(blockResults);
    }

    public List<StringClusters> getResult() {
        List<StringClusters> cluster = new ArrayList<>();
        cluster.add(stringClusters);
        return cluster;
    }

    /**
     * created by talend on 2015-07-28 Detailled comment.
     *
     */
    private static class RecordIterator implements Iterator<Record> {

        private final int currentBlockIndex;

        private final Iterator<String[]> iterator;

        private int index;

        public RecordIterator(int currentBlockIndex, Iterator<String[]> iterator) {
            this.currentBlockIndex = currentBlockIndex;
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Record next() {
            final String[] values = iterator.next();
            final Attribute value = new Attribute("col0");
            value.setValue(values[0]);
            return new Record(Collections.singletonList(value), currentBlockIndex + "-" + String.valueOf(index++), 0, "");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
