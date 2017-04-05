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
     * This constructor is used to initialize removeRepeatCharPattern and remove WhiteSpace chars like as " ","\t","\r","\n","\f".
     */
    public DuplicateCharEraser() {
        removeRepeatCharPattern = Pattern.compile("([\\s\\u0085\\p{Z}]|\r\n)" + "\\1+"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * 
     * This constructor is used to remove a given repeated String {@link #removeRepeatedChar(String)} .
     * initialize removeRepeatCharPattern,add the Escape "\\" for non-word character like as "{","[","(","^,"+" and so on.
     * 
     * @param repeatChar the string to be removed when it appears consecutively several times.
     */
    public DuplicateCharEraser(char repeatChar) {
        Pattern nonWordPattern = Pattern.compile("([\\W])"); //$NON-NLS-1$
        Matcher matcher = nonWordPattern.matcher(String.valueOf(repeatChar));
        if (matcher.find()) {
            removeRepeatCharPattern = Pattern.compile("([\\" + repeatChar + "])\\1+"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            removeRepeatCharPattern = Pattern.compile("([" + repeatChar + "])\\1+"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * 
     * Remove consecutive repeated characters by a specified char.
     * 
     * @param inputStr the source String
     * @return the string with the source string removed if found
     */
    public String removeRepeatedChar(String inputStr) {
        if (StringUtils.isEmpty(inputStr)) {
            return inputStr;
        }
        Matcher matcher = removeRepeatCharPattern.matcher(inputStr);
        return matcher.replaceAll("$1"); //$NON-NLS-1$
    }

}
