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

import org.talend.dataquality.datamasking.generic.Alphabet;

/**
 * created by jgonzalez on 22 juin 2015. This function will replace every letter by the parameter.
 *
 */
public class ReplaceCharacters extends ReplaceAll {

    private static final long serialVersionUID = 368348491822287354L;

    @Override
    public void setFF1Cipher(String alphabetName, String method, String password) {
        super.setFF1Cipher(Alphabet.LATIN_LETTERS.name(), method, password);
    }

    @Override
    protected boolean isGoodType(Integer codePoint) {
        return Character.isLetter(codePoint);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.CharactersOperation#isNeedCheck()
     */
    @Override
    protected boolean isNeedCheckSpecialCase() {
        return true;
    }

}
