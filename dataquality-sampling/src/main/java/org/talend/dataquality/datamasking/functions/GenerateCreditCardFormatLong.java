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

import org.talend.dataquality.datamasking.FunctionMode;

import java.util.Random;

import static org.talend.dataquality.datamasking.FunctionMode.CONSISTENT;

/**
 * created by jgonzalez on 19 juin 2015. See GenerateCreditCardFormat.
 *
 */
public class GenerateCreditCardFormatLong extends GenerateCreditCardFormat<Long> {

    private static final long serialVersionUID = 4432818921989956298L;

    @Override
    protected Long doGenerateMaskedField(Long l, FunctionMode mode) {
        Random r = rnd;
        if (CONSISTENT == mode)
            r = getRandomForObject(l);
        return doGenerateMaskedField(l, r);
    }

    @Override
    protected Long doGenerateMaskedField(Long l) {
        return doGenerateMaskedField(l, rnd);
    }

    private Long doGenerateMaskedField(Long l, Random r) {
        CreditCardType cctFormat;
        Long res;
        if (l == null) {
            cctFormat = chooseCreditCardType();
            res = generateCreditCard(cctFormat, r);
        } else {
            cctFormat = getCreditCardType(l);
            if (cctFormat != null) {
                res = generateCreditCardFormat(cctFormat, l, r);
            } else {
                cctFormat = chooseCreditCardType();
                res = generateCreditCard(cctFormat, r);
            }
        }
        return res;
    }
}
