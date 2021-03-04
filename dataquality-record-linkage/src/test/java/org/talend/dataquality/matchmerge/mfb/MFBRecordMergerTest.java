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

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

public class MFBRecordMergerTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#merge(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * @throws ParseException
     */
    @Test
    public void testMerge1() throws ParseException {
        MFBRecordMerger mfbRecordMerger = new MFBRecordMerger(null, null,
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        Map<String, String> patternMap = new HashMap<>();
        String datePattern = "dd-MM-yyyy"; //$NON-NLS-1$
        patternMap.put("0", datePattern); //$NON-NLS-1$
        patternMap.put("1", datePattern); //$NON-NLS-1$
        mfbRecordMerger.setColumnDatePatternMap(patternMap);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        List<Attribute> r1Arributes = new ArrayList<>();
        List<Attribute> r2Arributes = new ArrayList<>();
        String referenceValue = "03-03-2003"; //$NON-NLS-1$
        String inputColValue = "02-02-2000"; //$NON-NLS-1$
        String colName = "HIREDATE"; //$NON-NLS-1$
        Attribute attribute1 = new Attribute(colName, 0, inputColValue, 1);
        attribute1.setReferenceValue(referenceValue);
        r1Arributes.add(attribute1);
        referenceValue = "05-05-2005"; //$NON-NLS-1$
        inputColValue = "06-06-2006"; //$NON-NLS-1$
        Attribute attribute2 = new Attribute(colName, 0, inputColValue, 1);
        attribute2.setReferenceValue(referenceValue);
        r2Arributes.add(attribute2);
        Record record1 = new Record(r1Arributes, "record1", simpleDateFormat.parse("02-02-2000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record record2 = new Record(r2Arributes, "record1", simpleDateFormat.parse("03-03-3000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record mergedRecord = mfbRecordMerger.merge(record1, record2);
        Attribute attribute = mergedRecord.getAttributes().get(0);
        Assert.assertEquals("Merge value should be 06-06-2006", inputColValue, attribute.getValue()); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#merge(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * @throws ParseException
     */
    @Test
    public void testMerge2() throws ParseException {
        MFBRecordMerger mfbRecordMerger = new MFBRecordMerger(null, null,
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        Map<String, String> patternMap = new HashMap<>();
        String datePattern = "dd-MM-yyyy"; //$NON-NLS-1$
        patternMap.put("0", datePattern); //$NON-NLS-1$
        patternMap.put("1", datePattern); //$NON-NLS-1$
        mfbRecordMerger.setColumnDatePatternMap(patternMap);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        List<Attribute> r1Arributes = new ArrayList<>();
        List<Attribute> r2Arributes = new ArrayList<>();
        String referenceValue = "03-03-2003"; //$NON-NLS-1$
        String inputColValue = "02-02-2000"; //$NON-NLS-1$
        String colName = "HIREDATE"; //$NON-NLS-1$
        Attribute attribute1 = new Attribute(colName, 0, inputColValue, 1);
        attribute1.setReferenceValue(referenceValue);
        r1Arributes.add(attribute1);
        referenceValue = "05-05-2005"; //$NON-NLS-1$
        inputColValue = "01-01-1999"; //$NON-NLS-1$
        Attribute attribute2 = new Attribute(colName, 0, inputColValue, 1);
        attribute2.setReferenceValue(referenceValue);
        r2Arributes.add(attribute2);
        Record record1 = new Record(r1Arributes, "record1", simpleDateFormat.parse("02-02-2000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record record2 = new Record(r2Arributes, "record1", simpleDateFormat.parse("03-03-3000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record mergedRecord = mfbRecordMerger.merge(record1, record2);
        Attribute attribute = mergedRecord.getAttributes().get(0);
        Assert.assertEquals("Merge value should be 01-01-1999", inputColValue, attribute.getValue()); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#merge(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * @throws ParseException
     */
    @Test
    public void testMerge3() throws ParseException {
        MFBRecordMerger mfbRecordMerger = new MFBRecordMerger(null, null,
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        Map<String, String> patternMap = new HashMap<>();
        String datePattern = "dd-MM-yyyy"; //$NON-NLS-1$
        patternMap.put("1", datePattern); //$NON-NLS-1$
        mfbRecordMerger.setColumnDatePatternMap(patternMap);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        List<Attribute> r1Arributes = new ArrayList<>();
        List<Attribute> r2Arributes = new ArrayList<>();
        String referenceValue = "03-03-2003"; //$NON-NLS-1$
        String inputColValue = "beijing"; //$NON-NLS-1$
        String colName = "HIREDATE"; //$NON-NLS-1$
        Attribute attribute1 = new Attribute(colName, 0, inputColValue, 1);
        attribute1.setReferenceValue(referenceValue);
        r1Arributes.add(attribute1);
        referenceValue = "05-05-2005"; //$NON-NLS-1$
        inputColValue = "hebei"; //$NON-NLS-1$
        Attribute attribute2 = new Attribute(colName, 0, inputColValue, 1);
        attribute2.setReferenceValue(referenceValue);
        r2Arributes.add(attribute2);
        Record record1 = new Record(r1Arributes, "record1", simpleDateFormat.parse("02-02-2000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record record2 = new Record(r2Arributes, "record1", simpleDateFormat.parse("03-03-3000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record mergedRecord = mfbRecordMerger.merge(record1, record2);
        Attribute attribute = mergedRecord.getAttributes().get(0);
        Assert.assertEquals("Merge value should be hebei", inputColValue, attribute.getValue()); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#merge(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * @throws ParseException
     */
    @Test
    public void testMerge4() throws ParseException {
        MFBRecordMerger mfbRecordMerger = new MFBRecordMerger(null, null,
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        Map<String, String> patternMap = new HashMap<>();
        String datePattern = "dd-MM-yyyy"; //$NON-NLS-1$
        patternMap.put("1", datePattern); //$NON-NLS-1$
        mfbRecordMerger.setColumnDatePatternMap(patternMap);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        List<Attribute> r1Arributes = new ArrayList<>();
        List<Attribute> r2Arributes = new ArrayList<>();
        String referenceValue = "05-05-2005"; //$NON-NLS-1$
        String inputColValue = "beijing"; //$NON-NLS-1$
        String colName = "HIREDATE"; //$NON-NLS-1$
        Attribute attribute1 = new Attribute(colName, 0, inputColValue, 1);
        attribute1.setReferenceValue(referenceValue);
        r1Arributes.add(attribute1);
        referenceValue = "03-03-2003"; //$NON-NLS-1$
        inputColValue = "hebei"; //$NON-NLS-1$
        Attribute attribute2 = new Attribute(colName, 0, inputColValue, 1);
        attribute2.setReferenceValue(referenceValue);
        r2Arributes.add(attribute2);
        Record record1 = new Record(r1Arributes, "record1", simpleDateFormat.parse("02-02-2000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record record2 = new Record(r2Arributes, "record1", simpleDateFormat.parse("03-03-3000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record mergedRecord = mfbRecordMerger.merge(record1, record2);
        Attribute attribute = mergedRecord.getAttributes().get(0);
        Assert.assertEquals("Merge value should be beijing", "beijing", attribute.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#merge(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * @throws ParseException
     */
    @Test
    public void testMerge5() throws ParseException {
        MFBRecordMerger mfbRecordMerger = new MFBRecordMerger(null, null,
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        Map<String, String> patternMap = new HashMap<>();
        String datePattern = "dd-MM-yyyy"; //$NON-NLS-1$
        patternMap.put("0", datePattern); //$NON-NLS-1$
        mfbRecordMerger.setColumnDatePatternMap(patternMap);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        List<Attribute> r1Arributes = new ArrayList<>();
        List<Attribute> r2Arributes = new ArrayList<>();
        String referenceValue = "beijing"; //$NON-NLS-1$
        String inputColValue = "05-05-2005"; //$NON-NLS-1$
        String colName = "HIREDATE"; //$NON-NLS-1$
        Attribute attribute1 = new Attribute(colName, 0, inputColValue, 1);
        attribute1.setReferenceValue(referenceValue);
        r1Arributes.add(attribute1);
        referenceValue = "hebei"; //$NON-NLS-1$
        inputColValue = "03-03-2003"; //$NON-NLS-1$
        Attribute attribute2 = new Attribute(colName, 0, inputColValue, 1);
        attribute2.setReferenceValue(referenceValue);
        r2Arributes.add(attribute2);
        Record record1 = new Record(r1Arributes, "record1", simpleDateFormat.parse("02-02-2000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record record2 = new Record(r2Arributes, "record1", simpleDateFormat.parse("03-03-3000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record mergedRecord = mfbRecordMerger.merge(record1, record2);
        Attribute attribute = mergedRecord.getAttributes().get(0);
        Assert.assertEquals("Merge value should be 05-05-2005", "05-05-2005", attribute.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#merge(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * @throws ParseException
     */
    @Test
    public void testMerge6() throws ParseException {
        MFBRecordMerger mfbRecordMerger = new MFBRecordMerger(null, null,
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        Map<String, String> patternMap = new HashMap<>();
        String datePattern = "dd-MM-yyyy"; //$NON-NLS-1$
        patternMap.put("0", datePattern); //$NON-NLS-1$
        mfbRecordMerger.setColumnDatePatternMap(patternMap);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        List<Attribute> r1Arributes = new ArrayList<>();
        List<Attribute> r2Arributes = new ArrayList<>();
        String referenceValue = "beijing"; //$NON-NLS-1$
        String inputColValue = "03-03-2003"; //$NON-NLS-1$
        String colName = "HIREDATE"; //$NON-NLS-1$
        Attribute attribute1 = new Attribute(colName, 0, inputColValue, 1);
        attribute1.setReferenceValue(referenceValue);
        r1Arributes.add(attribute1);
        referenceValue = "hebei"; //$NON-NLS-1$
        inputColValue = "05-05-2005"; //$NON-NLS-1$
        Attribute attribute2 = new Attribute(colName, 0, inputColValue, 1);
        attribute2.setReferenceValue(referenceValue);
        r2Arributes.add(attribute2);
        Record record1 = new Record(r1Arributes, "record1", simpleDateFormat.parse("02-02-2000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record record2 = new Record(r2Arributes, "record1", simpleDateFormat.parse("03-03-3000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record mergedRecord = mfbRecordMerger.merge(record1, record2);
        Attribute attribute = mergedRecord.getAttributes().get(0);
        Assert.assertEquals("Merge value should be 05-05-2005", "05-05-2005", attribute.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#merge(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * @throws ParseException
     */
    @Test
    public void testMerge7() throws ParseException {
        String[] data = new String[] { "0", "1", "2", "3", "16", "17", "18", "19", "19" };
        MFBRecordMerger mfbRecordMerger =
                new MFBRecordMerger(null, data, new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.LARGEST });
        Map<String, String> patternMap = new HashMap<>();
        String datePattern = "dd/MM/yyyy"; //$NON-NLS-1$
        patternMap.put("1", datePattern); //$NON-NLS-1$
        mfbRecordMerger.setColumnDatePatternMap(patternMap);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        List<Attribute> r1Arributes = new ArrayList<>();
        List<Attribute> r2Arributes = new ArrayList<>();

        String inputColValue = "50"; //$NON-NLS-1$
        String colName = "score"; //$NON-NLS-1$
        Attribute attribute1 = new Attribute(colName, 0, inputColValue, 1);
        r1Arributes.add(attribute1);

        inputColValue = "20"; //$NON-NLS-1$
        Attribute attribute2 = new Attribute(colName, 0, inputColValue, 1);
        r2Arributes.add(attribute2);
        Record record1 = new Record(r1Arributes, "record1", simpleDateFormat.parse("02/02/2000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record record2 = new Record(r2Arributes, "record1", simpleDateFormat.parse("03/03/3000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$  
        Record mergedRecord = mfbRecordMerger.merge(record1, record2);
        Attribute attribute = mergedRecord.getAttributes().get(0);
        Assert.assertEquals("Merge value should be the large one:50", "50", attribute.getValue());
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#merge(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * @throws ParseException
     */
    @Test
    public void testMerge8() throws ParseException {
        String[] data = new String[] { "0", "1", "2", "3", "16", "17", "18", "19", "19" };
        MFBRecordMerger mfbRecordMerger = new MFBRecordMerger(null, data,
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        Map<String, String> patternMap = new HashMap<>();
        String datePattern = "dd/MM/yyyy"; //$NON-NLS-1$
        patternMap.put("1", datePattern); //$NON-NLS-1$
        mfbRecordMerger.setColumnDatePatternMap(patternMap);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        List<Attribute> r1Arributes = new ArrayList<>();
        List<Attribute> r2Arributes = new ArrayList<>();

        String inputColValue = null;
        ; //$NON-NLS-1$
        String colName = "score"; //$NON-NLS-1$
        Attribute attribute1 = new Attribute(colName, 0, inputColValue, 1);
        r1Arributes.add(attribute1);

        inputColValue = null; //$NON-NLS-1$
        Attribute attribute2 = new Attribute(colName, 0, inputColValue, 1);
        r2Arributes.add(attribute2);
        Record record1 = new Record(r1Arributes, "record1", simpleDateFormat.parse("02/02/2000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Record record2 = new Record(r2Arributes, "record1", simpleDateFormat.parse("03/03/3000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$  
        Record mergedRecord = mfbRecordMerger.merge(record1, record2);
        Attribute attribute = mergedRecord.getAttributes().get(0);
        Assert.assertEquals("Merge value should be the null", null, attribute.getValue());
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#createMergeValue(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * 
     */
    @Test
    public void testCreateMergeValue_CaseMostRecient() {
        MFBRecordMerger mFBRecordMerger = new MFBRecordMerger("", new String[0],
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        String left = "05-05-2005";
        String right = "06-07-2019";
        String mergeValue = mFBRecordMerger.createMergeValue("MFB", "MFB", null, 1, 2,
                SurvivorShipAlgorithmEnum.MOST_RECENT, left, right, null, null);
        assertEquals(right, mergeValue);

        left = "06-07-2019";
        right = "05-05-2005";
        mergeValue = mFBRecordMerger.createMergeValue("MFB", "MFB", null, 2, 1, SurvivorShipAlgorithmEnum.MOST_RECENT,
                left, right, null, null);
        assertEquals(left, mergeValue);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#createMergeValue(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * 
     */
    @Test
    public void testCreateMergeValue_CaseMostAncient() {
        MFBRecordMerger mFBRecordMerger = new MFBRecordMerger("", new String[0],
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        String left = "05-05-2005";
        String right = "06-07-2019";
        String mergeValue = mFBRecordMerger.createMergeValue("MFB", "MFB", null, 1, 2,
                SurvivorShipAlgorithmEnum.MOST_ANCIENT, left, right, null, null);
        assertEquals(left, mergeValue);

        left = "06-07-2019";
        right = "05-05-2005";
        mergeValue = mFBRecordMerger.createMergeValue("MFB", "MFB", null, 2, 1, SurvivorShipAlgorithmEnum.MOST_ANCIENT,
                left, right, null, null);
        assertEquals(right, mergeValue);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#createMergeValue(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * 
     */
    @Test
    public void testCreateMergeValue_CasePreferTrue() {
        MFBRecordMerger mFBRecordMerger = new MFBRecordMerger("", new String[0],
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        String left = "";
        String right = "true";
        String mergeValue = mFBRecordMerger.createMergeValue("MFB", "MFB", null, 1, 2,
                SurvivorShipAlgorithmEnum.PREFER_TRUE, left, right, null, null);
        assertEquals("true", mergeValue);

        left = "false";
        right = "false";
        mergeValue = mFBRecordMerger.createMergeValue("MFB", "MFB", null, 2, 1, SurvivorShipAlgorithmEnum.PREFER_TRUE,
                left, right, null, null);
        assertEquals("false", mergeValue);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#createMergeValue(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * 
     */
    @Test
    public void testCreateMergeValue_CasePreferFasle() {
        MFBRecordMerger mFBRecordMerger = new MFBRecordMerger("", new String[0],
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        String left = "true";
        String right = "true";
        String mergeValue = mFBRecordMerger.createMergeValue("MFB", "MFB", null, 1, 2,
                SurvivorShipAlgorithmEnum.PREFER_FALSE, left, right, null, null);
        assertEquals("true", mergeValue);

        left = "true";
        right = "false";
        mergeValue = mFBRecordMerger.createMergeValue("MFB", "MFB", null, 2, 1, SurvivorShipAlgorithmEnum.PREFER_FALSE,
                left, right, null, null);
        assertEquals("false", mergeValue);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matchmerge.mfb.MFBRecordMerger#createMergeValue(org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.Record)}
     * .
     * 
     * 
     */
    @Test
    public void testCreateMergeValue_CaseMostTrustSource() {
        MFBRecordMerger mFBRecordMerger = new MFBRecordMerger("", new String[0],
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT });
        String left = "false";
        String right = "true";
        String mergeValue = null;
        try {
            mFBRecordMerger.createMergeValue("MFB", "MFB", null, 1, 2, SurvivorShipAlgorithmEnum.MOST_TRUSTED_SOURCE,
                    left, right, null, null);
        } catch (IllegalStateException e) {
            Assert.assertEquals("Survivorship 'most trusted source' must specify a trusted source.", e.getMessage());
        }

        mergeValue = mFBRecordMerger.createMergeValue("MFB", "MFB", "MFB", 2, 1,
                SurvivorShipAlgorithmEnum.MOST_TRUSTED_SOURCE, left, right, null, null);
        assertEquals("true", mergeValue);

        mergeValue = mFBRecordMerger.createMergeValue("MFB", "MFB", "test", 2, 1,
                SurvivorShipAlgorithmEnum.MOST_TRUSTED_SOURCE, left, right, null, null);
        assertEquals("false", mergeValue);
    }
}
