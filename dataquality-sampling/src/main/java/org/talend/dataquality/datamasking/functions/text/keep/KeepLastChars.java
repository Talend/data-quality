package org.talend.dataquality.datamasking.functions.text.keep;

import org.talend.dataquality.datamasking.functions.text.CharactersOperation;

public abstract class KeepLastChars<T> extends CharactersOperation<T> {

    private static final long serialVersionUID = 4065232723157315230L;

    @Override
    protected void initAttributes() {
        endNumberToKeep = Integer.parseInt(parameters[0]);
        if (parameters.length == 2)
            charToReplace = parameters[1].charAt(0);
    }

}
