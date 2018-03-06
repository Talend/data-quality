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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

/**
 * @author scorreia
 * 
 * This class searches in a list of indexes.
 */
public class SynonymRecordSearcher {

    private static final Logger LOG = LoggerFactory.getLogger(SynonymRecordSearcher.class);

    private SynonymIndexSearcher[] searchers;

    private int recordSize = 0;

    /**
     * SynonymRecordSearcher constructor.
     * 
     * @param size the size of the record which will be searched
     */
    public SynonymRecordSearcher(int size) {
        this.recordSize = size;
        searchers = new SynonymIndexSearcher[recordSize];
    }

    /**
     * SynonymRecordSearcher represents a match in the index with its score and the input
     */
    static class WordResult {

        /**
         * the input word searched in the index
         */
        String input;

        /**
         * The word found in the index that matches the input
         */
        String word;

        /**
         * The score of the match
         */
        float score;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "WordResult=" + this.input + "->" + this.word + "; score=" + score;//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

    } // EOC WordResult

    /**
     * 
     * @author scorreia
     * 
     * RecordResult: a class that transforms an input record into a list of output records.
     * 
     */
    static class RecordResult {

        // The two following attributes are package protected because of junit tests.
        /**
         * The input record.
         */
        String[] record;

        /**
         * The results of a search.
         */
        final List<List<WordResult>> wordResults = new ArrayList<List<WordResult>>();

        /**
         * Method "computeOutputRows" computes the output rows
         * 
         * @return the list of OutputRecords
         */
        public List<OutputRecord> computeOutputRows() {
            Set<OutputRecord> outputRecords = new HashSet<OutputRecord>();
            computeOutputRows(record.length, new ArrayList<WordResult>(), wordResults, outputRecords);
            return new ArrayList<OutputRecord>(outputRecords);
        }

        /**
         * Method "computeOutputRows".
         * 
         * @param recordLength the record length
         * @param foundWords the list of word result that constitute the begining of the current output record
         * @param wrs the list of remaining word results
         * @param outputRows the output records (updated each time this method is called)
         */
        static void computeOutputRows(int recordLength, List<WordResult> foundWords, List<List<WordResult>> wrs,
                Collection<OutputRecord> outputRows) {
            boolean notEmpty = !wrs.isEmpty();
            assert notEmpty;
            // handle last vector of word results
            if (wrs.size() == 1) {
                List<WordResult> lastWR = wrs.get(0);
                // handle case when no synonym reference has been found
                // this is mainly for robustness and for tests as the search method of SynonymRecordSearcher already
                // handles this case
                if (lastWR.isEmpty()) {
                    outputRows.add(createOutputRecord(recordLength, foundWords, createEmptyWordResult("")));//$NON-NLS-1$
                } else {
                    // handle case when at least one synonym reference has been found (usual case)
                    for (WordResult wordResult : lastWR) {
                        outputRows.add(createOutputRecord(recordLength, foundWords, wordResult));
                    }
                }
            } else { // recusive call on a sublist
                List<WordResult> firstWR = wrs.get(0);
                List<List<WordResult>> sublist = wrs.subList(1, wrs.size());
                // handle case when no synonym reference has been found
                // this is mainly for robustness and for tests as the search method of SynonymRecordSearcher already
                // handles this case
                if (firstWR.isEmpty()) {
                    List<WordResult> wr = new ArrayList<WordResult>(foundWords);
                    wr.add(createEmptyWordResult(""));//$NON-NLS-1$
                    computeOutputRows(recordLength, wr, sublist, outputRows);
                }
                // handle case when at least one synonym reference has been found (usual case)
                for (WordResult wordResult : firstWR) {
                    List<WordResult> wr = new ArrayList<WordResult>(foundWords);
                    wr.add(wordResult);
                    computeOutputRows(recordLength, wr, sublist, outputRows);
                }
            }
        }

        private static OutputRecord createOutputRecord(int recordLength, List<WordResult> foundWords,
                WordResult currentWordResult) {
            OutputRecord outputRec = new OutputRecord(recordLength);
            for (int i = 0; i < foundWords.size(); i++) {
                updateOutputRec(outputRec, i, foundWords.get(i));
            }
            updateOutputRec(outputRec, foundWords.size(), currentWordResult);
            return outputRec;

        }

        private static void updateOutputRec(OutputRecord outputRec, int idx, WordResult wordResult) {
            outputRec.record[idx] = wordResult.word;
            outputRec.score += wordResult.score; // TODO add multiplicative weight here if needed
            outputRec.scores += "|" + wordResult.score;//$NON-NLS-1$
            if (Float.compare(wordResult.score, 0.0F) != 0) {
                outputRec.nbMatch++;
            }
        }
    }

    /**
     * method "search".
     * 
     * @param maxNbOutputResults the number of results to return
     * @param record a list of fields that will be search in the indexes (all fields of the record will be searched)
     * @throws java.io.IOException
     */
    public List<OutputRecord> search(int maxNbOutputResults, String[] record) throws IOException {
        assert record != null;

        RecordResult recRes = new RecordResult();

        // search each field in the appropriate index
        for (int i = 0; i < record.length; i++) {
            List<WordResult> wResults = new ArrayList<WordResult>();
            String field = record[i];

            // if input value is empty, create an empty record
            if (field == null || "".equals(field.trim())) {//$NON-NLS-1$
                wResults.add(createEmptyWordResult(field));
            } else {
                // search this field in one index
                SynonymIndexSearcher searcher = searchers[i];
                TopDocs docs = searcher.searchDocumentBySynonym(field);

                int nbDocs = Math.min(searcher.getTopDocLimit(), docs.totalHits);

                // store all found words in a list of results for this field
                for (int j = 0; j < nbDocs; j++) {
                    ScoreDoc scoreDoc = docs.scoreDocs[j];
                    // MOD sizhaoliu TDQ-3608 filter the results with matching threshold
                    if (scoreDoc.score > searcher.getMatchingThreshold()) {
                        String foundWord = searcher.getWordByDocNumber(scoreDoc.doc);
                        WordResult wordRes = new WordResult();
                        wordRes.input = field;
                        wordRes.word = foundWord;
                        wordRes.score = scoreDoc.score;
                        wResults.add(wordRes);
                    }
                }
                // handle case when nothing is found in the index
                if (wResults.isEmpty()) {
                    WordResult wordRes = createEmptyWordResult(field);
                    wResults.add(wordRes);
                }
            }

            // create
            recRes.record = record;
            recRes.wordResults.add(wResults);
        }
        List<OutputRecord> outputRecords = recRes.computeOutputRows();
        Collections.sort(outputRecords);
        int tempMaxNbOutputResults = Math.min(outputRecords.isEmpty() ? 0 : outputRecords.size(), maxNbOutputResults);
        return outputRecords.subList(0, tempMaxNbOutputResults);
    }

    /**
     * Method "addSearcher" add a searcher to the list of searchers at the given column index.
     * 
     * @param searcher the searcher
     * @param columnIndex the column index
     */
    public void addSearcher(SynonymIndexSearcher searcher, int columnIndex) {
        assert columnIndex < this.recordSize;
        if (searcher == null) {
            LOG.error("SynonymRecordSearcher.tried" + columnIndex);
        }
        searchers[columnIndex] = searcher;
    }

    /**
     * Method "getSearcher".
     * 
     * @param columnIndex the column index
     * @return the SynonymIndexSearcher added at this column index.
     */
    public SynonymIndexSearcher getSearcher(int columnIndex) {
        return searchers[columnIndex];
    }

    private static WordResult createEmptyWordResult(String input) {
        WordResult emptyWordResult = new WordResult();
        emptyWordResult.input = input;
        emptyWordResult.score = 0;
        emptyWordResult.word = null;
        return emptyWordResult;
    }
}
