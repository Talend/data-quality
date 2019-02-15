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

import java.util.Random;

/**
 * created by jgonzalez on 19 juin 2015. This function will generate a valid credit card number. It can be used on
 * String and Long values.
 *
 */
public abstract class GenerateCreditCardSimple<T> extends GenerateCreditCard<T> {

    private static final long serialVersionUID = 1364972443525284765L;

    protected Long number = null;

    protected void generateCreditCard(Random r) {
        CreditCardType cct = chooseCreditCardType();
        Long card = generateCreditCard(cct, r);
        number = card;
    }

    @Override
    protected abstract T doGenerateMaskedField(T t);
}
