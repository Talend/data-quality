package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;
import org.talend.dataquality.duplicating.RandomWrapper;
import org.talend.dataquality.utils.MockRandom;

public class KeepLastDigitsAndReplaceOtherDigitsTest {

    private String output;

    private String input = "a1b2c3d456"; //$NON-NLS-1$

    private KeepLastDigitsAndReplaceOtherDigits kfag = new KeepLastDigitsAndReplaceOtherDigits();

    @Test
    public void testGood() {
        kfag.parse("3", false, new Random(42));
        output = kfag.generateMaskedRow(input);
        assertEquals("a8b3c0d456", output); //$NON-NLS-1$
    }

    @Test
    public void consistent() {
        kfag.parse("3", false, new RandomWrapper(42));
        output = kfag.generateMaskedRow(input, true);
        assertEquals(output, kfag.generateMaskedRow(input, true)); //$NON-NLS-1$
    }

    @Test
    public void consistentNoSeed() {
        kfag.parse("3", false, new RandomWrapper());
        output = kfag.generateMaskedRow(input, true);
        assertEquals(output, kfag.generateMaskedRow(input, true)); //$NON-NLS-1$
    }

    @Test
    public void testGoodTwice() {
        kfag.parse("4", false, new Random(42));
        output = kfag.generateMaskedRow(input);
        assertEquals("a3b0c3d456", output); //$NON-NLS-1$
        output = kfag.generateMaskedRow(input);
        assertEquals("a4b8c3d456", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        kfag.parse("15", false, new Random(542));
        output = kfag.generateMaskedRow(input);
        assertEquals(input, output);
    }

    @Test
    public void testDummyGoodExactSize() {
        kfag.parse("10", false, new Random(542));
        output = kfag.generateMaskedRow(input);
        assertEquals(input, output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        kfag.parse("10", false, new MockRandom());
        output = kfag.generateMaskedRow("01234567890123456789");
        assertEquals("98765432100123456789", output);
    }
}
