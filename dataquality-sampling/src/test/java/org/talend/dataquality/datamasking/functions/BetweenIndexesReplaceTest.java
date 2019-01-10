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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.generic.Alphabet;
import org.talend.dataquality.duplicating.RandomWrapper;

import static org.junit.Assert.*;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class BetweenIndexesReplaceTest {

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    private BetweenIndexesReplace bir = new BetweenIndexesReplace();

    @Before
    public void tearUp() {
        bir.setRandom(new Random(42));
    }

    @Test
    public void random() {
        bir.parse("2, 4, X", false, new Random(42));
        output = bir.generateMaskedRow(input, FunctionMode.RANDOM.name());
        assertEquals("SXXXe", output); //$NON-NLS-1$
    }

    @Test
    public void consistent() {
        bir.parse("2, 4", false, new RandomWrapper(42));
        output = bir.generateMaskedRow(input, FunctionMode.CONSISTENT.name());
        assertEquals(output, bir.generateMaskedRow(input, FunctionMode.CONSISTENT.name())); //$NON-NLS-1$
    }

    @Test
    public void bijectiveReplaceOnlyValidCharacters() {
        bir.parse("2, 4", false, new RandomWrapper(42));
        bir.setFF1Cipher(Alphabet.DEFAULT_LATIN.name(), FormatPreservingMethod.SHA2_HMAC_PRF.name(), "data");
        String output = bir.generateMaskedRow("St€ve", FunctionMode.BIJECTIVE.name());
        assertEquals(input.length(), output.length());
        assertEquals('€', output.charAt(2));
    }

    @Test
    public void bijective() {
        Alphabet alphabet = Alphabet.DEFAULT_LATIN;
        bir.parse("2, 4", false, new RandomWrapper(42));
        bir.setFF1Cipher(alphabet.name(), FormatPreservingMethod.AES_CBC_PRF.name(), "data");
        Set<String> outputSet = new HashSet<>();
        String prefix = "a@";
        String suffix = "z98";
        for (int i = 0; i < alphabet.getRadix(); i++) {
            for (int j = 0; j < alphabet.getRadix(); j++) {
                String input = new StringBuilder().append(prefix).append(Character.toChars(alphabet.getCharactersMap().get(i)))
                        .append(Character.toChars(alphabet.getCharactersMap().get(j))).append(suffix).toString();
                String output = bir.generateMaskedRow(input, FunctionMode.BIJECTIVE.name());
                assertTrue("This output is already present : " + output + "\nInput : " + input + "\nIndex : " + (i + j),
                        outputSet.add(output));
            }
        }
    }

    @Test
    public void consistentNoSeed() {
        bir.parse("2, 4", false, new RandomWrapper());
        output = bir.generateMaskedRow(input, FunctionMode.CONSISTENT.name());
        assertEquals(output, bir.generateMaskedRow(input, FunctionMode.CONSISTENT.name())); //$NON-NLS-1$
    }

    @Test
    public void emptyReturnsEmpty() {
        bir.parse("2, 4, X", false, new Random(42));
        output = bir.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void dummyHighParameters() {
        bir.parse("1, 8", false, new Random(42));
        output = bir.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void tooFewParameters() {
        try {
            bir.parse("1", false, new Random(42));
            fail("should get exception with input " + Arrays.toString(bir.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void letterInParameters() {
        try {
            bir.parse("lk, df", false, new Random(42));
            fail("should get exception with input " + Arrays.toString(bir.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bir.generateMaskedRow(input);
        assertEquals("", output);
    }

}
