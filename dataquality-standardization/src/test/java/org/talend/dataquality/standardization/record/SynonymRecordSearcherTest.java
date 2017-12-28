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
package org.talend.dataquality.standardization.record;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;
import org.talend.dataquality.standardization.index.SynonymIndexBuilder;
import org.talend.dataquality.standardization.index.SynonymIndexBuilderTest;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;
import org.talend.dataquality.standardization.record.SynonymRecordSearcher.RecordResult;
import org.talend.dataquality.standardization.record.SynonymRecordSearcher.WordResult;

/**
 * Test class.
 */
public class SynonymRecordSearcherTest {

    private static final String[][][] WORDRESULTS = { //
            { { "11", "12", "13", "14", "15" }, { "21", "22", "23" }, { "31", "32", "33" }, { "41" } } // always //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
                                                                                                       // at least one
                                                                                                       // match
            , { { "11", "12" }, { "21", "22" }, { "31", "32" } } // several //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                                                                 // matches
            , { {}, { "21", "22" }, { "31", "32" } } // first search does //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                                                     // not match anything
            , { { "11", "12" }, {}, { "31", "32" } } // second search does //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                                                     // not match anything
            , { { "11", "12" }, { "21", "22" }, {} } // last search does //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                                                     // not match anything
            , { { "11", "12" }, {}, {} } // 2 searches did not match //$NON-NLS-1$ //$NON-NLS-2$
            , { {}, {}, {} } // nothing matched at all
            , { { "11", "11" }, { "21", "21" } } // matched are duplicate //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    };

    private static final boolean showInConsole = false;

    @Test
    public void testRecordResultCompute() {
        int nbDup = 0;
        for (String[][] wrs : WORDRESULTS) {
            printLineToConsole(" ###    Testing #### "); //$NON-NLS-1$

            nbDup += testRecordResultCompute(wrs);
        }
        assertEquals("last wordResult array should contain duplicates", 1, nbDup); //$NON-NLS-1$
    }

    private int testRecordResultCompute(String[][] wordresults) {
        int nbDuplicateFound = 0;

        // prepare wordresults
        List<List<WordResult>> wordResults = new ArrayList<List<WordResult>>();
        for (String[] elts : wordresults) {
            List<WordResult> wrs = new ArrayList<WordResult>();
            for (String elt : elts) {
                WordResult wr = new WordResult();
                wr.input = "input " + elt; //$NON-NLS-1$
                wr.word = "word " + elt; //$NON-NLS-1$
                wr.score = Integer.valueOf(elt);
                wrs.add(wr);
            }
            wordResults.add(wrs);
        }

        // --- compute output results as a list
        RecordResult recRes = new RecordResult();
        recRes.record = initializeRecordToSearch(wordresults);
        recRes.wordResults.addAll(wordResults);

        List<OutputRecord> expectedOutputRows = null;
        expectedOutputRows = new ArrayList<OutputRecord>();
        SynonymRecordSearcher.RecordResult.computeOutputRows(wordresults.length, new ArrayList<WordResult>(), recRes.wordResults,
                expectedOutputRows);
        for (OutputRecord outputRecord : expectedOutputRows) {
            printLineToConsole(outputRecord.toString());
        }

        // --- test that duplicates are removed when using a set instead of a list
        Set<OutputRecord> uniques = new HashSet<OutputRecord>();
        uniques.addAll(expectedOutputRows);
        assertTrue(uniques.size() <= expectedOutputRows.size());
        if (uniques.size() < expectedOutputRows.size()) {
            nbDuplicateFound++;
        }

        List<OutputRecord> outputRows = recRes.computeOutputRows();

        // --- check some assertions

        // verify number of results
        int expectedNbOutput = 1;
        for (String[] in : wordresults) {
            expectedNbOutput *= Math.max(in.length, 1);
        }

        assertEquals(expectedNbOutput, expectedOutputRows.size());
        assertTrue(expectedOutputRows.size() >= outputRows.size());

        for (OutputRecord outputRecord : outputRows) {
            boolean found = false;
            for (OutputRecord expectedRecord : expectedOutputRows) {
                if (expectedRecord.equals(outputRecord)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Record not found: " + outputRecord, found); //$NON-NLS-1$
        }
        return nbDuplicateFound;
    }

    /**
     * DOC scorreia Comment method "initializeRecordToSearch".
     * 
     * @param wordresults
     * @return
     */
    private String[] initializeRecordToSearch(String[][] wordresults) {
        Random rnd = new Random();
        String[] init = new String[wordresults.length];
        int i = 0;
        for (String[] wr : wordresults) {
            if (wr.length == 0) {
                init[i] = "AAA"; //$NON-NLS-1$
            } else {
                init[i] = wr[rnd.nextInt(wr.length)];
            }
            i++;
        }
        return init;
    }

    private void initIdx(String path) {
        SynonymIndexBuilder builder = new SynonymIndexBuilder();
        builder.deleteIndexFromFS(path);
        builder.initIndexInFS(path);
        for (String[] synonyms : SynonymIndexBuilderTest.synonyms) {
            try {
                builder.insertDocument(synonyms[0], synonyms[1]);
            } catch (IOException e) {
                e.printStackTrace();
                fail("should not get an exception here"); //$NON-NLS-1$
            }
        }
        builder.closeIndex();
    }

    public void testSearch(String[] record, int topDocLimit, int resultLimit, String fileIndex) {
        SynonymRecordSearcher recSearcher = new SynonymRecordSearcher(record.length);
        for (int i = 0; i < record.length; i++) {
            initIdx(("data/idx") + fileIndex + (i + 1));//$NON-NLS-1$
            final String indexPath = "data/idx" + fileIndex + (i + 1); //$NON-NLS-1$
            SynonymIndexSearcher searcher = new SynonymIndexSearcher(indexPath);
            searcher.setTopDocLimit(topDocLimit);
            recSearcher.addSearcher(searcher, i);
        }

        try {
            TopDocs topDocs;
            int hits = 1;
            for (int i = 0; i < record.length; i++) {
                topDocs = recSearcher.getSearcher(i).searchDocumentBySynonym(record[i]);
                hits *= topDocs.totalHits;
            }

            List<OutputRecord> results = recSearcher.search(resultLimit, record);
            assertNotNull(results);
            assertFalse(results.isEmpty());
            for (OutputRecord outputRecord : results) {
                assertNotNull(outputRecord);
                String[] resultingRecord = outputRecord.getRecord();
                assertEquals(record.length, resultingRecord.length);
                printLineToConsole(StringUtils.join(resultingRecord, '|'));
                printLineToConsole("\t" + outputRecord.getScore()); //$NON-NLS-1$
            }
            assertEquals(Math.min(hits, resultLimit), results.size());

            for (int i = 0; i < record.length; i++) {
                recSearcher.getSearcher(i).close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("should not get an exception here"); //$NON-NLS-1$
        }
        printLineToConsole(""); //$NON-NLS-1$

    }

    @Test
    public void testSearch1() {
        String[] record = { "I.B.M." }; //$NON-NLS-1$
        testSearch(record, 5, 15, "test1"); //$NON-NLS-1$
    }

    @Test
    public void testSearch2() {
        String[] record = { "IBM", "ANpe" }; //$NON-NLS-1$ //$NON-NLS-2$
        testSearch(record, 5, 15, "test2"); //$NON-NLS-1$
    }

    @Test
    public void testSearch3() {
        String[] record = { "IBM", "ANPE", "International" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        testSearch(record, 5, 15, "test3"); //$NON-NLS-1$
        testSearch(record, 5, 100, "test4"); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.record.SynonymRecordSearcher#addSearcher(org.talend.dataquality.standardization.index.SynonymIndexSearcher, int)}
     * .
     */
    @Test
    public void testAddSearcher() {
        SynonymRecordSearcher recSearcher = new SynonymRecordSearcher(2);
        try {
            recSearcher.addSearcher(null, 0);
        } catch (Exception e) {
            assertNotNull("we should get an exception here", e); //$NON-NLS-1$
        }
        try {
            recSearcher.addSearcher(new SynonymIndexSearcher(), 2);
            fail("Index should be out of bounds here: trying to set a searcher at position in an empty array"); //$NON-NLS-1$
        } catch (Exception e) {
            assertNotNull("we should get an exception here", e); //$NON-NLS-1$
        } catch (AssertionError e) {
            assertNotNull("we should get an assertion error here when -ea is added to the VM arguments", e); //$NON-NLS-1$
        }

        try {
            SynonymIndexSearcher searcher = new SynonymIndexSearcher();
            recSearcher.addSearcher(searcher, 1);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            SynonymIndexSearcher searcher = new SynonymIndexSearcher();
            recSearcher.addSearcher(searcher, -1);
        } catch (Exception e) {
            assertNotNull("we should get an exception here", e); //$NON-NLS-1$
        }
    }

    private void printLineToConsole(String text) {
        if (showInConsole) {
            System.out.println(text);
        }
    }
}
