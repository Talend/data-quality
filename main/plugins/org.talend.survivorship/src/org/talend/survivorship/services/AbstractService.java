// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.survivorship.services;

import org.talend.survivorship.model.DataSet;

/**
 * Abstract class of extra services to use in rules.
 */
public abstract class AbstractService {

    protected DataSet dataset;

    /**
     * AbstractService constructor.
     * 
     * @param dataset
     */
    public AbstractService(DataSet dataset) {
        this.dataset = dataset;
    }

    public abstract void init();

}
