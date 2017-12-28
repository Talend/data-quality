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
package org.talend.dataquality.standardization.phone;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * created by msjian on 2017年12月27日
 * Detailled comment
 *
 */
public class PhoneNumberTypeEnumTest {

    /**
     * Test method for {@link org.talend.dataquality.standardization.phone.PhoneNumberTypeEnum#getName()} .
     */
    @Test
    public void testGetName() {
        PhoneNumberTypeEnum[] allTypes = PhoneNumberTypeEnum.values();
        assertNotNull(allTypes);
        assertEquals(12, allTypes.length);

        for (PhoneNumberTypeEnum type : allTypes) {
            assertNotNull(type.getName());
            PhoneNumberTypeEnum enumByValue = PhoneNumberTypeEnum.valueOf(type.name());
            assertNotNull(enumByValue);
        }
    }
}
