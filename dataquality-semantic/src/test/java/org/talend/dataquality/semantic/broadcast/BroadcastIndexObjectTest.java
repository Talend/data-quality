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
package org.talend.dataquality.semantic.broadcast;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
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
    public void testCreateFromObjects() throws Exception {
        // given
        final BroadcastDocumentObject object = new BroadcastDocumentObject("CATEGORY", Collections.singleton("Value"));
        final BroadcastIndexObject bio = new BroadcastIndexObject(Collections.singletonList(object));

        try (Directory directory = bio.asDirectory()) { // when
            // then
            DirectoryReader directoryReader = DirectoryReader.open(directory);
            Document ramDoc = directoryReader.document(0);
            String word = ramDoc.getField(DictionarySearcher.F_CATID).stringValue();
            assertEquals(1, directoryReader.numDocs());
            assertEquals("CATEGORY", word);
        }
    }

    @Test
    public void testCreateAndGet() throws URISyntaxException, IOException {
        // init a local index
        final File testFolder = new File("target/broadcast");
        if (testFolder.exists()) {
            FileUtils.deleteDirectory(testFolder);
        }
        try {
            FSDirectory testDir = FSDirectory.open(testFolder);
            IndexWriter writer = new IndexWriter(testDir,
                    new IndexWriterConfig(Version.LATEST, new StandardAnalyzer(CharArraySet.EMPTY_SET)));
            if (writer.maxDoc() > 0) {
                writer.deleteAll();
                writer.commit();
            }
            for (String key : TEST_INDEX_CONTENT.keySet()) {
                Document doc = DictionaryUtils.generateDocument("TEST", key, key,
                        new LinkedHashSet<>(Arrays.asList(TEST_INDEX_CONTENT.get(key))));
                writer.addDocument(doc);
            }

            // here we add an extra document and remove it later.
            Document doc = DictionaryUtils.generateDocument("TEST", "DE_LAND", "DE_LAND",
                    new LinkedHashSet<>(Arrays.asList(new String[] { "Bayern" })));
            writer.addDocument(doc);
            writer.commit();

            // when a document is deleted from lucene index, it's marked as deleted, but not physically deleted.
            // we need to assure that it's not propagated to Spark cluster
            writer.deleteDocuments(new Term(DictionarySearcher.F_CATID, "DE_LAND"));
            writer.commit();

            writer.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // create the broadcast object from local index
        final Directory cpDir = ClassPathDirectory.open(testFolder.toURI());
        final BroadcastIndexObject bio = new BroadcastIndexObject(cpDir, true);
        // get the RamDirectory from BroadcastIndexObject
        final Directory ramDir = bio.asDirectory();

        // assertions
        try {
            DirectoryReader cpDirReader = DirectoryReader.open(cpDir);
            assertEquals("Unexpected document count in created index. ", TEST_INDEX_CONTENT.size(), cpDirReader.numDocs());
            DirectoryReader ramDirReader = DirectoryReader.open(ramDir);
            assertEquals("Unexpected document count in created index. ", TEST_INDEX_CONTENT.size(), ramDirReader.numDocs());
            for (int i = 0; i < TEST_INDEX_CONTENT.size(); i++) {
                Document doc = cpDirReader.document(i);
                String cpWord = doc.getField(DictionarySearcher.F_CATID).stringValue();
                Document ramDoc = ramDirReader.document(i);
                String ramWord = ramDoc.getField(DictionarySearcher.F_CATID).stringValue();
                assertEquals("Unexpected word", cpWord, ramWord);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testCreateWithOpenCategories() throws URISyntaxException {
        URI uri = CategoryRegistryManager.getInstance().getDictionaryURI();
        final Directory cpDir = ClassPathDirectory.open(uri);
        final BroadcastIndexObject bio = new BroadcastIndexObject(cpDir, true);
        assertEquals("Unexpected Document Size!", 145731, bio.getDocumentList().size());
    }

    @Test
    public void testCreateWithoutOpenCategories() throws URISyntaxException {
        URI uri = CategoryRegistryManager.getInstance().getDictionaryURI();
        final Directory cpDir = ClassPathDirectory.open(uri);
        final BroadcastIndexObject bio = new BroadcastIndexObject(cpDir, false);
        assertEquals("Unexpected Document Size!", 45829, bio.getDocumentList().size());
    }

}
