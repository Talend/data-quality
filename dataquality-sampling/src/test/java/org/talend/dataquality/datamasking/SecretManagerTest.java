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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.talend.dataquality.datamasking.utils.crypto.AesPrf;
import org.talend.dataquality.datamasking.utils.crypto.CipherParameterChecker;
import org.talend.dataquality.datamasking.utils.crypto.HmacPrf;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;

public class SecretManagerTest {

    @Test(expected = IllegalStateException.class)
    public void getPRFWhenMethodIsNull() {
        SecretManager secMng = new SecretManager();
        secMng.getPseudoRandomFunction();
    }

    @Test(expected = IllegalStateException.class)
    public void getPRFWhenMethodIsBasic() {
        SecretManager secMng = new SecretManager(FormatPreservingMethod.BASIC, "Password");
        secMng.getPseudoRandomFunction();
    }

    @Test
    public void getPRFWithEachMethod() {
        assumeTrue(CipherParameterChecker.IS_AES256_SUPPORTED);
        SecretManager secMng = new SecretManager(FormatPreservingMethod.AES_CBC_PRF, "Password");
        PseudoRandomFunction prf = secMng.getPseudoRandomFunction();
        assertTrue("The PRF is not of the correct type (AesPrf) !", prf instanceof AesPrf);

        secMng = new SecretManager(FormatPreservingMethod.SHA2_HMAC_PRF, "Password");
        prf = secMng.getPseudoRandomFunction();
        assertTrue("The PRF is not of the correct type (HmacPrf) !", prf instanceof HmacPrf);
    }

    @Test
    public void latinPasswordWithNumbers() {
        SecretManager secMng = new SecretManager(FormatPreservingMethod.SHA2_HMAC_PRF, "ARandomPassword921");
        byte[] res = secMng.getPseudoRandomFunction().apply("abcde".getBytes());

        String expected = "6a4fb8b769575e2f30658e56d1c632e076c8e7e325d9c7939eabdc9d455ddb43";
        assertEquals(expected, Hex.encodeHexString(res));
    }

    @Test
    public void passwordWithSpecialChars() {
        SecretManager secMng = new SecretManager(FormatPreservingMethod.SHA2_HMAC_PRF, "Pa$$_With%Spe{ial_Ch@rs");
        byte[] res = secMng.getPseudoRandomFunction().apply("abcde".getBytes());

        String expected = "8b40b26aac1b24d20927872342f5d75002b942817779f2b2b7de5ffa8813d15f";
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
        assumeTrue(CipherParameterChecker.IS_AES256_SUPPORTED);
        SecretManager secMng = new SecretManager(FormatPreservingMethod.AES_CBC_PRF, null);
        // AES supports only multiples of 16-byte inputs
        byte[] input = new byte[16];
        new Random(123456).nextBytes(input);
        byte[] res = secMng.getPseudoRandomFunction().apply(input);
        assertNotNull(res);
    }

}
