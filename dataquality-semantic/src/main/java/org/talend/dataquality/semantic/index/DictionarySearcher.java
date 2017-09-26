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
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.ValidationMode;

public class DictionarySearcher extends AbstractDictionarySearcher {

    public static final String UNABLE_TO_OPEN_INDEX = "Unable to open synonym index.";

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
            LOGGER.error(UNABLE_TO_OPEN_INDEX, e);
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
            LOGGER.error(UNABLE_TO_OPEN_INDEX, e);
        }
    }

    public DictionarySearcher(Directory indexDir) {
        try {
            mgr = new SearcherManager(indexDir, null);
        } catch (IOException e) {
            LOGGER.error(UNABLE_TO_OPEN_INDEX, e);
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
        Query query;
        switch (searchMode) {
        case MATCH_SEMANTIC_KEYWORD:
            query = createQueryForSemanticKeywordMatch(stringToSearch);
            break;
        case MATCH_SEMANTIC_DICTIONARY:
        default: // do the same as MATCH_SEMANTIC_DICTIONARY mode
            query = createQueryForSemanticDictionaryMatch(stringToSearch);
            break;
        }
        final IndexSearcher searcher = mgr.acquire();
        TopDocs topDocs = searcher.search(query, topDocLimit);
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
        case MATCH_SEMANTIC_KEYWORD:
            query = createQueryForSemanticKeywordMatch(stringToSearch);
            break;
        case MATCH_SEMANTIC_DICTIONARY:
        default: // do the same as MATCH_SEMANTIC_DICTIONARY mode
            query = createQueryForSemanticDictionaryMatch(stringToSearch);
            break;
        }
        final IndexSearcher searcher = mgr.acquire();
        CachingWrapperFilter cachingWrapperFilter = categoryToCache.get(semanticType.getId());
        boolean hasChildren = !CollectionUtils.isEmpty(children);

        // define the subset in which we will search
        if (cachingWrapperFilter == null) {
            if (hasChildren) {
                Set<String> childrenId = new HashSet<>();
                for (DQCategory category : children)
                    childrenId.add(category.getId());
                cachingWrapperFilter = new CachingWrapperFilter(
                        new FieldCacheTermsFilter(F_CATID, childrenId.toArray(new String[childrenId.size()])));
            } else
                cachingWrapperFilter = new CachingWrapperFilter(new FieldCacheTermsFilter(F_CATID, semanticType.getId()));
            categoryToCache.put(semanticType.getId(), cachingWrapperFilter);
        }

        // the lucene search
        TopDocs docs = searcher.search(query, cachingWrapperFilter, topDocLimit);

        ValidationMode validationMode = ValidationMode.EXACT;
        if (!hasChildren && semanticType.getValidationMode() != null) {
            validationMode = semanticType.getValidationMode();
            if (ValidationMode.SIMPLIFIED.equals(validationMode)) {
                mgr.release(searcher);
                return docs.totalHits != 0;
            }
        }

        boolean validDocument = false;
        for (int i = 0; i < docs.scoreDocs.length && !validDocument; i++) {
            Document document = searcher.doc(docs.scoreDocs[i].doc);
            if (hasChildren)
                validationMode = getChildrenValidationMode(children, document);
            validDocument = validDocumentByValidationMode(document, stringToSearch, validationMode);
        }
        mgr.release(searcher);
        return validDocument;
    }

    /**
     * This method searches the validation mode associated to the found document.
     * For that, we have to find its category.
     *
     * @param children the categories
     * @param document the found document
     * @return the validation mode
     */
    private ValidationMode getChildrenValidationMode(Set<DQCategory> children, Document document) {
        for (DQCategory child : children)
            if (child.getId().equals(document.getField(DictionarySearcher.F_CATID).stringValue()))
                return child.getValidationMode() != null ? child.getValidationMode() : ValidationMode.EXACT;
        // We should never enter here if everything went well, hence we log at an error level
        LOGGER.error("The document does not belong to any children category");
        return ValidationMode.EXACT;
    }

    /**
     * this method valids stringToSearch according to a validation mode
     *
     * @param document found in lucene index
     * @param stringToSearch to valid
     * @param validationMode
     * @return a boolean
     * @throws IOException
     */
    private boolean validDocumentByValidationMode(Document document, String stringToSearch, ValidationMode validationMode)
            throws IOException {

        if (ValidationMode.SIMPLIFIED.equals(validationMode))
            return true;
        String transformedString = transformSringByValidationMode(stringToSearch, validationMode);
        if (!StringUtils.isEmpty(transformedString))
            for (String raw : document.getValues(DictionarySearcher.F_RAW))
                if (transformedString.equals(transformSringByValidationMode(raw, validationMode)))
                    return true;
        return false;
    }

    /**
     * This method transforms a string according to a validation mode
     *
     * @param stringToTransform
     * @param validationMode
     * @return the transformed string
     */
    private String transformSringByValidationMode(String stringToTransform, ValidationMode validationMode) {
        if (ValidationMode.EXACT_IGNORE_CASE_AND_ACCENT.equals(validationMode))
            return StringUtils.stripAccents(stringToTransform.toLowerCase());
        return stringToTransform;
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
        return document != null ? document.getValues(F_RAW) : null;
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

    public TopDocs findSimilarValuesInCategory(String input, String category) throws IOException {
        BooleanQuery combinedQuery = new BooleanQuery();
        if (category != null && !StringUtils.EMPTY.equals(category)) {
            Term catTerm = new Term(DictionarySearcher.F_WORD, category);
            Query catQuery = new TermQuery(catTerm);
            combinedQuery.add(catQuery, BooleanClause.Occur.MUST);
        }

        BooleanQuery valueQuery = new BooleanQuery();
        List<String> tokens = getTokensFromAnalyzer(input);
        Query inputTermQuery = getTermQuery(F_SYNTERM, StringUtils.join(tokens, ' '), true);
        valueQuery.add(inputTermQuery, BooleanClause.Occur.SHOULD);

        BooleanQuery inputTokenQuery = new BooleanQuery();
        for (String token : tokens) {
            inputTokenQuery.add(getTermQuery(F_SYNTERM, token, true), BooleanClause.Occur.SHOULD);
        }
        valueQuery.add(inputTokenQuery, BooleanClause.Occur.SHOULD);

        combinedQuery.add(valueQuery, BooleanClause.Occur.MUST);

        final IndexSearcher searcher = mgr.acquire();
        TopDocs topDocs = searcher.search(combinedQuery, 50);
        mgr.release(searcher);
        return topDocs;
    }
}
