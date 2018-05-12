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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedRegexValidator;
import org.talend.dataquality.semantic.filter.impl.CharSequenceFilter;
import org.talend.dataquality.semantic.filter.impl.CharSequenceFilter.CharSequenceFilterType;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;
import org.talend.dataquality.semantic.model.DQFilter;
import org.talend.dataquality.semantic.model.DQRegEx;
import org.talend.dataquality.semantic.model.DQValidator;
import org.talend.dataquality.semantic.model.ValidationMode;

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
        Document doc = new Document();

        Field idTermField = new StringField(DictionarySearcher.F_DOCID, docId, Field.Store.YES);
        doc.add(idTermField);
        Field catidTermField = new StringField(DictionarySearcher.F_CATID, catId, Field.Store.YES);
        doc.add(catidTermField);
        Field wordTermField = new StringField(DictionarySearcher.F_WORD, word.trim(), Field.Store.YES);
        doc.add(wordTermField);
        for (String value : values) {
            if (value != null) {
                if (containsControlChars(value)) {
                    System.out.println("The value [" + value
                            + "] contains at least one ISO control character and is not added to the index of " + word + ".");
                    continue;
                }
                value = value.trim();
                if (value.length() > 0) {
                    List<String> tokens = DictionarySearcher.getTokensFromAnalyzer(value);
                    doc.add(new StringField(DictionarySearcher.F_SYNTERM, StringUtils.join(tokens, ' '), Field.Store.NO));
                    doc.add(new Field(DictionarySearcher.F_RAW, value, FIELD_TYPE_RAW_VALUE));
                }
            }
        }
        return doc;
    }

    private static boolean containsControlChars(String value) {
        for (char c : value.toCharArray()) {
            if (Character.isISOControl(c)) {
                return true;
            }
        }
        return false;
    }

    public static DQCategory categoryFromDocument(Document doc) {
        DQCategory dqCat = new DQCategory();
        dqCat.setId(doc.getField(DictionarySearcher.F_CATID).stringValue());
        dqCat.setName(doc.getField(DictionaryConstants.NAME).stringValue());
        dqCat.setLabel(
                doc.getField(DictionaryConstants.LABEL) == null ? "" : doc.getField(DictionaryConstants.LABEL).stringValue());
        dqCat.setType(CategoryType.valueOf(doc.getField(DictionaryConstants.TYPE).stringValue()));
        dqCat.setCompleteness(Boolean.valueOf(doc.getField(DictionaryConstants.COMPLETENESS).stringValue()));
        if (doc.getField(DictionaryConstants.MODIFIED) != null)
            dqCat.setModified(Boolean.valueOf(doc.getField(DictionaryConstants.MODIFIED).stringValue()));
        if (doc.getField(DictionaryConstants.DELETED) != null)
            dqCat.setDeleted(Boolean.valueOf(doc.getField(DictionaryConstants.DELETED).stringValue()));
        dqCat.setDescription(doc.getField(DictionaryConstants.DESCRIPTION) == null ? ""
                : doc.getField(DictionaryConstants.DESCRIPTION).stringValue());
        if (doc.getField(DictionaryConstants.VALIDATION_MODE) != null)
            dqCat.setValidationMode(ValidationMode.valueOf(doc.getField(DictionaryConstants.VALIDATION_MODE).stringValue()));

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

    public static Document categoryToDocument(DQCategory category) {
        Document doc = new Document();
        doc.add(new StringField(DictionarySearcher.F_CATID, category.getId(), Field.Store.YES));
        doc.add(new StringField(DictionaryConstants.NAME, category.getName(), Field.Store.YES));
        doc.add(new TextField(DictionaryConstants.LABEL, category.getLabel() == null ? category.getName() : category.getLabel(),
                Field.Store.YES));
        doc.add(new StringField(DictionaryConstants.TYPE, category.getType().name(), Field.Store.YES));
        doc.add(new StringField(DictionaryConstants.COMPLETENESS, String.valueOf(category.getCompleteness().booleanValue()),
                Field.Store.YES));
        doc.add(new TextField(DictionaryConstants.DESCRIPTION, category.getDescription() == null ? "" : category.getDescription(),
                Field.Store.YES));
        if (category.getModified() != null)

            doc.add(new StringField(DictionaryConstants.MODIFIED, String.valueOf(category.getModified()), Field.Store.YES));
        if (category.getDeleted() != null)
            doc.add(new StringField(DictionaryConstants.DELETED, String.valueOf(category.getDeleted()), Field.Store.YES));

        if (category.getValidationMode() != null)
            doc.add(new StringField(DictionaryConstants.VALIDATION_MODE, category.getValidationMode().name(), Field.Store.YES));

        if (!CollectionUtils.isEmpty(category.getChildren()))
            for (DQCategory child : category.getChildren())
                doc.add(new StringField(DictionaryConstants.CHILD, child.getId(), Field.Store.YES));
        return doc;
    }

    public static Document dqDocumentToLuceneDocument(DQDocument doc) {
        return generateDocument(doc.getId(), doc.getCategory().getId(), doc.getCategory().getName(), doc.getValues());
    }

    public static DQDocument dictionaryEntryFromDocument(Document doc) {
        String catId = doc.getField(DictionarySearcher.F_CATID).stringValue();
        String catName = doc.getField(DictionarySearcher.F_WORD).stringValue();
        return dictionaryEntryFromDocument(doc, catId, catName);
    }

    public static DQDocument dictionaryEntryFromDocument(Document doc, String knownCatId, String knownCatName) {
        DQDocument dqDoc = new DQDocument();
        DQCategory dqCat = CategoryRegistryManager.getInstance().getCategoryMetadataById(knownCatId);

        if (dqCat == null) {
            dqCat = new DQCategory();
            dqCat.setId(knownCatId);
            dqCat.setName(knownCatName);
        }
        dqDoc.setCategory(dqCat);

        dqDoc.setId(doc.getField(DictionarySearcher.F_DOCID).stringValue());
        IndexableField[] synTermFields = doc.getFields(DictionarySearcher.F_RAW);
        Set<String> synSet = new LinkedHashSet<>();
        for (IndexableField f : synTermFields) {
            synSet.add(f.stringValue());
        }
        dqDoc.setValues(synSet);
        return dqDoc;
    }

    static void rewriteIndex(Directory srcDir, File destFolder) throws IOException {
        final IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer(CharArraySet.EMPTY_SET));
        try (FSDirectory destDir = FSDirectory.open(destFolder); IndexWriter writer = new IndexWriter(destDir, iwc)) {
            writer.addIndexes(srcDir);
            writer.commit();
        }
    }

    public static final UserDefinedCategory regexClassifierfromDQCategory(DQCategory category) {
        DQRegEx dqRegEx = category.getRegEx();
        DQFilter dqFilter = dqRegEx.getFilter();
        DQValidator dqValidator = dqRegEx.getValidator();

        UserDefinedCategory regEx = new UserDefinedCategory(category.getName(), category.getLabel());
        regEx.setId(category.getId());
        regEx.setDescription(category.getDescription());
        regEx.setMainCategory(dqRegEx.getMainCategory());

        if (dqFilter != null) {
            CharSequenceFilter filter = new CharSequenceFilter();
            filter.setFilterParam(dqFilter.getFilterParam());
            filter.setFilterType(CharSequenceFilterType.valueOf(dqFilter.getFilterType()));
            regEx.setFilter(filter);
        }

        if (dqValidator != null) {
            UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
            validator.setPatternString(dqValidator.getPatternString());
            validator.setSubValidatorClassName(dqValidator.getSubValidatorClassName());
            regEx.setValidator(validator);
        }
        return regEx;
    }
}
