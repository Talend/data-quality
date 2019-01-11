package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.duplicating.RandomWrapper;
import org.talend.dataquality.utils.MockRandom;

public class KeepLastDigitsAndReplaceOtherDigitsTest {

    private String output;

    private String input = "a1b2c3d456"; //$NON-NLS-1$

    private KeepLastDigitsAndReplaceOtherDigits kfag = new KeepLastDigitsAndReplaceOtherDigits();

    @Test
    public void defaultBehavior() {
        kfag.parse("3", false, new Random(42));
        output = kfag.generateMaskedRow(input);
        assertEquals("a8b3c0d456", output); //$NON-NLS-1$
    }

    @Test
    public void random() {
        kfag.parse("3", false, new Random(42));
        output = kfag.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals("a8b3c0d456", output); //$NON-NLS-1$
    }

    @Test
    public void consistent() {
        kfag.parse("3", false, new RandomWrapper(42));
        output = kfag.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, kfag.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void consistentNoSeed() {
        kfag.parse("3", false, new RandomWrapper());
        output = kfag.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, kfag.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void bijectiveReplaceOnlyDigits() {
        kfag.parse("2", false, new RandomWrapper(42));
        kfag.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        String output = kfag.generateMaskedRow(input, FunctionMode.BIJECTIVE);
        assertEquals(input.length(), output.length());
        assertEquals('b', output.charAt(2));
    }

    @Test
    public void bijective() {
        kfag.parse("3", false, new RandomWrapper(42));
        kfag.setSecret(FormatPreservingMethod.AES_CBC_PRF, "data");
        Set<String> outputSet = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            StringBuilder sb = new StringBuilder();
            if (i < 10) {
                sb.append("00");
            } else if (i < 100) {
                sb.append(0);
            }
            String input = sb.append(i).toString();
            outputSet.add(kfag.generateMaskedRow(input, FunctionMode.BIJECTIVE));
        }
        assertEquals(1000, outputSet.size()); //$NON-NLS-1$
    }

    @Test
    public void dummyHighParameter() {
        kfag.parse("15", false, new Random(542));
        output = kfag.generateMaskedRow(input);
        assertEquals(input, output);
    }

    @Test
    public void dummyExactSizeParameter() {
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
