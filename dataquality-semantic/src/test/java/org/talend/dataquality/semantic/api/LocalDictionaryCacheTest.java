package org.talend.dataquality.semantic.api;

import org.junit.Test;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocalDictionaryCacheTest extends CategoryRegistryManagerAbstract {

    Map<String, String[]> EXPECTED_SUGGESTIONS = new LinkedHashMap<String, String[]>() {

        private static final long serialVersionUID = -1799392161895210253L;

        {
            put("c", new String[] {});
            put("  chalette", new String[] { "Chalette-sur-Voire", "Châlette-sur-Loing" });

            put("Chalette", new String[] { "Chalette-sur-Voire", "Châlette-sur-Loing" });
            put("châlette", new String[] { "Chalette-sur-Voire", "Châlette-sur-Loing", });
            put("loi",
                    new String[] { "Loigny-la-Bataille", //
                            "Loigné-sur-Mayenne", //
                            "Loire-les-Marais", //
                            "Loire-sur-Rhône", //
                            "Loiron", //
                            "Loiré", //
                            "Loiré-sur-Nie", //
                            "Loisail", //
                            "Loisey", //
                            "Loisia", //
                            "Loisieux", //
                            "Loisin", //
                            "Loison", //
                            "Loison-sous-Lens", //
                            "Loison-sur-Créquoise", //
                            "Loisy", //
                            "Loisy-en-Brie", //
                            "Loisy-sur-Marne", //
                            "Loivre", //
                            "Loix", //
            });
            put("loin",
                    new String[] { "Bagneaux-sur-Loing", //
                            "Châlette-sur-Loing", //
                            "Conflans-sur-Loing", //
                            "Corgoloin", //
                            "Dammarie-sur-Loing", //
                            "Floing", //
                            "Fontenay-sur-Loing", //
                            "Gauchin-Verloingt", //
                            "Grez-sur-Loing", //
                            "La Madeleine-sur-Loing", //
                            "Montigny-sur-Loing", //
                            "Sainte-Colombe-sur-Loing", //
                            "Souppes-sur-Loing", //
                            "Villeloin-Coulangé", //
            });
            put("loing",
                    new String[] { "Bagneaux-sur-Loing", //
                            "Châlette-sur-Loing", //
                            "Conflans-sur-Loing", //
                            "Dammarie-sur-Loing", //
                            "Floing", //
                            "Fontenay-sur-Loing", //
                            "Gauchin-Verloingt", //
                            "Grez-sur-Loing", //
                            "La Madeleine-sur-Loing", //
                            "Montigny-sur-Loing", //
                            "Sainte-Colombe-sur-Loing", //
                            "Souppes-sur-Loing", //
            });
            put("ur loin",
                    new String[] { "Bagneaux-sur-Loing", //
                            "Châlette-sur-Loing", //
                            "Conflans-sur-Loing", //
                            "Dammarie-sur-Loing", //
                            "Fontenay-sur-Loing", //
                            "Grez-sur-Loing", //
                            "La Madeleine-sur-Loing", //
                            "Montigny-sur-Loing", //
                            "Sainte-Colombe-sur-Loing", //
                            "Souppes-sur-Loing", //
            });
            put("ur-loin",
                    new String[] { "Bagneaux-sur-Loing", //
                            "Châlette-sur-Loing", //
                            "Conflans-sur-Loing", //
                            "Dammarie-sur-Loing", //
                            "Fontenay-sur-Loing", //
                            "Grez-sur-Loing", //
                            "La Madeleine-sur-Loing", //
                            "Montigny-sur-Loing", //
                            "Sainte-Colombe-sur-Loing", //
                            "Souppes-sur-Loing", //
            });
        }
    };

    public static void main(String[] args) {

        for (DQCategory cat : CategoryRegistryManager.getInstance().listCategories()) {
            System.out.println(cat);
        }

        LocalDictionaryCache dict = CategoryRegistryManager.getInstance().getDictionaryCache();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Choose category: ");
            String categoryName = br.readLine();

            while (true) {

                System.out.print("Show suggestions for: ");
                String input = br.readLine();

                if ("q".equals(input)) {
                    System.out.println("Exit!");
                    System.exit(0);
                }

                long begin = System.currentTimeMillis();
                Set<String> lookupResultList = dict.suggestValues(categoryName, input, Integer.MAX_VALUE);
                long end = System.currentTimeMillis();

                for (String res : lookupResultList) {
                    System.out.println(res);
                }
                System.out.println("Found " + lookupResultList.size() + " results in " + (end - begin) + " ms.");
                System.out.println("-----------\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSuggestValues() {
        LocalDictionaryCache dict = CategoryRegistryManager.getInstance().getDictionaryCache();
        for (String input : EXPECTED_SUGGESTIONS.keySet()) {
            Set<String> found = dict.suggestValues(SemanticCategoryEnum.FR_COMMUNE.name(), input);

            assertEquals("Unexpected result size for input: " + input, EXPECTED_SUGGESTIONS.get(input).length, found.size());
            for (String expected : EXPECTED_SUGGESTIONS.get(input)) {
                assertTrue("Expected result not found: " + expected, found.contains(expected));
            }
        }

    }

    @Test
    public void testSuggestValuesFromCustomDataDict() throws IOException {
        CustomDictionaryHolder holder = CategoryRegistryManager.getInstance().getCustomDictionaryHolder();

        DQCategory answerCategory = holder.getMetadata().get(SemanticCategoryEnum.ANSWER.getTechnicalId());
        holder.updateCategory(answerCategory);

        DQDocument newDoc = new DQDocument();
        newDoc.setCategory(answerCategory);
        newDoc.setId("the_doc_id");
        newDoc.setValues(new HashSet<>(Arrays.asList("true", "false")));
        holder.addDataDictDocuments(Collections.singletonList(newDoc));

        final LocalDictionaryCache dict = holder.getDictionaryCache();

        final Map<String, String[]> EXPECTED_SUGGESTIONS_CUSTOM = new LinkedHashMap<String, String[]>() {

            private static final long serialVersionUID = -1799392161895210253L;

            {
                put("TRUE", new String[] { "true" });
                put("  False", new String[] { "false" });
                put("nein", new String[] { "Nein" });
            }
        };

        for (String input : EXPECTED_SUGGESTIONS_CUSTOM.keySet()) {
            Set<String> found = dict.suggestValues(SemanticCategoryEnum.ANSWER.name(), input);

            assertEquals("Unexpected result size for input: " + input, EXPECTED_SUGGESTIONS_CUSTOM.get(input).length,
                    found.size());
            for (String expected : EXPECTED_SUGGESTIONS_CUSTOM.get(input)) {
                assertTrue("Expected result not found: " + expected, found.contains(expected));
            }
        }
    }

}
