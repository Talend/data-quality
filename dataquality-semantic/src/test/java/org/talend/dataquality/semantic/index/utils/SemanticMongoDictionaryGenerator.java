package org.talend.dataquality.semantic.index.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.cli.ServerActionRunner;
import org.talend.dataquality.semantic.cli.ServerResponse;
import org.talend.dataquality.semantic.filter.impl.CharSequenceFilter;

public class SemanticMongoDictionaryGenerator extends SemanticDictionaryGenerator {

    private static ServerActionRunner actionRunner = new ServerActionRunner();

    private void generateDictionaryForSpec(DictionaryGenerationSpec spec) throws IOException {

        System.out.println("-------------------" + spec.name() + "---------------------");
        // load CSV file
        Reader reader = new FileReader(SemanticDictionaryGenerator.class.getResource(spec.getSourceFile()).getPath());
        CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(spec.getCsvConfig().getDelimiter());
        if (spec.getCsvConfig().isWithHeader()) {
            csvFormat = csvFormat.withFirstRecordAsHeader();
        }

        // collect synonyms
        Iterable<CSVRecord> records = csvFormat.parse(reader);
        List<Set<String>> synonymSetList = getDictionaryForCategory(records, spec);

        int countCategory = 0;
        List<Set<String>> synonymsList = new ArrayList<>();
        for (Set<String> synonymSet : synonymSetList) {
            synonymsList.add(filterValues(spec.getCategoryName(), synonymSet));
            countCategory++;
        }

        SemanticCategoryEnum category = SemanticCategoryEnum.valueOf(spec.getCategoryName());
        ServerResponse response;
        response = actionRunner.runCreateAndFillValuesCategoryAction(category.name(), category.getCategoryType().name(),
                category.getDisplayName(), category.getDescription(), String.valueOf(category.getCompleteness()), synonymsList);

        // response = actionRunner.runPatchMetadataAction(category.name(), null, category.getDescription(), null);
        if (response != null && response.getCode() / 100 == 2) {
            System.out.println("Success");
        } else {
            System.err.println(">>> Http Status: " + response.getCode() + " -> " + getError(response.getBody()));
        }

        System.out.println("Total document count: " + countCategory + "\nExamples:");
        Iterator<Set<String>> it = synonymSetList.iterator();
        int count = 0;
        while (it.hasNext() && count < 10) {
            System.out.println("- " + it.next());
            count++;

        }

        reader.close();
    }

    private void generate(GenerationType type, String path) {
        for (DictionaryGenerationSpec spec : DictionaryGenerationSpec.values()) {
            if (spec.getGenerationType().equals(type)) {
                try {
                    generateDictionaryForSpec(spec);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) {
        final String resourcePath = SemanticMongoDictionaryGenerator.class.getResource(".").getFile();
        final String projectRoot = new File(resourcePath).getParentFile().getParentFile().getParentFile().getParentFile()
                .getParentFile().getParentFile().getParentFile().getParentFile().getPath() + File.separator;
        SemanticMongoDictionaryGenerator generator = new SemanticMongoDictionaryGenerator();
        generator.generate(GenerationType.DICTIONARY, projectRoot + DD_PATH);
        // generator.generate(GenerationType.KEYWORD, projectRoot + KW_PATH);

        generator.generateRegex();
    }

    private void generateRegex() {
        try {
            UserDefinedClassifier userDefinedClassifier = UDCategorySerDeser.getRegexClassifier();
            Set<ISubCategory> classifiers = userDefinedClassifier.getClassifiers();
            Set<String> ids = new HashSet<>();
            for (ISubCategory iSubCategory : classifiers) {
                UserDefinedCategory tmp = (UserDefinedCategory) iSubCategory;
                System.out.print("name: " + tmp.getName());
                System.out.print("  label: " + tmp.getLabel());
                System.out.print("  description: " + tmp.getDescription());
                System.out.print("  mainCategory: " + tmp.getMainCategory());
                if (tmp.getId() != null)
                    System.out.print("  id: " + tmp.getId());
                String filterParam = null;
                String filterType = null;
                if (tmp.getFilter() != null) {
                    filterParam = ((CharSequenceFilter) tmp.getFilter()).getFilterParam();
                    filterType = String.valueOf(((CharSequenceFilter) tmp.getFilter()).getFilterType());
                }
                if (tmp.getValidator().getSubValidatorClassName() != null
                        && tmp.getValidator().getSubValidatorClassName().length() > 0)
                    System.out.print("  SubValidatorClassName: " + tmp.getValidator().getSubValidatorClassName());
                System.out.println("  validator: " + tmp.getValidator().getPatternString());
                ArrayList<String> a = new ArrayList<String>();
                a.add(tmp.getValidator().getPatternString());
                actionRunner.runCreateRegexCategoryAction(tmp.getName(), tmp.getLabel(), tmp.getDescription(), "true",
                        tmp.getMainCategory().name(), filterParam, filterType, tmp.getValidator().getSubValidatorClassName(),
                        tmp.getValidator().getPatternString());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getError(String body) {
        int begin = body.lastIndexOf("<div>") + 5;
        int end = body.lastIndexOf("</div>");
        if (begin == -1 || end == -1 || begin >= end)
            return body;
        else
            return body.substring(begin, end);
    }

}
