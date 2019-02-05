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
 * created by jgonzalez on 19 juin 2015. This class is called when the required function is GenerateBetween. It will
 * parse and set the parameters.
 *
 */
public abstract class GenerateBetween<T> extends Function<T> {

    private static final long serialVersionUID = 1L;

    protected int min = 0;

    protected int max = 0;

    protected void setBounds() {
        if (parameters != null && parameters.length == 2) {
            try {
                min = Integer.parseInt(parameters[0]);
                max = Integer.parseInt(parameters[1]);
            } catch (NumberFormatException e) {
                // Do nothing
            }
        }
        if (min > max) {
            int tmp = min;
            min = max;
            max = tmp;
        }
    }

    @Override
    public final void parse(String extraParameter, boolean keepNullValues) {
        super.parse(extraParameter, keepNullValues);
        setBounds();
    }

    @Override
    protected abstract T doGenerateMaskedField(T t);
}
