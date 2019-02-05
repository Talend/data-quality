package org.talend.dataquality.datamasking.functions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.patterns.GenerateFormatPreservingPatterns;
import org.talend.dataquality.datamasking.generic.patterns.GenerateUniqueRandomPatterns;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

/**
 * Created by jteuladedenantes on 21/09/16.
 */
public abstract class AbstractGenerateUniquePhoneNumber extends AbstractGenerateWithSecret {

    private static final long serialVersionUID = -3495285699226639929L;

    private ReplaceNumericString replaceNumeric = new ReplaceNumericString();

    public AbstractGenerateUniquePhoneNumber() {
        List<AbstractField> fields = createFieldsListFromPattern();
        pattern = new GenerateUniqueRandomPatterns(fields);
    }

    @Override
    public void setSecret(FormatPreservingMethod method, String password) {
        secretMng = new SecretManager(method, password);

        if (FormatPreservingMethod.BASIC == secretMng.getMethod()) {
            secretMng.setKey(super.rnd.nextInt(Integer.MAX_VALUE - 1000000) + 1000000);
        } else {
            pattern = new GenerateFormatPreservingPatterns(10, pattern.getFields());
        }
    }

    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<>();
        long max = (long) Math.pow(10, getDigitsNumberToMask()) - 1;
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(max)));
        return fields;
    }

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = new ArrayList<>();

        strs.add(str.substring(str.length() - getDigitsNumberToMask()));

        Optional<StringBuilder> result = pattern.generateUniqueString(strs, secretMng);

        result.ifPresent(number -> number.insert(0, str.substring(0, str.length() - getDigitsNumberToMask())));

        return result.orElse(null);
    }

    @Override
    protected boolean isValidWithoutFormat(String str) {
        return !str.isEmpty() && str.length() >= pattern.getFieldsCharsLength();
    }

    /**
     * Remove all the non-digit characters in the input string
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
