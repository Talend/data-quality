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
package org.talend.dataquality.semantic.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

public class DictionaryUtils {

    public static final FieldType FIELD_TYPE_SYN = new FieldType();

    public static final FieldType FIELD_TYPE_RAW_VALUE = new FieldType();

    static {
        FIELD_TYPE_SYN.setStored(false);
        FIELD_TYPE_SYN.setIndexed(true);
        FIELD_TYPE_SYN.setOmitNorms(true);
        FIELD_TYPE_SYN.freeze();

        FIELD_TYPE_RAW_VALUE.setIndexed(false);
        FIELD_TYPE_RAW_VALUE.setStored(true);
        FIELD_TYPE_RAW_VALUE.freeze();
    }

    /**
     * hide implicit public constructor
     */
    private DictionaryUtils() {
    }

    /**
     * generate a document.
     *
     * @param word
     * @param values
     * @return
     */
    public static Document generateDocument(String docId, String catId, String word, Set<String> values) {
        String tempWord = word.trim();
        Document doc = new Document();

        Field idTermField = new StringField(DictionarySearcher.F_ID, docId, Field.Store.YES);
        doc.add(idTermField);
        Field catidTermField = new StringField(DictionarySearcher.F_CATID, catId, Field.Store.YES);
        doc.add(catidTermField);
        Field wordTermField = new StringField(DictionarySearcher.F_WORD, tempWord, Field.Store.YES);
        doc.add(wordTermField);
        for (String value : values) {
            if (value != null) {
                boolean containsControlChars = false;
                for (char c : value.toCharArray()) {
                    if (Character.isISOControl(c)) {
                        containsControlChars = true;
                    }
                }
                if (containsControlChars) {
                    System.out.println("The value [" + value
                            + "] contains at least one ISO control character and is not added to the index of " + word + ".");
                    continue;
                }
                value = value.trim();
                if (value.length() > 0 && !value.equals(tempWord)) {
                    List<String> tokens = DictionarySearcher.getTokensFromAnalyzer(value);
                    doc.add(new StringField(DictionarySearcher.F_SYNTERM, StringUtils.join(tokens, ' '), Field.Store.NO));
                    doc.add(new Field(DictionarySearcher.F_RAW, value, FIELD_TYPE_RAW_VALUE));
                    if (tokens.size() > 1) {
                        doc.add(new Field(DictionarySearcher.F_SYN, value, FIELD_TYPE_SYN));
                    }
                }
            }
        }
        return doc;
    }

    public static DQCategory categoryFromDocument(Document doc) {
        DQCategory dqCat = new DQCategory();
        dqCat.setId(doc.getField(DictionaryConstants.ID).stringValue());
        dqCat.setName(doc.getField(DictionaryConstants.NAME).stringValue());
        dqCat.setLabel(
                doc.getField(DictionaryConstants.LABEL) == null ? "" : doc.getField(DictionaryConstants.LABEL).stringValue());
        dqCat.setType(CategoryType.valueOf(doc.getField(DictionaryConstants.TYPE).stringValue()));
        dqCat.setCompleteness(Boolean.valueOf(doc.getField(DictionaryConstants.COMPLETENESS).stringValue()));
        dqCat.setDescription(doc.getField(DictionaryConstants.DESCRIPTION) == null ? ""
                : doc.getField(DictionaryConstants.DESCRIPTION).stringValue());
        IndexableField[] childrenFields = doc.getFields(DictionaryConstants.CHILD);
        if (childrenFields != null) {
            List<DQCategory> synSet = new ArrayList<>();
            for (IndexableField f : childrenFields) {
                DQCategory cat = new DQCategory();
                cat.setId(f.stringValue());
                synSet.add(cat);
            }
            dqCat.setChildren(synSet);
        }
        return dqCat;
    }

    public static Document categoryToDocument(DQCategory cat) {
        Document doc = new Document();
        doc.add(new StringField(DictionaryConstants.ID, cat.getId(), Field.Store.YES));
        doc.add(new StringField(DictionaryConstants.NAME, cat.getName(), Field.Store.YES));
        doc.add(new TextField(DictionaryConstants.LABEL, cat.getLabel(), Field.Store.YES));
        doc.add(new StringField(DictionaryConstants.TYPE, cat.getType().name(), Field.Store.YES));
        doc.add(new StringField(DictionaryConstants.COMPLETENESS, String.valueOf(cat.getCompleteness().booleanValue()),
                Field.Store.YES));
        doc.add(new TextField(DictionaryConstants.DESCRIPTION, cat.getDescription(), Field.Store.YES));
        if (!CollectionUtils.isEmpty(cat.getChildren()))
            for (DQCategory child : cat.getChildren())
                doc.add(new TextField(DictionaryConstants.CHILD, child.getId(), Field.Store.YES));
        return doc;
    }

    public static DQDocument dictionaryEntryFromDocument(Document doc) {
        String catName = doc.getField(DictionarySearcher.F_WORD).stringValue();
        return dictionaryEntryFromDocument(doc, catName);
    }

    public static DQDocument dictionaryEntryFromDocument(Document doc, String knownCategoryName) {
        DQDocument dqDoc = new DQDocument();
        DQCategory dqCat = null;
        if (knownCategoryName != null) {
            dqCat = CategoryRegistryManager.getInstance().getCategoryMetadataByName(knownCategoryName);
        }
        String catId = doc.getField(DictionarySearcher.F_CATID).stringValue();
        dqCat.setId(catId);
        String catName = doc.getField(DictionarySearcher.F_WORD).stringValue();
        dqCat.setName(catName);
        dqDoc.setCategory(dqCat);

        String docId = doc.getField(DictionarySearcher.F_ID).stringValue();
        dqDoc.setId(docId);
        IndexableField[] synTermFields = doc.getFields(DictionarySearcher.F_RAW);
        Set<String> synSet = new HashSet<String>();
        for (IndexableField f : synTermFields) {
            synSet.add(f.stringValue());
        }
        dqDoc.setValues(synSet);
        return dqDoc;
    }

    static void rewriteIndex(Directory srcDir, File destFolder) throws IOException {
        final FSDirectory destDir = FSDirectory.open(destFolder);
        final IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer(CharArraySet.EMPTY_SET));
        final IndexWriter writer = new IndexWriter(destDir, iwc);

        writer.addIndexes(srcDir);
        writer.commit();
        writer.close();
        destDir.close();
    }
}
