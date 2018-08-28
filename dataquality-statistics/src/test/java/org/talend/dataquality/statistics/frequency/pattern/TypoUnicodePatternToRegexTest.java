package org.talend.dataquality.statistics.frequency.pattern;

import org.junit.Test;
import org.talend.dataquality.common.regex.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class TypoUnicodePatternToRegexTest {

    private TypoUnicodePatternToRegex service = new TypoUnicodePatternToRegex();

    @Test
    public void lowerLatinCharInsensitive() {
        final String pattern = "[char]";

        String regex = service.toRegex(pattern, false);
        assertMatches("a", regex);
        assertMatches("b", regex);
        assertMatches("B", regex);
        assertNoMatches("0", regex);
        assertNoMatches("袁", regex);
    }

    @Test
    public void lowerLatinCharSensitive() {
        final String pattern = "[char]";
        String regex = service.toRegex(pattern, true);
        assertMatches("a", regex);
        assertMatches("b", regex);
        assertNoMatches("B", regex);
        assertNoMatches("0", regex);
    }

    @Test
    public void upperLatinCharInsensitive() {
        final String pattern = "[Char]";

        String regex = service.toRegex(pattern, false);
        assertMatches("A", regex);
        assertMatches("B", regex);
        assertMatches("b", regex);
        assertNoMatches("0", regex);
    }

    @Test
    public void upperLatinCharSensitive() {
        final String pattern = "[Char]";
        String regex = service.toRegex(pattern, true);
        assertMatches("A", regex);
        assertMatches("B", regex);
        assertNoMatches("b", regex);
        assertNoMatches("0", regex);
    }

    @Test
    public void lowerLatinWordInsensitive() {
        final String pattern = "[word]";

        String regex = service.toRegex(pattern, false);
        assertMatches("Aa", regex);
        assertMatches("vba", regex);
        assertNoMatches("A", regex);
        assertNoMatches("vba0", regex);
        assertMatches("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß", regex);
        assertMatches("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ", regex);
    }

    @Test
    public void lowerLatinWordSensitive() {
        final String pattern = "[word]";

        String regex = service.toRegex(pattern, true);
        assertNoMatches("Aa", regex);
        assertMatches("vba", regex);
        assertNoMatches("A", regex);
        assertNoMatches("vba0", regex);
        assertMatches("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß", regex);
        assertNoMatches("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ", regex);
    }

    @Test
    public void twoLatinWordsSeparatedBySpaceInsensitive() {
        final String pattern = "[word] [word]";

        String regex = service.toRegex(pattern, false);
        assertMatches("rainbow dash", regex);
        assertMatches("Big McIntosh", regex);
        assertNoMatches("too many spaces", regex);
        assertNoMatches("double  spaces", regex);
    }

    @Test
    public void twoLatinWordsSeparatedBySpaceSensitive() {
        final String pattern = "[word] [word]";

        String regex = service.toRegex(pattern, true);
        assertMatches("rainbow dash", regex);
        assertNoMatches("Big McIntosh", regex);
        assertNoMatches("too many spaces", regex);
    }

    @Test
    public void upperLatinWordInsensitive() {
        final String pattern = "[WORD]";

        String regex = service.toRegex(pattern, false);
        assertMatches("WOWOWO", regex);
        assertMatches("vba", regex);
        assertMatches("Aa", regex);
        assertNoMatches("A", regex);
        assertNoMatches("vba0", regex);
        assertMatches("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß", regex);
        assertMatches("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ", regex);
    }

    @Test
    public void upperLatinWordSensitive() {
        final String pattern = "[WORD]";

        String regex = service.toRegex(pattern, true);
        assertMatches("WOWOWO", regex);
        assertNoMatches("vba", regex);
        assertNoMatches("Aa", regex);
        assertNoMatches("A", regex);
        assertNoMatches("vba0", regex);
        assertNoMatches("abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß", regex);
        assertMatches("ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞŸ", regex);
    }

    @Test
    public void latinWordInsensitive() {
        final String pattern = "[Word]";

        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("WOWOWO", regex);
        assertMatches("vba", regex);
        assertMatches("Aa", regex);
        assertNoMatches("A", regex);
        assertNoMatches("vba0", regex);
    }


    @Test
    public void number() {
        final String pattern = "[number]";

        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("42", regex);
        assertNoMatches("0", regex);
        assertNoMatches("vba0", regex);
    }

    @Test
    public void digit() {
        final String pattern = "[digit]";
        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("9", regex);
        assertNoMatches("1337", regex);
        assertNoMatches("vba0", regex);
    }

    @Test
    public void latinAlphanumeric() {
        final String pattern = "[alnum]";

        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("aaa8", regex);
        assertNoMatches("9", regex);
        assertNoMatches("a", regex);
        assertNoMatches(".aaa", regex);
        assertNoMatches("a袁", regex);
        assertNoMatches("ac袁", regex);
    }

    @Test
    public void twoPatternSeparatedByDot() {
        final String pattern = "[number].[Word]";

        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("42.pony", regex);
        assertNoMatches("42!pony", regex);
        assertNoMatches("42Ppony", regex);
        assertNoMatches(".42.pony", regex);
        assertNoMatches("42..pony", regex);
    }

    @Test
    public void patternWithParenthesis() {
        final String pattern = "([number])";
        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("(42)", regex);
        assertMatches("(1337)", regex);
        assertNoMatches("()42", regex);
        assertNoMatches("()", regex);
        assertNoMatches("(4)", regex);
        assertNoMatches("42", regex);
    }

    @Test
    public void patternWithBrackets() {
        final String pattern = "[[number]]";

        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("[42]", regex);
        assertMatches("[1337]", regex);
        assertNoMatches("[]42", regex);
        assertNoMatches("[]", regex);
        assertNoMatches("[4]", regex);
        assertNoMatches("42", regex);
    }

    @Test
    public void patternWithQuestionMark() {
        final String pattern = "[number]?";

        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("42?", regex);
        assertMatches("1337?", regex);
        assertNoMatches("?42", regex);
        assertNoMatches("?", regex);
        assertNoMatches("4?", regex);
        assertNoMatches("42", regex);
    }

    @Test
    public void patternWithPlusAndStar() {

        final String pattern = "[number]+*";

        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("42+*", regex);
        assertMatches("1337+*", regex);
        assertNoMatches("+*42", regex);
        assertNoMatches("+*", regex);
        assertNoMatches("4+*", regex);
        assertNoMatches("42", regex);
    }

    @Test
    public void defaultText() {
        final String example = "C'est un TEXTE Test d'obSERVatIon des 8 pATTERNS possibles (sur plus de 10)";
        final String pattern = "[Char]'[word] [word] [WORD] [Word] [char]'[word][WORD][word][Word] [word] [digit] [char][WORD] [word] ([word] [word] [word] [number])";


        String insensitive = service.toRegex(pattern, false);
        assertMatches(example, insensitive);
        assertMatches(example.toLowerCase(), insensitive);
        assertMatches(example.toUpperCase(), insensitive);

        String sensitive = service.toRegex(pattern, true);
        assertMatches(example, sensitive);
        assertNoMatches(example.toLowerCase(), sensitive);
        assertNoMatches(example.toUpperCase(), sensitive);

    }

    @Test
    public void email() {
        final String example = "Example123@protonmail.com";
        final String pattern = "[alnum]@[word].[word]";

        String insensitive = service.toRegex(pattern, false);
        assertMatches(example, insensitive);
        assertMatches(example.toLowerCase(), insensitive);
        assertMatches(example.toUpperCase(), insensitive);

        String sensitive = service.toRegex(pattern, true);
        assertMatches(example, sensitive);
        assertMatches(example.toLowerCase(), sensitive);
        assertNoMatches(example.toUpperCase(), sensitive);
    }

    @Test
    public void chineseIdeogram() {
        final String pattern = "[Ideogram]";

        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("袁", regex);
        assertMatches("蘭", regex);
        assertNoMatches("9", regex);
        assertNoMatches("a", regex);
        assertNoMatches(".aaa", regex);
        assertNoMatches("a袁", regex);
        assertNoMatches("ac袁", regex);
    }

    @Test
    public void chineseAlphanumeric() {
        final String pattern = "[alnum(CJK)]";

        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("花木蘭88", regex);
        assertMatches("花木蘭88袁", regex);
        assertNoMatches("pony42", regex);
        assertNoMatches("a", regex);
        assertNoMatches(".aaa", regex);
        assertNoMatches("a袁", regex);
        assertNoMatches("ac袁", regex);
    }

    @Test
    public void chinese() {
        final String example = "袁 花木蘭88";
        final String patternNoCase = "[Ideogram] [alnum(CJK)]";

        testCaseInsensitivePattern(example, patternNoCase);

        final String patternCase = "[Ideogram] [IdeogramSeq][number]";

        testCaseInsensitivePattern(example, patternCase);
    }

    @Test
    public void chineseText() {
        final String example = "木兰辞\n" + "\n" + "唧唧复唧唧，木兰当户织。\n" + "不闻机杼声，唯闻女叹息。\n" + "问女何所思？问女何所忆？\n" + "女亦无所思，女亦无所忆。\n"
                + "昨夜见军帖，可汗大点兵，\n" + "军书十二卷，卷卷有爷名。\n" + "阿爷无大儿，木兰无长兄，\n" + "愿为市鞍马，从此替爷征。";
        final String pattern = "[IdeogramSeq]\n" + "\n[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]。\n"
                + "[IdeogramSeq]？[IdeogramSeq]？\n" + "[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]，\n"
                + "[IdeogramSeq]，[IdeogramSeq]。\n" + "[IdeogramSeq]，[IdeogramSeq]，\n" + "[IdeogramSeq]，[IdeogramSeq]。";

        testCaseInsensitivePattern(example, pattern);
    }

    @Test
    public void chineseQuestion() {
        final String example = "不亦1說乎？有";
        final String pattern = "[alnum(CJK)]？[Ideogram]";

        testCaseInsensitivePattern(example, pattern);
    }

    @Test
    public void japaneseAlphanumeric() {
        final String example = "こんにちは123";
        final String pattern = "[alnum]";

        testCaseInsensitivePattern(example, pattern);
    }

    @Test
    public void japaneseWord() {
        final String example = "こんにちは";
        final String pattern = "[word]";

        String regex = service.toRegex(pattern, false);
        assertMatches(example, regex);

        String regexCase = service.toRegex(pattern, true);
        assertMatches(example, regexCase);
    }

    @Test
    public void japaneseNoCase() {
        final String example = "こんにちは123 こんにちは？你好/Hello!";
        final String patternNoCase = "[alnum] [word]？[IdeogramSeq]/[word]!";

        String regex = service.toRegex(patternNoCase, false);
        assertMatches(example, regex);

        String regexCase = service.toRegex(patternNoCase, true);
        assertNoMatches(example, regexCase);
    }

    @Test
    public void japaneseCase() {
        final String example = "こんにちは123 こんにちは？你好/Hello!";
        final String patternCase = "[word][number] [word]？[IdeogramSeq]/[Word]!";
        assertCaseInsensitiveRegexMatche(example, patternCase);

        String regex = service.toRegex(patternCase, false);
        assertMatches(example, regex);

        String regexCase = service.toRegex(patternCase, true);
        assertMatches(example, regexCase);
    }

    @Test
    public void japaneseCJKNoCase() {
        final String example = "日本語123 日本語？你好/Hello!";
        final String patternNoCase = "[alnum(CJK)] [IdeogramSeq]？[IdeogramSeq]/[word]!";
        String regex = service.toRegex(patternNoCase, false);
        assertMatches(example, regex);

        String regexCase = service.toRegex(patternNoCase, true);
        assertNoMatches(example, regexCase);
    }

    @Test
    public void japaneseCJKCase() {
        final String example = "日本語123 日本語？你好/Hello!";
        final String patternCase = "[IdeogramSeq][number] [IdeogramSeq]？[IdeogramSeq]/[Word]!";

        String regex = service.toRegex(patternCase, false);
        assertMatches(example, regex);

        String regexCase = service.toRegex(patternCase, true);
        assertMatches(example, regexCase);
    }

    @Test
    public void surrogatePair() {
        testCaseInsensitivePattern("𠀐", "[Ideogram]");
        testCaseInsensitivePattern("𠀐𠀑我𠀒𠀓", "[IdeogramSeq]");
        testCaseInsensitivePattern("𠀐𠀑我𠀒𠀓 我Abc", "[IdeogramSeq] [Ideogram][Word]");
        testCaseInsensitivePattern("𠀐12//𠀑我?𠀑", "[alnum(CJK)]//[IdeogramSeq]?[Ideogram]");
        testCaseInsensitivePattern("𠀐12//𠀑我?𠀑", "[Ideogram][number]//[IdeogramSeq]?[Ideogram]");
    }

    @Test
    public void arabic() {
        final String example =
                "يَجِبُ عَلَى الإنْسَانِ أن يَكُونَ أمِيْنَاً وَصَادِقَاً مَعَ نَفْسِهِ وَمَعَ أَهْلِهِ وَجِيْرَانِهِ وَأَنْ يَبْذُلَ كُلَّ جُهْدٍ فِي إِعْلاءِ شَأْنِ الوَطَنِ وَأَنْ يَعْمَلَ عَلَى مَا يَجْلِبُ السَّعَادَةَ لِلنَّاسِ . ولَن يَتِمَّ لَهُ ذلِك إِلا بِأَنْ يُقَدِّمَ المَنْفَعَةَ العَامَّةَ عَلَى المَنْفَعَةِ الخَاصَّةِ وَهذَا مِثَالٌ لِلتَّضْحِيَةِ .";
        final String pattern = "[word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] . [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] [word] .";

        String regex = service.toRegex(pattern, false);
        assertMatches(example, regex);

        String regexCase = service.toRegex(pattern, true);
        assertMatches(example, regexCase);
    }

    @Test
    public void ancientGreekWordInsenitive() {
        final String pattern = "[Word]";

        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));

        assertMatches("Ἰοὺ", regex);
        assertMatches("ἰοὺ", regex);
        assertMatches("ἸΟῪ", regex);
    }

    @Test
    public void ancientGreekLowerWordInsensitive() {
        final String pattern = "[word]";

        String regex = service.toRegex(pattern, false);

        assertMatches("Ἰοὺ", regex);
        assertMatches("ἰοὺ", regex);
        assertMatches("ἸΟῪ", regex);
        assertMatches("πάντʼ", regex);
    }

    @Test
    public void ancientGreekLowerWord() {
        final String pattern = "[word]";

        String regex = service.toRegex(pattern, true);

        assertNoMatches("Ἰοὺ", regex);
        assertMatches("ἰοὺ", regex);
        assertNoMatches("ἸΟῪ", regex);
        assertMatches("πάντʼ", regex);
    }

    @Test
    public void ancientGreekUpperWordInsensitive() {
        final String pattern = "[WORD]";

        String regex = service.toRegex(pattern, false);

        assertMatches("Ἰοὺ", regex);
        assertMatches("ἰοὺ", regex);
        assertMatches("ἸΟῪ", regex);
        assertMatches("πάντʼ", regex);
    }

    @Test
    public void ancientGreekUpperWord() {
        final String pattern = "[WORD]";

        String regex = service.toRegex(pattern, true);

        assertNoMatches("Ἰοὺ", regex);
        assertNoMatches("ἰοὺ", regex);
        assertMatches("ἸΟῪ", regex);
        assertNoMatches("πάντʼ", regex);
    }

    @Test
    public void ancientGreekChar() {
        final String pattern = "[char]";

        String regex = service.toRegex(pattern, true);

        assertMatches("ʼ", regex);
        assertMatches("ἰ", regex);
        assertNoMatches("Ἰ", regex);
    }

    @Test
    public void ancientGreekCharInsensitive() {
        final String pattern = "[char]";

        String regex = service.toRegex(pattern, false);

        assertMatches("ʼ", regex);
        assertMatches("ἰ", regex);
        assertMatches("Ἰ", regex);
    }

    @Test
    public void ancientGreekUpperChar() {
        final String pattern = "[Char]";

        String regex = service.toRegex(pattern, true);

        assertMatches("ʼ", regex);
        assertNoMatches("ἰ", regex);
        assertMatches("Ἰ", regex);
        assertMatches("Ὦ", regex);
    }

    @Test
    public void ancientGreekUpperCharInsensitive() {
        final String pattern = "[Char]";

        String regex = service.toRegex(pattern, false);

        assertMatches("ʼ", regex);
        assertMatches("ἰ", regex);
        assertMatches("Ἰ", regex);
        assertMatches("Ὦ", regex);
    }

    @Test
    public void ancientGreek() {
        final String example = "Ἰοὺ ἰού· τὰ πάντʼ ἂν ἐξήκοι σαφῆ.Ὦ φῶς, τελευταῖόν σε προσϐλέψαιμι νῦν,ὅστις πέφασμαι φύς τʼ ἀφʼ ὧν οὐ χρῆν, ξὺν οἷς τʼοὐ χρῆν ὁμιλῶν, οὕς τέ μʼ οὐκ ἔδει κτανών.";
        final String pattern = "[Word] [word]· [word] [word] [word] [word] [word].[Char] [word], [word] [word] [word] [word],[word] [word] [word] [word] [word] [word] [word] [word], [word] [word] [word] [word] [word], [word] [word] [word] [word] [word] [word].";

        String regex = service.toRegex(pattern, false);

        assertMatches(example, regex);
    }

    private void assertCaseInsensitiveRegexMatche(String example, String pattern) {
        String regex = service.toRegex(pattern, false);
        assertTrue(Pattern.compile(regex).matcher(example).matches());
    }


    private void assertMatches(String example, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(example);
        assertTrue(String.format("Regex %s won't match %s", regex, example), matcher.matches());
    }

    private void assertNoMatches(String example, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(example);
        assertFalse(String.format("Regex %s match %s", regex, example), matcher.matches());
    }

    private void testCaseInsensitivePattern(String example, String pattern) {
        String regex = service.toRegex(pattern, false);
        assertEquals(regex, service.toRegex(pattern, true));
        assertMatches(example, regex);
    }
}