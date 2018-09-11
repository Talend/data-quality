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
    private Set<Integer> codePointSet;

    // Useful for quick get
    private List<Integer> codePointList;

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
        codePointSet = new HashSet<>();
        codePointList = new ArrayList<>();

        for (String subPattern : pattern.substring(1, pattern.length() - 1).split("\\|")) {
            if (subPattern.contains("-")) {
                String[] startEnd = subPattern.split("-");
                Integer start = getCodePoint(startEnd[0].substring(1));
                Integer end = getCodePoint(startEnd[1].substring(0, startEnd[1].length() - 1));
                for (int i = start; i <= end; i++) {
                    if (codePointSet.contains(i))
                        throw new IllegalArgumentException("Pattern " + subPattern + " is in conflict with another pattern");
                    codePointSet.add(i);
                    codePointList.add(i);
                }
            } else {
                Integer codePoint = getCodePoint(subPattern);
                if (codePointSet.contains(codePoint))
                    throw new IllegalArgumentException("Pattern " + subPattern + " is in conflict with another pattern");
                codePointSet.add(codePoint);
                codePointList.add(codePoint);
            }
        }
    }

    private Integer getCodePoint(String s) {
        return Integer.parseInt(s.substring(3, s.length() - 1), 16);
    }

    public Character getReplaceChar() {
        return replaceChar;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean contains(Integer c) {
        return codePointSet.contains(c);
    }

    public Integer getCodePoint(int position) {
        return codePointList.get(position);
    }

    public int getSize() {
        return codePointList.size();
    }
}
