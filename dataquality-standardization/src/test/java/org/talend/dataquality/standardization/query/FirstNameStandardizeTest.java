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
package org.talend.dataquality.standardization.query;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * DOC sizhaoliu class global comment. Detailled comment
 */
public class FirstNameStandardizeTest {

    private final static String indexfolder = "src/test/resources/data/TalendGivenNames_index"; // $NON-NLS-1$ //$NON-NLS-1$

    private static IndexSearcher searcher = null;

    private static Analyzer searchAnalyzer = null;

    private static FirstNameStandardize fnameStandardize = null;

    private static final String inputName = "Michel"; //$NON-NLS-1$

    private static final String[][] expected = { { "Michel", "AUS", "MITCHEL", "MICHELE", "MICHEL" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Michel", "BEL", "MICHEL", "MICHEL", "MICHEL" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Michel", "DEU", "MICHEL", "MICHEL", "MICHEL" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Michel", "ESP", "MICHEL", "MICHEL", "MICHEL" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Michel", "FRA", "MICHEL", "MICHEL", "MICHEL" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Michel", "ITA", "MICHELA", "MICHELA", "MICHELE" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Michel", "RUS", "MICHEL", "MICHEL", "MICHEL" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Michel", "USA", "MICHEL", "MICHEL", "MICHEL" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

            { "Adrian", "AUS", "ADRIAN", "ADRIAN", "ADRIAN" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Adrian", "BEL", "ADRIAN", "ADRIAN", "ADRIAN" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Adrian", "DEU", "ADRIAN", "ADRIAN", "ADRIAN" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Adrian", "ESP", "ADRIAN", "ADRIAN", "ADRIAN" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Adrian", "FRA", "ADRIAN", "ADRIAN", "ADRIAN" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Adrian", "ITA", "ADRIANO", "ADRIANA", "ADRIANO" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Adrian", "RUS", "ADRIAN", "ADRIAN", "ADRIAN" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            { "Adrian", "USA", "ADRIAN", "ADRIAN", "ADRIAN" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    };

    private static final String[][] expected_fuzzy = { { "Alessandra", "ALESSANDRA", "ALESSANDRA" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            { "Antonino", "ANTONINO", "ANTONINO" }, { "amar", "AMAR", "AMAR" }, { "jan", "JAN", "JAN" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
            { "James", "JAMES", "JAMES" }, { "Keith", "KEITH", "KEITH" }, { "guy", "GUY", "GUY" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
            { "roland", "ROLAND", "ROLAND" }, { "Angela", "ANGELA", "ANGELA" }, { "Joe", "JOE", "JOE" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
            { "eric", "ERIC", "ERIC" }, { "francesco", "FRANCESCO", "FRANCESCO" }, { "Manfred", "MANFRED", "MANFRED" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
            { "malathi", "", "MALACHI" }, { "Aly", "ALY", "ALY" }, { "sreedhar", "", "" }, { "Louann", "LOUANN", "LOUANN" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
            { "Elif", "ELIF", "ELIF" }, { "Sreenivas", "", "" }, { "subhash", "SUBHASH", "SUBHASH" }, { "Dara", "DARA", "DARA" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
            { "Gabor", "GABOR", "GABOR" }, { "Jill", "JILL", "JILL" }, { "Michael", "MICHAEL", "MICHAEL" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
            { "bhargav", "", "BHARGAW" }, { "nonya", "", "NONNA" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    // input name |input gender|FIRSTNAMEMATCH|
    private static final String[][] expected_gender = { { "Philippe", "M", "PHILIPPE" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            { "Hubert", "M", "HUBERT" }, { "Ghislain", "M", "GHISLAIN" }, { "Christian", "F", "CHRISTIAN" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
            { "Bernard", "M", "BERNARD" }, { "Michel", "M", "MICHEL" }, { "Nicole", "F", "NICOLE" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
            { "Pierre", "M", "PIERRE" }, { "George", "M", "GEORGE" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ 
            { "Jacques", "M", "JACQUES" }, { "Didier", "M", "DIDIER" }, { "Roger", "M", "ROGER" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
            { null, "M", "" }, { "", "F", "" }, { "M.", "M", "" }, { "M", "M", "" }, { "Philipp", "M", "PHILIPP" }, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$//$NON-NLS-7$//$NON-NLS-8$//$NON-NLS-9$//$NON-NLS-10$//$NON-NLS-11$//$NON-NLS-12$//$NON-NLS-13$//$NON-NLS-14$
            { "Habert", "M", "HOBERT" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /**
     * DOC sizhaoliu Comment method "setUpBeforeClass".
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Directory dir = FSDirectory.open(new File(indexfolder));
        IndexReader reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
        searchAnalyzer = new SimpleAnalyzer();
        fnameStandardize = new FirstNameStandardize(searcher, searchAnalyzer, 10);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (searcher != null) {
            searcher.getIndexReader().close();
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.query.FirstNameStandardize#replaceName(java.lang.String, boolean)}.
     */
    @Test
    public void testReplaceName() {
        try {
            String res = fnameStandardize.replaceName(inputName, true);
            System.out.println("testReplaceName:\n" + res); //$NON-NLS-1$
            assertEquals("MICHEL", res); //$NON-NLS-1$

            res = fnameStandardize.replaceNameWithCountryInfo(inputName, "ITA", true); //$NON-NLS-1$ 
            assertEquals("MICHELA", res); //$NON-NLS-1$

            res = fnameStandardize.replaceNameWithCountryInfo(inputName, "", true); //$NON-NLS-1$ 
            assertEquals("MICHEL", res); //$NON-NLS-1$

            res = fnameStandardize.replaceNameWithCountryGenderInfo(inputName, "ITA", "F", true); //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals("MICHELA", res); //$NON-NLS-1$

            res = fnameStandardize.replaceNameWithCountryGenderInfo(inputName, "", "F", true); //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals("MICHEL", res); //$NON-NLS-1$

            res = fnameStandardize.replaceNameWithCountryGenderInfo(inputName, "ITA", "", true); //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals("MICHELA", res); //$NON-NLS-1$

            res = fnameStandardize.replaceNameWithGenderInfo(inputName, "F", true); //$NON-NLS-1$ 
            assertEquals("MICHEL", res); //$NON-NLS-1$

            res = fnameStandardize.replaceNameWithGenderInfo(inputName, "", true); //$NON-NLS-1$ 
            assertEquals("MICHEL", res); //$NON-NLS-1$

            res = fnameStandardize.replaceNameWithGenderInfo(inputName, "M", true); //$NON-NLS-1$ 
            assertEquals("MICHEL", res); //$NON-NLS-1$
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.query.FirstNameStandardize#replaceName(java.lang.String, boolean)}.
     */
    @Test
    public void testReplaceNameWithBlankInputName() {
        try {
            // case1: when the input is ""
            String inputName2 = ""; //$NON-NLS-1$
            String res = fnameStandardize.replaceName(inputName2, true);
            System.out.println("testReplaceName:\n" + res); //$NON-NLS-1$
            assertEquals(inputName2, res);

            res = fnameStandardize.replaceNameWithCountryInfo(inputName2, "ITA", true); //$NON-NLS-1$
            assertEquals(inputName2, res);

            res = fnameStandardize.replaceNameWithCountryGenderInfo(inputName2, "ITA", "F", true); //$NON-NLS-1$ //$NON-NLS-2$ 
            assertEquals(inputName2, res);

            res = fnameStandardize.replaceNameWithGenderInfo(inputName2, "F", true); //$NON-NLS-1$
            assertEquals(inputName2, res);

            // case2: when the input is null
            String inputName3 = null;
            res = fnameStandardize.replaceName(inputName3, true);
            System.out.println("testReplaceName:\n" + res); //$NON-NLS-1$
            assertEquals(inputName2, res);

            res = fnameStandardize.replaceNameWithCountryInfo(inputName3, "ITA", true); //$NON-NLS-1$
            assertEquals(inputName2, res);

            res = fnameStandardize.replaceNameWithCountryGenderInfo(inputName3, "ITA", "F", true); //$NON-NLS-1$ //$NON-NLS-2$ 
            assertEquals(inputName2, res);

            res = fnameStandardize.replaceNameWithGenderInfo(inputName3, "F", true); //$NON-NLS-1$
            assertEquals(inputName2, res);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.query.FirstNameStandardize#replaceNameWithCountryGenderInfo(java.lang.String, java.lang.String, java.lang.String, boolean)}
     * .
     */
    @Test
    public void testReplaceNameWithCountryGenderInfo() {
        try {

            System.out.println("\ntestReplaceNameWithCountryGenderInfo:"); //$NON-NLS-1$
            System.out.println("Name\tCountry\tNon-gender\tFemale\tMale"); //$NON-NLS-1$
            for (String[] testCase : expected) {
                String res, resF, resM = ""; //$NON-NLS-1$
                System.out.print("{\"" + testCase[0] + "\", \"" + testCase[1] + "\", \""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                // results for query without gender info
                res = fnameStandardize.replaceNameWithCountryInfo(testCase[0], testCase[1], true);
                System.out.print(res + "\", \""); //$NON-NLS-1$
                assertEquals(testCase[2], res);

                // results for female first name query
                resF = fnameStandardize.replaceNameWithCountryGenderInfo(testCase[0], testCase[1], "F", true); //$NON-NLS-1$
                System.out.print(resF + "\", \""); //$NON-NLS-1$
                assertEquals(testCase[3], resF);

                // results for male first name query
                resM = fnameStandardize.replaceNameWithCountryGenderInfo(testCase[0], testCase[1], "M", true); //$NON-NLS-1$
                System.out.println(resM + "\"}, //"); //$NON-NLS-1$
                assertEquals(testCase[4], resM);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testReplaceNameWithFuzzyOption() {
        try {

            System.out.println("\ntestReplaceNameWithFuzzyOption:"); //$NON-NLS-1$
            System.out.println("Name\tNon-fuzzy\tFuzzy"); //$NON-NLS-1$
            for (String[] testCase : expected_fuzzy) {
                String res = ""; //$NON-NLS-1$
                System.out.print("{\"" + testCase[0] + "\", \""); //$NON-NLS-1$ //$NON-NLS-2$

                // results for non-country, non-fuzzy match
                res = fnameStandardize.replaceName(testCase[0], false);
                System.out.print(res + "\", \""); //$NON-NLS-1$
                assertEquals(testCase[1], res);

                // results for non-country, fuzzy match
                res = fnameStandardize.replaceName(testCase[0], true);
                System.out.print(res + "\"},\n"); //$NON-NLS-1$
                assertEquals(testCase[2], res);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.query.FirstNameStandardize#replaceNameWithGenderInfo(java.lang.String, java.lang.String, boolean)}
     * .
     */
    @Test
    public void testReplaceNameWithGenderInfo() {
        try {

            System.out.println("\ntestReplaceNameWithGenderInfo:"); //$NON-NLS-1$
            System.out.println("Name\tgender\tFIRSTNAMEMATCH"); //$NON-NLS-1$
            for (String[] testCase : expected_gender) {
                String res = ""; //$NON-NLS-1$
                System.out.print("{\"" + testCase[0] + "\", \"" + testCase[1] + "\", \""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                // results for query with gender info
                res = fnameStandardize.replaceNameWithGenderInfo(testCase[0], testCase[1], true);
                System.out.print(res + "\"},\n"); //$NON-NLS-1$
                assertEquals(testCase[2], res);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
