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
package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.generic.Alphabet;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceAllTest {

    private String output;

    private String input = "i86ut val 4"; //$NON-NLS-1$

    private ReplaceAll ra = new ReplaceAll();

    @Test
    public void replaceByParameter() {
        ra.parse("X", false, new Random(42));
        output = ra.generateMaskedRow(input);
        assertEquals("XXXXXXXXXXX", output); //$NON-NLS-1$
    }

    @Test
    public void randomWithSurrogate() {
        ra.parse("", false, new Random(42));
        output = ra.generateMaskedRow("\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3");
        assertEquals(4, output.codePoints().count()); //$NON-NLS-1$
    }

    @Test
    public void consistentWithSurrogate() {
        ra.parse("", false, new RandomWrapper(42));
        output = ra.generateMaskedRow("\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3", FunctionMode.CONSISTENT.name());
        assertEquals(output,
                ra.generateMaskedRow("\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3", FunctionMode.CONSISTENT.name()));
    }

    @Test
    public void bijectiveWithSurrogate() {
        ra.parse("", false, new RandomWrapper(42));
        ra.setFF1Cipher(Alphabet.DEFAULT_LATIN.name(), FormatPreservingMethod.SHA2_HMAC_PRF.name(), "data");
        String input = "abc\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3efgh";
        String output = ra.generateMaskedRow(input, FunctionMode.BIJECTIVE.name());
        assertEquals(input.length(), output.length());
        assertEquals(input.substring(3, 11), output.substring(3, 11));
    }

    @Test
    public void bijectiveTooShortValue() {
        ra.parse("", false, new RandomWrapper(42));
        ra.setFF1Cipher(Alphabet.DEFAULT_LATIN.name(), FormatPreservingMethod.SHA2_HMAC_PRF.name(), "data");
        String input = "a";
        String output = ra.generateMaskedRow(input, FunctionMode.BIJECTIVE.name());
        assertEquals(ra.getDefaultOutput(), output);
    }

    @Test
    public void bijectivity() {
        Alphabet alphabet = Alphabet.DEFAULT_LATIN;
        ra.parse("", false, new RandomWrapper(42));
        ra.setFF1Cipher(alphabet.name(), FormatPreservingMethod.AES_CBC_PRF.name(), "data");
        Set<String> outputSet = new HashSet<>();
        String prefix = "a@";
        String suffix = "z98";
        for (int i = 0; i < alphabet.getRadix(); i++) {
            for (int j = 0; j < alphabet.getRadix(); j++) {
                String input = new StringBuilder().append(prefix).append(Character.toChars(alphabet.getCharactersMap().get(i)))
                        .append(Character.toChars(alphabet.getCharactersMap().get(j))).append(suffix).toString();

                outputSet.add(ra.generateMaskedRow(input, FunctionMode.BIJECTIVE.name()));
            }
        }
        assertEquals((int) Math.pow(alphabet.getRadix(), 2), outputSet.size()); //$NON-NLS-1$
    }

    @Test
    public void emptyReturnsEmpty() {
        ra.setKeepEmpty(true);
        output = ra.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void lettersInParameter() {
        try {
            ra.parse("zi", false, new Random(42));
            fail("should get exception with input " + Arrays.toString(ra.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = ra.generateMaskedRow(input);
        assertEquals("", output); // $NON-NLS-1$
    }

    @Test
    public void noParameter() {
        ra.parse(" ", false, new Random(42));
        output = ra.generateMaskedRow(input);
        assertEquals("ñ38ñï xài 9", output); //$NON-NLS-1$
    }

    @Test
    public void noParameterConsistent() {
        ra.parse(" ", false, new RandomWrapper(42));
        output = ra.generateMaskedRow(input, FunctionMode.CONSISTENT.name());
        assertEquals(output, ra.generateMaskedRow(input, FunctionMode.CONSISTENT.name())); //$NON-NLS-1$
    }

    @Test
    public void noSeedConsistent() {
        ra.parse(" ", false, new RandomWrapper());
        output = ra.generateMaskedRow(input, FunctionMode.CONSISTENT.name());
        assertEquals(output, ra.generateMaskedRow(input, FunctionMode.CONSISTENT.name())); //$NON-NLS-1$
    }
}
