// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.talend.dataquality.record.linkage.attribute.AttributeMatcherFactory;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;

/**
 * created by scorreia on Jan 17, 2013 Detailled comment
 * 
 */
@SuppressWarnings("nls")
public class CombinedRecordMatcherTest {

    private static final double EPSILON = 0.000001;

    private static final boolean DEBUG = false;

    private static final String[][] MAINRECORDS = { { "seb", "talend", "suresnes", "data not used in record matching" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            { "seb", "talend", "suresns", null }, { "seb", "tlend", "sursnes", null }, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$
            { "sebas", "taland", "suresnes", null }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            { "seb", "tlend", "sursnes", null }, { "sebas", "taland", "suresnes", null }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            { "babass", "Atlend", "sursene", null }, { "Sebastião", "talènd", "Suresnes", "comment" }, }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

    // the algorithms selected by the user for each of the 3 match keys
    private static final String[] ATTRIBUTEMATCHERALGORITHMS =
            { "EXACT", "DOUBLE_METAPHONE", "LEVENSHTEIN", "SOUNDEX" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    // the weights given by the user to each of the 3 match key.
    private static final double[] ATTRIBUTEWEIGHTS_1 = { 30, 10, 0, 0 };

    private static final double[] ATTRIBUTEWEIGHTS_2 = { 1, 1, 30, 0 };

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.CombinedRecordMatcher#getMatchingWeight(java.lang.String[], java.lang.String[])}
     * .
     */
    @Test
    public void testGetMatchingWeight() {
        final double MATCH_THRESHOLD = 0.9;
        // create a first matcher
        IRecordMatcher recMatcher1 = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher,
                ATTRIBUTEMATCHERALGORITHMS, ATTRIBUTEWEIGHTS_1);
        recMatcher1.setRecordMatchThreshold(MATCH_THRESHOLD);
        List<List<Double>> matcherWeigths = computeWeights(recMatcher1);

        // check that the combined matcher with only one matcher is identical to the matcher
        CombinedRecordMatcher combMatcher1 = RecordMatcherFactory.createCombinedRecordMatcher();
        assertTrue(combMatcher1.add(recMatcher1));
        List<List<Double>> combinedMatcherWeigths = computeWeights(combMatcher1);
        System.out.println(combMatcher1);
        compare(matcherWeigths, combinedMatcherWeigths);
        int nbMatch1 = countMatches(matcherWeigths);

        // create a second simple matcher and do the same
        IRecordMatcher recMatcher2 = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher,
                ATTRIBUTEMATCHERALGORITHMS, ATTRIBUTEWEIGHTS_2);
        recMatcher2.setRecordMatchThreshold(MATCH_THRESHOLD);
        matcherWeigths = computeWeights(recMatcher2);

        CombinedRecordMatcher combMatcher2 = RecordMatcherFactory.createCombinedRecordMatcher();
        assertTrue(combMatcher2.add(recMatcher2));
        System.out.println(combMatcher2);
        compare(matcherWeigths, computeWeights(combMatcher2));

        // compare the number of matches for each matcher
        int nbMatch2 = countMatches(matcherWeigths);
        assertNotSame("The two matchers should not have the same match count", nbMatch1, nbMatch2); //$NON-NLS-1$

        // create a third simple matcher and do the same
        IRecordMatcher recMatcher3 = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher,
                new String[] { "DUMMY", "DUMMY", "DUMMY", "EXACT" }, new double[] { 0, 0, 0, 1 }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        recMatcher3.setRecordMatchThreshold(MATCH_THRESHOLD);
        matcherWeigths = computeWeights(recMatcher3);

        CombinedRecordMatcher combMatcher3 = RecordMatcherFactory.createCombinedRecordMatcher();
        assertTrue(combMatcher3.add(recMatcher3));
        System.out.println(combMatcher3);
        compare(matcherWeigths, computeWeights(combMatcher3));

        // compare the number of matches for each matcher
        int nbMatch3 = countMatches(matcherWeigths);
        assertNotSame("The two matchers should not have the same match count. " + nbMatch1, nbMatch1, nbMatch3); //$NON-NLS-1$
        assertNotSame("The two matchers should not have the same match count. " + nbMatch2, nbMatch2, nbMatch3); //$NON-NLS-1$

        // combine them
        CombinedRecordMatcher combMatcher = RecordMatcherFactory.createCombinedRecordMatcher();
        assertTrue(combMatcher.add(recMatcher1));
        assertTrue(combMatcher.add(recMatcher2));
        assertTrue(combMatcher.add(recMatcher3));
        System.out.println(combMatcher);

        int nbMatch = countMatches(combMatcher);
        assertEquals(true, nbMatch >= nbMatch1);
        assertEquals(true, nbMatch >= nbMatch2);
        assertEquals(true, nbMatch >= nbMatch3);

        // Test order of matcher. Results should be different.
        // combine them
        CombinedRecordMatcher reverseCombMatcher = RecordMatcherFactory.createCombinedRecordMatcher();
        assertTrue(reverseCombMatcher.add(recMatcher3));
        assertTrue(reverseCombMatcher.add(recMatcher2));
        assertTrue(reverseCombMatcher.add(recMatcher1));
        System.out.println(reverseCombMatcher);

        int nbMatchReverse = countMatches(combMatcher);
        assertEquals("The order of matcher should not change the number of matches", nbMatch, nbMatchReverse); //$NON-NLS-1$

    }

    @Test
    public void testMatchThreshold() {
        // create a first matcher
        IRecordMatcher recMatcher1 = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher,
                ATTRIBUTEMATCHERALGORITHMS, ATTRIBUTEWEIGHTS_1);
        IRecordMatcher recMatcher2 = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher,
                ATTRIBUTEMATCHERALGORITHMS, ATTRIBUTEWEIGHTS_2);
        CombinedRecordMatcher combMatcher = RecordMatcherFactory.createCombinedRecordMatcher();
        assertTrue(combMatcher.add(recMatcher1));
        assertTrue(combMatcher.add(recMatcher2));

        // count matches when no threshold is set
        int countMatches = countMatches(combMatcher);
        assertEquals(0, countMatches);

        // set thresholds
        recMatcher1.setRecordMatchThreshold(0.7);
        recMatcher2.setRecordMatchThreshold(0.9);
        countMatches = countMatches(combMatcher);
        assertNotSame(0, countMatches);

    }

    /**
     * DOC scorreia Comment method "compare".
     * 
     * @param m1
     * @param m2
     */
    private void compare(List<List<Double>> m1, List<List<Double>> m2) {
        for (int i = 0; i < m1.size(); i++) {
            List<Double> mi1 = m1.get(i);
            for (int j = 0; j < mi1.size(); j++) {
                assertEquals(mi1.get(j), m2.get(i).get(j));
            }
        }

    }

    private int countMatches(IRecordMatcher combMatcher) {
        List<List<Double>> allWeights = computeWeights(combMatcher);
        double recordMatchThreshold = combMatcher.getRecordMatchThreshold();
        return countMatches(allWeights, Double.POSITIVE_INFINITY != recordMatchThreshold);
    }

    private int countMatches(List<List<Double>> allWeights) {
        return countMatches(allWeights, true);
    }

    private int countMatches(List<List<Double>> allWeights, boolean useThreshold) {
        // count number of matches
        final double MATCH_THRESHOLD = 0;
        int nbMatch = 0;
        for (int i = 0; i < allWeights.size(); i++) {
            List<Double> list = allWeights.get(i);
            for (int j = 0; j < list.size(); j++) {
                Double value = list.get(j);
                if (value > MATCH_THRESHOLD) {
                    nbMatch++;
                }

                System.out.print(NumberFormat.getNumberInstance().format(value));
                System.out.print("\t"); //$NON-NLS-1$
                if (useThreshold && i == j) {
                    assertEquals("Diagonal weights should be one", 1.0d, value, EPSILON); //$NON-NLS-1$
                }
            }
            System.out.println();
        }
        System.out
                .println("nb match = " + nbMatch + " over " + MAINRECORDS.length * MAINRECORDS.length + " comparisons"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        System.out.println();
        return nbMatch;
    }

    private List<List<Double>> computeWeights(IRecordMatcher combMatcher) {
        // compare all records together
        List<List<Double>> allWeights = new ArrayList<List<Double>>();
        for (String[] record1 : MAINRECORDS) {
            List<Double> allWeightInRow = new ArrayList<Double>();
            for (String[] record2 : MAINRECORDS) {
                double matchingWeight = combMatcher.getMatchingWeight(record1, record2);
                // add the matching weight into the matrix when it's a match
                if (matchingWeight < combMatcher.getRecordMatchThreshold()) {
                    matchingWeight = 0; // add 0 otherwise
                }
                allWeightInRow.add(matchingWeight);

                if (DEBUG) {
                    // print details
                    StringBuffer buf = new StringBuffer();
                    buf.append(SimpleVSRRecordMatcherTest.printRecord(record1));
                    buf.append(" ~ "); //$NON-NLS-1$
                    buf.append(SimpleVSRRecordMatcherTest.printRecord(record2));
                    buf.append(" ; \tweight= " + matchingWeight); //$NON-NLS-1$
                    System.out.println(buf.toString());
                }
            }
            allWeights.add(allWeightInRow);
            if (DEBUG) {
                System.out.println();
            }
        }
        return allWeights;
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.CombinedRecordMatcher#add(org.talend.dataquality.record.linkage.record.IRecordMatcher)}
     * .
     */
    @Test
    public void testAdd() {
        // create a first matcher
        IRecordMatcher recMatcher1 = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher,
                ATTRIBUTEMATCHERALGORITHMS, ATTRIBUTEWEIGHTS_1);

        // check that the combined matcher with only one matcher is identical to the matcher
        CombinedRecordMatcher combMatcher1 = RecordMatcherFactory.createCombinedRecordMatcher();
        assertTrue(combMatcher1.add(recMatcher1));

        IRecordMatcher recMatcher2 = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher,
                ATTRIBUTEMATCHERALGORITHMS, new double[] { 1.0, 3.0 });
        assertNull("cannot create a matcher like this", recMatcher2); //$NON-NLS-1$
        recMatcher2 = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher,
                new String[] { "EXACT", "EXACT" }, //$NON-NLS-1$ //$NON-NLS-2$
                new double[] { 1.0, 3.0 });
        assertFalse("cannot add a matcher with a different size", combMatcher1.add(recMatcher2)); //$NON-NLS-1$

        recMatcher2 =
                RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher, new String[] { "EXACT", "EXACT", //$NON-NLS-1$ //$NON-NLS-2$
                        "EXACT", "EXACT" }, ATTRIBUTEWEIGHTS_1); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(combMatcher1.add(recMatcher2));

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.AbstractRecordMatcher#setAttributeMatchers(org.talend.dataquality.record.linkage.attribute.IAttributeMatcher[])}
     * .
     */
    @Test
    public void testSetAttributeMatchers() {
        for (RecordMatcherType type : RecordMatcherType.values()) {
            if (RecordMatcherType.T_SwooshAlgorithm.equals(type)) {
                continue;
            }
            IRecordMatcher recMatcher =
                    RecordMatcherFactory.createMatcher(type, ATTRIBUTEMATCHERALGORITHMS, ATTRIBUTEWEIGHTS_1);
            if (recMatcher == null) {
                continue;
            }
            checkAttributeMatching(1.0d, recMatcher);
            assertTrue(recMatcher.setAttributeWeights(new double[] { 3, 2, 0, 3 }));
            CombinedRecordMatcher combMatcher = RecordMatcherFactory.createCombinedRecordMatcher();
            checkAttributeMatching(0.0d, combMatcher);
            assertFalse("the attribute weights of a combined matcher cannot be modified", //$NON-NLS-1$
                    combMatcher.setAttributeWeights(new double[] { 3, 2, 0, 3 }));
            combMatcher.add(recMatcher);
            assertFalse("the attribute weights of a combined matcher cannot be modified", //$NON-NLS-1$
                    combMatcher.setAttributeWeights(new double[] { 3, 2, 0, 3 }));
        }

    }

    private void checkAttributeMatching(double expectedValue, IRecordMatcher recMatcher) {
        assertFalse(recMatcher.setAttributeWeights(new double[] { 3, 2, 0 }));

        String[][] records =
                { { "a", "a", "a", "a" }, { "a", "a", null, "a" }, { "a", "a", "", "a" }, { "a", "a", "b", "a" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$
        for (String[] rec1 : records) {
            for (String[] rec2 : records) {
                double matchingWeight = recMatcher.getMatchingWeight(rec1, rec2);
                assertEquals(expectedValue, matchingWeight, EPSILON);
            }
        }
    }

    @Test
    public void testCreateMatcherString() {
        IRecordMatcher recordMatcher = RecordMatcherFactory.createMatcher("Simple VSR Matcher"); //$NON-NLS-1$
        assertNotNull("Simple VSR Matcher not created!", recordMatcher);
    }

    @Test
    public void testgetLabeledAttributeMatchWeights() {
        IAttributeMatcher attMatcher1 = AttributeMatcherFactory.createMatcher("EXACT"); //$NON-NLS-1$
        IAttributeMatcher attMatcher2 = AttributeMatcherFactory.createMatcher("EXACT"); //$NON-NLS-1$

        IRecordMatcher recordMatcher = RecordMatcherFactory.createMatcher("Simple VSR Matcher"); //$NON-NLS-1$

        IAttributeMatcher[] attrMatchers = new IAttributeMatcher[] { attMatcher1, attMatcher2 };
        assertFalse("record size is not set. It's not allowed to set the attribute matchers", //$NON-NLS-1$
                recordMatcher.setAttributeMatchers(attrMatchers));
        recordMatcher.setRecordSize(2);
        assertTrue("The record size is now set. It's allowed to set the attribute matchers", //$NON-NLS-1$
                recordMatcher.setAttributeMatchers(attrMatchers));

        String[] record1 = { "toto@free.fr", "Tota" }; //$NON-NLS-1$ //$NON-NLS-2$
        double[] emptyAttributeMatchingWeights = recordMatcher.getCurrentAttributeMatchingWeights();
        for (double d : emptyAttributeMatchingWeights) {
            assertEquals(0.0d, d, EPSILON);
        }

        assertEquals(1.0d, recordMatcher.getMatchingWeight(record1, record1), EPSILON);

        assertFalse(recordMatcher.setAttributeWeights(ATTRIBUTEWEIGHTS_1));
        assertTrue(recordMatcher.setAttributeWeights(new double[] { 1.0, 1.0 }));
        assertEquals(1.0d, recordMatcher.getMatchingWeight(record1, record1), EPSILON);

        double[] currentAttributeMatchingWeights = recordMatcher.getCurrentAttributeMatchingWeights();
        for (double d : currentAttributeMatchingWeights) {
            assertEquals(1.0, d, EPSILON);
        }
        assertEquals("1.0 | 1.0", recordMatcher.getLabeledAttributeMatchWeights()); //$NON-NLS-1$

        attMatcher1.setAttributeName("EMAIL"); //$NON-NLS-1$
        attMatcher2.setAttributeName("NAME"); //$NON-NLS-1$
        recordMatcher.setDisplayLabels(true);
        assertEquals("EMAIL: 1.0 | NAME: 1.0", recordMatcher.getLabeledAttributeMatchWeights()); //$NON-NLS-1$

        CombinedRecordMatcher combMatcher = RecordMatcherFactory.createCombinedRecordMatcher();
        IRecordMatcher recordMatcher2 = RecordMatcherFactory.createMatcher("Simple VSR Matcher"); //$NON-NLS-1$
        recordMatcher2.setRecordSize(2);
        recordMatcher2.setAttributeMatchers(attrMatchers);
        combMatcher.add(recordMatcher);
        combMatcher.add(recordMatcher2);

        assertEquals(1.0d, combMatcher.getMatchingWeight(record1, record1), EPSILON);
        combMatcher.setDisplayLabels(Boolean.FALSE);
        assertEquals("1.0 | 1.0", combMatcher.getLabeledAttributeMatchWeights()); //$NON-NLS-1$
        combMatcher.setDisplayLabels(Boolean.TRUE);
        assertEquals("EMAIL: 1.0 | NAME: 1.0", combMatcher.getLabeledAttributeMatchWeights()); //$NON-NLS-1$

    }
}
