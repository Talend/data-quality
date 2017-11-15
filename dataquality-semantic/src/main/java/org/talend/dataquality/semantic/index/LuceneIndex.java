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
package org.talend.dataquality.semantic.index;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.record.linkage.attribute.LevenshteinMatcher;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * Created by sizhaoliu on 03/04/15.
 */
public class LuceneIndex implements Index {

    private static final Logger LOG = Logger.getLogger(LuceneIndex.class);

    private final DictionarySearcher searcher;

    private final LevenshteinMatcher levenshtein = new LevenshteinMatcher();

    public LuceneIndex(URI indexPath, DictionarySearchMode searchMode) {
        this(new DictionarySearcher(indexPath), searchMode);
    }

    public LuceneIndex(Directory directory, DictionarySearchMode searchMode) {
        this(new DictionarySearcher(directory), searchMode);
    }

    private LuceneIndex(DictionarySearcher searcher, DictionarySearchMode searchMode) {
        this.searcher = searcher;
        this.searcher.setTopDocLimit(20);
        this.searcher.setSearchMode(searchMode);
        this.searcher.setMaxEdits(2);
        levenshtein.setTokenMethod(TokenizedResolutionMethod.NO);
    }

    @Override
    public void setCategoriesToSearch(List<String> categoryIds) {
        searcher.setCategoriesToSearch(categoryIds);
    }

    @Override
    public void initIndex() {
        searcher.maybeRefreshIndex();
    }

    @Override
    public void closeIndex() {
        searcher.close();
    }

    @Override
    public Set<String> findCategories(String data) {

        Set<String> foundCategorySet = new HashSet<>();
        try {
            TopDocs docs = searcher.searchDocumentBySynonym(data);
            for (ScoreDoc scoreDoc : docs.scoreDocs) {
                Document document = searcher.getDocument(scoreDoc.doc);
                foundCategorySet.add(document.getField(DictionarySearcher.F_CATID).stringValue());
            }
        } catch (IOException e) {
            LOG.error(e, e);
        }
        return foundCategorySet;
    }

    @Override
    public boolean validCategories(String data, DQCategory semanticType, Set<DQCategory> children) {
        Boolean validCategory = false;
        try {
            validCategory = searcher.validDocumentWithCategories(data, semanticType, children);

        } catch (IOException e) {
            LOG.error(e, e);
        }
        return validCategory;
    }

    private static Map<String, Double> sortMapByValue(Map<String, Double> unsortedMap) {
        List<Map.Entry<String, Double>> list = new LinkedList<>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {

            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Double> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * 
     * @param input An input value
     * @param category Category name
     * @param similarity A threshold value, the compared score must be >= similarity
     * @return all similar fields in a map which has score >= similarity
     */
    public Map<String, Double> findSimilarFieldsInCategory(String input, String category, Double similarity) {
        Map<String, Double> similarFieldMap = new HashMap<>();
        try {
            TopDocs docs = searcher.findSimilarValuesInCategory(input, category);
            for (ScoreDoc scoreDoc : docs.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc.doc);
                IndexableField[] synFields = doc.getFields(DictionarySearcher.F_RAW);
                for (IndexableField synField : synFields) {
                    String synFieldValue = synField.stringValue();
                    if (!similarFieldMap.containsKey(synFieldValue)) {
                        double distance = calculateOverallSimilarity(input, synFieldValue);
                        if (distance >= similarity) {
                            similarFieldMap.put(synFieldValue, distance);
                        }
                    }
                }
            }

        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return sortMapByValue(similarFieldMap);
    }

    /**
     * 
     * @param input An input value
     * @param category Category name
     * @param similarity A threshold value, the compared score must be >= similarity
     * @return The most similar Filed which is the shortest distance
     */
    public String findMostSimilarFieldInCategory(String input, String category, Double similarity) {
        String mostSimilar = StringUtils.EMPTY;
        double mostDistance = 0.0d;
        try {
            TopDocs docs = searcher.findSimilarValuesInCategory(input, category);
            for (ScoreDoc scoreDoc : docs.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc.doc);
                IndexableField[] synFields = doc.getFields(DictionarySearcher.F_RAW);
                for (IndexableField synField : synFields) {
                    String synFieldValue = synField.stringValue();
                    double currentDistance = calculateOverallSimilarity(input, synFieldValue);
                    if (currentDistance >= similarity && currentDistance > mostDistance) {
                        mostDistance = currentDistance;
                        mostSimilar = synFieldValue;
                    }
                }
            }

        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return mostSimilar;
    }

    private double calculateOverallSimilarity(String input, String field) throws IOException {
        final List<String> inputTokens = DictionarySearcher.getTokensFromAnalyzer(input);
        final List<String> fieldTokens = DictionarySearcher.getTokensFromAnalyzer(field);

        double bestTokenSimilarity = 0;
        for (String inputToken : inputTokens) {
            for (String fieldToken : fieldTokens) {
                double similarity = levenshtein.getMatchingWeight(inputToken, fieldToken);
                if (similarity > bestTokenSimilarity) {
                    bestTokenSimilarity = similarity;
                }
            }
        }
        final double fullSimilarity = levenshtein.getMatchingWeight(input.toLowerCase(), field.toLowerCase());
        return (bestTokenSimilarity + fullSimilarity) / 2;
    }

}
