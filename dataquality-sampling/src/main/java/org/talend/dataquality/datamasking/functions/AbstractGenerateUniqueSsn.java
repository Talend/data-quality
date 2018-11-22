// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.functions;

import java.util.List;
import java.util.Random;

import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.patterns.GenerateFormatPreservingPatterns;
import org.talend.dataquality.datamasking.generic.patterns.GenerateUniqueRandomPatterns;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.patterns.AbstractGeneratePattern;
import org.talend.dataquality.datamasking.utils.crypto.BasicSpec;

/**
 * @author jteuladedenantes
 * 
 * This abstract class contains all attributes and methods similar among the SNN numbers.
 */
public abstract class AbstractGenerateUniqueSsn extends Function<String> {

    private static final long serialVersionUID = -2459692854626505777L;

    protected AbstractGeneratePattern ssnPattern;

    /**
     * Used in some countries to check the SSN number. The initialization can be done in createFieldsListFromPattern
     * method if necessary.
     */
    protected int checkSumSize = 0;

    public AbstractGenerateUniqueSsn() {
        List<AbstractField> fields = createFieldsListFromPattern();
        ssnPattern = new GenerateUniqueRandomPatterns(fields);
    }

    @Override
    public void setRandom(Random rand) {
        super.setRandom(rand);
    }

    @Override
    public void setSecretManager(SecretManager secMng) {
        this.secretMng = secMng;
        if (secMng.getMethod() == FormatPreservingMethod.BASIC) {
            secretMng.setKey(super.rnd.nextInt() % BasicSpec.BASIC_KEY_BOUND + BasicSpec.BASIC_KEY_OFFSET);
        } else {
            ssnPattern = new GenerateFormatPreservingPatterns(2, ssnPattern.getFields());
        }
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str == null) {
            return null;
        }

        String strWithoutFormat = super.removeFormatInString(str);
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
     * 
     * @param str
     * @return
     */
    private String getResult(String str) {
        if (keepInvalidPattern) {
            return str;
        } else {
            return null;
        }
    }

    /**
     * @return the list of patterns for each field
     */
    protected abstract List<AbstractField> createFieldsListFromPattern();

    /**
     * Split the string value into the corresponding list of fields.
     * @param str the string value without format
     * @return the list of string fields
     */
    protected abstract List<String> splitFields(String str);

    protected abstract StringBuilder doValidGenerateMaskedField(String str);

    private boolean isValidWithoutFormat(String str) {
        boolean isValid;

        if (str.isEmpty() || str.length() != ssnPattern.getFieldsCharsLength() + checkSumSize) {
            isValid = false;
        } else {
            isValid = ssnPattern.encodeFields(splitFields(str)) != null;
        }

        return isValid;
    }

    /**
     * Verifies the validity of an ssn string.
     * @return true if valid, false otherwise.
     */
    protected boolean isValid(String str) {
        boolean isValid;

        if (str == null) {
            isValid = false;
        } else {
            String strWithoutSpaces = super.removeFormatInString(str);
            isValid = isValidWithoutFormat(strWithoutSpaces);
        }

        return isValid;
    }

}
