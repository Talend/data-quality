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

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.generic.Alphabet;

import java.security.SecureRandom;

/**
 * created by jgonzalez on 18 juin 2015. This class is an abstract class that
 * all other functions extend. All the methods and fields that all functions
 * share are stored here.
 */
public abstract class Function<T> implements Serializable {

    public static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //$NON-NLS-1$

    public static final String LOWER = "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$

    protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

    protected static final Pattern patternSpace = Pattern.compile("\\s+");

    protected static final Pattern nonDigits = Pattern.compile("\\D+");

    private static final long serialVersionUID = 6333987486134315822L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Function.class);

    protected static final String ERROR_MESSAGE = "Configuration issue (check your parameters)";

    protected Random rnd = new SecureRandom();

    protected String[] parameters;

    protected boolean keepNull = false;

    protected boolean keepInvalidPattern = false;

    protected boolean keepEmpty = false;

    protected boolean keepFormat = false;

    protected String seed = RandomStringUtils.randomAlphanumeric(4);

    protected FunctionMode maskingMode = FunctionMode.RANDOM;

    /**
     * getter for random
     *
     * @return the random object
     */
    public Random getRandom() {
        return rnd;
    }

    /**
     * setter for random
     *
     * @param rnd The pseudo-random number generator
     */
    public void setRandom(Random rnd) {
        this.rnd = rnd == null ? new SecureRandom() : rnd;
    }

    /**
     * DOC jgonzalez Comment method "setKeepNull". This function sets a boolean
     * used to keep null values.
     *
     * @param keep The value of the boolean.
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
     * @param extraParameter The parameter we try to parse.
     * @param keepNullValues The parameter used for setKeepNull.
     */
    public void parse(String extraParameter, boolean keepNullValues) {
        if (extraParameter != null) {
            parameters = getParameters(extraParameter);
            if (parameters.length == 1 && isNeedCheckPath()
                    && (!isBothValidForFileOrNot() || !StringUtils.EMPTY.equals(parameters[0]))) {
                // check if it's a path to a readable file
                // For an empty param that is not mandatory, we do not want to return an error
                try {
                    List<String> aux = KeysLoader.loadKeys(parameters[0].trim());
                    parameters = aux.toArray(new String[aux.size()]);
                } catch (IOException | NullPointerException e2) { // otherwise, we just get the parameter
                    LOGGER.warn(e2.getMessage(), e2);
                    if (!isBothValidForFileOrNot()) {
                        resetParameterTo(ERROR_MESSAGE);
                    }
                }
            }
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = parameters[i].trim();
            }
        }

        setKeepNull(keepNullValues);
    }

    @Deprecated
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        parse(extraParameter, keepNullValues);
        setRandom(rand);
    }

    /**
     * Reset the parameter
     */
    protected void resetParameterTo(String errorMessage) {
        // no need do anything
    }

    /**
     * Judge whether current function need to check parameter as a path
     */
    protected boolean isNeedCheckPath() {
        return false;
    }

    /**
     * Judge whether the parameter can be both file and value
     */
    protected boolean isBothValidForFileOrNot() {
        return false;
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

    private String[] getParameters(String extraParameter) {
        return clean(extraParameter).split(",");
    }

    protected int getNumberParameters(String extraParameter) {
        int numberParameters = 0;
        if (extraParameter != null) {
            numberParameters = getParameters(extraParameter).length;
        }
        return numberParameters;
    }

    public T generateMaskedRow(T t) {
        return generateMaskedRow(t, maskingMode);
    }

    public T generateMaskedRow(T t, FunctionMode mode) {
        if (t == null && keepNull) {
            return null;
        }

        if (t != null && keepEmpty && String.valueOf(t).trim().isEmpty()) {
            return t;
        }

        try {
            return doGenerateMaskedField(t, mode);
        } catch (NotImplementedException e) {
            return doGenerateMaskedField(t);
        }
    }

    /**
     * @param strWithSpaces, resWithoutSpaces
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
     * @param t The input value.
     * @return A new value after applying the function.
     */
    protected abstract T doGenerateMaskedField(T t);

    protected T doGenerateMaskedField(T t, FunctionMode mode) {
        throw new NotImplementedException();
    }

    public void setSecret(FormatPreservingMethod method, String secret) {
        throw new UnsupportedOperationException("The class " + this.getClass().getName() + " should not use a secret.");
    }

    public void setAlphabet(Alphabet alphabet) {
        throw new UnsupportedOperationException("The class " + this.getClass().getName() + " should not use an alphabet.");
    }

    protected int nextRandomDigit(Random r) {
        return r.nextInt(10);
    }

    protected int nextRandomDigit() {
        return rnd.nextInt(10);
    }

    protected Random getRandomForObject(Object toBeReplaced) {
        if (toBeReplaced == null) {
            return new Random(seed.hashCode());
        } else {
            return new Random(toBeReplaced.hashCode() ^ seed.hashCode());
        }
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getSeed() {
        return seed;
    }

    public void setMaskingMode(FunctionMode maskingMode) {
        this.maskingMode = maskingMode;
    }

    public FunctionMode getMaskingMode() {
        return maskingMode;
    }
}
