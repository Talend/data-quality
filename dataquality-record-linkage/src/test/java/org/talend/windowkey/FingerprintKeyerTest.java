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
package org.talend.windowkey;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * created by scorreia on Jul 10, 2012 Detailled comment
 * 
 */
public class FingerprintKeyerTest {

    @SuppressWarnings("nls")
    private static final String testStr[][] = { { "Acorn", "acorn" }, { "Woodpecker", "woodpecker" },
            { "Bird Conservation Region", "bird conservation region" }, { "15", "15" }, { "PT-r2", "ptr2" },
            { "élément", "element" }, { "32 €", "32 €" }, { "Acorn", "acorn" }, { "Aret Big Cust", "aret big cust" },
            { "Big Arêt Cust", "aret big cust" }, { "Cust Aret Big ", "aret big cust" }, { "Cust-Aret Big ", "big custaret" },
            { "Big Data for big business", "big business data for" }, { "Data for big business", "big business data for" },
            { "A A A", "a" }, { "I.BM.", "ibm" }, { "I.B.M.", "ibm" }, { "IBM", "ibm" },
            { "Bird Conservation Region", "bird conservation region" },
            { "Bird bird Conservation Region", "bird conservation region" }, { "15", "15" }, { "PT-r2", "ptr2" },
            { "élément", "element" }, { "32 €", "32 €" } };

    @SuppressWarnings("nls")
    private static final String asciiTestStr[][] = { { "Acorn", "Acorn" }, { "Woodpecker", "Woodpecker" },
            { "Bird Conservation Region", "Bird Conservation Region" }, { "15", "15" }, { "PT-r2", "PT-r2" },
            { "élément", "element" }, { "32 €", "32 €" } };

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.contribs.algorithm.FingerprintKeyer#key(java.lang.String)}.
     */
    @Test
    public void testKey() {
        FingerprintKeyer keyer = new FingerprintKeyer();
        for (String[] element : testStr) {
            assertEquals(element[1], keyer.key(element[0]));
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.contribs.algorithm.FingerprintKeyer#asciify(java.lang.String)}.
     */
    @Test
    public void testAsciify() {
        FingerprintKeyer keyer = new FingerprintKeyer();
        for (String[] element : asciiTestStr) {
            assertEquals(element[1], keyer.asciify(element[0]));
        }
    }

}
