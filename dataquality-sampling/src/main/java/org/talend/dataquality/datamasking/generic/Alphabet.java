package org.talend.dataquality.datamasking.generic;

import java.util.HashMap;
import java.util.Map;

import org.talend.daikon.pattern.character.CharPattern;

public enum Alphabet {

    DEFAULT_LATIN(new CharPattern[] { CharPattern.LOWER_LATIN, CharPattern.UPPER_LATIN, CharPattern.DIGIT }),

    LATIN_LETTERS(new CharPattern[] { CharPattern.LOWER_LATIN, CharPattern.UPPER_LATIN }),

    DIGITS(CharPattern.DIGIT),

    COMPLETE_LATIN(
            new CharPattern[] { CharPattern.DIGIT, CharPattern.LOWER_LATIN, CharPattern.LOWER_LATIN_RARE, CharPattern.UPPER_LATIN,
                    CharPattern.UPPER_LATIN_RARE }),

    HIRAGANA(CharPattern.HIRAGANA),

    KATAKANA(new CharPattern[] { CharPattern.HALFWIDTH_KATAKANA, CharPattern.FULLWIDTH_KATAKANA }),

    KANJI(CharPattern.KANJI),

    HANGUL(CharPattern.HANGUL),

    BEST_GUESS();

    private Map<Integer, Integer> charactersMap;

    private Map<Integer, Integer> ranksMap;

    private Map<CharPattern, Map<Integer, Integer>> charPatternCharacterMap;

    private Map<CharPattern, Map<Integer, Integer>> charPatternRankMap;

    private int radix;

    Alphabet(CharPattern[] charPatterns) {
        charactersMap = new HashMap<>();
        ranksMap = new HashMap<>();
        int rank = 0;
        for (CharPattern pattern : charPatterns) {
            addPatternCodePoints(pattern, rank);
            rank += pattern.getCodePointSize();
        }
        radix = charactersMap.size();
    }

    Alphabet(CharPattern pattern) {
        charactersMap = new HashMap<>();
        ranksMap = new HashMap<>();
        addPatternCodePoints(pattern, 0);
        radix = charactersMap.size();
    }

    Alphabet() {
        charPatternCharacterMap = new HashMap<>();
        charPatternRankMap = new HashMap<>();
        for (CharPattern charPattern : CharPattern.values()) {
            Map<Integer, Integer> charactersMap = new HashMap<>();
            Map<Integer, Integer> ranksMap = new HashMap<>();
            for (int pos = 0; pos < charPattern.getCodePointSize(); pos++) {
                Integer codePoint = charPattern.getCodePointAt(pos);
                charactersMap.put(pos, codePoint);
                ranksMap.put(codePoint, pos);
            }
            charPatternCharacterMap.put(charPattern, charactersMap);
            charPatternRankMap.put(charPattern, ranksMap);
        }
    }

    public int getRadix() {
        return this.radix;
    }

    public void setRadix(int radix) {
        this.radix = radix;
    }

    public Map<Integer, Integer> getCharactersMap() {
        return charactersMap;
    }

    public Map<Integer, Integer> getRanksMap() {
        return ranksMap;
    }

    public Map<CharPattern, Map<Integer, Integer>> getCharPatternCharacterMap() {
        return charPatternCharacterMap;
    }

    public Map<CharPattern, Map<Integer, Integer>> getCharPatternRankMap() {
        return charPatternRankMap;
    }

    private void addPatternCodePoints(CharPattern pattern, int offsetRank) {
        int rank = offsetRank;
        for (int pos = 0; pos < pattern.getCodePointSize(); pos++) {
            Integer codePoint = pattern.getCodePointAt(pos);
            charactersMap.put(rank, codePoint);
            ranksMap.put(codePoint, rank++);
        }
    }
}
