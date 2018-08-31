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
package org.talend.dataquality.common.regex;

import java.util.regex.Pattern;

/**
 * Regex replacement for latin characters "abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿß"
 * 
 * @author mzhao
 *
 */
public class LatinLettersSmall extends ChainResponsibilityHandler {

    private Pattern pattern = Pattern.compile("[\\u0061-\\u007a|\\u00DF-\\u00F6|\\u00F8-\\u00FF]");

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.util.ChainResponsibilityHandler#getReplaceStr()
     */
    @Override
    protected String getReplaceStr() {
        return "a";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.util.ChainResponsibilityHandler#getRegex()
     */
    @Override
    protected Pattern getRegex() {
        // [a-z] from http://www.unicode.org/charts/PDF/U0000.pdf
        // [à-ÿ] exclude ÷
        return pattern;
    }

}
