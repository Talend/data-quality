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
        if (x == null) {
            return getTypeByName("integer"); //$NON-NLS-1$
        }
        return getTypeByName(x.getClass().getSimpleName().toLowerCase());
    }

    /**
     * DOC jgonzalez Comment method "getType". This function is used when the parameter is a Long.
     * 
     * @param x The input value
     * @return The type of the variable.
     */
    public int getType(Long x) {
        if (x == null) {
            return getTypeByName("long"); //$NON-NLS-1$
        }
        return getTypeByName(x.getClass().getSimpleName().toLowerCase());
    }

    /**
     * DOC jgonzalez Comment method "getType". This function is used when the parameter is a Float.
     * 
     * @param x The input value
     * @return The type of the variable.
     */
    public int getType(Float x) {
        if (x == null) {
            return getTypeByName("float"); //$NON-NLS-1$
        }
        return getTypeByName(x.getClass().getSimpleName().toLowerCase());
    }

    /**
     * DOC jgonzalez Comment method "getType". This function is used when the parameter is a Double.
     * 
     * @param x The input value
     * @return The type of the variable.
     */
    public int getType(Double x) {
        if (x == null) {
            return getTypeByName("double"); //$NON-NLS-1$
        }
        return getTypeByName(x.getClass().getSimpleName().toLowerCase());
    }

    /**
     * DOC jgonzalez Comment method "getType". This function is used when the parameter is a String.
     * 
     * @param x The input value
     * @return The type of the variable.
     */
    public int getType(String x) {
        if (x == null) {
            return getTypeByName("string"); //$NON-NLS-1$
        }
        return getTypeByName(x.getClass().getSimpleName().toLowerCase());
    }

    /**
     * DOC jgonzalez Comment method "getType". This function is used when the parameter is a Date.
     * 
     * @param x The input value
     * @return The type of the variable.
     */
    public int getType(Date x) {
        if (x == null) {
            return getTypeByName("date"); //$NON-NLS-1$
        }
        return getTypeByName(x.getClass().getSimpleName().toLowerCase());
    }

    public int getTypeByName(String dataType) {
        if (dataType == null) {
            return -1;
        }
        switch (dataType) {
        case "numeric": //$NON-NLS-1$
        case "integer": //$NON-NLS-1$
            return 0;
        case "long": //$NON-NLS-1$
            return 1;
        case "float": //$NON-NLS-1$
            return 2;
        case "double": //$NON-NLS-1$
        case "decimal": //$NON-NLS-1$
            return 3;
        case "string": //$NON-NLS-1$
            return 4;
        case "date": //$NON-NLS-1$
            return 5;
        default:
            return -1;

        }
    }
}
