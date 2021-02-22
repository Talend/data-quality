/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */
package org.talend.dataquality.matchmerge.mfb.mdm.utils;

public interface MetadataExtensible {

    /**
     * Sets the defined property with the given name.
     *
     * @param key  the name of the property
     * @param data the new value for the property
     */
    void setData(String key, Object data);

    /**
     * Returns the defined property for the given name, or <code>null</code> if it has not been set.
     *
     *
     * @param key the name of the property
     * @return the value or <code>null</code> if it has not been set
     */
    <X> X getData(String key);

}