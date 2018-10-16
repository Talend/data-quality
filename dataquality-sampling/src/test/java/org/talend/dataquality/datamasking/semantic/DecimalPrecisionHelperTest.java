package org.talend.dataquality.datamasking.semantic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class DecimalPrecisionHelperTest {

    @Test
    public void testGetDecimalPrecision() {
        assertEquals(1, DecimalPrecisionHelper.getDecimalPrecision("0.2"));
        assertEquals(0, DecimalPrecisionHelper.getDecimalPrecision("123"));
        assertEquals(3, DecimalPrecisionHelper.getDecimalPrecision("123.456E10"));
    }

    @Test
    public void testGetDecimalPrecisionOnInvalidInput() {
        try {
            DecimalPrecisionHelper.getDecimalPrecision("azerty");
            fail("NumberFormatException is not thrown as expected");
        } catch (Exception e) {
            assertEquals(NumberFormatException.class, e.getClass());
        }
    }

}
