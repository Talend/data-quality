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
package org.talend.dataquality.datamasking.functions;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.datamasking.FormatPreservingMethod;

/**
 * Test for function class
 */
public class FunctionTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.datamasking.functions.Function#parse(java.lang.String, boolean)}.
     */
    @Test
    public void testParse() {

        GenerateFromFileString generateFromFileString = new GenerateFromFileString();
        generateFromFileString.parse("abc1", false);// invalid path //$NON-NLS-1$
        Assert.assertEquals("Parameters length should be 1", 1, generateFromFileString.parameters.length); //$NON-NLS-1$
        Assert.assertTrue("Parameters should not be empty", generateFromFileString.parameters[0].length() > 0); //$NON-NLS-1$
        Assert.assertTrue("genericTokens should not be empty", generateFromFileString.genericTokens.get(0).length() > 0); //$NON-NLS-1$

    }

    @Test
    public void testParseNumeric() {
        ReplaceNumericString replaceNumericString = new ReplaceNumericString();
        replaceNumericString.parse("2", false); //$NON-NLS-1$
        Assert.assertEquals("Parameters length should be 1", 1, replaceNumericString.parameters.length); //$NON-NLS-1$
        Assert.assertTrue("Parameters should be valid", replaceNumericString.isValidParameters); //$NON-NLS-1$
    }

    @Test
    public void testEmptyParse() {
        GenerateFromFileString generateFromFileString = new GenerateFromFileString();
        generateFromFileString.parse("", false); //$NON-NLS-1$
        String parameterResult = generateFromFileString.parameters[0];
        Assert.assertEquals("Parameters length should be 1", 1, generateFromFileString.parameters.length); //$NON-NLS-1$
        Assert.assertTrue("Parameters should not be empty", //$NON-NLS-1$
                parameterResult.length() > 0);
        Assert.assertEquals("genericTokens should not be empty", "Configuration issue (check your parameters)", //$NON-NLS-1$ //$NON-NLS-2$
                generateFromFileString.genericTokens.get(0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setSecretWithWrongFunctionType() {
        Function fn = new GenerateCreditCardFormatLong();
        fn.setSecret(FormatPreservingMethod.BASIC, "Password");
    }
}
