package org.talend.dataquality.statistics.frequency.recognition;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by afournier on 06/04/17.
 */
public class TypoUnicodePatternRecognizerTest {

    private TypoUnicodePatternRecognizer noCaseRecognizer = TypoUnicodePatternRecognizer.noCase();

    private TypoUnicodePatternRecognizer withCaseRecognizer = TypoUnicodePatternRecognizer.withCase();

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
        str2Pattern.put("不45亦_1說乎@gmail.com", "[alnum(CJK)]_[alnum(CJK)]@[word].[word]");
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
        str2Pattern.put("袁 花木蘭88", "[Ideogram] [alnum(CJK)]");
        str2Pattern.put("愿为市鞍马，从此替爷征。", "[IdeogramSeq]，[IdeogramSeq]。");
        str2Pattern.put("不亦1說乎？有", "[alnum(CJK)]？[Ideogram]");
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
        str2Pattern.put("こんにちは123 こんにちは？你好/Hello!", "[alnum] [word]？[IdeogramSeq]/[word]!");
        str2Pattern.put("日本語123 日本語？你好/Hello!", "[alnum(CJK)] [IdeogramSeq]？[IdeogramSeq]/[word]!");
        checkPatterns(str2Pattern, noCaseRecognizer);
    }

    @Test
    /**
     * Some Japanese character is Ideogram, some is not
     */
    public void testJapaneseWithCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("こんにちは123 こんにちは？你好/Hello!", "[word][number] [word]？[IdeogramSeq]/[Word]!");
        str2Pattern.put("日本語123 日本語？你好/Hello!", "[IdeogramSeq][number] [IdeogramSeq]？[IdeogramSeq]/[Word]!");
        checkPatterns(str2Pattern, withCaseRecognizer);
    }

    @Test
    public void testSurrogatePairNoCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("𠀐", "[Ideogram]");
        str2Pattern.put("𠀐𠀑我𠀒𠀓", "[IdeogramSeq]");
        str2Pattern.put("𠀐𠀑我𠀒𠀓 我Abc", "[IdeogramSeq] [Ideogram][word]");
        str2Pattern.put("𠀐12//𠀑我?𠀑", "[alnum(CJK)]//[IdeogramSeq]?[Ideogram]");
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
    /**
     * Arabic does not have capital letters. The shape of letters change depending on the position in the word.
     */
    public void testArabic() {
        checkPattern(
                "ومنذ العمل بنظام التحكيم الدولي في مصر عام 1994 خسرت" + " القاهرة 76 قضية من إجمالي 78 قضية مع مستثمرين أجانب.",
                "[word] [word] [word] [word] [word] [word] [word] [word] [number] [word] "
                        + "[word] [number] [word] [word] [word] [number] [word] [word] [word] [word].",
                noCaseRecognizer);
    }

    @Test
    public void testAncientGreekNoCase() {
        checkPattern("Ἰοὺ ἰού· τὰ πάντʼ ἂν ἐξήκοι σαφῆ.", "[word] [word]· [word] [word] [word] [word] [word].", noCaseRecognizer);
    }

    @Test
    public void testAncientGreekWithCase() {
        checkPattern("Ἰοὺ ἰού· τὰ πάντʼ ἂν ἐξήκοι σαφῆ.", "[Word] [word]· [word] [word] [word] [word] [word].",
                withCaseRecognizer);
    }

    @Test
    public void testMixLatinChineseNoCase() {
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("子曰：「學而時習之，不1說乎？有朋Aar1AA23自遠方來，不亦樂乎？",
                "[IdeogramSeq]：「[IdeogramSeq]，[alnum(CJK)]？[IdeogramSeq][alnum][IdeogramSeq]，[IdeogramSeq]？");
        str2Pattern.put("Latin2中文", "[alnum][IdeogramSeq]");
        str2Pattern.put("中文2Latin", "[alnum(CJK)][word]");
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

    private void checkPattern(String searchExpression, String expectedPattern, TypoUnicodePatternRecognizer recognizer) {
        RecognitionResult result = recognizer.recognize(searchExpression);
        Assert.assertEquals(Collections.singleton(expectedPattern), result.getPatternStringSet());
        Assert.assertTrue(result.isComplete());
    }

    private void checkPatterns(Map<String, String> str2Pattern, TypoUnicodePatternRecognizer recognizer) {
        str2Pattern.forEach((str, pattern) -> {
            checkPattern(str, pattern, recognizer);
        });
    }
}