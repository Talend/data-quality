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

import org.talend.daikon.pattern.character.CharPattern;

/**
 * DOC talend class global comment. Detailled comment
 */
public class HiraganaSmall extends ChainResponsibilityHandler {

    private Pattern pattern = Pattern.compile(CharPattern.LOWER_HIRAGANA.getPattern().getRegex());

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.util.ChainResponsibilityHandler#getReplaceStr()
     */
    @Override
    protected String getReplaceStr() {
        return "h";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.util.ChainResponsibilityHandler#getRegex()
     */
    @Override
    protected Pattern getRegex() {
        // ........ぁ,...... ぃ ,.... ぅ ,.....ぇ,..... ぉ,.... っ,.... ゃ ,..... ゅ,.... ょ,.... ゎ ,..... ゕ ,..... ゖ
        return pattern;
    }

}
