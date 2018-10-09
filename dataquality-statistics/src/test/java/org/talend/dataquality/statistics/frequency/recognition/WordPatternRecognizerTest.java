package org.talend.dataquality.statistics.frequency.recognition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.talend.daikon.pattern.word.WordPattern;
import org.talend.daikon.pattern.word.WordPatternToRegex;

/**
 * Created by afournier on 06/04/17.
 */
public class WordPatternRecognizerTest {

    private WordPatternRecognizer noCaseRecognizer = WordPatternRecognizer.noCase();

    private WordPatternRecognizer withCaseRecognizer = WordPatternRecognizer.withCase();

    @Test
    public void checkIsoAllCodepoints() {

        List<Integer> issues = new ArrayList<>();
        for (int codePoint = 0; codePoint < Character.MAX_CODE_POINT; codePoint++) {
            String string = String.valueOf(Character.toChars(codePoint));

            String wordPattern = withCaseRecognizer.recognize(string).getPatternStringSet().iterator().next();
            String regex = WordPatternToRegex.toRegex(wordPattern, true);
            Pattern pattern = Pattern.compile(regex);
            String errorMessage = "Code point " + codePoint + " (used for " + string + " with hex code "
                    + Integer.toHexString(codePoint) + ") does not match " + regex + " (associated wordPattern is "
                    + (WordPattern.get(wordPattern) == null ? string : WordPattern.get(wordPattern).name()) + ")";
            Assert.assertTrue(errorMessage, pattern.matcher(string).matches());
            if (!pattern.matcher(string).matches())
                issues.add(codePoint);
        }
        display(issues);

    }

    @Test
    public void checkRangesUpper() {

        List<Integer> issues = new ArrayList<>();
        Pattern pattern = Pattern.compile(WordPattern.UPPER_CHAR.getCaseSensitive());
        for (int codePoint = 0; codePoint < Character.MAX_CODE_POINT; codePoint++) {
            String string = String.valueOf(Character.toChars(codePoint));
            boolean matchPattern = pattern.matcher(string).matches();
            boolean recognizeString = WordPatternRecognizer.isUpper(codePoint);
            Assert.assertTrue(matchPattern == recognizeString);
            if (matchPattern != recognizeString)
                issues.add(codePoint);
        }
        display(issues);
    }

    @Test
    public void checkRangesIdeographic() {

        List<Integer> issues = new ArrayList<>();
        Pattern pattern = Pattern.compile(WordPattern.IDEOGRAM.getCaseSensitive());
        for (int codePoint = 0; codePoint < Character.MAX_CODE_POINT; codePoint++) {
            String string = String.valueOf(Character.toChars(codePoint));
            boolean matchPattern = pattern.matcher(string).matches();
            boolean recognizeString = WordPatternRecognizer.isIdeographic(codePoint);
            Assert.assertTrue(matchPattern == recognizeString);
            if (matchPattern != recognizeString)
                issues.add(codePoint);
        }
        display(issues);
    }

    @Test
    public void checkRangesHiragana() {

        List<Integer> issues = new ArrayList<>();
        Pattern pattern = Pattern.compile(WordPattern.HIRAGANA.getCaseSensitive());
        for (int codePoint = 0; codePoint < Character.MAX_CODE_POINT; codePoint++) {
            String string = String.valueOf(Character.toChars(codePoint));
            boolean matchPattern = pattern.matcher(string).matches();
            boolean recognizeString = WordPatternRecognizer.isHiragana(codePoint);
            Assert.assertTrue(matchPattern == recognizeString);
            if (matchPattern != recognizeString)
                issues.add(codePoint);
        }
        display(issues);
    }

    @Test
    public void checkRangesKatakana() {

        List<Integer> issues = new ArrayList<>();
        Pattern pattern = Pattern.compile(WordPattern.KATAKANA.getCaseSensitive());
        for (int codePoint = 0; codePoint < Character.MAX_CODE_POINT; codePoint++) {
            String string = String.valueOf(Character.toChars(codePoint));
            boolean matchPattern = pattern.matcher(string).matches();
            boolean recognizeString = WordPatternRecognizer.isKatakana(codePoint);
            Assert.assertTrue(matchPattern == recognizeString);
            if (matchPattern != recognizeString)
                issues.add(codePoint);
        }
        display(issues);
    }

    @Test
    public void checkRangesHangul() {

        List<Integer> issues = new ArrayList<>();
        Pattern pattern = Pattern.compile(WordPattern.HANGUL.getCaseSensitive());
        for (int codePoint = 0; codePoint < Character.MAX_CODE_POINT; codePoint++) {
            String string = String.valueOf(Character.toChars(codePoint));
            boolean matchPattern = pattern.matcher(string).matches();
            boolean recognizeString = WordPatternRecognizer.isHangul(codePoint);
            Assert.assertTrue(matchPattern == recognizeString);
            if (matchPattern != recognizeString)
                issues.add(codePoint);
        }
        display(issues);
    }

    @Test
    public void checkRangesChar() {

        List<Integer> issues = new ArrayList<>();
        Pattern pattern = Pattern.compile(WordPattern.LOWER_CHAR.getCaseInsensitive());
        for (int codePoint = 0; codePoint < Character.MAX_CODE_POINT; codePoint++) {
            String string = String.valueOf(Character.toChars(codePoint));
            boolean matchPattern = pattern.matcher(string).matches();
            boolean recognizeString = WordPatternRecognizer.isLetter(codePoint);
            Assert.assertTrue(matchPattern == recognizeString);
            if (matchPattern != recognizeString)
                issues.add(codePoint);
        }
        display(issues);
    }

    @Test
    public void checkRangesLower() {

        List<Integer> issues = new ArrayList<>();
        Pattern pattern = Pattern.compile(WordPattern.LOWER_CHAR.getCaseSensitive());
        for (int codePoint = 0; codePoint < Character.MAX_CODE_POINT; codePoint++) {
            String string = String.valueOf(Character.toChars(codePoint));
            boolean matchPattern = pattern.matcher(string).matches();
            boolean recognizeString = WordPatternRecognizer.isLower(codePoint);
            Assert.assertTrue(matchPattern == recognizeString);
            if (matchPattern != recognizeString)
                issues.add(codePoint);
        }
        display(issues);
    }

    @Test
    public void checkRangesDigit() {

        List<Integer> issues = new ArrayList<>();
        Pattern pattern = Pattern.compile(WordPattern.DIGIT.getCaseSensitive());
        for (int codePoint = 0; codePoint < Character.MAX_CODE_POINT; codePoint++) {
            String string = String.valueOf(Character.toChars(codePoint));
            boolean matchPattern = pattern.matcher(string).matches();
            boolean recognizeString = WordPatternRecognizer.isDigit(codePoint);
            Assert.assertTrue(matchPattern == recognizeString);
            if (matchPattern != recognizeString)
                issues.add(codePoint);
        }
        display(issues);
    }

    private void display(List<Integer> issues) {
        if (CollectionUtils.isEmpty(issues))
            return;
        int start = issues.get(0).intValue();
        for (int i = 1; i < issues.size(); i++) {
            if (issues.get(i).intValue() == issues.get(i - 1).intValue() + 1) {
                if (i == issues.size() - 1)
                    System.out.print(start + " " + Integer.toHexString(start) + "  -  " + issues.get(i).intValue() + " "
                            + Integer.toHexString(issues.get(i).intValue()) + " (" + (issues.get(i).intValue() - start + 1)
                            + " codepoints)\n");
            } else {
                System.out.print(start + " " + Integer.toHexString(start) + "  -  " + issues.get(i - 1).intValue() + " "
                        + Integer.toHexString(issues.get(i - 1).intValue()) + " (" + (issues.get(i - 1).intValue() - start + 1)
                        + " codepoints)\n");
                start = issues.get(i).intValue();
            }
        }
        if (start == issues.get(issues.size() - 1).intValue())
            System.out.print(start + " " + Integer.toHexString(start) + "  -  " + start + " " + Integer.toHexString(start)
                    + " (1 codepoint)\n");

        System.out.println("Issue with " + issues.size() + " codepoints");
    }

    @Test
    public void testLatinNoCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("A character is NOT a Word", "[char] [word] [word] [word] [char] [word]");
        str2Pattern.put("someWordsINwORDS", "[word]");
        str2Pattern.put("WoWoWo", "[word]");
        str2Pattern.put("wOwOwO", "[word]");
        checkPatterns(str2Pattern, noCaseRecognizer);
    }

    @Test
    public void testLatinWithCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("A character is NOT a Word", "[Char] [word] [word] [WORD] [char] [Word]");
        str2Pattern.put("someWordsINwORDS", "[word][Word][WORD][char][WORD]");
        str2Pattern.put("WoWoWo", "[Word][Word][Word]");
        str2Pattern.put("wOwOwO", "[char][Word][Word][Char]");
        checkPatterns(str2Pattern, withCaseRecognizer);
    }

    @Test
    public void testEMailPatternsNoCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("Example123@protonmail.com", "[alnum]@[word].[word]");
        str2Pattern.put("anotherExample8@yopmail.com", "[alnum]@[word].[word]");
        str2Pattern.put("不45亦_1說乎@gmail.com", "[Ideogram][number][Ideogram]_[digit][IdeogramSeq]@[word].[word]");
        str2Pattern.put("afff123@gmail.com", "[alnum]@[word].[word]");
        str2Pattern.put("FfF123@gMail.com", "[alnum]@[word].[word]");
        str2Pattern.put("1@gmail123.com", "[digit]@[alnum].[word]");
        str2Pattern.put("123@gmail123.com", "[number]@[alnum].[word]");
        str2Pattern.put("123fe@gm  ail123.com", "[alnum]@[word]  [alnum].[word]");
        checkPatterns(str2Pattern, noCaseRecognizer);
    }

    @Test
    public void testEMailPatternsWithCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("Example123@protonmail.com", "[Word][number]@[word].[word]");
        str2Pattern.put("anotherExample8@yopmail.com", "[word][Word][digit]@[word].[word]");
        checkPatterns(str2Pattern, withCaseRecognizer);
    }

    @Test
    public void testChineseNoCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("袁 花木蘭88", "[Ideogram] [IdeogramSeq][number]");
        str2Pattern.put("愿为市鞍马，从此替爷征。", "[IdeogramSeq]，[IdeogramSeq]。");
        str2Pattern.put("不亦1說乎？有", "[IdeogramSeq][digit][IdeogramSeq]？[Ideogram]");
        checkPatterns(str2Pattern, noCaseRecognizer);
    }

    @Test
    public void testChineseWithCase() {
        checkPattern("袁 花木蘭88", "[Ideogram] [IdeogramSeq][number]", withCaseRecognizer);
    }

    @Test
    /**
     * Some Japanese character is Ideogram, some is not
     */
    public void testJapaneseNoCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("こんにちは123 ？你好/Hello!", "[hiraSeq][number] ？[IdeogramSeq]/[word]!");
        str2Pattern.put("日本語123 日本語？你好/Hello!", "[IdeogramSeq][number] [IdeogramSeq]？[IdeogramSeq]/[word]!");
        checkPatterns(str2Pattern, noCaseRecognizer);
    }

    @Test
    /**
     * Some Japanese character is Ideogram, some is not
     */
    public void testJapaneseWithCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("こんにちは123 ？你好/Hello!", "[hiraSeq][number] ？[IdeogramSeq]/[Word]!");
        str2Pattern.put("日本語123 日本語？你好/Hello!", "[IdeogramSeq][number] [IdeogramSeq]？[IdeogramSeq]/[Word]!");
        checkPatterns(str2Pattern, withCaseRecognizer);
    }

    @Test
    public void testKoreanWithCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("내 친구 말했는데 거기 정말 아름다운 곳이래.",
                "[hangul] [hangulSeq] [hangulSeq] [hangulSeq] [hangulSeq] [hangulSeq] [hangulSeq].");
        str2Pattern.put("그러던데", "[hangulSeq]");
        checkPatterns(str2Pattern, withCaseRecognizer);
    }

    @Test
    public void testKoreanNoCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("내 친구 말했는데 거기 정말 아름다운 곳이래.",
                "[hangul] [hangulSeq] [hangulSeq] [hangulSeq] [hangulSeq] [hangulSeq] [hangulSeq].");
        str2Pattern.put("그러던데", "[hangulSeq]");
        checkPatterns(str2Pattern, noCaseRecognizer);
    }

    @Test
    public void testSurrogatePairNoCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("𠀐", "[Ideogram]");
        str2Pattern.put("𠀐𠀑我𠀒𠀓", "[IdeogramSeq]");
        str2Pattern.put("𠀐𠀑我𠀒𠀓 我Abc", "[IdeogramSeq] [Ideogram][word]");
        str2Pattern.put("𠀐12//𠀑我?𠀑", "[Ideogram][number]//[IdeogramSeq]?[Ideogram]");
        checkPatterns(str2Pattern, noCaseRecognizer);
    }

    @Test
    public void testSurrogatePairWithCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("𠀐𠀑我𠀒𠀓 我Abc", "[IdeogramSeq] [Ideogram][Word]");
        str2Pattern.put("𠀐12//𠀑我?𠀑", "[Ideogram][number]//[IdeogramSeq]?[Ideogram]");
        checkPatterns(str2Pattern, withCaseRecognizer);
    }

    @Test
    public void testAncientGreekNoCase() {
        checkPattern("Ἰοὺ ἰού· τὰ πάντʼ ἂν ἐξήκοι σαφῆ.", "[word] [word]· [word] [word]ʼ [word] [word] [word].",
                noCaseRecognizer);
    }

    @Test
    public void testAncientGreekWithCase() {
        checkPattern("Ἰοὺ ἰού· τὰ πάντʼ ἂν ἐξήκοι σαφῆ.", "[Word] [word]· [word] [word]ʼ [word] [word] [word].",
                withCaseRecognizer);
    }

    @Test
    public void testMixLatinChineseNoCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("子曰：「學而時習之，不1說乎？有朋Aar1AA23自遠方來，不亦樂乎？",
                "[IdeogramSeq]：「[IdeogramSeq]，[Ideogram][digit][IdeogramSeq]？[IdeogramSeq][alnum][IdeogramSeq]，[IdeogramSeq]？");
        str2Pattern.put("Latin2中文", "[alnum][IdeogramSeq]");
        str2Pattern.put("中文2Latin", "[IdeogramSeq][alnum]");
        checkPatterns(str2Pattern, noCaseRecognizer);
    }

    @Test
    public void testMixLatinChineseWithCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("子曰：「學而時習之，不1說乎？有朋Aar1AA23自遠方來，不亦樂乎？",
                "[IdeogramSeq]：「[IdeogramSeq]，[Ideogram][digit][IdeogramSeq]？[IdeogramSeq][Word][digit][WORD][number][IdeogramSeq]，[IdeogramSeq]？");
        str2Pattern.put("Latin2中文", "[Word][digit][IdeogramSeq]");
        str2Pattern.put("中文2Latin", "[IdeogramSeq][digit][Word]");
        checkPatterns(str2Pattern, withCaseRecognizer);
    }

    private void checkPattern(String searchExpression, String expectedPattern, WordPatternRecognizer recognizer) {
        RecognitionResult result = recognizer.recognize(searchExpression);
        Assert.assertEquals(Collections.singleton(expectedPattern), result.getPatternStringSet());
        Assert.assertTrue(result.isComplete());
    }

    private void checkPatterns(Map<String, String> str2Pattern, WordPatternRecognizer recognizer) {
        str2Pattern.forEach((str, pattern) -> {
            checkPattern(str, pattern, recognizer);
        });
    }
}