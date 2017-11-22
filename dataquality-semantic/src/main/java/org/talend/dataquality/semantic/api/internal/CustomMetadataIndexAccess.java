package org.talend.dataquality.semantic.api.internal;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.api.CategoryMetadataUtils;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.DictionaryUtils;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.DQCategory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Low-level API for metadata lucene index.
 */
public class CustomMetadataIndexAccess extends AbstractCustomIndexAccess {

    private static final Logger LOGGER = Logger.getLogger(CustomMetadataIndexAccess.class);

    public CustomMetadataIndexAccess(Directory directory) {
        super(directory);
        init();
    }

    private void init() {
        try {
            boolean isLuceneDir = DirectoryReader.indexExists(directory);
            if (!isLuceneDir || getReader().maxDoc() == 0) {
                LOGGER.debug("Metadata index is not a lucene index or is empty, trying to make a copy from shared metadata.");
                for (DQCategory dqCat : CategoryRegistryManager.getInstance().getSharedCategoryMetadata().values()) {
                    createCategory(dqCat);
                }
                commitChanges();
            }
            mgr = new SearcherManager(directory, null);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public Map<String, DQCategory> readCategoryMedatada() {
        return CategoryMetadataUtils.loadMetadataFromIndex(directory);
    }

    public void createCategory(DQCategory category) {
        LOGGER.debug("createCategory: " + category);
        try {
            Document luceneDoc = DictionaryUtils.categoryToDocument(category);
            getWriter().addDocument(luceneDoc);
        } catch (IOException | NullPointerException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void insertOrUpdateCategory(DQCategory category) {
        LOGGER.debug("insertOrUpdateCategory: " + category);
        final Term searchTerm = new Term(DictionarySearcher.F_CATID, category.getId());
        final TermQuery termQuery = new TermQuery(searchTerm);
        try {
            IndexSearcher searcher = mgr.acquire();
            TopDocs result = searcher.search(termQuery, 1);
            mgr.release(searcher);
            if (result.totalHits == 1) {
                final Term term = new Term(DictionarySearcher.F_CATID, category.getId());
                List<IndexableField> fields = DictionaryUtils.categoryToDocument(category).getFields();
                getWriter().updateDocument(term, fields);
            } else {
                createCategory(category);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    public void deleteCategory(DQCategory category) {
        LOGGER.debug("deleteCategory: " + category);
        Term luceneId = new Term(DictionarySearcher.F_CATID, category.getId());
        try {
            getWriter().deleteDocuments(luceneId);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
