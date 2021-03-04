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

import java.util.Set;

import org.talend.dataquality.statistics.type.DataTypeEnum;

public abstract class AbstractPatternRecognizer {

    /**
     * Recognize the string pattern and the completeness status.
     * 
     * @param stringToRecognize the string whose pattern is to be recognized. default to DataTypeEnum.STRING
     * @return recognition result with completeness status.
     */
    public RecognitionResult recognize(String stringToRecognize) {
        return recognize(stringToRecognize, DataTypeEnum.STRING);
    }

    /**
     * Recognize the string pattern and the completeness status.
     * 
     * @param stringToRecognize the string whose pattern is to be recognized. default to DataTypeEnum.STRING
     * @param type the type of the data to recognize
     * @return recognition result with completeness status.
     */
    public abstract RecognitionResult recognize(String stringToRecognize, DataTypeEnum type);

    protected abstract Set<String> getValuePattern(String originalValue);

}
