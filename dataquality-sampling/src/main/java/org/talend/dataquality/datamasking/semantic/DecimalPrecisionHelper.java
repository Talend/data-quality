package org.talend.dataquality.datamasking.semantic;

import org.talend.daikon.number.BigDecimalParser;

public class DecimalPrecisionHelper {

    private DecimalPrecisionHelper() {
        // no need to implement
    }

    public static int getDecimalPrecision(final String input) {
        String inputWithoutScientificPart = input;
        if (input.contains("e")) { //$NON-NLS-1$
            inputWithoutScientificPart = input.substring(0, input.lastIndexOf("e")); //$NON-NLS-1$
        } else if (input.contains("E")) { //$NON-NLS-1$
            inputWithoutScientificPart = input.substring(0, input.lastIndexOf("E")); //$NON-NLS-1$
        }
        String bigDecimalString = BigDecimalParser.toBigDecimal(inputWithoutScientificPart).toString();
        int idx = bigDecimalString.lastIndexOf("."); //$NON-NLS-1$
        if (idx < 1) {
            return 0;
        } else {
            return inputWithoutScientificPart.length() - bigDecimalString.lastIndexOf(".") - 1; //$NON-NLS-1$
        }
    }
}
