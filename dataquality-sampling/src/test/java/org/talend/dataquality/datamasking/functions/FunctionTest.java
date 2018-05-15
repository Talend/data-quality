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

/**
 * Test for function class
 */
public class FunctionTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.datamasking.functions.Function#parse(java.lang.String, boolean, java.util.Random)}.
     */
    @Test
    public void testParse() {
        ReplaceNumericString replaceNumericString = new ReplaceNumericString();
        replaceNumericString.parse("2", false, null); //$NON-NLS-1$
        Assert.assertEquals("Parameters length should be 1", 1, replaceNumericString.parameters.length); //$NON-NLS-1$
        Assert.assertEquals("Parameters should be valid", true, replaceNumericString.isValidParameters); //$NON-NLS-1$

        GenerateFromFileString generateFromFileString = new GenerateFromFileString();
        generateFromFileString.parse("abc1", false, null);// invalid path //$NON-NLS-1$
        Assert.assertEquals("Parameters length should be 1", 1, generateFromFileString.parameters.length); //$NON-NLS-1$
        Assert.assertEquals("Parameters should not be empty", true, generateFromFileString.parameters[0].length() > 0); //$NON-NLS-1$
        Assert.assertEquals("genericTokens should not be empty", true, generateFromFileString.genericTokens.get(0).length() > 0); //$NON-NLS-1$

        generateFromFileString = new GenerateFromFileString();
        generateFromFileString.parse("", false, null); //$NON-NLS-1$
        String expectResultWin = "Empty is not a path to a file."; //$NON-NLS-1$
        String expectResultLinux = "(No such file or directory)"; //$NON-NLS-1$
        String parameterResult = generateFromFileString.parameters[0];
        String tokensResult = generateFromFileString.genericTokens.get(0);
        Assert.assertEquals("Parameters length should be 1", 1, generateFromFileString.parameters.length); //$NON-NLS-1$
        Assert.assertTrue("Parameters should not be empty", //$NON-NLS-1$
                expectResultWin.equals(parameterResult) || expectResultLinux.equals(parameterResult));
        Assert.assertTrue("genericTokens should not be empty", //$NON-NLS-1$
                expectResultWin.equals(tokensResult) || expectResultLinux.equals(tokensResult));
    }

}
