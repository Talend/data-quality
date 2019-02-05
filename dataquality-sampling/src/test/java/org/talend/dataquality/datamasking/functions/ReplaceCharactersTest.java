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

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.generic.Alphabet;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceCharactersTest {

    private String output;

    private String input = "inp456ut value"; //$NON-NLS-1$

    private ReplaceCharacters rc = new ReplaceCharacters();

    @Before
    public void setUp() throws Exception {
        rc.setRandom(new Random(42));
    }

    @Test
    public void replaceByParameter() {
        rc.parse("X", false);
        output = rc.generateMaskedRow(input);
        assertEquals("XXX456XX XXXXX", output); //$NON-NLS-1$
    }

    @Test
    public void defaultBehavior() {
        rc.parse("", false);
        output = rc.generateMaskedRow(input);
        assertEquals("ahw456ma rnqdp", output); //$NON-NLS-1$
    }

    @Test
    public void numberInParameter() {
        try {
            rc.parse("12", false);
            fail("should get exception with input " + Arrays.toString(rc.parameters)); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rc.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void emptyReturnsEmpty() {
        rc.setKeepEmpty(true);
        output = rc.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void consistent() {
        rc.parse(" ", false);
        output = rc.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rc.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void consistentNoSeed() {
        rc.setRandom(null);
        rc.parse(" ", false);
        output = rc.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rc.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void bijectiveReplaceOnlyCharactersFromAlphabet() {
        rc.parse("", false);
        rc.setAlphabet(Alphabet.LATIN_LETTERS);
        rc.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        String output = rc.generateMaskedRow(input, FunctionMode.BIJECTIVE);
        assertEquals("input : " + input + "\noutput : " + output, input.length(), output.length());
        assertEquals(input.substring(3, 6), output.substring(3, 6));
    }

    @Test
    public void bijective() {
        Alphabet alphabet = Alphabet.LATIN_LETTERS;
        rc.parse("", false);
        rc.setAlphabet(alphabet);
        rc.setSecret(FormatPreservingMethod.AES_CBC_PRF, "data");
        Set<String> outputSet = new HashSet<>();
        String prefix = "a@";
        String suffix = "z98";
        for (int i = 0; i < alphabet.getRadix(); i++) {
            for (int j = 0; j < alphabet.getRadix(); j++) {
                String input = prefix + String.valueOf(Character.toChars(alphabet.getCharactersMap().get(i)))
                        + String.valueOf(Character.toChars(alphabet.getCharactersMap().get(j))) + suffix;

                outputSet.add(rc.generateMaskedRow(input, FunctionMode.BIJECTIVE));
            }
        }
        assertEquals((int) Math.pow(alphabet.getRadix(), 2), outputSet.size()); //$NON-NLS-1$
    }
}
