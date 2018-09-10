package org.talend.dataquality.statistics.frequency.recognition;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by afournier on 06/04/17.
 */
public class TypoUnicodePatternRecognizerTest {

    @Test
    public void testWithCaseRecognition() {
        String str = "C'est un TEXTE Test d'obSERVatIon des 8 pATTERNS possibles (sur plus de 10)";
        RecognitionResult result = TypoUnicodePatternRecognizer.withCase().recognize(str);
        Assert.assertEquals(
                "[Char]'[word] [word] [WORD] [Word] [char]'[word][WORD][word][Word] [word] [digit] [char][WORD] [word] ([word] [word] [word] [number])",
                result.getPatternStringSet().iterator().next());
        Assert.assertTrue(result.isComplete());
    }

    @Test
    public void testLatin() {
        // examples presented in the JavaDoc
        String str = "A character is NOT a Word";
        RecognitionResult result = TypoUnicodePatternRecognizer.noCase().recognize(str);
        RecognitionResult result2 = TypoUnicodePatternRecognizer.withCase().recognize(str);
        Assert.assertEquals("[char] [word] [word] [word] [char] [word]", result.getPatternStringSet().iterator().next());
        Assert.assertEquals("[Char] [word] [word] [WORD] [char] [Word]", result2.getPatternStringSet().iterator().next());

        String str2 = "someWordsINwORDS";
        RecognitionResult result3 = TypoUnicodePatternRecognizer.noCase().recognize(str2);
        RecognitionResult result4 = TypoUnicodePatternRecognizer.withCase().recognize(str2);
        Assert.assertEquals("[word]", result3.getPatternStringSet().iterator().next());
        Assert.assertEquals("[word][Word][WORD][char][WORD]", result4.getPatternStringSet().iterator().next());

        // If capital and small letters alternate in the sequence, 
        // we recognize a new pattern "Word" or "wORD" each time (cf TDQ-15225)
        String str3 = "WoWoWo";
        RecognitionResult result5 = TypoUnicodePatternRecognizer.noCase().recognize(str3);
        RecognitionResult result6 = TypoUnicodePatternRecognizer.withCase().recognize(str3);
        Assert.assertEquals("[word]", result5.getPatternStringSet().iterator().next());
        Assert.assertEquals("[Word][Word][Word]", result6.getPatternStringSet().iterator().next());

        String str4 = "wOwOwO";
        RecognitionResult result7 = TypoUnicodePatternRecognizer.noCase().recognize(str4);
        RecognitionResult result8 = TypoUnicodePatternRecognizer.withCase().recognize(str4);
        Assert.assertEquals("[word]", result7.getPatternStringSet().iterator().next());
        Assert.assertEquals("[char][Word][Word][Char]", result8.getPatternStringSet().iterator().next());

        Assert.assertTrue(result.isComplete());
        Assert.assertTrue(result2.isComplete());
        Assert.assertTrue(result3.isComplete());
        Assert.assertTrue(result4.isComplete());
        Assert.assertTrue(result5.isComplete());
        Assert.assertTrue(result6.isComplete());
        Assert.assertTrue(result7.isComplete());
        Assert.assertTrue(result8.isComplete());
    }

    @Test
    public void testEMailPatterns() {
        // examples presented in the JavaDoc
        String str = "Example123@protonmail.com";
        RecognitionResult result = TypoUnicodePatternRecognizer.noCase().recognize(str);
        RecognitionResult result2 = TypoUnicodePatternRecognizer.withCase().recognize(str);
        Assert.assertEquals("[alnum]@[word].[word]", result.getPatternStringSet().iterator().next());
        Assert.assertEquals("[Word][number]@[word].[word]", result2.getPatternStringSet().iterator().next());

        String str2 = "anotherExample8@yopmail.com";
        RecognitionResult result3 = TypoUnicodePatternRecognizer.noCase().recognize(str2);
        RecognitionResult result4 = TypoUnicodePatternRecognizer.withCase().recognize(str2);
        Assert.assertEquals("[alnum]@[word].[word]", result3.getPatternStringSet().iterator().next());
        Assert.assertEquals("[word][Word][digit]@[word].[word]", result4.getPatternStringSet().iterator().next());

        // some other examples with mail patterns
        String str3 = "不45亦_1說乎@gmail.com";
        RecognitionResult result5 = TypoUnicodePatternRecognizer.noCase().recognize(str3);
        Assert.assertEquals("[alnum(CJK)]_[alnum(CJK)]@[word].[word]", result5.getPatternStringSet().iterator().next());

        String str4 = "afff123@gmail.com";
        RecognitionResult result6 = TypoUnicodePatternRecognizer.noCase().recognize(str4);
        Assert.assertEquals("[alnum]@[word].[word]", result6.getPatternStringSet().iterator().next());

        String str5 = "FfF123@gMail.com";
        RecognitionResult result7 = TypoUnicodePatternRecognizer.noCase().recognize(str5);
        Assert.assertEquals("[alnum]@[word].[word]", result7.getPatternStringSet().iterator().next());

        String str6 = "1@gmail123.com";
        RecognitionResult result8 = TypoUnicodePatternRecognizer.noCase().recognize(str6);
        Assert.assertEquals("[digit]@[alnum].[word]", result8.getPatternStringSet().iterator().next());

        String str7 = "123@gmail123.com";
        RecognitionResult result9 = TypoUnicodePatternRecognizer.noCase().recognize(str7);
        Assert.assertEquals("[number]@[alnum].[word]", result9.getPatternStringSet().iterator().next());

        String str8 = "123fe@gm  ail123.com";
        RecognitionResult result10 = TypoUnicodePatternRecognizer.noCase().recognize(str8);
        Assert.assertEquals("[alnum]@[word]  [alnum].[word]", result10.getPatternStringSet().iterator().next());

        Assert.assertTrue(result.isComplete());
        Assert.assertTrue(result2.isComplete());
        Assert.assertTrue(result3.isComplete());
        Assert.assertTrue(result4.isComplete());
        Assert.assertTrue(result5.isComplete());
        Assert.assertTrue(result6.isComplete());
        Assert.assertTrue(result7.isComplete());
        Assert.assertTrue(result8.isComplete());
        Assert.assertTrue(result9.isComplete());
        Assert.assertTrue(result10.isComplete());
    }

    @Test
    public void testChinese() {
        // example presented in the JavaDoc
        String str = "袁 花木蘭88";
        RecognitionResult result = TypoUnicodePatternRecognizer.noCase().recognize(str);
        RecognitionResult result2 = TypoUnicodePatternRecognizer.withCase().recognize(str);
        Assert.assertEquals("[Ideogram] [alnum(CJK)]", result.getPatternStringSet().iterator().next());
        Assert.assertEquals("[Ideogram] [IdeogramSeq][number]", result2.getPatternStringSet().iterator().next());

        // Chinese text (Extract from Ballad of Mulan) :
        String str2 = "木兰辞\n" + "\n" + "唧唧复唧唧，木兰当户织。\n" + "不闻机杼声，唯闻女叹息。\n" + "问女何所思？问女何所忆？\n" + "女亦无所思，女亦无所忆。\n"
                + "昨夜见军帖，可汗大点兵，\n" + "军书十二卷，卷卷有爷名。\n" + "阿爷无大儿，木兰无长兄，\n" + "愿为市鞍马，从此替爷征。";
        RecognitionResult result3 = TypoUnicodePatternRecognizer.noCase().recognize(str2);
        Assert.assertEquals("Recognized pattern : " + result.getPatternStringSet().iterator().next(),
                "[IdeogramSeq]\n" + "\n[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]。\n"
                        + "[IdeogramSeq]？[IdeogramSeq]？\n" + "[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]，\n"
                        + "[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]，\n" + "[IdeogramSeq]，[IdeogramSeq]。",
                result3.getPatternStringSet().iterator().next());

        String str3 = "不亦1說乎？有";
        RecognitionResult result4 = TypoUnicodePatternRecognizer.noCase().recognize(str3);
        Assert.assertEquals("[alnum(CJK)]？[Ideogram]", result4.getPatternStringSet().iterator().next());

        Assert.assertTrue(result.isComplete());
        Assert.assertTrue(result2.isComplete());
        Assert.assertTrue(result3.isComplete());
        Assert.assertTrue(result4.isComplete());
    }

    @Test
    /**
     * Some Japanese character is Ideogram, some is not
     */
    public void testJapanese() {
        String str = "こんにちは123 こんにちは？你好/Hello!";
        RecognitionResult result = TypoUnicodePatternRecognizer.noCase().recognize(str);
        RecognitionResult result2 = TypoUnicodePatternRecognizer.withCase().recognize(str);
        Assert.assertEquals("[alnum] [word]？[IdeogramSeq]/[word]!", result.getPatternStringSet().iterator().next());
        Assert.assertEquals("[word][number] [word]？[IdeogramSeq]/[Word]!", result2.getPatternStringSet().iterator().next());

        String str2 = "日本語123 日本語？你好/Hello!";
        RecognitionResult result3 = TypoUnicodePatternRecognizer.noCase().recognize(str2);
        RecognitionResult result4 = TypoUnicodePatternRecognizer.withCase().recognize(str2);
        Assert.assertEquals("[alnum(CJK)] [IdeogramSeq]？[IdeogramSeq]/[word]!", result3.getPatternStringSet().iterator().next());
        Assert.assertEquals("[IdeogramSeq][number] [IdeogramSeq]？[IdeogramSeq]/[Word]!",
                result4.getPatternStringSet().iterator().next());

        Assert.assertTrue(result.isComplete());
        Assert.assertTrue(result2.isComplete());
        Assert.assertTrue(result3.isComplete());
        Assert.assertTrue(result4.isComplete());
    }

    @Test
    public void testSurrogatePair() {
        String str = "𠀐";
        RecognitionResult result = TypoUnicodePatternRecognizer.noCase().recognize(str);
        Assert.assertEquals("[Ideogram]", result.getPatternStringSet().iterator().next());

        String str2 = "𠀐𠀑我𠀒𠀓";
        RecognitionResult result2 = TypoUnicodePatternRecognizer.noCase().recognize(str2);
        Assert.assertEquals("[IdeogramSeq]", result2.getPatternStringSet().iterator().next());

        String str3 = "𠀐𠀑我𠀒𠀓 我Abc";
        RecognitionResult result3 = TypoUnicodePatternRecognizer.noCase().recognize(str3);
        RecognitionResult result4 = TypoUnicodePatternRecognizer.withCase().recognize(str3);
        Assert.assertEquals("[IdeogramSeq] [Ideogram][word]", result3.getPatternStringSet().iterator().next());
        Assert.assertEquals("[IdeogramSeq] [Ideogram][Word]", result4.getPatternStringSet().iterator().next());

        String str4 = "𠀐12//𠀑我?𠀑";
        RecognitionResult result5 = TypoUnicodePatternRecognizer.noCase().recognize(str4);
        RecognitionResult result6 = TypoUnicodePatternRecognizer.withCase().recognize(str4);
        Assert.assertEquals("[alnum(CJK)]//[IdeogramSeq]?[Ideogram]", result5.getPatternStringSet().iterator().next());
        Assert.assertEquals("[Ideogram][number]//[IdeogramSeq]?[Ideogram]", result6.getPatternStringSet().iterator().next());

        Assert.assertTrue(result.isComplete());
        Assert.assertTrue(result2.isComplete());
        Assert.assertTrue(result3.isComplete());
        Assert.assertTrue(result4.isComplete());
        Assert.assertTrue(result5.isComplete());
        Assert.assertTrue(result6.isComplete());
    }

    @Test
    public void testArabic() {
        // Arabic text fro the Coran. The text is read from right to left and the pattern from left to right.
        String str = "ومنذ العمل بنظام التحكيم الدولي في مصر عام 1994 خسرت"
                + " القاهرة 76 قضية من إجمالي 78 قضية مع مستثمرين أجانب.";
        RecognitionResult result = TypoUnicodePatternRecognizer.noCase().recognize(str);
        RecognitionResult result2 = TypoUnicodePatternRecognizer.withCase().recognize(str);
        Assert.assertEquals(
                "[word] [word] [word] [word] [word] [word] [word] [word] [number] [word] "
                        + "[word] [number] [word] [word] [word] [number] [word] [word] [word] [word].",
                result.getPatternStringSet().iterator().next());
        // Arabic Pattern recognizer when case is taken into account
        Assert.assertEquals(
                "[word] [word] [word] [word] [word] [word] [word] [word] [number] [word] [word] "
                        + "[number] [word] [word] [word] [number] [word] [word] [word] [word].",
                result2.getPatternStringSet().iterator().next());

        Assert.assertTrue(result.isComplete());
        Assert.assertTrue(result2.isComplete());
    }

    @Test
    public void testAncientGreek() {
        // Ancient Greek Text. The Recognizer make the difference between Upper and Lower case.
        String str = "Ἰοὺ ἰού· τὰ πάντʼ ἂν ἐξήκοι σαφῆ. Ὦ φῶς, τελευταῖόν σε προσϐλέψαιμι νῦν,"
                + "ὅστις πέφασμαι φύς τʼ ἀφʼ ὧν οὐ χρῆν, ξὺν οἷς τʼ" + "οὐ χρῆν ὁμιλῶν, οὕς τέ μʼ οὐκ ἔδει κτανών.";
        RecognitionResult result = TypoUnicodePatternRecognizer.withCase().recognize(str);
        Assert.assertEquals(
                "[Word] [word]· [word] [word] [word] [word] [word]. [Char] [word], [word] [word] [word] [word],[word] [word] [word] [word] [word] [word] [word] [word], [word] [word] [word] [word] [word], [word] [word] [word] [word] [word] [word].",
                result.getPatternStringSet().iterator().next());

        Assert.assertTrue(result.isComplete());
    }

    @Test
    public void testMixLatinChinese() {

        // Chinese (Ideograms) mixed with Latin when case is important
        String str = "子曰：「學而時習之，不1說乎？有朋Aar1AA23自遠方來，不亦樂乎？";
        RecognitionResult result = TypoUnicodePatternRecognizer.noCase().recognize(str);
        RecognitionResult result2 = TypoUnicodePatternRecognizer.withCase().recognize(str);
        // Chinese (Ideograms) mixed with Latin when case is not important (see the difference with [alnum] )
        Assert.assertEquals("[IdeogramSeq]：「[IdeogramSeq]，[alnum(CJK)]？" + "[IdeogramSeq][alnum][IdeogramSeq]，[IdeogramSeq]？",
                result.getPatternStringSet().iterator().next());
        Assert.assertEquals(
                "[IdeogramSeq]：「[IdeogramSeq]，[Ideogram][digit][IdeogramSeq]？"
                        + "[IdeogramSeq][Word][digit][WORD][number][IdeogramSeq]，[IdeogramSeq]？",
                result2.getPatternStringSet().iterator().next());

        // example presented in the Javadoc
        String str2 = "Latin2中文";
        RecognitionResult result3 = TypoUnicodePatternRecognizer.noCase().recognize(str2);
        RecognitionResult result4 = TypoUnicodePatternRecognizer.withCase().recognize(str2);
        Assert.assertEquals("[alnum][IdeogramSeq]", result3.getPatternStringSet().iterator().next());
        Assert.assertEquals("[Word][digit][IdeogramSeq]", result4.getPatternStringSet().iterator().next());

        String str3 = "中文2Latin";
        RecognitionResult result5 = TypoUnicodePatternRecognizer.noCase().recognize(str3);
        RecognitionResult result6 = TypoUnicodePatternRecognizer.withCase().recognize(str3);
        Assert.assertEquals("[alnum(CJK)][word]", result5.getPatternStringSet().iterator().next());
        Assert.assertEquals("[IdeogramSeq][digit][Word]", result6.getPatternStringSet().iterator().next());

        Assert.assertTrue(result.isComplete());
        Assert.assertTrue(result2.isComplete());
        Assert.assertTrue(result3.isComplete());
        Assert.assertTrue(result4.isComplete());
        Assert.assertTrue(result5.isComplete());
        Assert.assertTrue(result6.isComplete());
    }
}