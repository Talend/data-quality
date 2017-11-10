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
import java.util.Set;

import org.talend.dataquality.statistics.datetime.CustomDateTimePatternManager;
import org.talend.dataquality.statistics.datetime.SystemDateTimePatternManager;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * Recognize date types given the predefined date regex pattern.
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class DateTimePatternRecognizer extends AbstractPatternRecognizer {

    private List<String> customDateTimePatterns = new ArrayList<>();

    public void addCustomDateTimePattern(String pattern) {
        //TDQ-14421 replace 'y' with 'u' if the pattern without 'G'.
        String correctPattern = SystemDateTimePatternManager.patternReplaceY(pattern);
        this.customDateTimePatterns.add(correctPattern);
    }

    public void addCustomDateTimePatterns(List<String> patterns) {
        patterns.forEach(pattern -> {
            addCustomDateTimePattern(pattern);
        });
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
            final Set<String> datePatternAfterReplace = CustomDateTimePatternManager.replaceByDateTimePattern(stringToRecognize,
                    customDateTimePatterns);
            if (datePatternAfterReplace.isEmpty()) {
                // Did not recognized.
                result.setResult(Collections.singleton(stringToRecognize), false);
            } else {
                result.setResult(datePatternAfterReplace, true);
            }
        }
        return result;
    }

    @Override
    protected Set<String> getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternStringSet();
    }
}
