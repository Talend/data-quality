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

/**
 * @author jteuladedenantes
 * 
 * A FieldInterval is a set of values defined by a interval. So all values included in this interval are possible
 * values.
 */
public class FieldInterval extends AbstractField {

    private static final long serialVersionUID = 4713567446010547849L;

    private long minInterval;

    private long maxInterval;

    public FieldInterval(long minInterval, long maxInterval) {
        super();
        length = String.valueOf(maxInterval).length();
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
    }

    @Override
    public long getWidth() {
        return maxInterval - minInterval + 1;
    }

    @Override
    public Long encode(String str) {
        Long longStr;
        try {
            longStr = Long.valueOf(str);
            if (longStr < minInterval || longStr > maxInterval)
                return -1L;
        } catch (NumberFormatException e) {
            return -1L;
        }
        return longStr - minInterval;
    }

    @Override
    public String decode(long number) {
        if (number >= getWidth())
            return "";
        String res = String.valueOf(number + minInterval);
        // we add the potential missing zeros
        while (res.length() < length)
            res = "0" + res;
        return res;
    }

}
