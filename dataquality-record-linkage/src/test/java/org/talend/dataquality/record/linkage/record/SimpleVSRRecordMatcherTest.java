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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.talend.dataquality.record.linkage.attribute.AttributeMatcherFactory;
import org.talend.dataquality.record.linkage.attribute.ExactIgnoreCaseMatcher;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.attribute.JaroWinklerMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SimpleVSRRecordMatcherTest {

    private static final double EPSILON = 0.000001;

    public static final String[][] RECORDS1 = { { "seb", "talend", "suresnes" }, { "seb", "talend", "suresns" }, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            { "seb", "tlend", "sursnes" }, { "sebas", "taland", "suresnes" } }; //$NON-NLS-2$//$NON-NLS-3$

    public static final String[][] RECORDS2 = { { "seb", "tlend", "sursnes" }, { "sebas", "taland", "suresnes" }, };

    public static final double[][] ALLATTRIBUTEWEIGHTS =
            { { 1, 1, 1 }, { 1, 1, 0 }, { 1, 0, 1 }, { 0, 1, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, { 0, 1, 0 }, { 1, 0, 0 },
                    { 0, 0, 0 }, { 0.4, 0.2, 1 }, { 0, 0, 1.5 }, { 20, 100, 10 }, { 1, 2, 3 }, { 20, 40, 60 } };

    // The value of the thresholds
    private static final double ACCEPTABLE_THRESHOLD = 0.95;

    private static final double UNACCEPTABLE_THRESHOLD = 0.8;

    // 2 samples of data
    private static final String[][] MAINRECORDS =
            { { "seb", "talend", "suresnes", "data not used in record matching" }, { "seb", "talend", "suresns", null },
                    { "seb", "tlend", "sursnes", null }, { "sebas", "taland", "suresnes", null } };

    private static final String[][] LOOKUPRECORDS =
            { { "seb", "tlend", "sursnes", null }, { "sebas", "taland", "suresnes", null }, };

    // the algorithms selected by the user for each of the 3 join keys
    private static final String[] ATTRIBUTEMATCHERALGORITHMS = { "Exact", "DOUBLE_METAPHONE", "LEVENSHTEIN" };

    // the weights given by the user to each of the 3 join key.
    private static final double[] ATTRIBUTEWEIGHTS = { 1, 1, 1 };

    // records with un-regular record size.
    public static final String[][] RECORDS2_UNREG = { { "seb" }, { "sebas", "taland", "suresnes" }, };

    /**
     *
     * Test what if the record size are not match.
     */
    @Test
    public void testGetMatchingWeightRcdSize() {
        int weightIdx = 0;
        IRecordMatcher match = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);
        for (double[] attributeWeights : ALLATTRIBUTEWEIGHTS) {
            match.setRecordSize(3);
            assertEquals(true, match.setAttributeMatchers(new IAttributeMatcher[] { new ExactIgnoreCaseMatcher(),
                    new JaroWinklerMatcher(), new ExactIgnoreCaseMatcher() }));

            if (!areValidAttributeWeitghts(match, attributeWeights)) {
                // don't continue the program
                return;
            }

            // compute proba
            int matchIdx = 0;
            for (String[] record1 : RECORDS1) {
                for (String[] record2 : RECORDS2_UNREG) {
                    double matchingProba = 0d;
                    try {
                        matchingProba = match.getMatchingWeight(record1, record2);
                        assertEquals(MATCH_PROBS[weightIdx][matchIdx], matchingProba, EPSILON);
                        System.out.println(
                                "P(" + printRecord(record1) + " = " + printRecord(record2) + ") =" + matchingProba);
                    } catch (ArrayIndexOutOfBoundsException idxOutExc) {
                        // When record size is less than the expected (set in the parameter setup), the index out of
                        // bounds exception is expected. It means that the client must handle this exception
                        // additionaly.
                        assertEquals(0d, matchingProba, EPSILON);
                    }
                    matchIdx++;
                }
            }
            weightIdx++;
        }
    }

    private static boolean areValidAttributeWeitghts(IRecordMatcher match, double[] attributeWeights) {
        if (!expectedReturnedValue(attributeWeights)) {
            // will get an exception
            try {
                match.setAttributeWeights(attributeWeights);
                fail("We should get an exception here as the attribute weights are not valid!");
            } catch (Exception e) {
                // we caught an exception.
                System.out.println("This exception is expected: " + e.getMessage());
                // don't even try to execute the remaining code
                return false;
            }
        }
        assertEquals(expectedReturnedValue(attributeWeights), match.setAttributeWeights(attributeWeights));
        return true;
    }

    @Test
    public void testGetMatchingWeightWithDistance() {
        IRecordMatcher recordMatcher = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);

        // ////////////// INITIALIZATION (MUST BE DONE ONCE ONLY) /////////////////////

        // initialize matcher
        int nbRecords = 3; // this value is the number of columns used in the JOIN_KEY parameter
        recordMatcher.setRecordSize(nbRecords);

        // create attribute matchers for each of the join key
        int nbJoinKey = ATTRIBUTEMATCHERALGORITHMS.length;
        IAttributeMatcher[] attributeMatchers = new IAttributeMatcher[nbJoinKey];
        for (int i = 0; i < attributeMatchers.length; i++) {
            attributeMatchers[i] = AttributeMatcherFactory.createMatcher(ATTRIBUTEMATCHERALGORITHMS[i]);
        }
        recordMatcher.setAttributeMatchers(attributeMatchers);

        // set the weights chosen by the user
        assertTrue(recordMatcher.setAttributeWeights(ATTRIBUTEWEIGHTS));

        // initialize the blocking variables
        // (we use the column which are in exact match as blocking variables but we could change this in the future)
        List<Integer> listIndices = new ArrayList<Integer>();
        for (int i = 0; i < attributeMatchers.length; i++) {
            AttributeMatcherType matchType = attributeMatchers[i].getMatchType();
            if (AttributeMatcherType.EXACT.equals(matchType)
                    || AttributeMatcherType.EXACT_IGNORE_CASE.equals(matchType)) {
                listIndices.add(i);
            }
        }
        int[] blockedVariableIndices = new int[listIndices.size()];
        for (int i = 0; i < listIndices.size(); i++) {
            blockedVariableIndices[i] = listIndices.get(i);
        }
        recordMatcher.setBlockingAttributeMatchers(blockedVariableIndices);

        // ////////////// END OF INITIALIZATION /////////////////////

        // /////////// MAIN LOOP now /////////////// compute proba
        int idx = 0;
        for (String[] record1 : MAINRECORDS) {
            for (String[] record2 : LOOKUPRECORDS) {
                final double matchingProba = recordMatcher.getMatchingWeight(record1, record2);
                assertEquals(MATCH_PROBS_WITH_DISTINCE[idx], matchingProba, EPSILON);
                idx++;
                if (matchingProba >= ACCEPTABLE_THRESHOLD) {
                    // put this record in the "matches" flow
                    System.out.println(
                            " MATCH P(" + printRecord(record1) + " = " + printRecord(record2) + ") =" + matchingProba
                                    + " DETAILS=" + printRecord(recordMatcher.getCurrentAttributeMatchingWeights()));
                    continue;
                }
                if (UNACCEPTABLE_THRESHOLD < matchingProba && matchingProba < ACCEPTABLE_THRESHOLD) {
                    // put this record in the "possible maches" flow
                    System.out.println("  POSSIBLE MATCH P(" + printRecord(record1) + " = " + printRecord(record2)
                            + ") =" + matchingProba + " DETAILS="
                            + printRecord(recordMatcher.getCurrentAttributeMatchingWeights()));
                    continue;
                }
                // put this record in the "non-matches" flow
                System.out.println(
                        "!match P(" + printRecord(record1) + " = " + printRecord(record2) + ") =" + matchingProba
                                + " DETAILS=" + printRecord(recordMatcher.getCurrentAttributeMatchingWeights()));
            }
        }
    }

    /**
     * Added test case for TDQ-10391, when 1 not divide into the number of columns
     */
    @Test
    public void testGetMatchingWeightWithDistance_2() {
        IRecordMatcher recordMatcher = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);

        // ////////////// INITIALIZATION (MUST BE DONE ONCE ONLY) /////////////////////

        // initialize matcher
        int nbRecords = 6; // this value is the number of columns used in the JOIN_KEY parameter
        recordMatcher.setRecordSize(nbRecords);

        // create attribute matchers for each of the join key
        int nbJoinKey = 6;
        IAttributeMatcher[] attributeMatchers = new IAttributeMatcher[nbJoinKey];
        for (int i = 0; i < attributeMatchers.length; i++) {
            attributeMatchers[i] = AttributeMatcherFactory.createMatcher("Exact");
        }
        recordMatcher.setAttributeMatchers(attributeMatchers);

        double[] attributeWeight = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
        // set the weights chosen by the user
        assertTrue(recordMatcher.setAttributeWeights(attributeWeight));

        String[] record1 = { "aTO1mK", "dXatJF", "6vVVQl", "5ILPaE", "cwBh91", "WEWkkS" };
        String[] record2 = { "ThSymJ", "ymLm1u", "ZM7ilc", "0nCUz8", "SOPHs7", "boqY3Y" };

        // /////////// MAIN LOOP now /////////////// compute proba
        final double matchingProba = recordMatcher.getMatchingWeight(record1, record1);
        assertTrue(matchingProba >= 1.0);
        final double matchingProbb = recordMatcher.getMatchingWeight(record2, record2);
        assertTrue(matchingProbb >= 1.0);
    }

    /**
     * Added test case for TDQ-10391, when 1 not divide into the number of columns: 19,21
     */
    @Test
    public void testGetMatchingWeightWithDistance_3() {
        IRecordMatcher recordMatcher = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);

        // ////////////// INITIALIZATION (MUST BE DONE ONCE ONLY) /////////////////////

        // initialize matcher
        int nbRecords = 19; // this value is the number of columns used in the JOIN_KEY parameter
        recordMatcher.setRecordSize(nbRecords);

        // create attribute matchers for each of the join key
        int nbJoinKey = 19;
        IAttributeMatcher[] attributeMatchers = new IAttributeMatcher[nbJoinKey];
        for (int i = 0; i < attributeMatchers.length; i++) {
            attributeMatchers[i] = AttributeMatcherFactory.createMatcher("Exact");
        }
        recordMatcher.setAttributeMatchers(attributeMatchers);

        double[] attributeWeight =
                { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
        // set the weights chosen by the user
        assertTrue(recordMatcher.setAttributeWeights(attributeWeight));

        String[] record1 = { "aTO1mK", "dXatJF", "6vVVQl", "5ILPaE", "cwBh91", "WEWkkS", "cIWupW", "2fj8BW", "melpcx",
                "8MOIxp", "NrJKCh", "XgFwsN", "f8OXQS", "iSJjtn", "Nflx4L", "lEEXi8", "mLi1Fy", "JiQvqQ", "onvRDZ",
                "JiQvqQ", "onvRDZ" };
        String[] record2 = { "ThSymJ", "ymLm1u", "ZM7ilc", "0nCUz8", "SOPHs7", "boqY3Y", "OWzqig", "eypLAO", "rJzJGe",
                "fwhrJT", "j8Ekqm", "8q9Jcr", "sSKY7P", "SQxLve", "vQRPJd", "gqvZeq", "ENchvh", "YKHGxQ", "xgkjjf",
                "JiQvqQ", "onvRDZ" };

        // /////////// MAIN LOOP now /////////////// compute proba
        final double matchingProba = recordMatcher.getMatchingWeight(record1, record1);
        assertTrue(matchingProba >= 1.0);
        final double matchingProbb = recordMatcher.getMatchingWeight(record2, record2);
        assertTrue(matchingProbb >= 1.0);
    }

    private static String printRecord(double[] record) {
        Double[] array = new Double[record.length];
        for (int i = 0; i < record.length; i++) {
            array[i] = record[i];
        }
        return StringUtils.join(array, '|');
    }

    @Test
    public void testGetMatchingWeight() {
        int weightIdx = 0;
        IRecordMatcher match = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);
        for (double[] attributeWeights : ALLATTRIBUTEWEIGHTS) {
            computeForWeights(attributeWeights, match, weightIdx);
            weightIdx++;
        }
    }

    /**
     * DOC scorreia Comment method "computeForWeights".
     *
     * @param attributeWeights
     */
    private static void computeForWeights(double[] attributeWeights, IRecordMatcher match, int weightIdxOut) {
        // print
        System.out.println("Weights = " + printWeight(attributeWeights)); //$NON-NLS-1$

        // prepare matcher

        match.setRecordSize(3);
        assertEquals(true, match.setAttributeMatchers(new IAttributeMatcher[] { new ExactIgnoreCaseMatcher(),
                new JaroWinklerMatcher(), new ExactIgnoreCaseMatcher() }));
        if (!areValidAttributeWeitghts(match, attributeWeights)) {
            // break here
            return;
        }
        assertEquals(true, match.setAttributeWeights(attributeWeights));

        // compute proba
        int matchIdx = 0;
        for (String[] record1 : RECORDS1) {
            for (String[] record2 : RECORDS2) {
                final double matchingProba = match.getMatchingWeight(record1, record2);
                assertEquals(MATCH_PROBS[weightIdxOut][matchIdx], matchingProba, EPSILON);
                matchIdx++;
                System.out.println("P(" + printRecord(record1) + " = " + printRecord(record2) + ") =" + matchingProba);
            }
        }
    }

    static String printRecord(Object[] record) {
        return StringUtils.join(record, '|');
    }

    private static String printWeight(double[] record) {
        return Arrays.toString(record);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.AbstractRecordMatcher#setAttributeGroups(int[][])}.
     */
    @Test
    public void testSetAttributeGroupsWithoutGroups() {
        IRecordMatcher match = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);
        assertTrue(match.setAttributeGroups(null));
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.AbstractRecordMatcher#setAttributeGroups(int[][])}.
     */
    @Test
    public void testSetAttributeGroupsWithGroupsBadLength() {
        IRecordMatcher match = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);
        match.setRecordSize(2);
        assertFalse(match.setAttributeGroups(new int[][] { { 1, 2, 3, 4 }, { 1, 2, 3, 4 }, { 1, 2, 3, 4 } }));
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.AbstractRecordMatcher#setAttributeGroups(int[][])}.
     */
    @Test
    public void testSetAttributeGroupsWithGroupsGoodLength() {
        IRecordMatcher match = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);
        match.setRecordSize(3);
        assertTrue(match.setAttributeGroups(new int[][] { { 1, 2, 3, 4 }, { 1, 2, 3, 4 }, { 1, 2, 3, 4 } }));
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.AbstractRecordMatcher#setAttributeMatchers(org.talend.dataquality.record.linkage.attribute.IAttributeMatcher[])}
     * .
     */
    @Test
    public void testSetAttributeMatchers() {
        IRecordMatcher match = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);
        match.setRecordSize(3);
        assertEquals(true, match.setAttributeMatchers(new IAttributeMatcher[] { new ExactIgnoreCaseMatcher(),
                new JaroWinklerMatcher(), new ExactIgnoreCaseMatcher() }));
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.AbstractRecordMatcher#setAttributeWeights(double[])}.
     */
    @Test
    public void testSetAttributeWeights() {
        // recordSize must be same to weights.length
        IRecordMatcher match = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);
        match.setRecordSize(2);
        assertFalse(match.setAttributeWeights(ALLATTRIBUTEWEIGHTS[0]));

        match.setRecordSize(3);
        // Assert values of zeros
        try {
            match.setAttributeWeights(new double[] { 0.0, 0.0, 0.0 });
            fail("we should not arrive here. ");
        } catch (Exception e) {
            assertTrue(e != null && e instanceof IllegalArgumentException);
        }
        assertNotNull(((SimpleVSRRecordMatcher) match).attributeWeights);

        // Assert the minus values
        try {
            match.setAttributeWeights(new double[] { -1, -1, 2 });
            fail("we should not arrive here. ");
        } catch (Exception e) {
            assertTrue(e != null && e instanceof IllegalArgumentException);
        }
    }

    // The matcing probilities weight with

    private static double[] MATCH_PROBS_WITH_DISTINCE =
            new double[] { 0.9583333333333333, 0.0, 0.9047619047619047, 0.0, 1.0, 0.0, 0.0, 1.0 };

    // The matching probabilities weights.
    private static double[][] MATCH_PROBS = new double[][] {
            { 0.649999992052714, 0.6407407283782959, 0.649999992052714, 0.30740739504496256, 1.0, 0.2800000031789144,
                    0.2800000031789144, 1.0 },
            { 0.9749999880790711, 0.46111109256744387, 0.9749999880790711, 0.46111109256744387, 1.0,
                    0.42000000476837157, 0.42000000476837157, 1.0 },
            { 0.5, 0.5, 0.5, 0.0, 1.0, 0.0, 0.0, 1.0 },
            { 0.47499998807907107, 0.9611110925674439, 0.47499998807907107, 0.46111109256744387, 1.0,
                    0.42000000476837157, 0.42000000476837157, 1.0 },
            { 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0 }, { 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0 },
            { 0.9499999761581421, 0.9222221851348877, 0.9499999761581421, 0.9222221851348877, 1.0, 0.8400000095367431,
                    0.8400000095367431, 1.0 },
            { 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 0.36874999701976774, 0.7402777731418609, 0.36874999701976774, 0.11527777314186097, 1.0,
                    0.10500000119209289, 0.10500000119209289, 1.0 },
            { 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0 },
            { 0.8846153662754941, 0.7863247577960676, 0.8846153662754941, 0.7094016808729906, 1.0, 0.6461538534898025,
                    0.6461538534898025, 1.0 },
            { 0.48333332538604745, 0.8074073950449625, 0.48333332538604745, 0.30740739504496256, 1.0,
                    0.2800000031789144, 0.2800000031789144, 1.0 },
            { 0.48333332538604745, 0.8074073950449625, 0.48333332538604745, 0.30740739504496256, 1.0,
                    0.2800000031789144, 0.2800000031789144, 1.0 } };

    private static boolean expectedReturnedValue(double[] attributeWeights) {
        if (attributeWeights == null) {
            return false;
        }
        double total = 0.0;
        for (double d : attributeWeights) {
            if (d < 0) {
                return false;
            }
            total += d;
        }
        return total > 0;
    }

    @Test
    public void testgetLabeledAttributeMatchWeights() {
        // create attribute matchers with names
        AttributeMatcherType type = AttributeMatcherType.JARO;
        IAttributeMatcher attMatcher = AttributeMatcherFactory.createMatcher(type);
        final String colName = "EMAIL";
        attMatcher.setAttributeName(colName);

        // create a record matcher with the attribute matchers
        IAttributeMatcher[] allAttMatchers = new IAttributeMatcher[] { attMatcher };
        IRecordMatcher matcher = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);
        matcher.setRecordSize(1);
        assertTrue(matcher.setAttributeMatchers(allAttMatchers));

        // test getLabeledAttributeMatchWeights (check that it gives expected results)
        matcher.setDisplayLabels(Boolean.TRUE);
        String labeledAttributeMatchWeights = matcher.getLabeledAttributeMatchWeights();
        assertEquals("no computation done. Result should be 0", "EMAIL: 0.0", labeledAttributeMatchWeights);

        assertEquals(1.0d, matcher.getMatchingWeight(RECORDS1[1], RECORDS1[1]), EPSILON);
        labeledAttributeMatchWeights = matcher.getLabeledAttributeMatchWeights();
        assertEquals("Computation done and exact match. Result should be 1", "EMAIL: 1.0",
                labeledAttributeMatchWeights);

    }
}
