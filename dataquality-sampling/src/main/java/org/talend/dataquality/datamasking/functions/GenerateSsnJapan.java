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
 * The Japanese ssn has 12 numbers. As we generate every number from 0 to 8 randomly, it can generate 282 429 536 481 (9
 * power 12) ssn numbers.<br>
 * However, every generation is independent, this class cannot guarantee the difference among all the execution.
 */
public class GenerateSsnJapan extends FunctionString {

    private static final long serialVersionUID = -8621894245597689328L;

    @Override
    protected String doGenerateMaskedField(String str) {
        return doGenerateMaskedFieldWithRandom(str, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 12; ++i) {
            result.append(nextRandomDigit(r));
        }
        return result.toString();
    }

}
