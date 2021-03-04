// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.QGramTokenizer;

/**
 * @author scorreia
 * 
 * this matcher computes the Q-grams distances. By default q=3.
 */
// This code has been inspired by SimMetrics.
public class QGramsMatcher extends AbstractAttributeMatcher {

    private static final long serialVersionUID = 5526107192050192051L;

    private QGramTokenizer tokenizer = new QGramTokenizer();

    /**
     * q the length of the q-gram. By default, it is set to 3.
     */
    private int q = 3;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    @Override
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.Q_GRAMS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getWeight(java.lang.String,
     * java.lang.String)
     */
    @Override
    public double getWeight(String string1, String string2) {
        final List<String> str1Tokens = tokenizer.tokenizeToArrayList(string1, q);
        final List<String> str2Tokens = tokenizer.tokenizeToArrayList(string2, q);

        final int maxQGramsMatching = str1Tokens.size() + str2Tokens.size();

        // return
        if (maxQGramsMatching == 0) {
            return 0.0f;
        } else {
            return (double) (maxQGramsMatching - getUnNormalisedSimilarity(str1Tokens, str2Tokens)) / maxQGramsMatching;
        }
    }

    /**
     * gets the un-normalised similarity measure of the metric for the given strings.
     * 
     * @param str1Tokens
     * @param str2Tokens
     * 
     * @return returns the score of the similarity measure (un-normalised)
     */
    private int getUnNormalisedSimilarity(final List<String> str1Tokens, final List<String> str2Tokens) {

        List<String> sorted1 = new ArrayList<String>();
        sorted1.addAll(str1Tokens);
        Collections.sort(sorted1);
        List<String> sorted2 = new ArrayList<String>();
        sorted2.addAll(str2Tokens);
        Collections.sort(sorted2);

        int difference = 0;
        while (!sorted1.isEmpty() && !sorted2.isEmpty()) {
            int comp = (sorted1.get(0)).compareTo(sorted2.get(0));
            if (comp > 0) {
                sorted2.remove(0);
                difference++;
            } else if (comp < 0) {
                sorted1.remove(0);
                difference++;
            } else {
                sorted1.remove(0);
                sorted2.remove(0);
            }
        }
        // return
        return difference + sorted1.size() + sorted2.size();
    }

    /**
     * Sets the q.
     * 
     * @param q the q to set
     */
    public void setQ(int q) {
        this.q = q;
    }
}
