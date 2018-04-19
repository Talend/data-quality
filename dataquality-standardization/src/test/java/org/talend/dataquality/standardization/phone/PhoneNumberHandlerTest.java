package org.talend.dataquality.standardization.phone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class PhoneNumberHandlerTest {

    private String US_NUM_1 = "+1-541-754-3010"; //$NON-NLS-1$

    private String US_NUM_2 = "1-541-754-3010"; //$NON-NLS-1$

    private String US_NUM_3 = "001-541-754-3010"; //$NON-NLS-1$

    private PhoneNumberHandler handler;

    @Before
    public void setUp() {
        handler = new PhoneNumberHandler();
        handler.setDefaultRegion("US"); //$NON-NLS-1$
        handler.setDefaultLocale(Locale.ENGLISH);
    }

    @Test
    public void testIsValidPhoneNumber() {
        assertFalse(handler.isValidPhoneNumber(null));
        assertTrue(handler.isValidPhoneNumber(US_NUM_1));
        assertTrue(handler.isValidPhoneNumber(US_NUM_2));
        assertFalse(handler.isValidPhoneNumber(US_NUM_3));
    }

    @Test
    public void testIsPossiblePhoneNumber() {
        assertFalse(handler.isPossiblePhoneNumber(null));
        assertTrue(handler.isPossiblePhoneNumber(US_NUM_1));
        assertTrue(handler.isPossiblePhoneNumber(US_NUM_2));
        assertFalse(handler.isPossiblePhoneNumber(US_NUM_3));

    }

    @Test
    public void testFormatE164() {
        assertEquals(StringUtils.EMPTY, handler.formatE164(null));
        assertEquals("+15417543010", handler.formatE164(US_NUM_1)); //$NON-NLS-1$
        assertEquals("+15417543010", handler.formatE164(US_NUM_2)); //$NON-NLS-1$
    }

    @Test
    public void testFormatInternational() {
        assertEquals(StringUtils.EMPTY, handler.formatInternational(null));

        assertEquals("+1 541-754-3010", handler.formatInternational(US_NUM_1)); //$NON-NLS-1$
        assertEquals("+1 541-754-3010", handler.formatInternational(US_NUM_2)); //$NON-NLS-1$

    }

    @Test
    public void testFormatNational() {
        assertEquals("(541) 754-3010", handler.formatNational(US_NUM_1)); //$NON-NLS-1$
        assertEquals("(541) 754-3010", handler.formatNational(US_NUM_2)); //$NON-NLS-1$
    }

    @Test
    public void testFormatRFC396() {
        assertEquals("tel:+1-541-754-3010", handler.formatRFC396(US_NUM_1)); //$NON-NLS-1$
        assertEquals("tel:+1-541-754-3010", handler.formatRFC396(US_NUM_2)); //$NON-NLS-1$
    }

    @Test
    public void testGetPhoneNumberType() {
        assertEquals(PhoneNumberTypeEnum.FIXED_LINE_OR_MOBILE, handler.getPhoneNumberType(US_NUM_2));
    }

    @Test
    public void testGetTimeZonesForNumber() {
        assertEquals("[America/Los_Angeles]", handler.getTimeZonesForNumber(US_NUM_1).toString()); //$NON-NLS-1$
        assertEquals("[America/Los_Angeles]", handler.getTimeZonesForNumber(US_NUM_2).toString()); //$NON-NLS-1$
    }

    @Test
    public void testGetGeocoderDescriptionForNumber() {
        assertEquals("Corvallis, OR", handler.getGeocoderDescriptionForNumber(US_NUM_1)); //$NON-NLS-1$
        assertEquals("Corvallis, OR", handler.getGeocoderDescriptionForNumber(US_NUM_2)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, handler.getGeocoderDescriptionForNumber(US_NUM_3));

    }

    @Test
    public void testGetCarrierNameForNumber() {
        assertEquals(StringUtils.EMPTY, handler.getCarrierNameForNumber(US_NUM_1));
    }

    @Test
    public void testGetDefaultLocale() {
        handler.setDefaultLocale(Locale.FRANCE);
        assertEquals(Locale.FRANCE, handler.getDefaultLocale());
    }

    @Test
    public void testGetDefaultRegion() {
        handler.setDefaultRegion("FR");
        assertEquals("FR", handler.getDefaultRegion());
    }
}
