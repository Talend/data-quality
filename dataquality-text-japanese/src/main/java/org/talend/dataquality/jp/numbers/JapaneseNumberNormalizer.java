// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.jp.numbers;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class JapaneseNumberNormalizer {

    private static char NO_NUMERAL = Character.MAX_VALUE;

    private static char[] numerals;

    private static char[] exponents;

    static {
        numerals = new char[0x10000];
        for (int i = 0; i < numerals.length; i++) {
            numerals[i] = NO_NUMERAL;
        }
        numerals['〇'] = 0; // 〇 U+3007 0
        numerals['零'] = 0; // 零 U+96F6 0
        numerals['一'] = 1; // 一 U+4E00 1
        numerals['二'] = 2; // 二 U+4E8C 2
        numerals['三'] = 3; // 三 U+4E09 3
        numerals['四'] = 4; // 四 U+56DB 4
        numerals['五'] = 5; // 五 U+4E94 5
        numerals['六'] = 6; // 六 U+516D 6
        numerals['七'] = 7; // 七 U+4E03 7
        numerals['八'] = 8; // 八 U+516B 8
        numerals['九'] = 9; // 九 U+4E5D 9

        exponents = new char[0x10000];
        for (int i = 0; i < exponents.length; i++) {
            exponents[i] = 0;
        }
        exponents['十'] = 1; // 十 U+5341 10
        exponents['百'] = 2; // 百 U+767E 100
        exponents['千'] = 3; // 千 U+5343 1,000
        exponents['万'] = 4; // 万 U+4E07 10,000
        exponents['億'] = 8; // 億 U+5104 100,000,000
        exponents['兆'] = 12; // 兆 U+5146 1,000,000,000,000
        exponents['京'] = 16; // 京 U+4EAC 10,000,000,000,000,000
        exponents['垓'] = 20; // 垓 U+5793 100,000,000,000,000,000,000
    }

    /**
     * Normalizes a Japanese number
     *
     * @param numberNotTrimmed number to normalize
     * @return normalized number, or number to normalize on error (no op)
     */
    public String normalizeNumber(String numberNotTrimmed) {
        // Small adaptation of the lib, because whitespaces were not handled
        String number = numberNotTrimmed.replaceAll("\\s+", "");
        try {
            NumberBuffer numberBuffer = new NumberBuffer(number);
            BigDecimal normalizedNumber = parseNumber(numberBuffer);
            if (normalizedNumber == null) {
                return numberNotTrimmed;
            }

            String formattedNumber = normalizedNumber.stripTrailingZeros().toPlainString();
            if (numberNotTrimmed.contains("、")) {
                NumberFormat formatter = NumberFormat.getInstance(new Locale("en_US"));
                formattedNumber = formatter.format(normalizedNumber.longValue());
            }

            return numberBuffer.prefix + formattedNumber + numberBuffer.suffix;
        } catch (NumberFormatException | ArithmeticException e) {
            // Return the source number in case of error, i.e. malformed input
            return number;
        }
    }

    /**
     * Parses a Japanese number
     *
     * @param buffer buffer to parse
     * @return parsed number, or null on error or end of input
     */
    private BigDecimal parseNumber(NumberBuffer buffer) {
        BigDecimal sum = BigDecimal.ZERO;
        boolean isNegative = false;
        if (buffer.string.length() > 0 && isNegativeSign(buffer.charAt(0))) {
            isNegative = true;
            buffer.advance();
        }

        // Remove prefix and suffix that are not numeric values
        computePrefix(buffer);
        computeSuffix(buffer);
        // check that remaining string is numeric
        if (!isNumeric(buffer)) {
            return null;
        }

        BigDecimal result = parseLargePair(buffer);

        if (result == null) {
            return null;
        }

        while (result != null) {
            sum = sum.add(result);
            result = parseLargePair(buffer);
        }

        if (isNegative) {
            sum = sum.negate();
        }
        return sum;
    }

    private boolean isNumeric(NumberBuffer buffer) {
        while (buffer.position < buffer.string.length()) {
            if (!isNumeric(buffer.charAt(buffer.position))) {
                return false;
            }
            buffer.advance();
        }
        buffer.position = 0;
        return true;
    }

    private void computePrefix(NumberBuffer buffer) {
        int start = buffer.position;
        while (buffer.position < buffer.string.length() && !isNumeric(buffer.charAt(buffer.position))) {
            buffer.advance();
        }

        buffer.setPrefix(start, buffer.position);
    }

    private void computeSuffix(NumberBuffer buffer) {
        buffer.position = buffer.string.length();
        while (buffer.position > 0 && !isNumeric(buffer.charAt(buffer.position - 1))) {
            buffer.back();
        }
        buffer.setSuffix(buffer.position);
    }

    /**
     * Parses a pair of large numbers, i.e. large kanji factor is 10,000（万）or larger
     *
     * @param buffer buffer to parse
     * @return parsed pair, or null on error or end of input
     */
    private BigDecimal parseLargePair(NumberBuffer buffer) {
        BigDecimal first = parseMediumNumber(buffer);
        BigDecimal second = parseLargeKanjiNumeral(buffer);

        if (first == null && second == null) {
            return null;
        }

        if (second == null) {
            // If there's no second factor, we return the first one
            // This can happen if we our number is smaller than 10,000 (万)
            return first;
        }

        if (first == null) {
            // If there's no first factor, just return the second one,
            // which is the same as multiplying by 1, i.e. with 万
            return second;
        }

        return first.multiply(second);
    }

    /**
     * Parses a "medium sized" number, typically less than 10,000（万）, but might be larger
     * due to a larger factor from {link parseBasicNumber}.
     *
     * @param buffer buffer to parse
     * @return parsed number, or null on error or end of input
     */
    private BigDecimal parseMediumNumber(NumberBuffer buffer) {
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal result = parseMediumPair(buffer);

        if (result == null) {
            return null;
        }

        while (result != null) {
            sum = sum.add(result);
            result = parseMediumPair(buffer);
        }

        return sum;
    }

    /**
     * Parses a pair of "medium sized" numbers, i.e. large kanji factor is at most 1,000（千）
     *
     * @param buffer buffer to parse
     * @return parsed pair, or null on error or end of input
     */
    private BigDecimal parseMediumPair(NumberBuffer buffer) {

        BigDecimal first = parseBasicNumber(buffer);
        BigDecimal second = parseMediumKanjiNumeral(buffer);

        if (first == null && second == null) {
            return null;
        }

        if (second == null) {
            // If there's no second factor, we return the first one
            // This can happen if we just have a plain number such as 五
            return first;
        }

        if (first == null) {
            // If there's no first factor, just return the second one,
            // which is the same as multiplying by 1, i.e. with 千
            return second;
        }

        // Return factors multiplied
        return first.multiply(second);
    }

    /**
     * Parse a basic number, which is a sequence of Arabic numbers or a sequence or 0-9 kanji numerals (〇 to 九).
     *
     * @param buffer buffer to parse
     * @return parsed number, or null on error or end of input
     */
    private BigDecimal parseBasicNumber(NumberBuffer buffer) {
        StringBuilder builder = new StringBuilder();
        int i = buffer.position();

        while (i < buffer.length()) {
            char c = buffer.charAt(i);

            if (isArabicNumeral(c)) {
                // Arabic numerals; 0 to 9 or ０ to ９ (full-width)
                builder.append(arabicNumeralValue(c));
            } else if (isKanjiNumeral(c)) {
                // Kanji numerals; 〇, 一, 二, 三, 四, 五, 六, 七, 八, or 九
                builder.append(kanjiNumeralValue(c));
            } else if (isDecimalPoint(c)) {
                builder.append(".");
            } else if (isThousandSeparator(c)) {
                // Just skip and move to the next character
            } else {
                // We don't have an Arabic nor kanji numeral, nor separation or punctuation, so we'll stop.
                break;
            }

            i++;
            buffer.advance();
        }

        if (builder.length() == 0) {
            // We didn't build anything, so we don't have a number
            return null;
        }

        return new BigDecimal(builder.toString());
    }

    /**
     * Parse large kanji numerals (ten thousands or larger)
     *
     * @param buffer buffer to parse
     * @return parsed number, or null on error or end of input
     */
    public BigDecimal parseLargeKanjiNumeral(NumberBuffer buffer) {
        int i = buffer.position();

        if (i >= buffer.length()) {
            return null;
        }

        char c = buffer.charAt(i);
        int power = exponents[c];

        if (power > 3) {
            buffer.advance();
            return BigDecimal.TEN.pow(power);
        }

        return null;
    }

    /**
     * Parse medium kanji numerals (tens, hundreds or thousands)
     *
     * @param buffer buffer to parse
     * @return parsed number or null on error
     */
    public BigDecimal parseMediumKanjiNumeral(NumberBuffer buffer) {
        int i = buffer.position();

        if (i >= buffer.length()) {
            return null;
        }

        char c = buffer.charAt(i);
        int power = exponents[c];

        if (1 <= power && power <= 3) {
            buffer.advance();
            return BigDecimal.TEN.pow(power);
        }

        return null;
    }

    /**
     * Arabic numeral predicate. Both half-width and full-width characters are supported
     *
     * @param c character to test
     * @return true if and only if c is an Arabic numeral
     */
    public boolean isArabicNumeral(char c) {
        return isHalfWidthArabicNumeral(c) || isFullWidthArabicNumeral(c);
    }

    /**
     * Arabic half-width numeral predicate
     *
     * @param c character to test
     * @return true if and only if c is a half-width Arabic numeral
     */
    private boolean isHalfWidthArabicNumeral(char c) {
        // 0 U+0030 - 9 U+0039
        return '0' <= c && c <= '9';
    }

    /**
     * Arabic full-width numeral predicate
     *
     * @param c character to test
     * @return true if and only if c is a full-width Arabic numeral
     */
    private boolean isFullWidthArabicNumeral(char c) {
        // ０ U+FF10 - ９ U+FF19
        return '０' <= c && c <= '９';
    }

    /**
     * Returns the numeric value for the specified character Arabic numeral.
     * Behavior is undefined if a non-Arabic numeral is provided
     *
     * @param c arabic numeral character
     * @return numeral value
     */
    private int arabicNumeralValue(char c) {
        int offset;
        if (isHalfWidthArabicNumeral(c)) {
            offset = '0';
        } else {
            offset = '０';
        }
        return c - offset;
    }

    /**
     * Kanji numeral predicate that tests if the provided character is one of 〇, 一, 二, 三, 四, 五, 六, 七, 八, or 九.
     * Larger number kanji gives a false value.
     *
     * @param c character to test
     * @return true if and only is character is one of 〇, 一, 二, 三, 四, 五, 六, 七, 八, or 九 (0 to 9)
     */
    private boolean isKanjiNumeral(char c) {
        return numerals[c] != NO_NUMERAL;
    }

    /**
     * Returns the value for the provided kanji numeral. Only numeric values for the characters where
     * {link isKanjiNumeral} return true are supported - behavior is undefined for other characters.
     *
     * @param c kanji numeral character
     * @return numeral value
     * @see #isKanjiNumeral(char)
     */
    private int kanjiNumeralValue(char c) {
        return numerals[c];
    }

    /**
     * Decimal point predicate
     *
     * @param c character to test
     * @return true if and only if c is a decimal point
     */
    private boolean isDecimalPoint(char c) {
        return handleNormalDotCase(c) || c == '点' || c == '點'; // U+FF0E FULLWIDTH FULL STOP
    }

    private boolean handleNormalDotCase(char c) {
        return c == '.' // U+002E FULL STOP
                || c == '．' || c == '・';
    }

    /**
     * Thousand separator predicate
     *
     * @param c character to test
     * @return true if and only if c is a thousand separator predicate
     */
    private boolean isThousandSeparator(char c) {
        return c == ',' // U+002C COMMA
                || c == '，' // U+FF0C FULLWIDTH COMMA
                || c == '、';
    }

    private boolean isKanjiExponent(char c) {
        return exponents[c] != 0;
    }

    private boolean isNegativeSign(char c) {
        return c == '-' || c == '負';
    }

    private boolean isNumeric(char c) {
        return isArabicNumeral(c) || isDecimalPoint(c) || isKanjiNumeral(c) || isKanjiExponent(c) || isNegativeSign(c)
                || isThousandSeparator(c);
    }

    /**
     * Buffer that holds a Japanese number string and a position index used as a parsed-to marker
     */
    public static class NumberBuffer {

        private int position;

        private String string;

        private String prefix;

        private String suffix;

        public NumberBuffer(String string) {
            this.string = string;
            this.position = 0;
            this.prefix = "";
            this.suffix = "";
        }

        public char charAt(int index) {
            return string.charAt(index);
        }

        public int length() {
            return string.length();
        }

        public void advance() {
            position++;
        }

        public void back() {
            position--;
        }

        public int position() {
            return position;
        }

        public void setPrefix(int start, int endPosition) {
            prefix = string.substring(start, endPosition);
            string = string.substring(endPosition);
            position = 0;
        }

        public void setSuffix(int startPosition) {
            suffix = string.substring(startPosition);
            string = string.substring(0, startPosition);
            position = 0;
        }
    }
}
