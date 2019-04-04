package org.talend.dataquality.datamasking.functions;

import java.util.Random;

import org.talend.dataquality.datamasking.FunctionMode;

public abstract class FunctionString extends Function<String> {

    private static final long serialVersionUID = -5198693724247210254L;

    @Override
    protected String doGenerateMaskedField(String string) {
        if (FunctionMode.CONSISTENT == maskingMode) {
            return doGenerateMaskedFieldWithRandom(string, getRandomForObject(string));
        }
        return doGenerateMaskedFieldWithRandom(string, rnd);
    }

    protected abstract String doGenerateMaskedFieldWithRandom(String str, Random r);
}
