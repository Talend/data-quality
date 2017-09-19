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

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * created by jgonzalez on 19 juin 2015. This function works like GenerateFromList, the difference is that the parameter
 * is now a String holding the path to a file in the user’s computer.
 *
 */
public abstract class GenerateFromFile<T> extends Function<T> {

    private static final long serialVersionUID = 1556057898878709265L;

    private static final Logger LOGGER = Logger.getLogger(GenerateFromFile.class);

    protected List<T> genericTokens = new ArrayList<T>();

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        for (int i = 0; i < parameters.length; ++i) {
            try {
                genericTokens.add(getOutput(parameters[i]));
            } catch (NumberFormatException e) {
                LOGGER.info("The parameter " + parameters[i] + " can't be parsed in the required type.");
            }
        }
    }

    protected abstract T getOutput(String string);

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
