package org.talend.dataquality.semantic.index.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.Version;
import org.talend.dataquality.semantic.api.DictionaryUtils;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

public class IndexRegenerator {

    public static void main(String[] args) throws IOException {
        IndexRegenerator.regenerateCategoryIndex("src/main/resources/category");
        IndexRegenerator.regenerateDictionaryIndex("src/main/resources/index/dictionary");
    }

    public static void regenerateCategoryIndex(String path) throws IOException {
        FSDirectory inputDir = FSDirectory.open(new File(path));
        IndexReader reader = DirectoryReader.open(inputDir);

        StandardAnalyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        File destFolder = new File(path + "_clone");
        if (destFolder.exists()) {
            FileUtils.deleteDirectory(destFolder);
        }
        FSDirectory outputDir = FSDirectory.open(new File(path + "_clone"));

        IndexWriter writer = new IndexWriter(outputDir, config);

        Bits liveDocs = MultiFields.getLiveDocs(reader);
        for (int i = 0; i < reader.maxDoc(); i++) {
            if (liveDocs != null && !liveDocs.get(i)) {
                continue;
            }
            Document doc = reader.document(i);
            DQCategory dqCat = DictionaryUtils.categoryFromDocument(doc);
            Document newdoc = DictionaryUtils.categoryToDocument(dqCat);
            writer.addDocument(newdoc);
        }
        writer.commit();
        writer.close();
        outputDir.close();
        reader.close();
        inputDir.close();
    }

    public static void regenerateDictionaryIndex(String path) throws IOException {
        FSDirectory inputDir = FSDirectory.open(new File(path));
        IndexReader reader = DirectoryReader.open(inputDir);

        StandardAnalyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        File destFolder = new File(path + "_clone");
        if (destFolder.exists()) {
            FileUtils.deleteDirectory(destFolder);
        }
        FSDirectory outputDir = FSDirectory.open(new File(path + "_clone"));

        IndexWriter writer = new IndexWriter(outputDir, config);

        Bits liveDocs = MultiFields.getLiveDocs(reader);
        for (int i = 0; i < reader.maxDoc(); i++) {
            if (liveDocs != null && !liveDocs.get(i)) {
                continue;
            }
            Document doc = reader.document(i);
            DQDocument entry = DictionaryUtils.dictionaryEntryFromDocument(doc);
            writer.addDocument(DictionaryUtils.generateDocument(entry.getId(), entry.getCategory().getId(),
                    entry.getCategory().getName(), entry.getValues()));
        }
        writer.commit();
        writer.close();
        outputDir.close();
        reader.close();
        inputDir.close();
    }

}
