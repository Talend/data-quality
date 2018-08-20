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
package org.talend.dataquality.statistics.type;

import static org.talend.dataquality.statistics.datetime.SystemDateTimePatternManager.isDate;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.semantic.recognizer.LFUCache;

/**
 * Type inference executor which provide several methods computing the types.<br>
 * The suggested usage of this class is the following call sequence:<br>
 * 1. {{@link #init()}, called once.<br>
 * 2. {{@link Analyzer#analyze(String...)} , called as many iterations as required.<br>
 * 3. {{@link #getResult()} , called once.<br>
 *
 * <b>Important note:</b> This class is <b>NOT</b> thread safe.
 *
 */
public class DataTypeAnalyzer implements Analyzer<DataTypeOccurences> {

    private static final long serialVersionUID = 373694310453353502L;

    private final ResizableList<DataTypeOccurences> dataTypeResults = new ResizableList<>(DataTypeOccurences.class);

    private final ResizableList<SortedList> frequentDatePatterns = new ResizableList<>(SortedList.class);

    /** Optional custom date patterns. */
    protected List<String> customDateTimePatterns = new ArrayList<>();

    private final ResizableList<LFUCache> knownDataTypeCaches = new ResizableList<>(LFUCache.class);

    /**
     * Default empty constructor.
     */
    public DataTypeAnalyzer() {
    }

    /**
     * Create a DataTypeAnalyzer with the given custom date patterns.
     *
     * @param customDateTimePatterns the patterns to use.
     */
    @Deprecated
    public DataTypeAnalyzer(List<String> customDateTimePatterns) {
        this.customDateTimePatterns.addAll(customDateTimePatterns);
    }

    public void init() {
        dataTypeResults.clear();
        frequentDatePatterns.clear();
        knownDataTypeCaches.clear();
    }

    /**
     * Analyze record of Array of string type, this method is used in scala library which not support parameterized
     * array type.
     *
     * @param record
     * @return
     */
    public boolean analyzeArray(String[] record) {
        return analyze(record);
    }

    /**
     * Inferring types record by record.
     *
     * @param record for which the data type is guessed.
     * @return true if inferred successfully, false otherwise.
     */
    public boolean analyze(String... record) {
        if (record == null) {
            return true;
        }
        dataTypeResults.resize(record.length);
        frequentDatePatterns.resize(record.length);
        knownDataTypeCaches.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final DataTypeOccurences dataType = dataTypeResults.get(i);
            final LFUCache<String, DataTypeEnum> knownDataTypeCache = knownDataTypeCaches.get(i);
            final String value = record[i];
            final DataTypeEnum knownDataType = knownDataTypeCache.get(value);
            if (knownDataType != null) {
                dataType.increment(knownDataType);
            } else {
                DataTypeEnum type = TypeInferenceUtils.getNativeDataType(value);
                //STRING means we didn't find any native data types
                if (DataTypeEnum.STRING.equals(type) && isDate(value, frequentDatePatterns.get(i)))
                    type = DataTypeEnum.DATE;
                knownDataTypeCache.put(value, type);
                dataType.increment(type);
            }
        }
        return true;
    }

    public void end() {
        // Nothing to do.
    }

    /**
     * Get the inferring result, this method should be called once and only once after {
     * {@link Analyzer#analyze(String...)} method.
     * 
     * @return A map for <b>each</b> column. Each map contains the type occurrence count.
     */
    public List<DataTypeOccurences> getResult() {
        return dataTypeResults;
    }

    @Override
    public void close() throws Exception {

    }
}
