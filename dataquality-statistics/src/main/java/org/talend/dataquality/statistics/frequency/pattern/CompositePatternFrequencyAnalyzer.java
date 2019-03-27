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
package org.talend.dataquality.statistics.frequency.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.common.util.LFUCache;
import org.talend.dataquality.statistics.frequency.AbstractFrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.recognition.AbstractPatternRecognizer;
import org.talend.dataquality.statistics.frequency.recognition.DateTimePatternRecognizer;
import org.talend.dataquality.statistics.frequency.recognition.EmptyPatternRecognizer;
import org.talend.dataquality.statistics.frequency.recognition.GenericCharPatternRecognizer;
import org.talend.dataquality.statistics.frequency.recognition.RecognitionResult;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * Compute the pattern frequency tables.<br>
 * This class is a composite analyzer that it will automatically attribute a character to the correct pattern group.
 * 
 * @since 1.3.3
 * @author mzhao
 *
 */
public class CompositePatternFrequencyAnalyzer extends AbstractFrequencyAnalyzer<PatternFrequencyStatistics> {

    private static final long serialVersionUID = -4658709249927616622L;

    private List<AbstractPatternRecognizer> patternFreqRecognizers = new ArrayList<>();

    private final ResizableList<LFUCache> knownPatternCaches = new ResizableList<>(LFUCache.class);

    private DataTypeEnum[] types; // types of columns

    public CompositePatternFrequencyAnalyzer() {
        this(new DataTypeEnum[] {});
    }

    public CompositePatternFrequencyAnalyzer(DataTypeEnum[] types) {
        patternFreqRecognizers.add(new EmptyPatternRecognizer());
        patternFreqRecognizers.add(new DateTimePatternRecognizer());
        patternFreqRecognizers.add(new GenericCharPatternRecognizer());
        this.types = types;
    }

    public CompositePatternFrequencyAnalyzer(List<AbstractPatternRecognizer> analyzerList) {
        this(analyzerList, new DataTypeEnum[] {});
    }

    public CompositePatternFrequencyAnalyzer(List<AbstractPatternRecognizer> analyzerList, DataTypeEnum[] types) {
        patternFreqRecognizers.addAll(analyzerList);
        this.types = types;
    }

    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            return true;
        }
        if (CollectionUtils.isEmpty(freqTableStatistics)) {
            initFreqTableList(record.length);
        }
        knownPatternCaches.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final LFUCache<String, Map<String, Locale>> knownPatternCache = knownPatternCaches.get(i);
            final Map<String, Locale> knownPatterns = knownPatternCache.get(record[i]);
            final PatternFrequencyStatistics freqStats = freqTableStatistics.get(i);
            if (MapUtils.isNotEmpty(knownPatterns)) {
                knownPatterns.entrySet().forEach(knownPattern -> freqStats.add(knownPattern));
            } else {
                if (types.length > 0) {
                    analyzeField(record[i], freqStats, types[i], knownPatternCache);
                } else {
                    analyzeField(record[i], freqStats, null, knownPatternCache);
                }
            }
        }
        return true;
    }

    protected void analyzeField(String field, PatternFrequencyStatistics freqStats, DataTypeEnum type,
            LFUCache<String, Map<String, Locale>> knownPatternCache) {
        Map<String, Locale> patternSet = getValuePatternSet(field, type);
        for (Map.Entry<String, Locale> patternAndLocale : patternSet.entrySet()) {
            freqStats.add(patternAndLocale);
        }
        knownPatternCache.put(field, patternSet);
    }

    @Override
    protected void analyzeField(String field, PatternFrequencyStatistics freqStats) {
        for (Map.Entry<String, Locale> patternAndLocale : getValuePatternSet(field).entrySet()) {
            freqStats.add(patternAndLocale);
        }
    }

    /**
     * Recognize the string and return the pattern of the string with a boolean indicating the pattern replacement is
     * complete if true ,false otherwise.
     * 
     * @param originalValue the string to be replaced by its pattern string
     * @return the recognition result bean.
     */
    Map<String, Locale> getValuePatternSet(String originalValue) {
        return getValuePatternSet(originalValue, null);
    }

    /**
     * Recognize the string and return the pattern of the string with a boolean indicating the pattern replacement is
     * complete if true ,false otherwise.
     * 
     * @param originalValue the string to be replaced by its pattern string
     * @param type the data type
     * @return the recognition result bean.
     */
    private Map<String, Locale> getValuePatternSet(String originalValue, DataTypeEnum type) {
        Map<String, Locale> resultMap = new HashMap<>();
        String patternString = originalValue;
        for (AbstractPatternRecognizer recognizer : patternFreqRecognizers) {
            RecognitionResult result = recognizer.recognize(patternString, type);
            resultMap = result.getPatternToLocale();
            if (result.isComplete()) {
                break;
            } else {
                if (!resultMap.isEmpty()) {
                    patternString = resultMap.keySet().iterator().next();
                }
            }
        }
        // value is not recognized completely.
        return resultMap;
    }

    @Override
    protected void initFreqTableList(int size) {
        List<PatternFrequencyStatistics> freqTableList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PatternFrequencyStatistics freqTable = new PatternFrequencyStatistics();
            freqTableList.add(freqTable);
        }
        freqTableStatistics = new ResizableList<>(freqTableList);
    }
}
