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
package org.talend.dataquality.standardization.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class AllDocCollector extends Collector {

    List<ScoreDoc> docs = new ArrayList<ScoreDoc>();

    private Scorer scorer;

    private int docBase;

    @Override
    public boolean acceptsDocsOutOfOrder() {
        return true;
    }

    @Override
    public void setScorer(Scorer scorer) {
        this.scorer = scorer;
    }

    public void setNextReader(IndexReader reader, int docBase) {
        this.docBase = docBase;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.search.Collector#collect(int)
     */
    @Override
    public void collect(int doc) throws IOException {
        docs.add(new ScoreDoc(doc + docBase, scorer.score()));

    }

    public void reset() {
        docs.clear();
    }

    public List<ScoreDoc> getHits() {
        return docs;
    }

    @Override
    public void setNextReader(AtomicReaderContext context) throws IOException {
        this.docBase = context.docBase;
    }

}
