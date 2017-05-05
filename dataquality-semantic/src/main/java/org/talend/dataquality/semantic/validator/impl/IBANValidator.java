package org.talend.dataquality.semantic.validator.impl;

import org.apache.commons.lang3.StringUtils;
import org.talend.dataquality.semantic.validator.ISemanticValidator;

/**
 * Created by jteuladedenantes on 09/09/16.
 */
public class IBANValidator implements ISemanticValidator {

    @Override
    public boolean isValid(String str) {
        String strWithoutSpaces = StringUtils.replace(str, " ", "");
        return org.apache.commons.validator.routines.IBANValidator.DEFAULT_IBAN_VALIDATOR.isValid(strWithoutSpaces);
    }

    @Override
    public boolean isValid(String str, boolean caseSensitive) {
        return isValid(str);
    }
}
