package org.talend.dataquality.semantic.api.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.Version;
import org.talend.dataquality.semantic.api.DictionaryConstants;
import org.talend.dataquality.semantic.api.DictionaryUtils;
import org.talend.dataquality.semantic.model.DQCategory;

public class CustomMetadataIndexAccess implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(CustomMetadataIndexAccess.class);

    protected IndexWriter luceneWriter;

    protected DirectoryReader luceneReader;

    protected IndexSearcher luceneSearcher;

    private Directory directory;

    public CustomMetadataIndexAccess(Directory directory) {
        this.directory = directory;
        createSearcher();
        createWriter();
    }

    public Map<String, DQCategory> readCategoryMedatada() {
        Map<String, DQCategory> metadata = new HashMap<>();
        Bits liveDocs = MultiFields.getLiveDocs(luceneReader);
        try {
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
        return metadata;
    }

    public void createCategory(DQCategory category) {
        LOGGER.debug("createCategory: " + category);
        try {
            Document luceneDoc = DictionaryUtils.categoryToDocument(category);

            if (!CollectionUtils.isEmpty(category.getChildren()))
                for (DQCategory child : category.getChildren())
                    luceneDoc.add(new TextField(DictionaryConstants.CHILD, child.getId(), Field.Store.YES));
            luceneWriter.addDocument(luceneDoc);
        } catch (IOException | NullPointerException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void insertOrUpdateCategory(DQCategory category) {
        LOGGER.debug("insertOrUpdateCategory: " + category);
        final Term searchTerm = new Term(DictionaryConstants.ID, category.getId());
        final TermQuery termQuery = new TermQuery(searchTerm);
        try {
            TopDocs result = luceneSearcher.search(termQuery, 1);
            if (result.totalHits == 1) {
                final Term term = new Term(DictionaryConstants.ID, category.getId());
                List<IndexableField> fields = DictionaryUtils.categoryToDocument(category).getFields();
                if (!CollectionUtils.isEmpty(category.getChildren()))
                    for (DQCategory child : category.getChildren())
                        fields.add(new TextField(DictionaryConstants.CHILD, child.getId(), Field.Store.YES));
                luceneWriter.updateDocument(term, fields);
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
            luceneWriter.deleteAll();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void deleteCategory(DQCategory category) {
        LOGGER.debug("deleteCategory: " + category);
        Term luceneId = new Term(DictionaryConstants.ID, category.getId());
        try {
            luceneWriter.deleteDocuments(luceneId);
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
