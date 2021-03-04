// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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
import org.talend.dataquality.statistics.type.DataTypeEnum;

public class EmptyPatternRecognizer extends AbstractPatternRecognizer {

    @Override
    public RecognitionResult recognize(String stringToRecognize, DataTypeEnum type) {
        RecognitionResult result = new RecognitionResult();
        if (stringToRecognize == null) {
            result.setResult(Collections.emptySet(), true);
        } else if (StringUtils.isBlank(stringToRecognize)) {
            result.setResult(Collections.singleton(StringUtils.EMPTY), true);
        } else {
            result.setResult(Collections.singleton(stringToRecognize), false);
        }
        return result;
    }

    @Override
    protected Set<String> getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternStringSet();
    }

}
