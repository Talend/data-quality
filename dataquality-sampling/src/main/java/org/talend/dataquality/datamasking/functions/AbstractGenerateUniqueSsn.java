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
import java.util.Optional;

import org.talend.dataquality.datamasking.generic.patterns.GenerateUniqueRandomPatterns;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;

/**
 * @author jteuladedenantes
 * 
 * This abstract class contains all attributes and methods similar among the SNN numbers.
 */
public abstract class AbstractGenerateUniqueSsn extends AbstractGenerateWithSecret {

    private static final long serialVersionUID = -2459692854626505777L;

    /**
     * Used in some countries to check the SSN number. The initialization can be done in createFieldsListFromPattern
     * method if necessary.
     */
    protected int checkSumSize = 0;

    public AbstractGenerateUniqueSsn() {
        List<AbstractField> fields = createFieldsListFromPattern();
        pattern = new GenerateUniqueRandomPatterns(fields);
    }

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = splitFields(str);

        Optional<StringBuilder> result = pattern.generateUniqueString(strs, secretMng);

        return result.isPresent() ? result.get().append(computeKey(result.get())) : null;
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

    /**
     * A control key is not necessarily put at the end of SSNs like in the US, the UK, Japan or Germany.
     * This method must be overridden if a SSN has a control key like for France, China and India.
     */
    protected String computeKey(StringBuilder str) {
        return "";
    }

    @Override
    protected boolean isValidWithoutFormat(String str) {
        boolean isValid;

        if (str.isEmpty() || str.length() != pattern.getFieldsCharsLength() + checkSumSize) {
            isValid = false;
        } else {
            isValid = pattern.encodeFields(splitFields(str)) != null;
        }

        return isValid;
    }

}
