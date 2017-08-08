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

import java.io.Serializable;

/**
 * @author jteuladedenantes
 * 
 * A Field is a set of values. According to a field, we can encode a value in a number or decode a number in a value of
 * the field
 */
public abstract class AbstractField implements Serializable {

    private static final long serialVersionUID = 9219485812042520145L;

    /**
     * the number of characters in a field
     */
    protected int length;

    /**
     * @return the number of different possible values in this field
     */
    public abstract long getWidth();

    /**
     * @param str, the string to encode
     * @return the position number related to this string, -1 if str doesn't exist in this field
     */
    public abstract Long encode(String str);

    /**
     * @param number, the number to decode
     * @return the string related to this number, "" if number is longer than the width
     */
    public abstract String decode(long number);

    /**
     * @return the number of characters in a field
     */
    public int getLength() {
        return length;
    }

}
