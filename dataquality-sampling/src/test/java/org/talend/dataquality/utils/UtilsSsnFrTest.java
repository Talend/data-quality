package org.talend.dataquality.utils;

import org.junit.Test;
import org.talend.dataquality.datamasking.utils.ssn.UtilsSsnFr;

import static org.junit.Assert.assertEquals;

public class UtilsSsnFrTest {

    @Test
    public void testControlKeyWithOneDigit() {
        String key = UtilsSsnFr.computeFrenchKey(new StringBuilder("2180898768616"));
        assertEquals('0', key.charAt(0));
    }
}
