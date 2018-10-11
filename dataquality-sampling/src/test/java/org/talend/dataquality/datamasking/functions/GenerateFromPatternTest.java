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

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * created by jgonzalez on 19 août 2015 Detailled comment
 *
 */
public class GenerateFromPatternTest {

    private String output;

    private GenerateFromPattern gfp = new GenerateFromPattern();

    @Before
    public void setUp() throws Exception {
        gfp.setRandom(new Random(42));
    }

    @Test
    public void standardPattern() {
        gfp.parameters = "aaAA99".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = gfp.generateMaskedRow(null);
        assertEquals(output, "ñjÖÛ05"); //$NON-NLS-1$
    }

    @Test
    public void patternWithReference() {
        gfp.parameters = "aaAA99\\1, @gmail.com".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = gfp.generateMaskedRow(null);
        assertEquals(output, "ñjÖÛ05@gmail.com"); //$NON-NLS-1$
    }

    @Test
    public void patternWithReferences() {
        gfp.parameters = "\\1aA9\\2hHkK\\3C\\4G,latin:,;japanese:,;chinese:,;korean:".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = gfp.generateMaskedRow(null);
        assertEquals(output, "latin:ñT8;japanese:hぅﾀト;chinese:睻;korean:롖"); //$NON-NLS-1$
    }

    @Test
    public void patternWithMissingReference() {
        gfp.parameters = "aaAA99\\, @gmail.com".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = gfp.generateMaskedRow(null);
        assertEquals(output, "ñjÖÛ05\\"); //$NON-NLS-1$
    }

    @Test
    public void patternWithWrongReference() {
        gfp.parameters = "aaAA99\\2, @gmail.com".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = gfp.generateMaskedRow(null);
        assertEquals(output, "ñjÖÛ05\\2"); //$NON-NLS-1$
    }

    @Test
    public void wrongPattern() {
        gfp.parameters = Function.EMPTY_STRING.split(","); //$NON-NLS-1$
        output = gfp.generateMaskedRow(null);
        assertEquals(output, Function.EMPTY_STRING);
    }

    @Test
    public void nullPattern() {
        gfp.keepNull = true;
        output = gfp.generateMaskedRow(null);
        assertEquals(output, null);
    }
}
