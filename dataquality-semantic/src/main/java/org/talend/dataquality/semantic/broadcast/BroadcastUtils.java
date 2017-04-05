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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Bits;
import org.talend.dataquality.semantic.index.DictionarySearcher;

class BroadcastUtils {

    /**
     * Forbid instantiation
     */
    private BroadcastUtils() {
    }

    /**
     * initialize a list of serializable BroadcastDocumentObject from existing lucene Directory
     */
    static List<BroadcastDocumentObject> readDocumentsFromIndex(Directory indexDir) throws IOException {
        List<BroadcastDocumentObject> dictionaryObject = new ArrayList<>();
        DirectoryReader reader = DirectoryReader.open(indexDir);
        Bits liveDocs = MultiFields.getLiveDocs(reader);
        for (int i = 0; i < reader.maxDoc(); i++) {
            if (liveDocs != null && !liveDocs.get(i)) {
                continue;
            }
            Document doc = reader.document(i);
            String category = doc.getField(DictionarySearcher.F_WORD).stringValue();
            Set<String> valueSet = new HashSet<String>();
            // original values must be read from the F_RAW field
            for (IndexableField syntermField : doc.getFields(DictionarySearcher.F_RAW)) {
                valueSet.add(syntermField.stringValue());
            }
            dictionaryObject.add(new BroadcastDocumentObject(category, valueSet));
        }
        return dictionaryObject;
    }

    /**
     * initialize a list of serializable BroadcastDocumentObject from existing lucene Directory
     */
    static List<BroadcastDocumentObject> readDocumentsFromIndex(Directory indexDir, Set<String> catNames) throws IOException {
        List<BroadcastDocumentObject> dictionaryObject = new ArrayList<>();
        DirectoryReader reader = DirectoryReader.open(indexDir);
        Bits liveDocs = MultiFields.getLiveDocs(reader);
        for (int i = 0; i < reader.maxDoc(); i++) {
            if (liveDocs != null && !liveDocs.get(i)) {
                continue;
            }
            Document doc = reader.document(i);
            String category = doc.getField(DictionarySearcher.F_WORD).stringValue();
            if (catNames.contains(category)) {
                Set<String> valueSet = new HashSet<String>();
                // original values must be read from the F_RAW field
                for (IndexableField syntermField : doc.getFields(DictionarySearcher.F_RAW)) {
                    valueSet.add(syntermField.stringValue());
                }
                dictionaryObject.add(new BroadcastDocumentObject(category, valueSet));
            }
        }
        return dictionaryObject;
    }

    /**
     * create a lucene RAMDirectory from a list of BroadcastDocumentObject
     */
    static Directory createRamDirectoryFromDocuments(List<BroadcastDocumentObject> dictionaryObject) throws IOException {
        RAMDirectory ramDirectory = new RAMDirectory();
        IndexWriterConfig writerConfig = new IndexWriterConfig(new StandardAnalyzer(CharArraySet.EMPTY_SET));
        IndexWriter writer = new IndexWriter(ramDirectory, writerConfig);
        for (BroadcastDocumentObject objectDoc : dictionaryObject) {
            writer.addDocument(BroadcastUtils.createLuceneDocumentFromObject(objectDoc));
        }
        writer.commit();
        writer.close();
        return ramDirectory;
    }

    private static Document createLuceneDocumentFromObject(BroadcastDocumentObject objectDoc) throws IOException {
        Document indexDoc = new Document();
        FieldType ftSyn = new FieldType();
        ftSyn.setStored(false);
        ftSyn.setIndexOptions(IndexOptions.DOCS);
        ftSyn.setOmitNorms(true);
        ftSyn.freeze();
        indexDoc.add(new StringField(DictionarySearcher.F_WORD, objectDoc.getCategory(), Field.Store.YES));
        for (String value : objectDoc.getValueSet()) {
            // no need to include the field F_RAW during recreation of directory
            indexDoc.add(new StringField(DictionarySearcher.F_SYNTERM, DictionarySearcher.getJointTokens(value), Field.Store.NO));
        }
        return indexDoc;
    }
}
