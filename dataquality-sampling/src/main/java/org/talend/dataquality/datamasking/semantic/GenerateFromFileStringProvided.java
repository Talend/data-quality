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
package org.talend.dataquality.datamasking.semantic;

import java.io.IOException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.functions.GenerateFromFileString;
import org.talend.dataquality.datamasking.functions.KeysLoader;

public class GenerateFromFileStringProvided extends GenerateFromFileString {

    private static final long serialVersionUID = 8936060786451303843L;

    private static final Logger LOG = LoggerFactory.getLogger(GenerateFromFileStringProvided.class);

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        try {
            genericTokens = KeysLoader.loadKeys(getClass().getResourceAsStream(parameters[0]));
        } catch (IOException | NullPointerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public String generateMaskedRow(String t) {
        if (t == null || EMPTY_STRING.equals(t.trim())) {
            return t;
        }
        return doGenerateMaskedField(t);
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        return super.doGenerateMaskedField(str);
    }
}
