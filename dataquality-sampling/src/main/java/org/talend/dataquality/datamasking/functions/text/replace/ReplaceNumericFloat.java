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
public class ReplaceNumericFloat extends ReplaceNumeric<Float> {

    private static final long serialVersionUID = -2936953156402359732L;

    @Override
    protected Float getDefaultOutput() {
        return 0f;
    }

    @Override
    protected Float getOutput(String str) {
        return Float.parseFloat(str);
    }
}
