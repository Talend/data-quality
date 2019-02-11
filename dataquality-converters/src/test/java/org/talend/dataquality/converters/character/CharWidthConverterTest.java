package org.talend.dataquality.converters.character;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class CharWidthConverterTest {

    private static final String HALF_DIGIT = "09";

    private static final String FULL_DIGIT = "０９";

    private static final String HALF_LETTER = "azAZ";

    private static final String FULL_LETTER = "ａｚＡＺ";

    private static final String HALF_KATAKANA = "ｦﾝ";

    private static final String FULL_KATAKANA = "ヲン";

    private static final String HALF_KATAKANA_WITH_SOUND_MARK = "ｶﾞﾊﾟ";

    private static final String FULL_KATAKANA_WITH_SOUND_MARK = "ガパ";

    private static final String HALF_SYMBOL = " !(-_^)~";

    private static final String FULL_SYMBOL = "　！（－＿＾）～";

    private static final String HALF_MIXED = "ﾃｽﾃｨｰﾝｸﾞ1-2-3 abc ABC";

    private static final String FULL_MIXED = "テスティーング１－２－３　ａｂｃ　ＡＢＣ";

    private static final Map<String, String> EXPECTED_NFKC = new HashMap<>();

    static {
        EXPECTED_NFKC.put("ﾂｲｯﾀｰ", "ツイッター");
        EXPECTED_NFKC.put("ｼｭﾜﾙﾂｪﾈｯｶﾞｰ", "シュワルツェネッガー");
        EXPECTED_NFKC.put("ﾊﾟﾊﾟ", "パパ");
        EXPECTED_NFKC.put("ﾋﾟｬﾆｯﾁ", "ピャニッチ");
        EXPECTED_NFKC.put("ﾌｨｼﾞｶﾙ", "フィジカル");
        EXPECTED_NFKC.put("かな変換ﾃｽﾃｨｰﾝｸﾞ｡ １－２－３ ａｂｃ ＡＢＣ", "かな変換テスティーング。 1-2-3 abc ABC");
        EXPECTED_NFKC.put("ｼﾞ", "ジ");
        EXPECTED_NFKC.put("i⁹", "i9");
        EXPECTED_NFKC.put("i₉", "i9");
        EXPECTED_NFKC.put("schön", "schön");
    }

    @Test
    public void testConvertNFKC() {
        ConversionConfig config = new ConversionConfig.Builder().nfkc().build();
        CharWidthConverter converter = new CharWidthConverter(config);
        for (String k : EXPECTED_NFKC.keySet()) {
            assertEquals(EXPECTED_NFKC.get(k), converter.convert(k));
        }
    }

    @Test
    public void testConvertHalfToFullDigit() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull() //
                .withDigit(true).withLetter(false).withKatanana(false).withOtherChars(false).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_DIGIT, converter.convert(HALF_DIGIT));
    }

    @Test
    public void testConvertHalfToFullLetter() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull() //
                .withDigit(false).withLetter(true).withKatanana(false).withOtherChars(false).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_LETTER, converter.convert(HALF_LETTER));
    }

    @Test
    public void testConvertHalfToFullKatakana() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull() //
                .withDigit(false).withLetter(false).withKatanana(true).withOtherChars(false).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_KATAKANA, converter.convert(HALF_KATAKANA));
    }

    @Test
    public void testConvertHalfToFullKatakanaWithSoundMark() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull() //
                .withDigit(false).withLetter(false).withKatanana(true).withOtherChars(false).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_KATAKANA_WITH_SOUND_MARK, converter.convert(HALF_KATAKANA_WITH_SOUND_MARK));
    }

    @Test
    public void testConvertHalfToFullSymbol() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull() //
                .withDigit(false).withLetter(false).withKatanana(false).withOtherChars(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_DIGIT + HALF_LETTER + FULL_SYMBOL, converter.convert(HALF_DIGIT + HALF_LETTER + HALF_SYMBOL));
    }

    @Test
    public void testConvertHalfToFullAllMixed() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull() //
                .withDigit(true).withLetter(true).withKatanana(true).withOtherChars(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_MIXED, converter.convert(HALF_MIXED));
    }

    @Test
    public void testConvertFullToHalfDigit() {
        ConversionConfig config = new ConversionConfig.Builder().fullToHalf() //
                .withDigit(true).withLetter(false).withKatanana(false).withOtherChars(false).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_DIGIT, converter.convert(FULL_DIGIT));
    }

    @Test
    public void testConvertFullToHalfLetter() {
        ConversionConfig config = new ConversionConfig.Builder().fullToHalf() //
                .withDigit(false).withLetter(true).withKatanana(false).withOtherChars(false).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_LETTER, converter.convert(FULL_LETTER));
    }

    @Test
    public void testConvertFullToHalfKatakana() {
        ConversionConfig config = new ConversionConfig.Builder().fullToHalf() //
                .withDigit(false).withLetter(false).withKatanana(true).withOtherChars(false).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_KATAKANA, converter.convert(FULL_KATAKANA));
    }

    @Test
    public void testConvertFullToHalfKatakanaWithSoundMark() {
        ConversionConfig config = new ConversionConfig.Builder().fullToHalf() //
                .withDigit(false).withLetter(false).withKatanana(true).withOtherChars(false).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_KATAKANA_WITH_SOUND_MARK, converter.convert(FULL_KATAKANA_WITH_SOUND_MARK));
    }

    @Test
    public void testConvertFullToHalfSymbol() {
        ConversionConfig config = new ConversionConfig.Builder().fullToHalf() //
                .withDigit(false).withLetter(false).withKatanana(false).withOtherChars(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_DIGIT + " " + FULL_LETTER + " " + HALF_SYMBOL,
                converter.convert(FULL_DIGIT + " " + FULL_LETTER + " " + FULL_SYMBOL));
    }

    @Test
    public void testConvertFullToHalfAllMixed() {
        ConversionConfig config = new ConversionConfig.Builder().fullToHalf() //
                .withDigit(true).withLetter(true).withKatanana(true).withOtherChars(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_MIXED, converter.convert(FULL_MIXED));
    }
}
