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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.talend.daikon.pattern.character.CharPattern;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.generic.Alphabet;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceCharactersTest {

    private String output;

    private String input = "inp456ut value"; //$NON-NLS-1$

    private ReplaceCharacters rc = new ReplaceCharacters();

    @Test
    public void replaceByParameter() {
        rc.parse("X", false, new Random(42));
        output = rc.generateMaskedRow(input);
        assertEquals("XXX456XX XXXXX", output); //$NON-NLS-1$
    }

    @Test
    public void defaultBehavior() {
        rc.parse("", false, new Random(42));
        output = rc.generateMaskedRow(input);
        assertEquals("ahw456ma rnqdp", output); //$NON-NLS-1$
    }

    @Test
    public void numberInParameter() {
        try {
            rc.parse("12", false, new Random(42));
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
        rc.parse(" ", false, new RandomWrapper(42));
        output = rc.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rc.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void consistentNoSeed() {
        rc.parse(" ", false, new RandomWrapper());
        output = rc.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rc.generateMaskedRow(input, FunctionMode.CONSISTENT)); //$NON-NLS-1$
    }

    @Test
    public void bijectiveReplaceOnlyCharactersFromAlphabet() {
        rc.parse("", false, new RandomWrapper(42));
        rc.setAlphabet(Alphabet.LATIN_LETTERS);
        rc.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        String output = rc.generateMaskedRow(input, FunctionMode.BIJECTIVE);
        assertEquals("input : " + input + "\noutput : " + output, input.length(), output.length());
        assertEquals(input.substring(3, 6), output.substring(3, 6));
    }

    @Test
    public void bijectiveBestGuess() {

        rc.parse("", false, new RandomWrapper());
        rc.setAlphabet(Alphabet.BEST_GUESS);
        rc.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF, "data");
        Map<String, String> inputOutput = new LinkedHashMap<String, String>() {

            {
                put("abc123", "gvlc95"); // lower_latin + digit
                put("123abc", "xohb4e"); // lower_latin + digit
                put("abcd", "pwij"); // lower_latin
                put("ぁｦアぁｦア", "こごヹｱﾑゔ"); // kanas
                put("éaièE", "épàüV"); // lower_latin + lower_latin_rare + upper_latin
                put("éaièE０ａＡ", "ñklÿV６ｕＣ"); // More than 5 patterns, plug pattern
                put("一一", "顠枔"); // Kanji, plug pattern
                put("一약", "璊얚"); // Kanji + hangul, plug pattern
                put("\u3400\u3401", "㢴\uD84F\uDF90"); // kanji_rare, plug pattern
                put("0aéBÈ０ａＡぁｦア一\u3400약", "5oþIÑ５ｕＡひｮア揹\uD876\uDEF8룵"); // all char patterns, plug pattern
                put("\u3400", "\uD864\uDFC0"); // switch to consistent for the moment
                put("\u4E00", "碽"); // Switch to consistent for the moment
            }
        };

        for (String input : inputOutput.keySet()) {
            String output = rc.generateMaskedRow(input, FunctionMode.BIJECTIVE);
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

    @Test
    public void bijective() {
        Alphabet alphabet = Alphabet.LATIN_LETTERS;
        rc.parse("", false, new RandomWrapper(42));
        rc.setAlphabet(alphabet);
        rc.setSecret(FormatPreservingMethod.AES_CBC_PRF, "data");
        Set<String> outputSet = new HashSet<>();
        String prefix = "a@";
        String suffix = "z98";
        for (int i = 0; i < alphabet.getRadix(); i++) {
            for (int j = 0; j < alphabet.getRadix(); j++) {
                String input = new StringBuilder().append(prefix).append(Character.toChars(alphabet.getCharactersMap().get(i)))
                        .append(Character.toChars(alphabet.getCharactersMap().get(j))).append(suffix).toString();

                outputSet.add(rc.generateMaskedRow(input, FunctionMode.BIJECTIVE));
            }
        }
        assertEquals((int) Math.pow(alphabet.getRadix(), 2), outputSet.size()); //$NON-NLS-1$
    }
}
