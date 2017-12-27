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
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2003-2007 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution License ("CDDL")(the
 * "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the License at https://open-dm-mi.dev.java.net/cddl.html or
 * open-dm-mi/bootstrap/legal/license.txt. See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * When distributing the Covered Code, include this CDDL Header Notice in each file and include the License file at
 * open-dm-mi/bootstrap/legal/license.txt. If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */
package org.talend.dataquality.record.linkage.contribs.algorithm;

import org.talend.dataquality.record.linkage.utils.AsciiUtils;

/**
 * Encode a string using a version of French Soundex algorithm (Soundex2 by .
 * 
 * @version $Revision: 1.1 $
 * 
 * MOD 2010-01-22 scorreia: only changed the import and removed a javadoc comment in "encode" method refering to
 * PhoneticEncoderException.
 * 
 * MOD scorreia 2010-02-18 replace str by trimmed str
 */
public class SoundexFR implements java.io.Serializable {

    private static final long serialVersionUID = 3547140878285556319L;

    /** Informative String about the encoding type this encoder does . * */
    public static final String ENCODING_TYPE = "SoundEx_FR"; //$NON-NLS-1$

    private static final String[] GROUP1_INPUT = { "GUI", "GUE", "GA", "GO", "GU", "CA", "CO", "CU", "Q", "CC", "CK" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$

    private static final String[] GROUP1_OUTPUT = { "KI", "KE", "KA", "KO", "K", "KA", "KO", "KU", "K", "K", "K" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$

    private static final String[] GROUP2_INPUT = { "MAC", "ASA", "KN", "PF", "SCH", "PH" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    private static final String[] GROUP2_OUTPUT = { "MCC", "AZA", "NN", "FF", "SSS", "FF" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    private static final char[] VOWELS = { 'E', 'I', 'O', 'U' };

    private static final int ONE_NINE_ONE = 191;

    private static final int FOUR = 4;

    /**
     * Creates a new Soundex encoder.
     */
    public SoundexFR() {
        // Do nothing
    }

    /**
     * Encode a string phoneticaly with SoundEx.
     * 
     * This algorithm is sampled from the code-snippers on http://www.sourceforge.net/
     * 
     * @param str The string to encode
     * @return the encoded version of the strToEncode
     */
    public String encode(String str) {
        if (str == null) {
            return null;
        }

        // Trim the token from trailing spaces
        // str.trim(); // MOD scorreia 2010-02-18 replace str by trimmed str
        String tempStr = str.trim();
        int size = tempStr.length();
        char ch;
        int i;

        // If the token is empty, return an empty 4 character-long string
        if (size == 0) {
            return "    "; //$NON-NLS-1$
        }

        // Convert all charatcers to uppercase
        tempStr = tempStr.toUpperCase();

        // If the token is one character long, return the character and 3 spaces
        if (size == 1) {
            return tempStr + "   "; //$NON-NLS-1$
        }

        // Wrap the String into a Stringbuffer
        StringBuilder word = new StringBuilder(tempStr);

        // Remove all diacretical marks

        for (i = 0; i < size; i++) {
            ch = tempStr.charAt(i);

            // Remove all special characters and numbers
            if (!Character.isLetter(ch)) {
                word.deleteCharAt(i);
                size--;
                i--;
                continue;
            }

            if (ch > ONE_NINE_ONE) {
                word.setCharAt(i, AsciiUtils.removeDiacriticalMark(ch));
            }
        }

        // Replace the group1 of substrings with their corresponding outputs
        size = GROUP1_INPUT.length;
        tempStr = word.toString();

        for (i = 0; i < size; i++) {
            tempStr = tempStr.replaceAll(GROUP1_INPUT[i], GROUP1_OUTPUT[i]);
        }

        // Replace all vowels (E,I,O,U) with A (excluding the fist character?)
        size = tempStr.length();

        for (i = 1; i < size; i++) {
            ch = tempStr.charAt(i);
            if (isVowel(ch)) {
                tempStr = tempStr.replace(ch, 'A');
            }
        }

        // Replace the group2 of substrings with their corresponding outputs
        size = GROUP2_INPUT.length;

        for (i = 0; i < size; i++) {
            tempStr = tempStr.replaceAll(GROUP2_INPUT[i], GROUP2_OUTPUT[i]);
        }

        // Remove all Hs if they are not preceeded by C or S
        size = tempStr.length();
        word.setLength(0);
        word.append(tempStr);

        for (i = 0; i < size; i++) {
            if (word.charAt(i) == 'H' && !(i > 0 && (word.charAt(i - 1) == 'C' || word.charAt(i - 1) == 'S'))) {
                word.deleteCharAt(i);
                size--;
                i--;
            }
        }

        // Remove all Ys, unless they are preceeded by As
        for (i = 0; i < size; i++) {
            if (word.charAt(i) == 'Y' && !(i > 0 && word.charAt(i - 1) == 'A')) {
                word.deleteCharAt(i);
                size--;
                i--;
            }
        }

        // MOD scorreia 2010-02-18 ensure that some characters remain
        if (size <= 0) { // could be zero when input is "y" for example then it would result in a out of bound index
            return "    "; //$NON-NLS-1$
        }

        // Remove the following characters (A, T, D, S) if they are the last character
        ch = word.charAt(size - 1);
        if (ch == 'A' || ch == 'T' || ch == 'D' || ch == 'S') {
            word.deleteCharAt(size - 1);
            size--;
        }

        // Remove all As. Keep it if it is the first character of the token
        for (i = 1; i < size; i++) {
            if (word.charAt(i) == 'A') {
                word.deleteCharAt(i);
                size--;
                i--;
            }
        }

        // Remove all subsets of similar successive characters
        removeSimilarGroupChars(word);

        // Keep only the first 4 characters (if the token is smaller, add spaces).
        size = word.length();

        if (word.length() >= FOUR) {
            word.setLength(FOUR);
        } else {
            while (size++ < FOUR) {
                word.append(" "); //$NON-NLS-1$
            }
        }

        return word.toString();
    }

    /**
     * Tests if the character is a vowel.
     * 
     * @param ch the character to test
     * @return true if character is a vowel, otherwise, false
     */
    private boolean isVowel(char ch) {
        int size = VOWELS.length;

        for (int i = 0; i < size; i++) {
            if (ch == VOWELS[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes duplicate characters in a word if they are in succession.
     * 
     * @param word the string buffer containing the word to check and modify
     */
    private void removeSimilarGroupChars(StringBuilder word) {
        int size = word.length();

        for (int i = 0; i < size; i++) {
            if (i < size - 1 && word.charAt(i) == word.charAt(i + 1)) {
                word.deleteCharAt(i);
                size--;
                i--;
            }
        }
    }

    /**
     * Getter for the encoding type attribute.
     * 
     * @return the encoding type the Phonetic Encoder implements (such as NYSIIS or Soundex) This value is for
     * information purposes and is not enforced to be of the same value as the 'encoding type' listed in the
     * PhoneticEncodersConfiguration for the encoder class instance. As a result it should not be used for calls to the
     * Phoneticizer.
     */
    public String getEncodingType() {
        return ENCODING_TYPE;
    }

}
