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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.matchmerge.mfb.MFBAttributeMatcher;
import org.talend.dataquality.matchmerge.mfb.MFBRecordMatcher;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.attribute.LevenshteinMatcher;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * normal result should like this
 * 
 * <pre>
.-----+-----+------------+------------------------------------+--------+------+-----+-----------+------------------+----------.
|                                                          tLogRow_1                                                          |
|=----+-----+------------+------------------------------------+--------+------+-----+-----------+------------------+---------=|
|id_pk|id   |name        |GID                                 |GRP_SIZE|MASTER|SCORE|GRP_QUALITY|MATCHING_DISTANCES|MERGE_INFO|
|=----+-----+------------+------------------------------------+--------+------+-----+-----------+------------------+---------=|
|12   |12345|singlerecord|f3f3dbbe-8b45-490e-a154-9ac1ef5ec6a6|1       |true  |1.0  |1.0        |                  |true      |
'-----+-----+------------+------------------------------------+--------+------+-----+-----------+------------------+----------'

.-----+--+----+------------------------------------+--------+------+-----+-----------+------------------+----------.
|                                                    tLogRow_2                                                     |
|=----+--+----+------------------------------------+--------+------+-----+-----------+------------------+---------=|
|id_pk|id|name|GID                                 |GRP_SIZE|MASTER|SCORE|GRP_QUALITY|MATCHING_DISTANCES|MERGE_INFO|
|=----+--+----+------------------------------------+--------+------+-----+-----------+------------------+---------=|
|6    |33|null|650ce205-16cf-4c7a-9272-b89587929ca8|2       |true  |1.0  |1.0        |                  |true      |
|5    |3 |null|650ce205-16cf-4c7a-9272-b89587929ca8|0       |false |1.0  |0.0        |id: 1.0           |false     |
|6    |3 |null|650ce205-16cf-4c7a-9272-b89587929ca8|0       |false |1.0  |0.0        |id: 1.0           |false     |
'-----+--+----+------------------------------------+--------+------+-----+-----------+------------------+----------'

.-----+-----------+----------+------------------------------------+--------+------+------------------+------------------+------------------------+----------.
|                                                                         tLogRow_3                                                                         |
|=----+-----------+----------+------------------------------------+--------+------+------------------+------------------+------------------------+---------=|
|id_pk|id         |name      |GID                                 |GRP_SIZE|MASTER|SCORE             |GRP_QUALITY       |MATCHING_DISTANCES      |MERGE_INFO|
|=----+-----------+----------+------------------------------------+--------+------+------------------+------------------+------------------------+---------=|
|2    |111        |lilis     |10335353-cddb-4a97-98d6-fdc828caa064|5       |true  |1.0               |0.6666666666666667|                        |true      |
|4    |2          |lis       |10335353-cddb-4a97-98d6-fdc828caa064|0       |false |0.6666666666666667|0.0               |name: 0.6666666666666667|true      |
|2    |111        |li        |10335353-cddb-4a97-98d6-fdc828caa064|0       |false |0.6666666666666667|0.0               |name: 0.6666666666666667|true      |
|1    |1          |li        |10335353-cddb-4a97-98d6-fdc828caa064|0       |false |1.0               |0.0               |id: 1.0                 |false     |
|2    |1          |wang      |10335353-cddb-4a97-98d6-fdc828caa064|0       |false |1.0               |0.0               |id: 1.0                 |false     |
|3    |1          |zhang     |10335353-cddb-4a97-98d6-fdc828caa064|0       |false |1.0               |0.0               |id: 1.0                 |false     |
|8    |13         |zhaoszhao |229e5a16-0599-4b51-919f-ae38ffebbb60|2       |true  |1.0               |0.8               |                        |true      |
|7    |12         |zhao      |229e5a16-0599-4b51-919f-ae38ffebbb60|0       |false |0.8               |0.0               |name: 0.8               |true      |
|8    |13         |zhaos     |229e5a16-0599-4b51-919f-ae38ffebbb60|0       |false |0.8               |0.0               |name: 0.8               |true      |
|10   |nihaosnihao|hellohello|b4a6cad4-bd57-4a5f-acef-dd1215c1ac37|4       |true  |1.0               |0.8333333333333334|                        |true      |
|11   |16         |hello     |b4a6cad4-bd57-4a5f-acef-dd1215c1ac37|0       |false |1.0               |0.0               |name: 1.0               |true      |
|10   |nihaosnihao|hello     |b4a6cad4-bd57-4a5f-acef-dd1215c1ac37|0       |false |1.0               |0.0               |name: 1.0               |true      |
|9    |nihao      |gogogo    |b4a6cad4-bd57-4a5f-acef-dd1215c1ac37|0       |false |0.8333333333333334|0.0               |id: 0.8333333333333334  |false     |
|10   |nihaos     |hello     |b4a6cad4-bd57-4a5f-acef-dd1215c1ac37|0       |false |0.8333333333333334|0.0               |id: 0.8333333333333334  |false     |
'-----+-----------+----------+------------------------------------+--------+------+------------------+------------------+------------------------+----------'
 * </pre>
 */
public class TSwooshGroupingTest {

    public List<RichRecord> result = new ArrayList<>();

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.TSwooshGrouping#swooshMatchWithMultipass(org.talend.dataquality.record.linkage.record.CombinedRecordMatcher, org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams, int)}
     * .
     */
    @Test
    public void testSwooshMatchWithMultipass() {
        // init data
        List<String[]> inputDataList = new ArrayList<>();
        inputDataList.add(new String[] { "10", "nihaosnihao", "hello", "0ca5ee30-6377-4995-8f93-7f167bc5e16e", "2", "true", "1.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                "0.8333333333333334", "", "true" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        inputDataList.add(new String[] { "9", "nihao", "gogogo", "0ca5ee30-6377-4995-8f93-7f167bc5e16e", "0", "false", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                "0.8333333333333334", "0.0", "id:0.8333333333333334", "false" });//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        inputDataList.add(new String[] { "10", "nihaos", "hello", "0ca5ee30-6377-4995-8f93-7f167bc5e16e", "0", "false", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                "0.8333333333333334", "0.0", "id:0.8333333333333334", "false" });//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        inputDataList
                .add(new String[] { "12", "12345", "singlerecord", "3f1839cf-bf43-449e-9003-84747cc92a84", "1", "true", "1.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                        "1.0", "", "true" });//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        inputDataList
                .add(new String[] { "11", "16", "hello", "4149c23f-046d-4a2b-a04d-354e5fbf6afc", "1", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                        "true" });//$NON-NLS-1$
        inputDataList.add(new String[] { "6", "33", null, "66cf5b44-f290-46e9-ab88-3c0779d9fc7b", "2", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "true" });//$NON-NLS-1$
        inputDataList.add(new String[] { "5", "3", null, "66cf5b44-f290-46e9-ab88-3c0779d9fc7b", "0", "false", "1.0", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                "id:1.0", "false" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList.add(new String[] { "6", "3", null, "66cf5b44-f290-46e9-ab88-3c0779d9fc7b", "0", "false", "1.0", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                "id:1.0", "false" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList.add(new String[] { "4", "2", "lis", "70ae237d-c24d-4ffc-b82e-bf111b9c441f", "1", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                "true" });//$NON-NLS-1$
        inputDataList.add(new String[] { "2", "111", "li", "7da41ca8-a4b0-4bf4-8d4d-bb53edfd97ef", "3", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                "true" });//$NON-NLS-1$
        inputDataList.add(new String[] { "1", "1", "li", "7da41ca8-a4b0-4bf4-8d4d-bb53edfd97ef", "0", "false", "1.0", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "id:1.0", "false" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList.add(new String[] { "2", "1", "wang", "7da41ca8-a4b0-4bf4-8d4d-bb53edfd97ef", "0", "false", "1.0", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "id:1.0", "false" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList.add(new String[] { "3", "1", "zhang", "7da41ca8-a4b0-4bf4-8d4d-bb53edfd97ef", "0", "false", "1.0", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "id:1.0", "false" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList.add(new String[] { "7", "12", "zhao", "aa210319-0ed9-4075-903e-9cd6b25735e1", "1", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                "true" });//$NON-NLS-1$
        inputDataList
                .add(new String[] { "8", "13", "zhaos", "e5e55f2f-2dfd-4773-8c02-9acbaa13c278", "1", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                        "true" });//$NON-NLS-1$

        Map<String, String> matchRuleMap = new HashMap<>();
        matchRuleMap.put("MATCHING_TYPE", "Levenshtein"); //$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("RECORD_MATCH_THRESHOLD", "0.25");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("ATTRIBUTE_NAME", "name");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("SURVIVORSHIP_FUNCTION", "Concatenate");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("CONFIDENCE_WEIGHT", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("HANDLE_NULL", "nullMatchNull");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("ATTRIBUTE_THRESHOLD", "0.25");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("COLUMN_IDX", "2");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("PARAMETER", "");//$NON-NLS-1$ //$NON-NLS-2$
        List<Map<String, String>> ruleMapList = new ArrayList<>();
        List<List<Map<String, String>>> ruleMapListList = new ArrayList<>();
        ruleMapList.add(matchRuleMap);
        ruleMapListList.add(ruleMapList);
        TempRecordGrouping tempRecordGrouping = new TempRecordGrouping();
        tempRecordGrouping.setOrginalInputColumnSize(3);
        tempRecordGrouping.setIsLinkToPrevious(true);
        TSwooshGrouping<String> tSwooshGrouping = new TSwooshGrouping<>(tempRecordGrouping);
        for (String[] stringRow : inputDataList) {
            tSwooshGrouping.addToList(stringRow, ruleMapListList);
        }

        int originalInputColumnSize = 3;

        // init CombinedRecordMatcher
        CombinedRecordMatcher combinMatcher = new CombinedRecordMatcher();
        combinMatcher.setblockingThreshold(1.0);
        combinMatcher.setRecordSize(1);
        MFBRecordMatcher mfbRecordMatcher = new MFBRecordMatcher(0.25);
        mfbRecordMatcher.setAttributeWeights(new double[] { 1.0 });
        mfbRecordMatcher.setRecordMatchThreshold(0.25);
        mfbRecordMatcher.setRecordSize(1);
        LevenshteinMatcher levenshteinMatcher = new LevenshteinMatcher();
        levenshteinMatcher.setAttributeName("name"); //$NON-NLS-1$
        MFBAttributeMatcher mfbattMatcher = MFBAttributeMatcher.wrap(levenshteinMatcher, 1.0, 0.25, new SubString(-1, 0));
        mfbRecordMatcher.setAttributeMatchers(new IAttributeMatcher[] { mfbattMatcher });
        combinMatcher.add(mfbRecordMatcher);
        // mfbRecordMatcher.getMatchingWeight(null, null);

        SurvivorShipAlgorithmParams survivorParams = createSurvivoshipAlgorithm(combinMatcher, mfbRecordMatcher);

        // call the test method
        tSwooshGrouping.swooshMatchWithMultipass(combinMatcher, survivorParams, originalInputColumnSize);
        tSwooshGrouping.afterAllRecordFinished();
        // check result
        boolean singlerecordMasterIsExist = false;
        boolean nullMasterIsExist = false;
        boolean lilisMasterIsExist = false;
        boolean zhaoszhaoMasterIsExist = false;
        boolean hellohelloMasterIsExist = false;
        Assert.assertEquals("Expect the size of result items is 18 but it is " + result.size(), 18, result.size()); //$NON-NLS-1$
        for (RichRecord rr : result) {
            String s = rr.getGID() == null ? rr.getGroupId() : rr.getGID().getValue();
            String t = rr.getGRP_SIZE() == null ? rr.getGrpSize() + "" : rr.getGRP_SIZE().getValue();
            System.err.println("--" + rr.getAttributes().get(0).getValue() + "--" + s + "==" + rr.isMaster() + "==" + t + "=="
                    + rr.getGroupQuality());
            if (rr.isMaster()) {
                Attribute attribute = rr.getAttributes().get(0);
                if ("singlerecord".equals(attribute.getValue()) && rr.getGRP_SIZE().getValue().equals("1") //$NON-NLS-1$
                        && rr.getGRP_QUALITY().getValue().equals("1.0")) {
                    singlerecordMasterIsExist = true;
                } else if (null == rr.getAttributes().get(0).getValue() && rr.getGRP_SIZE().getValue().equals("2")
                        && rr.getGRP_QUALITY().getValue().equals("1.0")) {
                    nullMasterIsExist = true;
                } else if ("lilis".equals(rr.getAttributes().get(0).getValue()) && StringUtils.equals(t, "4") //$NON-NLS-1$
                        && rr.getGroupQuality() == 0.6666666666666667) {// && rr.getGrpSize() == 5 
                    lilisMasterIsExist = true;
                } else if ("zhaoszhao".equals(rr.getAttributes().get(0).getValue()) && rr.getGrpSize() == 2 //$NON-NLS-1$
                        && rr.getGroupQuality() == 0.8) {
                    zhaoszhaoMasterIsExist = true;
                } else if ("hellohello".equals(rr.getAttributes().get(0).getValue()) && rr.getGrpSize() == 2
                        && rr.getGroupQuality() == 0.8333333333333334) {
                    hellohelloMasterIsExist = true;
                }
            }
        }
        Assert.assertTrue(
                "There is a master data which name is singlerecord should be show at here and which group size should be 0 which group quality should be 1.0", //$NON-NLS-1$
                singlerecordMasterIsExist);
        Assert.assertTrue(
                "There is a master data which name is null should be show at here and which group size should be 2 which group quality should be 1.0",
                nullMasterIsExist);
        Assert.assertTrue(
                "There is a master data which name is lilis should be show at here and which group size should be 5 which group quality should be 0.6666666666666667",
                lilisMasterIsExist);
        Assert.assertTrue(
                "There is a master data which name is zhaoszhao should be show at here and which group size should be 2 which group quality should be 0.8",
                zhaoszhaoMasterIsExist);
        Assert.assertTrue(
                "There is a master data which name is hellohello should be show at here and which group size should be 4 which group quality should be 0.8333333333333334",
                hellohelloMasterIsExist);
    }

    @Test
    /**
     * <pre>
     * +------+------------------+-----------------=|----------------------------------------------------------------------------------|
    |id|name         |blockkey1|blockkey2|GID                                 |GRP_SIZE|MASTER|SCORE             |GRP_QUALITY       |
    |=-+-------------+---------+---------+------------------------------------+--------+------+------------------+-----------------=|
    |6 | John Doe    | UK      | M       |139af1e9-76df-4014-b3f9-a9db21516e70|1       |true  |1.0               |1.0               |
    |5 | John B. Doe | UK      | F       |8cda2151-f4f7-4141-8bb2-271c872a9576|1       |true  |1.0               |1.0               |
    |1 | John Doe    | FR      | F       |ebb0c52a-6418-438f-995a-3cb361e5a82a|2       |true  |1.0               |0.9               |
    |1 | John Doe    | FR      | M       |ebb0c52a-6418-438f-995a-3cb361e5a82a|0       |false |0.9               |0.0               |
    |2 | Jon Doe     | FR      | F       |ebb0c52a-6418-438f-995a-3cb361e5a82a|0       |false |0.9               |0.0               |
    |3 | John Doe    | US      | F       |539379d2-6a03-41eb-8ef0-72ff1a0a694a|2       |true  |1.0               |0.8333333333333334|
    |3 | John Doe    | US      | F       |539379d2-6a03-41eb-8ef0-72ff1a0a694a|0       |false |0.8333333333333334|0.0               |
    |4 | Johnny Doe  | US      | M       |539379d2-6a03-41eb-8ef0-72ff1a0a694a|0       |false |0.8333333333333334|0.0               |
    '--+-------------+---------+---------+------------------------------------+--------+------+------------------+------------------'
     * Expected:
     * 1. id "1" and blockkey2 "M" can be output
     * 2. id 4 can  output
     * 3.id "4" GID should be updated with master.
     * </pre>
     */
    public void testSwooshMatchWithMultipass_withblock() {
        // look as block 1
        List<String[]> inputDataList_1 = new ArrayList<>();
        // look as block 2
        List<String[]> inputDataList_2 = new ArrayList<>();
        inputDataList_1
                .add(new String[] { "5", "John B. Doe", "UK", "F", "8cda2151-f4f7-4141-8bb2-271c872a9576", "1", "true", "1.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                        "1.0" });//$NON-NLS-1$ 
        inputDataList_1.add(new String[] { "1", "John Doe", "FR", "F", "ebb0c52a-6418-438f-995a-3cb361e5a82a", "2", "true", "1.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "0.9" });//$NON-NLS-1$ 
        inputDataList_1
                .add(new String[] { "2", "Jon Doe", "FR", "F", "ebb0c52a-6418-438f-995a-3cb361e5a82a", "0", "false", "0.9", "0.0" //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
        });
        inputDataList_1.add(new String[] { "3", "John Doe", "US", "F", "539379d2-6a03-41eb-8ef0-72ff1a0a694a", "0", "true", "1.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$//$NON-NLS-7$//$NON-NLS-8$
                "0.8333333333333334" });//$NON-NLS-1$
        inputDataList_1.add(new String[] { "3", "John Doe", "US", "F", "539379d2-6a03-41eb-8ef0-72ff1a0a694a", "0", "false", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$//$NON-NLS-7$
                "0.8333333333333334", "0.0" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList_2
                .add(new String[] { "1", "John Doe", "FR", "M", "ebb0c52a-6418-438f-995a-3cb361e5a82a", "0", "false", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                        "1.0" });//$NON-NLS-1$ 
        inputDataList_2.add(new String[] { "6", "John Doe", "UK", "M", "139af1e9-76df-4014-b3f9-a9db21516e70", "1", "true", "1.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "1.0" }); //$NON-NLS-1$
        inputDataList_2.add(new String[] { "4", "Johnny Doe", "US", "M", "539379d2-6a03-41eb-8ef0-72ff1a0a694a", "0", "false", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$//$NON-NLS-7$
                "0.8333333333333334", "0.0" });//$NON-NLS-1$ //$NON-NLS-2$

        List<List<Map<String, String>>> ruleMapListList = createMatchRules();
        TempRecordGrouping tempRecordGrouping = new TempRecordGrouping();
        tempRecordGrouping.setOrginalInputColumnSize(4);
        tempRecordGrouping.setIsLinkToPrevious(true);
        tempRecordGrouping.setIsStoreOndisk(true);
        TSwooshGrouping<String> tSwooshGrouping = new TSwooshGrouping<String>(tempRecordGrouping);

        // for block 1
        for (String[] stringRow : inputDataList_1) {
            tSwooshGrouping.addToList(stringRow, ruleMapListList);
        }

        int originalInputColumnSize = 4;

        // init CombinedRecordMatcher
        CombinedRecordMatcher combinMatcher = new CombinedRecordMatcher();
        combinMatcher.setblockingThreshold(1.0);
        combinMatcher.setRecordSize(1);
        MFBRecordMatcher mfbRecordMatcher = createMFBMatcher();
        combinMatcher.add(mfbRecordMatcher);

        SurvivorShipAlgorithmParams survivorParams = createSurvivoshipAlgorithm(combinMatcher, mfbRecordMatcher);

        // call the test method
        tSwooshGrouping.swooshMatchWithMultipass(combinMatcher, survivorParams, originalInputColumnSize);
        tSwooshGrouping.afterAllRecordFinished();

        // for block 2
        for (String[] stringRow : inputDataList_2) {
            tSwooshGrouping.addToList(stringRow, ruleMapListList);
        }

        // call the test method
        tSwooshGrouping.swooshMatchWithMultipass(combinMatcher, survivorParams, originalInputColumnSize);
        tSwooshGrouping.afterAllRecordFinished();

        // check result
        boolean isOutput4 = false;
        boolean isOutput1WithM = false;
        String updated4GID = "ebb0c52a-6418-438f-995a-3cb361e5a82a"; //$NON-NLS-1$
        boolean isUpdated4GID = false;
        for (RichRecord rr : result) {
            String s = rr.getGroupId() == null ? rr.getGID().getValue() : rr.getGroupId();
            String t = rr.getGRP_SIZE() == null ? rr.getGrpSize() + "" : rr.getGRP_SIZE().getValue(); //$NON-NLS-1$
            String id = rr.getAttributes().get(0).getValue();
            System.err.println("--" + rr.getAttributes().get(0).getValue() + "--" + s + "==" + rr.isMaster() + "==" + t + "==" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                    + rr.getGroupQuality());
            if ("4".equals(id)) { //$NON-NLS-1$
                isOutput4 = true;
                if (updated4GID.equals(s)) {
                    isUpdated4GID = true;
                }

            } else if ("1".equals(id) && "M".equals(rr.getAttributes().get(3).getValue())) { //$NON-NLS-1$//$NON-NLS-2$
                isOutput1WithM = true;
            }
        }
        Assert.assertTrue(isOutput4);
        Assert.assertTrue(isOutput1WithM);
        Assert.assertTrue(isUpdated4GID);
    }

    @Test
    /**
     * <pre>
     * +------+------------------+-----------------=|----------------------------------------------------------------------------------|
    |id|name         |blockkey1|blockkey2|GID                                 |GRP_SIZE|MASTER|SCORE             |GRP_QUALITY       |
    |=-+-------------+---------+---------+------------------------------------+--------+------+------------------+-----------------=|
    |  |1 | John Doe | FR      | F       |ebb0c52a-6418-438f-995a-3cb361e5a82a|2       |true  |1.0               |0.9               |
    |1 | John Doe    | FR      | M       |ebb0c52a-6418-438f-995a-3cb361e5a82a|0       |false |0.9               |0.0               |
    |2 | Jon Doe     | FR      | F       |ebb0c52a-6418-438f-995a-3cb361e5a82a|0       |false |0.9               |0.0               |
    |3 | John Doe    | US      | F       |539379d2-6a03-41eb-8ef0-72ff1a0a694a|2       |true  |1.0               |0.8333333333333334|
    |3 | John Doe    | US      | F       |539379d2-6a03-41eb-8ef0-72ff1a0a694a|0       |false |0.8333333333333334|0.0               |
    |4 | Johnny Doe  | US      | M       |539379d2-6a03-41eb-8ef0-72ff1a0a694a|0       |false |0.8333333333333334|0.0               |
    '--+-------------+---------+---------+------------------------------------+--------+------+------------------+------------------'
     * 
     * Expected:
     * 1. id "4" can  output
     * 2.id "4" GID should be updated with master.
     * </pre>
     */
    public void testSwooshMatchWithMultipass_withblock2() {
        // look as block 1
        List<String[]> inputDataList_1 = new ArrayList<>();
        // look as block 2
        List<String[]> inputDataList_2 = new ArrayList<>();
        inputDataList_1.add(new String[] { "1", "John Doe", "FR", "F", "ebb0c52a-6418-438f-995a-3cb361e5a82a", "2", "true", "1.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "0.9" });//$NON-NLS-1$ 
        inputDataList_1
                .add(new String[] { "2", "Jon Doe", "FR", "F", "ebb0c52a-6418-438f-995a-3cb361e5a82a", "0", "false", "0.9", "0.0" //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
        });
        inputDataList_1.add(new String[] { "3", "John Doe", "US", "F", "539379d2-6a03-41eb-8ef0-72ff1a0a694a", "0", "true", "1.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$//$NON-NLS-7$//$NON-NLS-8$
                "0.8333333333333334" });//$NON-NLS-1$
        inputDataList_1.add(new String[] { "3", "John Doe", "US", "F", "539379d2-6a03-41eb-8ef0-72ff1a0a694a", "0", "false", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$//$NON-NLS-7$
                "0.8333333333333334", "0.0" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList_2
                .add(new String[] { "1", "John Doe", "FR", "M", "ebb0c52a-6418-438f-995a-3cb361e5a82a", "0", "false", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                        "1.0" });//$NON-NLS-1$ 
        inputDataList_2.add(new String[] { "4", "Johnny Doe", "US", "M", "539379d2-6a03-41eb-8ef0-72ff1a0a694a", "0", "false", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$//$NON-NLS-7$
                "0.8333333333333334", "0.0" });//$NON-NLS-1$ //$NON-NLS-2$

        List<List<Map<String, String>>> ruleMapListList = createMatchRules();
        TempRecordGrouping tempRecordGrouping = new TempRecordGrouping();
        tempRecordGrouping.setOrginalInputColumnSize(4);
        tempRecordGrouping.setIsLinkToPrevious(true);
        tempRecordGrouping.setIsStoreOndisk(true);
        TSwooshGrouping<String> tSwooshGrouping = new TSwooshGrouping<String>(tempRecordGrouping);
        // for block 1
        for (String[] stringRow : inputDataList_1) {
            tSwooshGrouping.addToList(stringRow, ruleMapListList);
        }

        int originalInputColumnSize = 4;

        // init CombinedRecordMatcher
        CombinedRecordMatcher combinMatcher = new CombinedRecordMatcher();
        combinMatcher.setblockingThreshold(1.0);
        combinMatcher.setRecordSize(1);
        MFBRecordMatcher mfbRecordMatcher = createMFBMatcher();
        combinMatcher.add(mfbRecordMatcher);
        SurvivorShipAlgorithmParams survivorParams = createSurvivoshipAlgorithm(combinMatcher, mfbRecordMatcher);

        // call the test method
        tSwooshGrouping.swooshMatchWithMultipass(combinMatcher, survivorParams, originalInputColumnSize);
        tSwooshGrouping.afterAllRecordFinished();
        // for block 2
        for (String[] stringRow : inputDataList_2) {
            tSwooshGrouping.addToList(stringRow, ruleMapListList);
        }

        // call the test method
        tSwooshGrouping.swooshMatchWithMultipass(combinMatcher, survivorParams, originalInputColumnSize);
        tSwooshGrouping.afterAllRecordFinished();

        // check result
        boolean isOutput4 = false;
        String updated4GID = "ebb0c52a-6418-438f-995a-3cb361e5a82a"; //$NON-NLS-1$
        boolean isUpdated4GID = false;
        for (RichRecord rr : result) {
            String s = rr.getGroupId() == null ? rr.getGID().getValue() : rr.getGroupId();
            String t = rr.getGRP_SIZE() == null ? rr.getGrpSize() + "" : rr.getGRP_SIZE().getValue(); //$NON-NLS-1$
            String id = rr.getAttributes().get(0).getValue();
            System.err.println("--" + rr.getAttributes().get(0).getValue() + "--" + s + "==" + rr.isMaster() + "==" + t + "==" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                    + rr.getGroupQuality());
            if ("4".equals(id)) { //$NON-NLS-1$
                isOutput4 = true;
                if (updated4GID.equals(s)) {
                    isUpdated4GID = true;
                }

            }
        }
        Assert.assertTrue(isOutput4);
        Assert.assertTrue(isUpdated4GID);
    }

    private SurvivorShipAlgorithmParams createSurvivoshipAlgorithm(CombinedRecordMatcher combinMatcher,
            MFBRecordMatcher mfbRecordMatcher) {
        SurvivorShipAlgorithmParams survivorParams = new SurvivorShipAlgorithmParams();
        // init SurvivorShipAlgorithmParams
        Map<Integer, SurvivorshipFunction> defaultSSRuleMap = new HashMap<>();
        survivorParams.setDefaultSurviorshipRules(defaultSSRuleMap);
        survivorParams.setRecordMatcher(combinMatcher);
        SurvivorShipAlgorithmParams.SurvivorshipFunction ssFunction = survivorParams.new SurvivorshipFunction();
        ssFunction.setSurvivorShipKey("name"); //$NON-NLS-1$
        ssFunction.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.CONCATENATE);
        SurvivorshipFunction[] SSAlgos = new SurvivorshipFunction[] { ssFunction };
        survivorParams.setSurviorShipAlgos(SSAlgos);
        Map<IRecordMatcher, SurvivorshipFunction[]> SSAlgosMap = new HashMap<>();
        SSAlgosMap.put(mfbRecordMatcher, SSAlgos);
        survivorParams.setSurvivorshipAlgosMap(SSAlgosMap);
        return survivorParams;
    }

    private MFBRecordMatcher createMFBMatcher() {
        MFBRecordMatcher mfbRecordMatcher = new MFBRecordMatcher(0.8);
        mfbRecordMatcher.setAttributeWeights(new double[] { 1.0 });
        mfbRecordMatcher.setRecordMatchThreshold(0.8);
        mfbRecordMatcher.setRecordSize(1);
        LevenshteinMatcher levenshteinMatcher = new LevenshteinMatcher();
        levenshteinMatcher.setAttributeName("name"); //$NON-NLS-1$
        MFBAttributeMatcher mfbattMatcher = MFBAttributeMatcher.wrap(levenshteinMatcher, 1.0, 0.8, new SubString(-1, 0));
        mfbRecordMatcher.setAttributeMatchers(new IAttributeMatcher[] { mfbattMatcher });
        return mfbRecordMatcher;
    }

    private List<List<Map<String, String>>> createMatchRules() {
        Map<String, String> matchRuleMap = new HashMap<>();
        matchRuleMap.put("MATCHING_TYPE", "Levenshtein"); //$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("RECORD_MATCH_THRESHOLD", "0.85");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("ATTRIBUTE_NAME", "name");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("SURVIVORSHIP_FUNCTION", "Concatenate");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("CONFIDENCE_WEIGHT", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("HANDLE_NULL", "nullMatchNull");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("ATTRIBUTE_THRESHOLD", "0.8");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("COLUMN_IDX", "1");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("PARAMETER", ",");//$NON-NLS-1$ //$NON-NLS-2$
        List<Map<String, String>> ruleMapList = new ArrayList<>();
        List<List<Map<String, String>>> ruleMapListList = new ArrayList<>();
        ruleMapList.add(matchRuleMap);
        ruleMapListList.add(ruleMapList);
        return ruleMapListList;
    }

    class TempRecordGrouping extends AbstractRecordGrouping<String> {

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(java.lang.Object[])
         */
        @Override
        protected void outputRow(String[] row) {
            // result.add(row);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(org.talend.dataquality.record
         * .linkage.grouping.swoosh.RichRecord)
         */
        @Override
        protected void outputRow(RichRecord row) {
            result.add(row);

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#isMaster(java.lang.Object)
         */
        @Override
        protected boolean isMaster(String col) {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#incrementGroupSize(java.lang.Object)
         */
        @Override
        protected String incrementGroupSize(String oldGroupSize) {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#createTYPEArray(int)
         */
        @Override
        protected String[] createTYPEArray(int size) {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#castAsType(java.lang.Object)
         */
        @Override
        protected String castAsType(Object objectValue) {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
