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
package org.talend.dataquality.record.linkage.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class SurvivorshipUtilsTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils#createSurvivorShipAlgorithmParams(org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping, java.util.List, java.util.List, java.util.Map, java.util.Map)}
     * .
     * 
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test
    public void testCreateSurvivorShipAlgorithmParamsUpper()
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = matcherListInit(true);
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new SwooshMatchRecordGroupingImpl();
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();

        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = initDefaultRules();
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<>();
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<>();

        columnWithType_tMatchGroup_1.put("id", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("name", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("name", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("provinceID", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("provinceID", "2"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("gender", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("gender", "3"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GID", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GID", "4"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "5"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("MASTER", "6"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("SCORE", "7"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "8"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", //$NON-NLS-1$
                "id_String"); //$NON-NLS-1$
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "9"); //$NON-NLS-1$ //$NON-NLS-2$
        SurvivorShipAlgorithmParams createSurvivorShipAlgorithmParams = SurvivorshipUtils.createSurvivorShipAlgorithmParams(
                (AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1, matchingRulesAll_tMatchGroup_1,
                defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1, columnWithIndex_tMatchGroup_1);
        Map<IRecordMatcher, SurvivorshipFunction[]> survivorshipAlgosMap = createSurvivorShipAlgorithmParams
                .getSurvivorshipAlgosMap();
        SurvivorshipFunction[] survivorshipFunctions1 = survivorshipAlgosMap
                .get(recordGroupImp_tMatchGroup_1.getCombinedRecordMatcher().getMatchers().get(0));
        SurvivorshipFunction[] survivorshipFunctions2 = survivorshipAlgosMap
                .get(recordGroupImp_tMatchGroup_1.getCombinedRecordMatcher().getMatchers().get(1));

        Assert.assertNotNull(survivorshipFunctions1);
        Assert.assertNotNull(survivorshipFunctions2);
        // current AttributeMatcher is dummy and column type is mapping defaultFunction so that function use default
        Assert.assertEquals(SurvivorShipAlgorithmEnum.SMALLEST, survivorshipFunctions1[0].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions1[1].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is mapping defaultFunction so that function use default
        Assert.assertEquals(SurvivorShipAlgorithmEnum.SMALLEST, survivorshipFunctions1[2].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions1[3].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions2[0].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions2[1].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions2[2].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions2[3].getSurvivorShipAlgoEnum());

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils#createSurvivorShipAlgorithmParams(org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping, java.util.List, java.util.List, java.util.Map, java.util.Map)}
     * .
     * 
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test
    public void testCreateSurvivorShipAlgorithmParamsLowerWithParticularDefault()
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = matcherListInit(false);
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new SwooshMatchRecordGroupingImpl();
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();

        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = initDefaultRules();
        java.util.List<java.util.Map<String, String>> particularDefaultSurvivorshipDefinitions = initParticularDefaultRules();
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<>();
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<>();

        columnWithType_tMatchGroup_1.put("id", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("name", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("name", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("provinceID", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("provinceID", "2"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("gender", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("gender", "3"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GID", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GID", "4"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "5"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("MASTER", "6"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("SCORE", "7"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "8"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", //$NON-NLS-1$
                "id_String"); //$NON-NLS-1$
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "9"); //$NON-NLS-1$ //$NON-NLS-2$
        SurvivorShipAlgorithmParams createSurvivorShipAlgorithmParams = SurvivorshipUtils.createSurvivorShipAlgorithmParams(
                (AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1, matchingRulesAll_tMatchGroup_1,
                defaultSurvivorshipRules_tMatchGroup_1, particularDefaultSurvivorshipDefinitions, columnWithType_tMatchGroup_1,
                columnWithIndex_tMatchGroup_1);
        Map<IRecordMatcher, SurvivorshipFunction[]> survivorshipAlgosMap = createSurvivorShipAlgorithmParams
                .getSurvivorshipAlgosMap();
        SurvivorshipFunction[] survivorshipFunctions1 = survivorshipAlgosMap
                .get(recordGroupImp_tMatchGroup_1.getCombinedRecordMatcher().getMatchers().get(0));
        SurvivorshipFunction[] survivorshipFunctions2 = survivorshipAlgosMap
                .get(recordGroupImp_tMatchGroup_1.getCombinedRecordMatcher().getMatchers().get(1));

        Assert.assertNotNull(survivorshipFunctions1);
        Assert.assertNotNull(survivorshipFunctions2);
        // current AttributeMatcher is dummy and column type is mapping defaultFunction so that function use default
        Assert.assertEquals(SurvivorShipAlgorithmEnum.SMALLEST, survivorshipFunctions1[0].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions1[1].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is mapping ParticulardefaultFunction so that function use default
        Assert.assertEquals(SurvivorShipAlgorithmEnum.LONGEST, survivorshipFunctions1[2].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions1[3].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions2[0].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions2[1].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions2[2].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions2[3].getSurvivorShipAlgoEnum());

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils#createSurvivorShipAlgorithmParams(org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping, java.util.List, java.util.List, java.util.Map, java.util.Map)}
     * .
     * 
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test
    public void testCreateSurvivorShipAlgorithmParamsUpperWithParticularDefault()
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = matcherListInit(true);
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new SwooshMatchRecordGroupingImpl();
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();

        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = initDefaultRules();
        java.util.List<java.util.Map<String, String>> particularDefaultSurvivorshipDefinitions = initParticularDefaultRules();
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<>();
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<>();

        columnWithType_tMatchGroup_1.put("id", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("name", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("name", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("provinceID", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("provinceID", "2"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("gender", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("gender", "3"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GID", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GID", "4"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "5"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("MASTER", "6"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("SCORE", "7"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "8"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", //$NON-NLS-1$
                "id_String"); //$NON-NLS-1$
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "9"); //$NON-NLS-1$ //$NON-NLS-2$
        SurvivorShipAlgorithmParams createSurvivorShipAlgorithmParams = SurvivorshipUtils.createSurvivorShipAlgorithmParams(
                (AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1, matchingRulesAll_tMatchGroup_1,
                defaultSurvivorshipRules_tMatchGroup_1, particularDefaultSurvivorshipDefinitions, columnWithType_tMatchGroup_1,
                columnWithIndex_tMatchGroup_1);
        Map<IRecordMatcher, SurvivorshipFunction[]> survivorshipAlgosMap = createSurvivorShipAlgorithmParams
                .getSurvivorshipAlgosMap();
        SurvivorshipFunction[] survivorshipFunctions1 = survivorshipAlgosMap
                .get(recordGroupImp_tMatchGroup_1.getCombinedRecordMatcher().getMatchers().get(0));
        SurvivorshipFunction[] survivorshipFunctions2 = survivorshipAlgosMap
                .get(recordGroupImp_tMatchGroup_1.getCombinedRecordMatcher().getMatchers().get(1));

        Assert.assertNotNull(survivorshipFunctions1);
        Assert.assertNotNull(survivorshipFunctions2);
        // current AttributeMatcher is dummy and column type is mapping defaultFunction so that function use default
        Assert.assertEquals(SurvivorShipAlgorithmEnum.SMALLEST, survivorshipFunctions1[0].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions1[1].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is mapping particulardefaultFunction so that function use default
        Assert.assertEquals(SurvivorShipAlgorithmEnum.LONGEST, survivorshipFunctions1[2].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions1[3].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions2[0].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions2[1].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions2[2].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions2[3].getSurvivorShipAlgoEnum());

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils#createSurvivorShipAlgorithmParams(org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping, java.util.List, java.util.List, java.util.Map, java.util.Map)}
     * .
     * 
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test
    public void testCreateSurvivorShipAlgorithmParamsLower()
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = matcherListInit(false);
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new SwooshMatchRecordGroupingImpl();
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();

        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = initDefaultRules();
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<>();
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<>();

        columnWithType_tMatchGroup_1.put("id", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("name", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("name", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("provinceID", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("provinceID", "2"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("gender", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("gender", "3"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GID", "id_String"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GID", "4"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "5"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("MASTER", "6"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("SCORE", "7"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "8"); //$NON-NLS-1$ //$NON-NLS-2$
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", //$NON-NLS-1$
                "id_String"); //$NON-NLS-1$
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "9"); //$NON-NLS-1$ //$NON-NLS-2$
        SurvivorShipAlgorithmParams createSurvivorShipAlgorithmParams = SurvivorshipUtils.createSurvivorShipAlgorithmParams(
                (AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1, matchingRulesAll_tMatchGroup_1,
                defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1, columnWithIndex_tMatchGroup_1);
        Map<IRecordMatcher, SurvivorshipFunction[]> survivorshipAlgosMap = createSurvivorShipAlgorithmParams
                .getSurvivorshipAlgosMap();
        SurvivorshipFunction[] survivorshipFunctions1 = survivorshipAlgosMap
                .get(recordGroupImp_tMatchGroup_1.getCombinedRecordMatcher().getMatchers().get(0));
        SurvivorshipFunction[] survivorshipFunctions2 = survivorshipAlgosMap
                .get(recordGroupImp_tMatchGroup_1.getCombinedRecordMatcher().getMatchers().get(1));

        Assert.assertNotNull(survivorshipFunctions1);
        Assert.assertNotNull(survivorshipFunctions2);
        // current AttributeMatcher is dummy and column type is mapping defaultFunction so that function use default
        Assert.assertEquals(SurvivorShipAlgorithmEnum.SMALLEST, survivorshipFunctions1[0].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions1[1].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is mapping defaultFunction so that function use default
        Assert.assertEquals(SurvivorShipAlgorithmEnum.SMALLEST, survivorshipFunctions1[2].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions1[3].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions2[0].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions2[1].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is not dummy and column so that function keep Original
        Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunctions2[2].getSurvivorShipAlgoEnum());
        // current AttributeMatcher is dummy and column type is not mapping defaultFunction so that function fixed use CONCATENATE
        Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunctions2[3].getSurvivorShipAlgoEnum());

    }

    /**
     * DOC zshen Comment method "initDefaultRules".
     * 
     * @return
     */
    private List<Map<String, String>> initDefaultRules() {
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<>();
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new java.util.HashMap<>();
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", //$NON-NLS-1$
                "Smallest"); //$NON-NLS-1$
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "NUMBER"); //$NON-NLS-1$ //$NON-NLS-2$
        realSurShipMap_tMatchGroup_1.put("PARAMETER", ""); //$NON-NLS-1$ //$NON-NLS-2$
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);
        return defaultSurvivorshipRules_tMatchGroup_1;
    }

    /**
     * DOC zshen Comment method "initDefaultRules".
     * 
     * @return
     */
    private List<Map<String, String>> initParticularDefaultRules() {
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<>();
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new java.util.HashMap<>();
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", //$NON-NLS-1$
                "Longest"); //$NON-NLS-1$
        realSurShipMap_tMatchGroup_1.put("INPUT_COLUMN", "provinceID"); //$NON-NLS-1$ //$NON-NLS-2$
        realSurShipMap_tMatchGroup_1.put("PARAMETER", ""); //$NON-NLS-1$ //$NON-NLS-2$
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);
        return defaultSurvivorshipRules_tMatchGroup_1;
    }

    /**
     * DOC zshen Comment method "matcherListInit".
     * 
     * @return
     */
    private List<List<Map<String, String>>> matcherListInit(boolean uppercase) {
        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<>();
        List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = new ArrayList<>();
        Map<String, String> tmpMap_tMatchGroup_1 = new java.util.HashMap<>();
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", getDummyStr(uppercase)); //$NON-NLS-1$ 

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "id"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", 1.0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "0"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("TOKENIZATION_TYPE", "NO"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("PARAMETER", ""); //$NON-NLS-1$ //$NON-NLS-2$

        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = new java.util.HashMap<>();
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "name"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", 1.0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "1"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("TOKENIZATION_TYPE", "NO"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("PARAMETER", ""); //$NON-NLS-1$ //$NON-NLS-2$

        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = new java.util.HashMap<>();
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", getDummyStr(uppercase)); //$NON-NLS-1$ 

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "provinceID"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", 1.0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("TOKENIZATION_TYPE", "NO"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("PARAMETER", ""); //$NON-NLS-1$ //$NON-NLS-2$

        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = new java.util.HashMap<>();
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", getDummyStr(uppercase)); //$NON-NLS-1$ 

        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "gender"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", //$NON-NLS-1$
                SurvivorShipAlgorithmEnum
                        .getTypeByIndex((int) Math.round(Math.random() * (SurvivorShipAlgorithmEnum.getAllTypes().length - 1)))
                        .getComponentValueName());

        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", 1.0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "3"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("TOKENIZATION_TYPE", "NO"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("PARAMETER", ""); //$NON-NLS-1$ //$NON-NLS-2$

        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matcherList_tMatchGroup_1.sort(new Comparator<java.util.Map<String, String>>() {

            @Override
            public int compare(java.util.Map<String, String> map1, java.util.Map<String, String> map2) {

                return Integer.valueOf(map1.get("COLUMN_IDX")).compareTo(Integer.valueOf(map2.get("COLUMN_IDX"))); //$NON-NLS-1$ //$NON-NLS-2$
            }

        });
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        matcherList_tMatchGroup_1 = new java.util.ArrayList<>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<>();
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "id"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", 1.0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "0"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("TOKENIZATION_TYPE", "NO"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("PARAMETER", ""); //$NON-NLS-1$ //$NON-NLS-2$

        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = new java.util.HashMap<>();
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", getDummyStr(uppercase)); //$NON-NLS-1$ 

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "name"); //$NON-NLS-1$ //$NON-NLS-2$
        // get a random algorithm the result alaways be default one (most common)
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", //$NON-NLS-1$
                SurvivorShipAlgorithmEnum
                        .getTypeByIndex((int) Math.round(Math.random() * (SurvivorShipAlgorithmEnum.getAllTypes().length - 1)))
                        .getComponentValueName());

        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", 1.0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "1"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("TOKENIZATION_TYPE", "NO"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("PARAMETER", ""); //$NON-NLS-1$ //$NON-NLS-2$

        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = new java.util.HashMap<>();
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "provinceID"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", 1.0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("TOKENIZATION_TYPE", "NO"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("PARAMETER", ""); //$NON-NLS-1$ //$NON-NLS-2$

        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = new java.util.HashMap<>();
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", getDummyStr(uppercase)); //$NON-NLS-1$ 

        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "gender"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", //$NON-NLS-1$
                SurvivorShipAlgorithmEnum
                        .getTypeByIndex((int) Math.round(Math.random() * (SurvivorShipAlgorithmEnum.getAllTypes().length - 1)))
                        .getComponentValueName());

        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", 1.0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "3"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 0 + ""); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("TOKENIZATION_TYPE", "NO"); //$NON-NLS-1$ //$NON-NLS-2$

        tmpMap_tMatchGroup_1.put("PARAMETER", ""); //$NON-NLS-1$ //$NON-NLS-2$

        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matcherList_tMatchGroup_1.sort(new Comparator<java.util.Map<String, String>>() {

            @Override
            public int compare(java.util.Map<String, String> map1, java.util.Map<String, String> map2) {

                return Integer.valueOf(map1.get("COLUMN_IDX")).compareTo(Integer.valueOf(map2.get("COLUMN_IDX"))); //$NON-NLS-1$ //$NON-NLS-2$
            }

        });
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        return matchingRulesAll_tMatchGroup_1;
    }

    /**
     * DOC zshen Comment method "getDummyStr".
     * 
     * @param uppercase
     * @return
     */
    private String getDummyStr(boolean uppercase) {
        if (uppercase) {
            return "DUMMY"; //$NON-NLS-1$
        }
        return "dummy"; //$NON-NLS-1$
    }

    public static class row2Struct {

        final static byte[] commonByteArrayLock_TEST1_test_tMatchGroup_1 = new byte[0];

        static byte[] commonByteArray_TEST1_test_tMatchGroup_1 = new byte[0];

        public Integer id;

        public Integer getId() {
            return this.id;
        }

        public String name;

        public String getName() {
            return this.name;
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
            sb.append("["); //$NON-NLS-1$
            sb.append("id=" + String.valueOf(id)); //$NON-NLS-1$
            sb.append(",name=" + name); //$NON-NLS-1$
            sb.append(",provinceID=" + String.valueOf(provinceID)); //$NON-NLS-1$
            sb.append(",GID=" + GID); //$NON-NLS-1$
            sb.append(",GRP_SIZE=" + String.valueOf(GRP_SIZE)); //$NON-NLS-1$
            sb.append(",MASTER=" + String.valueOf(MASTER)); //$NON-NLS-1$
            sb.append(",SCORE=" + String.valueOf(SCORE)); //$NON-NLS-1$
            sb.append(",GRP_QUALITY=" + String.valueOf(GRP_QUALITY)); //$NON-NLS-1$
            sb.append(",MATCHING_DISTANCES=" + MATCHING_DISTANCES); //$NON-NLS-1$
            sb.append("]"); //$NON-NLS-1$

            return sb.toString();
        }

        public String toLogString() {
            StringBuilder sb = new StringBuilder();

            if (id == null) {
                sb.append("<null>"); //$NON-NLS-1$
            } else {
                sb.append(id);
            }

            sb.append("|"); //$NON-NLS-1$

            if (name == null) {
                sb.append("<null>"); //$NON-NLS-1$
            } else {
                sb.append(name);
            }

            sb.append("|"); //$NON-NLS-1$

            if (provinceID == null) {
                sb.append("<null>"); //$NON-NLS-1$
            } else {
                sb.append(provinceID);
            }

            sb.append("|"); //$NON-NLS-1$

            if (GID == null) {
                sb.append("<null>"); //$NON-NLS-1$
            } else {
                sb.append(GID);
            }

            sb.append("|"); //$NON-NLS-1$

            if (GRP_SIZE == null) {
                sb.append("<null>"); //$NON-NLS-1$
            } else {
                sb.append(GRP_SIZE);
            }

            sb.append("|"); //$NON-NLS-1$

            if (MASTER == null) {
                sb.append("<null>"); //$NON-NLS-1$
            } else {
                sb.append(MASTER);
            }

            sb.append("|"); //$NON-NLS-1$

            if (SCORE == null) {
                sb.append("<null>"); //$NON-NLS-1$
            } else {
                sb.append(SCORE);
            }

            sb.append("|"); //$NON-NLS-1$

            if (GRP_QUALITY == null) {
                sb.append("<null>"); //$NON-NLS-1$
            } else {
                sb.append(GRP_QUALITY);
            }

            sb.append("|"); //$NON-NLS-1$

            if (MATCHING_DISTANCES == null) {
                sb.append("<null>"); //$NON-NLS-1$
            } else {
                sb.append(MATCHING_DISTANCES);
            }

            sb.append("|"); //$NON-NLS-1$

            return sb.toString();
        }

        /**
         * Compare keys
         */
        public int compareTo(row2Struct other) {

            int returnValue = -1;

            return returnValue;
        }

    }

    class SwooshMatchRecordGroupingImpl
            extends org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping {

        @Override
        protected void outputRow(Object[] row) {
            row2Struct outStuct_tMatchGroup_1 = new row2Struct();

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
                    outStuct_tMatchGroup_1.provinceID = Integer.valueOf((String) row[2]);
                } catch (java.lang.NumberFormatException e) {
                    outStuct_tMatchGroup_1.provinceID = row[2] == null ? null : 0;
                }
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
        }

        @Override
        protected boolean isMaster(Object col) {
            return String.valueOf(col).equals("true"); //$NON-NLS-1$
        }
    }

}
