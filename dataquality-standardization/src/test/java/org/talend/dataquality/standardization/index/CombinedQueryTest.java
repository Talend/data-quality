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
package org.talend.dataquality.standardization.index;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.search.TopDocs;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for combined query.
 */
public class CombinedQueryTest {

    private static final String INDEX_PATH = "target/test_data/test_combined";

    /**
     * ATTENTION: Be careful when changing this list of synonyms, they are also use in SynonymIndexSearcherTest.
     */
    private static String[][] synonyms = {
            //
            { "Paris 5eme", "Paris 05 Panthéon|Paris 5|75005|some|other|synonyms" },
            { "Paris 2eme", "Paris 02 Bourse|Paris 2|75002" },
            { "Paris", "巴黎|Paryz|Parizh|Parizs|Paras|Pariz|Parigi|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T" },
            //
            { "222", "" }, { "", "222" },
            //
            { "111", "AA BB CC" }, { "222", "AA|BB CC|333" }, { "333", "AA BB|CC|DD|222" },
            //
            { "222 333", "XXX|YYY|ZZZ" }, { "222 444", "XXX|YYY|ZZZ|WWW" }, { "YYY", "222 333" },
            { "YYY", "222 444 | ZZZ" },
            //
            { "XXX", "AA2|AA3|AA4|AA5|YYY" }, { "A YYY ZZZ", "ZZZ|WWW" }, { "XXX", "AA2|AA3|AA4|AA5|YYY" },
            { "A YYY ZZZ", "ZZZ|WWW" }, { "XXX", "AA2|AA3|AA4|AA5|YYY" }, { "A YYY ZZZ", "ZZZ|WWW" },
            { "XXX", "AA2|AA3|AA4|AA5|YYY" }, { "A YYY ZZZ", "ZZZ|WWW" }, { "XXX", "AA2|AA3|AA4|AA5|YYY" },
            { "A YYY ZZZ", "ZZZ|WWW" }, { "XXX", "AA2|AA3|AA4|AA5|YYY" }, { "A YYY ZZZ", "ZZZ|WWW" },
            { "XXX", "AA2|AA3|AA4|AA5|YYY" }, { "A YYY ZZZ", "ZZZ|WWW" }, { "XXX", "AA2|AA3|AA4|AA5|YYY" },
            { "A YYY ZZZ", "ZZZ|WWW" }, { "XXX", "AA2|AA3|AA4|AA5|YYY" }, { "A YYY ZZZ", "ZZZ|WWW" },
            { "XXX", "AA2|AA3|AA4|AA5|YYY" }, { "A YYY ZZZ", "ZZZ|WWW" }, { "XXX", "AA2|AA3|AA4|AA5|YYY" },
            { "A YYY ZZZ", "ZZZ|WWW" }, { "XXX", "AA2|AA3|AA4|AA5|YYY" }, };

    public static final Map<String, List<Integer>> TEST_CASE_MAP = new LinkedHashMap<String, List<Integer>>() {

        private static final long serialVersionUID = 1L;
        {
            put("PARIS", Arrays.asList(new Integer[] { 2, 0, 1 }));
            put("222", Arrays.asList(new Integer[] { 3, 5, 6, 7, 8, 9, 10 }));
            put("333", Arrays.asList(new Integer[] { 6, 5, 7, 9 }));
            put("Aa", Arrays.asList(new Integer[] { 5, 4, 6 }));
            put("Bb", Arrays.asList(new Integer[] { 4, 5, 6 }));
            put("Cc", Arrays.asList(new Integer[] { 6, 4, 5 }));
            put("Aa Bb", Arrays.asList(new Integer[] { 6, 4, 5 }));
            put("Bb Cc", Arrays.asList(new Integer[] { 5, 4, 6 }));
            put("Aa Cc", Arrays.asList(new Integer[] { 4, 5, 6 }));
            put("Aa Bb Cc", Arrays.asList(new Integer[] { 4, 5, 6 }));
            put("PARIS", Arrays.asList(new Integer[] { 2, 0, 1 }));
        }
    };

    /**
     * DOC sizhaoliu Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        SynonymIndexBuilder builder = new SynonymIndexBuilder();
        builder.deleteIndexFromFS(INDEX_PATH);
        // clear any existing files
        assertEquals(builder.getError().getMessage(), true, builder.deleteIndexFromFS(INDEX_PATH));

        builder.initIndexInFS(INDEX_PATH);
        insertDocuments(builder);
        builder.closeIndex();
    }

    @After
    public void cleanup() throws Exception {
        FileUtils.deleteDirectory(new File(INDEX_PATH));
    }

    static void insertDocuments(SynonymIndexBuilder build) throws IOException {
        for (String[] syns : synonyms) {
            build.insertDocument(syns[0], syns[1]);
        }
        build.commit();
    }

    @Test
    public void testSearchDocumentBySynonym() throws IOException {
        SynonymIndexSearcher searcher = getSearcher();
        searcher.setTopDocLimit(10);

        for (String word_to_search : TEST_CASE_MAP.keySet()) {
            List<Integer> expectation = TEST_CASE_MAP.get(word_to_search);
            System.out.println("\n-----------Looking for <" + word_to_search + ">-----------");
            TopDocs docs = searcher.searchDocumentBySynonym(word_to_search);
            System.out.print(docs.totalHits + " documents found.");
            // assertEquals(3, docs.totalHits);
            // assertEquals(true, searcher.getTopDocLimit() >= docs.scoreDocs.length);
            for (int i = 0; i < docs.scoreDocs.length; i++) {
                int docNumber = docs.scoreDocs[i].doc;
                System.out.print("\ndoc=" + docNumber + "\tscore=" + docs.scoreDocs[i].score);
                // Document doc = builder.getSearcher().doc(docs.scoreDocs[i].doc);
                System.out.print("    \t" + searcher.getWordByDocNumber(docNumber));
                System.out.print("\t-> [");
                for (String syn : searcher.getSynonymsByDocNumber(docNumber)) {
                    System.out.print(syn + "|");
                }
                System.out.print("]");
                assertEquals("Unexpected document classment", new Integer(expectation.get(i)), new Integer(docNumber));
            }
        }
        searcher.close();
    }

    /**
     * DOC scorreia Comment method "getSearcher".
     * 
     * @return
     */
    private SynonymIndexSearcher getSearcher() {
        SynonymIndexSearcher searcher = new SynonymIndexSearcher();
        try {
            // searcher.setAnalyzer(builder.getAnalyzer());
            searcher.openIndexInFS(INDEX_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        searcher.setTopDocLimit(5);

        return searcher;
    }

}
