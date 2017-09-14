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

/**
 * created by jgonzalez on 16 juil. 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberUK extends Function<String> {

    private static final long serialVersionUID = -4131614123980116791L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder("020 3"); //$NON-NLS-1$
        for (int i = 0; i < 3; ++i) {
            result.append(rnd.nextInt(9));
        }
        result.append(" "); //$NON-NLS-1$
        for (int i = 0; i < 4; ++i) {
            result.append(rnd.nextInt(9));
        }
        return result.toString();
    }

}
