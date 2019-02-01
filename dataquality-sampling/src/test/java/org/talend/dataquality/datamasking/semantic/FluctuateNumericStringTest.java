package org.talend.dataquality.datamasking.semantic;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;

import static org.junit.Assert.assertEquals;

public class FluctuateNumericStringTest {

    private FluctuateNumericString fns;

    @Before
    public void setUp() {
        fns = new FluctuateNumericString();
    }

    @Test
    public void consistentMasking() {
        fns.setSeed("aSeed");
        String result = fns.doGenerateMaskedField("123412341234", FunctionMode.CONSISTENT);
        assertEquals("123030159208", result);
    }
}
