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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.statistics.frequency.AbstractFrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.AbstractFrequencyStatistics;
import org.talend.dataquality.statistics.frequency.recognition.AbstractPatternRecognizer;
import org.talend.dataquality.statistics.frequency.recognition.DateTimePatternRecognizer;
import org.talend.dataquality.statistics.frequency.recognition.EmptyPatternRecognizer;
import org.talend.dataquality.statistics.frequency.recognition.LatinExtendedCharPatternRecognizer;
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

    private List<AbstractPatternRecognizer> patternFreqRecognizers = new ArrayList<AbstractPatternRecognizer>();

    private DataTypeEnum[] types; // types of columns

    public CompositePatternFrequencyAnalyzer() {
        this(new DataTypeEnum[] {});
    }

    public CompositePatternFrequencyAnalyzer(DataTypeEnum[] types) {
        patternFreqRecognizers.add(new EmptyPatternRecognizer());
        patternFreqRecognizers.add(new DateTimePatternRecognizer());
        patternFreqRecognizers.add(new LatinExtendedCharPatternRecognizer());
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
        if (freqTableStatistics == null || freqTableStatistics.isEmpty()) {
            initFreqTableList(record.length);
        }
        for (int i = 0; i < record.length; i++) {
            AbstractFrequencyStatistics freqStats = freqTableStatistics.get(i);

            if (types.length > 0) {
                analyzeField(record[i], freqStats, types[i]);
            } else {
                analyzeField(record[i], freqStats, null);
            }
        }
        return true;
    }

    protected void analyzeField(String field, AbstractFrequencyStatistics freqStats, DataTypeEnum type) {
        for (String pattern : getValuePatternSet(field, type)) {
            freqStats.add(pattern);
        }
    }

    @Override
    protected void analyzeField(String field, AbstractFrequencyStatistics freqStats) {
        for (String pattern : getValuePatternSet(field)) {
            freqStats.add(pattern);
        }
    }

    /**
     * Recognize the string and return the pattern of the string with a boolean indicating the pattern replacement is
     * complete if true ,false otherwise.
     * 
     * @param originalValue the string to be replaced by its pattern string
     * @return the recognition result bean.
     */
    Set<String> getValuePatternSet(String originalValue) {
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
    Set<String> getValuePatternSet(String originalValue, DataTypeEnum type) {
        Set<String> resultSet = new HashSet<String>();
        String patternString = originalValue;
        for (AbstractPatternRecognizer recognizer : patternFreqRecognizers) {
            RecognitionResult result = recognizer.recognize(patternString, type);
            resultSet = result.getPatternStringSet();
            if (result.isComplete()) {
                break;
            } else {
                if (!resultSet.isEmpty()) {
                    patternString = resultSet.iterator().next();
                }
            }
        }
        // value is not recognized completely.
        return resultSet;
    }

    @Override
    protected void initFreqTableList(int size) {
        List<PatternFrequencyStatistics> freqTableList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PatternFrequencyStatistics freqTable = new PatternFrequencyStatistics();
            freqTable.setAlgorithm(algorithm);
            freqTableList.add(freqTable);
        }
        freqTableStatistics = new ResizableList<>(freqTableList);
    }
}
