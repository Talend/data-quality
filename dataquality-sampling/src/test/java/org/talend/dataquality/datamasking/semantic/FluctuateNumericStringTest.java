package org.talend.dataquality.datamasking.semantic;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;

public class FluctuateNumericStringTest {

    private FluctuateNumericString fns;

    @Before
    public void setUp() {
        fns = new FluctuateNumericString();
    }

    @Test
    public void consistentMasking() {
        fns.setMaskingMode(FunctionMode.CONSISTENT);
        fns.setSeed("aSeed");
        String result1 = fns.generateMaskedRow("123412341234");
        String result2 = fns.generateMaskedRow("123412341234");
        assertEquals(result2, result1);
    }
}
