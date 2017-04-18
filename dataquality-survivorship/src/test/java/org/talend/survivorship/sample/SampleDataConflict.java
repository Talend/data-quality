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
package org.talend.survivorship.sample;

import java.util.LinkedHashMap;

public class SampleDataConflict {

    public static final LinkedHashMap<String, String> COLUMNS_CONFLICT = new LinkedHashMap<String, String>() {

        private static final long serialVersionUID = 1L;
        {
            put("firstName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("lastName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("middleName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("city1", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("city2", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("id", "Integer"); //$NON-NLS-1$ //$NON-NLS-2$
            put("birthday", "java.util.Date"); //$NON-NLS-1$ //$NON-NLS-2$
            put("gid", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("grp_size", "Integer"); //$NON-NLS-1$ //$NON-NLS-2$

        }
    };
}
