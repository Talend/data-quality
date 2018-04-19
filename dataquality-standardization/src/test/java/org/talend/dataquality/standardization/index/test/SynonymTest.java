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
package org.talend.dataquality.standardization.index.test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;

import junit.framework.TestCase;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SynonymTest extends TestCase {

    /**
     * 
     */
    private static final String FIELD_NAME = "name";

    private static final String directoryPath = "data/test";

    public void testRun() {
        MMapDirectory index;
        try {
            index = new MMapDirectory(new File(directoryPath));
            // The same analyzer should be used for indexing and searching
            Analyzer analyzer = createAnalyzer();
            // Analyzer analyzer = new StandardAnalyzer();
            // the boolean arg in the IndexWriter ctor means to
            // create a new index, overwriting any existing index
            IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, analyzer);
            IndexWriter w = new IndexWriter(index, writerConfig);
            // read the data (this will be the input data of a component called
            // tFirstNameStandardize)

            String name = "Stephane";
            String gender = "M";
            Document doc = addDoc(w, name, gender);

            addSynonym(FIELD_NAME, "Steph", doc);

            w.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void testSearch() {
        // TODO search for steph
        Directory dir = null;
        IndexSearcher is = null;
        try {
            dir = FSDirectory.open(new File(directoryPath));
            DirectoryReader indexReader = DirectoryReader.open(dir);
            is = new IndexSearcher(indexReader);
            Analyzer analyzer = createAnalyzer();

            // Term termName = new Term("steph");
            QueryParser qp = new QueryParser(FIELD_NAME, analyzer);
            Query q = qp.parse("Stephane");

            TopDocsCollector<?> collector = TopScoreDocCollector.create(2, false);
            is.search(q, collector);

            ScoreDoc[] scoreDocs = collector.topDocs().scoreDocs;
            System.out.println("nb doc= " + scoreDocs.length);
            for (ScoreDoc scoreDoc : scoreDocs) {
                System.out.println(scoreDoc);
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CorruptIndexException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    static Analyzer createAnalyzer() {
        return new StandardAnalyzer();
    }

    /**
     * DOC scorreia Comment method "addSynonym".
     * 
     * @param fieldName
     * @param synonym
     * @param doc
     */
    private void addSynonym(String fieldName, String synonym, Document doc) {
        assert doc != null;
        doc.add(new Field(fieldName, synonym, Field.Store.YES, Field.Index.ANALYZED));
    }

    private static Document addDoc(IndexWriter w, String name, String gender) throws IOException {
        Document doc = new Document();
        Field field = new Field(FIELD_NAME, name, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES);
        doc.add(field);
        doc.add(new Field("gender", gender, Field.Store.YES, Field.Index.NOT_ANALYZED, TermVector.YES));
        w.addDocument(doc);
        return doc;
    }

}
