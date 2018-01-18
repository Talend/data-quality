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
package org.talend.dataquality.semantic.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.index.ClassPathDirectory;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

/**
 * API for dictionary value suggestion.
 */
public class LocalDictionaryCache {

    private static final Logger LOGGER = Logger.getLogger(LocalDictionaryCache.class);

    private SearcherManager sharedSearcherManager;

    private SearcherManager customSearcherMananger;

    private CustomDictionaryHolder customDictionaryHolder;

    LocalDictionaryCache(CustomDictionaryHolder customDictionaryHolder) {
        this.customDictionaryHolder = customDictionaryHolder;
        try {
            URI ddPath = CategoryRegistryManager.getInstance().getDictionaryURI();
            Directory sharedDir = ClassPathDirectory.open(ddPath);
            sharedSearcherManager = new SearcherManager(sharedDir, null);

            initCustomDirectory();
        } catch (IOException e) {
            LOGGER.error("Failed to read local dictionary cache! ", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to parse index URI! ", e);
        }
    }

    private void initCustomDirectory() {
        try {
            Directory customDir = customDictionaryHolder.getDataDictDirectory();
            if (customDir != null) {
                customSearcherMananger = new SearcherManager(customDir, null);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read local dictionary cache! ", e);
        }
    }

    private List<DQDocument> dqDocListFromTopDocs(String catId, String catName, TopDocs docs, boolean searchInCustom)
            throws IOException {
        SearcherManager searcherManager = getSearcherManager(searchInCustom);
        searcherManager.maybeRefresh();
        IndexSearcher searcher = searcherManager.acquire();
        IndexReader reader = searcher.getIndexReader();
        List<DQDocument> dqDocList = new ArrayList<>();
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document luceneDoc = reader.document(scoreDoc.doc);
            DQDocument dqDoc = DictionaryUtils.dictionaryEntryFromDocument(luceneDoc, catId, catName);
            dqDocList.add(dqDoc);
        }
        searcherManager.release(searcher);
        return dqDocList;
    }

    /**
     * get a list of DQDocument of all dictionary entries.
     */
    public List<DQDocument> listDocuments(String categoryName, int offset, int n) {
        try {
            DQCategory dqCat = customDictionaryHolder.getCategoryMetadataByName(categoryName);
            boolean isCategoryModified = dqCat.getModified();
            TopDocs docs = sendListDocumentsQuery(dqCat.getId(), offset, n, isCategoryModified);
            return dqDocListFromTopDocs(dqCat.getId(), dqCat.getName(), docs, isCategoryModified);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private Query getListDocumentsQuery(String categoryId) {
        return new TermQuery(new Term(DictionarySearcher.F_CATID, categoryId));
    }

    private TopDocs sendListDocumentsQuery(String categoryId, int offset, int n, boolean searchInCustom) throws IOException {
        SearcherManager searcherManager = getSearcherManager(searchInCustom);
        searcherManager.maybeRefresh();
        IndexSearcher searcher = searcherManager.acquire();
        TopDocs result;
        if (offset <= 0) {
            result = searcher.search(getListDocumentsQuery(categoryId), n);
        } else {
            TopDocs topDocs = searcher.search(getListDocumentsQuery(categoryId), offset + n);
            Query q = new TermQuery(new Term(DictionarySearcher.F_CATID, categoryId));
            result = searcher.searchAfter(topDocs.scoreDocs[Math.min(topDocs.totalHits, offset) - 1], q, n);
        }
        searcherManager.release(searcher);
        return result;
    }

    /**
     * Suggest dictionary values
     * 
     * @param categoryName the category name
     * @param input the string to search
     * @return all dictionary values containing the input string
     */
    public Set<String> suggestValues(String categoryName, String input) {
        return suggestValues(categoryName, input, 100);
    }

    /**
     * Suggest dictionary values
     * 
     * @param categoryName the category name
     * @param input the string to search
     * @param num number of results
     * @return all dictionary values containing the input string
     */
    public Set<String> suggestValues(String categoryName, String input, int num) {
        customDictionaryHolder.reloadCategoryMetadata();
        if (input != null) {
            final String trimmedInput = input.trim();
            if (trimmedInput.length() >= 2) {
                final DQCategory dqCat = customDictionaryHolder.getCategoryMetadataByName(categoryName);
                if (dqCat != null) {
                    boolean isCategoryModified = dqCat.getModified();
                    Set<String> values = doSuggestValues(categoryName, trimmedInput, num, true, isCategoryModified);
                    if (values.isEmpty()) {
                        return doSuggestValues(categoryName, trimmedInput, num, false, isCategoryModified);
                    } else {
                        return values;
                    }
                }
            }
        }
        return Collections.emptySet();
    }

    private Set<String> doSuggestValues(String categoryName, String input, int num, boolean isPrefixSearch,
            boolean searchCustomIndex) {
        String jointInput = DictionarySearcher.getJointTokens(input);
        String queryString = isPrefixSearch ? jointInput + "*" : "*" + jointInput + "*";

        final BooleanQuery booleanQuery = new BooleanQuery();
        final Query catQuery = new TermQuery(new Term(DictionarySearcher.F_WORD, categoryName));
        booleanQuery.add(catQuery, BooleanClause.Occur.MUST);
        final Query wildcardQuery = new WildcardQuery(new Term(DictionarySearcher.F_SYNTERM, queryString));
        booleanQuery.add(wildcardQuery, BooleanClause.Occur.MUST);

        Set<String> results = new TreeSet<>();

        try {
            SearcherManager searcherManager = getSearcherManager(searchCustomIndex);
            if (searcherManager != null) {
                searcherManager.maybeRefresh();
                IndexSearcher searcher = searcherManager.acquire();
                TopDocs topDocs = searcher.search(booleanQuery, num);
                for (int i = 0; i < topDocs.scoreDocs.length; i++) {
                    Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
                    IndexableField[] fields = doc.getFields(DictionarySearcher.F_RAW);
                    for (IndexableField f : fields) {
                        final String str = f.stringValue();
                        if (isPrefixSearch) {
                            if (StringUtils.startsWithIgnoreCase(str, input)
                                    || StringUtils.startsWithIgnoreCase(DictionarySearcher.getJointTokens(str), jointInput)) {
                                results.add(str);
                            }
                        } else {// infix search
                            if (StringUtils.containsIgnoreCase(str, input)
                                    || StringUtils.containsIgnoreCase(DictionarySearcher.getJointTokens(str), jointInput)) {
                                results.add(str);
                            }
                        }
                    }
                }
                searcherManager.release(searcher);
            }
        } catch (IOException e) {
            LOGGER.trace(e.getMessage(), e);
        }
        return results;
    }

    private SearcherManager getSearcherManager(boolean searchCustomIndex) {
        if (searchCustomIndex) {
            if (customSearcherMananger == null) {
                initCustomDirectory();
            }
            return customSearcherMananger;
        } else {
            return sharedSearcherManager;
        }
    }

    public void close() {
        if (sharedSearcherManager != null) {
            try {
                sharedSearcherManager.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (customSearcherMananger != null) {
            try {
                customSearcherMananger.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
