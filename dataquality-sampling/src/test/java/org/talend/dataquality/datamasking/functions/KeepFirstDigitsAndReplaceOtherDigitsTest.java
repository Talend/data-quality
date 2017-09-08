package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;
import org.talend.dataquality.sampling.exception.DQException;

public class KeepFirstDigitsAndReplaceOtherDigitsTest {

    private String output;

    private String input = "a1b2c3d456"; //$NON-NLS-1$

    private KeepFirstDigitsAndReplaceOtherDigits kfag = new KeepFirstDigitsAndReplaceOtherDigits();

    @Test
    public void testGood() throws DQException {
        kfag.parse("3", false, new Random(42));
        output = kfag.generateMaskedRow(input);
        assertEquals("a1b2c3d830", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() throws DQException {
        kfag.parse("15", false, new Random(542));
        output = kfag.generateMaskedRow(input);
        assertEquals(input, output);
    }

}
