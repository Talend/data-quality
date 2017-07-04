package org.talend.dataquality.email.CommonCheck;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EmailValidatorTest {

    @Test
    public void testgetInstance() {
        assertNotNull(EmailValidator.getInstance(true));
        assertNotNull(EmailValidator.getInstance(false));
    }

    @Test
    public void testIsValid() {
        assertTrue(EmailValidator.getInstance(true).isValid("aa"));
    }

    @Test
    public void testIsValidDomain() {
        assertTrue(EmailValidator.getInstance(true).isValidDomain("sina.com"));
        assertFalse(EmailValidator.getInstance(true).isValidDomain("222;11"));
    }

    @Test
    public void testIsValidUser() {
        assertTrue(EmailValidator.getInstance(true).isValidUser("sina.com"));
        assertFalse(EmailValidator.getInstance(true).isValidUser("111;22"));
    }

}
