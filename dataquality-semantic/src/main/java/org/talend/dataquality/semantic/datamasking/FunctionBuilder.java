package org.talend.dataquality.semantic.datamasking;

import org.talend.dataquality.datamasking.functions.Function;

public class FunctionBuilder {

    public static Function<String> functionInitializer(MaskableCategoryEnum cat)
            throws IllegalAccessException, InstantiationException {
        Function function = (Function<String>) cat.getFunctionType().getClazz().newInstance();
        function.parse(cat.getParameter(), true);
        function.setKeepFormat(true);
        return function;
    }
}
