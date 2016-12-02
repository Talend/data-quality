package org.talend.dataquality.semantic.index.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Pattern;

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
import org.talend.dataquality.semantic.index.utils.optimizer.CategoryOptimizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

public class SemanticMongoDictionaryGenerator {

    private static final String DD_PATH = "src/main/resources" + CategoryRecognizerBuilder.DEFAULT_DD_PATH;

    private static final String KW_PATH = "src/main/resources" + CategoryRecognizerBuilder.DEFAULT_KW_PATH;

    private static Pattern SPLITTER = Pattern.compile("\\|");

    private static ServerActionRunner actionRunner = new ServerActionRunner();

    private static Set<String> STOP_WORDS = new HashSet<String>(
            Arrays.asList("yes", "no", "y", "o", "n", "oui", "non", "true", "false", "vrai", "faux", "null"));

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
        List<Set<String>> synonymSetList = getDictinaryForCategory(records, spec);

        int countCategory = 0;
        List<Set<String>> synonymsList = new ArrayList<>();
        for (Set<String> synonymSet : synonymSetList) {

            // Set<String> a = generateDocument(spec.getCategoryName(), synonymSet);
            // if (a.iterator().hasNext())
            // synonymsList.add(a.iterator().next());
            synonymsList.add(generateDocument(spec.getCategoryName(), synonymSet));
            countCategory++;
        }

        SemanticCategoryEnum category = SemanticCategoryEnum.valueOf(spec.getCategoryName());
        ServerResponse response;
        response = actionRunner.runCreateAndFillSynonymsCategoryAction(category.name(), category.getCategoryType().name(),
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

    private List<Set<String>> getDictinaryForCategory(Iterable<CSVRecord> records, DictionaryGenerationSpec spec) {
        List<Set<String>> results = new ArrayList<Set<String>>();
        final int[] columnsToIndex = spec.getColumnsToIndex();
        final CategoryOptimizer optimizer = spec.getOptimizer();
        Set<String> existingValuesOfCategory = new HashSet<String>();
        int ignoredCount = 0;

        for (CSVRecord record : records) {

            List<String> allInputColumns = new ArrayList<String>();
            if (DictionaryGenerationSpec.CITY.equals(spec)) { // For CITY index, take all columns
                for (int col = 0; col < record.size(); col++) {
                    final String colValue = record.get(col);
                    final String[] splits = SPLITTER.split(colValue);
                    for (String syn : splits) {
                        if (syn != null && syn.trim().length() > 0) {
                            allInputColumns.add(syn.trim());
                        }
                    }
                }
            } else {
                for (int col : columnsToIndex) {
                    if (col < record.size()) { // sometimes, the value of last column can be missing.
                        final String colValue = record.get(col);
                        final String[] splits = SPLITTER.split(colValue);
                        for (String syn : splits) {
                            if (syn != null && syn.trim().length() > 0) {
                                allInputColumns.add(syn.trim());
                            }
                        }
                    }
                }
            }

            if (optimizer != null) {
                allInputColumns = new ArrayList<String>(optimizer.optimize(allInputColumns.toArray(new String[0])));
            }

            Set<String> synonymsInRecord = new HashSet<String>();
            for (String syn : allInputColumns) {
                if (STOP_WORDS.contains(syn.toLowerCase()) //
                        && (DictionaryGenerationSpec.COMPANY.equals(spec) //
                                || DictionaryGenerationSpec.FIRST_NAME.equals(spec) //
                                || DictionaryGenerationSpec.LAST_NAME.equals(spec) //
                        )) {
                    System.out.println("[" + syn + "] is exclued from the category [" + spec.getCategoryName() + "]");
                    continue;
                }
                if (!existingValuesOfCategory.contains(syn.toLowerCase())) {
                    synonymsInRecord.add(syn);
                    existingValuesOfCategory.add(syn.toLowerCase());
                } else {
                    ignoredCount++;
                }
            }
            if (synonymsInRecord.size() > 0) { // at least one synonym
                results.add(synonymsInRecord);
            }
        }
        System.out.println("Ignored value count: " + ignoredCount);
        return results;

    }

    /**
     * generate a document.
     *
     * @param word
     * @param synonyms
     * @return
     */
    Set<String> generateDocument(String word, Set<String> synonyms) {
        String tempWord = word.trim();
        Set<String> list = new HashSet<>();

        for (String syn : synonyms) {
            if (syn != null) {
                syn = syn.trim();
                if ("CITY".equals(tempWord)) { // ignore city abbreviations
                    if (syn.length() == 3 && syn.charAt(0) >= 'A' && syn.charAt(0) <= 'Z'//
                            && syn.charAt(1) >= 'A' && syn.charAt(1) <= 'Z'//
                            && syn.charAt(2) >= 'A' && syn.charAt(2) <= 'Z') {
                        continue;
                    }
                }

                if (syn.length() > 0 && !syn.equals(tempWord)) {
                    list.add(syn);
                }
            }
        }
        return list;
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
            UserDefinedClassifier userDefinedClassifier = UDCategorySerDeser.readJsonFile();
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
