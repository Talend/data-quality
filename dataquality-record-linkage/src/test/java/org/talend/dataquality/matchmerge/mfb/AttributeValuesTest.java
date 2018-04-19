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
package org.talend.dataquality.matchmerge.mfb;

import org.talend.dataquality.matchmerge.AttributeValues;

import junit.framework.TestCase;

public class AttributeValuesTest extends TestCase {

    public void testMostCommon1() throws Exception {
        AttributeValues<String> values = new AttributeValues<String>();
        values.get("BC").increment();
        values.get("AC").increment();
        values.get("AC").increment();
        assertEquals("AC", values.mostCommon());
    }

    public void testMostCommon2() throws Exception {
        AttributeValues<Integer> values = new AttributeValues<Integer>();
        values.get(0).increment();
        values.get(1).increment();
        values.get(1).increment();
        assertEquals(1, (int) values.mostCommon());
    }

    public void testArgs() throws Exception {
        AttributeValues<String> values = new AttributeValues<String>();
        values.get(null).increment();
        assertEquals(1, values.get(null).getOccurrence());
    }

    public void testIncrement() throws Exception {
        AttributeValues<String> values = new AttributeValues<String>();
        values.get("test1").increment();
        assertEquals(1, values.get("test1").getOccurrence());
        values.get("test1").increment();
        assertEquals(2, values.get("test1").getOccurrence());
    }

    public void testIncrementMultipleValues() throws Exception {
        AttributeValues<String> values = new AttributeValues<String>();
        values.get("test1").increment();
        assertEquals(1, values.get("test1").getOccurrence());
        values.get("test2").increment();
        assertEquals(1, values.get("test2").getOccurrence());
        assertEquals(1, values.get("test1").getOccurrence());
        values.get("test1").increment();
        assertEquals(1, values.get("test2").getOccurrence());
        assertEquals(2, values.get("test1").getOccurrence());
        values.get("test2").increment();
        assertEquals(2, values.get("test2").getOccurrence());
        assertEquals(2, values.get("test1").getOccurrence());
    }

    public void testAsList() throws Exception {
        AttributeValues<String> values = new AttributeValues<String>();
        values.get("test1").increment();
        assertEquals(1, values.asList().size());
        assertEquals("test1", values.asList().get(0));
        values.get("test2").increment();
        assertEquals(2, values.asList().size());
        assertTrue(values.asList().contains("test1"));
        assertTrue(values.asList().contains("test2"));
        values.get("test2").increment();
        assertEquals(3, values.asList().size());
        assertTrue(values.asList().contains("test1"));
        assertTrue(values.asList().contains("test2"));
    }

    public void testMerge() throws Exception {
        AttributeValues<String> values1 = new AttributeValues<String>();
        values1.get("test1").increment();
        assertEquals(1, values1.get("test1").getOccurrence());
        AttributeValues<String> values2 = new AttributeValues<String>();
        values2.get("test1").increment();
        values2.get("test2").increment();
        assertEquals(1, values2.get("test1").getOccurrence());
        assertEquals(1, values2.get("test2").getOccurrence());
        values1.merge(values2);
        assertEquals(2, values1.get("test1").getOccurrence());
        assertEquals(1, values1.get("test2").getOccurrence());

        AttributeValues<String> values3 = new AttributeValues<String>();
        values3.get("test2").increment();
        AttributeValues<String> values4 = new AttributeValues<String>();
        values4.get("test2").increment();
        values1.merge(values3);
        values1.merge(values4);

        assertEquals("test2", values1.mostCommon());
    }

    public void testMergeInNew() throws Exception {
        AttributeValues<String> values1 = new AttributeValues<String>();
        values1.get("test1").increment();
        values1.get("test1").increment();
        values1.get("test2").increment();
        AttributeValues<String> values2 = new AttributeValues<String>();
        values2.get("test1").increment();
        values2.get("test2").increment();
        AttributeValues<String> values3 = new AttributeValues<String>(); // New AttributeValues for merge
        values3.merge(values1);
        values3.merge(values2);
        assertEquals(3, values3.get("test1").getOccurrence());
        assertEquals(2, values3.get("test2").getOccurrence());
    }

    public void testDuplicatedValues() throws Exception {
        AttributeValues<String> values1 = new AttributeValues<String>();
        values1.get("test1").increment();
        values1.get("test2").increment();
        values1.get("test1").increment();
        values1.get("test1").increment();
        values1.get("test1").increment();
        values1.get("test2").increment();
        AttributeValues<String> values2 = new AttributeValues<String>();
        values2.get("test1").increment();
        values2.get("test2").increment();
        values2.get("test2").increment();
        values2.get("test2").increment();
        values2.get("test1").increment();
        values2.get("test1").increment();
        values1.merge(values2);
        int iteratorSize = 0;
        for (String value : values1) {
            assertTrue("test1".equals(value) || "test2".equals(value));
            iteratorSize++;
        }
        assertEquals(2, iteratorSize);
    }
}
