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

import org.talend.dataquality.datamasking.generic.GenerateUniqueRandomPatterns;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.sampling.exception.DQException;

import java.util.List;
import java.util.Random;

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

    public AbstractGenerateUniqueSsn() throws DQException {
        List<AbstractField> fields = createFieldsListFromPattern();
        ssnPattern = new GenerateUniqueRandomPatterns(fields);
    }

    @Override
    public void setRandom(Random rand) {
        super.setRandom(rand);
        ssnPattern.setKey(rand.nextInt() % 10000 + 1000);
    }

    @Override
    protected String doGenerateMaskedField(String str) throws DQException {
        if (str == null)
            return null;

        String strWithoutSpaces = super.removeFormatInString(str);
        // check if the pattern is valid
        if (strWithoutSpaces.isEmpty() || strWithoutSpaces.length() != ssnPattern.getFieldsCharsLength() + checkSumSize) {
            if (keepInvalidPattern)
                return str;
            else
                return null;
        }

        StringBuilder result = doValidGenerateMaskedField(strWithoutSpaces);
        if (result == null) {
            if (keepInvalidPattern)
                return str;
            else
                return null;
        }
        if (keepFormat)
            return insertFormatInString(str, result);
        else
            return result.toString();
    }

    /**
     * @return the list of patterns for each field
     */
    protected abstract List<AbstractField> createFieldsListFromPattern() throws DQException;

    protected abstract StringBuilder doValidGenerateMaskedField(String str) throws DQException;

}
