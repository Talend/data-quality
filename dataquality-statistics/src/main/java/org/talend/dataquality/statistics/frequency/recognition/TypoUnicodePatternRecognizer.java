package org.talend.dataquality.statistics.frequency.recognition;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 *
 * <b>This class enable the detection of patterns in texts that are encode in Unicode.</b>
 * <p>
 * Here are the main patterns recongized and when they are used :
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
 * presented in {@link TypoUnicodePatternRecognizer}.
 * <br>
 * <li>[Word] : A character string beginning by a capital letter followed only by small letters.</li>
 * <li>[wORD] : A character string beginning by a small letter followed only by capital letters.</li>
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
 * <li>"someWordsINwORDS" will have the following pattern : [word][Word][WORD][wORD]</li>
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
public abstract class TypoUnicodePatternRecognizer extends AbstractPatternRecognizer {

    /**
     * This methods returns a new instance of {@link WithCase}.
     *
     * @return
     */
    public static TypoUnicodePatternRecognizer withCase() {
        return new WithCase();
    }

    /**
     * This methods returns a new instance of {@link NoCase}.
     *
     * @return
     */
    public static TypoUnicodePatternRecognizer noCase() {
        return new NoCase();
    }

    private static boolean isAlphabeticButNotIdeographic(char[] ca, int pos) {
        return Character.isAlphabetic(Character.codePointAt(ca, pos)) && !Character.isIdeographic(Character.codePointAt(ca, pos));
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
        boolean isComplete = true;
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
                    runningPos += seqLength;
                }
            }
            if (loopStart == runningPos) {
                patternSeq.append(ca[loopStart]);
                isComplete = false;
                runningPos++;
            }
        }
        result.setResult(Collections.singleton(patternSeq.toString()), isComplete);
        return result;
    }

    protected abstract int exploreNextPattern(PatternExplorer pe, char[] ca, int startingPos);

    @Override
    public Set<String> getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternStringSet();
    }

    private enum PatternExplorer {

        ALPHABETIC("[char]", "[word]", "[alnum]"),
        IDEOGRAPHIC("[Ideogram]", "[IdeogramSeq]", "[alnum(CJK)]"),
        NUMERIC("[digit]", "[number]", null),
        UPPER_CASE("[Char]", "[WORD]", "[Word]"),
        NOT_UPPER_CASE("[char]", "[word]", "[wORD]");

        /**
         * Pattern for a single character type
         */
        private String patternUnit = "";

        /**
         * Pattern for a sequence of a character type
         */
        private String patternSequence = "";

        /**
         * Special pattern for a sequence of combined type of characters, depend on the last pattern
         */
        private String specialPattern;

        private boolean isSpecial;

        PatternExplorer(String patternUnit, String patternSequence, String specialPattern) {
            this.patternUnit = patternUnit;
            this.patternSequence = patternSequence;
            this.specialPattern = specialPattern;
        }

        private int exploreWithCase(char[] ca, int start) {
            isSpecial = false;
            int pos = start;
            switch (this) {
            case IDEOGRAPHIC:
                while (pos < ca.length && Character.isIdeographic(Character.codePointAt(ca, pos))) {
                    pos++;
                }
                break;
            case NUMERIC:
                while (pos < ca.length && Character.isDigit(Character.codePointAt(ca, pos))) {
                    pos++;
                }
                break;
            case UPPER_CASE:
                while (pos < ca.length && Character.isUpperCase(Character.codePointAt(ca, pos))) {
                    pos++;
                }
                if (pos == (start + 1)) {
                    pos += exploreSpecial(ca, pos);
                }
                break;
            case NOT_UPPER_CASE:
                while (pos < ca.length && isAlphabeticButNotIdeographic(ca, pos)
                        && !Character.isUpperCase(Character.codePointAt(ca, pos))) {
                    pos++;
                }
                if (pos == (start + 1)) {
                    pos += exploreSpecial(ca, pos);
                }
                break;
            default:
                break;
            }
            return pos - start;
        }

        private int exploreNoCase(char[] ca, int start) {
            isSpecial = false;
            int pos = start;
            switch (this) {
            case ALPHABETIC:
                while (pos < ca.length && isAlphabeticButNotIdeographic(ca, pos)) {
                    pos++;
                }
                break;
            case IDEOGRAPHIC:
                while (pos < ca.length && Character.isIdeographic(Character.codePointAt(ca, pos))) {
                    pos++;
                }
                break;
            case NUMERIC:
                while (pos < ca.length && Character.isDigit(Character.codePointAt(ca, pos))) {
                    pos++;
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
                while (pos < ca.length
                        && (isAlphabeticButNotIdeographic(ca, pos) || Character.isDigit(Character.codePointAt(ca, pos)))) {
                    pos++;
                }
                break;
            case IDEOGRAPHIC:
                while (pos < ca.length && (Character.isIdeographic(Character.codePointAt(ca, pos))
                        || Character.isDigit(Character.codePointAt(ca, pos)))) {
                    pos++;
                }
                break;
            case NUMERIC:
                pos += ALPHABETIC.exploreSpecial(ca, start);
                if (pos > start) {
                    specialPattern = "[alnum]";
                } else {
                    pos += IDEOGRAPHIC.exploreSpecial(ca, start);
                    if (pos > start) {
                        specialPattern = "[alnum(CJK)]";
                    }
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
            if (seqLength == 1) {
                return patternUnit;
            } else {
                return patternSequence;
            }
        }
    }

    static class WithCase extends TypoUnicodePatternRecognizer {

        @Override
        public int exploreNextPattern(PatternExplorer pe, char[] ca, int startingPos) {
            return pe.exploreWithCase(ca, startingPos);
        }
    }

    static class NoCase extends TypoUnicodePatternRecognizer {

        @Override
        public int exploreNextPattern(PatternExplorer pe, char[] ca, int startingPos) {
            return pe.exploreNoCase(ca, startingPos);
        }
    }
}