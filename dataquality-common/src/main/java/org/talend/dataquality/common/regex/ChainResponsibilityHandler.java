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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOC talend class global comment. Detailled comment
 */
public abstract class ChainResponsibilityHandler {

    /**
     * Next one successor
     */
    protected ChainResponsibilityHandler successor;

    protected boolean includeHiragana = false;

    protected boolean includeKatakana = false;

    /**
     * 
     * Handle the request
     */
    public String handleRequest(String value) {
        String tempValue = value;
        if (this.canHandler(value)) {
            Pattern pattern = getRegex();
            Matcher matcher = pattern.matcher(value);
            tempValue = matcher.replaceAll(getReplaceStr());
        }
        if (this.getSuccessor() == null) {
            if (includeKatakana)
                tempValue = tempValue.replaceAll("Kー", "KK");
            if (includeHiragana)
                tempValue = tempValue.replaceAll("Hー", "HH");
            return tempValue;
        }
        return this.getSuccessor().handleRequest(tempValue);
    }

    /**
     * DOC talend Comment method "getReplaceStr".
     */
    protected abstract String getReplaceStr();

    /**
     * DOC talend Comment method "getRegex".
     */
    protected abstract Pattern getRegex();

    /**
     * 
     * Judge whether current handler should be execute
     * 
     * @return
     */
    protected boolean canHandler(String value) {
        return getRegex() != null && getReplaceStr() != null && value != null;
    }

    /**
     * Getter for successor.
     * 
     * @return the successor
     */
    public ChainResponsibilityHandler getSuccessor() {
        return this.successor;
    }

    /**
     * Sets the successor.
     * 
     * @param successor the successor to set
     */
    public ChainResponsibilityHandler linkSuccessor(ChainResponsibilityHandler successor) {
        this.successor = successor;
        return successor;
    }

}
