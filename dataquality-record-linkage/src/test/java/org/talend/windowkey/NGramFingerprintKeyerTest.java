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
public class NGramFingerprintKeyerTest {

    @SuppressWarnings("nls")
    private static final String[][] testStr = { { "Acorn", "accoorrn" }, { "Aret Big Cust", "arbicuetgcigresttbus" },
            { "Big Arêt Cust", "arbicugaigresttcuset" }, { "Cust Aret Big ", "arbicuetigresttatbus" },
            { "Cust-Aret Big ", "arbicuetigresttatbus" }, { "Big Data for big business", "afatbibudaesfogbgdiginneorrbsisstaus" },
            { "Data for big business", "afatbibudaesfogbiginneorrbsisstaus" }, { "A A A", "aa" }, { "I.BM.", "bmib" },
            { "I.B.M.", "bmib" }, { "IBM", "bmib" }, { "Bird Conservation Region", "atbicodcegergiioirnrnsonrdrervsetiva" },
            { "Bird bird Conservation Region", "atbicodbdcegergiioirnrnsonrdrervsetiva" }, { "15", "15" }, { "PT-r2", "ptr2tr" },
            { "élément", "enlementelem" }, { "32 €", "2€32" } };

    @SuppressWarnings("nls")
    private static final String[][] test3gram = { { "Acorn", "acocororn" }, { "Aret Big Cust", "arebigcusetbgcuigcrettbiust" },
            { "Big Arêt Cust", "arebigcusgarigarettcuustetc" }, { "Cust Aret Big ", "arebigcusetbretstatartbiust" },
            { "Cust-Aret Big ", "arebigcusetbretstatartbiust" },
            { "Big Data for big business", "afoatabigbusdatessforgbugdaigbigdinenesorbrbisintafusi" },
            { "Data for big business", "afoatabigbusdatessforgbuigbinenesorbrbisintafusi" }, { "A A A", "aaa" },
            { "I.BM.", "ibm" }, { "I.B.M.", "ibm" }, { "IBM", "ibm" },
            { "Bird Conservation Region", "atibircondcoegiervgioionirdnrenseonronsrdcregrvasertiovat" },
            { "Bird bird Conservation Region", "atibircondbidcoegiervgioionirdnrenseonronsrdbrdcregrvasertiovat" }, { "15", "" },
            { "PT-r2", "ptrtr2" }, { "élément", "entlemmeneleeme" }, { "32 €", "32€" } };

    /**
     * Test method for {@link org.talend.windowkey.NGramFingerprintKeyer#key(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testKeyStringObjectArray() {
        NGramFingerprintKeyer keyer = new NGramFingerprintKeyer();
        for (String[] element : test3gram) {
            // System.out.println("{\"" + element[0] + "\",\"" + keyer.key(element[0], 3) + "\"},");
            assertEquals(element[1], keyer.key(element[0], 3));
        }
    }

    /**
     * Test method for {@link org.talend.windowkey.NGramFingerprintKeyer#key(java.lang.String)}.
     */
    @Test
    public void testKeyString() {
        NGramFingerprintKeyer keyer = new NGramFingerprintKeyer();
        for (String[] element : testStr) {
            // System.out.println("{\"" + element[0] + "\",\"" + keyer.key(element[0]) + "\"},");
            assertEquals(element[1], keyer.key(element[0]));
        }
    }

}
