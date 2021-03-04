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
package org.talend.dataquality.statistics.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SampleTest {

    private static List<String> DATE_SAMPLES;

    private static List<String> TIME_SAMPLES;

    private final Map<String, Set<String>> AMBIGUOUS_DATE_FORMATS = new LinkedHashMap<String, Set<String>>() {

        private static final long serialVersionUID = 1L;

        {
            put("22/03/99", new HashSet<>(Arrays.asList(//
                    "d/MM/yy", "dd/MM/yy")));
            put("22.03.99", new HashSet<>(Arrays.asList(//
                    "dd.MM.yy", "d.MM.yy")));
            put("22 mars 1999", new HashSet<>(Arrays.asList(//
                    "d MMMM yyyy", "d MMM yyyy", "dd MMMM yyyy")));
            put("22.03.1999", new HashSet<>(Arrays.asList(//
                    "d.MM.yyyy", "dd.MM.yyyy")));
            put("22-Mar-1999", new HashSet<>(Arrays.asList(//
                    "dd-MMM-yyyy", "d-MMM-yyyy")));
            put("22-mar-1999", new HashSet<>(Arrays.asList(//
                    "dd-MMM-yyyy", "d-MMM-yyyy")));
            put("22 March 1999", new HashSet<>(Arrays.asList(//
                    "d MMMM yyyy", "dd MMMM yyyy")));
            put("22 marzo 1999", new HashSet<>(Arrays.asList(//
                    "d MMMM yyyy", "dd MMMM yyyy")));
            put("22 mars 1999 05:06:07 CET", new HashSet<>(Arrays.asList(//
                    "dd MMMM yyyy HH:mm:ss z", "d MMMM yyyy HH:mm:ss z")));
            put("22 March 1999 05:06:07 CET", new HashSet<>(Arrays.asList(//
                    "dd MMMM yyyy HH:mm:ss z", "d MMMM yyyy HH:mm:ss z")));
            put("22.03.99 5:06", new HashSet<>(Arrays.asList(//
                    "dd.MM.yy H:mm", "d.MM.yy H:mm")));
            put("22.03.1999 5:06:07", new HashSet<>(Arrays.asList(//
                    "dd.MM.yyyy H:mm:ss", "d.MM.yyyy H:mm:ss")));
        }
    };

    @BeforeClass
    public static void loadTestData() throws IOException {

        InputStream dateInputStream = SystemDateTimePatternManager.class.getResourceAsStream("DateSampleTable.txt");
        DATE_SAMPLES = IOUtils.readLines(dateInputStream, "UTF-8");
        InputStream timeInputStream = SystemDateTimePatternManager.class.getResourceAsStream("TimeSampleTable.txt");
        TIME_SAMPLES = IOUtils.readLines(timeInputStream, "UTF-8");
    }

    private static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    @Test
    public void testDatesWithMultipleFormats() {
        for (String sample : AMBIGUOUS_DATE_FORMATS.keySet()) {
            Set<String> patternSet = SystemDateTimePatternManager.getDatePatterns(sample).keySet();
            assertEquals("Unexpected Format Set on sample <" + sample + ">", AMBIGUOUS_DATE_FORMATS.get(sample),
                    patternSet);
        }
    }

    @Test
    public void uniquenessOfDateFormats() {
        for (int i = 1; i < DATE_SAMPLES.size(); i++) {
            String line = DATE_SAMPLES.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String date = sampleLine[0];
                String pattern = sampleLine[1];

                if (AMBIGUOUS_DATE_FORMATS.containsKey(date)) {
                    // Multiple formats expected
                    continue;
                }

                Set<String> patternSet = SystemDateTimePatternManager.getDatePatterns(date).keySet();
                assertEquals("Unexpected number of patterns for date <" + date + ">", 1, patternSet.size());
                assertEquals("Unexpected pattern on date <" + date + ">", pattern,
                        patternSet.toArray(new String[] {})[0]);
            }
        }
    }

    @Test
    public void testAllSupportedDatesWithRegexes() throws Exception {

        for (int i = 1; i < DATE_SAMPLES.size(); i++) {
            String line = DATE_SAMPLES.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];

                setFinalStatic(SystemDateTimePatternManager.class.getDeclaredField("dateTimeFormatterCache"),
                        new HashMap<String, DateTimeFormatter>());

                assertTrue(sample + " is expected to be a valid date but actually not.",
                        SystemDateTimePatternManager.isDate(sample));
            }
        }
    }

    @Test
    public void testAllSupportedTimesWithRegexes() throws Exception {

        for (int i = 1; i < TIME_SAMPLES.size(); i++) {
            String line = TIME_SAMPLES.get(i);
            System.out.println(line);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];
                assertTrue(sample + " is expected to be a valid time but actually not.",
                        SystemDateTimePatternManager.isTime(sample));
            }
        }
    }

}
