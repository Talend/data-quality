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
package org.talend.dataquality.common.inference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.common.exception.DQCommonRuntimeException;

public class ResizableListTest {

    private ResizableList<Item> list;

    @Before
    public void setUp() throws Exception {
        list = new ResizableList<>(Item.class); // Init fixture
    }

    @After
    public void tearDown() {
        Item.NEXT_INDEX = 0; // Reset next index when a test is over
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArgument() throws Exception {
        new ResizableList<>(InvalidItem.class);
    }

    @Test
    public void testResizeZero() throws Exception {
        assertEquals(0, list.size());
        list.resize(0); // the original size is 0, so Resize to 0 should do nothing
        assertEquals(0, list.size());
    }

    @Test
    public void testResizeOne() throws Exception {
        list.resize(1); // Resize to 1 should add 1 item and 0 should be Item::index
        assertEquals(1, list.size());
        assertEquals(0, list.get(0).getIndex());
    }

    @Test
    public void testResizeTwo() throws Exception {
        list.resize(2); // Resize to 2 should add 1 new item and 1 should be Item::index
        assertEquals(2, list.size());
        assertEquals(1, list.get(1).getIndex());
    }

    @Test(expected = DQCommonRuntimeException.class)
    public void testResizeMinusOne() throws Exception {
        try {
            list.resize(-1);
        } catch (DQCommonRuntimeException e) {
            assertEquals("Unable to resize list of items.", e.getMessage());
            throw e;
        }
    }

    @Test
    public void testResizeDown() throws Exception {
        list.resize(2); // Resize to 2 should add 2 items and 0 and 1 as Item::index
        assertEquals(2, list.size());
        assertEquals(0, list.get(0).getIndex());
        assertEquals(1, list.get(1).getIndex());
        list.resize(1); // Resize to 2 should have no effect
        assertEquals(2, list.size());
        assertEquals(0, list.get(0).getIndex());
        assertEquals(1, list.get(1).getIndex());
    }

    @Test
    public void testResizeReturn() throws Exception {
        assertFalse(list.resize(0)); // List has already size = 0, no new item added
        assertTrue(list.resize(1)); // List hasn't size = 1, a new item added
        assertFalse(list.resize(1)); // List has already size = 1, no new item added
    }

    @Test
    public void testResizeReturnDown() throws Exception {
        assertFalse(list.resize(0)); // List has already size = 0, no new item added
        assertTrue(list.resize(2)); // List hasn't size = 2, 2 new items added
        assertFalse(list.resize(1)); // List has already size > 1, no new item added
    }

    // Test class for asserts on invalid input for resizable list
    public static class InvalidItem {

        private final String value;

        public InvalidItem(String value) {
            this.value = value;
        }
    }

    // Test class for asserts on creations
    public static class Item {

        public static int NEXT_INDEX = 0;

        int index;

        public Item() {
            index = NEXT_INDEX++;
        }

        public int getIndex() {
            return index;
        }
    }
}
