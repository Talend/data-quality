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
 */
public class GeneratePhoneNumberJapan extends FunctionString {

    private static final long serialVersionUID = -1152538201280991701L;

    @Override
    protected String doGenerateMaskedField(String str) {
        return doGenerateMaskedFieldWithRandom(str, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        StringBuilder result = new StringBuilder("03-"); //$NON-NLS-1$
        addFourRandomDigit(result, r);
        result.append("-"); //$NON-NLS-1$
        addFourRandomDigit(result, r);
        return result.toString();
    }

    /**
     * Add next four random digits.
     *
     * @param result
     */
    private void addFourRandomDigit(StringBuilder result, Random r) {
        for (int i = 0; i < 4; ++i) {
            result.append(nextRandomDigit(r));
        }
    }
}
