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
 * created by jgonzalez on 19 juin 2015. This function will generate a correct French phone number.
 */
public class GeneratePhoneNumberFrench extends FunctionString {

    private static final long serialVersionUID = -1118298923509759266L;

    @Override
    protected String doGenerateMaskedField(String str) {
        return doGenerateMaskedFieldWithRandom(str, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        StringBuilder result = new StringBuilder("+33 "); //$NON-NLS-1$
        result.append(r.nextInt(9) + 1);
        for (int i = 0; i < 8; i++) {
            result.append(nextRandomDigit(r));
        }
        return result.toString();
    }

}
