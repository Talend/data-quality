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
 * created by jgonzalez on 19 juin 2015. This function works like GenerateFromList, the difference is that the parameter
 * is now a String holding the path to a file in the user’s computer.
 *
 */
public abstract class GenerateFromFile<T> extends Function<T> {

    private static final long serialVersionUID = 1556057898878709265L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateFromFile.class);

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

    protected abstract T getOutput(String string);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#resetParameter()
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

    @Override
    protected T doGenerateMaskedField(T t) {
        if (!genericTokens.isEmpty()) {
            return genericTokens.get(rnd.nextInt(genericTokens.size()));
        } else {
            return getDefaultOutput();
        }
    }

    protected abstract T getDefaultOutput();
}
