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

/**
 * created by jgonzalez on 16 juil. 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberUS extends Function<String> {

    private static final long serialVersionUID = 1160032103743243299L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        result.append(rnd.nextInt(8) + 2);
        result.append(nextRandomDigit());
        result.append(nextDigitWithExclusion(result.charAt(1)));
        result.append("-"); //$NON-NLS-1$
        result.append(rnd.nextInt(8) + 2);
        result.append(nextRandomDigit());
        if (result.charAt(5) == 1) {
            result.append(nextDigitWithExclusion('1'));
        } else {
            result.append(nextRandomDigit());
        }
        result.append("-"); //$NON-NLS-1$
        for (int i = 0; i < 4; ++i) {
            result.append(nextRandomDigit());
        }
        return result.toString();
    }

    private int nextDigitWithExclusion(char exclusion) {
        int tmp;
        do {
            tmp = nextRandomDigit();
        } while (tmp == exclusion - '0');
        return tmp;
    }
}
