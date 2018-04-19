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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.junit.Before;
import org.junit.Test;

/**
 * DOC scorreia class global comment. Detailled comment
 * 
 */
public class SynonymIndexBuilderTest {

    // The abosolute path will be "path/to/svn/top/org.talend.dataquality.standardization.test/data/index
    static String path = "data/index";

    /**
     * ATTENTION: Be careful when changing this list of synonyms, they are also use in SynonymIndexSearcherTest.
     */
    public static String[][] synonyms = { { "I.B.M.", "IBM|International Business Machines|Big Blue" },
            { "IRTY", "IBM|International Business Machines" }, { "ISDF", "IBM|International Business Machines|Big Blue" },
            { "ANPE", "A.N.P.E.|Agence Nationale Pour l'Emploi|Pôle Emploi" },
            { "TEST", "A.N.P.E.|Agence Nationale Pour l'Emploi|Pôle Emploi" }, { "Sécurité Sociale", "Sécu|SS|CPAM" },
            { "IAIDQ", "International Association for Information & Data Quality|Int. Assoc. Info & DQ" }, };

    private static final boolean showInConsole = false;

    // private SynonymIndexBuilder builder;

    @Before
    public void setUp() {
        // clear any existing files
        File folder = new File(path);
        boolean deleteSuc = true;
        if (folder.exists()) {
            for (File f : folder.listFiles()) {
                if (f.delete() == false) {
                    deleteSuc = false;
                    break;
                }
            }
            if (!deleteSuc) {
                path = path + "1"; //$NON-NLS-1$
                setUp();
            }
        }

    }

    private void removePhisically(String filePath) {
        File folder = new File(filePath);
        if (folder.exists()) {
            for (File f : folder.listFiles()) {
                f.delete();
            }
            folder.delete();
        }
    }

    SynonymIndexSearcher getSearcher(SynonymIndexBuilder builder) {
        SynonymIndexSearcher searcher = new SynonymIndexSearcher();
        try {
            searcher.setAnalyzer(builder.getAnalyzer());
            searcher.openIndexInFS(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        searcher.setTopDocLimit(5);
        return searcher;
    }

    @Override
    public void finalize() throws Exception {
        //
        // try {
        // builder.closeIndex();
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    void insertDocuments(SynonymIndexBuilder builder) throws IOException {
        insertDocuments(builder, synonyms);
    }

    void insertDocuments(SynonymIndexBuilder builder, String[][] synonyms) throws IOException {
        for (String[] syns : synonyms) {
            builder.insertDocument(syns[0], syns[1]);
        }
        builder.commit();
    }

    private SynonymIndexBuilder createNewIndexBuilder(String p) {
        SynonymIndexBuilder builder = new SynonymIndexBuilder();
        builder.initIndexInFS(p);
        return builder;
    }

    @Test
    public void testInsertDocumentIfNotExists() throws Exception {
        printLineToConsole("\n---------------Test addDocument------------------");
        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);

        // this.testInsertDocuments();// insert documents first
        builder.insertDocumentIfNotExists("ADD", "This|is|a|new|document");
        builder.commit();
        SynonymIndexSearcher searcher = getSearcher(builder);
        assertEquals(synonyms.length + 1, searcher.getNumDocs());

        builder.insertDocumentIfNotExists("ANPE", "This|is|an|existing|document");
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        searcher.close();
        assertEquals(synonyms.length + 1, searcher.getNumDocs());

        builder.insertDocumentIfNotExists("Irish Bar Managers", "IBM");
        builder.commit();

        searcher = getSearcher(builder);
        assertEquals(synonyms.length + 2, searcher.getNumDocs());
        searcher.close();
        builder.closeIndex();
    }

    @Test
    public void testInsertDocuments() throws Exception {
        printLineToConsole("\n---------------Test insertDocuments--------------");

        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);

        SynonymIndexSearcher searcher = getSearcher(builder);
        assertEquals(synonyms.length, searcher.getNumDocs());
        builder.closeIndex();
        searcher.close();
    }

    @Test
    public void testUpdateSynonymDocument() throws Exception {
        printLineToConsole("\n---------------Test updateDocument---------------");
        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);

        SynonymIndexSearcher searcher = getSearcher(builder);
        assertEquals(0, searcher.searchDocumentBySynonym("updated").totalHits);

        builder.updateDocument("Sécurité Sociale", "I|have|been|updated");
        builder.commit();
        // close previous searcher
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(1, searcher.searchDocumentBySynonym("updated").totalHits);

        builder.updateDocument("INEXIST", "I|don't|exist");
        builder.commit();
        // close previous searcher
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(0, searcher.searchDocumentBySynonym("exist").totalHits);
        // close builders and searchers that are not used later
        builder.closeIndex();
        searcher.close();
    }

    @Test
    public void testUpdateSynonymDocument2() throws Exception {
        printLineToConsole("\n---------------Test updateDocument2---------------");
        // --- create a new index with several similar documents
        SynonymIndexBuilder synIdxBuild = new SynonymIndexBuilder();
        String idxPath = "data/test_update";
        removePhisically(idxPath);
        synIdxBuild.deleteIndexFromFS(idxPath);
        synIdxBuild.initIndexInFS(idxPath);
        int maxDoc = 4;
        String word = "salut";
        for (int i = 0; i < maxDoc; i++) {
            synIdxBuild.insertDocument(word, "synonym|toto");
        }
        String toupdate = "The document to update.";
        synIdxBuild.insertDocument(toupdate, "this document will be updated");
        int nbDocInIndex = maxDoc + 1;
        assertEquals(nbDocInIndex, synIdxBuild.getNumDocs());
        synIdxBuild.commit();

        int nbUpdatedDocuments = synIdxBuild.updateDocument("unknown", "new syn");
        assertEquals("there should be no document to update", 0, nbUpdatedDocuments);
        assertEquals("The document should not be inserted here", nbDocInIndex, synIdxBuild.getNumDocs());

        nbUpdatedDocuments = synIdxBuild.updateDocument(word, "new syn");
        assertEquals("no update should be done because several documents match the word " + word, -1, nbUpdatedDocuments);

        nbUpdatedDocuments = synIdxBuild.updateDocument(toupdate, "a new list of 3 synonyms|test|ok");
        assertEquals("One document should be updated", 1, nbUpdatedDocuments);

        synIdxBuild.commit();
        synIdxBuild.closeIndex();

        SynonymIndexSearcher search = new SynonymIndexSearcher();
        search.setTopDocLimit(maxDoc); // retrieve all possible documents
        search.openIndexInFS(idxPath);
        TopDocs salutDocs = search.searchDocumentByWord(word);
        assertEquals(maxDoc, salutDocs.totalHits);
        for (ScoreDoc scoreDoc : salutDocs.scoreDocs) {
            Document document = search.getDocument(scoreDoc.doc);

            // [M]assertion removed: the order of synonyms is not important
            // -sizhaoliu 08 Sep 2011
            // String syn = document.get(SynonymIndexSearcher.F_SYN);
            // assertEquals("the first synonym field should be the same as the word (after being analyzed)", word, syn);

            String[] word_values = document.getValues(SynonymIndexSearcher.F_WORD);
            String[] syn_values = document.getValues(SynonymIndexSearcher.F_SYN);
            // expect to see "synonym" and "toto"
            assertEquals(Arrays.asList(syn_values).toString(), 2, syn_values.length);

            List<String> valueList = Arrays.asList(word_values);
            assertTrue(valueList.contains(word));

            valueList = Arrays.asList(syn_values);
            assertTrue(valueList.contains("synonym"));
            assertTrue(valueList.contains("toto"));
        }

        TopDocs updatedDocs = search.searchDocumentByWord(toupdate);

        assertEquals("there should be only 1 document after the update", 1, updatedDocs.totalHits);
        for (ScoreDoc scoreDoc : updatedDocs.scoreDocs) {
            Document document = search.getDocument(scoreDoc.doc);

            // [M]assertion removed: the order of synonyms is not important
            // -sizhaoliu 08 Sep 2011
            // String syn = document.get(SynonymIndexSearcher.F_SYN);
            // assertEquals("the first synonym field should be the same as the word (after being analyzed)", toupdate,
            // syn);

            String[] word_values = document.getValues(SynonymIndexSearcher.F_WORD);
            String[] values = document.getValues(SynonymIndexSearcher.F_SYN);
            // expect to see "salut" and "synonym" and "toto"
            assertEquals("there should be 3 synonyms", 3, values.length);

            List<String> valueList = Arrays.asList(values);
            assertTrue(Arrays.asList(word_values).contains(toupdate));
            assertTrue(valueList.contains("a new list of 3 synonyms"));
            assertTrue(valueList.contains("test"));
            assertTrue(valueList.contains("ok"));
        }
        search.close();
    }

    @Test
    public void testDeleteDocumentByWord() throws IOException {
        printLineToConsole("\n---------------Test deleteDocument---------------");
        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);
        SynonymIndexSearcher searcher = getSearcher(builder);
        int docCount = searcher.getNumDocs();

        assertEquals(1, searcher.searchDocumentByWord("IAIDQ").totalHits);
        searcher.close();

        // the word to delete should be precise and case sensitive.
        // builder.deleteDocumentByWord("iaidq");
        // builder.commit();
        //
        // searcher = getSearcher(builder);
        // assertEquals(docCount, searcher.getNumDocs());
        // searcher.close();

        builder.deleteDocumentByWord("IAIDQ");
        builder.commit();

        searcher = getSearcher(builder);
        assertEquals(docCount - 1, searcher.getNumDocs());
        searcher.close();

        builder.deleteDocumentByWord("random");
        builder.commit();

        searcher = getSearcher(builder);
        assertEquals(docCount - 1, searcher.getNumDocs());
        // close previous searcher
        searcher.close();
        builder.closeIndex();

    }

    @Test
    public void testAddSynonymToWord() throws IOException {

        printLineToConsole("\n---------------Test addSynonymToWord-------------");
        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);
        SynonymIndexSearcher searcher = getSearcher(builder);
        assertEquals(0, searcher.searchDocumentBySynonym("another").totalHits);

        int originalSynonymCount = searcher.getSynonymCount("ANPE");
        int addedSynonymToDocument = builder.addSynonymToDocument("ANPE", "Another synonym of ANPE");
        builder.commit();
        searcher.close();
        assertEquals("1 new synonym should be appended to the list.", 1, addedSynonymToDocument);

        searcher = getSearcher(builder);
        assertEquals(1, searcher.searchDocumentBySynonym("another").totalHits);
        assertEquals(++originalSynonymCount, searcher.getSynonymCount("ANPE"));

        addedSynonymToDocument = builder.addSynonymToDocument("ANPE", "Anpe");
        builder.commit();
        searcher.close();
        assertEquals("anpe already exists, no synonym should be appended to the list.", 0, addedSynonymToDocument);

        searcher = getSearcher(builder);
        assertEquals(originalSynonymCount, searcher.getSynonymCount("ANPE"));

        builder.addSynonymToDocument("ANPEEEE", "A.N.P.E");
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(0, searcher.searchDocumentByWord("ANPEEEE").totalHits);
        searcher.close();
        builder.closeIndex();
    }

    @Test
    public void testRemoveSynonymFromWord() throws IOException {
        printLineToConsole("\n---------------Test removeSynonymFromWord-----------");
        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);

        SynonymIndexSearcher searcher = getSearcher(builder);
        int synonymCount = searcher.getSynonymCount("ANPE");
        // the synonym to delete should be precise and case sensitive
        int removed = builder.removeSynonymFromDocument("ANPE", "Agence Nationale Pour l'Emploi");
        assertEquals(1, removed);
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(--synonymCount, searcher.getSynonymCount("ANPE"));

        removed = builder.removeSynonymFromDocument("ANPE", "Anpe");
        assertEquals(0, removed);
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(synonymCount, searcher.getSynonymCount("ANPE"));

        removed = builder.removeSynonymFromDocument("ANPE", "A.N.P.E.");
        assertEquals(1, removed);
        removed = builder.removeSynonymFromDocument("ANPE", "A.N.P.E.");
        assertEquals("We did not commit, so we still should find a synonym to delete here", 1, removed);
        builder.commit();
        removed = builder.removeSynonymFromDocument("ANPE", "A.N.P.E.");
        assertEquals(0, removed);
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(--synonymCount, searcher.getSynonymCount("ANPE"));

        removed = builder.removeSynonymFromDocument("ANPE", "Pôle Emploi");
        assertEquals(1, removed);
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(--synonymCount, searcher.getSynonymCount("ANPE"));

        removed = builder.removeSynonymFromDocument("ANPEEEE", "A.N.P.E");
        assertEquals(0, removed);
        searcher.close();
        builder.closeIndex();
    }

    @Test
    public void testDeleteAllDocuments() throws IOException {
        printLineToConsole("\n---------------Test deleteAllDocuments----------");

        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);
        builder.deleteAllDocuments();
        assertEquals(0, builder.getWriter().numDocs());
        SynonymIndexSearcher searcher = getSearcher(builder);
        assertEquals("A searcher should still see the documents as no commit has been done yet", false,
                searcher.getNumDocs() == 0);
        builder.commit();
        assertEquals(
                "The previous searcher should still see the documents as it still has a reader on the indexs before the commit has been done",
                false, searcher.getNumDocs() == 0);
        // builder.closeIndex();
        searcher.close();

        searcher = getSearcher(builder);

        assertEquals("A new searcher should not see the documents anymore as a commit has been done", true,
                searcher.getNumDocs() == 0);
        searcher.close();
        builder.closeIndex();
    }

    @Test
    public void deleteIndexFromFS() throws IOException {
        printLineToConsole("\n---------------Test deleteIndexFromFS----------");
        String indexPath = "data/index2";
        SynonymIndexBuilder synonymIndexBuilder = new SynonymIndexBuilder();
        synonymIndexBuilder.initIndexInFS(indexPath);
        File indexfile = new File(indexPath);
        assertEquals(true, indexfile.exists());

        // TODO test with lock?
        synonymIndexBuilder.insertDocument("salut", "toto");
        synonymIndexBuilder.commit();
        synonymIndexBuilder.closeIndex();

        synonymIndexBuilder.deleteIndexFromFS(indexPath);
        // assertEquals(synonymIndexBuilder.getError().getMessage(), true, deleteIndexFromFS);
        assertEquals(false, indexfile.exists());
    }

    @Test
    public void initIndexInFS() throws IOException {
        String indexPath = "data/index3";
        SynonymIndexBuilder synonymIndexBuilder = new SynonymIndexBuilder();
        synonymIndexBuilder.initIndexInFS(indexPath);
        synonymIndexBuilder.insertDocument("salut", "toto");
        synonymIndexBuilder.commit();

        SynonymIndexSearcher searcher = new SynonymIndexSearcher(indexPath);
        int numDocs = searcher.getNumDocs();

        // check that two calls of initIndexInFS does not reset the index.
        synonymIndexBuilder.initIndexInFS(indexPath);
        synonymIndexBuilder.insertDocument("bye", "au revoir");
        synonymIndexBuilder.commit();

        // get a new searcher because the previous is open on the index when it contained only one document.
        SynonymIndexSearcher searcher2 = new SynonymIndexSearcher(indexPath);
        assertEquals(numDocs + 1, searcher2.getNumDocs());

        synonymIndexBuilder.closeIndex();
        searcher.close();
        searcher2.close();
        // when OS is windows delete the index will failed at here so that pass this test
        String os = System.getProperties().getProperty("os.name");
        if (!os.startsWith("win") && !os.startsWith("Win")) {
            boolean deleted = synonymIndexBuilder.deleteIndexFromFS(indexPath);
            assertEquals(true, deleted);
        }
    }

    private void printLineToConsole(String text) {
        if (showInConsole) {
            System.out.println(text);
        }
    }
}
