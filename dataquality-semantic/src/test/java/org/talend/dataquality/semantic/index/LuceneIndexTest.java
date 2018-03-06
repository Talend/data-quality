// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic.index;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

public class LuceneIndexTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneIndexTest.class);

    private Map<String[], String[]> EXPECTED_SIMILAR_VALUES = new LinkedHashMap<String[], String[]>() {

        private static final long serialVersionUID = 1L;

        {
            put(new String[] { "talend", SemanticCategoryEnum.COMPANY.getId() }, //
                    new String[] { "Talend" });
            put(new String[] { "Russian Federatio", SemanticCategoryEnum.COUNTRY.getId() }, //
                    new String[] { "Russian Federation", "Russia" });
            put(new String[] { "Federation de Russie", SemanticCategoryEnum.COUNTRY.getId() }, //
                    new String[] { "Russian Federation", "Fédération de Russie", "Russie" });
            put(new String[] { "Russie", SemanticCategoryEnum.COUNTRY.getId() }, //
                    new String[] { "Russie", "Russia", "Fédération de Russie" });

            put(new String[] { "viet nam", SemanticCategoryEnum.COUNTRY.getId() }, //
                    new String[] { "Viet Nam", "Viêt-nam", "Viêt Nam", "Vietnam", "Viêtnam" });
            put(new String[] { "vietnam", SemanticCategoryEnum.COUNTRY.getId() }, //
                    new String[] { "Vietnam", "Viêtnam", "Viet Nam", "Viêt-nam", "Viêt Nam" });

            put(new String[] { "Oil Gas Consumable Fuels", SemanticCategoryEnum.INDUSTRY.getId() }, //
                    new String[] { "Oil, Gas & Consumable Fuels" });

            put(new String[] { "Clermont Ferrand", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont-Ferrand", "Clermont" });
            put(new String[] { "clermont ferrand", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont-Ferrand", "Clermont" });
            put(new String[] { "Clermont Fd", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont" });
            put(new String[] { "clermont fd", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont" });
            put(new String[] { "Clermont", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont" });
            put(new String[] { "clermont", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont" });
            put(new String[] { "Ferrand", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Ferran" });
            put(new String[] { "ferrand", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Ferran" });

            put(new String[] { "carrières", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Cabrières", "Carnières", "Courrières" });
            put(new String[] { "carrieres", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Cabrières", "Carnières", "Courrières" });
            put(new String[] { "carrière", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Carrère", "Cabrières", "Carnières" });
            put(new String[] { "carriere", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Carrère", "Jarrier" });
        }
    };

    @Test
    public void testFindSimilarFieldsInCategory() throws URISyntaxException {

        final URI ddPath = CategoryRegistryManager.getInstance().getDictionaryURI();
        final LuceneIndex dataDictIndex = new LuceneIndex(ddPath, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);

        for (String[] input : EXPECTED_SIMILAR_VALUES.keySet()) {
            final List<String> expectedMatches = Arrays.asList(EXPECTED_SIMILAR_VALUES.get(input));
            LOGGER.debug("-----------search [" + input[0] + "] in category " + input[1] + "----------");
            Map<String, Double> resultMap = dataDictIndex.findSimilarFieldsInCategory(input[0], input[1], 0.6);
            for (String key : resultMap.keySet()) {
                LOGGER.debug(key + " \t " + resultMap.get(key));
            }
            for (String match : expectedMatches) {
                assertTrue("The value [" + match + "] is expected to be found for the search of [" + input[0] + "].",
                        resultMap.keySet().contains(match));
            }
        }

    }

    @Test
    public void testFindMostSimilarFieldInCategory() throws URISyntaxException {
        final URI ddPath = CategoryRegistryManager.getInstance().getDictionaryURI();
        final LuceneIndex dataDictIndex = new LuceneIndex(ddPath, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);

        Assert.assertEquals("Talend",
                dataDictIndex.findMostSimilarFieldInCategory("talend", SemanticCategoryEnum.COMPANY.getId(), 0.6));
        Assert.assertEquals("Russian Federation",
                dataDictIndex.findMostSimilarFieldInCategory("Russian Federatio", SemanticCategoryEnum.COUNTRY.getId(), 0.6));
        Assert.assertEquals("Clermont-Ferrand",
                dataDictIndex.findMostSimilarFieldInCategory("Clermont Ferrand", SemanticCategoryEnum.FR_COMMUNE.getId(), 0.7));

    }
}
