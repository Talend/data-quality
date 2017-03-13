package org.talend.dataquality.datamasking.semantic;

import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.datamasking.functions.Function;

public class ReplaceCharacterHelper {

    static String replaceCharacters(String input, Random rnd) {
        if (StringUtils.isEmpty(input)) {
            return input;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                char ch = input.charAt(i);
                if (Character.isUpperCase(ch)) {
                    sb.append(Function.UPPER.charAt(rnd.nextInt(26)));
                } else if (Character.isLowerCase(ch)) {
                    sb.append(Function.LOWER.charAt(rnd.nextInt(26)));
                } else if (Character.isDigit(ch)) {
                    sb.append(rnd.nextInt(10));
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
    }

}
