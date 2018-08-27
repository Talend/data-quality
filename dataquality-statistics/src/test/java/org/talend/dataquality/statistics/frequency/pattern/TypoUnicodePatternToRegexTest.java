package org.talend.dataquality.statistics.frequency.pattern;

import org.junit.Test;
import org.talend.dataquality.common.regex.*;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class TypoUnicodePatternToRegexTest {

    private static final String JAPANESE_SYLLABARY = Hiragana.range + HiraganaSmall.range + Katakana.range + KatakanaSmall.range;

    private static final String END_WORD_REGEX = "]+";

    private TypoUnicodePatternToRegex service = new TypoUnicodePatternToRegex();

    private void print(char c) {
        System.out.println("\\u" + Integer.toHexString(c | 0x10000).substring(1));
    }

    @Test
    public void lowerLatinChar() {
        final String example = "a";
        final String pattern = "[char]";

        assertCaseInsensitiveContain(pattern, LatinLettersSmall.range, LatinLetters.range);
        assertCaseInsensitiveRegexMatche(example, pattern);

        assertCaseSensitiveContain(pattern, LatinLettersSmall.range);
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void upperLatinChar() {
        final String example = "A";
        final String pattern = "[Char]";

        assertCaseInsensitiveContain(pattern, LatinLettersSmall.range, LatinLetters.range);
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveContain(pattern, LatinLetters.range);
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void lowerLatinWord() {
        final String example = "wowowo";
        final String pattern = "[word]";

        assertCaseInsensitiveContain(pattern, LatinLettersSmall.range, LatinLetters.range, END_WORD_REGEX);
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveContain(pattern, LatinLettersSmall.range, END_WORD_REGEX);
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void twoLatinWordsSeparatedBySpace() {
        final String example = "wowowo yolo";
        final String pattern = "[word] [word]";

        assertCaseSensitiveContain(pattern, LatinLettersSmall.range, END_WORD_REGEX, " ");
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void upperLatinWord() {
        final String example = "WOWOWO";
        final String pattern = "[WORD]";

        assertCaseInsensitiveContain(pattern, LatinLettersSmall.range, LatinLetters.range, END_WORD_REGEX);
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveContain(pattern, LatinLetters.range, END_WORD_REGEX);
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void latinWord() {
        final String example = "Test";
        final String pattern = "[Word]";

        assertCaseInsensitiveContain(pattern, LatinLettersSmall.range, LatinLetters.range, END_WORD_REGEX);
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveContain(pattern, LatinLettersSmall.range, LatinLetters.range, END_WORD_REGEX);
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void number() {
        final String example = "42";
        final String pattern = "[number]";

        assertCaseInsensitiveContain(pattern, LatinAsciiDigits.range, END_WORD_REGEX);
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveContain(pattern, LatinAsciiDigits.range, END_WORD_REGEX);
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void digit() {
        final String example = "9";
        final String pattern = "[digit]";

        assertCaseInsensitiveContain(pattern, LatinAsciiDigits.range);
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseInsensitiveContain(pattern, LatinAsciiDigits.range);
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void latinAlphanumeric() {
        final String example = "aaa8";
        final String pattern = "[alnum]";

        assertCaseInsensitiveContain(pattern, LatinLettersSmall.range, LatinLetters.range, LatinAsciiDigits.range,
                END_WORD_REGEX);
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseInsensitiveContain(pattern, LatinLettersSmall.range, LatinLetters.range, LatinAsciiDigits.range,
                END_WORD_REGEX);
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void twoPatternSeparatedByDot() {
        final String example = "42.pony";
        final String pattern = "[number].[word]";

        assertCaseInsensitiveContain(pattern, "\\.");
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveContain(pattern, "\\.");
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void patternWithParenthesis() {
        final String example = "(10)";
        final String pattern = "([number])";

        assertCaseInsensitiveContain(pattern, "\\(", "\\)");
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveContain(pattern, "\\(", "\\)");
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void patternWithBrackets() {
        final String example = "[10]";
        final String pattern = "[[number]]";

        assertCaseInsensitiveContain(pattern, "\\[", "\\]");
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveContain(pattern, "\\[", "\\]");
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void patternWithQuestionMark() {
        final String example = "10?";
        final String pattern = "[number]?";

        assertCaseInsensitiveContain(pattern, "\\?");
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveContain(pattern, "\\?");
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void patternWithPlusAndStar() {
        final String example = "10+*";
        final String pattern = "[number]+*";

        assertCaseInsensitiveContain(pattern, "\\+", "\\*");
        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveContain(pattern, "\\+", "\\*");
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void defaultText() {
        final String example = "C'est un TEXTE Test d'obSERVatIon des 8 pATTERNS possibles (sur plus de 10)";
        final String pattern = "[Char]'[word] [word] [WORD] [Word] [char]'[word][WORD][word][Word] [word] [digit] [char][WORD] [word] ([word] [word] [word] [number])";

        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void email() {
        final String example = "Example123@protonmail.com";
        final String pattern = "[alnum]@[word].[word]";

        assertCaseInsensitiveRegexMatche(example, pattern);
        assertCaseSensitiveRegexMatche(example, pattern);
    }

    @Test
    public void chineseIdeogram() {
        final String example = "袁";
        final String patternNoCase = "[Ideogram]";

        assertCaseInsensitiveRegexMatche(example, patternNoCase);
    }

    @Test
    public void chineseAlphanumeric() {
        final String example = "花木蘭88";
        final String patternNoCase = "[alnum(CJK)]";

        assertCaseInsensitiveRegexMatche(example, patternNoCase);
    }

    @Test
    public void chinese() {
        final String example = "袁 花木蘭88";
        final String patternNoCase = "[Ideogram] [alnum(CJK)]";
        assertCaseInsensitiveRegexMatche(example, patternNoCase);

        final String patternCase = "[Ideogram] [IdeogramSeq][number]";
        assertCaseInsensitiveRegexMatche(example, patternCase);
    }

    @Test
    public void chineseText() {
        final String example = "木兰辞\n" + "\n" + "唧唧复唧唧，木兰当户织。\n" + "不闻机杼声，唯闻女叹息。\n" + "问女何所思？问女何所忆？\n" + "女亦无所思，女亦无所忆。\n"
                + "昨夜见军帖，可汗大点兵，\n" + "军书十二卷，卷卷有爷名。\n" + "阿爷无大儿，木兰无长兄，\n" + "愿为市鞍马，从此替爷征。";
        final String pattern = "[IdeogramSeq]\n" + "\n[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]。\n"
                + "[IdeogramSeq]？[IdeogramSeq]？\n" + "[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]，\n"
                + "[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]，\n" + "[IdeogramSeq]，[IdeogramSeq]。";

        assertCaseInsensitiveRegexMatche(example, pattern);
    }

    @Test
    public void chineseQuestion() {
        final String example = "不亦1說乎？有";
        final String pattern = "[alnum(CJK)]？[Ideogram]";

        assertCaseInsensitiveRegexMatche(example, pattern);
    }

    @Test
    public void japaneseAlphanumeric() {
        final String example = "こんにちは123";
        final String patternNoCase = "[alnum]";
        assertCaseInsensitiveRegexMatche(example, patternNoCase);
    }

    @Test
    public void japaneseWord() {
        final String example = "こんにちは";
        final String patternNoCase = "[word]";
        assertCaseInsensitiveRegexMatche(example, patternNoCase);
    }

    @Test
    public void japanese() {
        final String example = "こんにちは123 こんにちは？你好/Hello!";
        final String patternNoCase = "[alnum] [word]？[IdeogramSeq]/[word]!";
        assertCaseInsensitiveRegexMatche(example, patternNoCase);

        final String patternCase = "[word][number] [word]？[IdeogramSeq]/[Word]!";
        assertCaseInsensitiveRegexMatche(example, patternCase);
    }

    @Test
    public void japaneseCJK() {
        final String example = "日本語123 日本語？你好/Hello!";
        final String patternNoCase = "[alnum(CJK)] [IdeogramSeq]？[IdeogramSeq]/[word]!";
        assertCaseInsensitiveRegexMatche(example, patternNoCase);

        final String patternCase = "[IdeogramSeq][number] [IdeogramSeq]？[IdeogramSeq]/[Word]!";
        assertCaseInsensitiveRegexMatche(example, patternCase);
    }

    @Test
    public void surrogatePair() {
        assertCaseInsensitiveRegexMatche("𠀐", "[Ideogram]");
        assertCaseInsensitiveRegexMatche("𠀐𠀑我𠀒𠀓", "[IdeogramSeq]");
        assertCaseInsensitiveRegexMatche("𠀐𠀑我𠀒𠀓 我Abc", "[IdeogramSeq] [Ideogram][word]");
        assertCaseInsensitiveRegexMatche("𠀐12//𠀑我?𠀑", "[alnum(CJK)]//[IdeogramSeq]?[Ideogram]");
        assertCaseInsensitiveRegexMatche("𠀐12//𠀑我?𠀑", "[Ideogram][number]//[IdeogramSeq]?[Ideogram]");
    }

    @Test
    public void arabic() {
        assertCaseInsensitiveRegexMatche(
                "يَجِبُ عَلَى الإنْسَانِ أن يَكُونَ أمِيْنَاً وَصَادِقَاً مَعَ نَفْسِهِ وَمَعَ أَهْلِهِ وَجِيْرَانِهِ وَأَنْ يَبْذُلَ كُلَّ جُهْدٍ فِي إِعْلاءِ شَأْنِ الوَطَنِ وَأَنْ يَعْمَلَ عَلَى مَا يَجْلِبُ السَّعَادَةَ لِلنَّاسِ . ولَن يَتِمَّ لَهُ ذلِك إِلا بِأَنْ يُقَدِّمَ المَنْفَعَةَ العَامَّةَ عَلَى المَنْفَعَةِ الخَاصَّةِ وَهذَا مِثَالٌ لِلتَّضْحِيَةِ .",
                "[word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] . [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] .");
        assertCaseInsensitiveRegexMatche(
                "يَجِبُ عَلَى الإنْسَانِ أن يَكُونَ أمِيْنَاً وَصَادِقَاً مَعَ نَفْسِهِ وَمَعَ أَهْلِهِ وَجِيْرَانِهِ وَأَنْ يَبْذُلَ كُلَّ جُهْدٍ فِي إِعْلاءِ شَأْنِ الوَطَنِ وَأَنْ يَعْمَلَ عَلَى مَا يَجْلِبُ السَّعَادَةَ لِلنَّاسِ . ولَن يَتِمَّ لَهُ ذلِك إِلا بِأَنْ يُقَدِّمَ المَنْفَعَةَ العَامَّةَ عَلَى المَنْفَعَةِ الخَاصَّةِ وَهذَا مِثَالٌ لِلتَّضْحِيَةِ .",
                "[word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] . [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] .");
    }

    @Test
    public void ancientGreek() {
        assertCaseSensitiveRegexMatche("Ἰοὺ", "[Word]");
        assertCaseSensitiveRegexMatche("ἰού", "[word]");
        assertCaseSensitiveRegexMatche("Ὦ", "[Char]");
        assertCaseSensitiveRegexMatche("ʼ", "[char]");
        assertCaseSensitiveRegexMatche("πάντʼ", "[word]");
        assertCaseSensitiveRegexMatche(
                "Ἰοὺ ἰού· τὰ πάντʼ ἂν ἐξήκοι σαφῆ.Ὦ φῶς, τελευταῖόν σε προσϐλέψαιμι νῦν,ὅστις πέφασμαι φύς τʼ ἀφʼ ὧν οὐ χρῆν, ξὺν οἷς τʼοὐ χρῆν ὁμιλῶν, οὕς τέ μʼ οὐκ ἔδει κτανών.",
                "[Word] [word]· [word] [word] [word] [word] [word].[Char] [word], [word] [word] [word] [word],[word] [word] [word] [word] [word] [word] [word] [word], [word] [word] [word] [word] [word], [word] [word] [word] [word] [word] [word].");
    }

    @Test
    public void latin() {
        assertCaseSensitiveRegexMatche("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞß", "[WORD]");
        assertCaseInsensitiveRegexMatche("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþß", "[WORD]");
        assertCaseSensitiveRegexMatche("ABCDEFGHIJKLMNOPQRSTUVwxyzàáâãäåæçèéêëìÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞß", "[Word]");
        assertCaseInsensitiveRegexMatche("abcdefghijklmnopqrsTUVWXYZÀÁÂÃÄÅÆÇÈéêëìíîïðñòóôõöøùúûüýþß", "[Word]");
        assertCaseSensitiveRegexMatche("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿ", "[word]");
        assertCaseInsensitiveRegexMatche("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞß", "[word]");
    }

    private String assertCaseInsensitiveRegexMatche(String example, String pattern) {
        String regex = service.toRegex(pattern, false);
        assertTrue(Pattern.compile(regex).matcher(example).matches());
        return regex;
    }

    private String assertCaseSensitiveRegexMatche(String example, String pattern) {
        String regex = service.toRegex(pattern, true);
        assertTrue(Pattern.compile(regex).matcher(example).matches());
        return regex;
    }

    private void assertCaseInsensitiveContain(String pattern, String... ranges) {
        String regex = service.toRegex(pattern, false);
        for (String range : ranges) {
            assertTrue(regex + " must contain " + range, regex.contains(range));
        }
    }

    private void assertCaseSensitiveContain(String pattern, String... ranges) {
        String regex = service.toRegex(pattern, true);
        for (String range : ranges) {
            assertTrue(regex + " must contain " + range, regex.contains(range));
        }
    }
}