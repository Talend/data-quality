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
public class NumericVarianceLong extends NumericVariance<Long> {

    private static final long serialVersionUID = -5508336438978305407L;

    @Override
    protected Long doGenerateMaskedField(Long l) {
        if (l == null) {
            return 0L;
        } else {
            init();
            long value = l * ((long) rate + 100) / 100;
            return value;
        }
    }
}
