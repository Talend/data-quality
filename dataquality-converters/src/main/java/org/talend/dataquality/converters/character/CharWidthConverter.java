package org.talend.dataquality.converters.character;

import static org.talend.dataquality.converters.character.KanaConstants.HALFWIDTH_ASPIRATED_MARK;
import static org.talend.dataquality.converters.character.KanaConstants.HALFWIDTH_VOICED_MARK;
import static org.talend.dataquality.converters.character.KanaConstants.MAPPING_FULL_TO_HALF_KATAKANA;
import static org.talend.dataquality.converters.character.KanaConstants.MAPPING_HALFWIDTH_DIACRITIC_SUFFIXES;
import static org.talend.dataquality.converters.character.KanaConstants.MAPPING_HALF_TO_FULL_KATAKANA;

import java.text.Normalizer;

public class CharWidthConverter {

    private static final int HALF_FULL_ASCII_DIFF = 65248;

    private ConversionConfig config;

    public CharWidthConverter(ConversionConfig config) {
        this.config = config;
    }

    public String convert(String input) {
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
            if (config.isConvertDigit() && (ch >= '0' && ch <= '9')) {
                sb.setCharAt(i, (char) (ch + HALF_FULL_ASCII_DIFF));
            } else if (config.isConvertLetter() && (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')) {
                sb.setCharAt(i, (char) (ch + HALF_FULL_ASCII_DIFF));
            } else if (config.isConvertOtherChars() && ch == ' ') {
                sb.setCharAt(i, '　');
            } else if (config.isConvertOtherChars() && (ch >= '!' && ch <= '~')) {
                sb.setCharAt(i, (char) (ch + HALF_FULL_ASCII_DIFF));
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
            if (config.isConvertDigit() && (ch >= '０' && ch <= '９')) {
                sb.setCharAt(i, (char) (ch - HALF_FULL_ASCII_DIFF));
            } else if (config.isConvertLetter() && (ch >= 'ａ' && ch <= 'ｚ' || ch >= 'Ａ' && ch <= 'Ｚ')) {
                sb.setCharAt(i, (char) (ch - HALF_FULL_ASCII_DIFF));
            } else if (config.isConvertOtherChars() && ch == '　') {
                sb.setCharAt(i, ' ');
            } else if (config.isConvertOtherChars() && (ch >= '！' && ch <= '～')) {
                sb.setCharAt(i, (char) (ch - HALF_FULL_ASCII_DIFF));
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
