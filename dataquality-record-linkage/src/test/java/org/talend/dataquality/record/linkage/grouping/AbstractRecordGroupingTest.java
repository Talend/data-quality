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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;

public class AbstractRecordGroupingTest {

    private static Logger log = LoggerFactory.getLogger(AbstractRecordGroupingTest.class);

    /**
     * The input data.
     */
    private List<String[]> inputList = null;

    private IRecordGrouping<String> recordGroup = null;

    private static final String columnDelimiter = "|"; //$NON-NLS-1$

    private List<String[]> groupingRecords = new ArrayList<String[]>();

    @Before
    public void setUp() throws Exception {

        // account_num , last name, first name, middle initial, address, city, state, zipcode, blocking key
        // Read input data file.
        InputStream in = this.getClass().getResourceAsStream("incoming_customers.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

    }

    @Test
    public void testDoGroup() {
        // set the matching parameters
        // matching parameters for lname

        recordGroup = new AbstractRecordGrouping<String>() {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(java.lang.String)
             */
            @Override
            protected void outputRow(String[] row) {
                groupingRecords.add(row);
            }

            @Override
            protected String incrementGroupSize(String oldGroupSize) {
                String newGroupSize = String.valueOf(Integer.parseInt(String.valueOf(oldGroupSize)) + 1);
                return newGroupSize;
            }

            @Override
            protected String castAsType(Object objectValue) {
                String column = String.valueOf(objectValue);
                return column;
            }

            @Override
            protected boolean isMaster(String col) {
                return "true".equals(col); //$NON-NLS-1$
            }

            @Override
            protected String[] createTYPEArray(int size) {
                String[] arrays = new String[size];
                return arrays;
            }

            @Override
            protected void outputRow(RichRecord row) {
                // TODO Auto-generated method stub

            }

        };
        recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        List<Map<String, String>> matchingRule = new ArrayList<Map<String, String>>();

        Map<String, String> lnameRecords = new HashMap<String, String>();
        lnameRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.MATCHING_TYPE, "JARO_WINKLER"); //$NON-NLS-1$
        lnameRecords.put(IRecordGrouping.TOKENIZATION_TYPE, TokenizedResolutionMethod.NO.toString()); // $NON-NLS-1$
        lnameRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.ATTRIBUTE_THRESHOLD, String.valueOf(1));
        matchingRule.add(lnameRecords);

        // matching parameters for state_province
        Map<String, String> accountRecords = new HashMap<String, String>();
        accountRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(6));
        accountRecords.put(IRecordGrouping.MATCHING_TYPE, "LEVENSHTEIN"); //$NON-NLS-1$
        accountRecords.put(IRecordGrouping.TOKENIZATION_TYPE, TokenizedResolutionMethod.NO.toString()); // $NON-NLS-1$
        accountRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(0.8));
        accountRecords.put(IRecordGrouping.ATTRIBUTE_THRESHOLD, String.valueOf(1));
        matchingRule.add(accountRecords);

        // matching parameters for custom algorithem
        Map<String, String> customRecords = new HashMap<String, String>();
        customRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(6));
        customRecords.put(IRecordGrouping.MATCHING_TYPE, "CUSTOM"); //$NON-NLS-1$
        customRecords.put(IRecordGrouping.TOKENIZATION_TYPE, TokenizedResolutionMethod.NO.toString()); // $NON-NLS-1$
        customRecords.put(IRecordGrouping.CUSTOMER_MATCH_CLASS, "testCustomMatcher.myCustomMatcherNullMatchAll"); //$NON-NLS-1$
        customRecords.put(IRecordGrouping.JAR_PATH, this.getClass().getResource("custom/CustomMatcherTest1.jar").getPath()); //$NON-NLS-1$
        customRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(0.9));
        customRecords.put(IRecordGrouping.ATTRIBUTE_THRESHOLD, String.valueOf(2));
        matchingRule.add(customRecords);

        recordGroup.addMatchRule(matchingRule);
        try {
            recordGroup.initialize();
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail(e.getMessage());
        }

        recordGroup.setIsOutputDistDetails(true);
        recordGroup.setAcceptableThreshold(0.95f);
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
        // mzhao, the output wont be in memory any more for performance consideration.
        // List<String[]> records = recordGroup.getResults();
        // Print the records

        for (String[] rds : groupingRecords) {
            if (rds[0].equals("26997914900")) { //$NON-NLS-1$
                // The group size should be 1 for account 26997914900 after add a custom match rule, before is 5
                Assert.assertEquals(1, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("13700177100")) { //$NON-NLS-1$
                // The group size should be 1 for account 13700177100 after add a custom match rule, before is 6
                Assert.assertEquals(1, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("12083684802")) { //$NON-NLS-1$
                // The group size should be 1 for account 12083684802 after add a custom match rule, before is 4
                Assert.assertEquals(1, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("13758354187")) { //$NON-NLS-1$
                // The group size should be 1 for account 13758354187 after add a custom match rule, before is 2
                Assert.assertEquals(1, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("15114446900")) { //$NON-NLS-1$
                // The group size should be 1 for account 15114446900 after add a custom match rule, before is 2
                Assert.assertEquals(1, Integer.valueOf(rds[rds.length - 5]).intValue());
            }

            for (String rd : rds) {
                log.info(rd + ","); //$NON-NLS-1$
            }

        }

        testGroupQuality();

        // Test record match threshold

        testMatchThreshold();

        testMatchThreshold_0();
    }

    /**
     * DOC zhao Comment method "testGroupQuality".
     */
    private void testGroupQuality() {
        // Test group quality
        try {
            recordGroup.initialize();
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }
        groupingRecords.clear();
        recordGroup.setIsComputeGrpQuality(Boolean.TRUE);
        recordGroup.setIsOutputDistDetails(true);
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
        for (String[] rds : groupingRecords) {
            if (rds[0].equals("95006021900")) { //$NON-NLS-1$
                // Assert group score
                Assert.assertEquals(0.9768518573708005, Double.valueOf(rds[rds.length - 2]).doubleValue(), 0d);
            }

        }
    }

    /**
     * DOC zhao Comment method "testMatchThreshold".
     */
    private void testMatchThreshold() {
        List<Map<String, String>> matchingRule;
        Map<String, String> lnameRecords;
        Map<String, String> accountRecords;
        groupingRecords.clear();
        recordGroup = new AbstractRecordGrouping<String>() {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(java.lang.String)
             */

            @Override
            protected void outputRow(String[] row) {
                groupingRecords.add(row);

            }

            @Override
            protected boolean isMaster(String col) {
                return "true".equals(col); //$NON-NLS-1$
            }

            @Override
            protected String incrementGroupSize(String oldGroupSize) {
                String newGroupSize = String.valueOf(Integer.parseInt(String.valueOf(oldGroupSize)) + 1);
                return newGroupSize;
            }

            @Override
            protected String[] createTYPEArray(int size) {
                String[] arrays = new String[size];
                return arrays;
            }

            @Override
            protected String castAsType(Object objectValue) {
                String column = String.valueOf(objectValue);
                return column;
            }

            @Override
            protected void outputRow(RichRecord row) {
                // Empty implementation for vsr
            }

        };
        recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        matchingRule = new ArrayList<Map<String, String>>();

        lnameRecords = new HashMap<String, String>();
        lnameRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.MATCHING_TYPE, "JARO_WINKLER"); //$NON-NLS-1$
        lnameRecords.put(IRecordGrouping.TOKENIZATION_TYPE, TokenizedResolutionMethod.NO.toString()); // $NON-NLS-1$
        lnameRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.ATTRIBUTE_THRESHOLD, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.RECORD_MATCH_THRESHOLD, String.valueOf(0.95f));
        matchingRule.add(lnameRecords);

        lnameRecords = new HashMap<String, String>();
        lnameRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.MATCHING_TYPE, "JARO_WINKLER"); //$NON-NLS-1$
        lnameRecords.put(IRecordGrouping.TOKENIZATION_TYPE, TokenizedResolutionMethod.NO.toString()); // $NON-NLS-1$
        lnameRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.RECORD_MATCH_THRESHOLD, String.valueOf(0.0f));
        matchingRule.add(lnameRecords);

        // matching parameters for state_province
        accountRecords = new HashMap<String, String>();
        accountRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(6));
        accountRecords.put(IRecordGrouping.MATCHING_TYPE, "LEVENSHTEIN"); //$NON-NLS-1$
        accountRecords.put(IRecordGrouping.TOKENIZATION_TYPE, TokenizedResolutionMethod.NO.toString()); // $NON-NLS-1$

        accountRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(0.8));
        accountRecords.put(IRecordGrouping.RECORD_MATCH_THRESHOLD, String.valueOf(0.95f));// Duplicate set of threshold,
        matchingRule.add(accountRecords);

        recordGroup.addMatchRule(matchingRule);
        recordGroup.setIsOutputDistDetails(true);
        try {
            recordGroup.initialize();
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }

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
        // mzhao, the output wont be in memory any more for performance consideration.
        // List<String[]> records = recordGroup.getResults();
        // Print the records

        for (String[] rds : groupingRecords) {
            if (rds[0].equals("26997914900")) { //$NON-NLS-1$
                // The group size should be 5 for account 26997914900
                Assert.assertEquals(5, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("13700177100")) { //$NON-NLS-1$
                // The group size should be 6 for account 13700177100
                Assert.assertEquals(6, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("12083684802")) { //$NON-NLS-1$
                // The group size should be 4 for account 12083684802
                Assert.assertEquals(4, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("13758354187")) { //$NON-NLS-1$
                // The group size should be 2 for account 13758354187
                Assert.assertEquals(1, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("15114446900")) { //$NON-NLS-1$
                // The group size should be 2 for account 15114446900
                Assert.assertEquals(2, Integer.valueOf(rds[rds.length - 5]).intValue());
            }

            for (String rd : rds) {
                log.info(rd + ","); //$NON-NLS-1$
            }

        }
    }

    /**
     * when RECORD_MATCH_THRESHOLD=0, all records should be in one group
     */
    private void testMatchThreshold_0() {
        List<Map<String, String>> matchingRule;
        Map<String, String> lnameRecords;
        groupingRecords.clear();
        recordGroup = new AbstractRecordGrouping<String>() {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(java.lang.String)
             */

            @Override
            protected void outputRow(String[] row) {
                groupingRecords.add(row);

            }

            @Override
            protected boolean isMaster(String col) {
                return "true".equals(col); //$NON-NLS-1$
            }

            @Override
            protected String incrementGroupSize(String oldGroupSize) {
                String newGroupSize = String.valueOf(Integer.parseInt(String.valueOf(oldGroupSize)) + 1);
                return newGroupSize;
            }

            @Override
            protected String[] createTYPEArray(int size) {
                String[] arrays = new String[size];
                return arrays;
            }

            @Override
            protected String castAsType(Object objectValue) {
                String column = String.valueOf(objectValue);
                return column;
            }

            @Override
            protected void outputRow(RichRecord row) {
                // Empty implementation.
            }

        };
        recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        matchingRule = new ArrayList<Map<String, String>>();

        lnameRecords = new HashMap<String, String>();
        lnameRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.MATCHING_TYPE, "JARO_WINKLER"); //$NON-NLS-1$
        lnameRecords.put(IRecordGrouping.TOKENIZATION_TYPE, TokenizedResolutionMethod.NO.toString()); // $NON-NLS-1$
        lnameRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.ATTRIBUTE_THRESHOLD, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.RECORD_MATCH_THRESHOLD, String.valueOf(0.0f));
        matchingRule.add(lnameRecords);

        recordGroup.addMatchRule(matchingRule);
        recordGroup.setIsOutputDistDetails(true);
        try {
            recordGroup.initialize();
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }

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

        for (String[] rds : groupingRecords) {
            if (rds[0].equals("26997914900")) { //$NON-NLS-1$
                Assert.assertEquals(0, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("13700177100")) { //$NON-NLS-1$
                Assert.assertEquals(0, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("12083684802")) { //$NON-NLS-1$
                Assert.assertEquals(0, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("13758354187")) { //$NON-NLS-1$
                Assert.assertEquals(0, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("15114446900")) { //$NON-NLS-1$
                Assert.assertEquals(0, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
            if (rds[0].equals("10389564000")) { //$NON-NLS-1$
                // The group size should be 101 , all records in one group
                Assert.assertEquals(101, Integer.valueOf(rds[rds.length - 5]).intValue());
            }

            for (String rd : rds) {
                log.info(rd + ","); //$NON-NLS-1$
            }

        }
    }
}
