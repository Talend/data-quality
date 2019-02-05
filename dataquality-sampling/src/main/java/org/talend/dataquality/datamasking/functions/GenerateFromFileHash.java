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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jgonzalez on 24 juin 2015. This function works like GenerateFromFile, the only difference is that it will
 * use the hashCode() function provided by Java to choose an element from the list. When having the hashCode, we apply a
 * modulo according to the number of elements in the list.
 *
 */
public abstract class GenerateFromFileHash<T> extends Function<T> {

    private static final long serialVersionUID = -4616169672287269594L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateFromFileHash.class);

    protected List<T> genericTokens = new ArrayList<>();

    @Override
    public void parse(String extraParameter, boolean keepNullValues) {
        super.parse(extraParameter, keepNullValues);
        for (String parameter : parameters) {
            try {
                genericTokens.add(getOutput(parameter));
            } catch (NumberFormatException e) {
                LOGGER.info("The parameter " + parameter + " can't be parsed in the required type.");
            }
        }
    }

    @Override
    protected T doGenerateMaskedField(T t) {
        if (!genericTokens.isEmpty()) {
            if (t == null) {
                return genericTokens.get(rnd.nextInt(genericTokens.size()));
            } else {
                return genericTokens.get(Math.abs(t.hashCode() % genericTokens.size()));
            }
        } else {
            return getDefaultOutput();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#resetParameterTo(java.lang.String)
     */
    @Override
    protected void resetParameterTo(String errorMessage) {
        parameters = new String[] { errorMessage };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#isNeedCheckPath()
     */
    @Override
    protected boolean isNeedCheckPath() {
        return true;
    }

    protected abstract T getOutput(String string);

    protected abstract T getDefaultOutput();

}
