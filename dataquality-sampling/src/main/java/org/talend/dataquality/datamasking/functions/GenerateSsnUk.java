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

import java.util.HashSet;
import java.util.Random;

import org.talend.dataquality.datamasking.semantic.DataMaskFunction;

/**
 * This class generates a ssn number randomly of United Kingdom.<br>
 * There are 373 combinations for the first two characters. Then the the number part, it generates 531441 (9 power 6)
 * combinations. It has 4 characters to choose from in the last position. In total, it has 792 909 972 results.<br>
 */
public class GenerateSsnUk extends DataMaskFunction {

    private static final long serialVersionUID = 4664211523958436354L;

    private static String first = "AZERTYOPSGHJKLMWXCBN"; //$NON-NLS-1$

    private static String second = "AZERTYPSGHJKLMWXCBN"; //$NON-NLS-1$

    private static HashSet<String> forbid = new HashSet<>();

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        forbid.add("BG"); //$NON-NLS-1$
        forbid.add("GB"); //$NON-NLS-1$
        forbid.add("NK"); //$NON-NLS-1$
        forbid.add("KN"); //$NON-NLS-1$
        forbid.add("NT"); //$NON-NLS-1$
        forbid.add("TN"); //$NON-NLS-1$
        forbid.add("ZZ"); //$NON-NLS-1$
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        StringBuilder prefix;
        char tmp;
        do {
            prefix = new StringBuilder(EMPTY_STRING);
            tmp = first.charAt(rnd.nextInt(20));
            prefix.append(tmp);
            tmp = second.charAt(rnd.nextInt(19));
            prefix.append(tmp);
        } while (getForbid().contains(prefix.toString()));
        result.append(prefix);
        result.append(" "); //$NON-NLS-1$
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 2; ++j) {
                result.append(rnd.nextInt(9));
            }
            result.append(" "); //$NON-NLS-1$
        }
        result.append(UPPER.charAt(rnd.nextInt(4)));
        return result.toString();
    }

    public static HashSet<String> getForbid() {
        return forbid;
    }
}
