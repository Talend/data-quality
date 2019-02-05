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

import org.talend.dataquality.datamasking.utils.ssn.UtilsSsnFr;

import java.util.Random;

/**
 * The first character has a range of (1, 99). The second character has a range of (1, 12). The third character has a
 * range of (1, 95). From the fourth to the ninth character, each character has a range of (0, 9). The last character
 * has a range of (0, 97).<br>
 * So this class proposes a ssn randomly from the range 5,877886263×10¹²<br>
 */
public class GenerateSsnFr extends FunctionString {

    private static final long serialVersionUID = 8845031997964609626L;

    @Override
    protected String doGenerateMaskedField(String str) {
        return doGenerateMaskedFieldWithRandom(str, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        result.append(r.nextInt(2) + 1);

        int year = r.nextInt(99) + 1;
        if (year < 10) {
            result.append("0"); //$NON-NLS-1$
        }
        result.append(year);

        int month = r.nextInt(12) + 1;
        if (month < 10) {
            result.append("0"); //$NON-NLS-1$
        }
        result.append(month);

        int dept = r.nextInt(UtilsSsnFr.getNumberOfFrenchDepartments());
        result.append(UtilsSsnFr.getFrenchDepartments().get(dept));

        // Commune
        result.append(generateTripleN(990, r));

        // Rank of birth
        result.append(generateTripleN(999, r));

        // Add the security key specified for french SSN
        String controlKey = UtilsSsnFr.computeFrenchKey(result);
        result.append(" ").append(controlKey);

        return result.toString();
    }

    private StringBuilder generateTripleN(int bound, Random r) {
        StringBuilder sb = new StringBuilder();
        int number = r.nextInt(bound) + 1;
        if (number < 10) {
            sb.append("00");
        } else if (number < 100) {
            sb.append(0);
        }
        sb.append(number);
        return sb;
    }
}