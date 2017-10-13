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
package org.talend.dataquality.datamasking.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * created by msjian on 2017.10.11.
 * TDQ-14147: data masking of a column with the content of its semantic type (dictionaries)
 *
 */
public class GenerateFromDictionaries extends Function<String> {

    private static final long serialVersionUID = 1556057898878709265L;

    private static final Logger LOGGER = Logger.getLogger(GenerateFromDictionaries.class);

    protected List<String> genericTokens = new ArrayList<String>();

    @Override
    protected String doGenerateMaskedField(String t) {
        if (!genericTokens.isEmpty()) {
            return genericTokens.get(rnd.nextInt(genericTokens.size()));
        } else {
            return EMPTY_STRING;
        }
    }

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        // TODO
    }

    protected String getOutput(String string) {
        return string;
    }

}
