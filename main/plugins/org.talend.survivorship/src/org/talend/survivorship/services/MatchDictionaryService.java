// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
 * Detect the existence of a String in a given index.
 * <p>
 * TODO complete MatchDictionaryService
 */
public class MatchDictionaryService extends AbstractService {

    /**
     * MatchDictionaryService constructor.
     * 
     * @param dataset
     */
    public MatchDictionaryService(DataSet dataset) {
        super(dataset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.services.AbstractService#init()
     */
    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

}
