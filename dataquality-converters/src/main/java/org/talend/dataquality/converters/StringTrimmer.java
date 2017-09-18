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
package org.talend.dataquality.converters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * this class is used to remove trailing and leading characters.<br/>
 * created by msjian on 2017.2.16
 *
 */
public class StringTrimmer {

    public static final Set<Character> WHITESPACE_CHARS_SUPERIOR_ASCII_SPACE = new HashSet<>(Arrays.asList( //
            '\u0085' // NEXT LINE (NEL) //$NON-NLS-1$
            , '\u00A0' // NO-BREAK SPACE //$NON-NLS-1$
            , '\u1680' // OGHAM SPACE MARK //$NON-NLS-1$
            , '\u180E' // MONGOLIAN VOWEL SEPARATOR //$NON-NLS-1$
            , '\u2000' // EN QUAD //$NON-NLS-1$
            , '\u2001' // EM QUAD //$NON-NLS-1$
            , '\u2002' // EN SPACE //$NON-NLS-1$
            , '\u2003' // EM SPACE //$NON-NLS-1$
            , '\u2004' // THREE-PER-EM SPACE //$NON-NLS-1$
            , '\u2005' // FOUR-PER-EM SPACE //$NON-NLS-1$
            , '\u2006' // SIX-PER-EM SPACE //$NON-NLS-1$
            , '\u2007' // FIGURE SPACE //$NON-NLS-1$
            , '\u2008' // PUNCTUATION SPACE //$NON-NLS-1$
            , '\u2009' // THIN SPACE //$NON-NLS-1$
            , '\u200A' // HAIR SPACE //$NON-NLS-1$
            , '\u2028' // LINE SEPARATOR //$NON-NLS-1$
            , '\u2029' // PARAGRAPH SEPARATOR //$NON-NLS-1$
            , '\u202F' // NARROW NO-BREAK SPACE //$NON-NLS-1$
            , '\u205F' // MEDIUM MATHEMATICAL SPACE //$NON-NLS-1$
            , '\u3000' // IDEOGRAPHIC SPACE //$NON-NLS-1$
    ));

    /**
     * Remove trailing and leading characters which may be empty string, space string,\t,\n,\r,\f...any space, break related
     * characters.
     *
     * @param inputStr - the input text.
     * @return String
     */
    public String removeTrailingAndLeadingWhitespaces(String inputStr) {
        if (StringUtils.isEmpty(inputStr)) {
            return inputStr;
        }

        int startIndex = 0;
        int endIndex = inputStr.length();
        char currentCharacter = inputStr.charAt(startIndex);
        while (startIndex < endIndex
                && (currentCharacter <= ' ' || WHITESPACE_CHARS_SUPERIOR_ASCII_SPACE.contains(currentCharacter))) {
            currentCharacter = inputStr.charAt(++startIndex);
        }
        if (startIndex == endIndex)
            return "";
        do {
            currentCharacter = inputStr.charAt(--endIndex);
        } while (currentCharacter <= ' ' || WHITESPACE_CHARS_SUPERIOR_ASCII_SPACE.contains(currentCharacter));

        return inputStr.substring(startIndex, endIndex + 1);
    }

    /**
     * Remove trailing and leading characters and the remove Character is whitespace only. <br/>
     * <br/>
     * Note: this is not equals inputStr.trim() for example: <br/>
     * when the inputStr is ("\t" + "abc "), this method will return ("\t" + "abc"),<br/>
     * but for trim method will return "abc"
     *
     * @param inputStr - the input text.
     * @return String
     */
    public String removeTrailingAndLeading(String inputStr) {
        return removeTrailingAndLeading(inputStr, " "); //$NON-NLS-1$
    }

    /**
     * Remove trailing and leading characters.
     *
     * @param inputStr - the input text.
     * @param removeStr - the remove string.
     * @return String.
     */
    public String removeTrailingAndLeading(String inputStr, String removeStr) {

        if (StringUtils.isEmpty(inputStr) || StringUtils.isEmpty(removeStr)) {
            return inputStr;
        } else if (removeStr.length() == 1) {
            return removeTrailingAndLeading(inputStr, removeStr.charAt(0));
        }

        String result = inputStr;

        while (result.startsWith(removeStr)) {
            result = StringUtils.removeStart(result, removeStr);
        }
        while (result.endsWith(removeStr)) {
            result = StringUtils.removeEnd(result, removeStr);
        }
        return result;
    }

    /**
     * Remove trailing and leading characters more quickly than removeTrailingAndLeading(String, String)
     *
     * @param inputStr
     * @param removeCharacter
     * @return
     */
    public String removeTrailingAndLeading(String inputStr, Character removeCharacter) {
        if (StringUtils.isEmpty(inputStr)) {
            return inputStr;
        }

        int startIndex = 0;
        int endIndex = inputStr.length();
        char currentCharacter = inputStr.charAt(startIndex);
        while (startIndex < endIndex && (currentCharacter == removeCharacter)) {
            currentCharacter = inputStr.charAt(++startIndex);
        }
        if (startIndex == endIndex)
            return "";
        do {
            currentCharacter = inputStr.charAt(--endIndex);
        } while (currentCharacter == removeCharacter);

        return inputStr.substring(startIndex, endIndex + 1);
    }
}
