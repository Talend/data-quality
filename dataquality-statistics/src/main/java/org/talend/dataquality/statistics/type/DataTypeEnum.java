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
package org.talend.dataquality.statistics.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public enum DataTypeEnum {
    BOOLEAN,
    INTEGER,
    DOUBLE,
    STRING,
    DATE,
    TIME,
    EMPTY;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTypeEnum.class);

    public static DataTypeEnum get(String typeName) {
        try {
            return DataTypeEnum.valueOf(typeName.toUpperCase());
        } catch (Exception e) {
            LOGGER.debug(e.getMessage(), e);
            return DataTypeEnum.STRING;
        }
    }
}
