package org.talend.dataquality.datamasking.functions;

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataquality.datamasking.FunctionMode;

import java.util.Random;

public abstract class FunctionString extends Function<String> {

    @Override
    protected String doGenerateMaskedField(String string) {
        throw new NotImplementedException();
    }

    @Override
    protected String doGenerateMaskedField(String str, FunctionMode mode) {

        Random r = rnd;
        if (FunctionMode.CONSISTENT == mode)
            r = getRandomForString(str);

        return doGenerateMaskedFieldWithRandom(r);
    }

    protected abstract String doGenerateMaskedFieldWithRandom(Random r);
}
