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
            // current position in the String
            int pos = 0;
            // Position of the beginning of the current sequence of characters
            int sequenceBeginning;
            // Last pattern recognize, useful to transform an upper case Char to a Word and a vice versa, without having to go
            // through the StringBuilder.
            String lastPattern = null;
            int length = stringToRecognize.length();

            while (pos < length) {
                int start = pos;
                // Check Upper-Case sequence
                if (Character.isUpperCase(Character.codePointAt(ca, pos))) {
                    sequenceBeginning = pos;
                    pos++;
                    while (pos < length && Character.isUpperCase(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    if ("[char]".equals(lastPattern)) {
                        lastPattern = "[wORD]";
                        sb.replace(sb.length() - 6, sb.length(), lastPattern);
                    } else if (pos == sequenceBeginning + 1) {
                        lastPattern = "[Char]";
                        sb.append(lastPattern);
                    } else if (pos != sequenceBeginning) {
                        lastPattern = "[WORD]";
                        sb.append(lastPattern);
                    }
                    if (pos == length) {
                        break;
                    }
                }

                // Check lower-case sequence (include alphabetical character that are not east asian ideograms).
                if (Character.isAlphabetic(Character.codePointAt(ca, pos))
                        && !Character.isIdeographic(Character.codePointAt(ca, pos))) {
                    sequenceBeginning = pos;
                    pos++;
                    while (pos < length && Character.isAlphabetic(Character.codePointAt(ca, pos))
                            && !Character.isUpperCase(Character.codePointAt(ca, pos))
                            && !Character.isIdeographic(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    // If the last pattern is a Capital Letter, the pattern will be replaced by [Word]
                    if ("[Char]".equals(lastPattern)) {
                        lastPattern = "[Word]";
                        sb.replace(sb.length() - 6, sb.length(), lastPattern);
                    }
                    // If the position has only moved by one, the sequence is a single char.
                    else if (pos == sequenceBeginning + 1) {
                        lastPattern = "[char]";
                        sb.append(lastPattern);
                    } else if (pos != sequenceBeginning) {
                        lastPattern = "[word]";
                        sb.append(lastPattern);
                    }
                    if (pos == length) {
                        break;
                    }
                }

                // Check ideograms
                if (Character.isIdeographic(Character.codePointAt(ca, pos))) {
                    sequenceBeginning = pos;
                    lastPattern = "[Ideogram]";
                    pos++;
                    while (pos < length && Character.isIdeographic(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    if (pos > sequenceBeginning + 1) {
                        lastPattern = "[IdeogramSeq]";
                    }
                    sb.append(lastPattern);
                    if (pos == length) {
                        break;
                    }
                }

                // Check numbers
                if (Character.isDigit(Character.codePointAt(ca, pos))) {
                    sequenceBeginning = pos;
                    pos++;
                    while (pos < length && Character.isDigit(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    if (pos == sequenceBeginning + 1) {
                        lastPattern = "[digit]";
                        sb.append(lastPattern);
                    } else if (pos != sequenceBeginning) {
                        lastPattern = "[number]";
                        sb.append(lastPattern);
                    }
                }

                if (start == pos) {
                    lastPattern = "" + ca[start];
                    sb.append(lastPattern);
                    isComplete = false;
                    pos++;
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
            int pos = 0;
            int sequenceBeginning;
            int length = stringToRecognize.length();
            while (pos < length) {
                int start = pos;

                sequenceBeginning = pos;

                // Check ideograms.
                // If there is some digits in the middle of the sequence, it will be recognized as a [alnum(CJK)] pattern.
                if (Character.isIdeographic(Character.codePointAt(ca, pos))) {
                    pos++;
                    while (pos < length && Character.isIdeographic(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    if (pos < length && Character.isDigit(Character.codePointAt(ca, pos))) {
                        pos++;
                        while (pos < length && (Character.isIdeographic(Character.codePointAt(ca, pos))
                                || Character.isDigit(Character.codePointAt(ca, pos)))) {
                            pos++;
                        }
                        sb.append("[alnum(CJK)]");
                    } else if (pos == sequenceBeginning + 1) {
                        sb.append("[Ideogram]");
                    } else {
                        sb.append("[IdeogramSeq]");
                    }
                    if (pos == length) {
                        break;
                    }
                } else if (Character.isAlphabetic(Character.codePointAt(ca, pos))) {
                    pos++;
                    while (pos < length && !Character.isIdeographic(Character.codePointAt(ca, pos))
                            && Character.isAlphabetic(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    if (pos < length && Character.isDigit(Character.codePointAt(ca, pos))) {
                        pos++;
                        // Continue to increment while the character is a digit or is alphabetic but not ideographic.
                        while (pos < length && (Character.isDigit(Character.codePointAt(ca, pos))
                                || (Character.isAlphabetic(Character.codePointAt(ca, pos))
                                        && !Character.isIdeographic(Character.codePointAt(ca, pos))))) {
                            pos++;
                        }
                        sb.append("[alnum]");
                    } else if (pos == sequenceBeginning + 1) {
                        sb.append("[char]");
                    } else {
                        sb.append("[word]");
                    }
                } else if (Character.isDigit(Character.codePointAt(ca, pos))) {
                    pos++;
                    while (pos < length && Character.isDigit(Character.codePointAt(ca, pos))) {
                        pos++;
                    }
                    if (pos < length && !Character.isIdeographic(Character.codePointAt(ca, pos))
                            && Character.isAlphabetic(Character.codePointAt(ca, pos))) {
                        pos++;
                        while (pos < length && !Character.isIdeographic(Character.codePointAt(ca, pos))
                                && (Character.isAlphabetic(Character.codePointAt(ca, pos))
                                        || Character.isDigit(Character.codePointAt(ca, pos)))) {
                            pos++;
                        }
                        sb.append("[alnum]");
                    } else if (pos == sequenceBeginning + 1) {
                        sb.append("[digit]");
                    } else {
                        sb.append("[number]");
                    }
                } else {
                    sb.append(ca[start]);
                    isComplete = false;
                    pos++;
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
    }

}
