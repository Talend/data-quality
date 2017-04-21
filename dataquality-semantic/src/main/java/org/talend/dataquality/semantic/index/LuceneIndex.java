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

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * Created by sizhaoliu on 03/04/15.
 */
public class LuceneIndex implements Index {

    private static final Logger LOG = Logger.getLogger(LuceneIndex.class);

    private final DictionarySearcher searcher;

    public LuceneIndex(URI indexPath, DictionarySearchMode searchMode) {
        this(new DictionarySearcher(indexPath), searchMode);
    }

    public LuceneIndex(Directory directory, DictionarySearchMode searchMode) {
        this(new DictionarySearcher(directory), searchMode);
    }

    private LuceneIndex(DictionarySearcher searcher, DictionarySearchMode searchMode) {
        this.searcher = searcher;
        searcher.setTopDocLimit(20);
        searcher.setSearchMode(searchMode);
    }

    @Override
    public void initIndex() {
        searcher.maybeRefreshIndex();
    }

    @Override
    public void closeIndex() {
        searcher.close();
    }

    @Override
    public Set<String> findCategories(String data) {

        Set<String> foundCategorySet = new HashSet<String>();
        try {
            TopDocs docs = searcher.searchDocumentBySynonym(data);
            for (ScoreDoc scoreDoc : docs.scoreDocs) {
                Document document = searcher.getDocument(scoreDoc.doc);
                foundCategorySet.add(document.getField(DictionarySearcher.F_CATID).stringValue());
            }
        } catch (IOException e) {
            LOG.error(e, e);
        }
        return foundCategorySet;
    }

    @Override
    public boolean validCategories(String data, DQCategory semanticType, Set<DQCategory> children) {
        Boolean validCategory = false;
        try {
            validCategory = searcher.validDocumentWithCategories(data, semanticType, children);

        } catch (IOException e) {
            LOG.error(e, e);
        }
        return validCategory;
    }
}
