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
package org.talend.dataquality.record.linkage.grouping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

public class SwooshRecordGroupingTest {

    private static Logger log = LoggerFactory.getLogger(SwooshRecordGroupingTest.class);

    /**
     * The input data.
     */
    private List<String[]> inputList = null;

    private AnalysisSwooshMatchRecordGrouping recordGroup = null;

    private static final String columnDelimiter = "|"; //$NON-NLS-1$

    private List<Object[]> groupingRecords = new ArrayList<Object[]>();

    @Before
    public void setUp() throws Exception {
        groupingRecords.clear();
    }

    @Test
    public void testDoGroup() throws IOException {
        // account_num , last name, first name, middle initial, address, city, state, zipcode, blocking key
        // Read input data file.
        InputStream in = this.getClass().getResourceAsStream("incoming_customers_swoosh1.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        // set the matching parameters
        // matching parameters for lname
        recordGroup = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                groupingRecords.add(row);
            }

        };
        ((AbstractRecordGrouping) recordGroup).setOrginalInputColumnSize(9);
        recordGroup.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        SurvivorShipAlgorithmParams survAlgParams = new SurvivorShipAlgorithmParams();
        SurvivorshipFunction func = survAlgParams.new SurvivorshipFunction();
        func.setParameter(""); //$NON-NLS-1$
        func.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        survAlgParams.setSurviorShipAlgos(new SurvivorshipFunction[] { func });
        // Set default survivorship functions.
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<Integer, SurvivorshipFunction>();
        SurvivorshipFunction survFunc = survAlgParams.new SurvivorshipFunction();
        survFunc.setParameter(StringUtils.EMPTY);
        survFunc.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.LONGEST);
        defaultSurvRules.put(1, survFunc);
        survAlgParams.setDefaultSurviorshipRules(defaultSurvRules);

        recordGroup.setSurvivorShipAlgorithmParams(survAlgParams);
        recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        List<Map<String, String>> matchingRule = new ArrayList<Map<String, String>>();

        Map<String, String> lnameRecords = createTmpMap(null, "1", null, "ID", "0", "JARO_WINKLER", "NO", "1", null, null, null);

        matchingRule.add(lnameRecords);

        recordGroup.addMatchRule(matchingRule);
        try {
            recordGroup.initialize();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }

        recordGroup.setIsOutputDistDetails(true);
        recordGroup.setAcceptableThreshold(0.95f);
        // loop on all input rows.
        try {
            for (String[] inputRow : inputList) {
                recordGroup.doGroup(inputRow);
            }
            recordGroup.end();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        // Assertions
        Assert.assertTrue(groupingRecords.size() > 0);
        for (Object[] rds : groupingRecords) {
            if (rds[0].equals("2") && rds[11].equals("true")) { //$NON-NLS-1$ //$NON-NLS-2$
                // The group size should be 2 for master record which id is 2
                Assert.assertEquals(2, Integer.valueOf((String) rds[rds.length - 5]).intValue());
            } else if (rds[0].equals("7") && rds[11].equals("true")) { //$NON-NLS-1$ //$NON-NLS-2$
                // The group size should be 2 for master record which id is 7
                Assert.assertEquals(2, Integer.valueOf((String) rds[rds.length - 5]).intValue());
            } else if (rds[0].equals("1") && rds[11].equals("true")) { //$NON-NLS-1$ //$NON-NLS-2$
                // The group size should be 3 for master record which id is 1
                Assert.assertEquals(3, Integer.valueOf((String) rds[rds.length - 5]).intValue());
            }
        }
    }

    @Test
    public void testDoGroupMergeValues() throws IOException {
        InputStream in = this.getClass().getResourceAsStream("incoming_customers_swoosh2.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }
        recordGroup = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                groupingRecords.add(row);
            }

        };
        recordGroup.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        SurvivorShipAlgorithmParams survivorShipAlgorithmParams = new SurvivorShipAlgorithmParams();
        SurvivorshipFunction func = survivorShipAlgorithmParams.new SurvivorshipFunction();
        func.setParameter(""); //$NON-NLS-1$
        func.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        survivorShipAlgorithmParams.setSurviorShipAlgos(new SurvivorshipFunction[] { func });
        recordGroup.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);

        // Set default survivorship functions.
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<Integer, SurvivorshipFunction>();
        SurvivorshipFunction survFunc = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc.setParameter(StringUtils.EMPTY);
        survFunc.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        defaultSurvRules.put(0, survFunc);

        SurvivorshipFunction survFunc2 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc2.setParameter(StringUtils.EMPTY);
        survFunc2.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.LONGEST);
        defaultSurvRules.put(2, survFunc2);

        SurvivorshipFunction survFunc3 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc3.setParameter(StringUtils.EMPTY);
        survFunc3.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        defaultSurvRules.put(3, survFunc3);

        SurvivorshipFunction survFunc4 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc4.setParameter(StringUtils.EMPTY);
        survFunc4.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.LARGEST);
        defaultSurvRules.put(4, survFunc4);

        SurvivorshipFunction survFunc5 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc5.setParameter(StringUtils.EMPTY);
        survFunc5.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_RECENT);
        defaultSurvRules.put(5, survFunc5);

        SurvivorshipFunction survFunc6 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc6.setParameter(StringUtils.EMPTY);
        survFunc6.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        defaultSurvRules.put(6, survFunc6);

        survivorShipAlgorithmParams.setDefaultSurviorshipRules(defaultSurvRules);

        recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        ((AbstractRecordGrouping) recordGroup).setOrginalInputColumnSize(9);

        List<Map<String, String>> matchingRule = new ArrayList<Map<String, String>>();

        Map<String, String> lnameRecords = createTmpMap(null, "0.9", null, "NAME", "1", "JARO_WINKLER", "NO", "1", null, null,
                null);
        matchingRule.add(lnameRecords);

        recordGroup.addMatchRule(matchingRule);
        try {
            recordGroup.initialize();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }

        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("5", "yyyy-mm-dd hh:mm:ss");
        recordGroup.setColumnDatePatternMap(dataMap);

        recordGroup.setIsOutputDistDetails(true);
        recordGroup.setAcceptableThreshold(0.95f);
        ((AbstractRecordGrouping) recordGroup).setOrginalInputColumnSize(8);

        // loop on all input rows.
        try {
            for (String[] inputRow : inputList) {
                recordGroup.doGroup(inputRow);
            }
            recordGroup.end();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        // Assertions
        Assert.assertTrue(groupingRecords.size() > 0);
        for (Object[] rds : groupingRecords) {
            if (rds[10].equals("true")) { //$NON-NLS-1$
                // Master record's group size is 4
                if ("null".equals(rds[1])) {
                    Assert.assertEquals(2, Integer.valueOf((String) rds[rds.length - 5]).intValue());
                } else {
                    Assert.assertEquals(4, Integer.valueOf((String) rds[rds.length - 5]).intValue());
                    // Group quality.
                    Assert.assertEquals(0.9666666746139526, Double.valueOf((String) rds[rds.length - 2]).doubleValue(), 0d);
                    // Assert the merged value is the "most common" value.
                    Assert.assertEquals("Amburgay", rds[1]);
                    // Longest
                    Assert.assertEquals("Gregory", rds[2]);
                    // Most common
                    Assert.assertEquals("R.", rds[3]);
                    // Largest
                    Assert.assertEquals("4151", rds[4]);
                    // Most recent. This is the logic from MDM , shoudn't be 2015-10-1 ?
                    Assert.assertEquals("2016-03-03 11:15:20.111", rds[5]);
                }
            }
        }
    }

    @Test
    public void testSwooshIntMatchGroup()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("customers_swoosh_tmatch.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // assert
        int n = 0;
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.GRP_SIZE == 7) {
                Assert.assertEquals("USAUSAUSAUSAUSAUSAUSA", one.country);

                for (int i = n + 1; i < n + 8; i++) {
                    Assert.assertEquals("USA", groupRows_tMatchGroup_1.get(i).country);
                }
                break;
            }
            n++;
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm

        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_tmatch.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.customer_id == 12 && one.MASTER) {
                Assert.assertTrue(StringUtils.equals("BB", one.country));
                Assert.assertTrue(StringUtils.equals("YY", one.city));
                Assert.assertTrue(5 == one.GRP_SIZE);
            } else if (one.customer_id == 7 && one.MASTER) {
                Assert.assertTrue(StringUtils.equals("FFF", one.country));
                Assert.assertTrue(StringUtils.equals("G", one.city));
                Assert.assertTrue(5 == one.GRP_SIZE);
            } else if (one.customer_id == 4 && one.MASTER) {
                Assert.assertTrue(StringUtils.equals("HH", one.country));
                Assert.assertTrue(StringUtils.equals("E", one.city));
                Assert.assertTrue(2 == one.GRP_SIZE);
            }
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroupWithReferenceColumn()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("MostRecent", "0.1", "", "name", "1", "Levenshtein", "NO", 1 + "", "nullMatchNull",
                0.5 + "", "TSWOOSH_MATCHER", "date1", "3");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2StructForReferenceColumn> masterRows_tMatchGroup_1 = new ArrayList<row2StructForReferenceColumn>();
        // all rows in a group
        final List<row2StructForReferenceColumn> groupRows_tMatchGroup_1 = new ArrayList<row2StructForReferenceColumn>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponentForReferenceColumn(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm

        Map<String, String> columnWithType_tMatchGroup_1 = fillColumnForReference("id_Integer", "id_String", "id_Integer",
                "id_Date", "id_Date", "id_String", "id_Integer", "id_Boolean", "id_Double", "id_Double");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumnForReference("0", "1", "2", "3", "4", "5", "6", "7", "8",
                "9");
        Map<String, String> columnDatePatternMap = new HashMap<>();
        columnDatePatternMap.put("3", "yyyy-MM-dd");
        columnDatePatternMap.put("4", "yyyy-MM-dd");
        recordGroupImp_tMatchGroup_1.setColumnDatePatternMap(columnDatePatternMap);

        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        java.util.List<java.util.Map<String, String>> particularSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "NUMBER");
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("REFERENCE_COLUMN", "date1");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "MostAncient");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);
        realSurShipMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("REFERENCE_COLUMN", "date2");
        realSurShipMap_tMatchGroup_1.put("PARAMETER", ",");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "MostRecent");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "DATE");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);
        realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("INPUT_COLUMN", "date1");
        realSurShipMap_tMatchGroup_1.put("REFERENCE_COLUMN", "date2");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "MostRecent");
        particularSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(
                        (org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1,
                        particularSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1, columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType(true);
        recordGroupImp_tMatchGroup_1.setIsPassOriginalValue(false);
        recordGroupImp_tMatchGroup_1.setIsStoreOndisk(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(5);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_tmatch_reference_column.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, ";");
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        SimpleDateFormat simFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (row2StructForReferenceColumn one : groupRows_tMatchGroup_1) {
            if (one.id == 2 && one.MASTER) {
                Assert.assertTrue(StringUtils.equals("bb", one.name));
                Assert.assertTrue(StringUtils.equals("2", one.number.toString()));
                Assert.assertTrue(StringUtils.equals("2005-05-05", simFormat.format(one.date1)));
                Assert.assertTrue(StringUtils.equals("2015-05-15", simFormat.format(one.date2)));
                Assert.assertTrue(2 == one.GRP_SIZE);
            } else if (one.id == 4 && one.MASTER) {
                Assert.assertTrue(StringUtils.equals("cc", one.name));
                Assert.assertTrue(StringUtils.equals("4", one.number.toString()));
                Assert.assertTrue(StringUtils.equals("2004-04-04", simFormat.format(one.date1)));
                Assert.assertTrue(StringUtils.equals("2014-04-14", simFormat.format(one.date2)));
                Assert.assertTrue(1 == one.GRP_SIZE);
            } else if (one.id == 3 && one.MASTER) {
                Assert.assertTrue(StringUtils.equals("aaa", one.name));
                Assert.assertTrue(StringUtils.equals("3", one.number.toString()));
                Assert.assertTrue(StringUtils.equals("2001-01-01", simFormat.format(one.date1)));
                Assert.assertTrue(StringUtils.equals("2013-03-13", simFormat.format(one.date2)));
                Assert.assertTrue(2 == one.GRP_SIZE);
            }
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup_3groups()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_swoosh.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // System.out.println("swoosh with multipass of 3 groups");
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.customer_id == 12 && one.MASTER) {
                Assert.assertTrue(StringUtils.equals("BB", one.country));
                Assert.assertTrue(StringUtils.equals("YY", one.city));
                Assert.assertTrue(5 == one.GRP_SIZE);
            } else if (one.customer_id == 7 && one.MASTER) {
                Assert.assertTrue(StringUtils.equals("FFF", one.country));
                Assert.assertTrue(StringUtils.equals("G", one.city));
                Assert.assertTrue(5 == one.GRP_SIZE);
            } else if (one.customer_id == 4 && one.MASTER) {
                Assert.assertTrue(StringUtils.equals("HH", one.country));
                Assert.assertTrue(StringUtils.equals("E", one.city));
                Assert.assertTrue(2 == one.GRP_SIZE);
            }
            if (one.GRP_SIZE == 7) {
                Assert.assertTrue(StringUtils.equals("FFF", one.country) || StringUtils.equals("BB", one.country));
                Assert.assertTrue(StringUtils.equals("G", one.city) || StringUtils.equals("AAA", one.city));
            }
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup_withNoNewMasterIn2ndPass()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_swoosh_noNew.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.GRP_SIZE == 2) {
                Assert.assertTrue(StringUtils.equals("B", one.country) || StringUtils.equals("F", one.country));
                Assert.assertTrue(StringUtils.equals("YY", one.city) || StringUtils.equals("WW", one.city));
            }
        }
    }

    @Test
    public void testSwooshIntMatchGroup_withBlocks()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_with_blocks.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        List<String[]> inputList2 = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            if (StringUtils.equals("F", fields[1])) {
                inputList.add(fields);
            } else {
                inputList2.add(fields);
            }
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while
        recordGroupImp_tMatchGroup_1.end();

        for (String[] inputRow : inputList2) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while
        recordGroupImp_tMatchGroup_1.end();

        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 10);
        boolean hasGroup5 = false, hasGroup2 = false;
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE+
            // "--" + one.MASTER);
            if (one.customer_id == 100000) {
                Assert.assertTrue(one.MASTER);
                Assert.assertTrue(1 == one.GRP_SIZE);
            } else if ("CCCCCCCCCCCCCCC".equals(one.country)) {
                hasGroup5 = true;
                Assert.assertTrue(one.MASTER);
                Assert.assertTrue(5 == one.GRP_SIZE);
            } else if ("AAAAAA".equals(one.country)) {
                hasGroup2 = true;
                Assert.assertTrue(one.MASTER);
                Assert.assertTrue(2 == one.GRP_SIZE);
            }
        }
        Assert.assertTrue(hasGroup5);
        Assert.assertTrue(hasGroup2);
    }

    @Test
    public void testSwooshIntMatchGroup_withoutBlocks()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_without_blocks.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while
        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() == 14);
        boolean hasGroup5 = false, hasGroup7 = false;
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE+
            // "--" + one.MASTER);
            if ("AAAAAAAAAAAAAAA".equals(one.country)) {
                hasGroup5 = true;
                Assert.assertTrue(one.MASTER);
                Assert.assertTrue(5 == one.GRP_SIZE);
            } else if ("CCCCCCCCCCCCCCCCCCCCC".equals(one.country)) {
                hasGroup7 = true;
                Assert.assertTrue(one.MASTER);
                Assert.assertTrue(7 == one.GRP_SIZE);
            }
        }
        Assert.assertTrue(hasGroup5);
        Assert.assertTrue(hasGroup7);
    }

    @Test
    public void testSetDefaultSurvivorshipRules() {
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        Map<String, String> realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "BOOLEAN");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);
        realSurShipMap_tMatchGroup_1 = new HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "MostCommon");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "STRING");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);
        realSurShipMap_tMatchGroup_1 = new HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "MostRecent");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "NUMBER");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);

        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("MostCommon", "1", "", "stu_Address", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        Map<String, String> columnWithType_tMatchGroup_1 = new HashMap<String, String>();

        Map<String, String> columnWithIndex_tMatchGroup_1 = new HashMap<String, String>();

        columnWithType_tMatchGroup_1.put("id", "id_Integer");
        columnWithIndex_tMatchGroup_1.put("id", "0");
        columnWithType_tMatchGroup_1.put("stu_Gender", "id_String");
        columnWithIndex_tMatchGroup_1.put("stu_Gender", "1");
        columnWithType_tMatchGroup_1.put("stu_Address", "id_String");
        columnWithIndex_tMatchGroup_1.put("stu_Address", "2");
        columnWithType_tMatchGroup_1.put("stu_Text", "id_String");
        columnWithIndex_tMatchGroup_1.put("stu_Text", "3");
        columnWithType_tMatchGroup_1.put("stu_SmallInt", "id_Short");
        columnWithIndex_tMatchGroup_1.put("stu_SmallInt", "4");
        columnWithType_tMatchGroup_1.put("stu_tinyint", "id_Byte");
        columnWithIndex_tMatchGroup_1.put("stu_tinyint", "5");
        columnWithType_tMatchGroup_1.put("stu_bigInt", "id_Long");
        columnWithIndex_tMatchGroup_1.put("stu_bigInt", "6");
        columnWithType_tMatchGroup_1.put("stu_double", "id_Double");
        columnWithIndex_tMatchGroup_1.put("stu_double", "7");
        columnWithType_tMatchGroup_1.put("stu_BIT", "id_Boolean");
        columnWithIndex_tMatchGroup_1.put("stu_BIT", "8");
        columnWithType_tMatchGroup_1.put("stu_Char", "id_String");
        columnWithIndex_tMatchGroup_1.put("stu_Char", "9");
        columnWithType_tMatchGroup_1.put("stu_date", "id_Date");
        columnWithIndex_tMatchGroup_1.put("stu_date", "10");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithIndex_tMatchGroup_1.put("GID", "11");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "12");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithIndex_tMatchGroup_1.put("MASTER", "13");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithIndex_tMatchGroup_1.put("SCORE", "14");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "15");

        AnalysisSwooshMatchRecordGrouping analysisSwooshMatchRecordGrouping = new AnalysisSwooshMatchRecordGrouping();
        analysisSwooshMatchRecordGrouping.setOrginalInputColumnSize(11);
        analysisSwooshMatchRecordGrouping.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            analysisSwooshMatchRecordGrouping.addMatchRule(matcherList);
        }
        try {
            analysisSwooshMatchRecordGrouping.initialize();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Assert.fail("initial failed :" + e.getMessage());
        }
        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(analysisSwooshMatchRecordGrouping, matchingRulesAll_tMatchGroup_1,
                        defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1, columnWithIndex_tMatchGroup_1);

        Map<Integer, SurvivorshipFunction> defaultSurviorshipRules = survivorShipAlgorithmParams_tMatchGroup_1
                .getDefaultSurviorshipRules();
        Iterator<Integer> keys = defaultSurviorshipRules.keySet().iterator();
        boolean containID = false, containAdd = false, containBIT = false;
        while (keys.hasNext()) {
            Integer next = keys.next();
            SurvivorshipFunction survivorshipFunction = defaultSurviorshipRules.get(next);
            Assert.assertNotNull(survivorshipFunction);
            Assert.assertNotEquals("", survivorshipFunction.getSurvivorShipKey());
            Assert.assertNotNull(survivorshipFunction.getSurvivorShipKey());
            if ("id".equals(survivorshipFunction.getSurvivorShipKey())) {
                containID = true;
                Assert.assertTrue(next.intValue() == 0);
                Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_RECENT, survivorshipFunction.getSurvivorShipAlgoEnum());
            } else if ("stu_Address".equals(survivorshipFunction.getSurvivorShipKey())) {
                containAdd = true;
                Assert.assertTrue(next.intValue() == 2);
                Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunction.getSurvivorShipAlgoEnum());
            } else if ("stu_BIT".equals(survivorshipFunction.getSurvivorShipKey())) {
                containBIT = true;
                Assert.assertTrue(next.intValue() == 8);
                Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunction.getSurvivorShipAlgoEnum());
            }
        }
        Assert.assertTrue(containID);
        Assert.assertTrue(containAdd);
        Assert.assertTrue(containBIT);
    }

    @Test
    public void testSwooshMultipasstMatchGroup_oneRecord()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_onerecord.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() == 1);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE+
            // "--" + one.MASTER);
            Assert.assertTrue(one.MASTER);
            Assert.assertTrue(1 == one.GRP_SIZE);
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup_differentRecord()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_different.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // System.out.println("swoosh with multipass :lost some record");
        Assert.assertTrue(groupRows_tMatchGroup_1.size() == 10);
        boolean hasGroup9 = false;
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE+
            // "--" + one.MASTER);
            if (one.MASTER) {
                hasGroup9 = true;
                Assert.assertTrue(one.MASTER);
                Assert.assertTrue(9 == one.GRP_SIZE);
            }
        }
        Assert.assertTrue(hasGroup9);
    }

    @Test
    public void testSwooshMultipasstMatchGroup_score()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "0.86", "", "country", "2", "Jaro", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_score.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        boolean hasGroup5 = false;
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE+
            // "--" + one.MASTER);
            Assert.assertTrue(one.SCORE > 0);
            if (one.MASTER) {
                hasGroup5 = true;
                Assert.assertTrue(5 == one.GRP_SIZE);
            }
        }
        Assert.assertTrue(hasGroup5);
    }

    @Test
    public void testSwooshIntMatchGroup_multipleRules()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>(3);
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "stuID", "0", "dummy", "NO", 0 + "", "nullMatchNull", null,
                null);
        matcherList_tMatchGroup_1.add(0, tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1", "", "stuAddress", "1", "Exact", "NO", 1 + "", "nullMatchNull",
                0.86 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(1, tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1", "", "stuProvinceID", "2", "Exact", "NO", 2 + "", "nullMatchNull",
                0.86 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(2, tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();

        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "stuProvinceID", "2", "dummy", "NO", 1 + "",
                "nullMatchNull", 0 + "", null);
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "stuID", "0", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "stuAddress", "1", "dummy", "NO", 0 + "", "nullMatchNull",
                null, null);
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        Collections.sort(matcherList_tMatchGroup_1, new Comparator<Map<String, String>>() {

            @Override
            public int compare(Map<String, String> map1, Map<String, String> map2) {
                return map1.get("COLUMN_IDX").compareTo(map2.get("COLUMN_IDX"));
            }
        });

        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multiRule_swoosh.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1); // assert
        int groupOfOne = 0;
        int groupOfTwo = 0;
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.GRP_SIZE == 1) {
                groupOfOne++;
            } else if (one.GRP_SIZE == 2) {
                groupOfTwo++;
            }
        }
        Assert.assertTrue(groupOfOne == 10);
        Assert.assertTrue(groupOfTwo == 2);
    }

    @Test
    public void testSwooshIntMatchGroup_multipleRulesCase2()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>(3);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "0.4", "", "name", "1", "Levenshtein", "NO", "1", "nullMatchNull",
                "0.3", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(0, tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "address", "2", "Exact", "NO", "1", "nullMatchNull", "0.3",
                "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(1, tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("MostCommon", "1.0", "", "provinceID", "3", "dummy", "NO", "0", "nullMatchNone", null,
                null);
        matcherList_tMatchGroup_1.add(2, tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();

        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "0.4", "", "name", "1", "dummy", "NO", "0", "nullMatchNull", null,
                null);
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "address", "2", "dummy", "NO", "0", "nullMatchNull", null,
                null);
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "provinceID", "3", "Exact", "NO", "1", "nullMatchNone",
                "0.3", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        Collections.sort(matcherList_tMatchGroup_1, new Comparator<Map<String, String>>() {

            @Override
            public int compare(Map<String, String> map1, Map<String, String> map2) {
                return map1.get("COLUMN_IDX").compareTo(map2.get("COLUMN_IDX"));
            }
        });

        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row4Struct> masterRows_tMatchGroup_1 = new ArrayList<row4Struct>();
        // all rows in a group
        final List<row4Struct> groupRows_tMatchGroup_1 = new ArrayList<row4Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Double> indexMap_tMatchGroup_1 = new HashMap<String, Double>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponentForRow4(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_Integer",
                "id_String", "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // id name address provinceID
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(4);
        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multiRule_swoosh_case2.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1); // assert
        int groupOfOne = 0;
        int groupOfTwo = 0;
        int groupOfthree = 0;
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row4Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.id + "--" + one.name + "--" + one.address + "--" + one.provinceID + "--" + one.GID + "--"
                    + one.GRP_QUALITY + "--" + one.MASTER + "--" + one.SCORE);
            if (one.id == 6 && one.MASTER == true) {
                Assert.assertEquals(0.6111111111111112d, one.GRP_QUALITY.doubleValue(), 0.0000000000000007d);
            } else if (one.id == 2 && one.MASTER == true) {
                Assert.assertEquals(1.0d, one.GRP_QUALITY.doubleValue(), 0.0d);
            }
            if (one.GRP_SIZE == 1) {
                groupOfOne++;
            } else if (one.GRP_SIZE == 2) {
                groupOfTwo++;
            } else if (one.GRP_SIZE == 3) {
                groupOfthree++;
            }
        }
        Assert.assertTrue(groupOfOne == 2);
        Assert.assertTrue(groupOfTwo == 1);
        Assert.assertTrue(groupOfthree == 1);
    }

    /**
     * DOC zshen Comment method "createComponent".
     * 
     * @param masterRows_tMatchGroup_1
     * @param groupRows_tMatchGroup_1
     * @param indexMap_tMatchGroup_1
     * @return
     */
    private AbstractRecordGrouping<Object> createComponentForRow4(final List<row4Struct> masterRows_tMatchGroup_1,
            final List<row4Struct> groupRows_tMatchGroup_1, final Map<String, Double> indexMap_tMatchGroup_1) {
        org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_2;
        recordGroupImp_tMatchGroup_2 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row4Struct outStuct_tMatchGroup_2 = new row4Struct();
                boolean isMaster = false;

                if (0 < row.length) {

                    try {
                        outStuct_tMatchGroup_2.id = Integer.valueOf((String) row[0]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_2.id = row[0] == null ? null : 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_2.name = row[1] == null ? null : String.valueOf(row[1]);
                }

                if (2 < row.length) {
                    outStuct_tMatchGroup_2.address = row[2] == null ? null : String.valueOf(row[2]);
                }

                if (3 < row.length) {

                    try {
                        outStuct_tMatchGroup_2.provinceID = Integer.valueOf((String) row[3]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_2.provinceID = row[3] == null ? null : 0;
                    }
                }

                if (4 < row.length) {
                    outStuct_tMatchGroup_2.GID = row[4] == null ? null : String.valueOf(row[4]);
                }

                if (5 < row.length) {

                    try {
                        outStuct_tMatchGroup_2.GRP_SIZE = Integer.valueOf((String) row[5]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_2.GRP_SIZE = row[5] == null ? null : 0;
                    }
                }

                if (6 < row.length) {
                    outStuct_tMatchGroup_2.MASTER = row[6] == null ? null : Boolean.valueOf((String) row[6]);
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_2.SCORE = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_2.SCORE = 0.0;
                    }
                }

                if (8 < row.length) {

                    try {
                        outStuct_tMatchGroup_2.GRP_QUALITY = Double.valueOf((String) row[8]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_2.GRP_QUALITY = 0.0;
                    }
                }

                if (9 < row.length) {
                    outStuct_tMatchGroup_2.MATCHING_DISTANCES = row[9] == null ? null : String.valueOf(row[9]);
                }

                if (outStuct_tMatchGroup_2.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_2);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_2.GID), outStuct_tMatchGroup_2.GRP_QUALITY);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_2);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };
        recordGroupImp_tMatchGroup_2.setOrginalInputColumnSize(4);
        recordGroupImp_tMatchGroup_2
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        return recordGroupImp_tMatchGroup_2;
    }

    /**
     * DOC zshen Comment method "fillColumn".
     * 
     * @param id
     * @param name
     * @param address
     * @param provinceID
     * @param gid
     * @param grp_size
     * @param master
     * @param score
     * @param grp_quality
     * @param matching_distances
     * @return
     */
    private Map<String, String> fillColumn(String id, String name, String address, String provinceID, String gid, String grp_size,
            String master, String score, String grp_quality, String matching_distances) {
        Map<String, String> map = new HashMap<String, String>();
        if (id != null) {
            map.put("id", id);
        }
        if (name != null) {
            map.put("name", name);
        }
        if (address != null) {
            map.put("address", address);
        }
        if (provinceID != null) {
            map.put("provinceID", provinceID);
        }
        if (gid != null) {
            map.put("GID", gid);
        }
        if (grp_size != null) {
            map.put("GRP_SIZE", grp_size);
        }
        if (master != null) {
            map.put("MASTER", master);
        }
        if (score != null) {
            map.put("SCORE", score);
        }
        if (grp_quality != null) {
            map.put("GRP_QUALITY", grp_quality);
        }
        if (matching_distances != null) {
            map.put("MATCHING_DISTANCES", matching_distances);
        }
        // if (mergeInfo != null)
        // map.put("MERGE_INFO", mergeInfo);
        return map;
    }

    @Test
    public void testSwooshIntMatchGroup_withCustomMatcher()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "custom", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        tmpMap_tMatchGroup_1.put("CUSTOMER_MATCH_CLASS", "org.talend.dataquality.record.linkage.grouping.MyDistance" + "");
        tmpMap_tMatchGroup_1.put("JAR_PATH",
                "/home/runtime/TDQ_EE_NEW2/LOCAL_PROJECT/TDQ_Libraries/Indicators/User Defined Indicators/lib/test.mydistance.jar");

        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("customers_swoosh_tmatch.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // assert
        int n = 0;
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE
            // + "--" + one.MASTER);
            if (one.GRP_SIZE == 7) {
                Assert.assertEquals("USAUSAUSAUSAUSAUSAUSA", one.country);

                for (int i = n + 1; i < n + 7; i++) {
                    row2Struct two = groupRows_tMatchGroup_1.get(i);
                    // System.out.println(one.customer_id + "--" + two.city + "--" + two.country + "--" + two.GID + "--"
                    // +two.GRP_SIZE+ "--" + two.MASTER);
                    Assert.assertEquals("USA", groupRows_tMatchGroup_1.get(i).country);
                }
                break;
            }
            n++;
        }
    }

    @Test
    public void testSwooshIntMatchGroup_withDisplayAttLabels()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                if (0 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = String.valueOf(row[1]);
                }
                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = String.valueOf(row[2]);
                }
                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = String.valueOf(row[3]);
                }
                if (4 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = 0;
                    }
                }
                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = Boolean.valueOf((String) row[5]);
                }
                if (6 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }
                if (7 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    }
                }
                if (8 < row.length) {
                    outStuct_tMatchGroup_1.MATCHING_DISTANCES = row[8] == null ? null : String.valueOf(row[8]);
                }
                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(true);
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("customers_swoosh_tmatch.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // assert
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.MASTER == false) {
                Assert.assertEquals("country: 1.0", one.MATCHING_DISTANCES);
            }
        }
    }

    @Test
    public void testSwooshIntMatchGroup_scoreValues()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "0.5", "", "city", "1", "Levenshtein", "NO", 1 + "", "nullMatchNull",
                0.5 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                if (0 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = String.valueOf(row[1]);
                }
                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = String.valueOf(row[2]);
                }
                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = String.valueOf(row[3]);
                }
                if (4 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = 0;
                    }
                }
                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = Boolean.valueOf((String) row[5]);
                }
                if (6 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }
                if (7 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    }
                }
                if (8 < row.length) {
                    outStuct_tMatchGroup_1.MATCHING_DISTANCES = row[8] == null ? null : String.valueOf(row[8]);
                }
                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(true);
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_score.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // assert
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.MASTER == false) {
                if ("AA".equals(one.city)) {
                    Assert.assertEquals(0.5, one.SCORE, 0);
                }
            }
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup_withOutputDD()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "-", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                if (0 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }
                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = String.valueOf(row[1]);
                }
                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = String.valueOf(row[2]);
                }
                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = String.valueOf(row[3]);
                }
                if (4 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = 0;
                    }
                }
                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = Boolean.valueOf((String) row[5]);
                }
                if (6 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (NumberFormatException | NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }
                if (7 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (NumberFormatException | NullPointerException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    }
                }
                if (8 < row.length) {
                    outStuct_tMatchGroup_1.MATCHING_DISTANCES = row[8] == null ? null : String.valueOf(row[8]);
                }
                if (9 < row.length) {
                    outStuct_tMatchGroup_1.MERGE_INFO = String.valueOf(row[9]);
                }

                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm

        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "9");
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter("|");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(true);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(true);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_outputDD.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // assert
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE
            // + "--" + one.MASTER + "--" + one.GRP_QUALITY + "--" + one.SCORE + "--" + one.MERGE_INFO + "--"
            // + one.MATCHING_DISTANCES);
            if (StringUtils.equals("true", one.MERGE_INFO) && !one.MASTER) {
                Assert.assertEquals("country: 1.0", one.MATCHING_DISTANCES);
            } else if (StringUtils.equals("false", one.MERGE_INFO) && !one.MASTER) {
                Assert.assertEquals("address:1.0", one.MATCHING_DISTANCES);
            }
        }
    }

    @Test
    public void testSwooshIntMatchGroup_tdq11599Smallelst()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;

        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        Map<String, String> realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Smallest");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "NUMBER");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);

        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("tdq11599.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.MASTER) {
                Assert.assertEquals("1", one.city);

                break;
            }
        }
    }

    private void initialize(AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1) {
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType(true);
    }

    @Test
    public void testSwooshIntMatchGroup_tdq11599Largest()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;

        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        Map<String, String> realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Largest");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "NUMBER");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);

        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_Integer", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);
        columnWithType_tMatchGroup_1.put("MERGED_RECORD", "id_Object");
        columnWithIndex_tMatchGroup_1.put("MERGED_RECORD", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("tdq11599.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.MASTER) {
                Assert.assertEquals("900", one.city);

                break;
            }
        }

    }

    @Test
    public void testSwooshIntMatchGroup_tdq14218mergeNULL()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;

        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        Map<String, String> realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Shortest");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "STRING");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);

        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("tdq11599.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            int i = 0;
            for (String str : fields) {
                if ("null".equals(str) || "".equals(str)) {
                    fields[i] = null;
                }
                i++;
            }
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.MASTER) {
                Assert.assertEquals("1", one.city);
                break;
            }
        }
    }

    private Map<String, String> createTmpMap(String survivorShipFunction, String attributeThresold, String parameter,
            String attributeName, String columnId, String matchingType, String tokenType, String confidenceWeight,
            String handleNull, String recordMatchingThreshold, String matchAlgo) {
        Map<String, String> map = new HashMap<String, String>();
        if (survivorShipFunction != null) {
            map.put("SURVIVORSHIP_FUNCTION", survivorShipFunction);
        }
        if (attributeThresold != null) {
            map.put("ATTRIBUTE_THRESHOLD", attributeThresold);
        }
        if (parameter != null) {
            map.put("PARAMETER", parameter);
        }
        if (attributeName != null) {
            map.put("ATTRIBUTE_NAME", attributeName);
        }
        if (columnId != null) {
            map.put("COLUMN_IDX", columnId);
        }
        if (matchingType != null) {
            map.put("MATCHING_TYPE", matchingType);
        }
        if (tokenType != null) {
            map.put("TOKENIZATION_TYPE", tokenType);
        }
        if (confidenceWeight != null) {
            map.put("CONFIDENCE_WEIGHT", confidenceWeight);
        }
        if (handleNull != null) {
            map.put("HANDLE_NULL", handleNull);
        }
        if (recordMatchingThreshold != null) {
            map.put("RECORD_MATCH_THRESHOLD", recordMatchingThreshold);
        }
        if (matchAlgo != null) {
            map.put("MATCHING_ALGORITHM", matchAlgo);
        }

        return map;
    }

    private Map<String, String> createTmpMap(String survivorShipFunction, String attributeThresold, String parameter,
            String attributeName, String columnId, String matchingType, String tokenType, String confidenceWeight,
            String handleNull, String recordMatchingThreshold, String matchAlgo, String referenceColumnName,
            String referenceColumnIndex) {
        Map<String, String> map = new HashMap<String, String>();
        if (survivorShipFunction != null) {
            map.put("SURVIVORSHIP_FUNCTION", survivorShipFunction);
        }
        if (attributeThresold != null) {
            map.put("ATTRIBUTE_THRESHOLD", attributeThresold);
        }
        if (parameter != null) {
            map.put("PARAMETER", parameter);
        }
        if (attributeName != null) {
            map.put("ATTRIBUTE_NAME", attributeName);
        }
        if (columnId != null) {
            map.put("COLUMN_IDX", columnId);
        }
        if (matchingType != null) {
            map.put("MATCHING_TYPE", matchingType);
        }
        if (tokenType != null) {
            map.put("TOKENIZATION_TYPE", tokenType);
        }
        if (confidenceWeight != null) {
            map.put("CONFIDENCE_WEIGHT", confidenceWeight);
        }
        if (handleNull != null) {
            map.put("HANDLE_NULL", handleNull);
        }
        if (recordMatchingThreshold != null) {
            map.put("RECORD_MATCH_THRESHOLD", recordMatchingThreshold);
        }
        if (matchAlgo != null) {
            map.put("MATCHING_ALGORITHM", matchAlgo);
        }
        if (referenceColumnName != null) {
            map.put("REFERENCE_COLUMN", referenceColumnName);
        }
        if (referenceColumnIndex != null) {
            map.put("REFERENCE_COLUMN_IDX", referenceColumnIndex);
        }

        return map;
    }

    private Map<String, String> fillColumn(String customerId, String city, String country, String gid, String grpSize,
            String master, String score, String grpQuality, String mergeInfo) {
        Map<String, String> map = new HashMap<String, String>();
        if (customerId != null) {
            map.put("customer_id", customerId);
        }
        if (city != null) {
            map.put("city", city);
        }
        if (country != null) {
            map.put("country", country);
        }
        if (gid != null) {
            map.put("GID", gid);
        }
        if (grpQuality != null) {
            map.put("GRP_SIZE", grpQuality);
        }
        if (master != null) {
            map.put("MASTER", master);
        }
        if (score != null) {
            map.put("SCORE", score);
        }
        if (grpQuality != null) {
            map.put("GRP_QUALITY", grpQuality);
        }
        // if (mergeInfo != null)
        // map.put("MERGE_INFO", mergeInfo);
        return map;
    }

    private Map<String, String> fillColumnForReference(String id, String name, String number, String date1, String date2,
            String gid, String grpSize, String master, String score, String grpQuality) {
        Map<String, String> map = new HashMap<String, String>();
        if (id != null) {
            map.put("id", id);
        }
        if (name != null) {
            map.put("name", name);
        }
        if (number != null) {
            map.put("number", number);
        }
        if (date1 != null) {
            map.put("date1", date1);
        }
        if (date2 != null) {
            map.put("date2", date2);
        }
        if (gid != null) {
            map.put("GID", gid);
        }
        if (grpQuality != null) {
            map.put("GRP_SIZE", grpQuality);
        }
        if (master != null) {
            map.put("MASTER", master);
        }
        if (score != null) {
            map.put("SCORE", score);
        }
        if (grpQuality != null) {
            map.put("GRP_QUALITY", grpQuality);
        }

        return map;
    }

    private AbstractRecordGrouping<Object> createComponent(final List<row2Struct> masterRows_tMatchGroup_1,
            final List<row2Struct> groupRows_tMatchGroup_1, final Map<String, Integer> indexMap_tMatchGroup_1) {
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                if (0 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }
                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = String.valueOf(row[1]);
                }
                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = String.valueOf(row[2]);
                }

                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = String.valueOf(row[3]);
                }
                if (4 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = 0;
                    }
                }
                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = Boolean.valueOf((String) row[5]);
                }
                if (6 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (NumberFormatException | NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }
                if (7 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (NumberFormatException | NullPointerException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    }
                }
                // if (8 < row.length) {
                // outStuct_tMatchGroup_1.MERGE_INFO = String.valueOf(row[8]);
                // }
                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        return recordGroupImp_tMatchGroup_1;
    }

    private AbstractRecordGrouping<Object> createComponentForReferenceColumn(
            final List<row2StructForReferenceColumn> masterRows_tMatchGroup_1,
            final List<row2StructForReferenceColumn> groupRows_tMatchGroup_1, final Map<String, Integer> indexMap_tMatchGroup_1) {

        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2StructForReferenceColumn outStuct_tMatchGroup_1 = new row2StructForReferenceColumn();
                boolean isMaster = false;

                if (0 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.id = Integer.valueOf((String) row[0]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.id = row[0] == null ? null : 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.name = row[1] == null ? null : String.valueOf(row[1]);
                }

                if (2 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.number = Integer.valueOf((String) row[2]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.number = row[2] == null ? null : 0;
                    }
                }

                if (3 < row.length) {
                    String currentPattern = null;
                    if (getColumnDatePatternMap() != null) {
                        currentPattern = getColumnDatePatternMap().get("3");
                    }
                    if (currentPattern == null) {
                        currentPattern = "yyyy-MM-dd";
                    }
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(currentPattern);
                    java.util.Date date = null;
                    if (row[3] == null) {
                        date = null;
                    } else {
                        try {
                            date = sdf.parse((String) row[3]);
                        } catch (java.text.ParseException e) {
                            sdf = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.US);
                            try {
                                date = sdf.parse((String) row[3]);
                            } catch (java.text.ParseException e1) {

                            }
                        }
                    }
                    outStuct_tMatchGroup_1.date1 = date;
                }

                if (4 < row.length) {
                    String currentPattern = null;
                    if (getColumnDatePatternMap() != null) {
                        currentPattern = getColumnDatePatternMap().get("4");
                    }
                    if (currentPattern == null) {
                        currentPattern = "yyyy-MM-dd";
                    }
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(currentPattern);
                    java.util.Date date = null;
                    if (row[4] == null) {
                        date = null;
                    } else {
                        try {
                            date = sdf.parse((String) row[4]);
                        } catch (java.text.ParseException e) {
                            sdf = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.US);
                            try {
                                date = sdf.parse((String) row[4]);
                            } catch (java.text.ParseException e1) {

                            }
                        }
                    }
                    outStuct_tMatchGroup_1.date2 = date;
                }

                if (5 < row.length) {
                    outStuct_tMatchGroup_1.GID = row[5] == null ? null : String.valueOf(row[5]);
                }

                if (6 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[6]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = row[6] == null ? null : 0;
                    }
                }

                if (7 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = row[7] == null ? null : Boolean.valueOf((String) row[7]);
                }

                if (8 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[8]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = 0.0;
                    }
                }

                if (9 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[9]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = 0.0;
                    }
                }

                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        return recordGroupImp_tMatchGroup_1;
    }

    class row2Struct implements Comparable<row2Struct> {

        protected static final int DEFAULT_HASHCODE = 1;

        protected static final int PRIME = 31;

        protected int hashCode = DEFAULT_HASHCODE;

        public boolean hashCodeDirty = true;

        public String loopKey;

        public int customer_id;

        public String city;

        public String country;

        public String GID;

        public Integer GRP_SIZE;

        public Boolean MASTER;

        public Double SCORE;

        public Double GRP_QUALITY;

        public String MERGE_INFO;

        public String MATCHING_DISTANCES;

        public Object MERGED_RECORD;

        public Object getMERGED_RECORD() {
            return this.MERGED_RECORD;
        }

        @Override
        public int hashCode() {
            if (this.hashCodeDirty) {
                final int prime = PRIME;
                int result = DEFAULT_HASHCODE;

                result = prime * result + this.customer_id;

                this.hashCode = result;
                this.hashCodeDirty = false;
            }
            return this.hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final row2Struct other = (row2Struct) obj;

            if (this.customer_id != other.customer_id) {
                return false;
            }

            return true;
        }

        public void copyDataTo(row2Struct other) {

            other.customer_id = this.customer_id;
            other.city = this.city;
            other.country = this.country;
            other.GID = this.GID;
            other.GRP_SIZE = this.GRP_SIZE;
            other.MASTER = this.MASTER;
            other.SCORE = this.SCORE;
            other.GRP_QUALITY = this.GRP_QUALITY;
            other.MERGED_RECORD = this.MERGED_RECORD;
        }

        public void copyKeysDataTo(row2Struct other) {
            other.customer_id = this.customer_id;
        }

        @Override
        public int compareTo(row2Struct row2) {
            if (!(this.GID).equals(row2.GID)) {
                return (this.GID).compareTo(row2.GID);
            } else {
                // false < true
                return (row2.MASTER).compareTo(this.MASTER);
            }
        }
    }

    /**
     * after the first tmatchgroup, there is an column added on its output. To contain the original
     * values(List<Attribute>
     * attributes from Record).
     */
    @Test
    public void testSwooshIntMatchGroup_passOriginal_1st()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "city", "1", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

                if (0 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = row[1] == null ? null : String.valueOf(row[1]);
                }

                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = row[2] == null ? null : String.valueOf(row[2]);
                }

                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = row[3] == null ? null : String.valueOf(row[3]);
                }

                if (4 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = row[4] == null ? null : 0;
                    }
                }

                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = row[5] == null ? null : Boolean.valueOf((String) row[5]);
                }

                if (6 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = 0.0;
                    }
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = 0.0;
                    }
                }

                if (8 < row.length) {
                    outStuct_tMatchGroup_1.MERGED_RECORD = row[8] == null ? null : row[8];
                }

                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((ComponentSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // pass the original values
        // recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);
        recordGroupImp_tMatchGroup_1.setIsPassOriginalValue(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_pass_original.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        List<String[]> inputList2 = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            if (StringUtils.equals("X", fields[2])) {
                inputList.add(fields);
            } else {
                inputList2.add(fields);
            }
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while
        recordGroupImp_tMatchGroup_1.end();

        for (String[] inputRow : inputList2) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while
        recordGroupImp_tMatchGroup_1.end();

        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);

        // assert
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE
            // + "--" + one.MASTER + "--" + one.MERGED_RECORD);

            if (one.MASTER) {

                Assert.assertTrue(one.MERGED_RECORD instanceof List);
                Assert.assertTrue(((List) one.MERGED_RECORD).size() > 0);

            }
        }

    }

    /**
     * Input of the 2nd tmatchgroup contains List<Attribute>, need to be handled. And should not be outputed.
     */
    @Test
    public void testSwooshIntMatchGroup_passOriginal_2nd()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "city", "1", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

                if (0 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = row[1] == null ? null : String.valueOf(row[1]);
                }

                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = row[2] == null ? null : String.valueOf(row[2]);
                }
                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = row[4] == null ? null : String.valueOf(row[3]);
                }

                if (4 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = row[5] == null ? null : 0;
                    }
                }

                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = row[6] == null ? null : Boolean.valueOf((String) row[5]);
                }

                if (6 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = 0.0;
                    }
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = 0.0;
                    }
                }

                if (8 < row.length) {
                    outStuct_tMatchGroup_1.MERGED_RECORD = row[3] == null ? null : row[8];
                }
                // if (9 < row.length) {
                // outStuct_tMatchGroup_1.MERGE_INFO = row[9] == null ? null : String.valueOf(row[9]);
                // }

                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();

        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((ComponentSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);
        // the "MERGED_RECORD" is NOT considered as an input column.
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_passOriginal_on2nd.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        List<Object[]> inputList2 = new ArrayList<Object[]>();
        List<Object> list2 = new ArrayList<Object>();
        for (String line : listOfLines) {
            Object[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            Collections.addAll(list2, fields);
            List<Attribute> list = new ArrayList<Attribute>();
            if (StringUtils.equalsIgnoreCase("true", (String) fields[5])) { // add the list into masters
                Attribute att2 = new Attribute("city", 1);
                att2.setValue("AA");
                att2.getValues().get("A").increment();
                att2.getValues().get("A").increment();
                list.add(att2);

            }
            list2.add(list);
            // add "Merge_info" at the end of the array
            // list2.add((String) fields[5]);

            inputList2.add(list2.toArray());
            list2.clear();
        }

        for (Object[] inputRow : inputList2) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        }
        recordGroupImp_tMatchGroup_1.end();

        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);

        // assert
        // System.err.println("--pass original---2---" + groupRows_tMatchGroup_1.size());
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE
            // + "--" + one.MASTER + "--");
            Assert.assertNull(one.MERGED_RECORD);
            if (one.MASTER) {

                Assert.assertEquals("should be: AAA", "AAA", one.city);
            }
        }

    }

    /**
     * Input of the 2nd tmatchgroup contains List<Attribute>, need to be handled. And should not be outputed.
     */
    @Test
    public void testSwooshIntMatchGroup_passOriginal_withOutputDetails()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "city", "1", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

                if (0 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = row[1] == null ? null : String.valueOf(row[1]);
                }

                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = row[2] == null ? null : String.valueOf(row[2]);
                }
                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = row[3] == null ? null : String.valueOf(row[3]);
                }

                if (4 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = row[4] == null ? null : 0;
                    }
                }

                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = row[5] == null ? null : Boolean.valueOf((String) row[5]);
                }

                if (6 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = 0.0;
                    }
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = 0.0;
                    }
                }
                if (8 < row.length) {
                    outStuct_tMatchGroup_1.MATCHING_DISTANCES = row[8] == null ? null : String.valueOf(row[8]);
                }
                if (9 < row.length) {
                    outStuct_tMatchGroup_1.MERGED_RECORD = row[9] == null ? null : row[9];
                }
                // if (10 < row.length) {
                // outStuct_tMatchGroup_1.MERGE_INFO = row[10] == null ? null : String.valueOf(row[10]);
                // }
                //
                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();

        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((ComponentSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);
        // the "MERGED_RECORD" is considered as an input column.
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(true);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_passOriginal_withoutput.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        List<Object[]> inputList2 = new ArrayList<Object[]>();
        List<Object> list2 = new ArrayList<Object>();
        for (String line : listOfLines) {
            Object[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            Collections.addAll(list2, fields);
            List<Attribute> list = new ArrayList<Attribute>();
            if (StringUtils.equalsIgnoreCase("true", (String) fields[5])) { // add the list into masters
                Attribute att2 = new Attribute("city", 1);
                att2.setValue("AA");
                att2.getValues().get("A").increment();
                att2.getValues().get("A").increment();
                list.add(att2);
            }
            list2.add(list);
            // add "Merge_info" at the end of the array
            // list2.add((String) fields[5]);

            inputList2.add(list2.toArray());
            list2.clear();
        }

        for (Object[] inputRow : inputList2) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        }
        recordGroupImp_tMatchGroup_1.end();

        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);

        // assert
        // System.err.println("--pass original---with output---" + groupRows_tMatchGroup_1.size());
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE
            // + "--" + one.MASTER + "--" + one.MATCHING_DISTANCES);

            if (one.MASTER) {
                Assert.assertEquals("should be: AAA", "AAA", one.city);
            } else if (one.customer_id == 3) {
                Assert.assertEquals("should has output details", "city: 1.0", one.MATCHING_DISTANCES);
            } else {
                Assert.assertEquals("should has output details", "city:1.0", one.MATCHING_DISTANCES);
            }
        }

    }

    @Test
    public void testSwooshIntMatchGroup_removeTempMasters()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "city", "1", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

                if (0 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = row[1] == null ? null : String.valueOf(row[1]);
                }

                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = row[2] == null ? null : String.valueOf(row[2]);
                }
                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = row[4] == null ? null : String.valueOf(row[3]);
                }

                if (4 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = row[5] == null ? null : 0;
                    }
                }

                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = row[6] == null ? null : Boolean.valueOf((String) row[5]);
                }

                if (6 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = 0.0;
                    }
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = 0.0;
                    }
                }

                if (8 < row.length) {
                    outStuct_tMatchGroup_1.MERGED_RECORD = row[3] == null ? null : row[8];
                }
                // if (9 < row.length) {
                // outStuct_tMatchGroup_1.MERGE_INFO = row[9] == null ? null : String.valueOf(row[9]);
                // }

                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();

        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((ComponentSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);
        // the "MERGED_RECORD" is NOT considered as an input column.
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_multi_remove_tempmaster.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        List<Object[]> inputList2 = new ArrayList<Object[]>();
        List<Object> list2 = new ArrayList<Object>();
        for (String line : listOfLines) {
            Object[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            Collections.addAll(list2, fields);

            inputList2.add(list2.toArray());
            list2.clear();
        }

        for (Object[] inputRow : inputList2) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        }
        recordGroupImp_tMatchGroup_1.end();

        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);

        // assert
        // System.err.println("--remove intermediate masters---" + groupRows_tMatchGroup_1.size());
        Assert.assertEquals("should be: 11 ", 11, groupRows_tMatchGroup_1.size());
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE
            // + "--" + one.MASTER);
            if (one.MASTER && one.customer_id == 2) {// no merge
                Assert.assertEquals("should be: 2", "2", String.valueOf(one.GRP_SIZE));
                Assert.assertEquals("should be: AA", "AA", one.city);
            } else if (one.MASTER && one.customer_id == 3) {
                Assert.assertEquals("should be: 1", "1", String.valueOf(one.GRP_SIZE));
                Assert.assertEquals("should be: A", "A", one.city);
            } else if (one.MASTER && one.customer_id == 5) {// merge 2 masters who has no other records in their group
                Assert.assertEquals("should be: 2", "2", String.valueOf(one.GRP_SIZE));
                Assert.assertEquals("should be: BB", "BB", one.city);
            } else if (one.MASTER && one.customer_id == 7) {// merged 2 masters, one of master's grpSize>1
                Assert.assertEquals("should be: 3", "3", String.valueOf(one.GRP_SIZE));
                Assert.assertEquals("should be: CCCC", "CCCC", one.city);
            } // merged 2 masters, both 2 group size >1
        }

    }

    @Test
    public void testSwoosh_one_grpQuality()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "0.79", "", "city", "1", "Levenshtein", "NO", 1 + "", "nullMatchNull",
                0.7 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_grpquality.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // assert
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_QUALITY+ "--" + one.MASTER);
            if (one.MASTER) {
                Assert.assertTrue(0.8333333333333334 == one.GRP_QUALITY || 0.8 == one.GRP_QUALITY);

            }
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup_grpQuality()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "0.79", "", "country", "2", "Levenshtein", "NO", 1 + "",
                "nullMatchNull", 0.7 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_multipass_grpquality.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_QUALITY
                    + "--" + one.MASTER + "--" + one.SCORE);
            if (one.MASTER) {
                if (one.customer_id == 8) {
                    Assert.assertTrue(0.8 == one.GRP_QUALITY);
                }
                if (one.customer_id == 10) {
                    Assert.assertTrue(0.8333333333333334 == one.GRP_QUALITY);
                }
                if (one.customer_id == 20) {
                    Assert.assertTrue(0.8333333333333334 == one.GRP_QUALITY);
                }
            } else {// not master, grp-quality=0
                Assert.assertTrue(0.0 == one.GRP_QUALITY);
            }
        }
    }

    @Test
    public void testSwooshIntMatchGroup_withDisplayAttLabels_2Rules()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "customer_id", "0", "dummy", "NO", 0 + "", "nullMatchNull",
                null, null);
        matcherList_tMatchGroup_1.add(0, tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1", "", "city", "1", "Exact", "NO", 1 + "", "nullMatchNull",
                0.86 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(1, tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1", "", "country", "2", "Exact", "NO", 2 + "", "nullMatchNull",
                0.86 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(2, tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();

        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "country", "2", "dummy", "NO", 1 + "", "nullMatchNull",
                0 + "", null);
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "customer_id", "0", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "city", "1", "dummy", "NO", 0 + "", "nullMatchNull", null,
                null);
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                if (0 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = String.valueOf(row[1]);
                }
                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = String.valueOf(row[2]);
                }
                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = String.valueOf(row[3]);
                }
                if (4 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = 0;
                    }
                }
                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = Boolean.valueOf((String) row[5]);
                }
                if (6 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }
                if (7 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    }
                }
                if (8 < row.length) {
                    outStuct_tMatchGroup_1.MATCHING_DISTANCES = row[8] == null ? null : String.valueOf(row[8]);
                }
                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(true);
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multiRule_swoosh.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // assert
        Assert.assertTrue(groupRows_tMatchGroup_1.size() > 0);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID +
            // "--" + one.MASTER + "--" + one.SCORE+"--" + one.MATCHING_DISTANCES);
            if (one.MASTER == false) {
                if (one.customer_id == 13) {
                    Assert.assertEquals("customer_id: 1.0", one.MATCHING_DISTANCES);
                }
                if (one.customer_id == 9) {
                    Assert.assertEquals("city: 1.0 | country: 1.0", one.MATCHING_DISTANCES);
                }
                if (one.customer_id == 103) {
                    Assert.assertEquals("city: 1.0 | country: 1.0", one.MATCHING_DISTANCES);
                }
                if (one.customer_id == 2) {
                    Assert.assertEquals("customer_id: 1.0", one.MATCHING_DISTANCES);
                }
                if (one.customer_id == 5) {
                    Assert.assertEquals("customer_id: 1.0", one.MATCHING_DISTANCES);
                }
                if (one.customer_id == 101) {
                    Assert.assertEquals("customer_id: 1.0", one.MATCHING_DISTANCES);
                }
                if (one.customer_id == 102) {
                    Assert.assertEquals("customer_id: 1.0", one.MATCHING_DISTANCES);
                }
            }
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup_oneRecordOneGroup_14229()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_oneRecordInOneGroup_14229.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        Assert.assertTrue(groupRows_tMatchGroup_1.size() == 8);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE
            // + "--" + one.MASTER);
            String gid = null;
            if (one.MASTER) {
                Assert.assertTrue(6 == one.GRP_SIZE || 1 == one.GRP_SIZE);
                gid = one.GID;
            }
        }
    }

    @Test
    public void testDoMergeOnMostRecent_14276_MOST_RECENT() throws IOException {
        InputStream in = this.getClass().getResourceAsStream("incoming_customers_swoosh2.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.US);
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);

            inputList.add(fields);
        }
        recordGroup = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                groupingRecords.add(row);
            }

        };
        recordGroup.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        SurvivorShipAlgorithmParams survivorShipAlgorithmParams = new SurvivorShipAlgorithmParams();
        SurvivorshipFunction func = survivorShipAlgorithmParams.new SurvivorshipFunction();
        func.setParameter(""); //$NON-NLS-1$
        func.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        survivorShipAlgorithmParams.setSurviorShipAlgos(new SurvivorshipFunction[] { func });
        recordGroup.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);

        // Set default survivorship functions.
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<Integer, SurvivorshipFunction>();
        SurvivorshipFunction survFunc5 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc5.setParameter(StringUtils.EMPTY);
        survFunc5.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_RECENT);
        survFunc5.setSurvivorShipKey("6");
        defaultSurvRules.put(5, survFunc5);

        survivorShipAlgorithmParams.setDefaultSurviorshipRules(defaultSurvRules);

        recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        ((AbstractRecordGrouping) recordGroup).setOrginalInputColumnSize(9);

        List<Map<String, String>> matchingRule = new ArrayList<Map<String, String>>();

        Map<String, String> lnameRecords = createTmpMap(null, "0.9", null, "NAME", "1", "JARO_WINKLER", "NO", "1", null, null,
                null);
        matchingRule.add(lnameRecords);

        recordGroup.addMatchRule(matchingRule);
        try {
            recordGroup.initialize();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }

        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("5", "yyyy-MM-dd HH:mm:ss.SSS");
        recordGroup.setColumnDatePatternMap(dataMap);

        recordGroup.setIsOutputDistDetails(true);
        recordGroup.setAcceptableThreshold(0.95f);
        ((AbstractRecordGrouping) recordGroup).setOrginalInputColumnSize(8);

        // loop on all input rows.
        try {
            for (String[] inputRow : inputList) {
                recordGroup.doGroup(inputRow);
            }
            recordGroup.end();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        // Assertions
        Assert.assertTrue(groupingRecords.size() > 0);
        for (Object[] rds : groupingRecords) {
            if (rds[10].equals("true") && rds[0].equals("1")) { //$NON-NLS-1$
                // Master record
                // Most recent. "Wed Jan 13 11:05:20 CST 2016"
                Assert.assertEquals("2016-03-03 11:15:20.111", rds[5]);
            }
        }
    }

    @Test
    public void testDoMergeOnMostRecent_14276_MOST_RECENT_null() throws IOException {
        InputStream in = this.getClass().getResourceAsStream("incoming_customers_swoosh2.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.US);
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            // when the date field is null, should not throw any exception, but just egnore it.
            if ("5".equals(fields[0])) {
                fields[5] = null;
            }
            if ("6".equals(fields[0])) {
                fields[5] = "null";
            }
            inputList.add(fields);
        }
        recordGroup = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                groupingRecords.add(row);
            }

        };
        recordGroup.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        SurvivorShipAlgorithmParams survivorShipAlgorithmParams = new SurvivorShipAlgorithmParams();
        SurvivorshipFunction func = survivorShipAlgorithmParams.new SurvivorshipFunction();
        func.setParameter(""); //$NON-NLS-1$
        func.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        survivorShipAlgorithmParams.setSurviorShipAlgos(new SurvivorshipFunction[] { func });
        recordGroup.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);

        // Set default survivorship functions.
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<Integer, SurvivorshipFunction>();
        SurvivorshipFunction survFunc5 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc5.setParameter(StringUtils.EMPTY);
        survFunc5.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_RECENT);
        survFunc5.setSurvivorShipKey("6");
        defaultSurvRules.put(5, survFunc5);

        survivorShipAlgorithmParams.setDefaultSurviorshipRules(defaultSurvRules);

        recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        ((AbstractRecordGrouping) recordGroup).setOrginalInputColumnSize(9);

        List<Map<String, String>> matchingRule = new ArrayList<Map<String, String>>();

        Map<String, String> lnameRecords = createTmpMap(null, "0.9", null, "NAME", "1", "JARO_WINKLER", "NO", "1", null, null,
                null);
        matchingRule.add(lnameRecords);

        recordGroup.addMatchRule(matchingRule);
        try {
            recordGroup.initialize();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }

        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("5", "yyyy-MM-dd HH:mm:ss.SSS");
        recordGroup.setColumnDatePatternMap(dataMap);

        recordGroup.setIsOutputDistDetails(true);
        recordGroup.setAcceptableThreshold(0.95f);
        ((AbstractRecordGrouping) recordGroup).setOrginalInputColumnSize(8);

        // loop on all input rows.
        try {
            for (String[] inputRow : inputList) {
                recordGroup.doGroup(inputRow);
            }
            recordGroup.end();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        // Assertions
        Assert.assertTrue(groupingRecords.size() > 0);
        for (Object[] rds : groupingRecords) {
            if (rds[10].equals("true") && rds[0].equals("1")) { //$NON-NLS-1$
                // Master record
                // Most recent. "Wed Jan 13 11:05:20 CST 2016"
                Assert.assertEquals("2016-03-03 11:15:20.111", rds[5]);
            }
        }
    }

    @Test
    public void testDoMergeOnMostRecent_14276_MOST_ANCIENT() throws IOException {
        InputStream in = this.getClass().getResourceAsStream("incoming_customers_swoosh2.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.US);
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);

            inputList.add(fields);
        }
        recordGroup = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                groupingRecords.add(row);
            }

        };
        recordGroup.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        SurvivorShipAlgorithmParams survivorShipAlgorithmParams = new SurvivorShipAlgorithmParams();
        SurvivorshipFunction func = survivorShipAlgorithmParams.new SurvivorshipFunction();
        func.setParameter(""); //$NON-NLS-1$
        func.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        survivorShipAlgorithmParams.setSurviorShipAlgos(new SurvivorshipFunction[] { func });
        recordGroup.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);

        // Set default survivorship functions.
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<Integer, SurvivorshipFunction>();
        SurvivorshipFunction survFunc5 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc5.setParameter(StringUtils.EMPTY);
        survFunc5.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_ANCIENT);
        survFunc5.setSurvivorShipKey("6");
        defaultSurvRules.put(5, survFunc5);

        survivorShipAlgorithmParams.setDefaultSurviorshipRules(defaultSurvRules);

        recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        ((AbstractRecordGrouping) recordGroup).setOrginalInputColumnSize(9);

        List<Map<String, String>> matchingRule = new ArrayList<Map<String, String>>();

        Map<String, String> lnameRecords = createTmpMap(null, "0.9", null, "NAME", "1", "JARO_WINKLER", "NO", "1", null, null,
                null);
        matchingRule.add(lnameRecords);

        recordGroup.addMatchRule(matchingRule);
        try {
            recordGroup.initialize();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("5", "yyyy-MM-dd HH:mm:ss.SSS");
        recordGroup.setColumnDatePatternMap(dataMap);

        recordGroup.setIsOutputDistDetails(true);
        recordGroup.setAcceptableThreshold(0.95f);
        ((AbstractRecordGrouping) recordGroup).setOrginalInputColumnSize(8);

        // loop on all input rows.
        try {
            for (String[] inputRow : inputList) {
                recordGroup.doGroup(inputRow);
            }
            recordGroup.end();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        // Assertions
        Assert.assertTrue(groupingRecords.size() > 0);
        for (Object[] rds : groupingRecords) {
            if (rds[10].equals("true") && rds[0].equals("1")) { //$NON-NLS-1$
                // Master record
                // Most ancient. "Fri Jan 03 11:05:20 CST 2014"
                Assert.assertEquals("2015-01-03 11:05:20.111", rds[5]);
            }
        }
    }

    public static class row4Struct implements Comparable<row4Struct> {

        final static byte[] commonByteArrayLock_TEST1_tMatchGroup_twoRuleTabs = new byte[0];

        static byte[] commonByteArray_TEST1_tMatchGroup_twoRuleTabs = new byte[0];

        boolean hashCodeDirty = true;

        protected static final int DEFAULT_HASHCODE = 1;

        protected static final int PRIME = 31;

        protected int hashCode = DEFAULT_HASHCODE;

        public Integer id;

        public Integer getId() {
            return this.id;
        }

        public String name;

        public String getName() {
            return this.name;
        }

        public String address;

        public String getAddress() {
            return this.address;
        }

        public Integer provinceID;

        public Integer getProvinceID() {
            return this.provinceID;
        }

        public String GID;

        public String getGID() {
            return this.GID;
        }

        public Integer GRP_SIZE;

        public Integer getGRP_SIZE() {
            return this.GRP_SIZE;
        }

        public Boolean MASTER;

        public Boolean getMASTER() {
            return this.MASTER;
        }

        public Double SCORE;

        public Double getSCORE() {
            return this.SCORE;
        }

        public Double GRP_QUALITY;

        public Double getGRP_QUALITY() {
            return this.GRP_QUALITY;
        }

        public String MATCHING_DISTANCES;

        public String getMATCHING_DISTANCES() {
            return this.MATCHING_DISTANCES;
        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append("[");
            sb.append("id=" + String.valueOf(id));
            sb.append(",name=" + name);
            sb.append(",address=" + address);
            sb.append(",provinceID=" + String.valueOf(provinceID));
            sb.append(",GID=" + GID);
            sb.append(",GRP_SIZE=" + String.valueOf(GRP_SIZE));
            sb.append(",MASTER=" + String.valueOf(MASTER));
            sb.append(",SCORE=" + String.valueOf(SCORE));
            sb.append(",GRP_QUALITY=" + String.valueOf(GRP_QUALITY));
            sb.append(",MATCHING_DISTANCES=" + MATCHING_DISTANCES);
            sb.append("]");

            return sb.toString();
        }

        /**
         * Compare keys
         */
        @Override
        public int compareTo(row4Struct other) {
            if (!(this.GID).equals(other.GID)) {
                return (this.GID).compareTo(other.GID);
            } else {
                // false < true
                return (other.MASTER).compareTo(this.MASTER);
            }
        }

        private int checkNullsAndCompare(Object object1, Object object2) {
            int returnValue = 0;
            if (object1 instanceof Comparable && object2 instanceof Comparable) {
                returnValue = ((Comparable) object1).compareTo(object2);
            } else if (object1 != null && object2 != null) {
                returnValue = compareStrings(object1.toString(), object2.toString());
            } else if (object1 == null && object2 != null) {
                returnValue = 1;
            } else if (object1 != null && object2 == null) {
                returnValue = -1;
            } else {
                returnValue = 0;
            }

            return returnValue;
        }

        private int compareStrings(String string1, String string2) {
            return string1.compareTo(string2);
        }

        @Override
        public int hashCode() {
            if (this.hashCodeDirty) {
                final int prime = PRIME;
                int result = DEFAULT_HASHCODE;

                result = prime * result + this.id;

                this.hashCode = result;
                this.hashCodeDirty = false;
            }
            return this.hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final row4Struct other = (row4Struct) obj;

            if (this.id != other.id) {
                return false;
            }

            return true;
        }

    }

    public static class row2StructForReferenceColumn implements Comparable<row2StructForReferenceColumn> {

        final static byte[] commonByteArrayLock_LOCAL_PROJECT_job1 = new byte[0];

        static byte[] commonByteArray_LOCAL_PROJECT_job1 = new byte[0];

        public Integer id;

        public Integer getId() {
            return this.id;
        }

        public String name;

        public String getName() {
            return this.name;
        }

        public Integer number;

        public Integer getNumber() {
            return this.number;
        }

        public java.util.Date date1;

        public java.util.Date getDate1() {
            return this.date1;
        }

        public java.util.Date date2;

        public java.util.Date getDate2() {
            return this.date2;
        }

        public String GID;

        public String getGID() {
            return this.GID;
        }

        public Integer GRP_SIZE;

        public Integer getGRP_SIZE() {
            return this.GRP_SIZE;
        }

        public Boolean MASTER;

        public Boolean getMASTER() {
            return this.MASTER;
        }

        public Double SCORE;

        public Double getSCORE() {
            return this.SCORE;
        }

        public Double GRP_QUALITY;

        public Double getGRP_QUALITY() {
            return this.GRP_QUALITY;
        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append("[");
            sb.append("id=" + String.valueOf(id));
            sb.append(",name=" + name);
            sb.append(",number=" + String.valueOf(number));
            sb.append(",date1=" + String.valueOf(date1));
            sb.append(",date2=" + String.valueOf(date2));
            sb.append(",GID=" + GID);
            sb.append(",GRP_SIZE=" + String.valueOf(GRP_SIZE));
            sb.append(",MASTER=" + String.valueOf(MASTER));
            sb.append(",SCORE=" + String.valueOf(SCORE));
            sb.append(",GRP_QUALITY=" + String.valueOf(GRP_QUALITY));
            sb.append("]");

            return sb.toString();
        }

        public String toLogString() {
            StringBuilder sb = new StringBuilder();

            if (id == null) {
                sb.append("<null>");
            } else {
                sb.append(id);
            }

            sb.append("|");

            if (name == null) {
                sb.append("<null>");
            } else {
                sb.append(name);
            }

            sb.append("|");

            if (number == null) {
                sb.append("<null>");
            } else {
                sb.append(number);
            }

            sb.append("|");

            if (date1 == null) {
                sb.append("<null>");
            } else {
                sb.append(date1);
            }

            sb.append("|");

            if (date2 == null) {
                sb.append("<null>");
            } else {
                sb.append(date2);
            }

            sb.append("|");

            if (GID == null) {
                sb.append("<null>");
            } else {
                sb.append(GID);
            }

            sb.append("|");

            if (GRP_SIZE == null) {
                sb.append("<null>");
            } else {
                sb.append(GRP_SIZE);
            }

            sb.append("|");

            if (MASTER == null) {
                sb.append("<null>");
            } else {
                sb.append(MASTER);
            }

            sb.append("|");

            if (SCORE == null) {
                sb.append("<null>");
            } else {
                sb.append(SCORE);
            }

            sb.append("|");

            if (GRP_QUALITY == null) {
                sb.append("<null>");
            } else {
                sb.append(GRP_QUALITY);
            }

            sb.append("|");

            return sb.toString();
        }

        /**
         * Compare keys
         */
        @Override
        public int compareTo(row2StructForReferenceColumn other) {

            int returnValue = -1;

            return returnValue;
        }

        private int checkNullsAndCompare(Object object1, Object object2) {
            int returnValue = 0;
            if (object1 instanceof Comparable && object2 instanceof Comparable) {
                returnValue = ((Comparable) object1).compareTo(object2);
            } else if (object1 != null && object2 != null) {
                returnValue = compareStrings(object1.toString(), object2.toString());
            } else if (object1 == null && object2 != null) {
                returnValue = 1;
            } else if (object1 != null && object2 == null) {
                returnValue = -1;
            } else {
                returnValue = 0;
            }

            return returnValue;
        }

        private int compareStrings(String string1, String string2) {
            return string1.compareTo(string2);
        }
    }

}
