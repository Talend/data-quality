package org.talend.dataquality.datamasking.functions;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.patterns.AbstractGeneratePattern;

/**
 * This class is for Functions using a secret that needs to be handled by a {@link SecretManager}, such as a password
 * for Format-Preserving Encryption.
 *
 * @author afournier
 * @see SecretManager
 */
public abstract class AbstractGenerateWithSecret extends Function<String> {

    private static final long serialVersionUID = -4582206371693548066L;

    /**
     * The SecretManager handles keys and secret used to generate masked values.
     */
    protected SecretManager secretMng = new SecretManager();

    protected AbstractGeneratePattern pattern;

    /**
     * This method overrides {@link Function#setSecret(String, String)} method which throws an exception by default.
     *
     * A new instance of {@link SecretManager} is created with the desired method and password for the format-preserving masking operation.
     *
     * @param method a String referring to a {@link FormatPreservingMethod}.
     * @param password the password entered by the user
     */
    @Override
    public void setSecret(String method, String password) {
        secretMng = new SecretManager(method, password);
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str == null) {
            return null;
        }

        String strWithoutFormat = removeFormatInString(str);
        // check if the pattern is valid
        if (!isValidWithoutFormat(strWithoutFormat)) {
            return getResult(str);
        }

        StringBuilder result = doValidGenerateMaskedField(strWithoutFormat);
        if (result == null) {
            return getResult(str);
        }
        if (keepFormat) {
            return insertFormatInString(str, result);
        } else {
            return result.toString();
        }
    }

    /**
     * Get result by input data
     */
    protected String getResult(String str) {
        if (keepInvalidPattern) {
            return str;
        } else {
            return null;
        }
    }

    protected abstract StringBuilder doValidGenerateMaskedField(String str);

    /**
     * Verifies the validity of a string with pattern without its format.
     */
    protected abstract boolean isValidWithoutFormat(String str);

    /**
     * Verifies the validity of a string with a certain pattern.
     */
    protected boolean isValid(String str) {
        boolean isValid;

        if (StringUtils.isEmpty(str)) {
            isValid = false;
        } else {
            String strWithoutSpaces = removeFormatInString(str);
            isValid = isValidWithoutFormat(strWithoutSpaces);
        }

        return isValid;
    }
}
