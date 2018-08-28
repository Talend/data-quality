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
 * DOC talend class global comment. Detailled comment
 */
public class KatakanaSmall extends ChainResponsibilityHandler {

    private Pattern pattern = Pattern.compile(
            "[\\u31F0-\\u31FF|\\uFF67-\\uFF6F|\\u30A1|\\u30A3|\\u30A5|\\u30A7|\\u30A9|\\u30C3|\\u30E3|\\u30E5|\\u30E7|\\u30EE|\\u30F5|\\u30F6]");

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.util.ChainResponsibilityHandler#getReplaceStr()
     */
    @Override
    protected String getReplaceStr() {
        return "k";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.util.ChainResponsibilityHandler#getRegex()
     */
    @Override
    protected Pattern getRegex() {
        // 31F0-31FF is Katakana Phonetic Extensions
        // ㇰㇱㇲㇳㇴㇵㇶㇷㇸㇹㇺㇻㇼㇽㇾㇿ
        // uFF67-FF6F small ｧ ｨ ｩ ｪ ｫ ｬ ｭ ｮ ｯ
        // other is ァ ィ ゥ ェ ォッャュョヮヵヶ
        return pattern;
    }

}
