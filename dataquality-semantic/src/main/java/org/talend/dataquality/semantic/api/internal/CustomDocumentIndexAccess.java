package org.talend.dataquality.semantic.api.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.api.DictionaryUtils;
import org.talend.dataquality.semantic.model.DQDocument;

public class CustomDocumentIndexAccess extends AbstractCustomIndexAccess {

    private static final Logger LOGGER = Logger.getLogger(CustomDocumentIndexAccess.class);

    public CustomDocumentIndexAccess(Directory directory) throws IOException {
        super(directory);
        init();
    }

    private void init() {
        LOGGER.debug("Metadata index is not readable, trying to make a copy from shared metadata.");
        try {
            getWriter().deleteAll();
            commitChangesAndCloseWriter();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * add the documents to the lucene index and share it on HDFS
     *
     * @param documents the documents to add
     */
    public void createDocument(List<DQDocument> documents) {
        try {
            for (DQDocument document : documents) {
                LOGGER.debug("createDocument " + document);
                getWriter().addDocument(DictionaryUtils.dqDocumentToLuceneDocument(document));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * update the documents in the lucene index and share it on HDFS
     *
     * @param documents the document to update
     */
    public void insertOrUpdateDocument(List<DQDocument> documents) throws IOException {
        for (DQDocument document : documents) {
            final Term term = new Term("docid", document.getId());
            IndexSearcher searcher = mgr.acquire();
            if (searcher.search(new TermQuery(term), 1).totalHits == 1) {
                LOGGER.debug("updateDocument " + document);
                getWriter().updateDocument(term, DictionaryUtils.dqDocumentToLuceneDocument(document).getFields());
            } else {
                createDocument(Arrays.asList(document));
            }
            mgr.release(searcher);
        }
    }

    /**
     * delete the documents in the lucene index
     *
     * @param documents the documents to update
     */
    public void deleteDocument(List<DQDocument> documents) throws IOException {
        for (DQDocument document : documents) {

            LOGGER.debug("deleteDocument " + document);
            Term luceneId = new Term("docid", document.getId());
            getWriter().deleteDocuments(luceneId);
        }
    }

}
