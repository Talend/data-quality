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

import org.apache.commons.lang3.StringUtils;

/**
 * created by jgonzalez on June 22, 2015. This class is used when the requested function is BetweenIndexesKeep. It will
 * return a new String that only contains the input elements that are between the bounds given as parameter.
 *
 */
public class BetweenIndexesKeep extends BetweenIndexes {

    private static final long serialVersionUID = 1913164034646800125L;

    @Override
    protected void initAttributes() {
        beginIndex = Integer.parseInt(parameters[0]) - 1;
        endIndex = Integer.parseInt(parameters[1]);
        toRemove = true;
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (!isValidParameters || StringUtils.isEmpty(str) || beginIndex > str.length())
            return getDefaultOutput();
        if (endIndex > str.length())
            endIndex = str.length();
        return str.substring(beginIndex, endIndex);
    }

    @Override
    protected boolean validParameters() {
        return CharactersOperationUtils.validParameters2Indexes(parameters);
    }
}
