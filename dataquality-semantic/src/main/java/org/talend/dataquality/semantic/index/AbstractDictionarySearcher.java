package org.talend.dataquality.semantic.index;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

/**
 * Created by jteuladedenantes on 16/11/16.
 */
public abstract class AbstractDictionarySearcher {

    private static final Logger LOGGER = Logger.getLogger(AbstractDictionarySearcher.class);

    public static final String F_ID = "docid";//$NON-NLS-1$

    public static final String F_WORD = "word";//$NON-NLS-1$

    public static final String F_SYNTERM = "synterm";//$NON-NLS-1$

    public static final String F_RAW = "raw";

    public static final String F_CATID = "catid";

    protected int topDocLimit = 3;

    private int maxEdits = 2; // Default value

    private static final int MAX_TOKEN_COUNT_FOR_KEYWORD_MATCH = 20;

    private static final int MAX_CHAR_COUNT_FOR_DICTIONARY_MATCH = 100;

    protected DictionarySearchMode searchMode = DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY;

    public abstract TopDocs searchDocumentBySynonym(String stringToSearch) throws IOException;

    public abstract Document getDocument(int docNum);

    /**
     * Method "setTopDocLimit" set the maximum number of documents to return after a search.
     *
     * @param topDocLimit the limit
     */
    public void setTopDocLimit(int topDocLimit) {
        this.topDocLimit = topDocLimit;
    }

    public void setMaxEdits(int maxEdits) {
        this.maxEdits = maxEdits;
    }

    public DictionarySearchMode getSearchMode() {
        return searchMode;
    }

    public void setSearchMode(DictionarySearchMode searchMode) {
        this.searchMode = searchMode;
    }

    private Query getTermQuery(String field, String text, boolean fuzzy) {
        Term term = new Term(field, text);
        return fuzzy ? new FuzzyQuery(term, maxEdits) : new TermQuery(term);
    }

    /**
     * @param input
     * @return
     * @throws IOException
     */
    protected Query createQueryForSemanticDictionaryMatch(String input) throws IOException {
        // for dictionary search, ignore searching for input containing too many tokens
        if (input.length() > MAX_CHAR_COUNT_FOR_DICTIONARY_MATCH) {
            return new TermQuery(new Term(F_SYNTERM, StringUtils.EMPTY));
        }

        return getTermQuery(F_SYNTERM, StringUtils.join(getTokensFromAnalyzer(input), ' '), false);
    }

    /**
     *
     *
     * @param input
     * @return
     * @throws IOException
     */
    protected Query createQueryForSemanticKeywordMatch(String input) throws IOException {
        BooleanQuery booleanQuery = new BooleanQuery();
        List<String> tokens = getTokensFromAnalyzer(input);
        // for keyword search, when the token count exceeds MAX_TOKEN_COUNT_FOR_KEYWORD_MATCH, only search the beginning
        // tokens from input
        for (int i = 0; i < Math.min(tokens.size(), MAX_TOKEN_COUNT_FOR_KEYWORD_MATCH); i++) {
            booleanQuery.add(getTermQuery(F_SYNTERM, tokens.get(i), false), BooleanClause.Occur.SHOULD);
        }
        return booleanQuery;
    }

    public static String getJointTokens(String input) {
        return StringUtils.join(getTokensFromAnalyzer(input), ' ');
    }

    /**
     *
     * @param input
     * @return a list of lower-case tokens which strips accents & punctuation
     * @throws IOException
     */
    public static List<String> getTokensFromAnalyzer(String input) {
        StandardTokenizer tokenStream = new StandardTokenizer(new StringReader(input));
        TokenStream result = new StandardFilter(tokenStream);
        result = new LowerCaseFilter(result);
        result = new ASCIIFoldingFilter(result);
        CharTermAttribute charTermAttribute = result.addAttribute(CharTermAttribute.class);
        List<String> termList = new ArrayList<String>();
        try {
            tokenStream.reset();
            while (result.incrementToken()) {
                String term = charTermAttribute.toString();
                termList.add(term);
            }
            result.close();
        } catch (IOException e) {
            LOGGER.debug(e);
        }
        if (termList.size() == 1) { // require exact match when the input has only one token
            termList.clear();
            termList.add(StringUtils.stripAccents(input.toLowerCase()));
        }
        return termList;
    }

}
