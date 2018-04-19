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
package org.talend.dataquality.matchmerge.mfb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.matchmerge.mfb.RecordIterator.ValueGenerator;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMerger;
import org.talend.dataquality.record.linkage.record.SimpleVSRRecordMatcher;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

import junit.framework.TestCase;

public class MFBTest extends TestCase {

    protected static final int COUNT = 200;

    private final static String[] CONSTANTS = { "constant", "value", "tac", "different", "big", "database", "heat", "quality" };

    private final static String[] SIMILARS = { "constant", "constan", "ocnstant", "constnat", "constnta", "oncstant", "constatn",
            "consttan" };

    private final static AttributeMatcherType[] TESTS_MATCH = { AttributeMatcherType.LEVENSHTEIN, AttributeMatcherType.SOUNDEX,
            AttributeMatcherType.JARO_WINKLER, AttributeMatcherType.DOUBLE_METAPHONE };

    private static void testConstant(final int constantNumber, int totalCount, AttributeMatcherType matchAlgorithm) {
        Map<String, ValueGenerator> generators = new HashMap<String, ValueGenerator>();
        generators.put("name", new ValueGenerator() {

            int index = 0;

            @Override
            public int getColumnIndex() {
                return index;
            }

            @Override
            public String newValue() {
                return CONSTANTS[index++ % constantNumber];
            }

            @Override
            public Object getAttribute() {
                // TODO Auto-generated method stub
                return null;
            }
        });
        RecordGenerator recordGenerator = new RecordGenerator();
        recordGenerator.setMatchKeyMap(generators);
        Iterator<Record> iterator = new RecordIterator(totalCount, recordGenerator);
        MatchMergeAlgorithm algorithm = MFB.build(new AttributeMatcherType[] { matchAlgorithm }, new String[] { "" },
                new float[] { 1 }, 0, new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.LONGEST }, new String[] { "" },
                new double[] { 1 }, new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchAll },
                new SubString[] { SubString.NO_SUBSTRING }, "MFB");
        List<Record> mergedRecords = algorithm.execute(iterator);
        assertEquals(constantNumber, mergedRecords.size());
        for (Record mergedRecord : mergedRecords) {
            assertEquals(Math.round(totalCount / constantNumber), mergedRecord.getRelatedIds().size());
        }
    }

    private static void testConcatenateParameter(final int constantNumber, int totalCount, AttributeMatcherType matchAlgorithm,
            String separator) {
        Map<String, ValueGenerator> generators = new HashMap<String, ValueGenerator>();
        generators.put("name", new ValueGenerator() {

            int index = 0;

            @Override
            public int getColumnIndex() {
                return index;
            }

            @Override
            public String newValue() {
                return CONSTANTS[index++ % constantNumber];
            }

            @Override
            public Object getAttribute() {
                // TODO Auto-generated method stub
                return null;
            }
        });
        RecordGenerator recordGenerator = new RecordGenerator();
        recordGenerator.setMatchKeyMap(generators);
        Iterator<Record> iterator = new RecordIterator(totalCount, recordGenerator);
        MatchMergeAlgorithm algorithm = MFB.build(new AttributeMatcherType[] { matchAlgorithm }, new String[] { "" },
                new float[] { 1 }, 0, new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.CONCATENATE },
                new String[] { separator }, new double[] { 1 },
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchAll },
                new SubString[] { SubString.NO_SUBSTRING }, "MFB");
        List<Record> mergedRecords = algorithm.execute(iterator);
        assertEquals(constantNumber, mergedRecords.size());
        int i = 0;
        for (Record mergedRecord : mergedRecords) {
            int relatedIdCount = mergedRecord.getRelatedIds().size();
            int length = separator == null ? 0 : separator.length();
            int spaceCount = ((relatedIdCount - 1) * length);
            List<Attribute> attributes = mergedRecord.getAttributes();
            assertEquals(Math.round(totalCount / constantNumber), relatedIdCount);
            assertEquals(1, attributes.size());
            Attribute attribute = attributes.get(0);
            assertEquals((CONSTANTS[i].length() * relatedIdCount) + spaceCount, attribute.getValue().length());
            i++;
        }
    }

    private static void testSimilar(final int similarNumber, int totalCount, AttributeMatcherType matchAlgorithm) {
        Map<String, ValueGenerator> generators = new HashMap<String, ValueGenerator>();
        generators.put("name", new ValueGenerator() {

            int index = 0;

            @Override
            public int getColumnIndex() {
                return index;
            }

            @Override
            public String newValue() {
                return SIMILARS[index++ % similarNumber];
            }

            @Override
            public Object getAttribute() {
                // TODO Auto-generated method stub
                return null;
            }
        });
        RecordGenerator recordGenerator = new RecordGenerator();
        recordGenerator.setMatchKeyMap(generators);
        Iterator<Record> iterator = new RecordIterator(totalCount, recordGenerator);
        MatchMergeAlgorithm algorithm = MFB.build(new AttributeMatcherType[] { matchAlgorithm }, new String[] { "" },
                new float[] { 0.5f }, 0, new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_COMMON },
                new String[] { "" }, new double[] { 1 },
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchAll },
                new SubString[] { SubString.NO_SUBSTRING }, "MFB");
        List<Record> mergedRecords = algorithm.execute(iterator);
        assertEquals(1, mergedRecords.size());
        for (Record mergedRecord : mergedRecords) {
            assertEquals(totalCount, mergedRecord.getRelatedIds().size());
        }
    }

    private static void testWeight(final int constantNumber, int totalCount, AttributeMatcherType matchAlgorithm) {
        Map<String, ValueGenerator> generators = new HashMap<String, ValueGenerator>();
        generators.put("name", new ValueGenerator() {

            int index = 0;

            @Override
            public int getColumnIndex() {
                return index;
            }

            @Override
            public String newValue() {
                return CONSTANTS[index++ % constantNumber];
            }

            @Override
            public Object getAttribute() {
                // TODO Auto-generated method stub
                return null;
            }
        });
        // Runs a first match with a weight 1
        RecordGenerator recordGenerator = new RecordGenerator();
        recordGenerator.setMatchKeyMap(generators);
        Iterator<Record> iterator = new RecordIterator(totalCount, recordGenerator);
        MatchMergeAlgorithm algorithm = MFB.build(new AttributeMatcherType[] { matchAlgorithm }, new String[] { "" },
                new float[] { 1 }, 0, new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.LONGEST }, new String[] { "" },
                new double[] { 1 }, // Mark rule with a weight of 1.
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchAll },
                new SubString[] { SubString.NO_SUBSTRING }, "MFB");
        List<Record> mergedRecords = algorithm.execute(iterator);
        assertEquals(constantNumber, mergedRecords.size());
        long totalConfidence1 = 0;
        for (Record mergedRecord : mergedRecords) {
            assertEquals(totalCount / constantNumber, mergedRecord.getRelatedIds().size());
            totalConfidence1 += mergedRecord.getConfidence();
        }
        // Runs a second match with a weight 4
        iterator = new RecordIterator(totalCount, recordGenerator);
        algorithm = MFB.build(new AttributeMatcherType[] { matchAlgorithm }, new String[] { "" }, new float[] { 1 }, 0,
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.LONGEST }, new String[] { "" }, new double[] { 4 }, // Mark rule with a weight of 4 -> should not affect overall score since score is
                // normalized.
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchAll },
                new SubString[] { SubString.NO_SUBSTRING }, "MFB");
        mergedRecords = algorithm.execute(iterator);
        assertEquals(constantNumber, mergedRecords.size());
        long totalConfidence2 = 0;
        for (Record mergedRecord : mergedRecords) {
            assertEquals(totalCount / constantNumber, mergedRecord.getRelatedIds().size());
            totalConfidence2 += mergedRecord.getConfidence();
        }
        // ... but this shouldn't change the overall score (because score is always between 0 and 1).
        assertEquals(totalConfidence1, totalConfidence2);
    }

    public void testArguments() throws Exception {
        try {
            MFB.build(new AttributeMatcherType[0], new String[0], new float[0], 0, new SurvivorShipAlgorithmEnum[0],
                    new String[0], new double[0], new IAttributeMatcher.NullOption[0], new SubString[0], "MFB");
            fail();
        } catch (Exception e) {
            // Expected
        }

        IRecordMatcher matcher = new SimpleVSRRecordMatcher();
        IRecordMerger merger = new MFBRecordMerger("", new String[0], new SurvivorShipAlgorithmEnum[0]);
        try {
            new MFB(matcher, null);
            fail();
        } catch (Exception e) {
            // Expected
        }
        try {
            new MFB(null, merger);
            fail();
        } catch (Exception e) {
            // Expected
        }
        try {
            new MFB(null, null);
            fail();
        } catch (Exception e) {
            // Expected
        }
    }

    public void testConstantValueRecords() throws Exception {
        for (AttributeMatcherType matchAlgorithm : TESTS_MATCH) {
            testConstant(1, COUNT, matchAlgorithm);
            testConstant(2, COUNT, matchAlgorithm);
            testConstant(4, COUNT, matchAlgorithm);
            testConstant(8, COUNT, matchAlgorithm);
        }
    }

    public void testConcatenateParameter() throws Exception {
        for (AttributeMatcherType matchAlgorithm : TESTS_MATCH) {
            testConcatenateParameter(1, COUNT, matchAlgorithm, null); // Argument test
        }
        for (AttributeMatcherType matchAlgorithm : TESTS_MATCH) {
            testConcatenateParameter(1, COUNT, matchAlgorithm, "");
            testConcatenateParameter(2, COUNT, matchAlgorithm, "");
            testConcatenateParameter(4, COUNT, matchAlgorithm, "");
            testConcatenateParameter(8, COUNT, matchAlgorithm, "");
        }
        for (AttributeMatcherType matchAlgorithm : TESTS_MATCH) {
            testConcatenateParameter(1, COUNT, matchAlgorithm, " / ");
            testConcatenateParameter(2, COUNT, matchAlgorithm, " / ");
            testConcatenateParameter(4, COUNT, matchAlgorithm, " / ");
            testConcatenateParameter(8, COUNT, matchAlgorithm, " / ");
        }
    }

    public void testMatchWeight() throws Exception {
        testWeight(1, COUNT, AttributeMatcherType.LEVENSHTEIN);
        testWeight(2, COUNT, AttributeMatcherType.LEVENSHTEIN);
        testWeight(4, COUNT, AttributeMatcherType.LEVENSHTEIN);
        testWeight(8, COUNT, AttributeMatcherType.LEVENSHTEIN);
    }

    public void testSimilarValueRecords() throws Exception {
        testSimilar(1, COUNT, AttributeMatcherType.LEVENSHTEIN);
        testSimilar(2, COUNT, AttributeMatcherType.LEVENSHTEIN);
        testSimilar(4, COUNT, AttributeMatcherType.LEVENSHTEIN);
        testSimilar(8, COUNT, AttributeMatcherType.LEVENSHTEIN);
    }

}
