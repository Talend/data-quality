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
package org.talend.dataquality.semantic.statistics;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Metadata;
import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.semantic.exception.DQSemanticRuntimeException;
import org.talend.dataquality.semantic.recognizer.CategoryFrequency;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

/**
 * Semantic type infer executor. <br>
 * 
 * @see Analyzer
 * 
 */
public class SemanticAnalyzer implements Analyzer<SemanticType> {

    private static final long serialVersionUID = 6808620909722453108L;

    private static float DEFAULT_WEIGHT_VALUE = 0.9f;

    private final ResizableList<SemanticType> results = new ResizableList<>(SemanticType.class);

    private final Map<Integer, CategoryRecognizer> columnIdxToCategoryRecognizer = new HashMap<>();

    private final CategoryRecognizerBuilder builder;

    // Threshold of rows to be handled. in case we only want to analyze a given number of samples. Default value is 10000.
    private int limit = 10000;

    private int currentCount = 0;

    private Map<Metadata, List<String>> metadataMap;

    private float weight = DEFAULT_WEIGHT_VALUE;

    /**
     * @param builder the builder for creating lucene index access and regex classifiers
     */
    public SemanticAnalyzer(CategoryRecognizerBuilder builder) {
        this(builder, 10000);
    }

    /**
     * @param builder the builder for creating lucene index access and regex classifiers
     * @param limit the limit of rows to handle
     */
    public SemanticAnalyzer(CategoryRecognizerBuilder builder, int limit) {
        this(builder, limit, DEFAULT_WEIGHT_VALUE);
    }

    /**
     * @param builder the builder for creating lucene index access and regex classifiers
     * @param limit the limit of rows to handle
     * @param weight the weight of data discovery result for score calculation, default to 0.9, which means the metadata will also
     * be taken into account for a weight of 0.1
     */
    public SemanticAnalyzer(CategoryRecognizerBuilder builder, int limit, float weight) {
        this.builder = builder;
        this.limit = limit;
        this.weight = weight;
        builder.initIndex();
        metadataMap = new HashMap<>();
    }

    /**
     * Set the maximum of records this semantic analyzer is expected to process. Any value <= 0 is considered as
     * "no limit". A value of 1 will only analyze first call to {@link #analyze(String...)}.
     * 
     * @param limit A integer that indicate the maximum number of record this analyzer should process.
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Set the weight of data discovery result for score calculation.
     *
     * @param weight the weight of data discovery result for score calculation, default to 0.9, which means the metadata will also
     * be taken into account for a weight of 0.1
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public void init() {
        currentCount = 0;
        columnIdxToCategoryRecognizer.clear();
        results.clear();
        builder.initIndex();
    }

    /**
     * Analyze the record by guessing the data semantic type.
     */
    @Override
    public boolean analyze(String... record) {
        results.resize(record.length);
        resizeCategoryRecognizer(record);
        if (currentCount < limit || limit <= 0) {
            for (int i = 0; i < record.length; i++) {
                CategoryRecognizer categoryRecognizer = columnIdxToCategoryRecognizer.get(i);
                if (categoryRecognizer == null) {
                    throw new DQSemanticRuntimeException(
                            "CategoryRecognizer is null for record and i=" + i + " " + Arrays.asList(record));
                } else {
                    categoryRecognizer.process(record[i]);
                }
            }
            currentCount++;
        }
        return true;
    }

    private void resizeCategoryRecognizer(String[] record) {
        if (columnIdxToCategoryRecognizer.size() > 0) {
            // already resized
            return;
        }
        for (int idx = 0; idx < record.length; idx++) {
            try {
                CategoryRecognizer recognizer = builder.build();
                columnIdxToCategoryRecognizer.put(idx, recognizer);
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to configure category recognizer with builder.", e);
            }
        }
    }

    @Override
    public void end() {
        // do nothing
    }

    /**
     * Get a list of guessed semantic type with type {{@link SemanticType}
     */
    @Override
    public List<SemanticType> getResult() {

        for (Entry<Integer, CategoryRecognizer> entry : columnIdxToCategoryRecognizer.entrySet()) {
            Integer colIdx = entry.getKey();
            Collection<CategoryFrequency> result = entry.getValue().getResult();

            for (CategoryFrequency semCategory : result) {
                final float scoreOnHeader = getScoreOnHeader(colIdx, semCategory.getCategoryName());
                final float score = semCategory.getFrequency() * weight + (scoreOnHeader * 100 * (1 - weight));

                semCategory.setScore(score);
                results.get(colIdx).increment(semCategory, semCategory.getCount());
            }
        }

        return results;
    }

    private float getScoreOnHeader(Integer columnIdx, String categoryName) {
        int score = 0;
        List<String> metadata = metadataMap.get(Metadata.HEADER_NAME);
        if (metadata != null) {
            final boolean match = StringUtils.equalsIgnoreCase(metadata.get(columnIdx), categoryName);
            if (metadataMap.get(Metadata.HEADER_NAME) != null && match && Float.compare(weight, 0f) != 0) {
                score = 1;
            }
        }
        return score;
    }

    @Override
    public Analyzer<SemanticType> merge(Analyzer<SemanticType> another) {
        throw new NotImplementedException("Merge function is not implemented.");
    }

    @Override
    public void close() throws Exception {
        for (CategoryRecognizer catRecognizer : columnIdxToCategoryRecognizer.values()) {
            catRecognizer.end();
        }
    }

    /**
     * Store metadata
     * 
     * @param metadata metadata name
     * @param values value associated to the metadata
     */
    public void setMetadata(Metadata metadata, List<String> values) {
        metadataMap.put(metadata, values);
    }
}
