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
package org.talend.dataquality.semantic.index;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.talend.dataquality.semantic.model.DQCategory;

public class DictionarySearcher extends AbstractDictionarySearcher {

    private static final Logger LOGGER = Logger.getLogger(DictionarySearcher.class);

    private SearcherManager mgr;

    private Map<String, CachingWrapperFilter> categoryToCache = new HashMap<>();

    /**
     * SynonymIndexSearcher constructor creates this searcher and initializes the index.
     *
     * @param indexPath the path to the index.
     */
    public DictionarySearcher(String indexPath) {
        try {
            FSDirectory indexDir = FSDirectory.open(new File(indexPath));
            mgr = new SearcherManager(indexDir, null);
        } catch (IOException e) {
            LOGGER.error("Unable to open synonym index.", e);
        }
    }

    /**
     * SynonymIndexSearcher constructor creates this searcher and initializes the index.
     *
     * @param indexPathURI the path to the index.
     */
    public DictionarySearcher(URI indexPathURI) {
        try {
            Directory indexDir = ClassPathDirectory.open(indexPathURI);
            mgr = new SearcherManager(indexDir, null);
        } catch (IOException e) {
            LOGGER.error("Unable to open synonym index.", e);
        }
    }

    public DictionarySearcher(Directory indexDir) {
        try {
            mgr = new SearcherManager(indexDir, null);
        } catch (IOException e) {
            LOGGER.error("Unable to open synonym index.", e);
        }
    }

    /**
     * search for documents by one of the synonym (which may be the word).
     *
     * @param stringToSearch
     * @return
     * @throws java.io.IOException
     */
    @Override
    public TopDocs searchDocumentBySynonym(String stringToSearch) throws IOException {
        TopDocs topDocs = null;
        Query query;
        switch (searchMode) {
        case MATCH_SEMANTIC_DICTIONARY:
            query = createQueryForSemanticDictionaryMatch(stringToSearch);
            break;
        case MATCH_SEMANTIC_KEYWORD:
            query = createQueryForSemanticKeywordMatch(stringToSearch);
            break;
        default: // do the same as MATCH_SEMANTIC_DICTIONARY mode
            query = createQueryForSemanticDictionaryMatch(stringToSearch);
            break;
        }
        final IndexSearcher searcher = mgr.acquire();
        topDocs = searcher.search(query, topDocLimit);
        mgr.release(searcher);
        return topDocs;
    }

    /**
     * Get a document from search result by its document number.
     *
     * @param docNum the doc number
     * @return the document (can be null if any problem)
     */
    @Override
    public Document getDocument(int docNum) {
        Document doc = null;
        try {
            final IndexSearcher searcher = mgr.acquire();
            doc = searcher.doc(docNum);
            mgr.release(searcher);
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return doc;
    }

    public boolean validDocumentWithCategories(String stringToSearch, DQCategory semanticType, Set<DQCategory> children)
            throws IOException {
        Query query;
        switch (searchMode) {
        case MATCH_SEMANTIC_DICTIONARY:
            query = createQueryForSemanticDictionaryMatch(stringToSearch);
            break;
        case MATCH_SEMANTIC_KEYWORD:
            query = createQueryForSemanticKeywordMatch(stringToSearch);
            break;
        default: // do the same as MATCH_SEMANTIC_DICTIONARY mode
            query = createQueryForSemanticDictionaryMatch(stringToSearch);
            break;
        }
        final IndexSearcher searcher = mgr.acquire();
        boolean validDocument = false;
        CachingWrapperFilter tmp = categoryToCache.get(semanticType);
        if (tmp == null) {
            if (CollectionUtils.isEmpty(children)) {
                tmp = new CachingWrapperFilter(new FieldCacheTermsFilter(F_CATID, semanticType.getId()));
            } else {
                tmp = new CachingWrapperFilter(new FieldCacheTermsFilter(F_CATID, children.toArray(new String[children.size()])));
            }
            categoryToCache.put(semanticType.getId(), tmp);
        }
        validDocument = searcher.search(query, tmp, 1).totalHits != 0;
        mgr.release(searcher);
        return validDocument;
    }

    /**
     *
     * @param semanticTypes
     * @return
     * @throws IOException
     */
    protected Filter createFilterForSemanticTypes(Set<String> semanticTypes) {
        List<Term> terms = new ArrayList<>();
        for (String semanticType : semanticTypes) {
            terms.add(new Term(F_WORD, semanticType));
        }
        return new TermsFilter(terms);
    }

    /**
     * Method "getWordByDocNumber".
     *
     * @param docNo the document number
     * @return the document or null
     */
    public String getWordByDocNumber(int docNo) {
        Document document = getDocument(docNo);
        return document != null ? document.getValues(F_WORD)[0] : null;
    }

    /**
     * Method "getSynonymsByDocNumber".
     *
     * @param docNo the doc number
     * @return the synonyms or null if no document is found
     */
    public String[] getSynonymsByDocNumber(int docNo) {
        Document document = getDocument(docNo);
        return document != null ? document.getValues(F_SYN) : null;
    }

    /**
     * Method "getNumDocs".
     *
     * @return the number of documents in the index
     */
    public int getNumDocs() {
        try {
            final IndexSearcher searcher = mgr.acquire();
            final int numDocs = searcher.getIndexReader().numDocs();
            mgr.release(searcher);
            return numDocs;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return -1;
    }

    public void close() {
        try {
            mgr.acquire().getIndexReader().close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void maybeRefreshIndex() {
        try {
            mgr.maybeRefresh();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
