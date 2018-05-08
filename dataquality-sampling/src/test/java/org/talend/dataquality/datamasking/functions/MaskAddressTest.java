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

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class MaskAddressTest {

    private String output;

    private MaskAddress ma = new MaskAddress();

    @Before
    public void setUp() throws Exception {
        ma.setRandom(new Random(42));
    }

    @Test
    public void testEmpty() {
        ma.setKeepEmpty(true);
        output = ma.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        String input = "5 rue de l'oise"; //$NON-NLS-1$
        output = ma.generateMaskedRow(input);
        assertEquals("6 rue XX XXXXXX", output); //$NON-NLS-1$
    }

    @Test
    public void testWithFile() throws URISyntaxException {
        String path = this.getClass().getResource("data/top-domain.txt").toURI().getPath(); //$NON-NLS-1$
        ma.parse(path, false, new Random(42));
        String input = "5 rue de l'oise et facebook"; //$NON-NLS-1$
        output = ma.generateMaskedRow(input);
        assertEquals("6 rue XX XXXXXX XX facebook", output); //$NON-NLS-1$
    }

    @Test
    public void testParseWillNotImpactResult() throws URISyntaxException {
        ma.parse("5 rue de l'oise", false, new Random(42)); //$NON-NLS-1$
        output = ma.generateMaskedRow("5 rue de l'oise");
        assertEquals("6 rue XX XXXXXX", output); //$NON-NLS-1$
        assertEquals("5 rue de l'oise", ma.parameters[0]); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        String input = "not an address"; //$NON-NLS-1$
        output = ma.generateMaskedRow(input);
        assertEquals(output, "XXX XX XXXXXXX"); //$NON-NLS-1$
    }

}
