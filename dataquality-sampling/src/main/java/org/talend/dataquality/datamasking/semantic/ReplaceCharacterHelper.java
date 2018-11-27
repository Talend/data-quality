package org.talend.dataquality.datamasking.semantic;

import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.common.pattern.TextPatternUtil;

public class ReplaceCharacterHelper {

    private ReplaceCharacterHelper() {
        // no need to implement
    }

    public static String replaceCharacters(String input, Random rnd) {
        if (StringUtils.isEmpty(input)) {
            return input;
        } else {
            StringBuilder sb = new StringBuilder();
            boolean skipNextLoop = false;
            for (int i = 0; i < input.length(); i++) {
                if (skipNextLoop) {
                    skipNextLoop = false;
                    continue;
                }
                Integer codePoint = input.codePointAt(i);
                sb.append(Character.toChars(TextPatternUtil.replaceCharacter(codePoint, rnd)));
                if (Character.isHighSurrogate(input.charAt(i))) {
                    skipNextLoop = true;
                }
            }
            return sb.toString();
        }
    }

}
