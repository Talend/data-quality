package org.talend.dataquality.common.pattern;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextPatternUtilTest {

    @Test
    public void testFindPattern() {

        checkPattern("abc-d", "aaa-a");
        checkPattern("Straße", "Aaaaaa");
        checkPattern("トンキン", "KKKK");
        checkPattern("とうきょう", "HHHhH");
        checkPattern("서울", "GG");
        checkPattern("北京", "CC");
    }

    private void checkPattern(String input, String expectedOutput) {
        assertEquals(expectedOutput, TextPatternUtil.findPattern(input));
    }

}
