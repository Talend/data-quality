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
package org.talend.windowkey;

import java.util.StringTokenizer;

import org.talend.dataquality.record.linkage.utils.AsciiUtils;

/**
 * FIXME this class should not provide static utilities.
 * 
 * FIXME all internal strings should be made constant.
 * 
 */
public class AlgoBox {

    private static final FingerprintKeyer FINGERPRINTKEYER = new FingerprintKeyer();

    private static final NGramFingerprintKeyer NGRAMKEYER = new NGramFingerprintKeyer();

    private static final org.apache.commons.codec.language.Soundex soundex = new org.apache.commons.codec.language.Soundex();

    private static final org.apache.commons.codec.language.DoubleMetaphone doublemetaphone = new org.apache.commons.codec.language.DoubleMetaphone();

    private static final org.apache.commons.codec.language.Metaphone metaphone = new org.apache.commons.codec.language.Metaphone();

    private static final org.apache.commons.codec.language.ColognePhonetic colognePhonetic = new org.apache.commons.codec.language.ColognePhonetic();

    /**
     * DOC ytao Comment method "main".
     * 
     * @param args
     */
    public static void main(String[] args) {

        String sInput = null;
        // key algos (notice that it is incorrect to return null, since the operation +)
        System.out.println("first_Char_EW:" + AlgoBox.first_Char_EW(sInput) + "-"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("first_N_Char_EW:" + AlgoBox.first_N_Char_EW(sInput, 2) + "-"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("first_N_Char:" + AlgoBox.first_N_Char(sInput, 5) + "-"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("last_N_Char:" + AlgoBox.last_N_Char(sInput, 3) + "-"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("first_N_Consonants:" + AlgoBox.first_N_Consonants(sInput, 2000) + "-"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("first_N_Vowels:" + AlgoBox.first_N_Vowels(sInput, 1000000) + "-"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("add_Left_Char:" + AlgoBox.add_Left_Char(sInput, "<") + "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        System.out.println("pick_Char:" + AlgoBox.pick_Char(sInput, "1-2;40;0-5") + "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        System.out.println("subStr:" + AlgoBox.subStr(sInput, "1;100") + "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        System.out.println("metaphone:" + AlgoBox.metaphone(sInput) + "-"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("soundex:" + AlgoBox.soundex(sInput) + "-"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("doublemetaphone:" + AlgoBox.doublemetaphone(sInput) + "-"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("exact:" + AlgoBox.exact(sInput) + "-"); //$NON-NLS-1$ //$NON-NLS-2$

        // optional algos (notice that it is no pbm to return null)
        System.out.println("removeDiacriticalMarks:" + AlgoBox.removeDiacriticalMarks(sInput) + "-"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("removeDMAndLowerCase: " + AlgoBox.removeDMAndLowerCase(sInput)); //$NON-NLS-1$
        System.out.println("removeDMAndUpperCase: " + AlgoBox.removeDMAndUpperCase(sInput)); //$NON-NLS-1$
        System.out.println("useDefault: " + AlgoBox.useDefault(sInput, "ytao")); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("add_Right_Char:" + AlgoBox.add_Right_Char(sInput, ">") + "-"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        System.out.println("lowerCase: " + AlgoBox.lowerCase(sInput)); //$NON-NLS-1$
        System.out.println("upperCase: " + AlgoBox.upperCase(sInput)); //$NON-NLS-1$

    }

    // Pick characters
    public static String pick_Char(String sInput, String pattern) {

        if (sInput == null || "".equals(sInput.trim())) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        if (pattern == null || "".equals(pattern.trim())) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        String d_pattern = "^[0-9--;]*"; //$NON-NLS-1$

        if (!pattern.matches(d_pattern)) {
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        String[] arr_1 = pattern.split(";"); //$NON-NLS-1$

        for (String valueOf_arr_1 : arr_1) {

            if (!"".equals(valueOf_arr_1)) { //$NON-NLS-1$
                String[] arr_2 = valueOf_arr_1.split("-"); //$NON-NLS-1$
                int len_arr_2 = arr_2.length;

                if (len_arr_2 == 2) {

                    if ("".equals(arr_2[0]) || "".equals(arr_2[1])) { //$NON-NLS-1$ //$NON-NLS-2$
                        ;
                    } else {
                        sb.append(subStr(sInput, arr_2[0] + ";" + arr_2[1])); //$NON-NLS-1$
                    }

                } else if (len_arr_2 == 1) {
                    if (Integer.parseInt(arr_2[0]) < sInput.length()) {
                        sb.append(sInput.charAt(Integer.parseInt(arr_2[0])));
                    }

                } else {
                    ;
                }

            }

        }

        return sb.toString();

    }

    // First N vowels of the string
    public static String first_N_Consonants(String sInput, int nb) {
        if (sInput == null || "".equals(sInput.trim())) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        String d_pattern = "[a-zA-Z&&[^aeiouAEIOU]]"; //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        int s_len = sInput.length();
        String s;

        for (int i = 0; i < s_len; i++) {
            s = sInput.substring(i, i + 1);
            if (!" ".equals(s) && s.matches(d_pattern) && ((--nb) >= 0)) { //$NON-NLS-1$
                sb.append(s);
            }
        }

        return sb.toString();
    }

    // First N consonants of the string
    public static String first_N_Vowels(String sInput, int nb) {
        if (sInput == null || "".equals(sInput.trim())) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        String d_pattern = "[aeiouAEIOU]"; //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        int s_len = sInput.length();
        String s;

        for (int i = 0; i < s_len; i++) {
            s = sInput.substring(i, i + 1);
            if (!" ".equals(s) && s.matches(d_pattern) && ((--nb) >= 0)) { //$NON-NLS-1$
                sb.append(s);
            }
        }

        return sb.toString();
    }

    // substring
    public static String subStr(String sInput, String pattern) {

        if (sInput == null || "".equals(sInput)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        if (pattern == null) {
            return ""; //$NON-NLS-1$
        } else {
            String d_pattern = "^[0-9]*[;][0-9]*"; //$NON-NLS-1$
            if (pattern.matches(d_pattern)) {
                int beginIndex = Integer.parseInt(pattern.substring(0, pattern.indexOf(";"))); //$NON-NLS-1$
                int endIndex = Integer.parseInt(pattern.substring(pattern.indexOf(";") + 1)); //$NON-NLS-1$

                if (sInput.length() < endIndex) {
                    endIndex = sInput.length();
                }

                if (beginIndex <= endIndex) {
                    return sInput.substring(beginIndex, endIndex);
                }
            }
        }

        return ""; //$NON-NLS-1$
    }

    // first N characters of the string
    public static String first_N_Char(String sInput, int nb) {
        if (sInput == null || "".equals(sInput)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        if (nb < 0) {
            return sInput;
        }

        if (sInput.length() < nb) {
            nb = sInput.length();
        }
        return sInput.substring(0, nb);
    }

    // last N characters of the string

    public static String last_N_Char(String sInput, int nb) {

        if (sInput == null || "".equals(sInput)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }
        int s_len = sInput.length();
        if (s_len < nb) {
            nb = s_len;
        }
        return sInput.substring(s_len - nb);
    }

    // N first characters of each word
    public static String first_N_Char_EW(String sInput, int nb) {

        if (sInput == null || "".equals(sInput)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer tok = new StringTokenizer(sInput);

        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();
            int len_word = word.length();
            for (int i = 0; i < nb && i < len_word; i++) {
                sb.append(word.charAt(i));
            }
        }

        return sb.toString();
    }

    // First character of each word
    public static String first_Char_EW(String sInput) {

        if (sInput == null || "".equals(sInput)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();

        StringTokenizer tok = new StringTokenizer(sInput);

        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();
            sb.append(word.charAt(0));
        }

        return sb.toString();
    }

    public static String soundex(String sInput) {
        if (sInput == null) {
            return ""; //$NON-NLS-1$
        }
        return soundex.soundex(sInput);
    }

    public static String doublemetaphone(String sInput) {
        if (sInput == null) {
            return ""; //$NON-NLS-1$
        }

        return doublemetaphone.doubleMetaphone(sInput);
    }

    public static String metaphone(String sInput) {
        if (sInput == null) {
            return ""; //$NON-NLS-1$
        }

        return metaphone.metaphone(sInput);
    }

    /*-----------------------optional algo---------------------*/
    // Add left position character

    public static String add_Left_Char(String sInput, String position) {

        if (position == null || "".equals(position)) { //$NON-NLS-1$
            return sInput;
        }

        if (sInput == null) {
            sInput = ""; //$NON-NLS-1$
        }

        return position + sInput;
    }

    // Add right position character

    public static String add_Right_Char(String sInput, String position) {

        if (position == null || "".equals(position)) { //$NON-NLS-1$
            return sInput;
        }

        if (sInput == null) {
            sInput = ""; //$NON-NLS-1$
        }

        return sInput + position;
    }

    // Remove diacritical marks
    public static String removeDiacriticalMarks(String sInput) {
        if (sInput == null) {
            return null;
        }
        return AsciiUtils.removeDiacriticalMarks(sInput);
    }

    public static String exact(String sInput) {
        // must set it to "" when it is null. otherwise use + to contact will get "null"
        return sInput == null ? "" : sInput; //$NON-NLS-1$
    }

    public static String useDefault(String sInput, String insteadOf) {

        if (sInput == null || "".equals(sInput)) { //$NON-NLS-1$
            return insteadOf;
        } else {
            return sInput;
        }
    }

    public static String lowerCase(String sInput) {
        if (sInput == null) {
            return null;
        }
        return sInput.toLowerCase();
    }

    public static String upperCase(String sInput) {
        if (sInput == null) {
            return null;
        }
        return sInput.toUpperCase();
    }

    public static String removeDMAndLowerCase(String sInput) {
        if (sInput == null) {
            return null;
        }
        return lowerCase(removeDiacriticalMarks(sInput));
    }

    public static String removeDMAndUpperCase(String sInput) {
        if (sInput == null) {
            return null;
        }
        return upperCase(removeDiacriticalMarks(sInput));
    }

    public static String fingerPrintKey(String sInput) {
        if (sInput == null) {
            return null;
        }
        return FINGERPRINTKEYER.key(sInput);
    }

    public static String nGramKey(String sInput) {
        if (sInput == null) {
            return null;
        }
        return NGRAMKEYER.key(sInput);
    }

    public static String colognePhonetic(String sInput) {
        if (sInput == null) {
            return null;
        }
        return colognePhonetic.colognePhonetic(sInput);
    }

}
