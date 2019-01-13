package org.talend.dataquality.datamasking.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.common.pattern.TextPatternUtil;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.generic.Alphabet;
import org.talend.dataquality.datamasking.generic.GenerateFromAlphabet;

public class KeepLastDigitsAndReplaceOtherDigits extends Function<String> {

    private static final long serialVersionUID = 948008740278615816L;

    private static final Logger LOGGER = LoggerFactory.getLogger(KeepLastDigitsAndReplaceOtherDigits.class);

    private int integerParam = 0;

    private GenerateFromAlphabet ff1Cipher;

    private transient Alphabet alphabet = Alphabet.DIGITS;

    @Override
    public void setSecret(FormatPreservingMethod method, String password) {
        ff1Cipher = new GenerateFromAlphabet(alphabet, method, password);
    }

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
        return doGenerateMaskedField(str, FunctionMode.RANDOM);
    }

    @Override
    protected String doGenerateMaskedField(String str, FunctionMode mode) {
        if (integerParam < 0)
            return EMPTY_STRING;

        if (str == null || integerParam >= str.trim().length())
            return str;

        StringBuilder sb = new StringBuilder(str.trim());

        switch (mode) {
        case CONSISTENT:
            generateConsistentDigits(sb);
            break;
        case BIJECTIVE:
            generateBijectiveDigits(sb);
            break;
        default:
            generateRandomDigits(sb);
            break;
        }

        return sb.toString();
    }

    private void generateRandomDigits(StringBuilder sb) {
        int totalDigit = 0;
        for (int i = sb.length() - 1; i >= 0; i--) {
            if (Character.isDigit(sb.charAt(i))) {
                if (integerParam > totalDigit)
                    totalDigit++;
                else
                    sb.setCharAt(i, Character.forDigit(nextRandomDigit(), 10));
            }
        }
    }

    private void generateConsistentDigits(StringBuilder sb) {
        int totalDigit = 0;
        String toBeReplaced = findDigits(sb);
        Random random = getRandomForString(toBeReplaced);
        List<Integer> replacedCodePoints = TextPatternUtil.replaceStringCodePoints(toBeReplaced, random);
        int counter = 0;
        for (int i = sb.length() - 1; i >= 0; i--) {
            if (Character.isDigit(sb.charAt(i))) {
                if (integerParam > totalDigit)
                    totalDigit++;
                else {
                    sb.setCharAt(i, Character.toChars(replacedCodePoints.get(counter))[0]);
                    counter++;
                }
            }
        }
    }

    private void generateBijectiveDigits(StringBuilder sb) {
        int totalDigit = 0;
        List<Integer> digitsToReplace = new ArrayList<>();
        List<Integer> indexesToReplace = new ArrayList<>();
        for (int i = sb.length() - 1; i >= 0; i--) {
            if (Character.isDigit(sb.charAt(i))) {
                if (integerParam > totalDigit)
                    totalDigit++;
                else {
                    indexesToReplace.add(i);
                    digitsToReplace.add(Character.digit(sb.charAt(i), 10));
                }
            }
        }
        List<Integer> replacedDigits = ff1Cipher.generateUniqueDigits(digitsToReplace);

        if (replacedDigits.isEmpty()) {
            LOGGER.warn("The element {} has too few digits to be masked bijectively. It will be masked consistently.", sb);
            generateConsistentDigits(sb);
        }

        Iterator<Integer> it = replacedDigits.iterator();
        for (int index : indexesToReplace) {
            sb.replace(index, index + 1, it.next().toString());
        }
    }

    private String findDigits(StringBuilder sb) {
        StringBuilder stringBuilder = new StringBuilder();
        int totalDigit = 0;
        for (int i = sb.length() - 1; i >= 0; i--) {
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
