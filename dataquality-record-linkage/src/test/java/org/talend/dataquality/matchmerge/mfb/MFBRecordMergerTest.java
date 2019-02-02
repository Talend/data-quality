// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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
}
