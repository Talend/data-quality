package org.talend.dataquality.semantic.broadcast;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.talend.dataquality.semantic.api.DictionaryUtils;
import org.talend.dataquality.semantic.index.ClassPathDirectory;
import org.talend.dataquality.semantic.index.DictionarySearcher;

public class BroadcastIndexObjectTest {

    private static final Map<String, String[]> TEST_INDEX_CONTENT = new LinkedHashMap<String, String[]>() {

        private static final long serialVersionUID = 1L;

        {
            put("AIRPORT", new String[] { "CDG" });
            put("COMPANY", new String[] { "Talend SA" });
            put("STREET_TYPE", new String[] { "BOULEVARD", "BD" });
        }
    };

    @Test
    public void testCreateAndGet() throws URISyntaxException {
        // init a local index
        final URI testUri = new File("target/broadcast").toURI();
        try {
            FSDirectory testDir = FSDirectory.open(new File(testUri));
            IndexWriter writer = new IndexWriter(testDir,
                    new IndexWriterConfig(Version.LATEST, new StandardAnalyzer(CharArraySet.EMPTY_SET)));
            if (writer.maxDoc() > 0) {
                writer.deleteAll();
            }
            for (String key : TEST_INDEX_CONTENT.keySet()) {
                Document doc = DictionaryUtils.generateDocument("TEST", key,
                        new HashSet<String>(Arrays.asList(TEST_INDEX_CONTENT.get(key))));
                writer.addDocument(doc);
            }
            writer.commit();
            writer.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // create the broadcast object from local index
        final Directory cpDir = ClassPathDirectory.open(testUri);
        final BroadcastIndexObject bio = new BroadcastIndexObject(cpDir);
        // get the RamDirectory from BroadcastIndexObject
        final Directory ramDir = bio.get();

        // assertions
        try {
            DirectoryReader cpDirReader = DirectoryReader.open(cpDir);
            assertEquals("Unexpected document count in created index. ", TEST_INDEX_CONTENT.size(), cpDirReader.maxDoc());
            DirectoryReader ramDirReader = DirectoryReader.open(ramDir);
            assertEquals("Unexpected document count in created index. ", TEST_INDEX_CONTENT.size(), ramDirReader.maxDoc());
            for (int i = 0; i < TEST_INDEX_CONTENT.size(); i++) {
                Document doc = cpDirReader.document(i);
                String cpWord = doc.getField(DictionarySearcher.F_WORD).stringValue();
                Document ramDoc = ramDirReader.document(i);
                String ramWord = ramDoc.getField(DictionarySearcher.F_WORD).stringValue();
                assertEquals("Unexpected word", cpWord, ramWord);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
