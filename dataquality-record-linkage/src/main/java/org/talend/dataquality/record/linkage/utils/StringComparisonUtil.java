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

/**
 * @author scorreia
 * 
 * 
 */
public final class StringComparisonUtil implements Serializable {

    private static final long serialVersionUID = 8786564034104314360L;

    /**
     * maximum prefix length to use.
     */
    private static final int MINPREFIXTESTLENGTH = 6;

    /**
     * StringComparisonUtil constructor.
     */
    private StringComparisonUtil() {
    }

    /**
     * Returns the number of characters in the two Strings that are the same.
     * 
     * @param str1 a String.
     * @param str2 a String.
     * @return The number of characters in the two Strings that are the same.
     * 
     */
    public static int difference(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0;
        }
        int str1CPCount = str1.codePointCount(0, str1.length());
        int str2CPCount = str2.codePointCount(0, str2.length());
        int lengthToMatch = Math.min(str1CPCount, str2CPCount);
        int diff = 0;
        for (int i = 0; i < lengthToMatch; i++) {
            if (str1.codePointAt(i) == str2.codePointAt(i)) {
                diff++;
            }
        }
        return diff;
    }

    // scorreia: this method comes from the simmetrics library (no change has been done).
    /**
     * returns a string buffer of characters from string1 within string2 if they are of a given distance seperation from
     * the position in string1.
     * 
     * @param string1
     * @param string2
     * @param distanceSep
     * @return a string buffer of characters from string1 within string2 if they are of a given distance seperation from
     * the position in string1
     */
    public static StringBuilder getCommonCharacters(final String string1, final String string2, final int distanceSep) {
        // create a return buffer of characters
        final StringBuilder returnCommons = new StringBuilder();
        // create a copy of string2 for processing
        final StringBuilder copy = new StringBuilder(string2);
        // iterate over string1
        long str1CPCount = string1.codePoints().count();
        long str2CPCount = string2.codePoints().count();
        for (int i = 0; i < str1CPCount; i++) {
            final int ch = string1.codePointAt(i);
            // set boolean for quick loop exit if found
            boolean foundIt = false;
            // compare char with range of characters to either side
            // MOD scorreia 2010-01-25 for identical strings, this method should return the full input string. I checked
            // against second string and it now gives the same results
            for (int j = Math.max(0, i - distanceSep); !foundIt && j < Math.min(i + distanceSep + 1, str2CPCount); j++) {
                // check if found
                if (copy.codePointAt(j) == ch) {
                    foundIt = true;
                    // append character found
                    returnCommons.appendCodePoint(copy.codePointAt(j));
                    // alter copied string2 for processing
                    copy.setCharAt(j, (char) 0);
                }
            }
        }
        return returnCommons;
    }

    /**
     * gets the prefix length found of common characters at the begining of the strings.
     * 
     * @param string1
     * @param string2
     * @return the prefix length found of common characters at the begining of the strings
     */
    public static int getPrefixLength(final String string1, final String string2) {
        if (string1 == null || string2 == null) {
            return 0;
        }

        int str1CPCount = string1.codePointCount(0, string1.length());
        int str2CPCount = string2.codePointCount(0, string2.length());
        final int n = Math.min(MINPREFIXTESTLENGTH, Math.min(str1CPCount, str2CPCount));
        // check for prefix similarity of length n
        for (int i = 0; i < n; i++) {
            // check the prefix is the same so far
            if (string1.codePointAt(i) != string2.codePointAt(i)) {
                // not the same so return as far as got
                return i;
            }
        }
        return n; // first n characters are the same
    }
}
