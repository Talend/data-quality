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

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * DOC qzhao class global comment. Detailled comment<br>
 * 
 * This MaskEmailDomain class extends {@link Function} class. It offers the methods to verify the validation of a given
 * email address and other auxiliary methods.<br>
 * 
 */
public abstract class MaskEmail extends Function<String> {

    private static final long serialVersionUID = 3837984827035744721L;

    protected static final Pattern EMAIL_REGEX = Pattern.compile(
            "^(([^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\\\"[^\\\"]*\\\"))(\\.(([^\\s\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]]|')+|(\\\"[^\\\"]*\\\")))*@([\\p{L}\\p{M}0-9]([\\p{L}\\p{M}0-9\\-]{0,61}[\\p{L}\\p{M}0-9])?\\.)+[\\p{L}\\p{M}]{2,6}$");

    /**
     * DOC qzhao Comment method "isValidEmailAddress".<br>
     * Verifies whether it is a valid email address
     * 
     * @param email email address
     * @return true when the input is valid
     */
    protected boolean isValidEmailAddress(String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }

    /**
     * Conditions in masking full email domain randomly:<br>
     * <ul>
     * <li>When user gives a space, masks the full domain with X</li>
     * <li>When user gives a list of parameters, chooses from the list randomly</li>
     * <li>When user gives a list of parameters with one or more space in the list, removes the spaces directly</li>
     * <li>when user gives a local file, gets the choices from the file</li>
     * </ul>
     */
    @Override
    protected String doGenerateMaskedField(String str) {
        if (StringUtils.isEmpty(str)) {
            return EMPTY_STRING;
        }
        if (!isValidEmailAddress(str)) {
            return maskInvalidEmail(str);
        }
        return maskEmail(str);
    }

    protected abstract String maskInvalidEmail(String address);

    protected abstract String maskEmail(String address);
}
