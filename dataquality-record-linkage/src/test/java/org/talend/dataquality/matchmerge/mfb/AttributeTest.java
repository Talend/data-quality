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
package org.talend.dataquality.matchmerge.mfb;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.matchmerge.Attribute;

public class AttributeTest {

    /**
     * Test method for {@link org.talend.dataquality.matchmerge.Attribute#getCompareValue()}.
     */
    @Test
    public void testGetCompareValue() {
        String referenceValue = "03-03-2003"; //$NON-NLS-1$
        String inputColValue = "02-02-2000"; //$NON-NLS-1$
        String colName = "HIREDATE"; //$NON-NLS-1$
        Attribute attribute = new Attribute(colName, 0, inputColValue, 1);
        attribute.setReferenceValue(referenceValue);
        String compareValue = attribute.getCompareValue();
        Assert.assertEquals(compareValue, referenceValue, compareValue);
        attribute = new Attribute(colName, 1, inputColValue, 1);
        attribute.setReferenceValue(referenceValue);
        compareValue = attribute.getCompareValue();
        Assert.assertEquals(compareValue, inputColValue, compareValue);
        attribute = new Attribute(colName, 1, null, 1);
        attribute.setReferenceValue(referenceValue);
        compareValue = attribute.getCompareValue();
        Assert.assertEquals(compareValue, null, compareValue);
        attribute = new Attribute(colName, 1, inputColValue, 1);
        attribute.setReferenceValue(null);
        compareValue = attribute.getCompareValue();
        Assert.assertEquals(compareValue, inputColValue, compareValue);
    }

}
