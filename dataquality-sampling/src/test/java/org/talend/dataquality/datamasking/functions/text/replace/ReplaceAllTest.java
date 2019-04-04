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
package org.talend.dataquality.datamasking.functions.text.replace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.pattern.character.CharPattern;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.functions.text.Alphabet;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceAllTest {

    private String output;

    private String input = "i86ut val 4"; //$NON-NLS-1$

    private ReplaceAll ra = new ReplaceAll();

    private Alphabet alphabet = Alphabet.DEFAULT_LATIN;

    @Before
    public void setUp() {
        ra.setRandom(new Random(42));
        ra.parse("", false);
    }

    @Test
    public void replaceByParameter() {
        ra.parse("X", false);
        output = ra.generateMaskedRow(input);
        assertEquals("XXXXXXXXXXX", output); //$NON-NLS-1$
    }

    @Test
    public void defaultBehavior() {
        output = ra.generateMaskedRow(input);
        assertEquals("a38ma rnq 9", output); //$NON-NLS-1$
    }

    @Test
    public void randomWithSurrogate() {
        output = ra.generateMaskedRow("\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3");
        assertEquals(4, output.codePoints().count()); // $NON-NLS-1$
    }

    @Test
    public void consistent() {
        ra.setMaskingMode(FunctionMode.CONSISTENT);
        output = ra.generateMaskedRow(input);
        assertEquals(output, ra.generateMaskedRow(input)); // $NON-NLS-1$
    }

    @Test
    public void noSeedConsistent() {
        ra.setMaskingMode(FunctionMode.CONSISTENT);
        ra.setRandom(null);
        ra.parse(" ", false);
        output = ra.generateMaskedRow(input);
        assertEquals(output, ra.generateMaskedRow(input)); // $NON-NLS-1$
    }

    @Test
    public void consistentWithSurrogate() {
        ra.setMaskingMode(FunctionMode.CONSISTENT);
        output = ra.generateMaskedRow("\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3");
        assertEquals(output, ra.generateMaskedRow("\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3"));
    }

    @Test
    public void bijectiveWithSurrogate() {
        ra.setMaskingMode(FunctionMode.BIJECTIVE);
        ra.setAlphabet(alphabet);
        ra.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        String input = "abc\uD840\uDC40\uD840\uDFD3\uD841\uDC01\uD840\uDFD3efgh";
        String output = ra.generateMaskedRow(input);
        assertEquals(input.length(), output.length());
        assertEquals(input.substring(3, 11), output.substring(3, 11));
    }

    @Test
    public void bijectiveTooShortValue() {
        String input = "a";
        ra.setMaskingMode(FunctionMode.BIJECTIVE);
        ra.setAlphabet(alphabet);
        ra.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        String output = ra.generateMaskedRow(input);
        assertNull(output);
    }

    @Test
    public void bijectivity() {
        ra.setRandom(null);
        ra.setMaskingMode(FunctionMode.BIJECTIVE);
        ra.setAlphabet(alphabet);
        ra.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        Set<String> outputSet = new HashSet<>();
        String prefix = "a@";
        String suffix = "z98";
        for (int i = 0; i < alphabet.getRadix(); i++) {
            for (int j = 0; j < alphabet.getRadix(); j++) {
                String input = prefix + String.valueOf(Character.toChars(alphabet.getCharactersMap().get(i)))
                        + String.valueOf(Character.toChars(alphabet.getCharactersMap().get(j))) + suffix;

                outputSet.add(ra.generateMaskedRow(input));
            }
        }
        assertEquals((int) Math.pow(alphabet.getRadix(), 2), outputSet.size()); // $NON-NLS-1$
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
            ra.parse("zi", false);
            fail("should get exception with input " + Arrays.toString(ra.getParsedParameters())); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", IllegalArgumentException.class.equals(e.getClass())); //$NON-NLS-1$
        }
        output = ra.generateMaskedRow(input);
        assertEquals("", output); // $NON-NLS-1$
    }

    @Test
    public void bijectiveBestGuess() {

        ra.parse("", false);
        ra.setMaskingMode(FunctionMode.BIJECTIVE);
        ra.setAlphabet(Alphabet.BEST_GUESS);
        ra.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        Map<String, String> inputOutput = new LinkedHashMap<>();
        inputOutput.put("abc123", "gvlc95"); // lower_latin + digit
        inputOutput.put("123abc", "xohb4e"); // lower_latin + digit
        inputOutput.put("abcd", "pwij"); // lower_latin
        inputOutput.put("ぁｦアぁｦア", "こごヹｱﾑゔ"); // kanas
        inputOutput.put("éaièE", "épàüV"); // lower_latin + lower_latin_rare + upper_latin
        inputOutput.put("éaièE０ａＡ", "ñklÿV６ｕＣ"); // More than 5 patterns, plug pattern
        inputOutput.put("一一", "顠枔"); // Kanji, plug pattern
        inputOutput.put("一약", "璊얚"); // Kanji + hangul, plug pattern
        inputOutput.put("\u3400\u3401", "㢴\uD84F\uDF90"); // kanji_rare, plug pattern
        inputOutput.put("0aéBÈ０ａＡぁｦア一\u3400약", "5oþIÑ５ｕＡひｮア揹\uD876\uDEF8룵"); // all char patterns, plug pattern
        inputOutput.put("\u3400", "\uD864\uDFC0"); // switch to consistent for the moment
        inputOutput.put("\u4E00", "碽"); // Switch to consistent for the moment

        for (String input : inputOutput.keySet()) {
            String output = ra.generateMaskedRow(input);
            assertEquals(inputOutput.get(input), output);
            assertTrue("The same charPatterns are present", checkPatterns(input, output));
        }
    }

    private boolean checkPatterns(String input, String output) {
        Set<CharPattern> charPatternSetInput = getPatterns(input);
        Set<CharPattern> charPatternSetOutput = getPatterns(output);
        if (charPatternSetInput.size() != charPatternSetOutput.size())
            return false;

        for (CharPattern charPattern : charPatternSetInput) {
            if (!charPatternSetOutput.contains(charPattern))
                return false;
        }
        return true;
    }

    private Set<CharPattern> getPatterns(String input) {
        Set<CharPattern> charPatternSet = new HashSet<>();
        long numberCodePoints = input.codePoints().count();
        for (int i = 0; i < numberCodePoints; i++) {
            Integer codePoint = input.codePointAt(input.offsetByCodePoints(0, i));
            for (CharPattern charPattern : CharPattern.values()) {
                if (charPattern.contains(codePoint)) {
                    charPatternSet.add(charPattern);
                    break;
                }
            }
        }
        return charPatternSet;
    }
}
