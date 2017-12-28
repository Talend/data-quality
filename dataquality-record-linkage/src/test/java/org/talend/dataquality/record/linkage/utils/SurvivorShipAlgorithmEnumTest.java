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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * created by msjian on 2017年12月27日
 * Detailled comment
 *
 */
public class SurvivorShipAlgorithmEnumTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum#getTypeByValue(java.lang.String)} . case1
     * all of normal case
     */
    @Test
    public void testGetTypeByValue1() {
        for (SurvivorShipAlgorithmEnum type : SurvivorShipAlgorithmEnum.values()) {
            SurvivorShipAlgorithmEnum enumByValue = SurvivorShipAlgorithmEnum.getTypeByValue(type.getValue());
            SurvivorShipAlgorithmEnum enumByValueLowerCase = SurvivorShipAlgorithmEnum
                    .getTypeByValue(type.getValue().toLowerCase());

            SurvivorShipAlgorithmEnum enumTypeByComponentValueName = SurvivorShipAlgorithmEnum
                    .getTypeBySavedValue(type.getComponentValueName());
            SurvivorShipAlgorithmEnum enumTypeByComponentValueNameLowerCase = SurvivorShipAlgorithmEnum
                    .getTypeBySavedValue(type.getComponentValueName().toLowerCase());

            // Assert no one is null
            assertNotNull(enumByValue);
            assertNotNull(enumByValueLowerCase);
            assertNotNull(enumTypeByComponentValueName);
            assertNotNull(enumTypeByComponentValueNameLowerCase);

            // all of return type is same
            assertTrue(enumByValue.equals(enumTypeByComponentValueName));
            assertTrue(enumByValueLowerCase.equals(enumTypeByComponentValueNameLowerCase));

        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum#getTypeByValue(java.lang.String)} . case2
     * input is null or empty
     */
    @Test
    public void testGetTypeByValue2() {
        SurvivorShipAlgorithmEnum survivorShipAlgorithmEnum1 = SurvivorShipAlgorithmEnum.getTypeByValue(null);
        assertNull(survivorShipAlgorithmEnum1);
        survivorShipAlgorithmEnum1 = SurvivorShipAlgorithmEnum.getTypeByValue(""); //$NON-NLS-1$
        assertNull(survivorShipAlgorithmEnum1);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum#getTypeBySavedValue(java.lang.String)} . case2
     * input is null or empty
     */
    @Test
    public void testGetTypeBySavedValue2() {
        SurvivorShipAlgorithmEnum survivorShipAlgorithmEnum1 = SurvivorShipAlgorithmEnum.getTypeBySavedValue(null);
        assertNull(survivorShipAlgorithmEnum1);
        survivorShipAlgorithmEnum1 = SurvivorShipAlgorithmEnum.getTypeBySavedValue(""); //$NON-NLS-1$
        assertNull(survivorShipAlgorithmEnum1);
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum#getAllTypes()} .
     */
    @Test
    public void testGetAllTypes() {
        String[] allTypes = SurvivorShipAlgorithmEnum.getAllTypes();
        assertNotNull(allTypes);
        assertEquals(11, allTypes.length);
        assertEquals(11, SurvivorShipAlgorithmEnum.values().length);
    }
}
