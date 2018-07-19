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
package org.talend.dataquality.statistics.frequency.recognition;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.statistics.frequency.pattern.CompositePatternFrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.pattern.PatternFrequencyStatistics;

public class CustomDateTimePatternRecognizerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRecognize() {

        ArrayList<AbstractPatternRecognizer> recognizers = new ArrayList<AbstractPatternRecognizer>();
        recognizers.add(new EmptyPatternRecognizer());
        DateTimePatternRecognizer recognizer = new DateTimePatternRecognizer();
        recognizers.add(recognizer);
        recognizers.add(new LatinExtendedCharPatternRecognizer());
        recognizers.add(new EastAsianCharPatternRecognizer());

        CompositePatternFrequencyAnalyzer analyzer = new CompositePatternFrequencyAnalyzer(recognizers);
        final List<String> DATETIME_TO_TEST = new ArrayList<String>() {

            private static final long serialVersionUID = 1L;

            {
                add("   ");
                add(" ");
                add(null);
                add("abc");
                add("19 rue Pagès");
                add("拓蓝科技");
                add("2001-9-10");
                add("2011-9-20");
                add("2011-2-20");
                add("2013-1-20");
                add("=14/5/18="); // [custom: =d/M/yy=]
                add("4/15/18");
                add("4/5/2014");
                add("02/03/2014");
                add("22/03/2014");
            }
        };

        final Map<String, Long> EXPECTED_PATTERN_MAP = new HashMap<String, Long>() {

            private static final long serialVersionUID = 1L;

            {
                put("yyyy-M-d", 4L);
                put("", 2L);
                put("dd/MM/yyyy", 2L);
                put("d/M/yyyy", 1L);
                put("M/d/yyyy", 1L);
                put("aaa", 1L);
                put("99 aaa Aaaaa", 1L);
                put("M/d/yy", 1L);
                put("MM/dd/yyyy", 1L);
                put("=99/9/99=", 1L);

            }
        };

        for (String str : DATETIME_TO_TEST) {
            analyzer.analyze(str);
        }

        List<PatternFrequencyStatistics> statsList = analyzer.getResult();
        PatternFrequencyStatistics stats = statsList.get(0);
        Map<String, Long> topK = stats.getTopK(10);
        assertEquals(EXPECTED_PATTERN_MAP.size(), topK.size());
        for (String key : topK.keySet()) {
            // System.out.println("put(\"" + key + "\", " + topK.get(key) + "L);");
            assertEquals("Unexpected pattern count on pattern <" + key + ">", EXPECTED_PATTERN_MAP.get(key), topK.get(key));
        }

    }
}
