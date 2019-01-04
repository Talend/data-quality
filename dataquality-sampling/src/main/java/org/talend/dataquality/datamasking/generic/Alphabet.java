package org.talend.dataquality.datamasking.generic;

import com.mifmif.common.regex.Generex;
import org.talend.daikon.pattern.character.CharPattern;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Alphabet {

    DIGITS("[0-9]"),

    DEFAULT_LATIN("[a-zA-Z0-9]"),

    COMPLETE_LATIN(Arrays.asList(CharPattern.DIGIT, CharPattern.LOWER_LATIN, CharPattern.UPPER_LATIN)),

    HIRAGANA(Collections.singletonList(CharPattern.HIRAGANA)),

    KATAKANA(Arrays.asList(CharPattern.HALFWIDTH_KATAKANA, CharPattern.FULLWIDTH_KATAKANA)),

    KANJI(Collections.singletonList(CharPattern.KANJI)),

    HANGUL(Collections.singletonList(CharPattern.HANGUL));

    private Map<Integer, String> charactersMap;

    private Map<String, Integer> ranksMap;

    private int radix;

    Alphabet(List<CharPattern> charPatterns) {
        charactersMap = new HashMap<>();
        ranksMap = new HashMap<>();
        int rank = 0;
        for (CharPattern pattern : charPatterns) {
            for (int pos = 0; pos < pattern.getCodePointSize(); pos++) {
                Integer codePoint = pattern.getCodePointAt(pos);
                String character = String.valueOf(Character.toChars(codePoint));
                charactersMap.put(rank, character);
                ranksMap.put(character, rank++);
            }
        }
        radix = charactersMap.size();
    }

    Alphabet(String regex) {
        charactersMap = new HashMap<>();
        ranksMap = new HashMap<>();
        if (Generex.isValidPattern(regex)) {
            Generex generex = new Generex(regex);
            com.mifmif.common.regex.util.Iterator iterator = generex.iterator();
            int rank = 0;
            while (iterator.hasNext()) {
                String character = iterator.next();
                charactersMap.put(rank, character);
                ranksMap.put(character, rank++);
            }
        }
        radix = charactersMap.size();
    }

    public int getRadix() {
        return this.radix;
    }

    public Map<Integer, String> getCharactersMap() {
        return charactersMap;
    }

    public Map<String, Integer> getRanksMap() {
        return ranksMap;
    }
}
