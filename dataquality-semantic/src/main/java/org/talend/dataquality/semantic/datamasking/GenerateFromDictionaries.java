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
package org.talend.dataquality.semantic.datamasking;

import org.apache.lucene.document.Document;
import org.talend.dataquality.datamasking.functions.FunctionString;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * created by msjian on 2017.10.11.
 * TDQ-14147: data masking of a column with the content of its semantic type (dictionaries)
 */
public class GenerateFromDictionaries extends FunctionString {

    private static final long serialVersionUID = 1476820256067746995L;

    protected List<String> valuesInDictionaries = null;

    private DictionarySnapshot dictionarySnapshot;

    private String semanticCategoryId;

    @Override
    protected String doGenerateMaskedField(String t) {
        return this.doGenerateMaskedFieldWithRandom(t, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        if (!valuesInDictionaries.isEmpty()) {
            return valuesInDictionaries.get(r.nextInt(valuesInDictionaries.size()));
        } else {
            return EMPTY_STRING;
        }
    }

    private List<String> getValuesFromIndex(Index index) {
        List<Document> listLuceneDocs = ((LuceneIndex) index).getSearcher().listDocumentsByCategoryId(semanticCategoryId, 0,
                Integer.MAX_VALUE);
        return listLuceneDocs.stream().flatMap(doc -> Arrays.asList(doc.getValues(DictionarySearcher.F_RAW)).stream())
                .collect(Collectors.toList());
    }

    @Override
    public void parse(String semanticCategoryId, boolean keepNullValues) {
        this.semanticCategoryId = semanticCategoryId;

        setKeepNull(keepNullValues);

        if (valuesInDictionaries == null && dictionarySnapshot != null) {
            DQCategory cat = dictionarySnapshot.getMetadata().get(semanticCategoryId);
            valuesInDictionaries = new ArrayList<>();
            if (cat != null) {
                if (!cat.getModified()) {
                    valuesInDictionaries.addAll(getValuesFromIndex(dictionarySnapshot.getSharedDataDict()));
                } else {
                    valuesInDictionaries.addAll(getValuesFromIndex(dictionarySnapshot.getCustomDataDict()));
                }
            }
        }

    }

    public void setDictionarySnapshot(DictionarySnapshot dictionarySnapshot) {
        this.dictionarySnapshot = dictionarySnapshot;
    }
}
