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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

import org.talend.daikon.number.BigDecimalParser;
import org.talend.dataquality.datamasking.functions.NumericVariance;

public class FluctuateNumericString extends NumericVariance<String> {

    private static final long serialVersionUID = -8029563336814263376L;

    private static final Pattern patternInteger = Pattern.compile("^(\\+|-)?\\d+$");

    @Override
    protected String doGenerateMaskedField(String input) {
        if (input == null || EMPTY_STRING.equals(input.trim())) {
            return input;
        } else {
            init();
            double rateToApply = rnd.nextDouble() * rate;
            if (patternInteger.matcher(input).matches()) {
                BigDecimal bigDecimal = new BigDecimal(input);
                if (bigDecimal.abs().compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
                    final BigDecimal result = bigDecimal.multiply(BigDecimal.valueOf(rateToApply + 100))
                            .divide(new BigDecimal(100));
                    return result.setScale(0, RoundingMode.HALF_UP).toString();
                } else {
                    final long result = Math.round(Long.valueOf(input) * (rateToApply + 100) / 100);
                    return String.valueOf(result);
                }
            } else {
                try {
                    BigDecimal bigDecimal = BigDecimalParser.toBigDecimal(input);
                    final int decimalLength = DecimalPrecisionHelper.getDecimalPrecision(input);
                    if (bigDecimal.abs().compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
                        final BigDecimal result = bigDecimal.multiply(BigDecimal.valueOf(rateToApply + 100))
                                .divide(new BigDecimal(100));
                        if (input.contains("e") || input.contains("E")) {
                            return String.valueOf(result.setScale(decimalLength, RoundingMode.HALF_UP).doubleValue());
                        } else {
                            return result.setScale(decimalLength, RoundingMode.HALF_UP).toString();
                        }
                    } else {
                        final Double doubleValue = bigDecimal.doubleValue() * (rateToApply + 100) / 100;
                        final BigDecimal result = new BigDecimal(doubleValue);
                        return String.valueOf(result.setScale(decimalLength, RoundingMode.HALF_UP).doubleValue());
                    }
                } catch (NumberFormatException e) {
                    return ReplaceCharacterHelper.replaceCharacters(input, rnd);
                }
            }
        }
    }
}
