package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.utils.MockRandom;

public class KeepFirstDigitsAndReplaceOtherDigitsTest {

    private String output;

    private String input = "a1b2c3d456"; //$NON-NLS-1$

    private KeepFirstDigitsAndReplaceOtherDigits kfag = new KeepFirstDigitsAndReplaceOtherDigits();

    @Before
    public void setUp() {
        kfag.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        kfag.parse("3", false);
        output = kfag.generateMaskedRow(input);
        assertEquals("a1b2c3d038", output); //$NON-NLS-1$
    }

    @Test
    public void random() {
        kfag.parse("3", false);
        output = kfag.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals("a1b2c3d038", output); //$NON-NLS-1$
    }

    @Test
    public void consistent() {
        kfag.parse("3", false);
        output = kfag.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, kfag.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void bijectiveReplaceOnlyDigits() {
        kfag.parse("2", false);
        kfag.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        String output = kfag.generateMaskedRow(input, FunctionMode.BIJECTIVE);
        assertEquals(input.length(), output.length());
        assertEquals('d', output.charAt(6));
    }

    @Test
    public void bijective() {
        kfag.parse("1", false);
        kfag.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
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
    public void bijectiveReturnsNullIfOneDigitToReplace() {
        kfag.parse("2", false);
        kfag.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        String output = kfag.generateMaskedRow("abc123", FunctionMode.BIJECTIVE);
        assertNull(output);
    }

    @Test
    public void bijectiveReturnsNullIfSmallerThanParam() {
        kfag.parse("2", false);
        kfag.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        String output = kfag.generateMaskedRow("a", FunctionMode.BIJECTIVE);
        assertNull(output);
    }

    @Test
    public void randomReturnsInputIfSmallerThanParam() {
        kfag.parse("2", false);
        kfag.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        String output = kfag.generateMaskedRow("a", FunctionMode.RANDOM);
        assertEquals("a", output);
    }

    @Test
    public void consistentNoSeed() {
        kfag.parse("3", false);
        output = kfag.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, kfag.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void dummyHighParameter() {
        kfag.parse("15", false);
        output = kfag.generateMaskedRow(input);
        assertEquals(input, output);
    }

    @Test
    public void allDigitCanBeGenerated() {
        kfag.setRandom(new MockRandom());
        kfag.parse("10", false);
        output = kfag.generateMaskedRow("01234567899876543210");
        assertEquals("01234567890123456789", output);
    }

}
