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
package org.talend.dataquality.semantic.broadcast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;

public class BroadcastRegexObjectTest extends CategoryRegistryManagerAbstract {

    @Test
    public void testCreateAndGet() throws Exception {
        // given
        final BroadcastRegexObject bro = new BroadcastRegexObject(CategoryRegistryManager.getInstance().getRegexURI());

        // when
        UserDefinedClassifier regex = bro.getRegexClassifier();

        // then
        assertNotNull(regex);
        assertEquals("Unexpected classifier size!", 47, regex.getClassifiers().size());

    }

}
