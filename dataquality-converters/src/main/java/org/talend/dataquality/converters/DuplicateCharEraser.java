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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * This class is used to remove consecutive duplicate characters.<br/>
 * created by qiongli on 2017.3.30
 * 
 */
public class DuplicateCharEraser {

    private Pattern removeRepeatCharPattern = null;

    /**
     *
     * This constructor is used to remove WhiteSpace chars like as " ","\t","\r","\n","\f".
     */
    public DuplicateCharEraser() {
        removeRepeatCharPattern = Pattern.compile("(" + "([\\s\\u0085\\p{Z}]|\r\n)" + ")\\1+"); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * 
     * This constructor is used to remove a specify repeated String {@link #removeRepeatedChar(String)} .
     * 
     * @param repeatStr
     * @throws IllegalArgumentException
     */
    public DuplicateCharEraser(char repeatStr) throws IllegalArgumentException {
        removeRepeatCharPattern = Pattern.compile("([" + repeatStr + "])\\1+"); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * 
     * Remove consecutive repeated characters by a specified char.
     * 
     * @param inputStr the source String
     * @return the string with the source string removed if found
     */
    public String removeRepeatedChar(String inputStr) {
        if (StringUtils.isEmpty(inputStr) || removeRepeatCharPattern == null) {
            return inputStr;
        }
        Matcher matcher = removeRepeatCharPattern.matcher(inputStr);
        return matcher.replaceAll("$1"); //$NON-NLS-1$
    }

}
