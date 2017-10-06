package org.talend.dataquality.semantic.api.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.DictionaryConstants;
import org.talend.dataquality.semantic.api.DictionaryUtils;
import org.talend.dataquality.semantic.model.DQCategory;

public class CustomMetadataIndexAccess extends AbstractCustomIndexAccess {

    private static final Logger LOGGER = Logger.getLogger(CustomMetadataIndexAccess.class);

    public CustomMetadataIndexAccess(Directory directory) {
        super(directory);
        init();
    }

    private void init() {
        LOGGER.debug("Metadata index is not readable, trying to make a copy from shared metadata.");
        try {
            if (getWriter().maxDoc() == 0) {
                for (DQCategory dqCat : CategoryRegistryManager.getInstance().getSharedCategoryMetadata().values()) {
                    createCategory(dqCat);
                }
                commitChangesAndCloseWriter();
            }
            mgr = new SearcherManager(directory, null);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public Map<String, DQCategory> readCategoryMedatada() {
        Map<String, DQCategory> metadata = new HashMap<>();
        try {
            luceneReader = getReader();
            Bits liveDocs = MultiFields.getLiveDocs(luceneReader);
            for (int i = 0; i < luceneReader.maxDoc(); i++) {
                if (liveDocs != null && !liveDocs.get(i)) {
                    continue;
                }
                Document doc = luceneReader.document(i);
                DQCategory entry = DictionaryUtils.categoryFromDocument(doc);
                metadata.put(entry.getId(), entry);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        closeReader();
        return metadata;
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
        final Term searchTerm = new Term(DictionaryConstants.ID, category.getId());
        final TermQuery termQuery = new TermQuery(searchTerm);
        try {
            IndexSearcher searcher = mgr.acquire();
            TopDocs result = searcher.search(termQuery, 1);
            mgr.release(searcher);
            if (result.totalHits == 1) {
                final Term term = new Term(DictionaryConstants.ID, category.getId());
                List<IndexableField> fields = DictionaryUtils.categoryToDocument(category).getFields();
                if (!CollectionUtils.isEmpty(category.getChildren()))
                    for (DQCategory child : category.getChildren())
                        fields.add(new TextField(DictionaryConstants.CHILD, child.getId(), Field.Store.YES));
                getWriter().updateDocument(term, fields);
            } else {
                createCategory(category);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    public void deleteAll() {
        LOGGER.debug("delete all content");
        try {
            getWriter().deleteAll();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void deleteCategory(DQCategory category) {
        LOGGER.debug("deleteCategory: " + category);
        Term luceneId = new Term(DictionaryConstants.ID, category.getId());
        try {
            getWriter().deleteDocuments(luceneId);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
