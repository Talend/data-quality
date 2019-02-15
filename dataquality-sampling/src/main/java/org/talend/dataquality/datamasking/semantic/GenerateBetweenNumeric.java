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
package org.talend.dataquality.datamasking.semantic;

import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.functions.GenerateBetween;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Generate a numerical value between the 2 given numerical values.
 * The result follows the precision of the input value.
 * In case the input value is not numerical, then we will generate a .
 */
public class GenerateBetweenNumeric extends GenerateBetween<String> {

    private static final long serialVersionUID = -8029563336814263376L;

    private static final Pattern patternInteger = Pattern.compile("^(\\+|-)?\\d+$"); //$NON-NLS-1$

    private double minDouble = 0;

    private double maxDouble = 0;

    private int minimumPrecision = 0;

    @Override
    protected void setBounds() {
        if (parameters != null && parameters.length == 2) {
            try {
                minDouble = Double.parseDouble(parameters[0]);
                maxDouble = Double.parseDouble(parameters[1]);
                minimumPrecision = Math.max(DecimalPrecisionHelper.getDecimalPrecision(parameters[0]),
                        DecimalPrecisionHelper.getDecimalPrecision(parameters[1]));
            } catch (NumberFormatException e) {
                minDouble = 0;
                maxDouble = 0;
            }
        }
        if (minDouble > maxDouble) {
            double tmp = minDouble;
            minDouble = maxDouble;
            maxDouble = tmp;
        }
        min = (int) Math.ceil(minDouble);
        max = (int) Math.floor(maxDouble);
    }

    @Override
    protected String doGenerateMaskedField(String input, FunctionMode mode) {
        Random r = rnd;
        if (FunctionMode.CONSISTENT == mode)
            r = getRandomForObject(input);

        return doGenerateMaskedFieldWithRandom(input, r);

    }

    @Override
    protected String doGenerateMaskedField(String input) {
        return doGenerateMaskedFieldWithRandom(input, rnd);
    }

    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        if (str == null || EMPTY_STRING.equals(str.trim())) {
            return str;
        } else {
            if (patternInteger.matcher(str).matches() && max >= min) {
                final int result = r.nextInt(max - min + 1) + min;
                return String.valueOf(result);
            } else {
                final double result = generateRandomDoubleValue(r);
                try {
                    final int decimalLength = DecimalPrecisionHelper.getDecimalPrecision(str);
                    return getResultStringWithPrecision(result, Math.max(decimalLength, minimumPrecision));
                } catch (NumberFormatException e) {
                    return getResultStringWithPrecision(result, minimumPrecision);
                }
            }
        }
    }

    private double generateRandomDoubleValue(Random r) {
        return r.nextDouble() * (maxDouble - minDouble) + minDouble;
    }

    private String getResultStringWithPrecision(double result, int decimalLength) {
        return BigDecimal.valueOf(result).setScale(decimalLength, RoundingMode.HALF_UP).toString();
    }

}
