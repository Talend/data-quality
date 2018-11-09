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
package org.talend.dataquality.datamasking;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SecretManagerTest {

    @Test
    public void latinPasswordWithNumbers() {
        SecretManager secMng = new SecretManager(2, "ARandomPassword921");
        byte[] res = secMng.getPseudoRandomFunction().apply("abcde".getBytes());
        assertEquals("c5cd0a933b2f329874e885c3614524042338648757a44a4bd71b3a15084bebd3", Hex.encodeHexString(res));
    }

    @Test
    public void passwordWithSpecialChars() {
        SecretManager secMng = new SecretManager(2, "Pa$$_With%Spe{ial_Ch@rs");
        byte[] res = secMng.getPseudoRandomFunction().apply("abcde".getBytes());
        assertEquals("51e22ba9968590ed4ef65837176b8e45a01a61a289c034a9041dc4f38f731b62", Hex.encodeHexString(res));
    }

    @Test
    public void getPrfNoPasswordHMAC() {
        SecretManager secMng = new SecretManager(2, null);
        byte[] res = secMng.getPseudoRandomFunction().apply("something".getBytes());
        assertNotNull(res);
    }

    @Test
    public void getPrfNoPasswordAES() {
        SecretManager secMng = new SecretManager(1, null);
        // AES supports only multiples of 16-byte inputs
        byte[] input = new byte[16];
        new Random(123456).nextBytes(input);
        byte[] res = secMng.getPseudoRandomFunction().apply(input);
        assertNotNull(res);
    }

}
