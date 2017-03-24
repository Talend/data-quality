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
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.matchmerge.mfb.MatchResult;
import org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.utils.BidiMultiMap;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class GroupingCallBackTest {

    BidiMultiMap<String, String> oldGID2New = null;

    List<RichRecord> listResult = null;

    AbstractRecordGrouping<Object> recordGrouping = null;

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
     * {@link org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBack#onMatch(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.mfb.MatchResult)}
     * .
     */
    @Test
    public void testOnMatch() {

        GroupingCallBack<Object> groupingCallBack = new GroupingCallBack<>(oldGID2New, recordGrouping);
        RichRecord record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        RichRecord record2 = new RichRecord("id2", new Date().getTime(), "source2"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setScore(0.1);
        record2.setScore(0.2);
        MatchResult matchResult = new MatchResult(1);

        // case1 id1==null id2==null
        record1.setGroupId(null);
        record2.setGroupId(null);

        groupingCallBack.onMatch(record1, record2, matchResult);
        Assert.assertEquals("The size of listResult should be 2", 2, listResult.size());
        Assert.assertEquals("The Confidence value of record1 should be 0.1", "0.1", "" + listResult.get(0).getConfidence());
        Assert.assertNotNull("The groupid of record1 should not be null", listResult.get(0).getGroupId());
        Assert.assertEquals("The group size of record1 should be 0", 0, listResult.get(0).getGrpSize());
        Assert.assertFalse("The master of record1 should be false", listResult.get(0).isMaster());
        Assert.assertEquals("\"The Confidence value of record2 should be 0.2", "0.2", "" + listResult.get(1).getConfidence());
        Assert.assertNotNull("The groupid of record2 should not be null", listResult.get(1).getGroupId());
        Assert.assertEquals("The group size of record2 should be 0", 0, listResult.get(1).getGrpSize());
        Assert.assertFalse("The master of record2 should be false", listResult.get(1).isMaster());

        // case1 id1=="id1" id2=="id2"
        listResult.clear();
        record1.setGroupId("id1");
        record2.setGroupId("id2");
        oldGID2New.put("id3", "id2");
        oldGID2New.put("id4", "id2");
        oldGID2New.put("id5", "id2");
        oldGID2New.put("id6", "id2");
        groupingCallBack.onMatch(record1, record2, matchResult);
        Assert.assertEquals("The size of listResult should be 0", 0, listResult.size());
        Assert.assertEquals("The Confidence value of record1 should be 0.1", "0.1", "" + record1.getConfidence());
        Assert.assertEquals("The groupid of record1 should be id1", "id1", record1.getGroupId());
        Assert.assertFalse("The master of record1 should be false", record1.isMaster());
        Assert.assertEquals("The Confidence value of record2 should be 0.2", "0.2", "" + record2.getConfidence());
        Assert.assertEquals("The groupid of record2 should be id1", "id1", record2.getGroupId());
        Assert.assertFalse("The master of record2 should be false", record2.isMaster());
        Assert.assertEquals("The size of oldGID2New should be 5", 5, oldGID2New.size());
        Assert.assertEquals("The size of mapping to id1 should be 5", 5, oldGID2New.getKeys("id1").size());

        // case1 id1==null id2=="id2"
        listResult.clear();
        oldGID2New.clear();
        record1.setGroupId(null);
        record1.setGrpSize(1);
        record1.setMaster(true);
        record2.setGroupId("id2");
        groupingCallBack.onMatch(record1, record2, matchResult);
        Assert.assertEquals("The size of listResult should be 1", 1, listResult.size());
        Assert.assertEquals("The Confidence value of record1 should be 0.1", "0.1", "" + listResult.get(0).getConfidence());
        Assert.assertEquals("The groupid of record1 should be id2", "id2", listResult.get(0).getGroupId());
        Assert.assertEquals("The group size of record1 should be 0", 0, listResult.get(0).getGrpSize());
        Assert.assertFalse("The master of record1 should be false", listResult.get(0).isMaster());

        // case1 id1=="id1" id2==null
        listResult.clear();
        oldGID2New.clear();
        record1.setGroupId("id1");
        record2.setGroupId(null);
        record2.setGrpSize(1);
        record2.setMaster(true);
        groupingCallBack.onMatch(record1, record2, matchResult);
        Assert.assertEquals("The size of listResult should be 1", 1, listResult.size());
        Assert.assertEquals("The Confidence value of record1 should be 0.2", "0.2", "" + listResult.get(0).getConfidence());
        Assert.assertEquals("The groupid of record2 should be id1", "id1", listResult.get(0).getGroupId());
        Assert.assertEquals("The group size of record2 should be 0", 0, listResult.get(0).getGrpSize());
        Assert.assertFalse("The master of record2 should be false", listResult.get(0).isMaster());
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBack#onNewMerge(org.talend.dataquality.matchmerge.Record)}
     * .
     */
    @Test
    public void testOnNewMerge() {
        GroupingCallBack<Object> groupingCallBack = new GroupingCallBack<>(oldGID2New, recordGrouping);
        // groupid !=null groupQuality==0.0
        RichRecord record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setGroupId("group1");
        record1.setMaster(false);
        record1.setScore(2.0);
        record1.setMerged(false);
        record1.getRelatedIds().add("id2");
        record1.getRelatedIds().add("id3");
        record1.setGroupQuality(0.0);
        record1.setConfidence(0.5);
        groupingCallBack.onNewMerge(record1);

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
        groupingCallBack.onNewMerge(record1);

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
        groupingCallBack.onNewMerge(record1);

        Assert.assertEquals("The master of record1 should be true", true, record1.isMaster());
        Assert.assertEquals("The score of record1 should be 1.0", "1.0", "" + record1.getScore());
        Assert.assertEquals("The merged of record1 should be true", true, record1.isMerged());
        Assert.assertEquals("The GrpSize of record1 should be 2", 2, record1.getGrpSize());
        Assert.assertEquals("The GroupQuality of record1 should be 0.1", "0.1", "" + record1.getGroupQuality());
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBack#onRemoveMerge(org.talend.dataquality.matchmerge.Record)}
     * .
     */
    @Test
    public void testOnRemoveMerge() {
        GroupingCallBack<Object> groupingCallBack = new GroupingCallBack<>(oldGID2New, recordGrouping);

        // isMerged==true
        RichRecord record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setOriginRow(new ArrayList<DQAttribute<?>>());
        record1.setGroupQuality(1.0);
        record1.setMerged(true);
        record1.setMaster(true);
        groupingCallBack.onRemoveMerge(record1);
        Assert.assertNull("The OriginRow of record1 should be null", record1.getOriginRow());
        Assert.assertEquals("The GroupQuality of record1 should be 0.0", "0.0", "" + record1.getGroupQuality());
        Assert.assertEquals("The GroupQuality of record1 should be false", false, record1.isMaster());
        Assert.assertEquals("The GroupQuality of record1 should be false", false, record1.isMerged());

        // isMerged==false
        record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setOriginRow(new ArrayList<DQAttribute<?>>());
        record1.setGroupQuality(1.0);
        record1.setMerged(false);
        record1.setMaster(true);
        groupingCallBack.onRemoveMerge(record1);
        Assert.assertNotNull("The OriginRow of record1 should not be null", record1.getOriginRow());
        Assert.assertEquals("The GroupQuality of record1 should be 0.0", "1.0", "" + record1.getGroupQuality());
        Assert.assertEquals("The GroupQuality of record1 should be false", false, record1.isMaster());
        Assert.assertEquals("The GroupQuality of record1 should be false", false, record1.isMerged());
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBack#onDifferent(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.mfb.MatchResult)}
     * .
     */
    @Test
    public void testOnDifferent() {
        GroupingCallBack<Object> groupingCallBack = new GroupingCallBack<>(oldGID2New, recordGrouping);
        RichRecord record1 = new RichRecord("id1", new Date().getTime(), "source1"); //$NON-NLS-1$ //$NON-NLS-2$
        RichRecord record2 = new RichRecord("id2", new Date().getTime(), "source2"); //$NON-NLS-1$ //$NON-NLS-2$
        record1.setScore(0.1);
        record2.setScore(0.2);
        record2.setMaster(false);
        MatchResult matchResult = new MatchResult(1);

        record1.setGroupId(null);
        record2.setGroupId(null);
        groupingCallBack.onDifferent(record1, record2, matchResult);
        Assert.assertEquals("The master of record2 should be true", true, record2.isMaster());
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.grouping.callback.GroupingCallBack#isInterrupted()}.
     */
    @Test
    public void testIsInterrupted() {
        GroupingCallBack<Object> groupingCallBack = new GroupingCallBack<>(oldGID2New, recordGrouping);
        Assert.assertEquals("isInterrupted alaways be false", false, groupingCallBack.isInterrupted());
    }

}
