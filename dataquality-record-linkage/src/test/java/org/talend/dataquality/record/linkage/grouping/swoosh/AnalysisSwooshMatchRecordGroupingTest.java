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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class AnalysisSwooshMatchRecordGroupingTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping#outputRow(org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord)}
     * .
     */
    @Test
    public void testOutputRow() {
        // current row is master case and label of row is Group quality
        double groupQuality = 0.6;
        List<DQAttribute<?>> originRow = new ArrayList<>();
        originRow.add(new DQAttribute<>(SwooshConstants.GROUP_QUALITY, 0, "1.0")); //$NON-NLS-1$
        RichRecord richRecord = new RichRecord("1", 0l, "a"); //$NON-NLS-1$ //$NON-NLS-2$
        JunitResultConsumer resultConsumer = new JunitResultConsumer();
        AnalysisSwooshMatchRecordGrouping analysisSwooshMatchRecordGrouping =
                new AnalysisSwooshMatchRecordGrouping(resultConsumer);
        richRecord.setRecordSize(1);
        richRecord.setOriginRow(originRow);
        richRecord.setGrpSize(1);
        richRecord.setMaster(true);
        richRecord.setGroupQuality(groupQuality);
        analysisSwooshMatchRecordGrouping.outputRow(richRecord);
        Object[] result = resultConsumer.getResult();
        Assert.assertTrue("can not get correct result", result.length > 0); //$NON-NLS-1$
        Assert.assertEquals("1.0", result[0]);

        // current row is not master case but the group size is 1 and label of row is Group quality
        richRecord = new RichRecord("1", 0l, "a"); //$NON-NLS-1$ //$NON-NLS-2$
        richRecord.setRecordSize(1);
        originRow = new ArrayList<>();
        originRow.add(new DQAttribute<>(SwooshConstants.GROUP_QUALITY, 0, "1.0")); //$NON-NLS-1$
        richRecord.setOriginRow(originRow);
        richRecord.setGrpSize(1);
        richRecord.setMaster(false);
        richRecord.setGroupQuality(groupQuality);
        analysisSwooshMatchRecordGrouping.outputRow(richRecord);
        result = resultConsumer.getResult();
        Assert.assertTrue("can not get correct result", result.length > 0); //$NON-NLS-1$
        Assert.assertEquals(StringUtils.EMPTY, result[5]);

        // current row is not master case but the group size is 0 and label of row is Group quality
        richRecord.setGrpSize(0);
        analysisSwooshMatchRecordGrouping.outputRow(richRecord);
        result = resultConsumer.getResult();
        Assert.assertTrue("can not get correct result", result.length > 0); //$NON-NLS-1$
        //        Assert.assertEquals(SwooshConstants.SUB_ITEM_GROUP_QUALITY_DEFAULT_VALUE, result[0]);

        // current row is not master case but the group size is 2 and label of row is Group quality
        richRecord.setGrpSize(2);
        analysisSwooshMatchRecordGrouping.outputRow(richRecord);
        result = resultConsumer.getResult();
        Assert.assertTrue("can not get correct result", result.length > 0); //$NON-NLS-1$
        //        Assert.assertEquals(SwooshConstants.SUB_ITEM_GROUP_QUALITY_DEFAULT_VALUE, result[0]);

        // current row is master and merged case but label of row is not Group quality
        originRow.clear();
        //        DQAttribute<String> dqAttribute1 = new DQAttribute<>(SwooshConstants.GID, 0, "group1"); //$NON-NLS-1$
        //        dqAttribute1.setValue("group2"); //$NON-NLS-1$
        //        originRow.add(dqAttribute1);
        //        // group size
        //        DQAttribute<Integer> dqAttribute2 = new DQAttribute<>(SwooshConstants.GROUP_SIZE, 1, 6);
        //        dqAttribute2.setValue("5"); //$NON-NLS-1$
        //        originRow.add(dqAttribute2);
        //        // is master
        //        DQAttribute<Boolean> dqAttribute3 = new DQAttribute<>(SwooshConstants.IS_MASTER, 2, true);
        //        dqAttribute3.setValue("false"); //$NON-NLS-1$
        //        originRow.add(dqAttribute3);
        //        // Score
        //        DQAttribute<Double> dqAttribute4 = new DQAttribute<>(SwooshConstants.SCORE2, 3, 1.0);
        //        dqAttribute4.setValue("0.0"); //$NON-NLS-1$
        //        originRow.add(dqAttribute4);
        //        // group quality
        //        DQAttribute<Double> dqAttribute5 = new DQAttribute<>(SwooshConstants.GROUP_QUALITY, 4, 1.0);
        //        dqAttribute5.setValue("0.0"); //$NON-NLS-1$
        //        originRow.add(dqAttribute5);
        //        // attribute scores (distance details).
        //        DQAttribute<String> dqAttribute6 = new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, 5, StringUtils.EMPTY);
        //        dqAttribute6.setValue("0.0"); //$NON-NLS-1$
        //        originRow.add(dqAttribute6);

        richRecord.setMaster(true);
        richRecord.setMerged(true);
        richRecord.setGroupId("group1"); //$NON-NLS-1$
        analysisSwooshMatchRecordGrouping.outputRow(richRecord);
        result = resultConsumer.getResult();
        Assert.assertTrue("can not get correct result", result.length == 6); //$NON-NLS-1$
        Assert.assertEquals("group1", result[0]); //$NON-NLS-1$
        Assert.assertEquals("2", result[1]); //$NON-NLS-1$
        Assert.assertEquals("true", result[2]); //$NON-NLS-1$
        Assert.assertEquals("1.0", result[3]); //$NON-NLS-1$
        Assert.assertEquals(String.valueOf(groupQuality), result[4]);
        Assert.assertEquals(StringUtils.EMPTY, result[5]);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping#outputRow(org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord)}
     * .
     */
    @Test
    public void testOutputRowForNotMasterOrNotMergedCase() {
        // current row is not master case and OriginalValue is null
        double groupQuality = 0.6;
        List<DQAttribute<?>> originRow = new ArrayList<>();
        DQAttribute<?> dqAttribute2 = new DQAttribute<>(StringUtils.EMPTY, 0, null);
        dqAttribute2.setValue("1.0"); //$NON-NLS-1$
        originRow.add(dqAttribute2);
        RichRecord richRecord = new RichRecord("1", 0l, "a"); //$NON-NLS-1$ //$NON-NLS-2$
        richRecord.setOriginRow(originRow);
        richRecord.setMaster(false);
        richRecord.setGroupQuality(groupQuality);
        JunitResultConsumer resultConsumer = new JunitResultConsumer();
        AnalysisSwooshMatchRecordGrouping analysisSwooshMatchRecordGrouping =
                new AnalysisSwooshMatchRecordGrouping(resultConsumer);
        analysisSwooshMatchRecordGrouping.outputRow(richRecord);
        Object[] result = resultConsumer.getResult();
        Assert.assertTrue("can not get correct result", result.length > 0); //$NON-NLS-1$
        Assert.assertEquals("1.0", result[0]); //$NON-NLS-1$

        // current row is not master case and OriginalValue is not null
        originRow.clear();
        dqAttribute2 = new DQAttribute<>(StringUtils.EMPTY, 0, "1.0"); //$NON-NLS-1$
        dqAttribute2.setValue("2.0"); //$NON-NLS-1$
        originRow.add(dqAttribute2);
        analysisSwooshMatchRecordGrouping.outputRow(richRecord);
        result = resultConsumer.getResult();
        Assert.assertTrue("can not get correct result", result.length > 0); //$NON-NLS-1$
        Assert.assertEquals("2.0", result[0]); //$NON-NLS-1$

        // current row is not master case and OriginalValue is not null
        richRecord.setMaster(true);
        richRecord.setMerged(false);
        originRow.clear();
        dqAttribute2 = new DQAttribute<>(StringUtils.EMPTY, 0, null);
        dqAttribute2.setValue("1.0"); //$NON-NLS-1$
        originRow.add(dqAttribute2);
        analysisSwooshMatchRecordGrouping.outputRow(richRecord);
        result = resultConsumer.getResult();
        Assert.assertTrue("can not get correct result", result.length > 0); //$NON-NLS-1$
        Assert.assertEquals("1.0", result[0]); //$NON-NLS-1$

        // current row is not master case and OriginalValue is not null
        originRow.clear();
        dqAttribute2 = new DQAttribute<>(StringUtils.EMPTY, 0, "1.0"); //$NON-NLS-1$
        dqAttribute2.setValue("2.0"); //$NON-NLS-1$
        originRow.add(dqAttribute2);
        analysisSwooshMatchRecordGrouping.outputRow(richRecord);
        result = resultConsumer.getResult();
        Assert.assertTrue("can not get correct result", result.length > 0); //$NON-NLS-1$
        Assert.assertEquals("2.0", result[0]); //$NON-NLS-1$

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
