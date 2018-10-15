package org.talend.dataquality.datamasking.semantic;

import org.talend.daikon.number.BigDecimalParser;

public class DecimalPrecisionHelper {

    public static int getDecimalPrecision(final String input) {
        String inputWithoutScientificPart = input;
        if (input.contains("e")) {
            inputWithoutScientificPart = input.substring(0, input.lastIndexOf("e"));
        } else if (input.contains("E")) {
            inputWithoutScientificPart = input.substring(0, input.lastIndexOf("E"));
        }
        String bigDecimalString = BigDecimalParser.toBigDecimal(inputWithoutScientificPart).toString();
        int idx = bigDecimalString.lastIndexOf(".");
        if (idx < 1) {
            return 0;
        } else {
            return inputWithoutScientificPart.length() - bigDecimalString.lastIndexOf(".") - 1;
        }
    }
}
