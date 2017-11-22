package org.talend.dataquality.semantic.api.internal;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.DictionaryUtils;
import org.talend.dataquality.semantic.index.ClassPathDirectory;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
        List<Document> luceneDocuments = new ArrayList<>();
        for (DQDocument document : documents)
            luceneDocuments.add(DictionaryUtils.dqDocumentToLuceneDocument(document));
        LOGGER.debug("createDocuments " + documents);
        try {
            getWriter().addDocuments(luceneDocuments);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * update the documents in the lucene index and share it on HDFS
     *
     * @param documents the document to update
     */
    public void insertOrUpdateDocument(List<DQDocument> documents) {
        List<DQDocument> documentsToCreate = new ArrayList<>();
        try {
            for (DQDocument document : documents) {
                final Term term = new Term(DictionarySearcher.F_DOCID, document.getId());
                IndexSearcher searcher = mgr.acquire();
                if (searcher.search(new TermQuery(term), 1).totalHits == 1) {
                    LOGGER.debug("updateDocument " + document);
                    getWriter().updateDocument(term, DictionaryUtils.dqDocumentToLuceneDocument(document).getFields());
                } else {
                    documentsToCreate.add(document);
                }
                mgr.release(searcher);
            }
            createDocument(documentsToCreate);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * delete the documents in the lucene index
     *
     * @param documents the documents to update
     */
    public void deleteDocument(List<DQDocument> documents) {
        for (DQDocument document : documents) {
            LOGGER.debug("deleteDocument " + document);
            Term luceneId = new Term(DictionarySearcher.F_DOCID, document.getId());
            try {
                getWriter().deleteDocuments(luceneId);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public void deleteDocumentsByCategoryId(String categoryId) {
        LOGGER.debug("deleteDocuments by categoryId " + categoryId);
        Term luceneId = new Term(DictionarySearcher.F_CATID, categoryId);
        try {
            getWriter().deleteDocuments(luceneId);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @param dqCategory the category to copy
     * copy base documents from shared directory to custom directory
     */
    public void copyBaseDocumentsFromSharedDirectory(DQCategory dqCategory) {
        try (Directory srcDir = ClassPathDirectory.open(CategoryRegistryManager.getInstance().getDictionaryURI());
                DirectoryReader reader = DirectoryReader.open(srcDir)) {
            IndexSearcher sharedLuceneDocumentSearcher = new IndexSearcher(reader);
            final Term searchTerm = new Term(DictionarySearcher.F_CATID, dqCategory.getId());
            for (ScoreDoc d : sharedLuceneDocumentSearcher.search(new TermQuery(searchTerm), Integer.MAX_VALUE).scoreDocs) {
                //useful to add the synterm field in lucene index
                DQDocument document = DictionaryUtils.dictionaryEntryFromDocument(sharedLuceneDocumentSearcher.doc(d.doc));
                getWriter().addDocument(DictionaryUtils.dqDocumentToLuceneDocument(document));
            }
            commitChanges();
        } catch (IOException | URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
