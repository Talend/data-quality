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
package org.talend.dataquality.datamasking.generic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.generic.FieldDefinition.FieldDefinitionType;

public class BijectiveSubstitutionFunctionTest {

    private String output;

    private BijectiveSubstitutionFunction fn;

    @Before
    public void setUp() throws Exception {

        List<FieldDefinition> fieldDefinitionList = new ArrayList<>();
        fieldDefinitionList.add(new FieldDefinition(FieldDefinitionType.INTERVAL.getComponentValue(), null, 1L, 2L));
        fieldDefinitionList.add(new FieldDefinition(FieldDefinitionType.INTERVAL.getComponentValue(), null, 0L, 99L));
        fieldDefinitionList.add(new FieldDefinition(FieldDefinitionType.INTERVAL.getComponentValue(), null, 1L, 12L));

        fieldDefinitionList.add(new FieldDefinition(FieldDefinitionType.ENUMERATION.getComponentValue(),
                "01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19," + "2A,2B," //
                        + "21,22,23,24,25,26,27,28,29,30,31,32,33," //
                        + "34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66," //
                        + "67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99", //
                null, null));
        fieldDefinitionList.add(new FieldDefinition(FieldDefinitionType.INTERVAL.getComponentValue(), null, 1L, 990L));
        fieldDefinitionList.add(new FieldDefinition(FieldDefinitionType.INTERVAL.getComponentValue(), null, 1L, 999L));

        fn = new BijectiveSubstitutionFunction(fieldDefinitionList);
        fn.setRandom(new Random(42));
        fn.setKeepFormat(true);
    }

    @Test
    public void testEmpty() throws org.talend.dataquality.sampling.exception.DQException {
        fn.setKeepEmpty(true);
        output = fn.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testKeepInvalidPatternTrue() throws org.talend.dataquality.sampling.exception.DQException {
        fn.setKeepInvalidPattern(true);
        output = fn.generateMaskedRow(null);
        assertEquals(null, output);
        output = fn.generateMaskedRow("");
        assertEquals("", output);
        output = fn.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void testKeepInvalidPatternFalse() throws org.talend.dataquality.sampling.exception.DQException {
        fn.setKeepInvalidPattern(false);
        output = fn.generateMaskedRow(null);
        assertEquals(null, output);
        output = fn.generateMaskedRow("");
        assertEquals(null, output);
        output = fn.generateMaskedRow("AHDBNSKD");
        assertEquals(null, output);
    }

    @Test
    public void testGood1() throws org.talend.dataquality.sampling.exception.DQException {
        output = fn.generateMaskedRow("1860348282074");
        assertEquals("2000132446558", output);
    }

    @Test
    public void testGood2() throws org.talend.dataquality.sampling.exception.DQException {
        // with spaces
        output = fn.generateMaskedRow("2 12 12 15 953 006");
        assertEquals("1 17 05 11 293 176", output);
    }

    @Test
    public void testGood3() throws org.talend.dataquality.sampling.exception.DQException {
        // corse department
        output = fn.generateMaskedRow("10501-2B-532895");
        assertEquals("12312-85-719322", output);
    }

    @Test
    public void testGood4() throws org.talend.dataquality.sampling.exception.DQException {
        fn.setKeepFormat(false);
        // with a control key less than 10
        output = fn.generateMaskedRow("1960159794247");
        assertEquals("2761158866619", output);
    }

    @Test
    public void testWrongSsnFieldNumber() throws org.talend.dataquality.sampling.exception.DQException {
        fn.setKeepInvalidPattern(false);
        // without a number
        output = fn.generateMaskedRow("186034828207");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnFieldLetter() throws org.talend.dataquality.sampling.exception.DQException {
        fn.setKeepInvalidPattern(false);
        // with a wrong letter
        output = fn.generateMaskedRow("186034Y282079");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnFieldPattern() throws org.talend.dataquality.sampling.exception.DQException {
        fn.setKeepInvalidPattern(false);
        // with a letter instead of a number
        output = fn.generateMaskedRow("1860I48282079");
        assertEquals(null, output);
    }

}
