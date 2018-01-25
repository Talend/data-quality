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
package org.talend.dataquality.datamasking.semantic;

import org.talend.dataquality.datamasking.functions.Function;

/**
 * The super class of function which used by data masking
 */
public class DataMaskFunction extends Function<String> {

    private static final long serialVersionUID = 3677558475093292942L;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#generateInvalidMaskData(java.lang.Object)
     */
    @Override
    protected String generateInvalidMaskData(String inputValue) {
        ReplaceCharactersWithGeneration replaceCharactersWithGeneration = new ReplaceCharactersWithGeneration();
        replaceCharactersWithGeneration.setRandom(this.getRandom());
        return replaceCharactersWithGeneration.doGenerateMaskedField(inputValue);
    }

}
