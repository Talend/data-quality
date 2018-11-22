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

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.talend.dataquality.datamasking.utils.crypto.AesPrf;
import org.talend.dataquality.datamasking.utils.crypto.CryptoConstants;
import org.talend.dataquality.datamasking.utils.crypto.HmacPrf;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SecretManagerTest {

    @Test(expected = IllegalStateException.class)
    public void getPRFWhenMethodIsNull() {
        SecretManager secMng = new SecretManager();
        secMng.getPseudoRandomFunction();
    }

    @Test
    public void getPRFWithEachMethod() {
        SecretManager secMng = new SecretManager(FormatPreservingMethod.BASIC, "Password");
        PseudoRandomFunction prf = secMng.getPseudoRandomFunction();
        assertNull("The method is basic but the PRF returned is not null ! ", prf);

        secMng = new SecretManager(FormatPreservingMethod.AES_CBC_PRF, "Password");
        prf = secMng.getPseudoRandomFunction();
        assertTrue("The PRF is not of the correct type (AesPrf) !", prf instanceof AesPrf);

        secMng = new SecretManager(FormatPreservingMethod.SHA2_HMAC_PRF, "Password");
        prf = secMng.getPseudoRandomFunction();
        assertTrue("The PRF is not of the correct type (HmacPrf) !", prf instanceof HmacPrf);
    }

    @Test
    public void latinPasswordWithNumbers() {
        SecretManager secMng = new SecretManager(FormatPreservingMethod.SHA2_HMAC_PRF, "ARandomPassword921");
        byte[] res = secMng.getPseudoRandomFunction().apply("abcde".getBytes());

        String expected;
        if (CryptoConstants.KEY_LENGTH == 32) {
            expected = "c5cd0a933b2f329874e885c3614524042338648757a44a4bd71b3a15084bebd3";
        } else {
            expected = "7afdf81ff65d04dfa071e76ba6abdf21e7f5e04d69d614ed24e77a4fee326ebd";
        }
        assertEquals(expected, Hex.encodeHexString(res));
    }

    @Test
    public void passwordWithSpecialChars() {
        SecretManager secMng = new SecretManager(FormatPreservingMethod.SHA2_HMAC_PRF, "Pa$$_With%Spe{ial_Ch@rs");
        byte[] res = secMng.getPseudoRandomFunction().apply("abcde".getBytes());

        String expected;
        if (CryptoConstants.KEY_LENGTH == 32) {
            expected = "51e22ba9968590ed4ef65837176b8e45a01a61a289c034a9041dc4f38f731b62";
        } else {
            expected = "4a1d0087927696b419a94418478465ca5f70a3c2c588028ad7c599adc0707e48";
        }
        assertEquals(expected, Hex.encodeHexString(res));
    }

    @Test
    public void getPrfNoPasswordHMAC() {
        SecretManager secMng = new SecretManager(FormatPreservingMethod.SHA2_HMAC_PRF, null);
        byte[] res = secMng.getPseudoRandomFunction().apply("something".getBytes());
        assertNotNull(res);
    }

    @Test
    public void getPrfNoPasswordAES() {
        SecretManager secMng = new SecretManager(FormatPreservingMethod.AES_CBC_PRF, null);
        // AES supports only multiples of 16-byte inputs
        byte[] input = new byte[16];
        new Random(123456).nextBytes(input);
        byte[] res = secMng.getPseudoRandomFunction().apply(input);
        assertNotNull(res);
    }

}
