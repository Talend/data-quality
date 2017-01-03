package org.talend.dataquality.datamasking.functions;

import java.util.Random;

public class KeepLastDigitsAndReplaceOtherDigits extends Function<String> {

    private int integerParam = 0;

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        if (CharactersOperationUtils.validParameters1Number(parameters))
            integerParam = Integer.parseInt(parameters[0]);
        else
            throw new IllegalArgumentException("The parameter is not a positive integer.");
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (integerParam < 0)
            return EMPTY_STRING;

        if (str == null || integerParam >= str.trim().length())
            return str;

        int totalDigit = 0;
        StringBuilder sb = new StringBuilder(str.trim());
        for (int i = sb.length() - 1; i >= 0; i--) {
            if (Character.isDigit(sb.charAt(i))) {
                if (integerParam > totalDigit)
                    totalDigit++;
                else
                    sb.setCharAt(i, Character.forDigit(rnd.nextInt(9), 10));
            }
        }
        return sb.toString();
    }

}
