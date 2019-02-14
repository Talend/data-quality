package org.talend.dataquality.converters.character;

import static org.talend.dataquality.converters.character.KanaConstants.HALFWIDTH_ASPIRATED_MARK;
import static org.talend.dataquality.converters.character.KanaConstants.HALFWIDTH_VOICED_MARK;
import static org.talend.dataquality.converters.character.KanaConstants.MAPPING_FULL_TO_HALF_KATAKANA;
import static org.talend.dataquality.converters.character.KanaConstants.MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES;
import static org.talend.dataquality.converters.character.KanaConstants.MAPPING_HALF_TO_FULL_KATAKANA;

import java.text.Normalizer;

import org.apache.commons.lang.StringUtils;

/**
 * API for character width conversion
 */
public class CharWidthConverter {

    private static final int HALF_FULL_ASCII_DIFF = 65248;

    /**
     * see https://en.wikipedia.org/wiki/Non-breaking_space
     */
    private static final char NBSP = '\u00a0'; // Standard non-breaking space

    private static final char NBSP_VAR_1 = '\u202f'; // U+202F NARROW NO-BREAK SPACE

    private static final char NBSP_VAR_2 = '\u2007'; // U+2007 FIGURE SPACE

    private static final char FULLWIDTH_SPACE = '\u3000';

    private ConversionConfig config;

    public CharWidthConverter(ConversionConfig config) {
        this.config = config;
    }

    /**
     * convert the character width of a string.
     * 
     * @param input
     * @return
     */
    public String convert(String input) {
        if (StringUtils.isEmpty(input)) {
            return StringUtils.EMPTY;
        }
        String result = null;
        switch (config.getMode()) {
        case NFKC:
            result = Normalizer.normalize(input, Normalizer.Form.NFKC);
            break;
        case HALF_TO_FULL:
            result = halfwidthToFullwidth(input);
            break;
        case FULL_TO_HALF:
            result = fullwidthToHalfwidth(input);
            break;
        default:
            break;
        }
        return result;
    }

    private String halfwidthToFullwidth(String input) {
        final StringBuilder sb = new StringBuilder(input);
        for (int i = 0; i < sb.length(); i++) {
            final char ch = sb.charAt(i);
            if (ch >= '!' && ch <= '~') {
                if ((ch >= '0' && ch <= '9')) {
                    if (config.isConvertDigit()) {
                        sb.setCharAt(i, (char) (ch + HALF_FULL_ASCII_DIFF));
                    }
                } else if ((ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')) {
                    if (config.isConvertLetter()) {
                        sb.setCharAt(i, (char) (ch + HALF_FULL_ASCII_DIFF));
                    }
                } else if (config.isConvertOtherChars()) {
                    sb.setCharAt(i, (char) (ch + HALF_FULL_ASCII_DIFF));
                }
            } else if (ch == ' ' || ch == NBSP || ch == NBSP_VAR_1 || ch == NBSP_VAR_2) {
                if (config.isConvertOtherChars()) {
                    sb.setCharAt(i, FULLWIDTH_SPACE);
                }
            } else if (config.isConvertKatakana() && KanaConstants.isHalfwidthKatakana(ch)) {
                final Character fullwidthChar = MAPPING_HALF_TO_FULL_KATAKANA.get(ch);
                if (fullwidthChar != null) {
                    sb.setCharAt(i, fullwidthChar);
                } else if (i > 0 && (ch == HALFWIDTH_VOICED_MARK || ch == HALFWIDTH_ASPIRATED_MARK)
                        && KanaConstants.isHalfwidthKatakana(sb.charAt(i - 1))) {
                    sb.deleteCharAt(i);
                    i--;
                    if (ch == HALFWIDTH_VOICED_MARK) {
                        sb.setCharAt(i, (char) (sb.charAt(i) + 1));// code point distance 1
                    } else if (ch == HALFWIDTH_ASPIRATED_MARK) {
                        sb.setCharAt(i, (char) (sb.charAt(i) + 2));// code point distance 2
                    }
                }
            }
        }
        return sb.toString();
    }

    private String fullwidthToHalfwidth(String input) {
        final StringBuilder sb = new StringBuilder(input);
        for (int i = 0; i < sb.length(); i++) {
            char ch = sb.charAt(i);
            if ((ch >= '！' && ch <= '～')) {
                if ((ch >= '０' && ch <= '９')) {
                    if (config.isConvertDigit()) {
                        sb.setCharAt(i, (char) (ch - HALF_FULL_ASCII_DIFF));
                    }
                } else if (ch >= 'ａ' && ch <= 'ｚ' || ch >= 'Ａ' && ch <= 'Ｚ') {
                    if (config.isConvertLetter()) {
                        sb.setCharAt(i, (char) (ch - HALF_FULL_ASCII_DIFF));
                    }
                } else if (config.isConvertOtherChars()) {
                    sb.setCharAt(i, (char) (ch - HALF_FULL_ASCII_DIFF));
                }
            } else if (ch == FULLWIDTH_SPACE) {
                if (config.isConvertOtherChars()) {
                    sb.setCharAt(i, ' ');
                }
            } else if (config.isConvertKatakana() && KanaConstants.isFullwidthKatakana(ch)) {
                Character halfwidthChar = MAPPING_FULL_TO_HALF_KATAKANA.get(ch);
                if (halfwidthChar != null) {
                    sb.setCharAt(i, halfwidthChar);
                    Character diacriticSuffix = MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES.get(ch);
                    if (diacriticSuffix != null) {
                        sb.insert(i + 1, diacriticSuffix);
                    }
                }
            }
        }
        return sb.toString();
    }
}
