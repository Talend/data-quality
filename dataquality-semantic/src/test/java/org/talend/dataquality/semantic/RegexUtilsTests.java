package org.talend.dataquality.semantic;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.talend.dataquality.semantic.utils.RegexUtils;

public class RegexUtilsTests {

    private Map<String, String> EXPECTED_REMOVAL_MAP = new LinkedHashMap<String, String>() {

        private static final long serialVersionUID = 1L;

        {
            put("^abc$", "abc"); // basic removal
            put("^abc\\$", "abc\\$"); // literal dollar sign must be kept
            put("^abc\\$$", "abc\\$"); // literal dollar sign must be kept
            put("^abc\\\\$", "abc\\\\"); // literal backslash should not impact the real ending dollar
            put("^abc\\\\\\$", "abc\\\\\\$"); // what about a literal backslash followed by a literal dollar sign
            put("^^^^^^^^^^^^aaaaa^^^^aaa$$$$aaa$$$aaa$$$$$$$$$$$$$", "aaaaa^^^^aaa$$$$aaa$$$aaa"); // multiple anchors
        }
    };

    /**
     * Test method for {@link org.talend.dataquality.semantic.utils.RegexUtils#removeStartingAndEndingAnchors(String)}.
     */
    @Test
    public void testRemoveStartingAndEndingAnchors() {
        for (String beforeRemoval : EXPECTED_REMOVAL_MAP.keySet()) {
            String expectedResult = EXPECTED_REMOVAL_MAP.get(beforeRemoval);
            String actualResult = RegexUtils.removeStartingAndEndingAnchors(beforeRemoval);
            assertEquals("Unexpected result for pattern: " + beforeRemoval, expectedResult, actualResult);
        }
    }
}
