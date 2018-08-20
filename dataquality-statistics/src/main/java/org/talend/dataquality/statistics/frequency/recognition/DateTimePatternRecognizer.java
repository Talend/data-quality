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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;
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

    @Deprecated
    public void addCustomDateTimePattern(String pattern) {
        this.customDateTimePatterns.add(pattern);
    }

    @Deprecated
    public void addCustomDateTimePatterns(List<String> patterns) {
        this.customDateTimePatterns.addAll(patterns);
    }

    @Deprecated
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
            final Map<String, Locale> datePatternAfterReplace = CustomDateTimePatternManager.getPatterns(stringToRecognize,
                    frequentDatePatterns);

            result.setResult(MapUtils.isNotEmpty(datePatternAfterReplace) ? datePatternAfterReplace
                    : Collections.singletonMap(stringToRecognize, null), MapUtils.isNotEmpty(datePatternAfterReplace));
        }
        return result;
    }

    @Override
    protected Set<String> getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternStringSet();
    }
}
