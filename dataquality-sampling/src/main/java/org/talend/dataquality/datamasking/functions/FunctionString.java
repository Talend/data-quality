package org.talend.dataquality.datamasking.functions;

import java.util.Random;

import org.apache.commons.lang3.NotImplementedException;
import org.talend.dataquality.datamasking.FunctionMode;

public abstract class FunctionString extends Function<String> {

    private static final long serialVersionUID = -5198693724247210254L;

    @Override
    protected String doGenerateMaskedField(String string) {
        throw new NotImplementedException("Not implemented for Function: " + getClass().getName());
    }

    @Override
    protected String doGenerateMaskedField(String str, FunctionMode mode) {

        Random r = rnd;
        if (FunctionMode.CONSISTENT == mode)
            r = getRandomForObject(str);

        return doGenerateMaskedFieldWithRandom(str, r);
    }

    protected abstract String doGenerateMaskedFieldWithRandom(String str, Random r);
}
