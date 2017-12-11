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
package org.talend.dataquality.semantic;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;

import static org.talend.dataquality.semantic.api.CategoryRegistryManager.DEFAULT_TENANT_ID;

public abstract class CategoryRegistryManagerAbstract {

    @BeforeClass
    public static void before() {
        CategoryRegistryManager.setLocalRegistryPath("target/test_crm" + System.currentTimeMillis());
    }
}
