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
 * The class generates the American ssn number randomly.<br>
 * The first two characters has 72 different combinations. The the following characters, whether is 00, whether is 06,
 * whether is an integer from 0 to 8, so it has 11 permutations. The following character has 9 combinations. The
 * following character has 9 combinations too. The end four characters, it can generate at least 5832 combinations. <br>
 * In totoal, it has 374 134 464 results.<br>
 */
public class GenerateSsnUs extends Function<String> {

    private static final long serialVersionUID = -7651076296534530622L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        result.append(rnd.nextInt(8));
        result.append(rnd.nextInt(9));
        if (result.charAt(0) == '0' && result.charAt(1) == '0' || result.charAt(0) == '6' && result.charAt(1) == '6') {
            int tmp;
            do {
                tmp = rnd.nextInt(9);
            } while ((char) tmp == result.charAt(0));
            result.append(tmp);
        } else {
            result.append(rnd.nextInt(9));
        }
        result.append("-"); //$NON-NLS-1$
        result.append(rnd.nextInt(9));
        if (result.charAt(4) == '0') {
            result.append(rnd.nextInt(8) + 1);
        } else {
            result.append(rnd.nextInt(9));
        }
        result.append("-"); //$NON-NLS-1$
        for (int i = 0; i < 3; ++i) {
            result.append(rnd.nextInt(9));
        }
        if (result.charAt(7) == '0' && result.charAt(8) == '0' && result.charAt(9) == '0') {
            result.append(rnd.nextInt(8) + 1);
        } else {
            result.append(rnd.nextInt(9));
        }
        return result.toString();
    }

}
