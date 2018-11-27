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
 * created by jgonzalez on 8 sept. 2015 Detailled comment
 *
 */
public abstract class GenerateSequence<T> extends Function<T> {

    private static final long serialVersionUID = 3643893998777572476L;

    protected int seq = 0;

    @Override
    public final void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        setSeq(extraParameter);
    }

    public int setSeq(String s) {
        int i = 0;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            // Do nothing
        }
        return i;
    }

    @Override
    protected abstract T doGenerateMaskedField(T t);

}
