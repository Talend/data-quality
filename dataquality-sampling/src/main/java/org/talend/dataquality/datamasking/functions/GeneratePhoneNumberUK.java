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

import org.talend.dataquality.datamasking.FunctionMode;

import java.util.Random;

/**
 * created by jgonzalez on 16 juil. 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberUK extends Function<String> {

    private static final long serialVersionUID = -4131614123980116791L;

    @Override
    protected String doGenerateMaskedField(String str, FunctionMode mode) {
        Random r = rnd;
        if (FunctionMode.CONSISTENT == mode)
            r = getRandomForString(str);

        return doGenerateMaskedField(str, r);
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        return doGenerateMaskedField(str, rnd);
    }

    private String doGenerateMaskedField(String str, Random r) {
        StringBuilder result = new StringBuilder("020 3"); //$NON-NLS-1$
        addRandomDigit(result, 3, r);
        result.append(" "); //$NON-NLS-1$
        addRandomDigit(result, 4, r);
        return result.toString();
    }

    /**
     * Add random digit by special size
     * 
     * @param result
     */
    private void addRandomDigit(StringBuilder result, int size, Random r) {
        for (int i = 0; i < size; ++i) {
            result.append(nextRandomDigit(r));
        }
    }

}
