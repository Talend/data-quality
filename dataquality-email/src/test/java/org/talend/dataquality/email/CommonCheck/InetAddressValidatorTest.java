package org.talend.dataquality.email.CommonCheck;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InetAddressValidatorTest {

    @Test
    public void testisValid() {
        assertFalse(InetAddressValidator.getInstance().isValid(null));
        assertFalse(InetAddressValidator.getInstance().isValid(""));
        assertFalse(InetAddressValidator.getInstance().isValid("ab.c@yahoo.com"));
        assertTrue(InetAddressValidator.getInstance().isValid("192.168.3.10"));
    }

}
