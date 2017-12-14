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
import org.apache.log4j.Logger;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Metadata;
import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.semantic.exception.DQSemanticRuntimeException;
import org.talend.dataquality.semantic.recognizer.CategoryFrequency;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.DefaultCategoryRecognizer;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

/**
 * Semantic type infer executor. <br>
 *
 * @see Analyzer
 */
public class SemanticAnalyzer implements Analyzer<SemanticType> {

    private static final long serialVersionUID = 6808620909722453108L;

    private static final Logger LOGGER = Logger.getLogger(SemanticQualityAnalyzer.class);

    private static float DEFAULT_WEIGHT_VALUE = 0.1f;

    private final ResizableList<SemanticType> results = new ResizableList<>(SemanticType.class);

    private final Map<Integer, CategoryRecognizer> columnIdxToCategoryRecognizer = new HashMap<>();

    // Threshold of rows to be handled. in case we only want to analyze a given number of samples. Default value is
    // 10000.
    private int limit = 10000;

    private int currentCount = 0;

    private Map<Metadata, List<String>> metadataMap;

    private float weight = DEFAULT_WEIGHT_VALUE;

    private DictionarySnapshot dictionarySnapshot;

    public SemanticAnalyzer(DictionarySnapshot dictionarySnapshot) {
        if (dictionarySnapshot == null)
            throw new NullPointerException("Dictionary dictionarySnapshot is Null.");
        this.dictionarySnapshot = dictionarySnapshot;
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
     * @param weight the weight of data discovery result for score calculation, default to 0.9, which means the metadata
     * will also
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
        if (dictionarySnapshot != null) {
            dictionarySnapshot.getCustomDataDict().initIndex();
        }
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
                columnIdxToCategoryRecognizer.put(idx, new DefaultCategoryRecognizer(dictionarySnapshot));
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to configure category recognizer with dictionary snapshot.", e);
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

            String columnName = "";
            if (metadataMap.get(Metadata.HEADER_NAME) != null) {
                List<String> metadata = metadataMap.get(Metadata.HEADER_NAME);
                columnName = metadata.get(colIdx);
            }

            Collection<CategoryFrequency> result = entry.getValue().getResult(columnName, weight);

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
