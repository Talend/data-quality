package org.talend.dataquality.semantic.api.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.talend.dataquality.semantic.api.DictionaryUtils;
import org.talend.dataquality.semantic.model.DQDocument;

public class CustomDocumentIndexAccess implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(CustomDocumentIndexAccess.class);

    protected IndexWriter luceneWriter;

    protected DirectoryReader luceneReader;

    protected IndexSearcher luceneSearcher;

    private Directory directory;

    public CustomDocumentIndexAccess(Directory directory) throws IOException {
        this.directory = directory;
        createSearcher();
        createWriter();
    }

    /**
     * add the documents to the lucene index and share it on HDFS
     *
     * @param documents the documents to add
     */
    protected void createDocument(List<DQDocument> documents) throws IOException {
        for (DQDocument document : documents) {
            LOGGER.debug("createDocument " + document);
            luceneWriter.addDocument(DictionaryUtils.dqDocumentToLuceneDocument(document));

        }
    }

    /**
     * update the documents in the lucene index and share it on HDFS
     *
     * @param documents the document to update
     */
    private void insertOrUpdateDocument(List<DQDocument> documents) throws IOException {
        for (DQDocument document : documents) {
            final Term term = new Term("docid", document.getId());
            if (luceneSearcher.search(new TermQuery(term), 1).totalHits == 1) {
                LOGGER.debug("updateDocument " + document);
                luceneWriter.updateDocument(term, DictionaryUtils.dqDocumentToLuceneDocument(document).getFields());
            } else {
                createDocument(Arrays.asList(document));
            }

        }
    }

    /**
     * delete the documents in the lucene index
     *
     * @param documents the documents to update
     */
    private void deleteDocument(List<DQDocument> documents) throws IOException {
        for (DQDocument document : documents) {

            LOGGER.debug("deleteDocument " + document);
            Term luceneId = new Term("docid", document.getId());
            luceneWriter.deleteDocuments(luceneId);
        }
    }

    public void deleteAll() {
        LOGGER.debug("delete all content");
        try {
            luceneWriter.deleteAll();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Create the index writer
     */
    private void createWriter() {
        if (luceneWriter == null) {
            try {
                final Analyzer analyzer = new StandardAnalyzer();
                final IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                iwc.setWriteLockTimeout(5000000);
                luceneWriter = new IndexWriter(directory, iwc);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Create the index searcher
     */
    private void createSearcher() {
        if (luceneSearcher == null) {
            try {
                luceneReader = DirectoryReader.open(directory);
                luceneSearcher = new IndexSearcher(luceneReader);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (luceneWriter != null) {
            try {
                luceneWriter.commit();
                luceneWriter.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (luceneReader != null) {
            try {
                luceneReader.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (directory != null) {
            directory.close();
        }
    }

    public void commitChanges() {
        if (luceneWriter != null) {
            try {
                luceneWriter.commit();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
