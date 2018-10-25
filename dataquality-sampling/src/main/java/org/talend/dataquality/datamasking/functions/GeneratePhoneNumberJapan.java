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
public class GeneratePhoneNumberJapan extends Function<String> {

    private static final long serialVersionUID = -1152538201280991701L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder("03-"); //$NON-NLS-1$
        for (int i = 0; i < 4; ++i) {
            result.append(nextRandomDigit());
        }
        result.append("-"); //$NON-NLS-1$
        for (int i = 0; i < 4; ++i) {
            result.append(nextRandomDigit());
        }
        return result.toString();
    }
}
