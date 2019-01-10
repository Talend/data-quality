package org.talend.dataquality.datamasking.generic;

import org.talend.daikon.pattern.character.CharPattern;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Alphabet {

    DEFAULT_LATIN(
            Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
                    "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g",
                    "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")),

    LATIN_LETTERS(
            Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
                    "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
                    "r", "s", "t", "u", "v", "w", "x", "y", "z")),

    DIGITS(CharPattern.DIGIT),

    COMPLETE_LATIN(new CharPattern[] { CharPattern.DIGIT, CharPattern.LOWER_LATIN, CharPattern.UPPER_LATIN }),

    HIRAGANA(CharPattern.HIRAGANA),

    KATAKANA(new CharPattern[] { CharPattern.HALFWIDTH_KATAKANA, CharPattern.FULLWIDTH_KATAKANA }),

    KANJI(CharPattern.KANJI),

    HANGUL(CharPattern.HANGUL);

    private Map<Integer, Integer> charactersMap;

    private Map<Integer, Integer> ranksMap;

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

    Alphabet(List<String> characters) {
        charactersMap = new HashMap<>();
        ranksMap = new HashMap<>();
        int rank = 0;
        for (String ch : characters) {
            int codePoint = Character.codePointAt(ch, 0);
            charactersMap.put(rank, codePoint);
            ranksMap.put(codePoint, rank++);
        }
        radix = charactersMap.size();
    }

    public int getRadix() {
        return this.radix;
    }

    public Map<Integer, Integer> getCharactersMap() {
        return charactersMap;
    }

    public Map<Integer, Integer> getRanksMap() {
        return ranksMap;
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
