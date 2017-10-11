package org.talend.dataquality.semantic.classifier;

import java.util.Map;

import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * Run this program to check if the content of SemanticCategoryEnum is identical to the category metadata in the lucene index.
 */
public class SemanticCategoryEnumChecker {

    public static void main(String[] args) {
        Map<String, DQCategory> idMap = CategoryRegistryManager.getInstance().getSharedCategoryMetadata();

        for (SemanticCategoryEnum catEnum : SemanticCategoryEnum.values()) {
            DQCategory meta = idMap.get(catEnum.getTechnicalId());
            if (meta != null) {
                String enumString = catEnum.name() + "(\"" + meta.getId() + "\", \"" + catEnum.getDisplayName() + "\", \""
                        + catEnum.getDescription() + "\", CategoryType." + catEnum.getCategoryType().name() + ", "
                        + catEnum.getCompleteness() + "),";

                String dqCatString = meta.getName() + "(\"" + meta.getId() + "\", \"" + meta.getLabel() + "\", \""
                        + meta.getDescription() + "\", CategoryType." + meta.getType().name() + ", " + meta.getCompleteness()
                        + "),";

                if (!enumString.equals(dqCatString)) {
                    System.err.println(">>> The enumeration item {" + catEnum.name()
                            + "} differs from actual metadata. Please update one of them.");
                }
                System.out.println("Enum: " + enumString);
                System.out.println("Meta: " + dqCatString + "\n");
            }
        }

        for (DQCategory meta : idMap.values()) {
            if (SemanticCategoryEnum.getCategoryById(meta.getName()) == null) {
                System.err.println(">>> Could not find category {" + meta.getName() + "} in current enumeration. Please add it.");

                String dqCatString = meta.getName() + "(\"" + meta.getId() + "\", \"" + meta.getLabel() + "\", \""
                        + meta.getDescription() + "\", CategoryType." + meta.getType().name() + ", " + meta.getCompleteness()
                        + "),";
                System.err.println(dqCatString + "\n");
            }
        }
    }
}
