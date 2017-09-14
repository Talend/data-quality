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

public class NumericVarianceString extends NumericVariance<String> {

    private static final long serialVersionUID = -8029563336814263376L;

    @Override
    protected String doGenerateMaskedField(String input) {
        if (input == null || EMPTY_STRING.equals(input)) {
            return input;
        } else {
            init();
            final Double value = Double.valueOf(input) * ((double) rate + 100) / 100;
            return value.toString();
        }
    }
}
