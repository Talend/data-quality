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

import org.apache.commons.lang.StringUtils;

/**
 * this class is used to remove trailing and leading characters.<br/>
 * created by msjian on 2017.2.16
 * 
 */
public class StringTrimmer {

    public String[] WHITESPACE_CHARS = new String[] { "\t" // CHARACTER TABULATION //$NON-NLS-1$
            , "\n" // LINE FEED (LF) //$NON-NLS-1$
            , '\u000B' + "" // LINE TABULATION //$NON-NLS-1$
            , "\f" // FORM FEED (FF) //$NON-NLS-1$
            , "\r" // CARRIAGE RETURN (CR) //$NON-NLS-1$
            , " " // SPACE //$NON-NLS-1$
            , '\u0085' + "" // NEXT LINE (NEL) //$NON-NLS-1$
            , '\u00A0' + "" // NO-BREAK SPACE //$NON-NLS-1$
            , '\u1680' + "" // OGHAM SPACE MARK //$NON-NLS-1$
            , '\u180E' + "" // MONGOLIAN VOWEL SEPARATOR //$NON-NLS-1$
            , '\u2000' + "" // EN QUAD //$NON-NLS-1$
            , '\u2001' + "" // EM QUAD //$NON-NLS-1$
            , '\u2002' + "" // EN SPACE //$NON-NLS-1$
            , '\u2003' + "" // EM SPACE //$NON-NLS-1$
            , '\u2004' + "" // THREE-PER-EM SPACE //$NON-NLS-1$
            , '\u2005' + "" // FOUR-PER-EM SPACE //$NON-NLS-1$
            , '\u2006' + "" // SIX-PER-EM SPACE //$NON-NLS-1$
            , '\u2007' + "" // FIGURE SPACE //$NON-NLS-1$
            , '\u2008' + "" // PUNCTUATION SPACE //$NON-NLS-1$
            , '\u2009' + "" // THIN SPACE //$NON-NLS-1$
            , '\u200A' + "" // HAIR SPACE //$NON-NLS-1$
            , '\u2028' + "" // LINE SEPARATOR //$NON-NLS-1$
            , '\u2029' + "" // PARAGRAPH SEPARATOR //$NON-NLS-1$
            , '\u202F' + "" // NARROW NO-BREAK SPACE //$NON-NLS-1$
            , '\u205F' + "" // MEDIUM MATHEMATICAL SPACE //$NON-NLS-1$
            , '\u3000' + "" // IDEOGRAPHIC SPACE //$NON-NLS-1$
    };

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

        String result = inputStr;
        while (StringUtils.startsWithAny(result, WHITESPACE_CHARS)) {
            result = StringUtils.removeStart(result, result.substring(0, 1));
        }

        while (StringUtils.endsWithAny(result, WHITESPACE_CHARS)) {
            result = StringUtils.removeEnd(result, result.substring(result.length() - 1, result.length()));
        }
        return result;
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
     * @param removeCharacter - the remove character.
     * @return String.
     */
    public String removeTrailingAndLeading(String inputStr, String removeCharacter) {
        if (StringUtils.isEmpty(inputStr) || StringUtils.isEmpty(removeCharacter)) {
            return inputStr;
        }

        String result = inputStr;

        while (result.startsWith(removeCharacter)) {
            result = StringUtils.removeStart(result, removeCharacter);
        }
        while (result.endsWith(removeCharacter)) {
            result = StringUtils.removeEnd(result, removeCharacter);
        }
        return result;
    }

}
