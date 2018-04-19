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
public class BlockingKeyPostAlgorithmEnumTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.BlockingKeyPostAlgorithmEnum#getTypeByValue(java.lang.String)} . case1
     * all of normal case
     */
    @Test
    public void testGetTypeByValue1() {
        for (BlockingKeyPostAlgorithmEnum type : BlockingKeyPostAlgorithmEnum.values()) {
            BlockingKeyPostAlgorithmEnum enumByValue = BlockingKeyPostAlgorithmEnum.getTypeByValue(type.getValue());
            BlockingKeyPostAlgorithmEnum enumByValueLowerCase = BlockingKeyPostAlgorithmEnum
                    .getTypeByValue(type.getValue().toLowerCase());

            BlockingKeyPostAlgorithmEnum enumTypeByComponentValueName = BlockingKeyPostAlgorithmEnum
                    .getTypeBySavedValue(type.getComponentValueName());
            BlockingKeyPostAlgorithmEnum enumTypeByComponentValueNameLowerCase = BlockingKeyPostAlgorithmEnum
                    .getTypeBySavedValue(type.getComponentValueName().toLowerCase());
            BlockingKeyPostAlgorithmEnum enumByIndex = BlockingKeyPostAlgorithmEnum.getTypeByIndex(type.getIndex());

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
     * {@link org.talend.dataquality.record.linkage.utils.BlockingKeyPostAlgorithmEnum#getTypeByValue(java.lang.String)} . case2
     * input is null or empty
     */
    @Test
    public void testGetTypeByValue2() {
        BlockingKeyPostAlgorithmEnum survivorShipAlgorithmEnum1 = BlockingKeyPostAlgorithmEnum.getTypeByValue(null);
        assertNull(survivorShipAlgorithmEnum1);
        survivorShipAlgorithmEnum1 = BlockingKeyPostAlgorithmEnum.getTypeByValue(""); //$NON-NLS-1$
        assertNull(survivorShipAlgorithmEnum1);
    }

    /**
     * Test method for {@link
     * org.talend.dataquality.record.linkage.utils.BlockingKeyPostAlgorithmEnum#getTypeByIndex(java.lang.int)} .
     * case2
     * input is not in the valid range
     */
    @Test
    public void testGetTypeByIndex2() {
        BlockingKeyPostAlgorithmEnum survivorShipAlgorithmEnum1 = BlockingKeyPostAlgorithmEnum.getTypeByIndex(5);
        assertNull(survivorShipAlgorithmEnum1);
        survivorShipAlgorithmEnum1 = BlockingKeyPostAlgorithmEnum.getTypeByIndex(0);
        assertEquals(BlockingKeyPostAlgorithmEnum.NON_ALGO, survivorShipAlgorithmEnum1);
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.utils.BlockingKeyPostAlgorithmEnum#getAllTypes()} .
     */
    @Test
    public void testGetAllTypes() {
        String[] allTypes = BlockingKeyPostAlgorithmEnum.getAllTypes();
        assertNotNull(allTypes);
        assertEquals(4, allTypes.length);
        assertEquals(4, BlockingKeyPostAlgorithmEnum.values().length);
    }
}
