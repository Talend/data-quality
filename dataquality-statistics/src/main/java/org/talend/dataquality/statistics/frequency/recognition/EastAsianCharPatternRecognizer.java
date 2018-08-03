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
import org.talend.dataquality.common.regex.ChainResponsibilityHandler;
import org.talend.dataquality.common.regex.HandlerFactory;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * * Recognize East Asia characters given predefined list of characters and its pattern mappings.
 * 
 * @since 1.3.0
 * @author mzhao
 */
@Deprecated
public class EastAsianCharPatternRecognizer extends AbstractPatternRecognizer {

    private final ChainResponsibilityHandler createEastAsiaPatternHandler = HandlerFactory.createEastAsiaPatternHandler();

    @Override
    public RecognitionResult recognize(String stringToRecognize, DataTypeEnum type) {
        RecognitionResult result = new RecognitionResult();
        if (StringUtils.isEmpty(stringToRecognize)) {
            result.setResult(Collections.singleton(stringToRecognize), false);
            return result;
        }
        // since the current implementation of East Asia character replacement is using regex macher , there is no way
        // to get the "isComplete" status during the process. So here the status simply deemed as "not complete yet".
        result.setResult(Collections.singleton(createEastAsiaPatternHandler.handleRequest(stringToRecognize)), true);
        return result;
    }

    @Override
    protected Set<String> getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternStringSet();
    }

}
