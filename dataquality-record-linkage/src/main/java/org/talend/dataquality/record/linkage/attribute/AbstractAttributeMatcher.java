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
package org.talend.dataquality.record.linkage.attribute;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;
import org.talend.windowkey.FingerprintKeyer;

/**
 * Abstract matcher class for shared operations like blank string checking.
 */
public abstract class AbstractAttributeMatcher implements IAttributeMatcher, ITokenization, Serializable {

    private static final long serialVersionUID = -21096755142812677L;

    private NullOption nullOption = NullOption.nullMatchNull;

    private String attributeName = null;

    protected TokenizedResolutionMethod tokenMethod = TokenizedResolutionMethod.NO;

    private double bestWeightSameOrder;

    private boolean initialComparison = false;

    private boolean fingerPrintApply = false;

    private String regexTokenize = " ";

    @Override
    public float getThreshold() {
        throw new NotImplementedException();
    }

    @Override
    public double getWeight() {
        throw new NotImplementedException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchingWeight(java.lang.String,
     * java.lang.String)
     */
    @Override
    public double getMatchingWeight(String string1, String string2) {
        String str1 = string1, str2 = string2;
        boolean str1IsNull = isNullOrEmpty(str1);
        boolean str2IsNull = isNullOrEmpty(str2);
        switch (nullOption) {
        case nullMatchAll:
            if (str1IsNull || str2IsNull) {
                return 1.0;
            }
            break;
        case nullMatchNone:
            if (str1IsNull || str2IsNull) {
                return 0.0;
            }
            break;
        case nullMatchNull:
            if (str1IsNull && str2IsNull) // both null => match
                return 1.0;
            else if (str1IsNull || str2IsNull) // only one null => non-match
                return 0.0;
            break;
        default:
            break;
        }

        boolean isNullorEmptyStr1 = !isNullOrEmpty(str1);
        boolean isNullorEmptyStr2 = !isNullOrEmpty(str2);
        assert isNullorEmptyStr1 : "string should not be null or empty here"; //$NON-NLS-1$
        assert isNullorEmptyStr2 : "string should not be null or empty here"; //$NON-NLS-1$
        // TDQ-10366 qiongli,catch the Exception.
        if (fingerPrintApply) {
            FingerprintKeyer fg = new FingerprintKeyer();
            str1 = fg.key(str1);
            str2 = fg.key(str2);
        }

        return weight(str1, str2);

    }

    private double weight(String str1, String str2) {
        double w;
        switch (tokenMethod) {
        case NO:
            w = getWeight(str1, str2);
            break;
        case ANYORDER:
            w = computeWeightTokenHungarian(str1, str2);
            break;
        case SAMEPLACE:
            w = computeWeightTokenSamePlace(str1, str2);
            break;
        case SAMEORDER:
            w = computeWeightTokenSameOrder(str1, str2);
            break;
        default:
            throw new UnsupportedOperationException();
        }
        return w;
    }

    /**
     * 
     * DOC dprot if one of the two strings correspond to an initial, return 0 or 1 according to the other initial
     * if not, return -1
     * 
     * @param str1
     * @param str2
     * @return
     */
    private int getInitialSimilarity(String str1, String str2) {
        String strShort = str1, strLong = str2;
        long strShortCPCount = strShort.codePoints().count();
        long strLongCPCount = strLong.codePoints().count();
        if (strShortCPCount > strLongCPCount) {
            strShort = str2;
            strLong = str1;
        }
        if (strLongCPCount == 1
                || (strLongCPCount == 2 && ".".equals(strShort.substring(strShort.offsetByCodePoints(0, 1), 2)))) {
            if (strShort.codePointAt(0) == strLong.codePointAt(0))
                return 1;
            else
                return 0;
        }
        return -1;
    }

    private double computeWeightTokenHungarian(String str1, String str2) {

        // --- Compute the lists of tokens
        String[] list1 = str1.split(regexTokenize);
        String[] list2 = str2.split(regexTokenize);

        // --- Create the matrix of weights
        int n = list1.length;
        int m = list2.length;

        // --- TDQ-12486 String with only whitespaces
        if (n == 0) {
            list1 = new String[1];
            list1[0] = str1;
            n = 1;
        }
        if (m == 0) {
            list2 = new String[1];
            list2[0] = str2;
            m = 1;
        }

        int maxDim = Math.max(n, m);
        double[][] weights = new double[maxDim][maxDim];
        for (double[] row : weights)
            Arrays.fill(row, 0.0);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                fillWeight(weights, i, j, list1, list2);
            }
        }

        // --- Compute the maximum weighted matching
        int[] match = new HungarianAlgorithm(weights).execute();

        // --- Compute the weight
        double weight = 0;
        for (int i = 0; i < maxDim; i++) {
            if (i < n && match[i] < m) {
                weight += (1 - weights[i][match[i]]);
            }
        }
        weight /= maxDim;
        return weight;
    }

    private void fillWeight(double[][] weights, int i, int j, String[] list1, String[] list2) {
        if (initialComparison) {
            int simInitial = getInitialSimilarity(list1[i], list2[j]);
            if (simInitial != -1)
                weights[i][j] = 1.0 - simInitial;
            else {
                double similarityMeasure = getWeight(list1[i], list2[j]);
                weights[i][j] = 1 - similarityMeasure;
            }
        } else {
            double similarityMeasure = getWeight(list1[i], list2[j]);
            weights[i][j] = 1 - similarityMeasure;
        }

    }

    private double computeWeightTokenSamePlace(String str1, String str2) {
        // --- Compute the lists of tokens
        String[] list1 = str1.split(regexTokenize);
        String[] list2 = str2.split(regexTokenize);

        // --- Create the matrix of weights
        int n = list1.length;
        int m = list2.length;
        // --- TDQ-12486 String with only whitespaces
        if (n == 0) {
            list1 = new String[1];
            list1[0] = str1;
            n = 1;
        }
        if (m == 0) {
            list2 = new String[1];
            list2[0] = str2;
            m = 1;
        }
        int maxDim = Math.max(n, m);
        int minDim = Math.min(n, m);

        double weight = 0;
        for (int i = 0; i < minDim; i++) {
            if (initialComparison) {
                int w = getInitialSimilarity(list1[i], list2[i]);
                if (w != -1) {
                    weight += w;
                } else {
                    weight += getWeight(list1[i], list2[i]);
                }
            } else {
                weight += getWeight(list1[i], list2[i]);
            }
        }
        weight /= maxDim;
        return weight;
    }

    // Can we improve this method ? Especially the recursive call
    private double computeWeightTokenSameOrder(String str1, String str2) {
        // --- Compute the lists of tokens
        String[] list1 = str1.split(regexTokenize);
        String[] list2 = str2.split(regexTokenize);

        // --- Create the matrix of weights
        int n = list1.length;
        int m = list2.length;
        // --- TDQ-12486 String with only whitespaces
        if (n == 0) {
            list1 = new String[1];
            list1[0] = str1;
            n = 1;
        }
        if (m == 0) {
            list2 = new String[1];
            list2[0] = str2;
            m = 1;
        }
        String[] shortString, longString;
        int maxDim, minDim;
        if (n < m) {
            shortString = list1;
            longString = list2;
            minDim = n;
            maxDim = m;
        } else {
            shortString = list2;
            longString = list1;
            minDim = m;
            maxDim = n;
        }

        // Loop on all the combination of minDim out of maxDim
        bestWeightSameOrder = 0;
        combinations(longString, minDim, 0, new String[minDim], shortString);
        bestWeightSameOrder /= maxDim;
        return bestWeightSameOrder;
    }

    private void combinations(String[] longString, int len, int startPosition, String[] result, String[] shortString) {
        if (len == 0) {
            // Compute weight:
            double weight = computeWeight(shortString, result);

            if (bestWeightSameOrder < weight)
                bestWeightSameOrder = weight;
            return;
        }
        for (int i = startPosition; i <= longString.length - len; i++) {
            result[result.length - len] = longString[i];
            combinations(longString, len - 1, i + 1, result, shortString);
        }
    }

    private double computeWeight(String[] shortString, String[] result) {
        double weight = 0.0;
        for (int i = 0; i < shortString.length; i++) {
            if (initialComparison) {
                int w = getInitialSimilarity(result[i], shortString[i]);
                if (w != -1) {
                    weight += w;
                } else {
                    weight += getWeight(result[i], shortString[i]);
                }
            } else {
                weight += getWeight(result[i], shortString[i]);
            }
        }

        return weight;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str); //$NON-NLS-1$
    }

    /**
     * Calculate matching weight using specified matcher.
     * 
     * @param record1 the first string
     * @param record2 the second string
     * @return result between 0 and 1
     */
    protected abstract double getWeight(String record1, String record2);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#setNullOption(org.talend.dataquality.record
     * .linkage.attribute.IAttributeMatcher.NullOption)
     */
    @Override
    public void setNullOption(NullOption option) {
        this.nullOption = option;
    }

    @Override
    public NullOption getNullOption() {
        return nullOption;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getAttributeName()
     */
    @Override
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Getter for fingerPrintApply.
     * 
     * @return the fingerPrintApply
     */
    public boolean isFingerPrintApply() {
        return fingerPrintApply;
    }

    /**
     * Getter for tokenMethod.
     * 
     * @return the tokenMethod
     */
    public TokenizedResolutionMethod getTokenMethod() {
        return tokenMethod;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#setAttributeName(java.lang.String)
     */
    @Override
    public void setAttributeName(String name) {
        this.attributeName = name;
    }

    /**
     * Getter for initialComparison.
     * 
     * @return the initialComparison
     */
    public boolean isInitialComparison() {
        return initialComparison;
    }

    /**
     * Sets the tokenMethod.
     * 
     * @param tokenMethod the tokenMethod to set
     */
    @Override
    public void setTokenMethod(TokenizedResolutionMethod tokenMethod) {
        this.tokenMethod = tokenMethod;
    }

    /**
     * Sets the initialComparison.
     * 
     * @param initialComparison the initialComparison to set
     */
    public void setInitialComparison(boolean initialComparison) {
        this.initialComparison = initialComparison;
    }

    /**
     * Sets the regexTokenize.
     * 
     * @param regexTokenize the regexTokenize to set
     */
    public void setRegexTokenize(String regexTokenize) {
        this.regexTokenize = regexTokenize;
    }

    /**
     * Sets the fingerPrintApply.
     * 
     * @param fingerPrintApply the fingerPrintApply to set
     */
    public void setFingerPrintApply(boolean fingerPrintApply) {
        this.fingerPrintApply = fingerPrintApply;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#setNullOption(java.lang.String)
     */
    @Override
    public void setNullOption(String option) {
        if (IAttributeMatcher.NullOption.nullMatchAll.name().equalsIgnoreCase(option)) {
            this.nullOption = IAttributeMatcher.NullOption.nullMatchAll;
        } else if (IAttributeMatcher.NullOption.nullMatchNone.name().equalsIgnoreCase(option)) {
            this.nullOption = IAttributeMatcher.NullOption.nullMatchNone;
        } else if (IAttributeMatcher.NullOption.nullMatchNull.name().equalsIgnoreCase(option)) {
            this.nullOption = IAttributeMatcher.NullOption.nullMatchNull;
        } else {
            this.nullOption = IAttributeMatcher.NullOption.nullMatchNull;
        }
    }

}
