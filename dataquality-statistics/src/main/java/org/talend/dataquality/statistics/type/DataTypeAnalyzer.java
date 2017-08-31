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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.ResizableList;

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

    private final ResizableList<DataTypeOccurences> dataTypes = new ResizableList<>(DataTypeOccurences.class);

    /** Optional custom date patterns. */
    protected List<String> customDateTimePatterns = new ArrayList<>();

    /**
     * Default empty constructor.
     */
    public DataTypeAnalyzer() {
        this(Collections.<String> emptyList());
    }

    /**
     * Create a DataTypeAnalyzer with the given custom date patterns.
     * 
     * @param customDateTimePatterns the patterns to use.
     */
    public DataTypeAnalyzer(List<String> customDateTimePatterns) {
        this.customDateTimePatterns.addAll(customDateTimePatterns);
    }

    public void init() {
        dataTypes.clear();
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
        dataTypes.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final DataTypeOccurences dataType = dataTypes.get(i);
            dataType.increment(TypeInferenceUtils.getDataType(record[i], customDateTimePatterns));
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
        return dataTypes;
    }

    @Override
    public Analyzer<DataTypeOccurences> merge(Analyzer<DataTypeOccurences> another) {
        int idx = 0;
        DataTypeAnalyzer mergedAnalyzer = new DataTypeAnalyzer();
        for (DataTypeOccurences dt : dataTypes) {
            mergedAnalyzer.getResult().add(idx, dt);
            if (!another.getResult().isEmpty()) {
                Map<DataTypeEnum, Long> typeFreqTable = dt.getTypeFrequencies();
                Map<DataTypeEnum, Long> anotherTypeFreqTable = another.getResult().get(idx).getTypeFrequencies();
                Iterator<DataTypeEnum> anotherDTIt = anotherTypeFreqTable.keySet().iterator();
                while (anotherDTIt.hasNext()) {
                    DataTypeEnum anotherDT = anotherDTIt.next();
                    // Update the current map
                    if (typeFreqTable.containsKey(anotherDT)) {
                        mergedAnalyzer.getResult().get(idx).getTypeFrequencies().put(anotherDT,
                                typeFreqTable.get(anotherDT) + anotherTypeFreqTable.get(anotherDT));
                    } else {
                        mergedAnalyzer.getResult().get(idx).getTypeFrequencies().put(anotherDT,
                                anotherTypeFreqTable.get(anotherDT));
                    }
                }
            }
            idx++;
        }
        return mergedAnalyzer;

    }

    @Override
    public void close() throws Exception {

    }
}
