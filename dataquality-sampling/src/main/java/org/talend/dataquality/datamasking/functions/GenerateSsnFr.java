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

import org.talend.dataquality.datamasking.utils.utilsSsnFr;

/**
 * The first character has a range of (1, 99). The second character has a range of (1, 12). The third character has a
 * range of (1, 95). From the fourth to the ninth character, each character has a range of (0, 9). The last character
 * has a range of (0, 97).<br>
 * So this class proposes a ssn randomly from the range 5,877886263×10¹²<br>
 */
public class GenerateSsnFr extends Function<String> {

    private static final long serialVersionUID = 8845031997964609626L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        result.append(rnd.nextInt(2) + 1);
        int yy = rnd.nextInt(99) + 1;
        if (yy < 10) {
            result.append("0"); //$NON-NLS-1$
        }
        result.append(yy);
        int mm = rnd.nextInt(12) + 1;
        if (mm < 10) {
            result.append("0"); //$NON-NLS-1$
        }
        result.append(mm);
        int ll = rnd.nextInt(utilsSsnFr.getNumberOfFrenchDepartments());
        result.append(utilsSsnFr.getFrenchDepartments().get(ll));
        for (int i = 0; i < 6; ++i) {
            result.append(nextRandomDigit());
        }

        String controlKey = utilsSsnFr.computeFrenchKey(result.toString());
        result.append(" ").append(controlKey);

        return result.toString();
    }
}
