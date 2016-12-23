package org.talend.dataquality.datamasking.functions;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public final class CharactersOperationUtils {

    protected static final Pattern patternNumber = Pattern.compile("[0-9]+");

    protected static final Pattern patternCharacter = Pattern.compile(".");

    protected static final Pattern patternDigit = Pattern.compile("[0-9]");

    private CharactersOperationUtils() {
    }

    private static final boolean factorise(String[] parameters, int length, Pattern pattern) {
        return parameters.length == length && pattern.matcher(parameters[length - 1]).matches();
    }

    private static final boolean factorise2Indexes(String[] parameters) {
        return patternNumber.matcher(parameters[0]).matches() && patternNumber.matcher(parameters[1]).matches();
    }

    public static final boolean validParameters2Indexes(String[] parameters) {
        return parameters != null && parameters.length == 2 && factorise2Indexes(parameters);
    }

    public static final boolean validParameters2Indexes1CharReplace(String[] parameters) {
        return parameters != null && (parameters.length == 2 || factorise(parameters, 3, patternCharacter))
                && factorise2Indexes(parameters);
    }

    public static final boolean validParameters1Number1DigitReplace(String[] parameters) {
        return parameters != null && (parameters.length == 1 || factorise(parameters, 2, patternDigit))
                && patternNumber.matcher(parameters[0]).matches();
    }

    public static final boolean validParameters1Number1CharReplace(String[] parameters) {
        return parameters != null && (parameters.length == 1 || factorise(parameters, 2, patternCharacter))
                && patternNumber.matcher(parameters[0]).matches();
    }

    public static final boolean validParameters1Number(String[] parameters) {
        return parameters != null && factorise(parameters, 1, patternNumber);
    }

    public static final boolean validParameters1DigitReplace(String[] parameters) {
        return parameters == null || (parameters.length == 1
                && (StringUtils.isEmpty(parameters[0]) || patternDigit.matcher(parameters[0]).matches()));
    }

    public static final boolean validParameters1CharReplace(String[] parameters) {
        return parameters == null || (parameters.length == 1
                && (StringUtils.isEmpty(parameters[0]) || patternCharacter.matcher(parameters[0]).matches()));
    }

}
