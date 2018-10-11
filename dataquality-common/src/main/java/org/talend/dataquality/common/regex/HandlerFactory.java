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

/**
 * DOC talend class global comment. Detailled comment
 */
public class HandlerFactory {

    public static ChainResponsibilityHandler createEastAsiaPatternHandler() {
        ChainResponsibilityHandler handler = new Hiragana();
        handler.linkSuccessor(new KatakanaSmall()).linkSuccessor(new Katakana()).linkSuccessor(new FullwidthLatinNumbers())
                .linkSuccessor(new FullwidthLatinLowercasedLetters()).linkSuccessor(new FullwidthLatinUppercasedLetters())
                .linkSuccessor(new Hangul()).linkSuccessor(new Kanji()).linkSuccessor(new KanjiRare());
        return handler;

    }

    public static ChainResponsibilityHandler createLatinPatternHandler() {
        ChainResponsibilityHandler handler = new LatinLetters();
        handler.linkSuccessor(new LatinLettersSmall()).linkSuccessor(new LatinAsciiDigits());
        return handler;
    }
}
