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
package org.talend.dataquality.record.linkage.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher.NullOption;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;

/**
 * Unit tests for attribute matchers
 */
public class AbstractAttributeMatcherTest {

    private static final String[][] testcase = {
            // tests for Exact matcher
            { AttributeMatcherType.EXACT.toString(), "E", "E", "1.0" },
            { AttributeMatcherType.EXACT.toString(), "E", "e", "0.0" },
            { AttributeMatcherType.EXACT.toString(), "A", "Ä", "0.0" },

            // tests for ExactIgnoreCase matcher
            { AttributeMatcherType.EXACT_IGNORE_CASE.toString(), "E", "e", "1.0" },
            { AttributeMatcherType.EXACT_IGNORE_CASE.toString(), "Î", "î", "1.0" },
            { AttributeMatcherType.EXACT_IGNORE_CASE.toString(), "A", "Ä", "0.0" },

            // tests for Soundex matcher
            { AttributeMatcherType.SOUNDEX.toString(), "kate", "Cade", "0.75" },
            { AttributeMatcherType.SOUNDEX.toString(), "unmottreslong", "unautremotlong", "1.0" },
            { AttributeMatcherType.SOUNDEX.toString(), "steff", "stephanie", "0.75" },
            { AttributeMatcherType.SOUNDEX.toString(), "Sebastiao", "Sepastien", "1.0" },
            { AttributeMatcherType.SOUNDEX.toString(), "Sizhao", "sejao", "1.0" },
            { AttributeMatcherType.SOUNDEX.toString(), "A", "Ä", "0.0" },

            // tests for SoundexFR matcher

            { AttributeMatcherType.SOUNDEX_FR.toString(), "kate", "Cade", "0.75" },
            { AttributeMatcherType.SOUNDEX_FR.toString(), "unmottreslong", "unautremotlong", "0.25" },
            { AttributeMatcherType.SOUNDEX_FR.toString(), "steff", "stephanie", "0.75" },
            { AttributeMatcherType.SOUNDEX_FR.toString(), "Sebastiao", "Sepastien", "0.75" },
            { AttributeMatcherType.SOUNDEX_FR.toString(), "Sizhao", "sejao", "0.75" },
            { AttributeMatcherType.SOUNDEX_FR.toString(), "A", "Ä", "0.75" },

            // tests for Metaphone/DoubleMataphone matcher
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), "kate", "Cade", "1.0" },
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), "unmottreslong", "unautremotlong", "0.5" },
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), "steff", "stephanie", "0.75" },
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), "Sebastiao", "Sepastien", "0.75" },
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), "Sizhao", "sejao", "1.0" },
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), "A", "Ä", "0.0" },

            // tests for Levenshtein matcher
            { AttributeMatcherType.LEVENSHTEIN.toString(), "kate", "Cade", "0.5" },
            { AttributeMatcherType.LEVENSHTEIN.toString(), "unmottreslong", "unautremotlong", "0.57" },
            { AttributeMatcherType.LEVENSHTEIN.toString(), "steff", "stephanie", "0.33" },
            { AttributeMatcherType.LEVENSHTEIN.toString(), "Sebastiao", "Sepastien", "0.67" },
            { AttributeMatcherType.LEVENSHTEIN.toString(), "Sizhao", "sejao", "0.33" },
            { AttributeMatcherType.LEVENSHTEIN.toString(), "A", "Ä", "0.0" },

            // tests for Jaro(-Winkler) matcher
            { AttributeMatcherType.JARO.toString(), "kate", "Cade", "0.66" },
            { AttributeMatcherType.JARO.toString(), "unmottreslong", "unautremotlong", "0.84" },
            { AttributeMatcherType.JARO.toString(), "steff", "stephanie", "0.64" },
            { AttributeMatcherType.JARO.toString(), "Sebastiao", "Sepastien", "0.78" },
            { AttributeMatcherType.JARO.toString(), "Sizhao", "sejao", "0.57" },
            { AttributeMatcherType.JARO.toString(), "A", "Ä", "0.0" },

            // tests for LCS matcher
            { AttributeMatcherType.LCS.toString(), "kate", "Cade", "0.25" },
            { AttributeMatcherType.LCS.toString(), "unmottreslong", "unautremotlong", "0.29" },
            { AttributeMatcherType.LCS.toString(), "steff", "stephanie", "0.33" },
            { AttributeMatcherType.LCS.toString(), "Sebastiao", "Sepastien", "0.44" },
            { AttributeMatcherType.LCS.toString(), "Sizhao", "sejao", "0.33" },
            { AttributeMatcherType.LCS.toString(), "A", "Ä", "0.0" },

            // tests for Qgrams matcher
            { AttributeMatcherType.Q_GRAMS.toString(), "kate", "Cade", "0.16" },
            { AttributeMatcherType.Q_GRAMS.toString(), "unmottreslong", "unautremotlong", "0.51" },
            { AttributeMatcherType.Q_GRAMS.toString(), "steff", "stephanie", "0.33" },
            { AttributeMatcherType.Q_GRAMS.toString(), "Sebastiao", "Sepastien", "0.36" },
            { AttributeMatcherType.Q_GRAMS.toString(), "Sizhao", "sejao", "0.26" },
            { AttributeMatcherType.Q_GRAMS.toString(), "A", "Ä", "0.0" },

            // tests for Hamming matcher
            { AttributeMatcherType.HAMMING.toString(), "kate", "Cade", "0.5" },
            { AttributeMatcherType.HAMMING.toString(), "unmottreslong", "unautremotlong", "0.0" },
            { AttributeMatcherType.HAMMING.toString(), "steff", "stephanie", "0.0" },
            { AttributeMatcherType.HAMMING.toString(), "Sebastiao", "Sepastien", "0.67" },
            { AttributeMatcherType.HAMMING.toString(), "Sizhao", "sejao", "0.0" },
            { AttributeMatcherType.HAMMING.toString(), "A", "Ä", "0.0" },

            // tests for blank fields
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), "", "stephanie", "0.0" },
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), "stephanie", "", "0.0" },
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), "", "", "1.0" },
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), "A", "Ä", "0.0" },

            // tests for null fields (default null option)
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), null, "stephanie", "0.0" },
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), "stephanie", null, "0.0" },
            { AttributeMatcherType.DOUBLE_METAPHONE.toString(), null, null, "1.0" }, };

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getMatchingWeight(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetMatchingWeight() {
        for (String[] str : testcase) {
            AbstractAttributeMatcher matcher = (AbstractAttributeMatcher) AttributeMatcherFactory.createMatcher(str[0]);
            matcher.setTokenMethod(TokenizedResolutionMethod.NO);
            matcher.setInitialComparison(false);
            matcher.setFingerPrintApply(false);
            assertEquals("The score of test case is unexpected.\n" + Arrays.asList(str), Double.valueOf(str[3]),
                    matcher.getMatchingWeight(str[1], str[2]), 0.01);
        }
    }

    @Test
    public void testNullOptions() {
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            if (type.equals(AttributeMatcherType.CUSTOM)) {
                continue; // do not handle this case.
            }
            IAttributeMatcher matcher = AttributeMatcherFactory.createMatcher(type);
            matcher.setNullOption(NullOption.nullMatchAll);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight(null, null), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight(null, "toto"), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("", "toto"), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("", ""), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("a", "a"), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight(null, "Ä"), 0);

            // change option
            matcher.setNullOption(NullOption.nullMatchNone);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight(null, null), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight(null, "toto"), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight("", "toto"), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight("", ""), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("a", "a"), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight(null, "Ä"), 0);

            // change option
            matcher.setNullOption(NullOption.nullMatchNull);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight(null, null), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight(null, "toto"), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight("", "toto"), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("", ""), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("a", "a"), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight(null, "Ä"), 0);

        }

    }

    @Test
    public void tokenizePerfectAnyOrder() {
        String str1 = "John Doe";
        String str2 = "Doe John";

        List<AbstractAttributeMatcher> listMeasure = new ArrayList<AbstractAttributeMatcher>();
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            if (type != AttributeMatcherType.CUSTOM && type != AttributeMatcherType.DUMMY)
                listMeasure.add((AbstractAttributeMatcher) AttributeMatcherFactory.createMatcher(type));
        }

        for (AbstractAttributeMatcher measure : listMeasure) {

            measure.setTokenMethod(TokenizedResolutionMethod.ANYORDER);
            measure.setRegexTokenize(" ");
            double w = measure.getMatchingWeight(str1, str2);
            assertEquals(1.0, w, 0);
        }
    }

    @Test
    public void tokenizeSameOrder() {
        String str1 = "John Doe";
        String str2 = "John F. Patrick Doe";
        String str3 = "John Patrick Doe Jones";

        List<AbstractAttributeMatcher> listMeasure = new ArrayList<AbstractAttributeMatcher>();
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            if (type != AttributeMatcherType.CUSTOM && type != AttributeMatcherType.DUMMY)
                listMeasure.add((AbstractAttributeMatcher) AttributeMatcherFactory.createMatcher(type));
        }

        List<Boolean> listInitials = new ArrayList<Boolean>();
        listInitials.add(true);
        listInitials.add(false);

        for (Boolean ini : listInitials)
            for (AbstractAttributeMatcher measure : listMeasure) {
                measure.setInitialComparison(ini);
                measure.setTokenMethod(TokenizedResolutionMethod.SAMEORDER);
                measure.setRegexTokenize(" ");

                double w12 = measure.getMatchingWeight(str1, str2);
                assertTrue(w12 >= 0.5);

                double w13 = measure.getMatchingWeight(str1, str3);
                assertTrue(w13 >= 0.5);
            }

    }

    @Test
    public void tokenizeSamePlace() {
        String str1 = "John Doe";
        String str2 = "John D. Doe";

        List<AbstractAttributeMatcher> listMeasure = new ArrayList<AbstractAttributeMatcher>();
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            if (type != AttributeMatcherType.CUSTOM && type != AttributeMatcherType.DUMMY)
                listMeasure.add((AbstractAttributeMatcher) AttributeMatcherFactory.createMatcher(type));
        }

        List<Boolean> listInitials = new ArrayList<Boolean>();
        listInitials.add(true);
        listInitials.add(false);

        for (Boolean ini : listInitials)
            for (AbstractAttributeMatcher measure : listMeasure) {
                measure.setInitialComparison(ini);
                measure.setTokenMethod(TokenizedResolutionMethod.SAMEPLACE);
                measure.setRegexTokenize(" ");
                double w = measure.getMatchingWeight(str1, str2);
                assertTrue(w < 0.67);
            }
    }

    @Test
    public void tokenizeInitial() {
        String str1 = "John Doe";
        String str2 = "J. D";

        List<AbstractAttributeMatcher> listMeasure = new ArrayList<AbstractAttributeMatcher>();
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            if (type != AttributeMatcherType.CUSTOM && type != AttributeMatcherType.DUMMY)
                listMeasure.add((AbstractAttributeMatcher) AttributeMatcherFactory.createMatcher(type));
        }

        for (AbstractAttributeMatcher measure : listMeasure) {
            measure.setInitialComparison(true);
            measure.setTokenMethod(TokenizedResolutionMethod.SAMEPLACE);
            measure.setRegexTokenize(" ");
            double w = measure.getMatchingWeight(str1, str2);
            assertEquals(1.0, w, 0);

            measure.setInitialComparison(false);
            w = measure.getMatchingWeight(str1, str2);
            assertNotEquals(1.0, w);
        }
    }

}
