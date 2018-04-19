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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * created by msjian on 2017年12月27日
 * Detailled comment
 *
 */
public class BlockingKeyPreAlgorithmEnumTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.BlockingKeyPreAlgorithmEnum#getTypeByValue(java.lang.String)} . case1
     * all of normal case
     */
    @Test
    public void testGetTypeByValue1() {
        for (BlockingKeyPreAlgorithmEnum type : BlockingKeyPreAlgorithmEnum.values()) {
            BlockingKeyPreAlgorithmEnum enumByValue = BlockingKeyPreAlgorithmEnum.getTypeByValue(type.getValue());
            BlockingKeyPreAlgorithmEnum enumByValueLowerCase = BlockingKeyPreAlgorithmEnum
                    .getTypeByValue(type.getValue().toLowerCase());

            BlockingKeyPreAlgorithmEnum enumTypeByComponentValueName = BlockingKeyPreAlgorithmEnum
                    .getTypeBySavedValue(type.getComponentValueName());
            BlockingKeyPreAlgorithmEnum enumTypeByComponentValueNameLowerCase = BlockingKeyPreAlgorithmEnum
                    .getTypeBySavedValue(type.getComponentValueName().toLowerCase());
            BlockingKeyPreAlgorithmEnum enumByIndex = BlockingKeyPreAlgorithmEnum.getTypeByIndex(type.getIndex());

            // Assert no one is null
            assertNotNull(enumByValue);
            assertNotNull(enumByValueLowerCase);
            assertNotNull(enumTypeByComponentValueName);
            assertNotNull(enumTypeByComponentValueNameLowerCase);
            assertNotNull(enumByIndex);

            // all of return type is same
            assertTrue(enumByValue.equals(enumTypeByComponentValueName));
            assertTrue(enumByValueLowerCase.equals(enumTypeByComponentValueNameLowerCase));
            assertTrue(enumByValue.equals(enumByIndex));
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.BlockingKeyPreAlgorithmEnum#getTypeByValue(java.lang.String)} . case2
     * input is null or empty
     */
    @Test
    public void testGetTypeByValue2() {
        BlockingKeyPreAlgorithmEnum survivorShipAlgorithmEnum1 = BlockingKeyPreAlgorithmEnum.getTypeByValue(null);
        assertNull(survivorShipAlgorithmEnum1);
        survivorShipAlgorithmEnum1 = BlockingKeyPreAlgorithmEnum.getTypeByValue(""); //$NON-NLS-1$
        assertNull(survivorShipAlgorithmEnum1);
    }

    /**
     * Test method for {@link
     * org.talend.dataquality.record.linkage.utils.BlockingKeyPreAlgorithmEnum#getTypeByIndex(java.lang.int)} .
     * case2
     * input is not in the valid range
     */
    @Test
    public void testGetTypeByIndex2() {
        BlockingKeyPreAlgorithmEnum survivorShipAlgorithmEnum1 = BlockingKeyPreAlgorithmEnum.getTypeByIndex(9);
        assertNull(survivorShipAlgorithmEnum1);
        survivorShipAlgorithmEnum1 = BlockingKeyPreAlgorithmEnum.getTypeByIndex(0);
        assertEquals(BlockingKeyPreAlgorithmEnum.NON_ALGO, survivorShipAlgorithmEnum1);
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.utils.BlockingKeyPreAlgorithmEnum#getAllTypes()} .
     */
    @Test
    public void testGetAllTypes() {
        String[] allTypes = BlockingKeyPreAlgorithmEnum.getAllTypes();
        assertNotNull(allTypes);
        assertEquals(8, allTypes.length);
        assertEquals(8, BlockingKeyPreAlgorithmEnum.values().length);
    }
}
