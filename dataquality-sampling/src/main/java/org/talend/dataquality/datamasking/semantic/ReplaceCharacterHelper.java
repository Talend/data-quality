package org.talend.dataquality.datamasking.semantic;

import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.common.pattern.TextPatternUtil;

public class ReplaceCharacterHelper {

    public static String replaceCharacters(String input, Random rnd) {
        if (StringUtils.isEmpty(input)) {
            return input;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                Integer codePoint = input.codePointAt(i);
                sb.append(Character.toChars(TextPatternUtil.replaceCharacter(codePoint, rnd)));
                if (Character.isHighSurrogate(input.charAt(i)))
                    i++;
            }
            return sb.toString();
        }
    }

}
