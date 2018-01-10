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
import java.util.List;
import java.util.Random;

import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.LocalDictionaryCache;
import org.talend.dataquality.semantic.model.DQDocument;

/**
 * created by msjian on 2017.10.11.
 * TDQ-14147: data masking of a column with the content of its semantic type (dictionaries)
 *
 */
public class GenerateFromDictionaries extends Function<String> {

    private static final long serialVersionUID = 1476820256067746995L;

    private String maskResult = EMPTY_STRING;

    @Override
    protected String doGenerateMaskedField(String t) {
        return maskResult;
    }

    @Override
    public void parse(String semanticCategory, boolean keepNullValues, Random rand) {
        if (semanticCategory != null) {
            List<String> valuesInDictionaries = new ArrayList<>();
            // in order to get the lastest dictionary Category values, so close first.
            CategoryRegistryManager.getInstance().getCustomDictionaryHolder().closeDictionaryCache();
            LocalDictionaryCache dict = CategoryRegistryManager.getInstance().getDictionaryCache();
            List<DQDocument> listDocuments = dict.listDocuments(semanticCategory, 0, 1);
            for (DQDocument dqDocument : listDocuments) {
                valuesInDictionaries.addAll(dqDocument.getValues());
            }
            if (!valuesInDictionaries.isEmpty()) {
                // only use the first value to mask all.
                this.maskResult = valuesInDictionaries.get(0);
            }
        }

        setKeepNull(keepNullValues);
        if (rand != null) {
            setRandom(rand);
        }
    }

}
