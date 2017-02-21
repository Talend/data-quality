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
package org.talend.dataquality.converters;

import org.apache.commons.lang.StringUtils;

/**
 * this class is used for Converting a String.<br/>
 * created by msjian on 2017.2.16
 * 
 */
public class StringConverter {

    private String inputStr;

    /**
     * StringConverter constructor.
     * 
     * @param inputStr
     */
    public StringConverter(String inputStr) {
        super();
        this.inputStr = inputStr;
    }

    /**
     * Remove trailing and leading characters(as default the removeCharacter is whitespace).
     * 
     * @return
     */
    public String removeTrailingAndLeadingCharacters() {
        return removeTrailingAndLeadingCharacters(" "); //$NON-NLS-1$
    }

    /**
     * Remove trailing and leading characters.
     * 
     * @param removeCharacter - the character need to remove.
     * @return String.
     */
    public String removeTrailingAndLeadingCharacters(String removeCharacter) {
        if ("".equals(removeCharacter)) { //$NON-NLS-1$
            return inputStr;
        }
        String result = inputStr;

        while (result.startsWith(removeCharacter)) {
            result = StringUtils.removeStart(result, removeCharacter);
        }
        while (result.endsWith(removeCharacter)) {
            result = StringUtils.removeEnd(result, removeCharacter);
        }
        return result;
    }

}
