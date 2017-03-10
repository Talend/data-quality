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
package org.talend.dataquality.email.checkerImpl;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.email.Constant;

/**
 * created by talend on 2014年12月26日 Detailled comment
 *
 */
public class LocalPartRegexCheckerImpl extends AbstractEmailChecker {

    private Pattern localpartPattern;

    // isShort = true, means the user selected: "use simlified pattern"
    public LocalPartRegexCheckerImpl(String regex, boolean isShort, boolean isCaseSensitive) {
        super();

        String localPart = isShort ? translateToRegex(regex) : regex;
        if (localPart != null) {
            localpartPattern = isCaseSensitive ? Pattern.compile(localPart)
                    : Pattern.compile(localPart, Pattern.CASE_INSENSITIVE);
        }
    }

    /**
     * DOC msjian Comment method "replaceLocalpart".
     * 
     * @param localPart
     * @return
     */
    String translateToRegex(String localPart) {
        // note: this is case sensitive
        // W-->[A-Z]+
        // A-->[A-Z]
        // w-->[a-z]+
        // a-->[a-z]
        // 9-->[0-9]
        if (localPart == null) {
            return null;
        }

        // consider the case where the brackets are repeated several times, like <tal>a9w<end>.
        String[] loacalPartSplits = StringUtils.split(localPart, Constant.LEFT_BRACKET);
        StringBuilder buf = new StringBuilder();
        for (String loacalPartSplit : loacalPartSplits) {
            String splitedStr = loacalPartSplit;
            int indexOf = splitedStr.indexOf(Constant.RIGHT_END);
            if (indexOf != -1) {
                String startStr = splitedStr.substring(0, indexOf);
                String endStr = splitedStr.substring(indexOf + 1);

                endStr = replaceChar(endStr);
                splitedStr = startStr + endStr;
            } else {
                splitedStr = replaceChar(splitedStr);
            }
            buf.append(splitedStr);
        }

        return buf.toString();
    }

    private String replaceChar(String originalStr) {
        if ("".equals(originalStr.trim())) { //$NON-NLS-1$
            return originalStr;
        }
        String regex = StringUtils.replace(originalStr, "A", "[A-Z]"); //$NON-NLS-1$ //$NON-NLS-2$
        regex = StringUtils.replace(regex, "a", "[a-z]"); //$NON-NLS-1$ //$NON-NLS-2$
        regex = StringUtils.replace(regex, "W", "[A-Z]+"); //$NON-NLS-1$ //$NON-NLS-2$
        regex = StringUtils.replace(regex, "w", "[a-z]+"); //$NON-NLS-1$ //$NON-NLS-2$
        regex = StringUtils.replace(regex, "9", "[0-9]"); //$NON-NLS-1$ //$NON-NLS-2$
        regex = StringUtils.replace(regex, ".", "\\."); //$NON-NLS-1$ //$NON-NLS-2$
        regex = StringUtils.replace(regex, "?", "[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]"); //$NON-NLS-1$ //$NON-NLS-2$
        regex = StringUtils.replace(regex, "*", "[^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]*"); //$NON-NLS-1$ //$NON-NLS-2$
        return regex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.IEmailChecker#check(java.lang.String)
     */
    @Override
    public boolean check(String email) {
        if (email == null || localpartPattern == null) {
            return false;
        }
        int pos = email.indexOf(Constant.AT);
        if (pos == -1) {
            return false;
        }
        String inputLocalPart = email.substring(0, pos);
        if (!localpartPattern.matcher(inputLocalPart).matches()) {
            return false;
        }
        return true;
    }

}
