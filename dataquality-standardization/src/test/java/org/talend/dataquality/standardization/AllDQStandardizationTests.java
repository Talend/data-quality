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
package org.talend.dataquality.standardization;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.talend.dataquality.standardization.index.CombinedQueryTest;
import org.talend.dataquality.standardization.index.SynonymIndexBuilderTest;
import org.talend.dataquality.standardization.index.SynonymIndexSearcherTest;
import org.talend.dataquality.standardization.index.test.SynonymTest;
import org.talend.dataquality.standardization.query.FirstNameStandardizeTest;
import org.talend.dataquality.standardization.record.SynonymRecordSearcherTest;

/**
 * DOC yyin class global comment. Detailled comment
 */
@RunWith(Suite.class)
@SuiteClasses({ SynonymRecordSearcherTest.class, FirstNameStandardizeTest.class, CombinedQueryTest.class,
        SynonymIndexBuilderTest.class, SynonymIndexSearcherTest.class, SynonymTest.class })
public class AllDQStandardizationTests {

}
