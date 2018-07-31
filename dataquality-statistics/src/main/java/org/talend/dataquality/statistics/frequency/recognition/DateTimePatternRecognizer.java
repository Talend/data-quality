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
package org.talend.dataquality.statistics.frequency.recognition;

import static org.talend.dataquality.statistics.datetime.SystemDateTimePatternManager.validateWithPatternInAnyLocale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.talend.dataquality.statistics.datetime.CustomDateTimePatternManager;
import org.talend.dataquality.statistics.type.DataTypeEnum;
import org.talend.dataquality.statistics.type.SortedList;

/**
 * Recognize date types given the predefined date regex pattern.
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class DateTimePatternRecognizer extends AbstractPatternRecognizer {

    private List<String> customDateTimePatterns = new ArrayList<>();

    private final SortedList<Map<Pattern, String>> frequentDatePatterns = new SortedList<>();

    public void addCustomDateTimePattern(String pattern) {
        this.customDateTimePatterns.add(pattern);
    }

    public void addCustomDateTimePatterns(List<String> patterns) {
        this.customDateTimePatterns.addAll(patterns);
    }

    public List<String> getCustomDateTimePattern() {
        return customDateTimePatterns;
    }

    @Override
    public RecognitionResult recognize(String stringToRecognize) {
        return recognize(stringToRecognize, DataTypeEnum.DATE);
    }

    @Override
    public RecognitionResult recognize(String stringToRecognize, DataTypeEnum type) {
        RecognitionResult result = new RecognitionResult();
        if (type != null && !DataTypeEnum.DATE.equals(type)) {
            result.setResult(Collections.singleton(stringToRecognize), false);
            return result;
        }
        if (stringToRecognize != null && stringToRecognize.length() > 6) {
            if (findValueInFrequentDatePatterns(stringToRecognize, result))
                return result;
            final Pair<Set<String>, Map<Pattern, String>> datePatternAfterReplace = CustomDateTimePatternManager
                    .getPatternsAndAssociatedGroup(stringToRecognize);

            result.setResult(
                    CollectionUtils.isNotEmpty(datePatternAfterReplace.getLeft()) ? datePatternAfterReplace.getLeft()
                            : Collections.singleton(stringToRecognize),
                    CollectionUtils.isNotEmpty(datePatternAfterReplace.getLeft()));
            if (MapUtils.isNotEmpty(datePatternAfterReplace.getRight()))
                frequentDatePatterns.addNewValue(datePatternAfterReplace.getRight());

        }
        return result;
    }

    private boolean findValueInFrequentDatePatterns(String stringToRecognize, RecognitionResult result) {
        Set<String> resultSet = new HashSet<>();
        for (int j = 0; j < frequentDatePatterns.size(); j++) {
            Map<Pattern, String> cachedPattern = frequentDatePatterns.get(j).getLeft();
            boolean isFoundRegex = false;
            for (Map.Entry<Pattern, String> entry : cachedPattern.entrySet()) {

                Matcher matcher = entry.getKey().matcher(stringToRecognize);
                if (matcher.find()) {
                    isFoundRegex = true;
                    validateWithPatternInAnyLocale(stringToRecognize, entry.getValue(), matcher)
                            .ifPresent(opt -> resultSet.add(entry.getValue()));
                }
            }
            if (isFoundRegex) {
                if (!resultSet.isEmpty()) {
                    frequentDatePatterns.increment(j);
                }
                result.setResult((!resultSet.isEmpty()) ? resultSet : Collections.singleton(stringToRecognize),
                        !resultSet.isEmpty());
                return true;
            }
        }
        return false;
    }

    @Override
    protected Set<String> getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternStringSet();
    }
}
