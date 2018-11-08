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

package org.talend.dataquality.datamasking;

import java.util.ArrayList;
import java.util.List;

/**
 * created by jgonzalez on 25 juin 2015 This class is the main class used in the masking process.
 *
 * @param <I> The input schema
 * @param <O> The output schema
 */
public abstract class DataMasker<I, O> {

    protected abstract O generateOutput(I v, boolean isOriginal);

    /**
     * DOC jgonzalez Comment method "process". This method is called to generate the masked output.
     * 
     * @param v A row from the input schema
     * @param keepOriginal A boolean, if true, the masker will return two rows : the original one and the masked one, if
     * not, only the masked row wiil be generated.
     * @return A masekd row from the ouput schema.
     */
    public List<O> process(I v, boolean keepOriginal) {
        List<O> reslutList = new ArrayList<>();

        if (keepOriginal) {
            reslutList.add(generateOutput(v, true));
        }
        reslutList.add(generateOutput(v, false));

        return reslutList;
    }

}