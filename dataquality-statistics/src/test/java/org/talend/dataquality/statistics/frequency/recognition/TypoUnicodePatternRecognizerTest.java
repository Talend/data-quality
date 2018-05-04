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
        Assert.assertEquals(
                "[Char]'[word] [word] [WORD] [Word] [char]'[word][WORD][word][Word] [word] [digit] [char][WORD] [word] ([word] [word] [word] [number])",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);
    }

    @Test
    public void testLatin() {
        // examples presented in the JavaDoc
        String str = "A character is NOT a Word";
        Assert.assertEquals("[char] [word] [word] [word] [char] [word]",
                TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);
        Assert.assertEquals("[Char] [word] [word] [WORD] [char] [Word]",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);

        str = "someWordsINwORDS";
        Assert.assertEquals("[word]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);
        Assert.assertEquals("[word][Word][WORD][char][WORD]",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);

        // If capital and small letters alternate in the sequence, we recognize a new pattern "Word" or "wORD" each time (cf TDQ-15225)
        str = "WoWoWo";
        Assert.assertEquals("[word]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);
        Assert.assertEquals("[Word][Word][Word]", TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);

        str = "wOwOwO";
        Assert.assertEquals("[word]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);
        Assert.assertEquals("[char][Word][Word][Char]",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);

    }

    @Test
    public void testEMailPatterns() {
        // examples presented in the JavaDoc
        String str = "Example123@protonmail.com";
        Assert.assertEquals("[alnum]@[word].[word]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);
        Assert.assertEquals("[Word][number]@[word].[word]",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);

        str = "anotherExample8@yopmail.com";
        Assert.assertEquals("[alnum]@[word].[word]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);
        Assert.assertEquals("[word][Word][digit]@[word].[word]",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);

        // some other examples with mail patterns
        str = "不45亦_1說乎@gmail.com";
        Assert.assertEquals("[alnum(CJK)]_[alnum(CJK)]@[word].[word]",
                TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);

        str = "afff123@gmail.com";
        Assert.assertEquals("[alnum]@[word].[word]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);

        str = "FfF123@gMail.com";
        Assert.assertEquals("[alnum]@[word].[word]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);

        str = "1@gmail123.com";
        Assert.assertEquals("[digit]@[alnum].[word]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);

        str = "123@gmail123.com";
        Assert.assertEquals("[number]@[alnum].[word]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);

        str = "123fe@gm  ail123.com";
        Assert.assertEquals("[alnum]@[word]  [alnum].[word]",
                TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);

    }

    @Test
    public void testChinese() {
        // example presented in the JavaDoc
        String str = "袁 花木蘭88";
        Assert.assertEquals("[Ideogram] [alnum(CJK)]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);
        Assert.assertEquals("[Ideogram] [IdeogramSeq][number]",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);

        // Chinese text (Extract from Ballad of Mulan) :
        str = "木兰辞\n" + "\n" + "唧唧复唧唧，木兰当户织。\n" + "不闻机杼声，唯闻女叹息。\n" + "问女何所思？问女何所忆？\n" + "女亦无所思，女亦无所忆。\n" + "昨夜见军帖，可汗大点兵，\n"
                + "军书十二卷，卷卷有爷名。\n" + "阿爷无大儿，木兰无长兄，\n" + "愿为市鞍马，从此替爷征。";
        Assert.assertEquals(
                "[IdeogramSeq]\n" + "\n[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]。\n"
                        + "[IdeogramSeq]？[IdeogramSeq]？\n" + "[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]，\n"
                        + "[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]，\n" + "[IdeogramSeq]，[IdeogramSeq]。",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);

        str = "不亦1說乎？有";
        Assert.assertEquals("[alnum(CJK)]？[Ideogram]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);
    }

    @Test
    public void testArabic() {
        // Arabic text fro the Coran. We can see that the text is read from right to left. (see the last point).
        String str = "يَجِبُ عَلَى الإنْسَانِ أن يَكُونَ أمِيْنَاً وَصَادِقَاً مَعَ نَفْسِهِ وَمَعَ أَهْلِهِ وَجِيْرَانِهِ وَأَنْ يَبْذُلَ كُلَّ جُهْدٍ فِي إِعْلاءِ شَأْنِ الوَطَنِ وَأَنْ يَعْمَلَ عَلَى مَا يَجْلِبُ السَّعَادَةَ لِلنَّاسِ . ولَن يَتِمَّ لَهُ ذلِك إِلا بِأَنْ يُقَدِّمَ المَنْفَعَةَ العَامَّةَ عَلَى المَنْفَعَةِ الخَاصَّةِ وَهذَا مِثَالٌ لِلتَّضْحِيَةِ .";
        Assert.assertEquals(
                "[word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] . [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] .",
                TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);
        // Arabic Pattern recognizer when case is taken into account
        Assert.assertEquals(
                "[word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] . [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] .",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);
    }

    @Test
    public void testAncientGreek() {
        // Ancient Greek Text. The Recognizer make the difference between Upper and Lower case.
        String str = "Ἰοὺ ἰού· τὰ πάντʼ ἂν ἐξήκοι σαφῆ." + "Ὦ φῶς, τελευταῖόν σε προσϐλέψαιμι νῦν,"
                + "ὅστις πέφασμαι φύς τʼ ἀφʼ ὧν οὐ χρῆν, ξὺν οἷς τʼ" + "οὐ χρῆν ὁμιλῶν, οὕς τέ μʼ οὐκ ἔδει κτανών.";
        Assert.assertEquals(
                "[Word] [word]· [word] [word] [word] [word] [word].[Char] [word], [word] [word] [word] [word],[word] [word] [word] [word] [word] [word] [word] [word], [word] [word] [word] [word] [word], [word] [word] [word] [word] [word] [word].",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);

    }

    @Test
    public void testMixLatinChinese() {

        // Chinese (Ideograms) mixed with Latin when case is important
        String str = "子曰：「學而時習之，不1說乎？有朋Aar1AA23自遠方來，不亦樂乎？";
        Assert.assertEquals(
                "[IdeogramSeq]：「[IdeogramSeq]，[Ideogram][digit][IdeogramSeq]？[IdeogramSeq][Word][digit][WORD][number][IdeogramSeq]，[IdeogramSeq]？",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);
        // Chinese (Ideograms) mixed with Latin when case is not important (see the difference with [alnum] )
        Assert.assertEquals("[IdeogramSeq]：「[IdeogramSeq]，[alnum(CJK)]？[IdeogramSeq][alnum][IdeogramSeq]，[IdeogramSeq]？",
                TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);

        // example presented in the Javadoc
        str = "Latin2中文";
        Assert.assertEquals("[alnum][IdeogramSeq]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);
        Assert.assertEquals("[Word][digit][IdeogramSeq]",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);

        str = "中文2Latin";
        Assert.assertEquals("[alnum(CJK)][word]", TypoUnicodePatternRecognizer.noCase().getValuePattern(str).toArray()[0]);
        Assert.assertEquals("[IdeogramSeq][digit][Word]",
                TypoUnicodePatternRecognizer.withCase().getValuePattern(str).toArray()[0]);
    }
}