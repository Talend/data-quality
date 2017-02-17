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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.matchmerge.Attribute;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class RichRecordTest {

    private final String NEWGID = "c70d2961-dd21-42df-8eae-d52caface219";

    private final String OLDGID = "610ba2eb-b7d7-4c06-b3b6-112159f2d8b0";

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
     * {@link org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord#getOutputRow(java.util.Map, boolean)}.
     */
    @Test
    public void testGetOutputRowMapOfStringStringAndboolean() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.addAll(generateAttributesList());
        RichRecord mergedRecord = new RichRecord(attributes, "1", 1l, "MFB"); //$NON-NLS-1$ //$NON-NLS-2$
        mergedRecord.setOriginRow(generateOriginRow());
        // mergedRecord.set
        Map<String, String> oldGID2new = new HashMap<>();
        oldGID2new.put(OLDGID, NEWGID);
        mergedRecord.setMerged(true);
        mergedRecord.setMaster(true);
        mergedRecord.setGroupId(NEWGID);
        mergedRecord.setGrpSize(2);
        mergedRecord.setRecordSize(3);
        mergedRecord.setGRP_SIZE(4);
        mergedRecord.setGroupQuality(0.7272727272727273d);

        List<DQAttribute<?>> outputRow = mergedRecord.getOutputRow(oldGID2new, true);
        Assert.assertEquals(3, outputRow.size());
        Assert.assertEquals("John Doe,John B. Doe,Jon Doe,John Doe", outputRow.get(0).getValue()); //$NON-NLS-1$
        Assert.assertEquals("Nantes", outputRow.get(1).getValue()); //$NON-NLS-1$
        Assert.assertEquals(null, outputRow.get(2).getValue());

    }

    /**
     * DOC zshen Comment method "generateOriginRow".
     * 
     * @return
     */
    private List<DQAttribute<?>> generateOriginRow() {
        List<DQAttribute<?>> listResult = new ArrayList<>();
        DQAttribute<String> dqAttribute = new DQAttribute<>("", 0); //$NON-NLS-1$
        dqAttribute.setOriginalValue("John Doe,John B. Doe"); //$NON-NLS-1$
        dqAttribute.setValue("John Doe,John B. Doe"); //$NON-NLS-1$
        listResult.add(dqAttribute);

        dqAttribute = new DQAttribute<>("", 1); //$NON-NLS-1$
        dqAttribute.setOriginalValue("Nantes"); //$NON-NLS-1$
        dqAttribute.setValue("Nantes"); //$NON-NLS-1$
        listResult.add(dqAttribute);

        dqAttribute = new DQAttribute<>("", 2); //$NON-NLS-1$
        dqAttribute.setOriginalValue(null);
        dqAttribute.setValue(null);
        listResult.add(dqAttribute);
        return listResult;
    }

    /**
     * DOC zshen Comment method "generateAttributesList".
     * 
     * @return
     */
    private Collection<? extends Attribute> generateAttributesList() {
        List<Attribute> resultList = new ArrayList<>();

        Attribute attribute = new Attribute("fullName", 0); //$NON-NLS-1$
        attribute.setValue("John Doe,John B. Doe,Jon Doe,John Doe"); //$NON-NLS-1$
        resultList.add(attribute);
        attribute = new Attribute("city", 1); //$NON-NLS-1$
        attribute.setValue("Nantes"); //$NON-NLS-1$
        resultList.add(attribute);
        attribute = new Attribute("zipCode", 2); //$NON-NLS-1$
        attribute.setValue(null);
        resultList.add(attribute);
        attribute = new Attribute("GID", 3); //$NON-NLS-1$
        attribute.setValue(OLDGID);
        resultList.add(attribute);
        attribute = new Attribute("MERGED_RECORD", 4); //$NON-NLS-1$
        attribute.setValue("2"); //$NON-NLS-1$
        resultList.add(attribute);
        return resultList;
    }

}
