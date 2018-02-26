// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

public class SampleDataCheckOutputConflictColumn {

    public static final LinkedHashMap<String, String> COLUMNS_CONFLICT = new LinkedHashMap<String, String>() {

        private static final long serialVersionUID = 1L;
        {
            put("firstName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("lastName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("gid", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("grp_size", "Integer"); //$NON-NLS-1$ //$NON-NLS-2$

        }
    };

    public static final Object[][] SAMPLE_INPUT_1 = { { "aa", "bb", "g1", 4 }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            { "aa", "bb1", "g1", 0 }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            { "abc", "bb2", "g1", 0 }, //$NON-NLS-1$ //$NON-NLS-2$
            { "abc", "bb3", "g1", 0 } };

    public static final Object[][] SAMPLE_INPUT_2 = { { "aa", "bb", "g1", 6 }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            { "aa", "bb1", "g1", 0 }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            { "abc", "bb2", "g1", 0 }, //$NON-NLS-1$ //$NON-NLS-2$
            { "abc", "bb3", "g1", 0 }, { "def", "bb4", "g1", 0 }, { "def", "bb5", "g1", 0 }, };
}
