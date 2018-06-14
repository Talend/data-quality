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

import static org.talend.dataquality.semantic.TestUtils.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TenancyContextHolder.class })
public abstract class CategoryRegistryManagerAbstract {

    private static final String TARGET_TEST_CRM_PATH = "target/test_crm";

    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void before() {
        CategoryRegistryManager.setLocalRegistryPath(TARGET_TEST_CRM_PATH);
    }

    @AfterClass
    public static void after() throws IOException {
        FileUtils.forceDeleteOnExit(new File(TARGET_TEST_CRM_PATH));
    }

    @Before
    public void setUp() {
        mockWithTenant(this.getClass().getSimpleName() + "_" + testName.getMethodName());
    }

}
