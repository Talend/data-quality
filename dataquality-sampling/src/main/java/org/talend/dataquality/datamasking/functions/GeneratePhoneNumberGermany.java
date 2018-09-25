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
public class GeneratePhoneNumberGermany extends Function<String> {

    private static final long serialVersionUID = -2193040590064798675L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        int choice = rnd.nextInt(4);
        switch (choice) {
        case 0:
            result.append("030 "); //$NON-NLS-1$
            break;
        case 1:
            result.append("040 "); //$NON-NLS-1$
            break;
        case 2:
            result.append("069 "); //$NON-NLS-1$
            break;
        case 3:
            result.append("089 "); //$NON-NLS-1$
            break;
        default:
            break;
        }
        for (int i = 0; i < 8; ++i) {
            result.append(nextRandomDigit());
        }
        return result.toString();
    }

}
