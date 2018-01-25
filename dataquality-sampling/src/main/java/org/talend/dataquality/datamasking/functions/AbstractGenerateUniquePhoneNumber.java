package org.talend.dataquality.datamasking.functions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.talend.dataquality.datamasking.generic.GenerateUniqueRandomPatterns;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

/**
 * Created by jteuladedenantes on 21/09/16.
 */
public abstract class AbstractGenerateUniquePhoneNumber extends Function<String> {

    private static final long serialVersionUID = -3495285699226639929L;

    protected GenerateUniqueRandomPatterns phoneNumberPattern;

    private ReplaceNumericString replaceNumeric = new ReplaceNumericString();

    public AbstractGenerateUniquePhoneNumber() {
        List<AbstractField> fields = createFieldsListFromPattern();

        phoneNumberPattern = new GenerateUniqueRandomPatterns(fields);
    }

    @Override
    public void setRandom(Random rand) {
        super.setRandom(rand);
        replaceNumeric.parse(null, false, rand);
        phoneNumberPattern.setKey(rand.nextInt(Integer.MAX_VALUE - 1000000) + 1000000);
    }

    @Override
    protected String doGenerateMaskedField(String str) {

        if (str == null) {
            return null;
        }

        String strWithoutSpaces = removeFormatInString(str);
        // check if the pattern is valid
        if (strWithoutSpaces.isEmpty() || strWithoutSpaces.length() < phoneNumberPattern.getFieldsCharsLength()) {
            if (keepInvalidPattern) {
                return str;
            } else {
                return replaceNumeric.doGenerateMaskedField(str);
            }
        }

        StringBuilder result = doValidGenerateMaskedField(strWithoutSpaces);
        if (result == null) {
            if (keepInvalidPattern) {
                return str;
            } else {
                return replaceNumeric.doGenerateMaskedField(str);
            }
        }
        if (keepFormat) {
            return insertFormatInString(str, result);
        } else {
            return result.toString();
        }
    }

    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<AbstractField>();
        long max = (long) Math.pow(10, getDigitsNumberToMask()) - 1;
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(max)));
        return fields;
    }

    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();

        strs.add(str.substring(str.length() - getDigitsNumberToMask(), str.length()));

        StringBuilder result = phoneNumberPattern.generateUniqueString(strs);
        if (result == null) {
            return null;
        }
        result.insert(0, str.substring(0, str.length() - getDigitsNumberToMask()));
        return result;
    }

    /**
     * Remove all the spaces in the input string
     *
     * @param input
     * @return
     */
    @Override
    protected String removeFormatInString(String input) {
        return nonDigits.matcher(input).replaceAll("");
    }

    @Override
    protected String insertFormatInString(String strWithFormat, StringBuilder resWithoutFormat) {
        if (strWithFormat == null || resWithoutFormat == null) {
            return strWithFormat;
        }
        for (int i = 0; i < strWithFormat.length(); i++) {
            if (!Character.isDigit(strWithFormat.charAt(i))) {
                resWithoutFormat.insert(i, strWithFormat.charAt(i));
            }
        }
        return resWithoutFormat.toString();
    }

    protected abstract int getDigitsNumberToMask();
}
