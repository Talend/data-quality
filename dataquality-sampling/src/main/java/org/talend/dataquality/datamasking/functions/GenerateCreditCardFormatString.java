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

import org.apache.commons.lang.StringUtils;

/**
 * created by jgonzalez on 19 juin 2015. See GenerateCreditCardFormat.
 *
 */
public class GenerateCreditCardFormatString extends GenerateCreditCardFormat<String> {

    private static final long serialVersionUID = 3682663337119470753L;

    @Override
    protected String doGenerateMaskedField(String str) {
        String strWithoutSpaces = removeFormatInString(str);
        CreditCardType cctFormat;
        StringBuilder res = new StringBuilder();
        if (StringUtils.isEmpty(strWithoutSpaces)) {
            cctFormat = chooseCreditCardType();
            res.append(generateCreditCard(cctFormat));
        } else {
            try {
                cctFormat = getCreditCardType(Long.parseLong(strWithoutSpaces)); // $NON-NLS-1$
            } catch (NumberFormatException e) {
                cctFormat = chooseCreditCardType();
            }
            if (cctFormat != null) {
                res.append(generateCreditCardFormat(cctFormat, strWithoutSpaces));
            } else {
                cctFormat = chooseCreditCardType();
                res.append(generateCreditCard(cctFormat));
            }
        }
        if (keepFormat)
            return insertFormatInString(str, res);
        else
            return res.toString();
    }
}
