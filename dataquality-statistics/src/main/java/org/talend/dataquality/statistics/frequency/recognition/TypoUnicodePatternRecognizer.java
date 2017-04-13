package org.talend.dataquality.statistics.frequency.recognition;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

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
 * <li>[wORD] : A character string beginning by a capital letter followed only by capital letters.</li>
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

    static class WithCase extends TypoUnicodePatternRecognizer {

        /**
         * This method recognize pattern in a String.
         * It uses different methods of the class {@link Character} of java.lang in order to recognize the characters types
         * according to Unicode.
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
            StringBuilder sb = new StringBuilder();
            // Pattern that will be used to recognize when to replace the last pattern (a char) when a word has been recognize
            // from it.
            Pattern p = Pattern.compile("((\\[Word])|(\\[wORD]))");
            // current position in the String
            int runningPos = 0;
            // Position at the beginning of the while loop. Used to identify special characters :
            // If this value is still the same after the for loop, this means the character is a special character.
            int loopStart;
            // Last recognized pattern, useful to transform an upper case Char to a Word and a vice versa, without having to go
            // through the StringBuilder.
            String lastPattern = "";
            String currentPattern;
            CasePatternExplorer[] patternExplorers = CasePatternExplorer.values();
            while (runningPos < ca.length) {
                loopStart = runningPos;
                for (CasePatternExplorer cpe : patternExplorers) {
                    currentPattern = cpe.explore(ca, runningPos, lastPattern);
                    if (currentPattern != null) {
                        runningPos += cpe.seqLength;
                        if (p.matcher(currentPattern).matches()) {
                            sb.replace(sb.length() - 6, sb.length(), currentPattern);
                        } else {
                            sb.append(currentPattern);
                        }
                        lastPattern = currentPattern;
                    }
                }
                if (loopStart == runningPos) {
                    lastPattern = "" + ca[loopStart];
                    sb.append(lastPattern);
                    isComplete = false;
                    runningPos++;
                }
            }
            result.setResult(Collections.singleton(sb.toString()), isComplete);
            return result;
        }

        @Override
        public Set<String> getValuePattern(String originalValue) {
            RecognitionResult result = recognize(originalValue);
            return result.getPatternStringSet();
        }

        private enum CasePatternExplorer {

            IDEOGRAPHIC(1, "[Ideogram]", "[IdeogramSeq]"),
            NUMERIC(2, "[digit]", "[number]"),
            UPPER_CASE(3, "[Char]", "[WORD]", "[wORD]", "\\[char\\]"),
            NOT_UPPER_CASE(4, "[char]", "[word]", "[Word]", "\\[Char\\]");

            /**
             * Character type, indicates what to match the character with
             */
            private int type = 0;

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
            private String SpecialPattern = null;

            /**
             * Pattern matcher to verify if the special pattern has to be used
             */
            private Pattern lastPattern2Match = null;

            private int seqLength = 0;

            CasePatternExplorer(int type, String patternUnit, String patternSequence) {
                this.type = type;
                this.patternUnit = patternUnit;
                this.patternSequence = patternSequence;
            }

            CasePatternExplorer(int type, String patternUnit, String patternSequence, String SpecialPattern,
                    String lastPattern2Match) {
                this(type, patternUnit, patternSequence);
                this.SpecialPattern = SpecialPattern;
                this.lastPattern2Match = Pattern.compile(lastPattern2Match);
            }

            private String explore(char[] ca, int start, String lastPattern) {
                int pos = start;
                switch (type) {
                case 1:
                    while (pos < ca.length && Character.isIdeographic(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    break;
                case 2:
                    while (pos < ca.length && Character.isDigit(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    break;
                case 3:
                    while (pos < ca.length && Character.isUpperCase(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    break;
                case 4:
                    while (pos < ca.length && Character.isAlphabetic(Character.codePointAt(ca, pos))
                            && !Character.isUpperCase(Character.codePointAt(ca, pos))
                            && !Character.isIdeographic(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    break;
                default:
                    break;
                }
                seqLength = pos - start;
                return getPattern(seqLength, lastPattern);
            }

            private String getPattern(int seqLength, String lastPattern) {
                if (seqLength == 0) {
                    return null;
                }
                if (lastPattern2Match != null && lastPattern2Match.matcher(lastPattern).matches()) {
                    return SpecialPattern;
                }
                if (seqLength == 1) {
                    return patternUnit;
                } else {
                    return patternSequence;
                }
            }
        }
    }

    static class NoCase extends TypoUnicodePatternRecognizer {

        /**
         * This method recognize pattern in a String.
         * It uses different methods of the class {@link Character#} of java.lang in order to recognize the characters types
         * according to Unicode.
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
            char[] ca = stringToRecognize.toCharArray();
            StringBuilder sb = new StringBuilder();
            int runningPos = 0;
            int loopStart;
            String currentPattern;
            while (runningPos < ca.length) {
                loopStart = runningPos;

                for (NoCasePatternExplorer ncpe : NoCasePatternExplorer.values()) {
                    currentPattern = ncpe.explore(ca, runningPos);
                    if (currentPattern != null) {
                        runningPos += ncpe.seqLength;
                        sb.append(currentPattern);
                    }
                }

                if (runningPos == loopStart) {
                    sb.append(ca[loopStart]);
                    isComplete = false;
                    runningPos++;
                }
            }
            result.setResult(Collections.singleton(sb.toString()), isComplete);
            return result;
        }

        @Override
        public Set<String> getValuePattern(String originalValue) {
            RecognitionResult result = recognize(originalValue);
            return result.getPatternStringSet();
        }

        private enum NoCasePatternExplorer {

            ALPHABETIC(1, "[char]", "[word]", "[alnum]"),
            IDEOGRAPHIC(2, "[Ideogram]", "[IdeogramSeq]", "[alnum(CJK)]"),
            NUMERIC(3, "[digit]", "[number]");

            /**
             * character type, indicates what to match the character with
             */
            private int type = 0;

            /**
             * Pattern for a single character type
             */
            private String patternUnit = "";

            /**
             * Pattern for a sequence of a character type
             */
            private String patternSequence = "";

            /**
             * Pattern for an alphanumeric sequence of the character type
             */
            private String patternAlnum = "";

            /**
             * Tells if the current sequence is an alnum
             */
            private boolean isAlnum = false;

            /**
             * Current pattern found at exploration
             */
            private int seqLength = 0;

            NoCasePatternExplorer(int type, String patternUnit, String patternSequence) {
                this.type = type;
                this.patternUnit = patternUnit;
                this.patternSequence = patternSequence;
            }

            NoCasePatternExplorer(int type, String patternMatch1, String patternMatch2, String patternAlnum) {
                this(type, patternMatch1, patternMatch2);
                this.patternAlnum = patternAlnum;
            }

            private String explore(char[] ca, int start) {
                isAlnum = false;
                int pos = start;
                int posAlnum;
                switch (type) {
                case 1:
                    while (pos < ca.length && Character.isAlphabetic(Character.codePointAt(ca, pos))
                            && !Character.isIdeographic(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    if (pos > start) {
                        posAlnum = exploreAlnum(ca, pos);
                        if (posAlnum > pos) {
                            isAlnum = true;
                            pos = posAlnum;
                        }
                    }
                    break;
                case 2:
                    while (pos < ca.length && Character.isIdeographic(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    if (pos > start) {
                        posAlnum = exploreCJKalnum(ca, pos);
                        if (posAlnum > pos) {
                            isAlnum = true;
                            pos = posAlnum;
                        }
                    }
                    break;
                case 3:
                    while (pos < ca.length && Character.isDigit(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    if (pos > start) {
                        posAlnum = exploreAlnum(ca, pos);
                        if (posAlnum > pos) {
                            isAlnum = true;
                            patternAlnum = "[alnum]";
                            pos = posAlnum;
                        } else { // If not, is it an alnum with CJK Ideograms ?
                            posAlnum = exploreCJKalnum(ca, pos);
                            if (posAlnum > pos) {
                                isAlnum = true;
                                patternAlnum = "[alnum(CJK)]";
                                pos = posAlnum;
                            }
                        }
                    }
                    break;
                default:
                    break;
                }
                seqLength = pos - start;
                return getPattern(seqLength);
            }

            private int exploreCJKalnum(char[] ca, int start) {
                int pos = start;
                while (pos < ca.length && (Character.isDigit(Character.codePointAt(ca, pos))
                        || Character.isIdeographic(Character.codePointAt(ca, pos)))) {
                    pos++;
                }
                return pos;
            }

            private int exploreAlnum(char[] ca, int start) {
                int pos = start;
                while (pos < ca.length && (Character.isDigit(Character.codePointAt(ca, pos))
                        || (Character.isAlphabetic(Character.codePointAt(ca, pos))
                                && !Character.isIdeographic(Character.codePointAt(ca, pos))))) {
                    pos++;
                }
                return pos;
            }

            private String getPattern(int seqLength) {
                if (seqLength == 0) {
                    return null;
                }
                if (isAlnum) {
                    return patternAlnum;
                }
                if (seqLength == 1) {
                    return patternUnit;
                } else {
                    return patternSequence;
                }
            }
        }
    }
}
