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
 * created by jgonzalez on 19 juin 2015. This function will generate a correct French phone number.
 *
 */
public class GeneratePhoneNumberFrench extends Function<String> {

    private static final long serialVersionUID = -1118298923509759266L;

    @Override
    protected String doGenerateMaskedField(String str) throws org.talend.dataquality.sampling.exception.DQException {
        StringBuilder result = new StringBuilder("+33 "); //$NON-NLS-1$
        result.append(rnd.nextInt(9) + 1);
        for (int i = 0; i < 8; i++) {
            result.append(rnd.nextInt(9));
        }
        return result.toString();
    }
}
