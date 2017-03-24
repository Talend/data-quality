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
package org.talend.dataquality.record.linkage.grouping.callback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.matchmerge.mfb.MatchResult;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SwooshConstants;
import org.talend.dataquality.record.linkage.utils.BidiMultiMap;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class MultiPassGroupingCallBackTest {

    private Map<String, List<List<DQAttribute<?>>>> groupRows = null;

    private BidiMultiMap<String, String> oldGID2New = null;

    private List<RichRecord> listResult = null;

    private AbstractRecordGrouping<Object> recordGrouping = null;

    /**
     * DOC zshen Comment method "setUpBeforeClass".
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * DOC zshen Comment method "tearDownAfterClass".
     * 
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * DOC zshen Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        oldGID2New = new BidiMultiMap();
        listResult = new ArrayList<RichRecord>();
        groupRows = new HashMap<String, List<List<DQAttribute<?>>>>();
        recordGrouping = new AbstractRecordGrouping<Object>() {

            @Override
            protected void outputRow(Object[] row) {
                // TODO Auto-generated method stub

            }

            @Override
            protected void outputRow(RichRecord row) {
                listResult.add(row);

            }

            @Override
            protected boolean isMaster(Object col) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            protected Object incrementGroupSize(Object oldGroupSize) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected Object[] createTYPEArray(int size) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected Object castAsType(Object objectValue) {
                // TODO Auto-generated method stub
                return null;
            }

        };
    }

    /**
     * DOC zshen Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.callback.MultiPassGroupingCallBack#onMatch(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.mfb.MatchResult)}
     * .
     */
    @Test
    public void testOnMatch() {
        MultiPassGroupingCallBack<Object> mPGroupingCallBack = new MultiPassGroupingCallBack<>(oldGID2New, recordGrouping,
                groupRows);
        RichRecord record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        RichRecord record2 = new RichRecord("id2", new Date().getTime(), "source2"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setScore(0.1);
        record2.setScore(0.2);
        MatchResult matchResult = new MatchResult(1);

        // matchResult is not match case
        matchResult.setScore(2, AttributeMatcherType.EXACT, 0.5, "id1", "value1", "id2", "value2");
        matchResult.setThreshold(2, 0.6f);
        mPGroupingCallBack.onMatch(record1, record2, matchResult);
        Assert.assertEquals("The size of listResult should be 0", 0, listResult.size());
        Assert.assertEquals("The Confidence of record1 should be 1.0", "1.0", "" + record1.getConfidence());
        Assert.assertEquals("The Confidence of record1 should be 0.1", "0.1", "" + record1.getScore());
        Assert.assertEquals("The Confidence of record2 should be 1.0", "1.0", "" + record2.getConfidence());
        Assert.assertEquals("The Confidence of record1 should be 0.2", "0.2", "" + record2.getScore());

        // case1 id1==null id2==null
        // old quality 2>1 case
        // groupRows:old !=null new GID==null
        record1.setGroupId(null);
        record2.setGroupId(null);
        matchResult = new MatchResult(1);

        record1.setRecordSize(1);
        record2.setRecordSize(1);
        List<DQAttribute<?>> originRow1 = initOringinRow("GID1", 2, true, 0.21, "0.9");
        List<DQAttribute<?>> originRow2 = initOringinRow("GID2", 2, true, 0.22, "0.8");
        record1.setOriginRow(originRow1);
        record2.setOriginRow(originRow2);
        groupRows.put("GID2", new ArrayList<List<DQAttribute<?>>>());

        Assert.assertEquals("The size of groupRows should be 1", 1, groupRows.size());
        mPGroupingCallBack.onMatch(record1, record2, matchResult);
        Assert.assertEquals("The size of listResult should be 2", 2, listResult.size());
        Assert.assertEquals("The Confidence value of record1 should be 0.1", "0.1", "" + listResult.get(0).getConfidence());
        Assert.assertNotNull("The groupid of record1 should not be null", listResult.get(0).getGroupId());
        Assert.assertEquals("The group size of record1 should be 0", 0, listResult.get(0).getGrpSize());
        Assert.assertFalse("The master of record1 should be false", listResult.get(0).isMaster());
        Assert.assertEquals("The Confidence value of record2 should be 0.2", "0.2", "" + listResult.get(1).getConfidence());
        Assert.assertNotNull("The groupid of record2 should not be null", listResult.get(1).getGroupId());
        Assert.assertEquals("The group size of record2 should be 0", 0, listResult.get(1).getGrpSize());
        Assert.assertFalse("The master of record2 should be false", listResult.get(1).isMaster());
        Assert.assertEquals("The size of groupRows should be 1", 1, groupRows.size());
        Assert.assertEquals("The size of oldGID2New should be 1", 1, oldGID2New.size());
        Assert.assertEquals("The result of oldGID2New.get(\"GID2\") should be GID1", "GID1", oldGID2New.get("GID2"));
        Assert.assertNotNull("The GID1 attributes should not be null", groupRows.get("GID1"));

        // case1 id1=="id1" id2=="id2"
        // old quality 1>2 case
        // groupRows:old !=null new GID!=null
        listResult.clear();
        groupRows.clear();
        oldGID2New.clear();
        record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record2 = new RichRecord("id2", new Date().getTime(), "source2"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setGroupId("id1");
        record2.setGroupId("id2");
        record1.setScore(0.1);
        record2.setScore(0.2);
        matchResult = new MatchResult(1);

        record1.setRecordSize(1);
        record2.setRecordSize(1);
        originRow1 = initOringinRow("GID1", 2, true, 0.21, "0.8");
        originRow2 = initOringinRow("GID2", 2, true, 0.22, "0.9");
        record1.setOriginRow(originRow1);
        record2.setOriginRow(originRow2);
        oldGID2New.put("id3", "id2");
        oldGID2New.put("id4", "id2");
        oldGID2New.put("id5", "id2");
        oldGID2New.put("id6", "id2");
        groupRows.put("id1", new ArrayList<List<DQAttribute<?>>>());
        groupRows.put("id2", new ArrayList<List<DQAttribute<?>>>());

        Assert.assertEquals("The size of groupRows should be 2", 2, groupRows.size());
        mPGroupingCallBack.onMatch(record1, record2, matchResult);
        Assert.assertEquals("The size of listResult should be 0", 0, listResult.size());
        Assert.assertEquals("The Confidence value of record1 should be 0.1", "0.1", "" + record1.getConfidence());
        Assert.assertEquals("The groupid of record1 should be id1", "id1", record1.getGroupId());
        Assert.assertFalse("The master of record1 should be false", record1.isMaster());
        Assert.assertEquals("The Confidence value of record2 should be 0.2", "0.2", "" + record2.getConfidence());
        Assert.assertEquals("The groupid of record2 should be id1", "id1", record2.getGroupId());
        Assert.assertFalse("The master of record2 should be false", record2.isMaster());
        Assert.assertEquals("The size of oldGID2New should be 5", 5, oldGID2New.size());
        Assert.assertEquals("The size of mapping to id1 should be 5", 5, oldGID2New.getKeys("id1").size());
        Assert.assertEquals("The size of groupRows should be 1", 1, groupRows.size());
        Assert.assertNotNull("The id1 attributes should not be null", groupRows.get("id1"));

        // case1 id1==null id2=="id2"
        // old quality 1==2 case
        // groupRows:old ==null
        listResult.clear();
        groupRows.clear();
        oldGID2New.clear();
        record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record2 = new RichRecord("id2", new Date().getTime(), "source2"); //$NON-NLS-1$ //$NON-NLS-2$
        record2.setGroupId("id2");
        record1.setGroupId(null);
        record1.setGrpSize(1);
        record1.setMaster(true);
        record1.setScore(0.3);
        record2.setScore(0.3);
        mPGroupingCallBack.onMatch(record1, record2, matchResult);
        Assert.assertEquals("The size of listResult should be 1", 1, listResult.size());
        Assert.assertEquals("The Confidence value of record1 should be 0.3", "0.3", "" + listResult.get(0).getConfidence());
        Assert.assertEquals("The groupid of record1 should be id2", "id2", listResult.get(0).getGroupId());
        Assert.assertEquals("The group size of record1 should be 0", 0, listResult.get(0).getGrpSize());
        Assert.assertFalse("The master of record1 should be false", listResult.get(0).isMaster());

        // case1 id1=="id1" id2==null
        // old quality 1==2 case
        // groupRows:old ==null
        listResult.clear();
        groupRows.clear();
        oldGID2New.clear();
        record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record2 = new RichRecord("id2", new Date().getTime(), "source2"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setGroupId("id1");
        record2.setGroupId(null);
        record2.setGrpSize(1);
        record2.setMaster(true);
        record1.setScore(0.3);
        record2.setScore(0.3);
        mPGroupingCallBack.onMatch(record1, record2, matchResult);
        Assert.assertEquals("The size of listResult should be 1", 1, listResult.size());
        Assert.assertEquals("The Confidence value of record1 should be 0.3", "0.3", "" + listResult.get(0).getConfidence());
        Assert.assertEquals("The groupid of record2 should be id1", "id1", listResult.get(0).getGroupId());
        Assert.assertEquals("The group size of record2 should be 0", 0, listResult.get(0).getGrpSize());
        Assert.assertFalse("The master of record2 should be false", listResult.get(0).isMaster());
    }

    /**
     * DOC zshen Comment method "initOringinRow".
     * 
     * @return
     */
    private List<DQAttribute<?>> initOringinRow(String GID, Integer GS, Boolean isMaster, Double score2, String GQ) {
        List<DQAttribute<?>> returnList = new ArrayList<>();
        returnList.add(new DQAttribute<>("OtherData", 0, "take in string"));
        // SwooshConstants.GID
        returnList.add(new DQAttribute<>(SwooshConstants.GID, 1, GID));
        // SwooshConstants.GROUP_SIZE
        returnList.add(new DQAttribute<>(SwooshConstants.GROUP_SIZE, 2, GS));

        // SwooshConstants.IS_MASTER
        returnList.add(new DQAttribute<>(SwooshConstants.IS_MASTER, 3, isMaster));

        // SwooshConstants.SCORE2
        returnList.add(new DQAttribute<>(SwooshConstants.SCORE2, 4, score2));

        // SwooshConstants.GROUP_QUALITY
        returnList.add(new DQAttribute<>(SwooshConstants.GROUP_QUALITY, 5, GQ));
        return returnList;
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.callback.MultiPassGroupingCallBack#onNewMerge(org.talend.dataquality.matchmerge.Record)}
     * .
     */
    @Test
    public void testOnNewMerge() {
        MultiPassGroupingCallBack<Object> mPGroupingCallBack = new MultiPassGroupingCallBack<>(oldGID2New, recordGrouping,
                groupRows);
        // groupid !=null groupQuality==0.0 OldGrpQualiry>newGrpQualiry
        RichRecord record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setGroupId("group1");
        record1.setMaster(false);
        record1.setScore(2.0);
        record1.setMerged(false);
        record1.getRelatedIds().add("id2");
        record1.getRelatedIds().add("id3");
        record1.setGroupQuality(0.0);
        record1.setConfidence(0.5);
        record1.setGRP_QUALITY(new DQAttribute<>(SwooshConstants.GROUP_QUALITY, 5, "0.6"));// >0.5

        mPGroupingCallBack.onNewMerge(record1);

        Assert.assertEquals("The master of record1 should be true", true, record1.isMaster());
        Assert.assertEquals("The score of record1 should be 1.0", "1.0", "" + record1.getScore());
        Assert.assertEquals("The merged of record1 should be true", true, record1.isMerged());
        Assert.assertEquals("The GrpSize of record1 should be 2", 2, record1.getGrpSize());
        Assert.assertEquals("The GroupQuality of record1 should be 0.5", "0.5", "" + record1.getGroupQuality());

        // groupid !=null groupQuality==0.0 OldGrpQualiry<newGrpQualiry
        record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setGroupId("group1");
        record1.setMaster(false);
        record1.setScore(2.0);
        record1.setMerged(false);
        record1.getRelatedIds().add("id2");
        record1.getRelatedIds().add("id3");
        record1.setGroupQuality(0.0);
        record1.setConfidence(0.5);
        record1.setGRP_QUALITY(new DQAttribute<>(SwooshConstants.GROUP_QUALITY, 5, "0.4"));// <0.5
        mPGroupingCallBack.onNewMerge(record1);

        Assert.assertEquals("The master of record1 should be true", true, record1.isMaster());
        Assert.assertEquals("The score of record1 should be 1.0", "1.0", "" + record1.getScore());
        Assert.assertEquals("The merged of record1 should be true", true, record1.isMerged());
        Assert.assertEquals("The GrpSize of record1 should be 2", 2, record1.getGrpSize());
        Assert.assertEquals("The GroupQuality of record1 should be 0.4", "0.4", "" + record1.getGroupQuality());

        // groupid !=null groupQuality==0.0 OldGrpQualiry==newGrpQualiry
        record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setGroupId("group1");
        record1.setMaster(false);
        record1.setScore(2.0);
        record1.setMerged(false);
        record1.getRelatedIds().add("id2");
        record1.getRelatedIds().add("id3");
        record1.setGroupQuality(0.0);
        record1.setConfidence(0.5);
        record1.setGRP_QUALITY(new DQAttribute<>(SwooshConstants.GROUP_QUALITY, 5, "0.5"));// ==0.5
        mPGroupingCallBack.onNewMerge(record1);

        Assert.assertEquals("The master of record1 should be true", true, record1.isMaster());
        Assert.assertEquals("The score of record1 should be 1.0", "1.0", "" + record1.getScore());
        Assert.assertEquals("The merged of record1 should be true", true, record1.isMerged());
        Assert.assertEquals("The GrpSize of record1 should be 2", 2, record1.getGrpSize());
        Assert.assertEquals("The GroupQuality of record1 should be 0.5", "0.5", "" + record1.getGroupQuality());

        // groupid ==null
        record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setMaster(false);
        record1.setScore(2.0);
        record1.setMerged(false);
        record1.getRelatedIds().add("id2");
        record1.getRelatedIds().add("id3");
        record1.setGroupQuality(0.0);
        record1.setConfidence(0.5);
        mPGroupingCallBack.onNewMerge(record1);

        Assert.assertEquals("The master of record1 should be true", true, record1.isMaster());
        Assert.assertEquals("The score of record1 should be 1.0", "1.0", "" + record1.getScore());
        Assert.assertEquals("The merged of record1 should be false", false, record1.isMerged());
        Assert.assertEquals("The GrpSize of record1 should be 0", 0, record1.getGrpSize());
        Assert.assertEquals("The GroupQuality of record1 should be 0.0", "0.0", "" + record1.getGroupQuality());

        // groupid !=null groupQuality!=0.0
        record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setGroupId("group1");
        record1.setMaster(false);
        record1.setScore(2.0);
        record1.setMerged(false);
        record1.getRelatedIds().add("id2");
        record1.getRelatedIds().add("id3");
        record1.setGroupQuality(0.1);
        record1.setConfidence(0.5);
        mPGroupingCallBack.onNewMerge(record1);

        Assert.assertEquals("The master of record1 should be true", true, record1.isMaster());
        Assert.assertEquals("The score of record1 should be 1.0", "1.0", "" + record1.getScore());
        Assert.assertEquals("The merged of record1 should be true", true, record1.isMerged());
        Assert.assertEquals("The GrpSize of record1 should be 2", 2, record1.getGrpSize());
        Assert.assertEquals("The GroupQuality of record1 should be 0.1", "0.1", "" + record1.getGroupQuality());
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.callback.MultiPassGroupingCallBack#onRemoveMerge(org.talend.dataquality.matchmerge.Record)}
     * .
     */
    @Test
    public void testOnRemoveMerge() {
        MultiPassGroupingCallBack<Object> mPGroupingCallBack = new MultiPassGroupingCallBack<>(oldGID2New, recordGrouping,
                groupRows);

        // isMerged==true
        RichRecord record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setOriginRow(new ArrayList<DQAttribute<?>>());
        record1.setGroupQuality(1.0);
        record1.setMerged(true);
        record1.setMaster(true);
        mPGroupingCallBack.onRemoveMerge(record1);
        Assert.assertNotNull("The OriginRow of record1 should not be null", record1.getOriginRow());
        Assert.assertEquals("The GroupQuality of record1 should be 0.0", "0.0", "" + record1.getGroupQuality());
        Assert.assertEquals("The GroupQuality of record1 should be false", false, record1.isMaster());
        Assert.assertEquals("The GroupQuality of record1 should be false", false, record1.isMerged());

        // isMerged==false
        record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setOriginRow(new ArrayList<DQAttribute<?>>());
        record1.setGroupQuality(1.0);
        record1.setMerged(false);
        record1.setMaster(true);
        mPGroupingCallBack.onRemoveMerge(record1);
        Assert.assertNotNull("The OriginRow of record1 should not be null", record1.getOriginRow());
        Assert.assertEquals("The GroupQuality of record1 should be 0.0", "1.0", "" + record1.getGroupQuality());
        Assert.assertEquals("The GroupQuality of record1 should be false", false, record1.isMaster());
        Assert.assertEquals("The GroupQuality of record1 should be false", false, record1.isMerged());
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.callback.MultiPassGroupingCallBack#onDifferent(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.mfb.MatchResult)}
     * .
     */
    @Test
    public void testOnDifferent() {
        MultiPassGroupingCallBack<Object> mPGroupingCallBack = new MultiPassGroupingCallBack<>(oldGID2New, recordGrouping,
                groupRows);
        RichRecord record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        RichRecord record2 = new RichRecord("id2", new Date().getTime(), "source2"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setScore(0.1);
        record2.setScore(0.2);
        record2.setMaster(false);
        MatchResult matchResult = new MatchResult(1);

        record1.setGroupId(null);
        record2.setGroupId(null);
        mPGroupingCallBack.onDifferent(record1, record2, matchResult);
        Assert.assertEquals("The master of record2 should be true", true, record2.isMaster());
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.grouping.callback.MultiPassGroupingCallBack#getIndexGID()}.
     */
    @Test
    public void testSetGetTest() {
        MultiPassGroupingCallBack<Object> mPGroupingCallBack = new MultiPassGroupingCallBack<>(oldGID2New, recordGrouping,
                groupRows);
        Assert.assertEquals("Default IndexGID should be 0", 0, mPGroupingCallBack.getIndexGID());
        Assert.assertEquals("Default IndexGQ should be 4", 4, mPGroupingCallBack.getIndexGQ());
        // setGIDindex
        mPGroupingCallBack.setGIDindex(1);
        // getIndexGID
        Assert.assertEquals("After set IndexGID should be 1", 1, mPGroupingCallBack.getIndexGID());
        // getIndexGQ
        mPGroupingCallBack.getIndexGQ();
        Assert.assertEquals("After set IndexGQ should be 5", 5, mPGroupingCallBack.getIndexGQ());
    }

}
