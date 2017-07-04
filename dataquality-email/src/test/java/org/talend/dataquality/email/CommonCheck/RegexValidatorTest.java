package org.talend.dataquality.email.CommonCheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Assert;
import org.junit.Test;

public class RegexValidatorTest {

    // Regular expression strings for hostnames (derived from RFC2396 and RFC 1123)
    private static final String DOMAIN_LABEL_REGEX = "\\p{Alnum}(?>[\\p{Alnum}-]*\\p{Alnum})*";

    @Test
    public void testisValid() {
        RegexValidator regexValid = new RegexValidator(DOMAIN_LABEL_REGEX);

        String email = "ab.c@sina.com";
        assertFalse(regexValid.isValid(email));
        String email2 = "ab.c@yahoo.com";
        assertFalse(regexValid.isValid(email2));
    }

    @Test
    public void testCreate() {
        try {
            String[] nullStr = null;
            RegexValidator regexValid = new RegexValidator(nullStr, true);
        } catch (Exception e) {
            assertEquals("Regular expressions are missing", e.getMessage());
            return;
        }
        Assert.fail("Should throw exception");
    }

    @Test
    public void testValidate() {
        RegexValidator regexValid = new RegexValidator(DOMAIN_LABEL_REGEX);

        assertEquals("", regexValid.validate("aabb"));
        assertEquals(null, regexValid.validate("sina.com"));
        assertEquals(null, regexValid.validate(null));
    }

    @Test
    public void testToString() {
        RegexValidator regexValid = new RegexValidator("\\a(?>)*");
        assertEquals("RegexValidator{\\a(?>)*}", regexValid.toString());
    }
}
