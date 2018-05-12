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
package org.talend.dataquality.standardization.query;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.standardization.constant.PluginConstant;
import org.talend.dataquality.standardization.exception.DQException;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class FirstNameStandardize {

    private int maxEdits = 1;

    private IndexSearcher searcher;

    @Deprecated
    public FirstNameStandardize(IndexSearcher indexSearcher, Analyzer analyzer, int hitsPerPage) throws IOException {
        this(indexSearcher);
    }

    public FirstNameStandardize(IndexSearcher indexSearcher) {
        assert indexSearcher != null;
        this.searcher = indexSearcher;
    }

    @Deprecated
    public void getFuzzySearch(String input, TopDocsCollector<?> collector) throws DQException {
        Query q = new FuzzyQuery(new Term(PluginConstant.FIRST_NAME_STANDARDIZE_NAME, input));
        Query qalias = new FuzzyQuery(new Term(PluginConstant.FIRST_NAME_STANDARDIZE_ALIAS, input));
        BooleanQuery combinedQuery = new BooleanQuery();
        combinedQuery.add(q, BooleanClause.Occur.SHOULD);
        combinedQuery.add(qalias, BooleanClause.Occur.SHOULD);
        try {
            searcher.search(combinedQuery, collector);
        } catch (IOException e) {
            throw new DQException(e);
        }
    }

    // FIXME this variable is only for tests
    @Deprecated
    public static final boolean SORT_WITH_COUNT = true;

    private Query getTermQuery(String field, String text, boolean fuzzy) {
        Term term = new Term(field, text);
        return fuzzy ? new FuzzyQuery(term, maxEdits) : new TermQuery(term);
    }

    private List<String> getTokensFromAnalyzer(String input) throws IOException {
        StandardTokenizer tokenStream = new StandardTokenizer(new StringReader(input));
        TokenStream result = new StandardFilter(tokenStream);
        result = new LowerCaseFilter(result);
        CharTermAttribute charTermAttribute = result.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        List<String> termList = new ArrayList<String>();
        while (result.incrementToken()) {
            String term = charTermAttribute.toString();
            termList.add(term);
        }
        result.close();
        return termList;
    }

    public ScoreDoc[] standardize(String inputName, Map<String, String> information2value, boolean fuzzySearch)
            throws IOException {
        if (inputName == null || inputName.length() == 0) {
            return new ScoreDoc[0];
        }

        // set get county and gender fields value
        String countryText = null;
        String genderText = null;
        if (information2value != null) {
            countryText = information2value.get(PluginConstant.FIRST_NAME_STANDARDIZE_COUNTRY);
            genderText = information2value.get(PluginConstant.FIRST_NAME_STANDARDIZE_GENDER);
        }

        BooleanQuery combinedQuery = new BooleanQuery();

        BooleanQuery nameQueries = new BooleanQuery();
        // always add a non-fuzzy query on each token.
        List<String> tokens = getTokensFromAnalyzer(inputName);
        for (String token : tokens) {
            Query termQuery = getTermQuery(PluginConstant.FIRST_NAME_STANDARDIZE_NAME, token, false);
            termQuery.setBoost(2);
            nameQueries.add(termQuery, BooleanClause.Occur.SHOULD);
        }

        Query nameTermQuery = getTermQuery(PluginConstant.FIRST_NAME_STANDARDIZE_NAMETERM, inputName.toLowerCase(), fuzzySearch);
        nameQueries.add(nameTermQuery, BooleanClause.Occur.SHOULD);

        combinedQuery.add(nameQueries, BooleanClause.Occur.MUST);

        if (countryText != null && !"".equals(countryText)) {//$NON-NLS-1$
            Query countryQuery = getTermQuery(PluginConstant.FIRST_NAME_STANDARDIZE_COUNTRY, countryText, false);
            countryQuery.setBoost(5);
            combinedQuery.add(countryQuery, BooleanClause.Occur.SHOULD);
        }
        if (genderText != null && !"".equals(genderText)) {//$NON-NLS-1$
            Query genderQuery = getTermQuery(PluginConstant.FIRST_NAME_STANDARDIZE_GENDER, genderText, false);
            genderQuery.setBoost(5);
            combinedQuery.add(genderQuery, BooleanClause.Occur.SHOULD);
        }

        TopDocs matches = searcher.search(combinedQuery, 10);

        return matches.scoreDocs;
    }

    /**
     * Method "replaceName".
     * 
     * @param inputName a first name
     * @return the standardized first name
     * @throws Exception
     */
    public String replaceName(String inputName, boolean fuzzyQuery) throws IOException {
        ScoreDoc[] results = standardize(inputName, null, fuzzyQuery);
        return getFinalAccurateResult(inputName, results);
    }

    public String replaceNameWithCountryGenderInfo(String inputName, String inputCountry, String inputGender, boolean fuzzyQuery)
            throws IOException {
        Map<String, String> indexFields = new HashMap<String, String>();
        indexFields.put("country", inputCountry);//$NON-NLS-1$
        indexFields.put("gender", inputGender);//$NON-NLS-1$
        ScoreDoc[] results = standardize(inputName, indexFields, fuzzyQuery);
        return getFinalAccurateResult(inputName, results);
    }

    public String replaceNameWithCountryInfo(String inputName, String inputCountry, boolean fuzzyQuery) throws IOException {
        Map<String, String> indexFields = new HashMap<String, String>();
        indexFields.put("country", inputCountry);//$NON-NLS-1$
        ScoreDoc[] results = standardize(inputName, indexFields, fuzzyQuery);
        return getFinalAccurateResult(inputName, results);
    }

    public String replaceNameWithGenderInfo(String inputName, String inputGender, boolean fuzzyQuery) throws IOException {
        Map<String, String> indexFields = new HashMap<String, String>();
        indexFields.put("gender", inputGender);//$NON-NLS-1$
        ScoreDoc[] results = standardize(inputName, indexFields, fuzzyQuery);
        return getFinalAccurateResult(inputName, results);
    }

    /**
     * TDQ-12953: improve try to get an accurate result.
     * 
     * @param inputName
     * @param results
     * @return
     * @throws IOException
     */
    protected String getFinalAccurateResult(String inputName, ScoreDoc[] results) throws IOException {
        if (results.length == 0) {
            return "";//$NON-NLS-1$
        }
        // if the result and input are more different, we return "".
        String result = searcher.doc(results[0].doc).get("name");//$NON-NLS-1$
        if (result.length() - inputName.length() >= 3) {
            return "";//$NON-NLS-1$
        }
        return result;
    }

    public void setMaxEdits(int maxEdits) {
        this.maxEdits = maxEdits;
    }

}
