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
package org.talend.dataquality.datamasking.functions.text.replace;

import org.talend.dataquality.datamasking.functions.text.BetweenIndexes;
import org.talend.dataquality.datamasking.functions.util.CharactersOperationUtils;

/**
 * created by jgonzalez on 22 juin 2015. This class is used when the requested function is BetweenIndexesKeep. It will
 * return a new String where all the elements between the indexes are replaced.
 *
 */
public class BetweenIndexesReplace extends BetweenIndexes {

    private static final long serialVersionUID = 1440323544625986870L;

    @Override
    protected void initAttributes() {
        beginIndex = Integer.parseInt(parameters[0]) - 1;
        endIndex = Integer.parseInt(parameters[1]);
        if (parameters.length == 3) {
            charToReplace = parameters[2].charAt(0);
        }

    }

    @Override
    protected boolean validParameters() {
        return CharactersOperationUtils.validParameters2Indexes1CharReplace(parameters);
    }

}
