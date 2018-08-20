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
package org.talend.dataquality.statistics.quality;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.talend.dataquality.common.inference.QualityAnalyzer;
import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.semantic.recognizer.LFUCache;
import org.talend.dataquality.statistics.datetime.CustomDateTimePatternManager;
import org.talend.dataquality.statistics.datetime.SystemDateTimePatternManager;
import org.talend.dataquality.statistics.type.DataTypeEnum;
import org.talend.dataquality.statistics.type.SortedList;
import org.talend.dataquality.statistics.type.TypeInferenceUtils;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class DataTypeQualityAnalyzer extends QualityAnalyzer<ValueQualityStatistics, DataTypeEnum[]> {

    private static final long serialVersionUID = -5951511723860660263L;

    private final ResizableList<ValueQualityStatistics> results = new ResizableList<>(ValueQualityStatistics.class);

    private final ResizableList<SortedList> frequentDatePatterns = new ResizableList<>(SortedList.class);

    private final ResizableList<LFUCache> knownDataTypeCaches = new ResizableList<>(LFUCache.class);

    private List<String> customDateTimePatterns = new ArrayList<>();

    public DataTypeQualityAnalyzer(DataTypeEnum[] types, boolean isStoreInvalidValues) {
        this.isStoreInvalidValues = isStoreInvalidValues;
        setTypes(types);
    }

    public DataTypeQualityAnalyzer(DataTypeEnum... types) {
        setTypes(types);
    }

    @Deprecated
    public void addCustomDateTimePattern(String pattern) {
        if (StringUtils.isNotBlank(pattern)) {
            customDateTimePatterns.add(pattern);
        }
    }

    @Override
    public void init() {
        frequentDatePatterns.clear();
        results.clear();
        knownDataTypeCaches.clear();
    }

    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            record = new String[] { StringUtils.EMPTY };
        }
        results.resize(record.length);
        frequentDatePatterns.resize(record.length);
        knownDataTypeCaches.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final LFUCache<String, Boolean> knownDataTypeCache = knownDataTypeCaches.get(i);
            final String value = record[i];
            final Boolean knownDataType = knownDataTypeCache.get(value);
            final ValueQualityStatistics valueQuality = results.get(i);
            if (knownDataType != null) {
                if (knownDataType)
                    valueQuality.incrementValid();
                else {
                    valueQuality.incrementInvalid();
                    processInvalidValue(valueQuality, value);
                }
            } else {
                if (TypeInferenceUtils.isEmpty(value)) {
                    valueQuality.incrementEmpty();
                } else if (DataTypeEnum.DATE == getTypes()[i] && isValidDate(value, frequentDatePatterns.get(i))) {
                    valueQuality.incrementValid();
                    knownDataTypeCache.put(value, Boolean.TRUE);
                } else if (DataTypeEnum.TIME == getTypes()[i]
                        && CustomDateTimePatternManager.isTime(value, customDateTimePatterns)) {
                    valueQuality.incrementValid();
                    knownDataTypeCache.put(value, Boolean.TRUE);
                } else if (TypeInferenceUtils.isValid(getTypes()[i], value)) {
                    valueQuality.incrementValid();
                    knownDataTypeCache.put(value, Boolean.TRUE);
                } else {
                    // while list analyzers
                    // analyzer.incrementValid or invalid...
                    // else
                    valueQuality.incrementInvalid();
                    processInvalidValue(valueQuality, value);
                    knownDataTypeCache.put(value, Boolean.FALSE);
                }

            }
        }
        return true;
    }

    private boolean isValidDate(String value, SortedList<Pair<Pattern, DateTimeFormatter>> orderedPatterns) {
        for (int j = 0; j < orderedPatterns.size(); j++) {
            Pair<Pattern, DateTimeFormatter> cachedPattern = orderedPatterns.get(j).getLeft();
            if (cachedPattern.getLeft().matcher(value).find()
                    && SystemDateTimePatternManager.isMatchDateTimePattern(value, cachedPattern.getRight())) {
                orderedPatterns.increment(j);
                return true;
            }
        }

        Optional<Pair<Pattern, DateTimeFormatter>> foundPattern = SystemDateTimePatternManager.findOneDatePattern(value);
        foundPattern.ifPresent(pattern -> orderedPatterns.addNewValue(pattern));
        return foundPattern.isPresent();
    }

    private void processInvalidValue(ValueQualityStatistics valueQuality, String invalidValue) {
        if (isStoreInvalidValues) {
            valueQuality.appendInvalidValue(invalidValue);
        }
    }

    @Override
    public void end() {
        // Nothing to do.
    }

    @Override
    public List<ValueQualityStatistics> getResult() {
        return results;
    }

    @Override
    public void close() throws Exception {
    }
}
