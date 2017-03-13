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
package org.talend.dataquality.record.linkage.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author scorreia
 * 
 * A tokenizer which extracts the q-grams.
 */
public class QGramTokenizer implements Serializable {

    private static final long serialVersionUID = -1099452033117058336L;

    private static final String TWO_CHARS = "##"; //$NON-NLS-1$

    private static final String THREE_CHARS = "###"; //$NON-NLS-1$

    private static final String FOUR_CHARS = "####"; //$NON-NLS-1$

    /**
     * Method "tokenizeToArrayList".
     * 
     * @param input a string to split in tokens (may be null)
     * @param q the length of each token
     * @return the list of tokens (never null)
     */
    public final List<String> tokenizeToArrayList(final String input, int q) {
        if (input == null) {
            return new ArrayList<String>();
        }
        String toTokenize = pad(input, q);
        List<String> tokens = new ArrayList<String>();
        for (int i = 0; i < toTokenize.length() - q + 1; i++) {
            tokens.add(toTokenize.substring(i, i + q));
        }
        return tokens;
    }

    /**
     * DOC scorreia Comment method "pad".
     * 
     * @param input
     * @return
     */
    private String pad(String input, int q) {
        switch (q) {
        case 2:
            return new StringBuffer("#").append(input).append("#").toString(); //$NON-NLS-1$ //$NON-NLS-2$
        case 3:
            return new StringBuffer(TWO_CHARS).append(input).append(TWO_CHARS).toString();
        case 4:
            return new StringBuffer(THREE_CHARS).append(input).append(THREE_CHARS).toString();
        case 5:
            return new StringBuffer(FOUR_CHARS).append(input).append(FOUR_CHARS).toString();
        default:
            return calDefaultPad('#', q, input);
        }
    }

    private String calDefaultPad(char charac, int repeat, String input) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            buf.append(charac);
        }
        return buf.append(input).append(buf.toString()).toString();
    }

}
