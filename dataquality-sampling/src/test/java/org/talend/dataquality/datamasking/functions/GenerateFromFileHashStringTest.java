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

import java.net.URISyntaxException;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateFromFileHashStringTest {

    private String output;

    public void testGood() throws URISyntaxException {
        GenerateFromFileHashString gffhs = new GenerateFromFileHashString();
        final String path = this.getClass().getResource("data/name.txt").toURI().getPath(); //$NON-NLS-1$
        gffhs.setRandom(new Random(42L));
        gffhs.parse(path, false);
        output = gffhs.generateMaskedRow(null);
        assertEquals("Brad X", output); //$NON-NLS-1$
    }

    @Test
    public void testEmpty() {
        GenerateFromFileHashString gffhs = new GenerateFromFileHashString();
        gffhs.setKeepEmpty(true);
        output = gffhs.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testSeparatorWin() throws URISyntaxException {
        GenerateFromFileHashString gffhs = new GenerateFromFileHashString();
        final String pathWin = this.getClass().getResource("data/name_win.txt").toURI().getPath(); //$NON-NLS-1$
        gffhs.parse(pathWin, false);
        int runTimes = 10000;
        String[] keysArray = new String[] { "Brad X", "Marouane", "Matthieu", "Xavier", "Aymen" };
        while (runTimes > 0) {
            output = gffhs.generateMaskedRow(null);
            assertTrue(ArrayUtils.contains(keysArray, output));
            runTimes--;
        }
    }

    @Test
    public void testSeparatorLinux() throws URISyntaxException {
        GenerateFromFileHashString gffhs = new GenerateFromFileHashString();
        final String linuxFilePath = this.getClass().getResource("data/last_names.csv").toURI().getPath(); //$NON-NLS-1$
        gffhs.parse(linuxFilePath, false);
        gffhs.setRandom(new Random(42));
        int runTimes = 5;
        String[] keysArray = new String[] { "Mendez", "Slaven", "Posner", "Rosemont", "Wyllie" };
        while (runTimes > 0) {
            output = gffhs.generateMaskedRow(null);
            assertTrue(ArrayUtils.contains(keysArray, output));
            runTimes--;
        }
    }

    public void testNull() throws URISyntaxException {
        GenerateFromFileHashString gffhs = new GenerateFromFileHashString();
        final String path = this.getClass().getResource("data/name.txt").toURI().getPath(); //$NON-NLS-1$
        gffhs.parse(path, false);
        gffhs.keepNull = true;
        output = gffhs.generateMaskedRow(null);
        assertNull(output);
    }
}
