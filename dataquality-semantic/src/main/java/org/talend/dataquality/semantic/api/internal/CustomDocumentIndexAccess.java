package org.talend.dataquality.semantic.api.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.DictionaryUtils;
import org.talend.dataquality.semantic.index.ClassPathDirectory;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

/**
 * Low-level API for custom data dict lucene index
 */
public class CustomDocumentIndexAccess extends AbstractCustomIndexAccess {

    private static final Logger LOGGER = Logger.getLogger(CustomDocumentIndexAccess.class);

    public CustomDocumentIndexAccess(Directory directory) throws IOException {
        super(directory);
        init();
    }

    private void init() {
        try {
            boolean isLuceneDir = DirectoryReader.indexExists(directory);
            if (!isLuceneDir) {
                LOGGER.debug("Document index is not a lucene index, trying to initialize an empty lucene index ");
                commitChanges();
            }
            mgr = new SearcherManager(directory, null);
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
            final Term term = new Term(DictionarySearcher.F_DOCID, document.getId());
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
            Term luceneId = new Term(DictionarySearcher.F_DOCID, document.getId());
            getWriter().deleteDocuments(luceneId);
        }
    }

    /**
     * @param dqCategory the category to copy
     * copy base documents from shared directory to custom directory
     */
    public void copyBaseDocumentsFromSharedDirectory(DQCategory dqCategory) {
        try {
            Directory srcDir = ClassPathDirectory.open(CategoryRegistryManager.getInstance().getDictionaryURI());
            DirectoryReader reader = DirectoryReader.open(srcDir);
            for (int i = 0; i < reader.maxDoc(); i++) {
                Document doc = reader.document(i);
                String catName = doc.getField(DictionarySearcher.F_WORD).stringValue();
                if (dqCategory.getName().equals(catName)) {
                    getWriter().addDocument(
                            DictionaryUtils.dqDocumentToLuceneDocument(DictionaryUtils.dictionaryEntryFromDocument(doc)));
                }
            }
            commitChanges();
            reader.close();
        } catch (IOException | URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
