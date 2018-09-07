package org.talend.dataquality.common.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum CharPatternToRegexEnum {

    DIGIT('9', CharPatternToRegexConstants.DIGIT),

    LOWER_LATIN('a', CharPatternToRegexConstants.LOWER_LATIN),

    UPPER_LATIN('A', CharPatternToRegexConstants.UPPER_LATIN),

    FULLWIDTH_DIGIT('9', CharPatternToRegexConstants.FULLWIDTH_DIGIT),

    FULLWIDTH_LOWER_LATIN('a', CharPatternToRegexConstants.FULLWIDTH_LOWER_LATIN),

    FULLWIDTH_UPPER_LATIN('A', CharPatternToRegexConstants.FULLWIDTH_UPPER_LATIN),

    LOWER_HIRAGANA('h', CharPatternToRegexConstants.LOWER_HIRAGANA),

    UPPER_HIRAGANA('H', CharPatternToRegexConstants.UPPER_HIRAGANA),

    LOWER_KATAKANA('k', CharPatternToRegexConstants.LOWER_KATAKANA),

    UPPER_KATAKANA('K', CharPatternToRegexConstants.UPPER_KATAKANA),

    KANJI('C', CharPatternToRegexConstants.KANJI),

    HANGUL('G', CharPatternToRegexConstants.HANGUL);

    private Character replaceChar;

    private String pattern;

    // Useful for quick contain
    private Set<Character> characterSet;

    // Useful for quick get
    private List<Character> characterList;

    private static final Map<Character, CharPatternToRegexEnum> lookup = new HashMap<>();

    static {
        for (CharPatternToRegexEnum value : CharPatternToRegexEnum.values()) {
            lookup.put(value.getReplaceChar(), value);
        }
    }

    public static CharPatternToRegexEnum get(final String pattern) {
        return lookup.get(pattern);
    }

    CharPatternToRegexEnum(char replace, String pattern) {
        replaceChar = replace;
        this.pattern = pattern;
        buildCharacters(pattern);
    }

    private void buildCharacters(String pattern) {
        characterSet = new HashSet<>();
        characterList = new ArrayList<>();

        for (String subPattern : pattern.substring(1, pattern.length() - 1).split("\\|")) {
            if (subPattern.contains("-")) {
                String[] startEnd = subPattern.split("-");
                char start = getChar(startEnd[0]);
                char end = getChar(startEnd[1]);
                for (int i = start; i <= end; i++) {
                    characterSet.add((char) i);
                    characterList.add((char) i);
                }
            } else {
                char character = getChar(subPattern);
                characterSet.add(character);
                characterList.add(character);
            }
        }
    }

    private char getChar(String s) {
        return (char) Integer.parseInt(s.substring(2), 16);
    }

    public Character getReplaceChar() {
        return replaceChar;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean contains(char c) {
        return characterSet.contains(c);
    }

    public char getCharacter(int position) {
        return characterList.get(position);
    }

    public int getSize() {
        return characterList.size();
    }
}
