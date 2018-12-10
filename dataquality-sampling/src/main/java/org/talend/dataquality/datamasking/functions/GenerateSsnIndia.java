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

import org.talend.dataquality.datamasking.utils.ssn.UtilsSsnIndia;

/**
 * /**
 * 
 * @author dprot
 * 
 * Indian pattern: abbbbbbbbbbc
 * a: 1 -> 9
 * b: 0 -> 9
 * c: checksum with Verhoeff' algorithm
 */
public class GenerateSsnIndia extends Function<String> {

    private static final long serialVersionUID = -8621894245597689328L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        result.append(1 + rnd.nextInt(9));
        for (int i = 0; i < 10; i++) {
            result.append(nextRandomDigit());
        }

        // Add the security key specified for Indian SSN
        result.append(UtilsSsnIndia.computeIndianKey(result));

        return result.toString();
    }
}
