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
package org.talend.dataquality.datamasking.functions;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author jteuladedenantes
 * 
 * A FieldEnum is a list of specific values. We defined a FieldEnum by an exhaustive list of all possible values.
 */
public class FieldEnum extends AbstractField {

    private static final long serialVersionUID = 4434958606928963578L;

    private static final Logger LOGGER = Logger.getLogger(FieldEnum.class);

    /**
     * The exhaustive list of values
     */
    private List<String> enumValues;

    public FieldEnum(List<String> enumValues, int length) {
        this.length = length;
        for (String value : enumValues)
            if (value.length() != length) {
                LOGGER.error("The field <" + value + "> with a length = " + value.length() + " should have a length = " + length);
                return;
            }
        this.enumValues = enumValues;
    }

    @Override
    public long getWidth() {
        return enumValues.size();
    }

    @Override
    public Long encode(String str) {
        return (long) enumValues.indexOf(str);
    }

    @Override
    public String decode(long number) {
        if (number >= getWidth())
            return "";
        return enumValues.get((int) number);
    }
}
