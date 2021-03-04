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

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.record.linkage.attribute.ExactMatcher;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.attribute.JaroMatcher;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class MFBRecordMatcherTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMatcher#getMatchingWeight(java.lang.String[], java.lang.String[])}
     * .
     */
    @Test
    public void testGetMatchingWeightStringArrayStringArray() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMatcher#getMatchingWeight(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     */
    @Test
    public void testGetMatchingWeightRecordRecordForSameInputAttributeThreshold0_8() {
        // init record
        Record record1 = new Record(null, 0, StringUtils.EMPTY);
        Attribute attribute = new Attribute("0"); //$NON-NLS-1$
        attribute.setValue("jinan"); //$NON-NLS-1$
        record1.getAttributes().add(attribute);
        Record record2 = new Record(null, 0, StringUtils.EMPTY);
        attribute = new Attribute("1"); //$NON-NLS-1$
        attribute.setValue("jinan"); //$NON-NLS-1$
        record2.getAttributes().add(attribute);

        // init Attribute matcher
        IAttributeMatcher[] attributeMatchers = new IAttributeMatcher[] {
                MFBAttributeMatcher.wrap(new JaroMatcher(), 1.0, 0.8, SubString.NO_SUBSTRING) };

        MFBRecordMatcher mfbRecordMatcher = new MFBRecordMatcher(0.85d);
        mfbRecordMatcher.setRecordSize(1);
        Assert.assertTrue("setAttributeMatchers fail", mfbRecordMatcher.setAttributeMatchers(attributeMatchers)); //$NON-NLS-1$
        MatchResult matchingResult = mfbRecordMatcher.getMatchingWeight(record1, record2);
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getScores().get(0).score)); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMatcher#getMatchingWeight(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     */
    @Test
    public void testGetMatchingWeightRecordRecordForDifferentInputAttributeThreshold0_8() {
        // init record
        Record record1 = new Record(null, 0, StringUtils.EMPTY);
        Attribute attribute = new Attribute("0"); //$NON-NLS-1$
        // add master value
        attribute.setValue("jinanjinanjinanjinan"); //$NON-NLS-1$
        record1.getAttributes().add(attribute);
        Record record2 = new Record(null, 0, StringUtils.EMPTY);
        attribute = new Attribute("1"); //$NON-NLS-1$
        // add master value
        attribute.setValue("jinanjinan"); //$NON-NLS-1$
        record2.getAttributes().add(attribute);

        // init Attribute matcher
        IAttributeMatcher[] attributeMatchers = new IAttributeMatcher[] {
                MFBAttributeMatcher.wrap(new JaroMatcher(), 1.0, 0.8, SubString.NO_SUBSTRING) };

        MFBRecordMatcher mfbRecordMatcher = new MFBRecordMatcher(0.85d);
        mfbRecordMatcher.setRecordSize(1);
        Assert.assertTrue("setAttributeMatchers fail", mfbRecordMatcher.setAttributeMatchers(attributeMatchers)); //$NON-NLS-1$
        MatchResult matchingResult = mfbRecordMatcher.getMatchingWeight(record1, record2);
        Assert.assertEquals("0.8333333134651184", String.valueOf(matchingResult.getScores().get(0).score)); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMatcher#getMatchingWeight(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     */
    @Test
    public void testGetMatchingWeightRecordRecordForSameInputAttributeThreshold1_0() {
        // init record
        Record record1 = new Record(null, 0, StringUtils.EMPTY);
        Attribute attribute = new Attribute("0"); //$NON-NLS-1$
        attribute.setValue("jinan"); //$NON-NLS-1$
        record1.getAttributes().add(attribute);
        Record record2 = new Record(null, 0, StringUtils.EMPTY);
        attribute = new Attribute("1"); //$NON-NLS-1$
        attribute.setValue("jinan"); //$NON-NLS-1$
        record2.getAttributes().add(attribute);

        // init Attribute matcher
        IAttributeMatcher[] attributeMatchers = new IAttributeMatcher[] {
                MFBAttributeMatcher.wrap(new JaroMatcher(), 1.0, 1.0, SubString.NO_SUBSTRING) };

        MFBRecordMatcher mfbRecordMatcher = new MFBRecordMatcher(0.85d);
        mfbRecordMatcher.setRecordSize(1);
        Assert.assertTrue("setAttributeMatchers fail", mfbRecordMatcher.setAttributeMatchers(attributeMatchers)); //$NON-NLS-1$
        MatchResult matchingResult = mfbRecordMatcher.getMatchingWeight(record1, record2);
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getScores().get(0).score)); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMatcher#getMatchingWeight(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     */
    @Test
    public void testGetMatchingWeightRecordRecordForDifferentInputAttributeThreshold1_0() {
        // init record
        Record record1 = new Record(null, 0, StringUtils.EMPTY);
        Attribute attribute = new Attribute("0"); //$NON-NLS-1$
        // add master value
        attribute.setValue("jinanjinanjinanjinan"); //$NON-NLS-1$
        // sub element value
        attribute.getValues().get("jinan"); //$NON-NLS-1$
        record1.getAttributes().add(attribute);
        Record record2 = new Record(null, 0, StringUtils.EMPTY);
        attribute = new Attribute("1"); //$NON-NLS-1$
        // add master value
        attribute.setValue("jinanjinan"); //$NON-NLS-1$
        // add element value
        attribute.getValues().get("jinan"); //$NON-NLS-1$
        record2.getAttributes().add(attribute);

        // init Attribute matcher
        IAttributeMatcher[] attributeMatchers = new IAttributeMatcher[] {
                MFBAttributeMatcher.wrap(new JaroMatcher(), 1.0, 1.0, SubString.NO_SUBSTRING) };

        MFBRecordMatcher mfbRecordMatcher = new MFBRecordMatcher(0.85d);
        mfbRecordMatcher.setRecordSize(1);
        Assert.assertTrue("setAttributeMatchers fail", mfbRecordMatcher.setAttributeMatchers(attributeMatchers)); //$NON-NLS-1$
        MatchResult matchingResult = mfbRecordMatcher.getMatchingWeight(record1, record2);
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getScores().get(0).score)); //$NON-NLS-1$
    }

    @Test
    public void test_tdq18347() {
        // init record
        String[] survivorShipFunctions = new String[1];
        survivorShipFunctions[0] = SurvivorShipAlgorithmEnum.CONCATENATE.getValue();
        Record record1 = new Record(null, 0, StringUtils.EMPTY);
        Attribute attribute = new Attribute("0"); //$NON-NLS-1$
        // add master value
        attribute.setValue("jinanjinan"); //$NON-NLS-1$
        // sub element value
        attribute.getValues().get("jinan").increment(); //$NON-NLS-1$
        ;
        record1.getAttributes().add(attribute);

        Record record2 = new Record(null, 0, StringUtils.EMPTY);
        attribute = new Attribute("1"); //$NON-NLS-1$
        // add master value
        attribute.setValue("jinanjinan"); //$NON-NLS-1$
        record2.getAttributes().add(attribute);

        Record record3 = new Record(null, 0, StringUtils.EMPTY);
        attribute = new Attribute("2"); //$NON-NLS-1$
        // add master value
        attribute.setValue("jinan"); //$NON-NLS-1$
        record3.getAttributes().add(attribute);

        Record record4 = new Record(null, 0, StringUtils.EMPTY);
        attribute = new Attribute("3"); //$NON-NLS-1$
        // add master value
        attribute.setValue("jinan"); //$NON-NLS-1$
        record4.getAttributes().add(attribute);

        // init Attribute matcher
        IAttributeMatcher[] attributeMatchers = new IAttributeMatcher[] {
                MFBAttributeMatcher.wrap(new ExactMatcher(), 1.0, 1.0, SubString.NO_SUBSTRING) };

        MFBRecordMatcher mfbRecordMatcher = new MFBRecordMatcher(0.85d);
        mfbRecordMatcher.setSurvivorShipFunction(survivorShipFunctions);
        mfbRecordMatcher.setRecordSize(1);
        mfbRecordMatcher.setAttributeMatchers(attributeMatchers);

        MatchResult matchingResult = mfbRecordMatcher.getMatchingWeight(record1, record2);
        Assert.assertEquals("0.0", String.valueOf(matchingResult.getScores().get(0).score)); //$NON-NLS-1$

        matchingResult = mfbRecordMatcher.getMatchingWeight(record1, record3);
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getScores().get(0).score)); //$NON-NLS-1$

        matchingResult = mfbRecordMatcher.getMatchingWeight(record4, record3);
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getScores().get(0).score)); //$NON-NLS-1$
    }

    @Test
    public void test_tdq18347_2() {
        // 2 different functions and 2 matchers
        String[] survivorShipFunctions = new String[2];
        survivorShipFunctions[0] = SurvivorShipAlgorithmEnum.CONCATENATE.getValue();
        survivorShipFunctions[1] = SurvivorShipAlgorithmEnum.MOST_COMMON.getValue();
        Record record1 = new Record(null, 0, StringUtils.EMPTY);
        Attribute attribute = new Attribute("0"); //$NON-NLS-1$
        attribute.setValue("jinanjinan"); //$NON-NLS-1$
        attribute.getValues().get("jinan").increment();//$NON-NLS-1$
        record1.getAttributes().add(attribute);
        attribute = new Attribute("1"); //$NON-NLS-1$
        attribute.setValue("CCC"); //$NON-NLS-1$
        attribute.getValues().get("CC").increment();//$NON-NLS-1$
        record1.getAttributes().add(attribute);

        Record record2 = new Record(null, 0, StringUtils.EMPTY);
        attribute = new Attribute("0"); //$NON-NLS-1$
        attribute.setValue("jinanjinan"); //$NON-NLS-1$
        record2.getAttributes().add(attribute);
        attribute = new Attribute("1"); //$NON-NLS-1$
        attribute.setValue("CC"); //$NON-NLS-1$
        record2.getAttributes().add(attribute);

        Record record3 = new Record(null, 0, StringUtils.EMPTY);
        attribute = new Attribute("0"); //$NON-NLS-1$
        attribute.setValue("jinan"); //$NON-NLS-1$
        record3.getAttributes().add(attribute);
        attribute = new Attribute("1"); //$NON-NLS-1$
        attribute.setValue("CC"); //$NON-NLS-1$
        record3.getAttributes().add(attribute);

        Record record4 = new Record(null, 0, StringUtils.EMPTY);
        attribute = new Attribute("0"); //$NON-NLS-1$
        attribute.setValue("jinan"); //$NON-NLS-1$
        record4.getAttributes().add(attribute);
        attribute = new Attribute("1"); //$NON-NLS-1$
        attribute.setValue("CCC"); //$NON-NLS-1$
        record4.getAttributes().add(attribute);

        // init Attribute matcher
        IAttributeMatcher[] attributeMatchers = new IAttributeMatcher[] {
                MFBAttributeMatcher.wrap(new ExactMatcher(), 1.0, 1.0, SubString.NO_SUBSTRING),
                MFBAttributeMatcher.wrap(new ExactMatcher(), 1.0, 1.0, SubString.NO_SUBSTRING) };

        MFBRecordMatcher mfbRecordMatcher = new MFBRecordMatcher(0.85d);
        mfbRecordMatcher.setSurvivorShipFunction(survivorShipFunctions);
        mfbRecordMatcher.setRecordSize(2);
        mfbRecordMatcher.setAttributeMatchers(attributeMatchers);

        MatchResult matchingResult = mfbRecordMatcher.getMatchingWeight(record1, record2);
        Assert.assertEquals("0.0", String.valueOf(matchingResult.getScores().get(0).score)); //$NON-NLS-1$
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getScores().get(1).score));

        matchingResult = mfbRecordMatcher.getMatchingWeight(record1, record3);
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getScores().get(0).score)); //$NON-NLS-1$
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getScores().get(1).score));

        matchingResult = mfbRecordMatcher.getMatchingWeight(record4, record3);
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getScores().get(0).score)); //$NON-NLS-1$
        Assert.assertEquals("0.0", String.valueOf(matchingResult.getScores().get(1).score));

        matchingResult = mfbRecordMatcher.getMatchingWeight(record1, record4);
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getScores().get(0).score)); //$NON-NLS-1$
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getScores().get(1).score));
        Assert.assertEquals("1.0", String.valueOf(matchingResult.getNormalizedConfidence()));
    }
}
