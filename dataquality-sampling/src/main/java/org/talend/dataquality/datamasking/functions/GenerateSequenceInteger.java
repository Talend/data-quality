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
 * created by jgonzalez on 24 juin 2015. This function will return the super.seq value and increment it.
 *
 */
public class GenerateSequenceInteger extends GenerateSequence<Integer> {

    private static final long serialVersionUID = 6100107351748175598L;

    @Override
    protected Integer doGenerateMaskedField(Integer i) {
        return seq++;
    }

}
