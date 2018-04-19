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
package org.talend.dataquality.record.linkage.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * created by msjian on 2017年12月27日
 * Detailled comment
 *
 */
public class HandleNullEnumTest {

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.utils.HandleNullEnum#getTypeByValue(java.lang.String)} . case1
     * all of normal case
     */
    @Test
    public void testGetTypeByValue1() {
        for (HandleNullEnum type : HandleNullEnum.values()) {
            HandleNullEnum handleNullEnumTypeByValue = HandleNullEnum.getTypeByValue(type.getValue());
            HandleNullEnum handleNullEnumTypeByValueLowerCase = HandleNullEnum.getTypeByValue(type.getValue().toLowerCase());
            // Assert no one is null
            assertNotNull(handleNullEnumTypeByValue);
            assertNotNull(handleNullEnumTypeByValueLowerCase);
        }
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.utils.HandleNullEnum#get(java.lang.String)} . case2
     * input is null or empty
     */
    @Test
    public void testGetTypeByValue2() {
        HandleNullEnum handleNullEnum1 = HandleNullEnum.getTypeByValue(null);
        assertNull(handleNullEnum1);
        handleNullEnum1 = HandleNullEnum.getTypeByValue(""); //$NON-NLS-1$
        assertNull(handleNullEnum1);
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.utils.HandleNullEnum#getAllTypes()} .
     */
    @Test
    public void testGetAllTypes() {
        String[] allTypes = HandleNullEnum.getAllTypes();
        assertNotNull(allTypes);
        assertEquals(3, allTypes.length);
        assertEquals(3, HandleNullEnum.values().length);
    }
}
