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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

/**
 * created by msjian on 2017.10.11.
 * TDQ-14147: data masking of a column with the content of its semantic type (dictionaries)
 *
 */
public class GenerateFromDictionaries extends Function<String> {

    private static final long serialVersionUID = 1476820256067746995L;

    protected List<String> valuesInDictionaries = null;

    private DictionarySnapshot dictionarySnapshot;

    private String semanticCategoryId;

    @Override
    protected String doGenerateMaskedField(String t) {
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

        if (!valuesInDictionaries.isEmpty()) {
            return valuesInDictionaries.get(rnd.nextInt(valuesInDictionaries.size()));
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
    public void parse(String semanticCategoryId, boolean keepNullValues, Random rand) {
        this.semanticCategoryId = semanticCategoryId;

        setKeepNull(keepNullValues);
        if (rand != null) {
            setRandom(rand);
        }
    }

    public void setDictionarySnapshot(DictionarySnapshot dictionarySnapshot) {
        this.dictionarySnapshot = dictionarySnapshot;
    }
}
