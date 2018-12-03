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

import org.talend.dataquality.datamasking.generic.GenerateUniqueRandomPatterns;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;

/**
 * @author jteuladedenantes
 * 
 * This abstract class contains all attributes and methods similar among the SNN numbers.
 */
public abstract class AbstractGenerateUniqueSsn extends Function<String> {

    private static final long serialVersionUID = -2459692854626505777L;

    protected GenerateUniqueRandomPatterns ssnPattern;

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
        ssnPattern.setKey(rand.nextInt() % 10000 + 1000);
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str == null) {
            return null;
        }

        String strWithoutSpaces = super.removeFormatInString(str);
        // check if the pattern is valid
        if (strWithoutSpaces.isEmpty() || strWithoutSpaces.length() != ssnPattern.getFieldsCharsLength() + checkSumSize) {
            return getResult(str);
        }

        StringBuilder result = doValidGenerateMaskedField(strWithoutSpaces);
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

    protected abstract StringBuilder doValidGenerateMaskedField(String str);

}
