package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;
import org.talend.dataquality.sampling.exception.DQException;

public class KeepLastDigitsAndReplaceOtherDigitsTest {

    private String output;

    private String input = "a1b2c3d456"; //$NON-NLS-1$

    private KeepLastDigitsAndReplaceOtherDigits kfag = new KeepLastDigitsAndReplaceOtherDigits();

    @Test
    public void testGood() throws DQException {
        kfag.parse("3", false, new Random(42));
        output = kfag.generateMaskedRow(input);
        assertEquals("a0b3c8d456", output); //$NON-NLS-1$
    }

    @Test
    public void testGoodTwice() throws DQException {
        kfag.parse("4", false, new Random(42));
        output = kfag.generateMaskedRow(input);
        assertEquals("a3b8c3d456", output); //$NON-NLS-1$
        output = kfag.generateMaskedRow(input);
        assertEquals("a8b0c3d456", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() throws DQException {
        kfag.parse("15", false, new Random(542));
        output = kfag.generateMaskedRow(input);
        assertEquals(input, output);
    }

    @Test
    public void testDummyGoodExactSize() throws DQException {
        kfag.parse("10", false, new Random(542));
        output = kfag.generateMaskedRow(input);
        assertEquals(input, output);
    }
}
