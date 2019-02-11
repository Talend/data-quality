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
    public void testConvertDigitHalfToFull() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull().withDigit(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_DIGIT, converter.convert(HALF_DIGIT));
    }

    @Test
    public void testConvertLetterHalfToFull() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull().withLetter(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_LETTER, converter.convert(HALF_LETTER));
    }

    @Test
    public void testConvertKatakanaHalfToFull() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull().withKatanana(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_KATAKANA, converter.convert(HALF_KATAKANA));
    }

    @Test
    public void testConvertKatakanaWithMarkHalfToFull() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull().withKatanana(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_KATAKANA_WITH_SOUND_MARK, converter.convert(HALF_KATAKANA_WITH_SOUND_MARK));
    }

    @Test
    public void testConvertSymbolHalfToFull() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull().withOtherChars(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_SYMBOL, converter.convert(HALF_SYMBOL));
    }

    @Test
    public void testConvertMixedHalfToFull() {
        ConversionConfig config = new ConversionConfig.Builder().halfToFull() //
                .withDigit(true).withLetter(true).withKatanana(true).withOtherChars(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(FULL_MIXED, converter.convert(HALF_MIXED));
    }

    @Test
    public void testConvertDigitFullToHalf() {
        ConversionConfig config = new ConversionConfig.Builder().fullTofhalf().withDigit(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_DIGIT, converter.convert(FULL_DIGIT));
    }

    @Test
    public void testConvertLetterFullToHalf() {
        ConversionConfig config = new ConversionConfig.Builder().fullTofhalf().withLetter(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_LETTER, converter.convert(FULL_LETTER));
    }

    @Test
    public void testConvertKatakanaFullToHalf() {
        ConversionConfig config = new ConversionConfig.Builder().fullTofhalf().withKatanana(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_KATAKANA, converter.convert(FULL_KATAKANA));
    }

    @Test
    public void testConvertKatakanaWithMarkFullToHalf() {
        ConversionConfig config = new ConversionConfig.Builder().fullTofhalf().withKatanana(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_KATAKANA_WITH_SOUND_MARK, converter.convert(FULL_KATAKANA_WITH_SOUND_MARK));
    }

    @Test
    public void testConvertSymbolFullToHalf() {
        ConversionConfig config = new ConversionConfig.Builder().fullTofhalf().withOtherChars(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_SYMBOL, converter.convert(FULL_SYMBOL));
    }

    @Test
    public void testConvertMixedFullToHalf() {
        ConversionConfig config = new ConversionConfig.Builder().fullTofhalf()//
                .withDigit(true).withLetter(true).withKatanana(true).withOtherChars(true).build();
        CharWidthConverter converter = new CharWidthConverter(config);
        assertEquals(HALF_MIXED, converter.convert(FULL_MIXED));
    }
}
