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
 * The class generates the American ssn number randomly.<br>
 * The first two characters has 72 different combinations. The the following characters, whether is 00, whether is 06,
 * whether is an integer from 0 to 8, so it has 11 permutations. The following character has 9 combinations. The
 * following character has 9 combinations too. The end four characters, it can generate at least 5832 combinations. <br>
 * In totoal, it has 374 134 464 results.<br>
 */
public class GenerateSsnUs extends FunctionString {

    private static final long serialVersionUID = -7651076296534530622L;

    @Override
    protected String doGenerateMaskedField(String str) {
        return this.doGenerateMaskedFieldWithRandom(str, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        StringBuilder result = new StringBuilder();
        result.append(generateFirstGroup(r));
        result.append("-"); //$NON-NLS-1$
        result.append(generateSecondGroup(r));
        result.append("-"); //$NON-NLS-1$
        result.append(generateThirdGroup(r));
        return result.toString();
    }

    private String generateFirstGroup(Random r) {
        int firstNumber;
        do {
            firstNumber = r.nextInt(900);
        } while (firstNumber == 0 || firstNumber == 666);
        return String.format("%03d", firstNumber);
    }

    private String generateSecondGroup(Random r) {
        int secondNumber = r.nextInt(99) + 1;
        return String.format("%02d", secondNumber);
    }

    private String generateThirdGroup(Random r) {
        int value = r.nextInt(9999) + 1;
        return String.format("%04d", value);
    }
}
