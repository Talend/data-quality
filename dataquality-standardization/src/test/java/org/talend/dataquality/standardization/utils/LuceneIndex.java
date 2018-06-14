package org.talend.dataquality.standardization.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneIndex {

    private LuceneIndex() {

    }

    public static void createLuceneIndex(String folder) throws IOException {
        final File testFolder = new File(folder);
        if (testFolder.exists()) {
            FileUtils.forceDeleteOnExit(testFolder);
        }
        try {
            FSDirectory testDir = FSDirectory.open(testFolder);
            IndexWriter writer = new IndexWriter(testDir,
                    new IndexWriterConfig(Version.LATEST, new StandardAnalyzer(CharArraySet.EMPTY_SET)));

            Document doc = new Document();
            Field idTermField = new StringField("catid", "1", Field.Store.YES);
            doc.add(idTermField);

            doc.add(new StringField("synterm", "", Field.Store.NO));

            writer.addDocument(doc);
            writer.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
