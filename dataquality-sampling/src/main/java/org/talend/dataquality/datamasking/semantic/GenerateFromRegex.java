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
package org.talend.dataquality.datamasking.semantic;

import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.datamasking.functions.Function;

import com.mifmif.common.regex.Generex;

/**
 * Generate masking data from regex str
 */
public class GenerateFromRegex extends Function<String> {

    private static final long serialVersionUID = 2315410175790920472L;

    protected transient Generex generex = null;

    private static final String[] invalidKw = { "(?:", "(?!", "(?=", "[[:space:]]", "[[:digit:]]", "\\u" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    private Pattern startPattern = Pattern.compile("[\\^]*+", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

    private Pattern endPattern = Pattern.compile("[\\$]*$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#doGenerateMaskedField(java.lang.Object)
     */
    @Override
    protected String doGenerateMaskedField(String inputValue) {
        if (keepNull && inputValue == null) {
            return null;
        }
        if (StringUtils.isEmpty(inputValue)) {
            return EMPTY_STRING;
        }
        String result = generex.random();
        // just remove "$"(last) from the result

        return result.substring(0, result.length() - 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#parse(java.lang.String, boolean, java.util.Random)
     */
    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        if (extraParameter != null) {
            String patterStr = removeInvalidCharacter(extraParameter);
            generex = new Generex(patterStr);
            setKeepNull(keepNullValues);
            setRandom(rand);
        }
    }

    /**
     * Keep only one '$'at the end and remove other no need character '$'
     * 
     * @param extraParameter
     * @return valid pattern string
     */
    protected String removeInvalidCharacter(String extraParameter) {
        String patternStr = stringStartTrim(extraParameter, "\\^"); //$NON-NLS-1$
        patternStr = stringEndTrim(patternStr, "\\$"); //$NON-NLS-1$
        patternStr = patternStr + "$"; //$NON-NLS-1$
        return patternStr;
    }

    /**
     * Remove special character from start
     * 
     * @param stream original string
     * @param trim The character which you want to remove
     * @return
     */
    private String stringStartTrim(String stream, String trim) {
        if (StringUtils.isEmpty(stream) || StringUtils.isEmpty(trim)) {
            return stream;
        }
        // The end location which need to remove str
        int end;
        String result = stream;

        // remove characters
        Matcher matcher = startPattern.matcher(stream);
        if (matcher.lookingAt()) {
            end = matcher.end();
            result = result.substring(end);
        }
        // return result after deal
        return result;
    }

    /**
     * Remove special character from tail
     * 
     * @param stream original string
     * @param trim The character which you want to remove
     * @return
     */
    private String stringEndTrim(String stream, String trim) {
        if (StringUtils.isEmpty(stream) || StringUtils.isEmpty(trim)) {
            return stream;
        }
        // The start location which need to remove str
        int start;
        String result = stream;
        // remove characters from tail
        Matcher matcher = endPattern.matcher(stream);
        if (matcher.find()) {
            start = matcher.start();
            result = result.substring(0, start);
        }
        // return result after deal
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#setRandom(java.util.Random)
     */
    @Override
    public void setRandom(Random rand) {
        SecureRandom secureRandom = new SecureRandom();
        super.setRandom(rand == null ? secureRandom : rand);
        if (generex != null) {
            generex.setSeed(rnd.nextLong());
        }
    }

    public static boolean isValidPattern(String patternString) {
        if (patternString == null) {
            return false;
        }
        for (String keyWord : invalidKw) {
            if (patternString.contains(keyWord)) {
                return false;
            }
        }
        return Generex.isValidPattern(patternString);
    }

}