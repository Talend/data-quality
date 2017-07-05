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
import org.talend.dataquality.common.exception.DQRuntimeException;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.ResizableList;
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

    private final ResizableList<SemanticType> results = new ResizableList<>(SemanticType.class);

    private final Map<Integer, CategoryRecognizer> columnIdxToCategoryRecognizer = new HashMap<>();

    private final CategoryRecognizerBuilder builder;

    // Threshold of handle to be run. since the semantic inferring will require
    // more time than expected, we may only want to run the handle method on a
    // sample with small size. Default value is 10000.
    private int limit = 10000;

    private int currentCount = 0;

    public SemanticAnalyzer(CategoryRecognizerBuilder builder) {
        this(builder, 10000);
    }

    public SemanticAnalyzer(CategoryRecognizerBuilder builder, int limit) {
        this.builder = builder;
        this.limit = limit;
        builder.initIndex();
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
                    throw new DQRuntimeException(
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
                results.get(colIdx).increment(semCategory, semCategory.getCount());
            }
        }
        return results;
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

}
