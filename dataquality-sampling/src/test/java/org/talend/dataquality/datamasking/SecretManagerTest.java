package org.talend.dataquality.datamasking;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SecretManagerTest {

    SecretManager secMng = new SecretManager();

    @Test
    public void latin_password_with_numbers() {
        secMng.setPrfAlgo(2);
        secMng.setPassword("ARandomPassword921");
        byte[] res = secMng.getPseudoRandomFunction().apply("abcde".getBytes());

        // Result obtained at https://www.freeformatter.com/hmac-generator.html#ad-output with SHA256 algorithm.
        assertEquals("c5cd0a933b2f329874e885c3614524042338648757a44a4bd71b3a15084bebd3", Hex.encodeHexString(res));
    }

    @Test
    public void password_with_special_chars() {
        secMng.setPrfAlgo(2);
        secMng.setPassword("Pa$$_With%Spe{ial_Ch@rs");
        byte[] res = secMng.getPseudoRandomFunction().apply("abcde".getBytes());

        // Result obtained at https://www.freeformatter.com/hmac-generator.html#ad-output with SHA256 algorithm.
        assertEquals("51e22ba9968590ed4ef65837176b8e45a01a61a289c034a9041dc4f38f731b62", Hex.encodeHexString(res));
    }

    @Test
    public void get_prf_when_password_not_set() {
        secMng = new SecretManager();
        secMng.setPrfAlgo(2);
        byte[] res = secMng.getPseudoRandomFunction().apply("something".getBytes());
        assertNotNull(res);
    }
}
