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

import java.io.File;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * The similarity scoring formula of Lucene:
 * 
 * Σ(t in q) (tf(t in d) * idf(t) * boost(t.field in d) * lengthNorm(t.field in d )) * coord(q,d) * queryNorm(q)
 * 
 * in which we have the following factors:
 * 
 * -tf: factor of term frequency in document. -idf: factor of documents with term in index ─ boost: field-level boost ─
 * coord: factor-based # of query terms in document ─ queryNorm: normalization for query weights
 * 
 * The purpose of this formula is to measure the similarity between a query and each document that matches the query.
 * The score is computed for each document (d) matching each term (t) in a query (q).
 * 
 * The scoring formula seems daunting—and it is. We’re talking about factors that rank one document higher than another
 * based on a query; that in and of itself deserves the sophistication going on. If you want to see how all these
 * factors play out, Lucene provides a helpful feature called Explanation. IndexSearcher has an explain method, which
 * requires a Query and a document ID and returns an Explanation object.
 */
public class Explainer {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: Explainer <index dir> <query>");
            System.exit(1);
        }
        String indexDir = args[0];
        String queryExpression = args[1];
        Directory directory = FSDirectory.open(new File(indexDir).toPath());
        QueryParser parser = new QueryParser("contents", new SimpleAnalyzer());
        Query query = parser.parse(queryExpression);
        System.out.println("Query: " + queryExpression);
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc match : topDocs.scoreDocs) {
            Explanation explanation = searcher.explain(query, match.doc);
            System.out.println("----------");
            Document doc = searcher.doc(match.doc);
            System.out.println(doc.get("title"));
            System.out.println(explanation.toString());
        }
        searcher.getIndexReader().close();
        directory.close();
    }

}
