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
package org.talend.dataquality.record.linkage.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

public class StringsClusterAnalyzerTest {

    StringsClusterAnalyzer analyser = null;

    private static void assertTShirtsResult(StringClusters clusters) {
        Map<String, List<String>> expectedClusters = new HashMap<>();
        expectedClusters.put("Black T-shirt", Arrays.asList("Blck T-shirt", "Black T-shirt", "Black Tshirt", "Black T-shirt",
                "Black T-Shirt", "Black T-shirT"));
        expectedClusters.put("Blck T-shirt", Collections.singletonList("Blck T-shirt"));
        expectedClusters.put("White T-shirt", Collections.singletonList("White T-shirt"));
        for (StringClusters.StringCluster cluster : clusters) {
            Assert.assertTrue(expectedClusters.containsKey(cluster.survivedValue));
            for (String originalValue : cluster.originalValues) {
                Assert.assertTrue(expectedClusters.get(cluster.survivedValue).contains(originalValue));
            }
        }
    }

    private static void assertElementResult(StringClusters clusters) {
        Map<String, List<String>> expectedClusters = new HashMap<>();
        expectedClusters.put("élément", Arrays.asList("élément", "element"));
        for (StringClusters.StringCluster cluster : clusters) {
            Assert.assertTrue(expectedClusters.containsKey(cluster.survivedValue));
            for (String originalValue : cluster.originalValues) {
                Assert.assertTrue(expectedClusters.get(cluster.survivedValue).contains(originalValue));
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        analyser = new StringsClusterAnalyzer();
    }

    @Test
    public void testGetResult() throws IOException {
        analyser.init();
        String columnDelimiter = "|";
        InputStream in = this.getClass().getResourceAsStream("incoming_customers_swoosh_fingerprintkey.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            analyser.analyze(fields[1]);
        }
        analyser.end();
        List<StringClusters> results = analyser.getResult();
        assertElementResult(results.get(0));
    }

    @Test
    public void testCluster10000() throws IOException {
        analyser.init();
        String columnDelimiter = "|";
        InputStream in = this.getClass().getResourceAsStream("cluster10000.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            analyser.analyze(fields[0]);
        }
        analyser.end();
        List<StringClusters> results = analyser.getResult();
        assertElementResult(results.get(0));
    }

    @Test
    public void testCluster10000WithThreshold() throws IOException {
        analyser.init();
        analyser.setBlockSizeThreshold(10);
        analyser.withPostMerges(new PostMerge(AttributeMatcherType.JARO_WINKLER, 0.8f));
        String columnDelimiter = "|";
        InputStream in = this.getClass().getResourceAsStream("cluster10000.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            analyser.analyze(fields[0]);
        }
        analyser.end();
        List<StringClusters> results = analyser.getResult();
        assertElementResult(results.get(0));
    }

    @Test
    public void testTShirtsLogic() throws IOException {
        analyser.init();
        String columnDelimiter = "|";
        InputStream in = this.getClass().getResourceAsStream("tshirts.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            analyser.analyze(fields[0]);
        }
        analyser.end();

        assertTShirtsResult(analyser.getResult().get(0));
    }

    @Test
    public void testTShirtsLogicWithThreshold() throws IOException {
        analyser.init();
        analyser.setBlockSizeThreshold(2); // Holds at most 2 records in memory for each block
        analyser.withPostMerges(new PostMerge(AttributeMatcherType.SOUNDEX, 0.8f));
        String columnDelimiter = "|";
        InputStream in = this.getClass().getResourceAsStream("tshirts.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            analyser.analyze(fields[0]);
        }

        analyser.end();
        assertTShirtsResult(analyser.getResult().get(0));
    }

    @Test
    public void testDuplicates() throws IOException {
        analyser.init();
        String[] data = new String[] { "test1", "test2", "test1", "test1", "test2", "test2", "test1" };
        for (String line : data) {
            analyser.analyze(line);
        }

        analyser.end();
        StringClusters scs = analyser.getResult().get(0);

        Map<String, List<String>> expectedClusters = new HashMap<>();
        expectedClusters.put("test1", Arrays.asList("test1"));
        expectedClusters.put("test2", Collections.singletonList("test2"));
        for (StringClusters.StringCluster cluster : scs) {
            Assert.assertTrue(expectedClusters.containsKey(cluster.survivedValue));
            for (String originalValue : cluster.originalValues) {
                Assert.assertTrue(expectedClusters.get(cluster.survivedValue).contains(originalValue));
            }
        }
    }

}
