package org.talend.dataquality.statistics.frequency.recognition;

import static java.lang.Character.getType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.talend.daikon.pattern.character.CharPattern;
import org.talend.daikon.pattern.word.WordPattern;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 *
 * <b>This class enables the detection of patterns in texts that are encoded in Unicode.</b>
 * <p>
 * Here are the main patterns recognized and when they are used :
 * <li>[char] : A char is defined as a single alphabetic character (except ideograms) between non alphabetic characters.</li>
 * <li>[word] : A word is defined as a string of alphabetic characters.</li>
 * <li>[Ideogram] : One of the 80 thousands CJK Unified CJK Ideographs, as defined in the Unicode. For more information about
 * ideograms and how Java handles it, see {@link Character#isIdeographic(int)}</li>
 * <li>[IdeogramSeq] : A sequence of ideograms.</li>
 * <li>[digit] : A digit is one of the Hindu-Arabic numerals : 0,1,2,3,4,5,6,7,8,9.</li>
 * <li>[number] : A number is defined as a combination of digits.</li>
 * <li>Every other character will be left as it is in the pattern definition.</li>
 *
 * <br>
 * Two different configurations can be chosen : withCase and noCase.
 * <br>
 * As their name indicates, they are used to specify whether the character's case is important. According to the cases, specific
 * patterns will be used.
 * <br>
 * A more detailed presentation of these configurations is described in the following.
 * </p>
 * <br>
 * <b>When cases are important :</b>
 * <p>
 * When cases are taken into account in the detection of patterns, some variations have been introduced to the patterns
 * presented in {@link WordPatternRecognizer}.
 * <br>
 * <li>[Word] : A character string beginning by a capital letter followed only by small letters.</li>
 * <li>[WORD] : A character string only composed with capital letters.</li>
 * <li>[word] : A character string only composed with small letters.</li>
 * <li>[Char] : A single capital letter comprised between non alphabetic characters and ideographs.</li>
 * <li>[char] : A small letter comprised between non alphabetic characters and ideographs.</li>
 *
 * <br>
 * Because of these patterns, in some cases a single character string can be replaced by several types of [word] patterns.
 * </p>
 * <p>
 * Some examples :
 * <br>
 * <li>"A character is NOT a Word" will have the following pattern : [Char] [word] [word] [WORD] [char] [Word]</li>
 * <li>"Example123@protonmail.com" will have the following pattern : [Word][number]@[word].[word]</li>
 * <li>"anotherExample8@yopmail.com" will have the following pattern : [word][Word][digit]@[word].[word]</li>
 * <li>"袁 花木蘭88" will have the following pattern : [Ideogram] [IdeogramSeq][number]</li>
 * <li>"Latin2中文" will have the following pattern : [Word][digit][IdeogramSeq]</li>
 * <li>"中文2Latin" will have the following pattern : [IdeogramSeq][digit][Word]</li>
 * </p>
 * <br>
 * <b>When cases are not important : </b>
 * <p>
 * When cases are not important, two new patterns can be recognized : <br>
 * <li>[alnum] : "alnum" stands for alphanumeric and corresponds to a combination of alphabetic characters and number.
 * <li>[alnum(CJK)] : CJK version of alnum, i.e. ideograms mixed with numbers.</li>
 * </p>
 * <p>
 * Some examples :
 * <br>
 * <li>"A character is NOT a Word" will have the following pattern : [char] [word] [word] [word] [char] [word]</li>
 * <li>"someWordsINwORDS" will have the following pattern : [word]</li>
 * <li>"Example123@protonmail.com" will have the following pattern : [alnum]@[word].[word]</li>
 * <li>"anotherExample8@yopmail.com" will have the following pattern : [alnum]@[word].[word]</li>
 * <li>"袁 花木蘭88" will have the following pattern : [Ideogram] [alnum(CJK)]</li>
 * <li>"Latin2中文" will have the following pattern : [alnum][IdeogramSeq]</li>
 * <li>"中文2Latin" will have the following pattern : [alnum(CJK)][word]</li>
 * </p>
 * Created by afournier on 06/04/17.
 */
public abstract class WordPatternRecognizer extends AbstractPatternRecognizer {

    private static final Set<Integer> ADDITIONAL_IDEOGRAMS = new HashSet<>();

    private static final Set<Integer> REMOVED_IDEOGRAMS = new HashSet<>();

    // Some codepoints are recognized as p{Han} but not as Character.isIdeographic, hence we add them here to be iso-functional
    // https://www.unicode.org/charts/PDF/U2E80.pdf
    // https://www.unicode.org/charts/PDF/U2F00.pdf
    // https://www.unicode.org/charts/PDF/U3000.pdf
    static {
        for (int i = 11904; i <= 11929; i++)
            ADDITIONAL_IDEOGRAMS.add(i);
        for (int i = 11931; i <= 12019; i++)
            ADDITIONAL_IDEOGRAMS.add(i);
        for (int i = 12032; i <= 12245; i++)
            ADDITIONAL_IDEOGRAMS.add(i);
        ADDITIONAL_IDEOGRAMS.add(12293);
        ADDITIONAL_IDEOGRAMS.add(12347);

        REMOVED_IDEOGRAMS.add(12294);
    }

    /**
     * This methods returns a new instance of {@link WithCase}.
     *
     * @return
     */
    public static WordPatternRecognizer withCase() {
        return new WithCase();
    }

    /**
     * This methods returns a new instance of {@link NoCase}.
     *
     * @return
     */
    public static WordPatternRecognizer noCase() {
        return new NoCase();
    }

    /**
     * This method explore a String and extract the patterns associated to it.
     *
     * @param stringToRecognize the string whose pattern is to be recognized. default to DataTypeEnum.STRING
     * @param type the type of the data to recognize
     * @return
     */
    @Override
    public RecognitionResult recognize(String stringToRecognize, DataTypeEnum type) {
        RecognitionResult result = new RecognitionResult();
        if (StringUtils.isEmpty(stringToRecognize)) {
            result.setResult(Collections.singleton(stringToRecognize), false);
            return result;
        }
        // convert the string to recognize into a char array, in order to use Character class.
        char[] ca = stringToRecognize.toCharArray();
        // Initialize the patternSeq String builder to a new StringBuilder as it is a new String to Recognize.
        StringBuilder patternSeq = new StringBuilder();
        // current position in the String
        int runningPos = 0;
        // Position at the beginning of the while loop. Used to identify special characters :
        // If this value is still the same after the for loop, this means the character is a special character.
        int loopStart;
        // length of the explored sequence
        int seqLength;
        while (runningPos < ca.length) {
            loopStart = runningPos;
            for (PatternExplorer pe : PatternExplorer.values()) {
                seqLength = exploreNextPattern(pe, ca, runningPos);
                if (seqLength > 0) {
                    patternSeq.append(pe.getPattern(seqLength));
                    runningPos += seqLength; // Iterate through all possible patterns until all the string has been explored
                }
            }
            if (loopStart == runningPos) {
                patternSeq.append(ca[loopStart]);
                runningPos++;
            }
        }
        result.setResult(Collections.singleton(patternSeq.toString()), true);
        return result;
    }

    protected abstract int exploreNextPattern(PatternExplorer pe, char[] ca, int startingPos);

    @Override
    public Set<String> getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternStringSet();
    }

    private enum PatternExplorer {

        ALPHABETIC(
                WordPattern.LOWER_CHAR.getPattern(),
                WordPattern.LOWER_WORD.getPattern(),
                WordPattern.ALPHANUMERIC.getPattern()),
        IDEOGRAPHIC(WordPattern.IDEOGRAM.getPattern(), WordPattern.IDEOGRAM_SEQUENCE.getPattern(), null),
        HIRAGANA(WordPattern.HIRAGANA.getPattern(), WordPattern.HIRAGANA_SEQUENCE.getPattern(), null),
        KATAKANA(WordPattern.KATAKANA.getPattern(), WordPattern.KATAKANA_SEQUENCE.getPattern(), null),
        HANGUL(WordPattern.HANGUL.getPattern(), WordPattern.HANGUL_SEQUENCE.getPattern(), null),
        NUMERIC(WordPattern.DIGIT.getPattern(), WordPattern.NUMBER.getPattern(), null),
        UPPER_CASE(WordPattern.UPPER_CHAR.getPattern(), WordPattern.UPPER_WORD.getPattern(), WordPattern.WORD.getPattern()),
        NOT_UPPER_CASE(WordPattern.LOWER_CHAR.getPattern(), WordPattern.LOWER_WORD.getPattern(), null);

        /**
         * Pattern for a single character type
         */
        private String patternUnit;

        /**
         * Pattern for a sequence of a character type
         */
        private String patternSequence;

        /**
         * Special pattern for a sequence of combined type of characters, depend on the last pattern
         */
        private String specialPattern;

        private boolean isSpecial;

        /**
         * A sequence of combined type of characters if include surrogate pair characters
         */
        private boolean isIncludeSurrPair;

        PatternExplorer(String patternUnit, String patternSequence, String specialPattern) {
            this.patternUnit = patternUnit;
            this.patternSequence = patternSequence;
            this.specialPattern = specialPattern;
        }

        private int exploreWithCase(char[] ca, int start) {
            isSpecial = false;
            isIncludeSurrPair = false;
            int pos = start;
            switch (this) {
            case IDEOGRAPHIC:
                while (pos < ca.length && isIdeographic(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            case NUMERIC:
                while (pos < ca.length && isDigit(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            case UPPER_CASE:
                while (pos < ca.length && isUpper(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                if (pos == (start + 1)) {
                    pos += exploreSpecial(ca, pos);
                }
                break;
            case NOT_UPPER_CASE:
                while (pos < ca.length && isLower(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            case HANGUL:
                while (pos < ca.length && isHangul(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            case HIRAGANA:
                while (pos < ca.length && isHiragana(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            case KATAKANA:
                while (pos < ca.length && isKatakana(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            default:
                break;
            }
            return pos - start;
        }

        private int exploreNoCase(char[] ca, int start) {
            isSpecial = false;
            isIncludeSurrPair = false;
            int pos = start;
            switch (this) {
            case ALPHABETIC:
                while (pos < ca.length && isLetter(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            case IDEOGRAPHIC:
                while (pos < ca.length && isIdeographic(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            case NUMERIC:
                while (pos < ca.length && isDigit(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            case HANGUL:
                while (pos < ca.length && isHangul(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            case HIRAGANA:
                while (pos < ca.length && isHiragana(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            case KATAKANA:
                while (pos < ca.length && isKatakana(Character.codePointAt(ca, pos))) {
                    pos += getCodepointSize(ca[pos]);
                }
                break;
            default:
                break;
            }

            int seqLength = pos - start;
            if (seqLength > 0) {
                seqLength += exploreSpecial(ca, pos);
            }
            return seqLength;
        }

        private int exploreSpecial(char[] ca, int start) {
            int pos = start;
            switch (this) {
            case ALPHABETIC:
                while (pos < ca.length && (isLetter(Character.codePointAt(ca, pos)) || isDigit(Character.codePointAt(ca, pos)))) {
                    pos++;
                }
                break;
            case NUMERIC:
                pos += ALPHABETIC.exploreSpecial(ca, start);
                if (pos > start) {
                    specialPattern = WordPattern.ALPHANUMERIC.getPattern();
                }
                break;
            case UPPER_CASE:
                pos += NOT_UPPER_CASE.exploreWithCase(ca, start);
                break;
            case NOT_UPPER_CASE:
                pos += UPPER_CASE.exploreWithCase(ca, start);
                break;
            default:
                break;
            }

            int seqLength = pos - start;
            if (seqLength > 0) {
                isSpecial = true;
            }

            return seqLength;
        }

        private String getPattern(int seqLength) {
            if (seqLength == 0) {
                return null;
            }
            if (isSpecial) {
                return specialPattern;
            }
            if (seqLength == 1 || (seqLength == 2 && isIncludeSurrPair)) {
                return patternUnit;
            } else {
                return patternSequence;
            }
        }

        private int getCodepointSize(char currentChar) {
            if (!Character.isSurrogate(currentChar)) {
                return 1;
            }
            // it is a surrogate pair character
            isIncludeSurrPair = true;
            return 2;

        }
    }

    static boolean isLower(int codePoint) {
        return getType(codePoint) == Character.LOWERCASE_LETTER;
    }

    static boolean isUpper(int codePoint) {
        return getType(codePoint) == Character.UPPERCASE_LETTER;
    }

    static boolean isLetter(int codePoint) {
        return isLower(codePoint) || isUpper(codePoint);
    }

    static boolean isIdeographic(int codePoint) {
        return (Character.isIdeographic(codePoint) || ADDITIONAL_IDEOGRAMS.contains(codePoint))
                && !REMOVED_IDEOGRAMS.contains(codePoint);
    }

    static boolean isHangul(int codePoint) {
        return CharPattern.HANGUL.contains(codePoint);
    }

    static boolean isHiragana(int codePoint) {
        return CharPattern.HIRAGANA.contains(codePoint);
    }

    static boolean isKatakana(int codePoint) {
        return CharPattern.HALFWIDTH_KATAKANA.contains(codePoint) || CharPattern.FULLWIDTH_KATAKANA.contains(codePoint);
    }

    static boolean isDigit(int codePoint) {
        return Character.isDigit(codePoint);
    }

    static class WithCase extends WordPatternRecognizer {

        @Override
        public int exploreNextPattern(PatternExplorer pe, char[] ca, int startingPos) {
            return pe.exploreWithCase(ca, startingPos);
        }
    }

    static class NoCase extends WordPatternRecognizer {

        @Override
        public int exploreNextPattern(PatternExplorer pe, char[] ca, int startingPos) {
            return pe.exploreNoCase(ca, startingPos);
        }
    }
}
