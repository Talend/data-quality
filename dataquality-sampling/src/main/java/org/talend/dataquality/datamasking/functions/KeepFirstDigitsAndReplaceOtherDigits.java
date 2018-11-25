package org.talend.dataquality.datamasking.functions;

import java.util.List;
import java.util.Random;

import org.talend.dataquality.common.pattern.TextPatternUtil;

public class KeepFirstDigitsAndReplaceOtherDigits extends Function<String> {

    private int integerParam = 0;

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        if (CharactersOperationUtils.validParameters1Number(parameters))
            integerParam = Integer.parseInt(parameters[0]);
        else
            throw new IllegalArgumentException("The parameter is not a positive integer");

    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (integerParam < 0)
            return EMPTY_STRING;

        if (str == null || integerParam >= str.trim().length())
            return str;

        int totalDigit = 0;
        StringBuilder sb = new StringBuilder(str.trim());
        for (int i = 0; i < sb.length(); i++) {
            if (Character.isDigit(sb.charAt(i))) {
                if (integerParam > totalDigit)
                    totalDigit++;
                else
                    sb.setCharAt(i, Character.forDigit(nextRandomDigit(), 10));
            }
        }

        return sb.toString();
    }

    @Override
    protected String doGenerateMaskedFieldConsistent(String str) {
        if (integerParam < 0)
            return EMPTY_STRING;

        if (str == null || integerParam >= str.trim().length())
            return str;

        int totalDigit = 0;
        StringBuilder sb = new StringBuilder(str.trim());
        String toBeReplaced = findDigits(sb);
        Random random = getRandomForString(toBeReplaced);
        List<Integer> replacedCodePoints = TextPatternUtil.replaceStringCodePoints(toBeReplaced, random);
        int counter = 0;
        for (int i = 0; i < sb.length(); i++) {
            if (Character.isDigit(sb.charAt(i))) {
                if (integerParam > totalDigit)
                    totalDigit++;
                else {
                    sb.setCharAt(i, Character.toChars(replacedCodePoints.get(counter))[0]);
                    counter++;
                }
            }
        }
        return sb.toString();
    }

    private String findDigits(StringBuilder sb) {
        StringBuilder stringBuilder = new StringBuilder();
        int totalDigit = 0;
        for (int i = 0; i < sb.length(); i++) {
            if (Character.isDigit(sb.charAt(i))) {
                if (integerParam > totalDigit)
                    totalDigit++;
                else
                    stringBuilder.append(sb.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

}
