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
package org.talend.dataquality.datamasking.functions;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jgonzalez on 18 juin 2015. This class is an abstract class that
 * all other functions extends. All the methods and fiels that all functions
 * share are stored here.
 *
 */
public abstract class Function<T> implements Serializable {

    private static final long serialVersionUID = 6333987486134315822L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Function.class);

    protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

    public static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //$NON-NLS-1$

    public static final String LOWER = "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$

    protected Random rnd = new Random();

    protected String[] parameters;

    protected boolean keepNull = false;

    protected boolean keepInvalidPattern = false;

    protected boolean keepEmpty = false;

    protected boolean keepFormat = false;

    protected static final Pattern patternSpace = Pattern.compile("\\s+");

    protected static final Pattern nonDigits = Pattern.compile("\\D+");

    /**
     * setter for random
     *
     * @param rand
     * The java.util.Random instance.
     */
    public void setRandom(Random rand) {
        if (rand == null) {
            rnd = new Random();
        } else {
            rnd = rand;
        }
    }

    /**
     * getter for random
     *
     * @return the random object
     */
    public Random getRandom() {
        return rnd;
    }

    /**
     * DOC jgonzalez Comment method "setKeepNull". This function sets a boolean
     * used to keep null values.
     * 
     * @param keep
     * The value of the boolean.
     */
    public void setKeepNull(boolean keep) {
        this.keepNull = keep;
    }

    public void setKeepFormat(boolean keep) {
        this.keepFormat = keep;
    }

    public void setKeepEmpty(boolean empty) {
        this.keepEmpty = empty;
    }

    public void setKeepInvalidPattern(boolean keepInvalidPattern) {
        this.keepInvalidPattern = keepInvalidPattern;
    }

    /**
     * DOC jgonzalez Comment method "parse". This function is called at the
     * beginning of the job and parses the parameter. Moreover, it will call
     * methods setKeepNull and setRandomWrapper
     * 
     * @param extraParameter
     * The parameter we try to parse.
     * @param keepNullValues
     * The parameter used for setKeepNull.
     * @param rand
     * The parameter used for setRandomMWrapper.
     */
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        if (extraParameter != null) {
            parameters = clean(extraParameter).split(","); //$NON-NLS-1$
            if (parameters.length == 1) { // check if it's a path to a readable file
                try {
                    List<String> aux = KeysLoader.loadKeys(parameters[0].trim());
                    parameters = aux.toArray(new String[aux.size()]);
                } catch (IOException | NullPointerException e2) { // otherwise, we just get the parameter
                    LOGGER.debug("The parameter is not a path to a file.");
                    LOGGER.debug(e2.getMessage(), e2);
                }
            }
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = parameters[i].trim();
            }

        }
        setKeepNull(keepNullValues);
        if (rand != null) {
            setRandom(rand);
        }
    }

    protected String clean(String extraParameter) {
        StringBuilder res = new StringBuilder(extraParameter.trim());
        while (res.length() > 0 && res.charAt(0) == ',') {
            res.deleteCharAt(0);
        }
        while (res.length() > 0 && res.charAt(res.length() - 1) == ',') {
            res.deleteCharAt(res.length() - 1);
        }
        return res.toString();
    }

    public T generateMaskedRow(T t) {
        if (t == null && keepNull) {
            return null;
        }

        if (t != null && keepEmpty && String.valueOf(t).trim().isEmpty()) {
            return t;
        }

        return doGenerateMaskedField(t);
    }

    /**
     * @param strWithSpaces,
     * resWithoutSpaces
     * @return the res with spaces
     */
    protected String insertFormatInString(String strWithSpaces, StringBuilder resWithoutSpaces) {
        if (strWithSpaces == null || resWithoutSpaces == null) {
            return strWithSpaces;
        }
        for (int i = 0; i < strWithSpaces.length(); i++) {
            if (strWithSpaces.charAt(i) == ' ' || strWithSpaces.charAt(i) == '/' || strWithSpaces.charAt(i) == '-'
                    || strWithSpaces.charAt(i) == '.') {
                resWithoutSpaces.insert(i, strWithSpaces.charAt(i));
            }
        }
        return resWithoutSpaces.toString();
    }

    /**
     * Remove all the spaces in the input string
     * 
     * @param input
     * @return
     */
    protected String removeFormatInString(String input) {
        return StringUtils.replaceEach(input, new String[] { " ", ".", "-", "/" }, new String[] { "", "", "", "" });
    }

    /**
     * DOC jgonzalez Comment method "generateMaskedRow". This method applies a
     * function on a field and returns the its new value.
     * 
     * @param t
     * The input value.
     * @return A new value after applying the function.
     */
    protected abstract T doGenerateMaskedField(T t);
}
