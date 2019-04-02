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
package org.talend.dataquality.datamasking.functions.text.replace;

/**
 * created by jgonzalez on 22 juin 2015. See ReplaceNumeric.
 *
 */
public class ReplaceNumericDouble extends ReplaceNumeric<Double> {

    private static final long serialVersionUID = 2373577711682777009L;

    @Override
    protected Double getDefaultOutput() {
        return 0d;
    }

    @Override
    protected Double getOutput(String str) {
        return Double.parseDouble(str);
    }
}
