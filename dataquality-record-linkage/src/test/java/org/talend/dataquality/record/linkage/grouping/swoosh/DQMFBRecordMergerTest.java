// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

public class DQMFBRecordMergerTest {

    /**
     * Test method for {@link DQMFBRecordMerger#createNewRecord(Record, Record, long)} .
     * 
     * @throws ParseException
     */
    @Test
    public void testMerge1() throws ParseException {
        DQMFBRecordMerger dqMFBRecordMerger = new DQMFBRecordMerger(null, null,
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT }, initSurvivorShipAlgorithmParams());
        Map<String, String> patternMap = new HashMap<>();
        String datePattern = "dd-MM-yyyy"; //$NON-NLS-1$
        patternMap.put("0", datePattern); //$NON-NLS-1$
        patternMap.put("1", datePattern); //$NON-NLS-1$
        dqMFBRecordMerger.setColumnDatePatternMap(patternMap);
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
        RichRecord record1 = new RichRecord(r1Arributes, "record1", simpleDateFormat.parse("02-02-2000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        RichRecord record2 = new RichRecord(r2Arributes, "record2", simpleDateFormat.parse("03-03-3000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        record1.setOriginRow(initDQAttribute("02-02-2000", "03-03-2003", "beijing")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        record2.setOriginRow(initDQAttribute("06-06-2006", "05-05-2005", "shanghai")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        RichRecord createNewRecord = (RichRecord) dqMFBRecordMerger.createNewRecord(record1, record2, 0);
        Assert.assertEquals("Merge value should be shanghai", "shanghai", createNewRecord.getOriginRow().get(2).getValue()); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("Merge reference value should be 05-05-2005", "05-05-2005", //$NON-NLS-1$//$NON-NLS-2$
                createNewRecord.getOriginRow().get(2).getReferenceValue());
        Assert.assertEquals("Merge reference column index value should be 1", 1, //$NON-NLS-1$
                createNewRecord.getOriginRow().get(2).getReferenceColumnIndex());
    }

    /**
     * Test method for {@link DQMFBRecordMerger#createNewRecord(Record, Record, long)} .
     * 
     * @throws ParseException
     * case 2 for datePattern is null
     */
    @Test
    public void testMerge2() throws ParseException {
        DQMFBRecordMerger dqMFBRecordMerger = new DQMFBRecordMerger(null, null,
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT }, initSurvivorShipAlgorithmParams());
        Map<String, String> patternMap = new HashMap<>();
        String datePattern = "dd-MM-yyyy"; //$NON-NLS-1$
        patternMap.put("0", datePattern); //$NON-NLS-1$
        patternMap.put("1", datePattern); //$NON-NLS-1$
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
        RichRecord record1 = new RichRecord(r1Arributes, "record1", simpleDateFormat.parse("02-02-2000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        RichRecord record2 = new RichRecord(r2Arributes, "record2", simpleDateFormat.parse("03-03-3000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        record1.setOriginRow(initDQAttribute("02-02-2000", "03-03-2003", "beijing")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        record2.setOriginRow(initDQAttribute("06-06-2006", "05-05-2005", "shanghai")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        RichRecord createNewRecord = (RichRecord) dqMFBRecordMerger.createNewRecord(record1, record2, 0);
        // compare timestamp in this case
        Assert.assertEquals("Merge value should be shanghai", "shanghai", createNewRecord.getOriginRow().get(2).getValue()); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("Merge reference value should be 05-05-2005", "05-05-2005", //$NON-NLS-1$//$NON-NLS-2$
                createNewRecord.getOriginRow().get(2).getReferenceValue());
        Assert.assertEquals("Merge reference column index value should be 1", 1, //$NON-NLS-1$
                createNewRecord.getOriginRow().get(2).getReferenceColumnIndex());
    }

    /**
     * Test method for {@link DQMFBRecordMerger#createNewRecord(Record, Record, long)} .
     * 
     * @throws ParseException
     * case 2 for datePattern is not null but the pattern is null case
     */
    @Test
    public void testMerge3() throws ParseException {
        DQMFBRecordMerger dqMFBRecordMerger = new DQMFBRecordMerger(null, null,
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_RECENT }, initSurvivorShipAlgorithmParams());
        Map<String, String> patternMap = new HashMap<>();
        String datePattern = "dd-MM-yyyy"; //$NON-NLS-1$
        patternMap.put("0", datePattern); //$NON-NLS-1$
        dqMFBRecordMerger.setColumnDatePatternMap(patternMap);
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
        RichRecord record1 = new RichRecord(r1Arributes, "record1", simpleDateFormat.parse("02-02-2000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        RichRecord record2 = new RichRecord(r2Arributes, "record2", simpleDateFormat.parse("03-03-3000").getTime(), "MFB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        record1.setOriginRow(initDQAttribute("02-02-2000", "03-03-2003", "beijing")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        record2.setOriginRow(initDQAttribute("06-06-2006", "05-05-2005", "shanghai")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        RichRecord createNewRecord = (RichRecord) dqMFBRecordMerger.createNewRecord(record1, record2, 0);
        // compare timestamp in this case
        Assert.assertEquals("Merge value should be shanghai", "shanghai", createNewRecord.getOriginRow().get(2).getValue()); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertEquals("Merge reference value should be shanghai", "shanghai", //$NON-NLS-1$//$NON-NLS-2$
                createNewRecord.getOriginRow().get(2).getReferenceValue());
        Assert.assertEquals("Merge reference column index value should be 1", 1, //$NON-NLS-1$
                createNewRecord.getOriginRow().get(2).getReferenceColumnIndex());
    }

    private SurvivorShipAlgorithmParams initSurvivorShipAlgorithmParams() {
        SurvivorShipAlgorithmParams surShipAlgoriPar = new SurvivorShipAlgorithmParams();
        Map<Integer, SurvivorshipFunction> defaultSurviorshipRules = new HashMap<>();
        SurvivorshipFunction survivFunction = surShipAlgoriPar.new SurvivorshipFunction();
        survivFunction.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_RECENT);
        survivFunction.setReferenceColumnIndex(1);
        defaultSurviorshipRules.put(2, survivFunction);
        surShipAlgoriPar.setDefaultSurviorshipRules(defaultSurviorshipRules);
        return surShipAlgoriPar;
    }

    private List<DQAttribute<?>> initDQAttribute(String col0, String col1, String col2) {
        List<DQAttribute<?>> attributeList = new ArrayList<>();
        attributeList.add(new DQAttribute<Object>("HIREDATE", 0, col0)); //$NON-NLS-1$
        attributeList.add(new DQAttribute<Object>("ENDDATE", 1, col1)); //$NON-NLS-1$
        attributeList.add(new DQAttribute<Object>("CITY", 2, col2, 1)); //$NON-NLS-1$
        return attributeList;
    }

}
