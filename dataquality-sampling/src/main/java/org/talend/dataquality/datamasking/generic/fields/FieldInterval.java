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
package org.talend.dataquality.datamasking.generic.fields;

import java.math.BigInteger;

/**
 * @author jteuladedenantes
 * 
 * A FieldInterval is a set of values defined by a interval. So all values included in this interval are possible
 * values.
 */
public class FieldInterval extends AbstractField {

    private static final long serialVersionUID = 4713567446010547849L;

    private BigInteger minInterval;

    private BigInteger maxInterval;

    public FieldInterval(BigInteger minInterval, BigInteger maxInterval) {
        super();
        length = String.valueOf(maxInterval).length();
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
    }

    @Override
    public BigInteger getWidth() {
        return maxInterval.subtract(minInterval).add(BigInteger.ONE);
    }

    @Override
    public BigInteger encode(String str) {
        BigInteger bigInteger;
        try {
            bigInteger = new BigInteger(str);
            if (bigInteger.compareTo(minInterval) < 0 || bigInteger.compareTo(maxInterval) > 0)
                return BigInteger.valueOf(-1);
        } catch (NumberFormatException e) {
            return BigInteger.valueOf(-1);
        }
        return bigInteger.subtract(minInterval);
    }

    @Override
    public String decode(BigInteger number) {
        if (number.compareTo(getWidth()) >= 0)
            return "";
        String res = number.add(minInterval).toString();
        // we add the potential missing zeros
        while (res.length() < length)
            res = "0" + res;
        return res;
    }

}
