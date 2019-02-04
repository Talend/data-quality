package org.talend.dataquality.semantic.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.talend.dataquality.semantic.datamasking.GenerateFromRegex;
import org.talend.dataquality.semantic.datamasking.model.CategoryValues;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

public class GenerateValidator {

    public static List<CategoryValues> initSemanticTypes(DictionarySnapshot dictionarySnapshot, DQCategory cat, Random rnd) {
        List<CategoryValues> categoryValues = new ArrayList<>();
        Random finalRnd = rnd == null ? new Random() : rnd;

        cat.getChildren().forEach(child -> {
            DQCategory completeChild = dictionarySnapshot.getMetadata().get(child.getId());
            CategoryType childType = completeChild.getType();

            switch (childType) {
            case DICT:
                if (completeChild != null) {
                    List<String> values = new ArrayList<>();
                    if (!completeChild.getModified()) {
                        values.addAll(getValuesFromIndex(dictionarySnapshot.getSharedDataDict(), completeChild.getId()));
                    } else {
                        values.addAll(getValuesFromIndex(dictionarySnapshot.getCustomDataDict(), completeChild.getId()));
                    }
                    CategoryValues catValue = new CategoryValues();
                    catValue.setCategoryId(child.getId());
                    catValue.setName(completeChild.getName());
                    catValue.setType(CategoryType.DICT);
                    catValue.setValue(values);

                    categoryValues.add(catValue);
                }
                break;
            case REGEX:
                String pattern = dictionarySnapshot.getRegexClassifier().getPatternStringByCategoryId(child.getId());
                if (GenerateFromRegex.isValidPattern(pattern)) {
                    CategoryValues catValue = new CategoryValues();
                    catValue.setCategoryId(child.getId());
                    catValue.setName(completeChild.getName());
                    catValue.setType(CategoryType.REGEX);
                    catValue.setValue(pattern);
                    categoryValues.add(catValue);
                }
                break;
            case COMPOUND:
                categoryValues.addAll(initSemanticTypes(dictionarySnapshot, completeChild, finalRnd));
                break;
            }
        });

        return categoryValues;
    }

    private static List<String> getValuesFromIndex(Index index, String categoryId) {
        List<Document> listLuceneDocs = ((LuceneIndex) index).getSearcher().listDocumentsByCategoryId(categoryId);
        return listLuceneDocs.stream().flatMap(doc -> Arrays.asList(doc.getValues(DictionarySearcher.F_RAW)).stream())
                .collect(Collectors.toList());
    }

}
