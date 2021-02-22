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
package org.talend.dataquality.matchmerge.mfb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.matchmerge.mfb.mdm.utils.DQFieldMetadataImpl;
import org.talend.dataquality.matchmerge.mfb.mdm.utils.DQMatchMergeRuleImpl;
import org.talend.dataquality.matchmerge.mfb.mdm.utils.FieldMetadata;
import org.talend.dataquality.matchmerge.mfb.mdm.utils.GroupingLoggerCallback;
import org.talend.dataquality.matchmerge.mfb.mdm.utils.MatchMergeRule;
import org.talend.dataquality.matchmerge.mfb.mdm.utils.MatchMergeRule.Declaration;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;
import org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.IRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer;
import org.talend.dataquality.record.linkage.grouping.TSwooshGrouping;
import org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBack;
import org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQMFB;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQMFBRecordMerger;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.grouping.swoosh.golden.DQGoldenRecordMFB;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.utils.BidiMultiMap;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

public class GoldenRecordMFBTest {

    public static final String MERGED_RECORD_SOURCE = "MDM";

    private static final Logger log = LoggerFactory.getLogger(AnalysisMatchRecordGrouping.class);

    private long currentIndex = 1;

    private long timestamp = 0;

    @SuppressWarnings("serial")
    List<Map<String, String>> MatchRuleCase1 = new ArrayList<Map<String, String>>() {

        {
            add(new HashMap<String, String>() {

                {
                    put("REFERENCE_COLUMN_IDX", "0");
                    put("REFERENCE_COLUMN", "id");
                    put("MATCHING_TYPE", "DUMMY");
                    put("ATTRIBUTE_NAME", "id");
                    put("COLUMN_IDX", "0");
                    put("CONFIDENCE_WEIGHT", "0");

                }
            });
            add(new HashMap<String, String>() {

                {
                    put("REFERENCE_COLUMN_IDX", "1");
                    put("REFERENCE_COLUMN", "name");
                    put("MATCHING_TYPE", "DUMMY");
                    put("ATTRIBUTE_NAME", "name");
                    put("COLUMN_IDX", "1");
                    put("CONFIDENCE_WEIGHT", "0");

                }
            });
            add(new HashMap<String, String>() {

                {
                    put("REFERENCE_COLUMN_IDX", "3");
                    put("HANDLE_NULL", "nullMatchNull");
                    put("ATTRIBUTE_THRESHOLD", 1 + "");
                    put("COLUMN_IDX", "2");
                    put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
                    put("TOKENIZATION_TYPE", "NO");
                    put("MATCHING_TYPE", "EXACT");
                    put("REFERENCE_COLUMN", "birthday");
                    put("RECORD_MATCH_THRESHOLD", 0.85 + "");
                    put("ATTRIBUTE_NAME", "city");
                    put("SURVIVORSHIP_FUNCTION", "MOST_COMMON");
                    put("CONFIDENCE_WEIGHT", "1");
                    put("PARAMETER", ",");

                }
            });
            add(new HashMap<String, String>() {

                {
                    put("MATCHING_TYPE", "DUMMY");
                    put("REFERENCE_COLUMN", "birthday");
                    put("REFERENCE_COLUMN_IDX", "3");
                    put("ATTRIBUTE_NAME", "birthday");
                    put("SURVIVORSHIP_FUNCTION", "MOST_COMMON");
                    put("HANDLE_NULL", "nullMatchNull");
                    put("ATTRIBUTE_THRESHOLD", 1 + "");
                    put("CONFIDENCE_WEIGHT", "0");
                    put("COLUMN_IDX", "3");
                    put("TOKENIZATION_TYPE", "NO");
                    put("PARAMETER", ",");
                }
            });
            add(new HashMap<String, String>() {

                {
                    put("REFERENCE_COLUMN_IDX", "4");
                    put("REFERENCE_COLUMN", "group");
                    put("MATCHING_TYPE", "DUMMY");
                    put("ATTRIBUTE_NAME", "group");
                    put("COLUMN_IDX", "4");
                    put("CONFIDENCE_WEIGHT", "0");
                }
            });
        }
    };

    @SuppressWarnings("serial")
    List<Map<String, String>> MatchRuleCase2 = new ArrayList<Map<String, String>>() {

        {
            add(new HashMap<String, String>() {

                {
                    put("REFERENCE_COLUMN_IDX", "0");
                    put("REFERENCE_COLUMN", "id");
                    put("MATCHING_TYPE", "DUMMY");
                    put("ATTRIBUTE_NAME", "id");
                    put("COLUMN_IDX", "0");
                    put("CONFIDENCE_WEIGHT", "0");

                }
            });
            add(new HashMap<String, String>() {

                {
                    put("REFERENCE_COLUMN_IDX", "1");
                    put("REFERENCE_COLUMN", "name");
                    put("MATCHING_TYPE", "DUMMY");
                    put("ATTRIBUTE_NAME", "name");
                    put("COLUMN_IDX", "1");
                    put("CONFIDENCE_WEIGHT", "0");

                }
            });
            add(new HashMap<String, String>() {

                {
                    put("MATCHING_TYPE", "DUMMY");
                    put("REFERENCE_COLUMN", "birthday");
                    put("REFERENCE_COLUMN_IDX", "3");
                    put("ATTRIBUTE_NAME", "city");
                    put("SURVIVORSHIP_FUNCTION", "MOST_COMMON");
                    put("HANDLE_NULL", "nullMatchNull");
                    put("ATTRIBUTE_THRESHOLD", 1 + "");
                    put("CONFIDENCE_WEIGHT", "0");
                    put("COLUMN_IDX", "2");
                    put("TOKENIZATION_TYPE", "NO");
                    put("PARAMETER", ",");

                }
            });
            add(new HashMap<String, String>() {

                {
                    put("REFERENCE_COLUMN_IDX", "3");
                    put("HANDLE_NULL", "nullMatchNull");
                    put("ATTRIBUTE_THRESHOLD", 1 + "");
                    put("COLUMN_IDX", "3");
                    put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
                    put("TOKENIZATION_TYPE", "NO");
                    put("MATCHING_TYPE", "EXACT");
                    put("REFERENCE_COLUMN", "birthday");
                    put("RECORD_MATCH_THRESHOLD", 0.85 + "");
                    put("ATTRIBUTE_NAME", "birthday");
                    put("SURVIVORSHIP_FUNCTION", "MOST_COMMON");
                    put("CONFIDENCE_WEIGHT", "1");
                    put("PARAMETER", ",");
                }
            });
            add(new HashMap<String, String>() {

                {
                    put("REFERENCE_COLUMN_IDX", "4");
                    put("REFERENCE_COLUMN", "group");
                    put("MATCHING_TYPE", "DUMMY");
                    put("ATTRIBUTE_NAME", "group");
                    put("COLUMN_IDX", "4");
                    put("CONFIDENCE_WEIGHT", "0");
                }
            });
        }
    };

    @SuppressWarnings("serial")
    List<String[]> inputData1 = new ArrayList<String[]>() {

        {
            add(new String[] { "1", "jone", "beijing", "2016-05-04", "group1" });
            add(new String[] { "2", "peter", "nanjing", "2016-05-05", "group1" });
            add(new String[] { "3", "sendi", "beijing", "2016-05-06", "group1" });
            add(new String[] { "4", "qiqi", "beijing", "2016-05-10", "group1" });
            add(new String[] { "5", "yuyu", "shanxi", "2016-05-10", "group1" });
        }
    };

    @SuppressWarnings("serial")
    List<String[]> inputData2 = new ArrayList<String[]>() {

        {
            add(new String[] { "1", "jone", "beijing", "2016-05-04", "group1" });
            add(new String[] { "2", "peter", "nanjing", "2016-05-05", "group1" });
            add(new String[] { "3", "sendi", "beijing", "2016-05-10", "group1" });
            add(new String[] { "4", "qiqi", "beijing", "2016-05-10", "group1" });
            add(new String[] { "5", "yuyu", "shanxi", "2016-05-10", "group1" });
        }

    };

    @SuppressWarnings("serial")
    List<String[]> inputData3 = new ArrayList<String[]>() {

        {
            add(new String[] { "1", "jone", "beijing", "2016-05-04", "group1" });
            add(new String[] { "2", "peter", "nanjing", "2016-05-05", "group1" });
            add(new String[] { "3", "sendi", "beijing", "2016-05-10", "group1" });
            add(new String[] { "4", "qiqi", "beijing", "2016-05-10", "group1" });
            add(new String[] { "5", "yuyu", "beijing", "2016-05-10", "group1" });
        }

    };
    // 1;jone;beijing;2016-05-04;group1
    // 2;peter;nanjing;2016-05-05;group1
    // 3;sendi;beijing;2016-05-06;group1
    // 4;hanter;beijing;2016-05-07;group2
    // 5;joyce;xian;2016-05-08;group2
    // 6;jacken;beijing;2016-05-09;group2
    // 7;qiqi;beijing;2016-05-10;group1
    // 8;chuanchuan;beijing;2016-05-11;group2
    // 9;yuyu;shanxi;2016-05-10;group1
    // 10;guoguo;shandong;2016-05-11;group2

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void nonGodlenRecordTest() {
        String[][] expectResult = new String[][] { { "2", "2" }, { "5", "1", "3", "4", "5" } };
        String[][] expectFobiddenList = new String[][] { {}, {}, {}, {} };
        List<MatchMergeRule> matchRules = generateMatchMergeRuleList();
        Callback callback = new MatchMergeCallback(10, 2);
        Iterator<Record> matchMergeInput = generateInputRecord();
        try {
            MatchMergeAlgorithm buildDQMFB = buildDQMFB(matchRules, callback);
            List<Record> matchMergeResults = buildDQMFB.execute(matchMergeInput);
            validateResult(matchMergeResults, callback, expectResult, expectFobiddenList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("serial")
    @Test
    /**
     * case1 golden reocrd in the start and middle(first record and third one record are golden record)
     */
    public void godlenRecordTestStartAndMiddle() {
        String[][] expectResult = new String[][] { { "2", "2" }, { "1", "1", "4", "5" }, { "3", "3", "4", "5" } };
        String[][] expectFobiddenList = new String[][] { {}, { "3" }, { "1" } };
        List<MatchMergeRule> matchRules = generateMatchMergeRuleList();
        Callback callback = new MatchMergeCallback(10, 2);
        Map<String, String[]> forbiddenListMap = new HashMap<String, String[]>() {

            {
                put("1", new String[] { "3" });
            }
        };
        Iterator<Record> matchMergeInput = generateForbiddenInputRecord(inputData1, forbiddenListMap);
        try {
            MatchMergeAlgorithm buildDQMFB = buildDQMFB(matchRules, callback);
            ((DQMFB) buildDQMFB).setHandleGoldenRecord(true);
            List<Record> matchMergeResults = buildDQMFB.execute(matchMergeInput);
            validateResult(matchMergeResults, callback, expectResult, expectFobiddenList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("serial")
    @Test
    /**
     * case2 golden record in the start and end(first record and last one record are golden record)
     */
    public void godlenRecordTestStartAndEnd() {
        String[][] expectResult = new String[][] { { "2", "2" }, { "1", "1", "3", "4" }, { "5", "4", "5", "3" } };
        String[][] expectFobiddenList = new String[][] { {}, { "5" }, { "1" } };
        List<MatchMergeRule> matchRules = generateMatchMergeRuleList();
        Callback callback = new MatchMergeCallback(10, 2);
        Map<String, String[]> forbiddenListMap = new HashMap<String, String[]>() {

            {
                put("1", new String[] { "5" });
            }
        };
        Iterator<Record> matchMergeInput = generateForbiddenInputRecord(inputData1, forbiddenListMap);
        try {
            MatchMergeAlgorithm buildDQMFB = buildDQMFB(matchRules, callback);
            ((DQMFB) buildDQMFB).setHandleGoldenRecord(true);
            List<Record> matchMergeResults = buildDQMFB.execute(matchMergeInput);
            validateResult(matchMergeResults, callback, expectResult, expectFobiddenList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("serial")
    @Test
    /**
     * case3 golden record in the start and end(last two records are golden record)
     */
    public void godlenRecordTestEndAndEnd() {
        String[][] expectResult = new String[][] { { "2", "2" }, { "5", "1", "3", "5" }, { "4", "1", "3", "4" } };
        String[][] expectFobiddenList = new String[][] { {}, { "4" }, { "5" } };
        List<MatchMergeRule> matchRules = generateMatchMergeRuleList();
        Callback callback = new MatchMergeCallback(10, 2);
        Map<String, String[]> forbiddenListMap = new HashMap<String, String[]>() {

            {
                put("4", new String[] { "5" });
            }
        };
        Iterator<Record> matchMergeInput = generateForbiddenInputRecord(inputData1, forbiddenListMap);
        try {
            MatchMergeAlgorithm buildDQMFB = buildDQMFB(matchRules, callback);
            ((DQMFB) buildDQMFB).setHandleGoldenRecord(true);
            List<Record> matchMergeResults = buildDQMFB.execute(matchMergeInput);
            validateResult(matchMergeResults, callback, expectResult, expectFobiddenList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("serial")
    @Test
    /**
     * case4 two pairs golden records in this case
     * 
     */
    public void godlenRecordTestTwoPairs() {
        String[][] expectResult = new String[][] { { "2", "2" }, { "5", "5" }, { "3", "4", "3" }, { "1", "4", "1" } };
        String[][] expectFobiddenList = new String[][] { {}, { "4" }, { "5", "1" }, { "5", "3" } };
        List<MatchMergeRule> matchRules = generateMatchMergeRuleList();
        Callback callback = new MatchMergeCallback(10, 2);
        Map<String, String[]> forbiddenListMap = new HashMap<String, String[]>() {

            {
                put("4", new String[] { "5" });
                put("1", new String[] { "3" });
            }
        };
        Iterator<Record> matchMergeInput = generateForbiddenInputRecord(inputData1, forbiddenListMap);
        try {
            MatchMergeAlgorithm buildDQMFB = buildDQMFB(matchRules, callback);
            ((DQMFB) buildDQMFB).setHandleGoldenRecord(true);
            List<Record> matchMergeResults = buildDQMFB.execute(matchMergeInput);
            validateResult(matchMergeResults, callback, expectResult, expectFobiddenList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("serial")
    @Test
    /**
     * case5 two pairs golden records in this case
     */
    public void godlenRecordTestThreePairs() {
        String[][] expectResult = new String[][] { { "2", "2" }, { "5", "5" }, { "3", "4", "3" }, { "1", "4", "1" } };
        String[][] expectFobiddenList = new String[][] { {}, { "3", "4" }, { "5", "1" }, { "5", "3" } };
        List<MatchMergeRule> matchRules = generateMatchMergeRuleList();
        Callback callback = new MatchMergeCallback(10, 2);
        Map<String, String[]> forbiddenListMap = new HashMap<String, String[]>() {

            {
                put("1", new String[] { "3" });
                put("3", new String[] { "5" });
                put("4", new String[] { "5" });
            }
        };
        Iterator<Record> matchMergeInput = generateForbiddenInputRecord(inputData2, forbiddenListMap);
        try {
            MatchMergeAlgorithm buildDQMFB = buildDQMFB(matchRules, callback);
            ((DQMFB) buildDQMFB).setHandleGoldenRecord(true);
            List<Record> matchMergeResults = buildDQMFB.execute(matchMergeInput);

            validateResult(matchMergeResults, callback, expectResult, expectFobiddenList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("serial")
    @Test
    /**
     * case6 two pairs golden records in this case
     * change last one from { "3", "5", "3" } to { "5", "5", "3" }
     */
    public void godlenRecordTestFourPairs() {
        String[][] expectResult = new String[][] { { "1", "1" }, { "2", "2" }, { "3", "4", "3" }, { "5", "5", "3" } };
        String[][] expectFobiddenList = new String[][] { { "3", "4", "5" }, {}, { "1", "5" }, { "1", "4" } };
        List<MatchMergeRule> matchRules = generateMatchMergeRuleList();
        Callback callback = new MatchMergeCallback(10, 2);
        Map<String, String[]> forbiddenListMap = new HashMap<String, String[]>() {

            {
                put("4", new String[] { "5" });
                put("1", new String[] { "3", "4", "5" });
            }
        };
        Iterator<Record> matchMergeInput = generateForbiddenInputRecord(inputData3, forbiddenListMap);
        try {
            MatchMergeAlgorithm buildDQMFB = buildDQMFB(matchRules, callback);
            ((DQMFB) buildDQMFB).setHandleGoldenRecord(true);
            List<Record> matchMergeResults = buildDQMFB.execute(matchMergeInput);
            validateResult(matchMergeResults, callback, expectResult, expectFobiddenList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("serial")
    @Test
    /**
     * case7 two pairs golden records in this case
     */
    public void godlenRecordTestFivePairs() {
        String[][] expectResult = new String[][] { { "1", "1" }, { "2", "2" }, { "4", "4" }, { "3", "5", "3" } };
        String[][] expectFobiddenList = new String[][] { { "3", "4", "5" }, {}, { "1", "3", "5" }, { "1", "4" } };
        List<MatchMergeRule> matchRules = generateMatchMergeRuleList();
        Callback callback = new MatchMergeCallback(10, 2);
        Map<String, String[]> forbiddenListMap = new HashMap<String, String[]>() {

            {
                put("4", new String[] { "3", "5" });
                put("1", new String[] { "3", "4", "5" });
            }
        };
        Iterator<Record> matchMergeInput = generateForbiddenInputRecord(inputData3, forbiddenListMap);
        try {
            MatchMergeAlgorithm buildDQMFB = buildDQMFB(matchRules, callback);
            ((DQMFB) buildDQMFB).setHandleGoldenRecord(true);
            List<Record> matchMergeResults = buildDQMFB.execute(matchMergeInput);
            validateResult(matchMergeResults, callback, expectResult, expectFobiddenList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Iterator<Record> generateInputRecord() {
        List<Record> matchRuleList = new ArrayList<>();
        for (int index = 0; index < inputData1.size(); index++) {
            List<Attribute> attributeList = getAttributList(inputData1.get(index));
            List<DQAttribute<?>> dqAttributeList = getDQAttributList(inputData1.get(index));
            RichRecord richRecord = new RichRecord(attributeList, String.valueOf(currentIndex++), timestamp++, "MFB");
            richRecord.setOriginRow(dqAttributeList);
            matchRuleList.add(richRecord);
        }
        return matchRuleList.iterator();
    }

    private Iterator<Record> generateForbiddenInputRecord(List<String[]> inputData,
            Map<String, String[]> forbiddenMap) {
        List<Record> matchRuleList = new ArrayList<>();
        for (int index = 0; index < inputData.size(); index++) {
            List<Attribute> attributeList = getAttributList(inputData.get(index));
            List<DQAttribute<?>> dqAttributeList = getDQAttributList(inputData.get(index));
            RichRecord richRecord = new RichRecord(attributeList, String.valueOf(currentIndex++), timestamp++, "MFB");
            richRecord.setOriginRow(dqAttributeList);
            matchRuleList.add(richRecord);
        }
        maskForbiddenRecord(matchRuleList, forbiddenMap);
        return matchRuleList.iterator();
    }

    private void maskForbiddenRecord(List<Record> matchRuleList, Map<String, String[]> forbiddenMap) {
        Set<String> forbiddenKeySet = forbiddenMap.keySet();
        for (String keyIndex : forbiddenKeySet) {
            Record goldenRecordLeft = matchRuleList.get(Integer.parseInt(keyIndex) - 1);
            String[] fobiddenIDList = forbiddenMap.get(keyIndex);
            for (String fobiddenRecordIndexRight : fobiddenIDList) {
                Record goldenRecordRight = matchRuleList.get(Integer.parseInt(fobiddenRecordIndexRight) - 1);
                goldenRecordLeft.addInForbiddenList(goldenRecordRight);
            }
        }
    }

    private List<DQAttribute<?>> getDQAttributList(String[] inputData) {
        List<DQAttribute<?>> result = new ArrayList<>();
        DQAttribute<String> dqAttribute = new DQAttribute<>("id", 0, inputData[0], 0);
        dqAttribute.setReferenceValue(inputData[0]);
        dqAttribute.setOriginalValue(inputData[0]);
        result.add(dqAttribute);
        dqAttribute = new DQAttribute<>("name", 1, inputData[1], 1);
        dqAttribute.setReferenceValue(inputData[1]);
        dqAttribute.setOriginalValue(inputData[1]);
        result.add(dqAttribute);
        dqAttribute = new DQAttribute<>("city", 2, inputData[2], 2);
        dqAttribute.setReferenceValue(inputData[2]);
        dqAttribute.setOriginalValue(inputData[2]);
        result.add(dqAttribute);
        dqAttribute = new DQAttribute<>("birthday", 3, inputData[3], 3);
        dqAttribute.setReferenceValue(inputData[3]);
        dqAttribute.setOriginalValue(inputData[3]);
        result.add(dqAttribute);
        dqAttribute = new DQAttribute<>("group", 4, inputData[4], 4);
        dqAttribute.setReferenceValue(inputData[4]);
        dqAttribute.setOriginalValue(inputData[4]);
        result.add(dqAttribute);
        return result;
    }

    private List<Attribute> getAttributList(String[] inputData) {
        List<Attribute> result = new ArrayList<>();
        Attribute dqAttribute = new Attribute("id", 0, inputData[0], 0);
        dqAttribute.setReferenceValue(inputData[0]);
        result.add(dqAttribute);
        dqAttribute = new Attribute("name", 1, inputData[1], 1);
        dqAttribute.setReferenceValue(inputData[1]);
        result.add(dqAttribute);
        dqAttribute = new Attribute("city", 2, inputData[2], 2);
        dqAttribute.setReferenceValue(inputData[2]);
        result.add(dqAttribute);
        dqAttribute = new Attribute("birthday", 3, inputData[3], 3);
        dqAttribute.setReferenceValue(inputData[3]);
        result.add(dqAttribute);
        dqAttribute = new Attribute("group", 4, inputData[4], 4);
        dqAttribute.setReferenceValue(inputData[4]);
        result.add(dqAttribute);
        return result;
    }

    private void validateResult(List<Record> matchMergeResults, Callback callback, String[][] expectResult,
            String[][] expectFobiddenList) {
        ((MatchMergeCallback) callback).onEnd(matchMergeResults);
        for (int index = 0; index < matchMergeResults.size(); index++) {
            Record checkRecord = matchMergeResults.get(index);
            Assert.assertTrue("The ID of master should be same", expectResult[index][0].equals(checkRecord.getId()));
            Set<String> relatedIds = checkRecord.getRelatedIds();
            Assert.assertTrue("The sub ID of record should be same", Arrays.equals(
                    Arrays.copyOfRange(expectResult[index], 1, expectResult[index].length), relatedIds.toArray()));
            List<String> forbiddenList = checkRecord.getForbiddenList();
            Assert.assertTrue("The sub ID of record should be same",
                    Arrays.equals(expectFobiddenList[index], forbiddenList.toArray()));
        }

    }

    private List<MatchMergeRule> generateMatchMergeRuleList() {
        List<MatchMergeRule> matchMergeRuleList = new ArrayList<>();
        MatchMergeRule matchMergeRule = new DQMatchMergeRuleImpl();
        matchMergeRuleList.add(matchMergeRule);
        initMatchRule(null, matchMergeRule, MatchRuleCase1);
        matchMergeRule = new DQMatchMergeRuleImpl();
        matchMergeRuleList.add(matchMergeRule);
        initMatchRule(matchMergeRuleList.get(0), matchMergeRule, MatchRuleCase2);
        return matchMergeRuleList;
    }

    private void initMatchRule(MatchMergeRule originalMatchMergeRule, MatchMergeRule matchMergeRule,
            List<Map<String, String>> matchRuleParameter) {
        Map<FieldMetadata, Declaration> declarations = matchMergeRule.getDeclarations();
        for (Map<String, String> matchKey : matchRuleParameter) {
            DQFieldMetadataImpl dqFieldMetadataImpl = new DQFieldMetadataImpl(matchKey.get("ATTRIBUTE_NAME"));
            if (originalMatchMergeRule != null) {
                List<FieldMetadata> matchFields = originalMatchMergeRule.getMatchFields();
                for (FieldMetadata dqFieldMetadata : matchFields) {
                    if (dqFieldMetadata.getName().equals(dqFieldMetadataImpl.getName())) {
                        dqFieldMetadataImpl = (DQFieldMetadataImpl) dqFieldMetadata;
                        break;
                    }
                }
            }
            String conWeight = matchKey.get("CONFIDENCE_WEIGHT");
            Double dConWeight = conWeight == null ? 0.0d : Double.parseDouble(conWeight);

            String handleNull = matchKey.get("HANDLE_NULL");
            IAttributeMatcher.NullOption eHandleNull = handleNull == null ? IAttributeMatcher.NullOption.nullMatchNull
                    : IAttributeMatcher.NullOption.valueOf(handleNull);

            String threshold = matchKey.get("ATTRIBUTE_THRESHOLD");
            float fthreshold = threshold == null ? 0.0f : Float.parseFloat(threshold);

            String tokenizationType = matchKey.get("TOKENIZATION_TYPE");
            TokenizedResolutionMethod tokenMethod = tokenizationType == null ? TokenizedResolutionMethod.NO
                    : TokenizedResolutionMethod.valueOf(tokenizationType);

            String matchingType = matchKey.get("MATCHING_TYPE");
            AttributeMatcherType matchAlgorithm =
                    matchingType == null ? AttributeMatcherType.DUMMY : AttributeMatcherType.valueOf(matchingType);

            String matchingParameter = matchKey.get("PARAMETER");

            String survivorshipFunction = matchKey.get("SURVIVORSHIP_FUNCTION");
            SurvivorShipAlgorithmEnum mergeAlgorithm = survivorshipFunction == null
                    ? SurvivorShipAlgorithmEnum.MOST_COMMON : SurvivorShipAlgorithmEnum.valueOf(survivorshipFunction);

            String recordMatchThreshold = matchKey.get("RECORD_MATCH_THRESHOLD");
            if (recordMatchThreshold != null) {
                ((DQMatchMergeRuleImpl) matchMergeRule).setMatchThreshold(Double.parseDouble(recordMatchThreshold));
            }

            Declaration declaration = new Declaration(dConWeight, eHandleNull, fthreshold, tokenMethod, matchAlgorithm,
                    matchingParameter, mergeAlgorithm, matchingParameter, false);
            declarations.put(dqFieldMetadataImpl, declaration);
        }
    }

    private MatchMergeAlgorithm buildMFB(float attrThreshold, double minConfidence,
            SurvivorShipAlgorithmEnum mergeAlgo) {
        return MFB.build( //
                new AttributeMatcherType[] { AttributeMatcherType.LEVENSHTEIN }, // algorithms
                new String[] { "" }, // algo params //$NON-NLS-1$
                new float[] { attrThreshold }, // thresholds
                minConfidence, // min confidence
                new SurvivorShipAlgorithmEnum[] { mergeAlgo }, // merge algos
                new String[] { "" }, // merge params //$NON-NLS-1$
                new double[] { 1 }, // weights
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchAll }, // null option
                new SubString[] { SubString.NO_SUBSTRING }, // substring option
                "MFB" // source //$NON-NLS-1$
        );
    }

    /**
     * Build DQMFB instance with complex match rules
     *
     * @param matchRules
     * @param callback
     * @return
     * @throws Exception
     */
    public static MatchMergeAlgorithm buildDQMFB(List<MatchMergeRule> matchRules, Callback callback) throws Exception {
        RecordMatcherType recordLinkageAlgorithm = matchRules.get(0).getRecordLinkageAlgorithm();
        List<FieldMetadata> allMatchFields = getAllMatchFields(matchRules);
        List<List<Map<String, String>>> multiMatchRules = getMultiMatchRules(matchRules, allMatchFields);
        Map<Integer, SurvivorshipFunction> defaultSurviorshipRules =
                getDefaultSurviorshipRules(matchRules.get(0), allMatchFields);
        List<SurvivorshipFunction[]> multiSurvivorshipFunctions =
                getMultiSurvivorshipFunctions(matchRules, allMatchFields);

        AnalysisSwooshMatchRecordGrouping recordGrouping =
                new AnalysisSwooshMatchRecordGrouping(new DefaultMatchGroupResultConsumer());
        recordGrouping.setRecordLinkAlgorithm(recordLinkageAlgorithm);
        for (List<Map<String, String>> matchRule : multiMatchRules) {
            recordGrouping.addMatchRule(matchRule);
        }
        recordGrouping.initialize();
        CombinedRecordMatcher combinedMatcher = recordGrouping.getCombinedRecordMatcher();
        List<IRecordMatcher> matchers = combinedMatcher.getMatchers();

        int size = multiSurvivorshipFunctions.get(0).length;
        String[] parameters = new String[size];
        SurvivorShipAlgorithmEnum[] typeMergeTable = new SurvivorShipAlgorithmEnum[size];

        SurvivorShipAlgorithmParams matchMergeParam = new SurvivorShipAlgorithmParams();
        matchMergeParam.setRecordMatcher(combinedMatcher);
        matchMergeParam.setDefaultSurviorshipRules(defaultSurviorshipRules);
        for (int i = 0; i < matchers.size(); i++) {
            matchMergeParam.getSurvivorshipAlgosMap().put(matchers.get(i), multiSurvivorshipFunctions.get(i));
        }
        DQMFBRecordMerger recordMerger =
                new DQMFBRecordMerger(MERGED_RECORD_SOURCE, parameters, typeMergeTable, matchMergeParam);

        if (callback instanceof GroupingLoggerCallback) {
            TSwooshGrouping<Object> tswooshGrouping = new TSwooshGrouping<Object>(recordGrouping);
            BidiMultiMap<String, String> oldGID2New = (BidiMultiMap<String, String>) tswooshGrouping.getOldGID2New();
            ((GroupingLoggerCallback) callback)
                    .setGroupingCallback(new GroupingCallBack<Object>(oldGID2New, recordGrouping));
        }

        return new DQGoldenRecordMFB(combinedMatcher, recordMerger, callback);
    }

    /**
     * Get all fields including task fields and match fields defined in all rules
     *
     * @param matchRules
     * @return
     */
    public static List<FieldMetadata> getAllMatchFields(List<MatchMergeRule> matchRules) {
        List<FieldMetadata> allMatchFields = new ArrayList<FieldMetadata>();
        allMatchFields.addAll(matchRules.get(0).getTaskFields());
        for (MatchMergeRule rule : matchRules) {
            for (FieldMetadata field : rule.getMatchFields()) {
                if (!allMatchFields.contains(field)) {
                    allMatchFields.add(field);
                }
            }
        }
        return allMatchFields;
    }

    private static List<List<Map<String, String>>> getMultiMatchRules(List<MatchMergeRule> matchRules,
            List<FieldMetadata> allMatchFields) {
        List<List<Map<String, String>>> multiMatchRules = new ArrayList<List<Map<String, String>>>();
        for (MatchMergeRule rule : matchRules) {
            multiMatchRules.add(toMatchRuleParameter(rule, allMatchFields));
        }
        return multiMatchRules;
    }

    private static List<Map<String, String>> toMatchRuleParameter(MatchMergeRule rule,
            List<FieldMetadata> allMatchFields) {
        List<Map<String, String>> matchRule = new ArrayList<Map<String, String>>();
        Map<FieldMetadata, Map<String, String>> declarationMap = getInitialDeclarationMap(rule, allMatchFields);

        Map<FieldMetadata, Declaration> declarations = rule.getDeclarations();
        for (Map.Entry<FieldMetadata, Declaration> entry : declarations.entrySet()) {
            FieldMetadata field = entry.getKey();
            Declaration declaration = entry.getValue();
            Map<String, String> map = declarationMap.get(field);
            map.put(IRecordGrouping.MATCHING_TYPE, declaration.matchAlgorithm.getComponentValue());
            map.put(IRecordGrouping.JAR_PATH, declaration.getMatchCustomJarPath());
            map.put(IRecordGrouping.CUSTOMER_MATCH_CLASS, declaration.getMatchCustomClassName());
            map.put(IRecordGrouping.TOKENIZATION_TYPE, declaration.tokenMethod.getComponentValue());
            map.put(IRecordGrouping.ATTRIBUTE_THRESHOLD, String.valueOf(declaration.threshold));
            map.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(declaration.confidenceWeight));
            map.put(IRecordGrouping.HANDLE_NULL, declaration.handleNull.toString());
            map.put(IRecordGrouping.RECORD_MATCH_THRESHOLD, String.valueOf(rule.getMatchThreshold()));
        }
        matchRule.addAll(declarationMap.values());

        return matchRule;
    }

    private static Map<FieldMetadata, Map<String, String>> getInitialDeclarationMap(MatchMergeRule rule,
            List<FieldMetadata> allMatchFields) {
        Map<FieldMetadata, Map<String, String>> initialDeclarationMap =
                new LinkedHashMap<FieldMetadata, Map<String, String>>();
        // Set Dummy Match Merge Rules Map
        Map<String, String> dummyDeclaration;
        for (int i = 0; i < allMatchFields.size(); i++) {
            FieldMetadata field = allMatchFields.get(i);
            dummyDeclaration = new HashMap<String, String>();
            dummyDeclaration.put(IRecordGrouping.COLUMN_IDX, String.valueOf(i));
            dummyDeclaration.put(IRecordGrouping.ATTRIBUTE_NAME, getAttributeKey(field));
            dummyDeclaration.put(IRecordGrouping.MATCHING_TYPE, AttributeMatcherType.DUMMY.name());
            initialDeclarationMap.put(field, dummyDeclaration);
        }

        return initialDeclarationMap;
    }

    /**
     * Get name set in Attribute, like "Product/Id", "ProductFamily/Name"
     *
     * @param field
     * @return
     */
    public static String getAttributeKey(FieldMetadata field) {
        return field.getContainingType() + "/" + field.getName();
    }

    private static Map<Integer, SurvivorshipFunction> getDefaultSurviorshipRules(MatchMergeRule rule,
            List<FieldMetadata> allMatchFields) {
        Map<Integer, SurvivorshipFunction> defaultSurviorshipRules = new LinkedHashMap<Integer, SurvivorshipFunction>();
        int inx = 0;
        for (SurvivorshipFunction function : getParticularDefaultSurvivorshipMap(rule,
                getInitialSurvivorshipMap(rule, allMatchFields)).values()) {
            defaultSurviorshipRules.put(inx, function);
            inx++;
        }
        return defaultSurviorshipRules;
    }

    private static Map<FieldMetadata, SurvivorshipFunction> getInitialSurvivorshipMap(MatchMergeRule rule,
            List<FieldMetadata> allMatchFields) {
        Map<FieldMetadata, SurvivorshipFunction> initialSurvivorshipMap =
                new LinkedHashMap<FieldMetadata, SurvivorshipFunction>();
        SurvivorShipAlgorithmParams params = new SurvivorShipAlgorithmParams();
        SurvivorshipFunction function;
        for (FieldMetadata field : allMatchFields) {
            function = params.new SurvivorshipFunction();
            function.setSurvivorShipKey(getAttributeKey(field));
            function.setSurvivorShipAlgoEnum(rule.getDefaultMergeAlgorithm(field));
            function.setParameter(rule.getDefaultMergeParameter(field));
            initialSurvivorshipMap.put(field, function);
        }

        return initialSurvivorshipMap;
    }

    private static Map<FieldMetadata, SurvivorshipFunction> getParticularDefaultSurvivorshipMap(MatchMergeRule rule,
            Map<FieldMetadata, SurvivorshipFunction> survivorshipMap) {
        Map<FieldMetadata, SurvivorshipFunction> particularDefaultSurvivorshipMap =
                new LinkedHashMap<FieldMetadata, SurvivorshipFunction>();
        survivorshipMap.forEach((field, survivorshipFunction) -> {
            if (rule.getParticularDefaultSurvivorShipAlgorithm(field) != null) {
                survivorshipFunction.setSurvivorShipAlgoEnum(rule.getParticularDefaultSurvivorShipAlgorithm(field));
                survivorshipFunction.setParameter(rule.getParticularDefaultSurvivorShipParameter(field));
            }
            particularDefaultSurvivorshipMap.put(field, survivorshipFunction);
        });
        return particularDefaultSurvivorshipMap;
    }

    private static List<SurvivorshipFunction[]> getMultiSurvivorshipFunctions(List<MatchMergeRule> matchRules,
            List<FieldMetadata> allMatchFields) {
        List<SurvivorshipFunction[]> functionsList = new ArrayList<SurvivorshipFunction[]>();
        for (MatchMergeRule rule : matchRules) {
            functionsList.add(toSurvivorshipFunctionParameger(rule, allMatchFields));
        }
        return functionsList;
    }

    private static SurvivorshipFunction[] toSurvivorshipFunctionParameger(MatchMergeRule rule,
            List<FieldMetadata> allMatchFields) {
        Map<FieldMetadata, SurvivorshipFunction> functionMap =
                getParticularDefaultSurvivorshipMap(rule, getInitialSurvivorshipMap(rule, allMatchFields));
        SurvivorshipFunction[] functions = new SurvivorshipFunction[functionMap.size()];
        Map<FieldMetadata, Declaration> declarations = rule.getDeclarations();
        for (Map.Entry<FieldMetadata, Declaration> entry : declarations.entrySet()) {
            FieldMetadata field = entry.getKey();
            Declaration declaration = entry.getValue();
            SurvivorshipFunction function = functionMap.get(field);
            function.setSurvivorShipAlgoEnum(declaration.mergeAlgorithm);
            function.setParameter(declaration.mergeParameter);
        }
        functionMap.values().toArray(functions);
        return functions;
    }

    /**
     * Default consumer which does nothing, it is used to avoid NPE in AnalysisSwooshMatchRecordGrouping#outputRow()
     *
     */
    private static class DefaultMatchGroupResultConsumer extends MatchGroupResultConsumer {

        public DefaultMatchGroupResultConsumer() {
            this(false);
        }

        private DefaultMatchGroupResultConsumer(boolean isKeepDataInMemory) {
            super(isKeepDataInMemory);
        }

        @Override
        public void handle(Object row) {
            // Do nothing
        }
    }

    public static class MatchMergeCallback extends GroupingLoggerCallback {

        private int maxGroupSize = 50;

        private long matchCandidateNumberCount = 0;

        private long currentMatchCount = 0;

        public MatchMergeCallback(int maxGroupSize, long matchCandidateNumberCount) {
            this.maxGroupSize = maxGroupSize;
            this.matchCandidateNumberCount = matchCandidateNumberCount;
        }

        @Override
        public void onMatch(Record record1, Record record2, MatchResult matchResult) {
            super.onMatch(record1, record2, matchResult);
            // This assumes record1 and record2 are not both golden record (should not occur).
            if (record1.getId().equals(record1.getGroupId())) {
                currentMatchCount += record2.getRelatedIds().size();
            } else if (record2.getId().equals(record2.getGroupId())) {
                currentMatchCount += record1.getRelatedIds().size();
            }
        }

        @Override
        public void onNewMerge(Record record) {
            super.onNewMerge(record);
            if (record.getRelatedIds().size() > maxGroupSize) {
                throw new RuntimeException("Too many records in group (" + record.getRelatedIds().size() + " > "
                        + maxGroupSize + "). Please review configuration.");
            }
        }

        @Override
        public boolean isInterrupted() {
            return currentMatchCount >= matchCandidateNumberCount;
        }

        public void onEnd(List<Record> matchMergeResults) {
            for (Record record : matchMergeResults) {
                super.onEndRecord(record);
            }
        }

    }

}
