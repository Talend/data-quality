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
 * created by jgonzalez on 18 juin 2015. See NumericVariance.
 *
 */
public class NumericVarianceDouble extends NumericVariance<Double> {

    private static final long serialVersionUID = 3652667602304768170L;

    @Override
    protected Double doGenerateMaskedField(Double d) {
        if (d == null) {
            return 0.0;
        } else {
            init();
            double value = d * ((double) rate + 100) / 100;
            return value;
        }
    }
}
