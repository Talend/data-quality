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

import java.util.Random;

/**
 * created by jgonzalez on 16 juil. 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberUS extends FunctionString {

    private static final long serialVersionUID = 1160032103743243299L;

    @Override
    protected String doGenerateMaskedField(String str) {
        return doGenerateMaskedFieldWithRandom(str, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        result.append(r.nextInt(8) + 2);
        result.append(nextRandomDigit(r));
        result.append(nextDigitWithExclusion(result.charAt(1), r));
        result.append("-"); //$NON-NLS-1$
        result.append(r.nextInt(8) + 2);
        result.append(nextRandomDigit(r));
        if (result.charAt(5) == 1) {
            result.append(nextDigitWithExclusion('1', r));
        } else {
            result.append(nextRandomDigit(r));
        }
        result.append("-"); //$NON-NLS-1$
        for (int i = 0; i < 4; ++i) {
            result.append(nextRandomDigit(r));
        }
        return result.toString();
    }

    private int nextDigitWithExclusion(char exclusion, Random r) {
        int tmp;
        do {
            tmp = nextRandomDigit(r);
        } while (tmp == exclusion - '0');
        return tmp;
    }
}
