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

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * * Recognize ascii characters given predefined list of Ascii characters and its pattern mappings.
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class LatinExtendedCharPatternRecognizer extends AbstractPatternRecognizer {

    public static final String CHARS_TO_REPLACE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞß0123456789";

    public static final String REPLACEMENT_CHARS = "aaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999";

    private Pattern charsPattern = Pattern.compile("[a-z|A-Z|à-ÿ|À-ß]");

    @Override
    public RecognitionResult recognize(String stringToRecognize, DataTypeEnum type) {
        RecognitionResult result = new RecognitionResult();
        if (StringUtils.isEmpty(stringToRecognize)) {
            result.setResult(Collections.singleton(stringToRecognize), false);
            return result;
        }
        boolean isComplete = true;
        StringBuilder sb = new StringBuilder();
        int n = stringToRecognize.length();
        for (int i = 0; i < n; i++) {
            char c = stringToRecognize.charAt(i);
            int pos = CHARS_TO_REPLACE.indexOf(c);
            if (pos > -1) {
                sb.append(REPLACEMENT_CHARS.charAt(pos));
            } else {
                sb.append(c);
                isComplete = false;
            }
        }
        result.setResult(Collections.singleton(sb.toString()), isComplete);
        return result;
    }

    /**
     * Whether the patternString contains the predefined alpha character.
     * 
     * @param patternString
     * @return
     */
    public boolean containsAlphabetic(String patternString) {
        return charsPattern.matcher(patternString).find();
    }

    @Override
    public Set<String> getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternStringSet();
    }

}
