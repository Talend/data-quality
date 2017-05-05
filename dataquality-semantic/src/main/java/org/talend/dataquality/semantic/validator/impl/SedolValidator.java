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
package org.talend.dataquality.semantic.validator.impl;

import org.apache.commons.lang3.StringUtils;
import org.talend.dataquality.semantic.validator.ISemanticSubValidator;

/**
 * SEDOL validator using checksum algorithm from http://rosettacode.org/wiki/SEDOLs#Java
 * 
 * @since 1.3.0
 * @author mzhao
 *
 */
public class SedolValidator implements ISemanticSubValidator {

    private static final int[] mult = { 1, 3, 1, 7, 3, 9 };

    @Override
    public boolean isValid(String str) {
        if (str == null || str.length() != 7) {
            return false;
        }
        String sedolStr = StringUtils.left(str, 6);
        // Extract the checksum digit.
        int checksum = -1;
        try {
            String csStr = StringUtils.right(str, 1);
            checksum = Integer.valueOf(csStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid checksum digit. ", e);
        }
        int checksumFromSedol = getSedolCheckDigit(sedolStr);
        return checksum == checksumFromSedol;
    }

    public int getSedolCheckDigit(String str) {
        String strUpper = str.toUpperCase();
        int total = 0;
        for (int i = 0; i < 6; i++) {
            char s = strUpper.charAt(i);
            total += Character.digit(s, 36) * mult[i];
        }
        return (10 - (total % 10)) % 10;
    }

}
