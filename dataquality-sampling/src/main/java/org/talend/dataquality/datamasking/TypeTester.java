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
package org.talend.dataquality.datamasking;

import java.util.Date;

/**
 * created by jgonzalez on 23 juin 2015. This class is used to know which type a variable is. It uses the java overload
 * to call the right function according to the parameter.
 *
 */
public class TypeTester {

    /**
     * DOC jgonzalez Comment method "getType". This function is used when the parameter is an Integer.
     * 
     * @param x The input value
     * @return The type of the variable.
     */
    public int getType(Integer x) {
        return 0;
    }

    /**
     * DOC jgonzalez Comment method "getType". This function is used when the parameter is a Long.
     * 
     * @param x The input value
     * @return The type of the variable.
     */
    public int getType(Long x) {
        return 1;
    }

    /**
     * DOC jgonzalez Comment method "getType". This function is used when the parameter is a Float.
     * 
     * @param x The input value
     * @return The type of the variable.
     */
    public int getType(Float x) {
        return 2;
    }

    /**
     * DOC jgonzalez Comment method "getType". This function is used when the parameter is a Double.
     * 
     * @param x The input value
     * @return The type of the variable.
     */
    public int getType(Double x) {
        return 3;
    }

    /**
     * DOC jgonzalez Comment method "getType". This function is used when the parameter is a String.
     * 
     * @param x The input value
     * @return The type of the variable.
     */
    public int getType(String x) {
        return 4;
    }

    /**
     * DOC jgonzalez Comment method "getType". This function is used when the parameter is a Date.
     * 
     * @param x The input value
     * @return The type of the variable.
     */
    public int getType(Date x) {
        return 5;
    }

    public int getTypeByName(String dataType) {
        switch (dataType) {
        case "numeric":
        case "integer":
            return 0;
        case "long":
            return 1;
        case "float":
            return 2;
        case "double":
        case "decimal":
            return 3;
        case "date":
            return 5;
        case "string":
            return 4;
        default:
            return -1;

        }
    }

}
