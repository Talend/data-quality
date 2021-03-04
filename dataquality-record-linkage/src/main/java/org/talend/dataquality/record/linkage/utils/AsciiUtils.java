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
package org.talend.dataquality.record.linkage.utils;

/**
 * Utility class for removal of accents in texts. This solution handles only the accented letters in latin-1 suppliment
 * charset,
 * but is much faster than StringUtils.stripAccents(input) from Apache Commons Lang package.
 */
public final class AsciiUtils {

    // private constructor, can't be instanciated!
    private AsciiUtils() {
    }

    /**
     * Removes diacritical mark from a character.
     * 
     * @param ch a character
     * @return the same input character without the diacritical mark if any.
     */
    public static char removeDiacriticalMark(char c) {

        if (c < 192)
            return c;
        if (c >= 192 && c <= 197)
            return 'A';
        if (c == 199)
            return 'C';
        if (c >= 200 && c <= 203)
            return 'E';
        if (c >= 204 && c <= 207)
            return 'I';
        if (c == 209)
            return 'N';
        if (c >= 210 && c <= 214)
            return 'O';
        if (c >= 217 && c <= 220)
            return 'U';
        if (c == 221)
            return 'Y';
        if (c >= 224 && c <= 229)
            return 'a';
        if (c == 231)
            return 'c';
        if (c >= 232 && c <= 235)
            return 'e';
        if (c >= 236 && c <= 239)
            return 'i';
        if (c == 241)
            return 'n';
        if (c >= 242 && c <= 246)
            return 'o';
        if (c >= 249 && c <= 252)
            return 'u';
        if (c == 253 || c == 255)
            return 'y';

        return c;
    }

    /**
     * Removes diacritical marks from a string.
     * 
     * @param st a string
     * @return a new string without the diacritical mark if any.
     */
    public static String removeDiacriticalMarks(String st) {
        if (st == null) {
            return null;
        }

        final int len = st.length();
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            sb.append(removeDiacriticalMark(st.charAt(i)));
        }
        return sb.toString();
    }

}
