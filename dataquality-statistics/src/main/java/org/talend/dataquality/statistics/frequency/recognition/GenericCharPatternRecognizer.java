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

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.common.pattern.TextPatternUtil;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * * Recognize ascii characters given predefined list of Ascii characters and its pattern mappings.
 * 
 * @since 6.1.0
 * @author jteuladedenantes
 */
public class GenericCharPatternRecognizer extends AbstractPatternRecognizer {

    @Override
    public RecognitionResult recognize(String stringToRecognize, DataTypeEnum type) {
        RecognitionResult result = new RecognitionResult();
        if (StringUtils.isEmpty(stringToRecognize)) {
            result.setResult(Collections.singleton(stringToRecognize), false);
            return result;
        }

        String pattern = TextPatternUtil.findPattern(stringToRecognize);
        result.setResult(Collections.singleton(pattern), true);
        return result;
    }

    @Override
    public Set<String> getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternStringSet();
    }

}
