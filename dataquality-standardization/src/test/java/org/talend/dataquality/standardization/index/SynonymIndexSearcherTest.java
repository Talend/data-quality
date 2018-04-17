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
package org.talend.dataquality.standardization.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.TopDocs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher.SynonymSearchMode;
import org.talend.dataquality.standardization.record.OutputRecord;
import org.talend.dataquality.standardization.record.SynonymRecordSearcher;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SynonymIndexSearcherTest {

    boolean showInConsole = false;

    private boolean doAsserts = true;

    /**
     *
     */
    private SynonymIndexBuilderTest synIdxBuilderTest;

    /**
     * DOC scorreia Comment method "setUp".
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // create the index
        this.synIdxBuilderTest = new SynonymIndexBuilderTest();
        synIdxBuilderTest.setUp();

        SynonymIndexBuilder synonymIdxBuilder = new SynonymIndexBuilder();
        synonymIdxBuilder.initIndexInFS(SynonymIndexBuilderTest.path);
        synIdxBuilderTest.insertDocuments(synonymIdxBuilder);
        synonymIdxBuilder.closeIndex();
    }

    @Override
    @After
    public void finalize() throws Throwable {
        super.finalize();
        this.synIdxBuilderTest.finalize();
    }

    /**
     * Test method for {@link SynonymIndexSearcher#openIndexInFS(java.net.URI)}.
     */
    @Test
    public void testInitIndexInFS() {
        SynonymIndexSearcher searcher = new SynonymIndexSearcher();
        String path = "data/tmp_index";
        File idxFolder = new File(path);
        idxFolder.delete();
        doAssertEquals(false, idxFolder.exists());
        doAssertEquals(true, idxFolder.mkdirs());
        try {
            searcher.openIndexInFS(path);
        } catch (IOException e) {
            doAssertEquals(true, e.getMessage().contains("no segment"));
        }
        // use an existing index folder.
        try {
            searcher.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        doAssertEquals(true, idxFolder.exists());
        doAssertEquals(true, idxFolder.delete());
        searcher.close();
    }

    /**
     * Test method for {@link SynonymIndexSearcher#searchDocumentByWord(String)}.
     */
    @Test
    public void testSearchDocumentByWord() {
        SynonymIndexSearcher searcher = getSearcher();
        String bigblue[] = { "big blue", "Big Blue" };
        for (int i = 0; i < bigblue.length; i++) {
            String toFind = bigblue[i];
            TopDocs doc = searcher.searchDocumentByWord(toFind);
            doAssertEquals("we should have found no document, check the code", true, doc.totalHits == 0);
        }
        String words[] = { "I.B.M.", "ANPE" };
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            TopDocs doc = searcher.searchDocumentByWord(w);
            doAssertEquals("we should have found at least one document, check the list of synonyms or the code", false,
                    doc.totalHits == 0);
        }

        searcher.close();
    }

    @Test
    public void testSearchDocumentBySynonym() throws IOException {
        printLineToConsole("\n-----------Test searchDocumentBySynonym----------");
        SynonymIndexSearcher searcher = getSearcher();
        searcher.setAnalyzer(new StandardAnalyzer(CharArraySet.EMPTY_SET));
        TopDocs docs = searcher.searchDocumentBySynonym("ibm");
        printLineToConsole(docs.totalHits + " documents found.");

        doAssertEquals("unexpected totalHits size!", 3, docs.totalHits);
        doAssertEquals(true, searcher.getTopDocLimit() >= docs.scoreDocs.length);
        for (int i = 0; i < docs.scoreDocs.length; i++) {
            int docNumber = docs.scoreDocs[i].doc;
            printToConsole("\ndoc=" + docNumber + "\tscore=" + docs.scoreDocs[i].score);
            // Document doc = builder.getSearcher().doc(docs.scoreDocs[i].doc);
            printLineToConsole("\tword: " + searcher.getWordByDocNumber(docNumber));
            printLineToConsole("\tsynonyms: " + Arrays.toString(searcher.getSynonymsByDocNumber(docNumber)));
        }
        String[] bigblue = { "Big Blue", "big blue", "Blue", "big" };
        for (int i = 0; i < bigblue.length; i++) {
            String toFind = bigblue[i];
            TopDocs doc = searcher.searchDocumentBySynonym(toFind);
            doAssertEquals("we should have found at least one document, check the list of synonyms or the code", false,
                    doc.totalHits == 0);

        }

        searcher.close();

        // TODO check that the best matching is the exact string.
        // float[] scores = new float[bigblue.length];

    }

    private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget nulla. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur sodales ligula in libero. Sed dignissim lacinia nunc. Curabitur tortor. Pellentesque nibh. Aenean quam. In scelerisque sem at dolor. Maecenas mattis. Sed convallis tristique sem. Proin ut ligula vel nunc egestas porttitor. Morbi lectus risus, iaculis vel, suscipit quis, luctus non, massa. Fusce ac turpis quis ligula lacinia aliquet. Mauris ipsum. Nulla metus metus, ullamcorper vel, tincidunt sed, euismod in, nibh. Quisque volutpat condimentum velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nam nec ante. Sed lacinia, urna non tincidunt mattis, tortor neque adipiscing diam, a cursus ipsum ante quis turpis. Nulla facilisi. Ut fringilla. Suspendisse potenti. Nunc feugiat mi a tellus consequat imperdiet. Vestibulum sapien. Proin quam. Etiam ultrices. Suspendisse in justo eu magna luctus suscipit. Sed lectus. Integer euismod lacus luctus magna. Quisque cursus, metus vitae pharetra auctor, sem massa mattis sem, at interdum magna augue eget diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Morbi lacinia molestie dui. Praesent blandit dolor. Sed non quam. In vel mi sit amet augue congue elementum. Morbi in ipsum sit amet pede facilisis laoreet. Donec lacus nunc, viverra nec, blandit vel, egestas et, augue. Vestibulum tincidunt malesuada tellus. Ut ultrices ultrices enim. Curabitur sit amet mauris. Morbi in dui quis est pulvinar ullamcorper. Nulla facilisi. Integer lacinia sollicitudin massa. Cras metus. Sed aliquet risus a tortor. Integer id quam. Morbi mi. Quisque nisl felis, venenatis tristique, dignissim in, ultrices sit amet, augue. Proin sodales libero eget ante. Nulla quam. Aenean laoreet. Vestibulum nisi lectus, commodo ac, facilisis ac, ultricies eu, pede. Ut orci risus, accumsan porttitor, cursus quis, aliquet eget, justo. Sed pretium blandit orci. Ut eu diam at pede suscipit sodales. Aenean lectus elit, fermentum non, convallis id, sagittis at, neque. Nullam mauris orci, aliquet et, iaculis et, viverra vitae, ligula. Nulla ut felis in purus aliquam imperdiet. Maecenas aliquet mollis lectus. Vivamus consectetuer risus et tortor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget nulla. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur sodales ligula in libero. Sed dignissim lacinia nunc. Curabitur tortor. Pellentesque nibh. Aenean quam. In scelerisque sem at dolor. Maecenas mattis. Sed convallis tristique sem. Proin ut ligula vel nunc egestas porttitor. Morbi lectus risus, iaculis vel, suscipit quis, luctus non, massa. Fusce ac turpis quis ligula lacinia aliquet. Mauris ipsum. Nulla metus metus, ullamcorper vel, tincidunt sed, euismod in, nibh. Quisque volutpat condimentum velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nam nec ante. Sed lacinia, urna non tincidunt mattis, tortor neque adipiscing diam, a cursus ipsum ante quis turpis. Nulla facilisi. Ut fringilla. Suspendisse potenti. Nunc feugiat mi a tellus consequat imperdiet. Vestibulum sapien. Proin quam. Etiam ultrices. Suspendisse in justo eu magna luctus suscipit. Sed lectus. Integer euismod lacus luctus magna. Quisque cursus, metus vitae pharetra auctor, sem massa mattis sem, at interdum magna augue eget diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Morbi lacinia molestie dui. Praesent blandit dolor. Sed non quam. In vel mi sit amet augue congue elementum. Morbi in ipsum sit amet pede facilisis laoreet. Donec lacus nunc, viverra nec, blandit vel, egestas et, augue. Vestibulum tincidunt malesuada tellus. Ut ultrices ultrices enim. Curabitur sit amet mauris. Morbi in dui quis est pulvinar ullamcorper. Nulla facilisi. Integer lacinia sollicitudin massa. Cras metus. Sed aliquet risus a tortor. Integer id quam. Morbi mi. Quisque nisl felis, venenatis tristique, dignissim in, ultrices sit amet, augue. Proin sodales libero eget ante. Nulla quam. Aenean laoreet. Vestibulum nisi lectus, commodo ac, facilisis ac, ultricies eu, pede. Ut orci risus, accumsan porttitor, cursus quis, aliquet eget, justo. Sed pretium blandit orci. Ut eu diam at pede suscipit sodales. Aenean lectus elit, fermentum non, convallis id, sagittis at, neque. Nullam mauris orci, aliquet et, iaculis et, viverra vitae, ligula. Nulla ut felis in purus aliquam imperdiet. Maecenas aliquet mollis lectus. Vivamus consectetuer risus et tortor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget nulla. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur sodales ligula in libero. Sed dignissim lacinia nunc. Curabitur tortor. Pellentesque nibh. Aenean quam. In scelerisque sem at dolor. Maecenas mattis. Sed convallis tristique sem. Proin ut ligula vel nunc egestas porttitor. Morbi lectus risus, iaculis vel, suscipit quis, luctus non, massa. Fusce ac turpis quis ligula lacinia aliquet. Mauris ipsum. Nulla metus metus, ullamcorper vel, tincidunt sed, euismod in, nibh. Quisque volutpat condimentum velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nam nec ante. Sed lacinia, urna non tincidunt mattis, tortor neque adipiscing diam, a cursus ipsum ante quis turpis. Nulla facilisi. Ut fringilla. Suspendisse potenti. Nunc feugiat mi a tellus consequat imperdiet. Vestibulum sapien. Proin quam. Etiam ultrices. Suspendisse in justo eu magna luctus suscipit. Sed lectus. Integer euismod lacus luctus magna. Quisque cursus, metus vitae pharetra auctor, sem massa mattis sem, at interdum magna augue eget diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Morbi lacinia molestie dui. Praesent blandit dolor. Sed non quam. In vel mi sit amet augue congue elementum. Morbi in ipsum si.";

    /**
     * DOC scorreia Comment method "getSearcher".
     * 
     * @return
     */
    private SynonymIndexSearcher getSearcher() {
        SynonymIndexSearcher searcher = new SynonymIndexSearcher();
        try {
            // searcher.setAnalyzer(builder.getAnalyzer());
            searcher.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        searcher.setTopDocLimit(5);

        return searcher;
    }

    @Test
    public void testSearchInSeveralIndexes() throws IOException {
        printLineToConsole("\n-----------Test SearchInSeveralIndexes----------");

        // assume we have two fields to search
        String row1Company = "ibm";
        String row1Label = "ANPE";

        SynonymIndexSearcher searcher = getSearcher();
        // search
        TopDocs docsField1 = searcher.searchDocumentBySynonym(row1Company);
        printLineToConsole(docsField1.totalHits + " documents found for " + row1Company);

        for (int i = 0; i < docsField1.scoreDocs.length; i++) {
            int docNumber = docsField1.scoreDocs[i].doc;
            printToConsole("\ndoc=" + docNumber + "\tscore=" + docsField1.scoreDocs[i].score);
            // Document doc = builder.getSearcher().doc(docs.scoreDocs[i].doc);
            printLineToConsole("\tword: " + searcher.getWordByDocNumber(docNumber));
            printLineToConsole("\tsynonyms: " + Arrays.toString(searcher.getSynonymsByDocNumber(docNumber)));
        }

        TopDocs docsField2 = searcher.searchDocumentBySynonym(row1Label);
        printLineToConsole(docsField2.totalHits + " documents found for " + row1Label);

        for (int i = 0; i < docsField2.totalHits; i++) {
            int docNumber = docsField2.scoreDocs[i].doc;
            printToConsole("\ndoc=" + docNumber + "\tscore=" + docsField2.scoreDocs[i].score);
            // Document doc = builder.getSearcher().doc(docs.scoreDocs[i].doc);
            printLineToConsole("\tword: " + searcher.getWordByDocNumber(docNumber));
            printLineToConsole("\tsynonyms: " + Arrays.toString(searcher.getSynonymsByDocNumber(docNumber)));
        }

        // build output by a cross product of matched results
        for (int i = 0; i < docsField1.totalHits; i++) {
            int docNumber = docsField1.scoreDocs[i].doc;
            String word1 = searcher.getWordByDocNumber(docNumber);
            for (int j = 0; j < docsField2.totalHits; j++) {
                int docNumber2 = docsField2.scoreDocs[j].doc;
                String word2 = searcher.getWordByDocNumber(docNumber2);
                printLineToConsole("output row = " + word1 + " , " + word2);
            }
        }

        printLineToConsole("   ----------   ");

        // record to search
        String[] record = new String[] { row1Company, row1Label };

        SynonymRecordSearcher recsearcher = new SynonymRecordSearcher(record.length);
        recsearcher.addSearcher(searcher, 0);
        recsearcher.addSearcher(searcher, 1);
        List<OutputRecord> output = recsearcher.search(3, record);
        for (OutputRecord outputRecord : output) {
            printLineToConsole("out= " + outputRecord);
        }

        searcher.close();

        // FIXME add assertions here (or create a junit test for SynonymRecordSearcher instead)

    }

    /**
     * Test method for {@link SynonymIndexSearcher#getSynonymCount(String)}.
     */
    @Test
    public void testGetSynonymCount() {
        SynonymIndexSearcher search = new SynonymIndexSearcher();
        try {
            search.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        doAssertEquals("unexpected synonym count!", 2, search.getSynonymCount("IAIDQ"));
        search.close();
    }

    /**
     * Test method for {@link SynonymIndexSearcher#getDocument(int)}.
     */
    @Test
    public void testGetDocument() {
        SynonymIndexSearcher search = new SynonymIndexSearcher();
        try {
            search.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        TopDocs docs = search.searchDocumentByWord("IAIDQ");
        doAssertEquals(false, docs.totalHits == 0);
        Document document = search.getDocument(docs.scoreDocs[0].doc);
        assertNotNull(document);
        String[] values = document.getValues(SynonymIndexSearcher.F_WORD);
        assertNotNull(values);
        doAssertEquals("unexpected values!", 1, values.length);
        search.close();
    }

    /**
     * Test method for {@link SynonymIndexSearcher#getWordByDocNumber(int)} .
     */
    @Test
    public void testGetWordByDocNumber() {
        SynonymIndexSearcher search = new SynonymIndexSearcher();
        try {
            search.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        String word = search.getWordByDocNumber(0);
        assertNotNull(word);
        assertNotSame(0, word.length());
        // the word found should be one the input words
        boolean wordFound = false;
        for (int i = 0; i < SynonymIndexBuilderTest.synonyms.length; i++) {
            String[] input = SynonymIndexBuilderTest.synonyms[i];
            if (word.equals(input[0])) {
                wordFound = true;
                break;
            }
        }
        assertTrue(wordFound);
        search.close();
    }

    /**
     * Test method for {@link SynonymIndexSearcher#getSynonymsByDocNumber(int)}.
     */
    @Test
    public void testGetSynonymsByDocNumber() {
        SynonymIndexSearcher search = new SynonymIndexSearcher();
        try {
            search.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        String[] syns = search.getSynonymsByDocNumber(0);
        assertNotNull(syns);
        assertNotSame(0, syns.length);
        // the synonyms found should be one the input synonyms
        boolean synonymFound = false;
        for (int i = 0; i < SynonymIndexBuilderTest.synonyms.length; i++) {
            String[] input = SynonymIndexBuilderTest.synonyms[i];
            for (int j = 0; j < syns.length; j++) {
                String syn = syns[j];
                if (input[1].contains(syn)) {
                    synonymFound = true;
                    break;
                }
            }
        }
        assertTrue(synonymFound);
        search.close();
    }

    /**
     * Test method for {@link SynonymIndexSearcher#getNumDocs()}.
     */
    @Test
    public void testGetNumDocs() {
        SynonymIndexSearcher search = new SynonymIndexSearcher();
        try {
            search.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        int numDocs = search.getNumDocs();
        doAssertEquals("unexpected synonym length!", SynonymIndexBuilderTest.synonyms.length, numDocs);
        search.close();
    }

    @Test
    public void testSearchDocumentBySynonymWithNewOptions() throws IOException {
        final String path = "data/quick_brown_dog_index";
        synIdxBuilderTest.setUp();
        SynonymIndexBuilder synonymIdxBuilder = new SynonymIndexBuilder();
        synonymIdxBuilder.deleteIndexFromFS(path);
        synonymIdxBuilder.initIndexInFS(path);
        synIdxBuilderTest.insertDocuments(synonymIdxBuilder, synonyms4newoptions);
        synonymIdxBuilder.closeIndex();

        SynonymIndexSearcher searcher = new SynonymIndexSearcher();
        searcher.setTopDocLimit(30);
        searcher.setMaxEdits(2);
        try {
            searcher.openIndexInFS(path);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        for (String key : ExpectResults4MatchAny.keySet()) {
            printLineToConsole("\n-------------------Searching for <" + key + ">--------------------");

            for (int m = 0; m < 6; m++) {
                SynonymSearchMode mode = SynonymSearchMode.values()[m];
                printLineToConsole(">>>> option: " + mode + "<<<<");
                searcher.setSearchMode(mode);
                TopDocs docs = searcher.searchDocumentBySynonym(key);
                LinkedHashMap<String, Integer[]> expected = ExpectResults4NewOptions.get(mode.toString());
                doAssertEquals("unexpected totalHits size!", expected.get(key).length, docs.totalHits);
                for (int i = 0; i < docs.totalHits; i++) {
                    Document document = searcher.getDocument(docs.scoreDocs[i].doc);
                    String[] syns = document.getValues(SynonymIndexSearcher.F_SYN);
                    printToConsole(docs.scoreDocs[i] + "\n\t" + document.getValues(SynonymIndexSearcher.F_WORD)[0] + " -> ");
                    printLineToConsole(Arrays.asList(syns).toString());
                    doAssertEquals("unexpected hit!", expected.get(key)[i].intValue(), docs.scoreDocs[i].doc);
                }

            }
        }

    }

    public static final String[][] synonyms4newoptions = { { "Dulux Trade", "ABC DEF" }, { "GHI JKL", "Dulux Trade" },
            { "Dulux Trade Red Paint 5L", "DEF ABC" }, { "Trade", "PPP" }, { "Trade Dulux", "PPP" },
            { "IBM", "International Business Machines|Big Blue" }, { "Big Blue", "IBM" }, { "ALMOND/WH", "ALMOND/WHITE" },
            { "The quick brown fox jumps over the lazy dog", "" }, { "The quick brown fox", "" },
            { "The quick brown lazy fox", "" }, { "quick fox", "" }, { "quic fax", "" }, { "quick", "" } };

    public static final LinkedHashMap<String, Integer[]> ExpectResults4MatchAny = new LinkedHashMap<String, Integer[]>() {

        private static final long serialVersionUID = 1L;

        {
            put("Dulux Trade", new Integer[] { 0, 1, 2, 4, 3 });
            put("Trade", new Integer[] { 3, 1, 0, 2, 4 });

            put("Big Blue", new Integer[] { 6, 5 });
            put("Business International", new Integer[] { 5 });
            put("International Business", new Integer[] { 5 });

            put("ALMOND/WH", new Integer[] { 7 });
            put("QUICK FOX", new Integer[] { 11, 8, 9, 10, 13 });
        }
    };

    public static final LinkedHashMap<String, Integer[]> ExpectResults4MatchPartial = new LinkedHashMap<String, Integer[]>() {

        private static final long serialVersionUID = 1L;

        {
            put("Dulux Trade", new Integer[] { 0, 1, 2 });
            put("Trade", new Integer[] { 3, 1, 0, 2, 4 });

            put("Big Blue", new Integer[] { 6, 5 });
            put("Business International", new Integer[] {});
            put("International Business", new Integer[] { 5 });

            put("ALMOND/WH", new Integer[] { 7 });

            put("QUICK FOX", new Integer[] { 11, 8, 9 });
        }
    };

    public static final LinkedHashMap<String, Integer[]> ExpectResults4MatchAll = new LinkedHashMap<String, Integer[]>() {

        private static final long serialVersionUID = 1L;

        {
            put("Dulux Trade", new Integer[] { 0, 1, 2, 4 });
            put("Trade", new Integer[] { 3, 1, 0, 2, 4 });

            put("Big Blue", new Integer[] { 6, 5 });
            put("Business International", new Integer[] { 5 });
            put("International Business", new Integer[] { 5 });

            put("ALMOND/WH", new Integer[] { 7 });

            put("QUICK FOX", new Integer[] { 11, 8, 9, 10 });
        }
    };

    public static final LinkedHashMap<String, Integer[]> ExpectResults4MatchExact = new LinkedHashMap<String, Integer[]>() {

        private static final long serialVersionUID = 1L;

        {
            put("Dulux Trade", new Integer[] { 0, 1 });
            put("Trade", new Integer[] { 3 });

            put("Big Blue", new Integer[] { 6, 5 });
            put("Business International", new Integer[] {});
            put("International Business", new Integer[] {});

            put("ALMOND/WH", new Integer[] { 7 });

            put("QUICK FOX", new Integer[] { 11 });
        }
    };

    public static final LinkedHashMap<String, Integer[]> ExpectResults4MatchAnyFuzzy = new LinkedHashMap<String, Integer[]>() {

        private static final long serialVersionUID = 1L;

        {

            put("Dulux Trade", new Integer[] { 0, 1, 2, 4, 3 });
            put("Trade", new Integer[] { 3, 1, 0, 2, 4 });

            put("Big Blue", new Integer[] { 6, 5, 8 });
            put("Business International", new Integer[] { 5 });
            put("International Business", new Integer[] { 5 });

            put("ALMOND/WH", new Integer[] { 7 });

            put("QUICK FOX", new Integer[] { 12, 11, 8, 9, 10, 13 });
        }
    };

    public static final LinkedHashMap<String, Integer[]> ExpectResults4MatchAllFuzzy = new LinkedHashMap<String, Integer[]>() {

        private static final long serialVersionUID = 1L;

        {
            put("Dulux Trade", new Integer[] { 0, 1, 2, 4 });
            put("Trade", new Integer[] { 3, 1, 0, 2, 4 });

            put("Big Blue", new Integer[] { 6, 5 });
            put("Business International", new Integer[] { 5 });
            put("International Business", new Integer[] { 5 });

            put("ALMOND/WH", new Integer[] { 7 });

            put("QUICK FOX", new Integer[] { 12, 11, 8, 9, 10 });
        }
    };

    public static final LinkedHashMap<String, LinkedHashMap<String, Integer[]>> ExpectResults4NewOptions = new LinkedHashMap<String, LinkedHashMap<String, Integer[]>>() {

        private static final long serialVersionUID = 1L;

        {
            put("MATCH_ANY", ExpectResults4MatchAny);
            put("MATCH_PARTIAL", ExpectResults4MatchPartial);
            put("MATCH_ALL", ExpectResults4MatchAll);
            put("MATCH_EXACT", ExpectResults4MatchExact);
            put("MATCH_ANY_FUZZY", ExpectResults4MatchAnyFuzzy);
            put("MATCH_ALL_FUZZY", ExpectResults4MatchAllFuzzy);
        }
    };

    private void doAssertEquals(String string, int intValue, int doc) {
        if (doAsserts) {
            assertEquals(string, intValue, doc);
        }
    }

    private void doAssertEquals(boolean b, boolean exists) {
        if (doAsserts) {
            assertEquals(b, exists);
        }
    }

    private void doAssertEquals(String string, boolean b, boolean c) {
        if (doAsserts) {
            assertEquals(string, b, c);
        }
    }

    private void printLineToConsole(String text) {
        if (showInConsole) {
            System.out.println(text);
        }
    }

    private void printToConsole(String text) {
        if (showInConsole) {
            System.out.print(text);
        }
    }
}
