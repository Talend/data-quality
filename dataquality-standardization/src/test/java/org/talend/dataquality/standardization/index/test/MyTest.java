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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
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
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import junit.framework.TestCase;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class MyTest extends TestCase {

    private static final String[] input = { "WING ASSEMBY, USE 5J868-A HEX BOLT .25” - DRILL FOUR HOLES",
            "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH", "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH",
            "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH", "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH",
            "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH", "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH",
            "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH", "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH",
            "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH", "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH",
            "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH", "WING ASSY DRILL 4 HOLE USE 5J868A HEXBOLT 1/4 INCH",
            "WING ASSEMBY, USE 5J868-A HEX BOLT .25” - DRILL FOUR HOLES",
            "USE 4 5J868A BOLTS (HEX .25) - DRILL HOLES FOR EA ON WING ASSEM" };

    public void testRun() {
        Directory index;
        try {
            index = new RAMDirectory();
            // The same analyzer should be used for indexing and searching
            Analyzer analyzer = SynonymTest.createAnalyzer();
            // Analyzer analyzer = new StandardAnalyzer();
            // the boolean arg in the IndexWriter ctor means to
            // create a new index, overwriting any existing index
            IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, analyzer);
            IndexWriter w = new IndexWriter(index, writerConfig);
            // read the data (this will be the input data of a component called
            // tFirstNameStandardize)

            for (String data : input) {
                Document doc = createDoc(data);
                w.addDocument(doc);
            }

            w.close();

            // now search
            DirectoryReader indexReader = DirectoryReader.open(index);
            IndexSearcher is = new IndexSearcher(indexReader);
            Set<String> set = new HashSet<String>(Arrays.asList(input));

            for (String data : set) {

                // Term termName = new Term("steph");
                QueryParser qp = new QueryParser("FIELD_NAME", analyzer);
                Query q = qp.parse(data);

                TopDocsCollector<?> collector = TopScoreDocCollector.create(2, false);
                is.search(q, collector);

                ScoreDoc[] scoreDocs = collector.topDocs().scoreDocs;
                System.out.println("\nnb doc= " + scoreDocs.length);
                System.out.print("input: " + data);
                for (ScoreDoc scoreDoc : scoreDocs) {
                    String matchedData = is.doc(scoreDoc.doc).get("FIELD_NAME");
                    System.out.println("  matches  " + matchedData);
                }

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * DOC scorreia Comment method "createDoc".
     * 
     * @param data
     * @return
     */
    private Document createDoc(String data) {
        Document doc = new Document();
        Field field = new Field("FIELD_NAME", data, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES);
        doc.add(field);
        return doc;
    }

}
