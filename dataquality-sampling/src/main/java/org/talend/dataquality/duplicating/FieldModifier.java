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
package org.talend.dataquality.duplicating;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.language.RefinedSoundex;

public class FieldModifier {

    public enum Function {
        REPLACE_LETTER,
        ADD_LETTER,
        REPLACE_DIGIT,
        ADD_DIGIT,
        REMOVE_LETTER,
        REMOVE_DIGIT,
        EXCHANGE_CHAR,
        SOUNDEX_REPLACE,
        // find this function at SynonymReplaceAction class of org.talend.dataquality.standardization
        SYNONYM_REPLACE,
        SET_TO_BLANK,
        SET_TO_NULL,
        MODIFY_DATE_VALUE,
        SWITCH_DAY_MONTH_VALUE,
        REPLACE_BY_RANDOM_DATE
    }

    private static final String LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //$NON-NLS-1$

    private static final String DIGIT = "0123456789"; //$NON-NLS-1$

    public static final String US_ENGLISH_MAPPING_STRING = RefinedSoundex.US_ENGLISH_MAPPING_STRING;

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private final Random random = new Random();

    private static char[] soundexMap = US_ENGLISH_MAPPING_STRING.toCharArray();

    private Map<Character, List<Character>> inverseSoundexMap;

    private DateChanger dateChanger = new DateChanger();

    public void setSeed(long seed) {
        random.setSeed(seed);
        dateChanger.setSeed(seed);
    }

    private Map<Character, List<Character>> getInverseSoundexMap() {
        if (inverseSoundexMap == null) {
            inverseSoundexMap = new HashMap<Character, List<Character>>();
            for (int i = 0; i < soundexMap.length; i++) {
                List<Character> charSet = inverseSoundexMap.get(soundexMap[i]);
                if (charSet == null) {
                    charSet = new ArrayList<Character>();
                    inverseSoundexMap.put(soundexMap[i], charSet);
                }
                charSet.add((char) ('A' + i));
            }
        }
        return inverseSoundexMap;
    }

    public Date generateDuplicate(Date date, Function function, int modifCount, String extraParameter) {
        if (date == null || function == null) {
            return date;
        }
        Date newDate = new Date(date.getTime());
        switch (function) {
        case SET_TO_NULL:
            return null;
        case MODIFY_DATE_VALUE:
            for (int i = 0; i < modifCount; i++) {
                dateChanger.modifyDateValue(newDate);
            }
            return newDate;
        case SWITCH_DAY_MONTH_VALUE:
            for (int i = 0; i < modifCount; i++) {
                dateChanger.switchDayMonthValue(newDate);
            }
            return newDate;
        case REPLACE_BY_RANDOM_DATE:
            for (int i = 0; i < modifCount; i++) {
                dateChanger.replaceWithRandomDate(newDate);
            }
            return newDate;
        default:
            break;
        }
        return date;
    }

    public String generateDuplicateString(String str, Function function, int modifCount, String extraParameter) {
        StringBuilder sb = new StringBuilder(str);
        switch (function) {
        case REPLACE_LETTER:
            if (sb.length() > 0) {
                for (int i = 0; i < modifCount; i++) {
                    int pos = random.nextInt(sb.length());
                    int idx = random.nextInt(LETTER.length());
                    sb.setCharAt(pos, LETTER.charAt(idx));
                }
            }
            break;
        case ADD_LETTER:
            for (int i = 0; i < modifCount; i++) {
                int pos = sb.length() == 0 ? 0 : random.nextInt(sb.length());
                int idx = random.nextInt(LETTER.length());
                sb.insert(pos, LETTER.charAt(idx));
            }
            break;
        case REPLACE_DIGIT:
            if (sb.length() > 0) {
                for (int i = 0; i < modifCount; i++) {
                    int pos = random.nextInt(sb.length());
                    int idx = random.nextInt(DIGIT.length());
                    sb.setCharAt(pos, DIGIT.charAt(idx));
                }
            }
            break;
        case ADD_DIGIT:
            for (int i = 0; i < modifCount; i++) {
                int pos = sb.length() == 0 ? 0 : random.nextInt(sb.length());
                int idx = random.nextInt(DIGIT.length());
                if (pos == 0) {
                    idx = random.nextInt(DIGIT.length() - 1) + 1;
                }
                sb.insert(pos, DIGIT.charAt(idx));
            }
            break;
        case REMOVE_LETTER:
            for (int i = 0; i < modifCount; i++) {
                if (sb.length() > 0) {
                    int pos = random.nextInt(sb.length());
                    sb.deleteCharAt(pos);
                }
            }
            break;
        case REMOVE_DIGIT:
            for (int i = 0; i < modifCount; i++) {
                if (sb.length() > 1) {
                    int pos = random.nextInt(sb.length());
                    sb.deleteCharAt(pos);
                }
            }
            break;
        case EXCHANGE_CHAR:
            if (sb.length() > 1) {
                for (int i = 0; i < modifCount; i++) {
                    int pos1 = random.nextInt(sb.length());
                    int pos2 = random.nextInt(sb.length());
                    if (pos1 != pos2) {
                        char ch1 = sb.charAt(pos1);
                        char ch2 = sb.charAt(pos2);
                        sb.setCharAt(pos1, ch2);
                        sb.setCharAt(pos2, ch1);
                    }
                }
            }
            break;
        case SOUNDEX_REPLACE:
            if (sb.length() > 0) {
                List<Character> charSet = new ArrayList<Character>();
                for (int i = 0; i < modifCount; i++) {
                    int pos = random.nextInt(sb.length());
                    char charToReplace = sb.charAt(pos);
                    int idx = Character.toUpperCase(charToReplace) - 'A';
                    if (idx >= 0 && idx < 26) {
                        List<Character> soundexSet = getInverseSoundexMap().get(soundexMap[idx]);
                        if (soundexSet != null) {
                            charSet.clear();
                            charSet.addAll(soundexSet);
                            charSet.remove(charSet.indexOf(Character.toUpperCase(charToReplace)));
                            if (!charSet.isEmpty()) {
                                Character[] charArray = charSet.toArray(new Character[charSet.size()]);
                                Character newChar = charArray[random.nextInt(charArray.length)];
                                if (Character.isLowerCase(charToReplace)) {
                                    newChar = Character.toLowerCase(newChar);
                                }
                                sb.setCharAt(pos, newChar);
                            }
                        }
                    }
                }
            }
            break;
        default:
            return str;
        }

        return sb.toString();
    }

    /**
     * Generate duplicates with modification.
     * 
     * @param obj the value to be duplicated
     * @param function {@link Function}
     * @param modifCount the times of modification
     * @param synonymIndexPath the path of the lucene index being used to generate a similar value.
     * @return
     */
    public Object generateDuplicate(Object obj, Function function, int modifCount, String extraParameter) {
        if (function == Function.SET_TO_BLANK) {
            return EMPTY_STRING;
        } else if (function == Function.SET_TO_NULL) {
            return null;
        }

        String originalStr = (obj == null) ? EMPTY_STRING : String.valueOf(obj);
        return generateDuplicateString(originalStr, function, modifCount, extraParameter);
    }

    /**
     * Getter for random.
     * 
     * @return the random
     */
    public Random getRandom() {
        return this.random;
    }

}
