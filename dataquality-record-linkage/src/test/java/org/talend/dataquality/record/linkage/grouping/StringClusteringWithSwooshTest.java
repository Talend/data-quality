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
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;
import org.talend.dataquality.record.linkage.genkey.BlockingKeyHandler;
import org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

public class StringClusteringWithSwooshTest {

    private static Logger log = LoggerFactory.getLogger(StringClusteringWithSwooshTest.class);

    /**
     * The input data.
     */
    private List<Object[]> inputList = null;

    private IRecordGrouping<Object> recordGroup = null;

    private static final String columnDelimiter = "|"; //$NON-NLS-1$

    private List<Object[]> groupingRecords = new ArrayList<Object[]>();

    @Before
    public void setUp() throws Exception {

        groupingRecords.clear();

    }

    @Test
    public void testDoGroupMergeValues() throws IOException {
        InputStream in = this.getClass().getResourceAsStream("incoming_customers_swoosh_fingerprintkey.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<Object[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(new Object[] { fields[1] });
        }
        String columnName = "NAME";
        // Blocking the data given fingerprint key
        List<Map<String, String>> blockKeySchema = new ArrayList<Map<String, String>>();
        Map<String, String> blockKeyDefMap = new HashMap<String, String>();

        blockKeyDefMap.put(MatchAnalysisConstant.PRECOLUMN, columnName);
        blockKeyDefMap.put(MatchAnalysisConstant.KEY_ALGO, BlockingKeyAlgorithmEnum.FINGERPRINTKEY.getValue());
        blockKeySchema.add(blockKeyDefMap);

        Map<String, String> colName2IndexMap = new HashMap<String, String>();
        colName2IndexMap.put(columnName, String.valueOf(0));
        BlockingKeyHandler blockKeyHandler = new BlockingKeyHandler(blockKeySchema, colName2IndexMap);
        blockKeyHandler.setInputData(inputList);
        blockKeyHandler.run();
        Map<String, List<String[]>> resultData = blockKeyHandler.getResultDatas();

        // Do grouping given swoosh algorithm with Dummy matcher.
        JunitResultConsumer resultConsumer = new JunitResultConsumer();
        recordGroup = new AnalysisSwooshMatchRecordGrouping(resultConsumer);
        ((AnalysisSwooshMatchRecordGrouping) recordGroup).setOrginalInputColumnSize(2);
        recordGroup.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams = new SurvivorShipAlgorithmParams();
        SurvivorshipFunction func = survivorShipAlgorithmParams.new SurvivorshipFunction();
        func.setParameter(""); //$NON-NLS-1$
        func.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);

        survivorShipAlgorithmParams.setSurviorShipAlgos(new SurvivorshipFunction[] { func });
        recordGroup.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);

        // // Set default survivorship functions.
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<Integer, SurvivorshipFunction>();
        SurvivorshipFunction survFunc = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc.setParameter(StringUtils.EMPTY);
        survFunc.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        defaultSurvRules.put(0, survFunc);

        survivorShipAlgorithmParams.setDefaultSurviorshipRules(defaultSurvRules);

        // recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        List<Map<String, String>> matchingRule = new ArrayList<Map<String, String>>();

        Map<String, String> lnameRecords = new HashMap<String, String>();
        lnameRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(0));
        lnameRecords.put(IRecordGrouping.ATTRIBUTE_NAME, columnName);
        lnameRecords.put(IRecordGrouping.MATCHING_TYPE, AttributeMatcherType.DUMMY.name());
        lnameRecords.put(IRecordGrouping.TOKENIZATION_TYPE, TokenizedResolutionMethod.NO.toString());
        lnameRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.ATTRIBUTE_THRESHOLD, String.valueOf(0.9));

        matchingRule.add(lnameRecords);

        recordGroup.setIsOutputDistDetails(false);
        recordGroup.setAcceptableThreshold(0.95f);
        try {

            // loop on all input rows.
            Iterator<List<String[]>> values = resultData.values().iterator();
            while (values.hasNext()) {
                recordGroup.addMatchRule(matchingRule);
                recordGroup.initialize();
                // for each block
                for (Object[] inputRow : values.next()) {
                    recordGroup.doGroup(inputRow);
                }
                recordGroup.end();
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }
        // Assertions

        Object[] rds = resultConsumer.getResult();
        // for (Object[] rds : ) {
        if (rds[rds.length - 5].equals("5")) { //$NON-NLS-1$
            // Group quality.
            Assert.assertEquals(1, Double.valueOf(rds[rds.length - 2].toString()).doubleValue(), 0d);
            // Assert the merged value is the "most common" value.
            Assert.assertEquals("élément", rds[0].toString());
        }
        // }

    }

    class JunitResultConsumer extends MatchGroupResultConsumer {

        public Object[] result;

        /**
         * Getter for result.
         * 
         * @return the result
         */
        public Object[] getResult() {
            return this.result;
        }

        /**
         * DOC zshen junitResultConsumer constructor comment.
         * 
         * @param isKeepDataInMemory
         */
        public JunitResultConsumer() {
            super(false);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer#handle(java.lang.Object)
         */
        @Override
        public void handle(Object row) {
            result = ((Object[]) row);
        }

    }

}
