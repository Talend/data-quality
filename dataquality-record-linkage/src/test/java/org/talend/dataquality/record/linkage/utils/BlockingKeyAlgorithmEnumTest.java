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
public class BlockingKeyAlgorithmEnumTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum#getTypeByValue(java.lang.String)} . case1
     * all of normal case
     */
    @Test
    public void testGetTypeByValue1() {
        for (BlockingKeyAlgorithmEnum type : BlockingKeyAlgorithmEnum.values()) {
            BlockingKeyAlgorithmEnum enumByValue = BlockingKeyAlgorithmEnum.getTypeByValue(type.getValue());
            BlockingKeyAlgorithmEnum enumByValueLowerCase = BlockingKeyAlgorithmEnum
                    .getTypeByValue(type.getValue().toLowerCase());

            BlockingKeyAlgorithmEnum enumTypeByComponentValueName = BlockingKeyAlgorithmEnum
                    .getTypeBySavedValue(type.getComponentValueName());
            BlockingKeyAlgorithmEnum enumTypeByComponentValueNameLowerCase = BlockingKeyAlgorithmEnum
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
     * {@link org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum#getTypeByValue(java.lang.String)} . case2
     * input is null or empty
     */
    @Test
    public void testGetTypeByValue2() {
        BlockingKeyAlgorithmEnum blockingKeyAlgorithmEnum1 = BlockingKeyAlgorithmEnum.getTypeByValue(null);
        assertNull(blockingKeyAlgorithmEnum1);
        blockingKeyAlgorithmEnum1 = BlockingKeyAlgorithmEnum.getTypeByValue(""); //$NON-NLS-1$
        assertNull(blockingKeyAlgorithmEnum1);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum#getTypeBySavedValue(java.lang.String)} . case2
     * input is null or empty
     */
    @Test
    public void testGetTypeBySavedValue2() {
        BlockingKeyAlgorithmEnum blockingKeyAlgorithmEnum1 = BlockingKeyAlgorithmEnum.getTypeBySavedValue(null);
        assertEquals(blockingKeyAlgorithmEnum1.EXACT, blockingKeyAlgorithmEnum1);
        blockingKeyAlgorithmEnum1 = BlockingKeyAlgorithmEnum.getTypeBySavedValue(""); //$NON-NLS-1$
        assertEquals(blockingKeyAlgorithmEnum1.EXACT, blockingKeyAlgorithmEnum1);
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum#getAllTypes()} .
     */
    @Test
    public void testGetAllTypes() {
        String[] allTypes = BlockingKeyAlgorithmEnum.getAllTypes();
        assertNotNull(allTypes);
        assertEquals(15, allTypes.length);
        assertEquals(15, BlockingKeyAlgorithmEnum.values().length);
    }
}
