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
package org.talend.dataquality.standardization.phone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.google.i18n.phonenumbers.PhoneNumberToTimeZonesMapper;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class PhoneNumberHandlerBaseTest {

    private String FR_NUM_1 = "+33656965822"; //$NON-NLS-1$

    private String FR_NUM_2 = "+33(0)147554323"; //$NON-NLS-1$

    private String FR_NUM_3 = "000147554323"; //$NON-NLS-1$

    private String FR_NUM_4 = "00(0)147554323"; //$NON-NLS-1$

    private String FR_NUM_5 = "0662965822"; //$NON-NLS-1$

    private String US_NUM_1 = "+1-541-754-3010"; //$NON-NLS-1$

    private String US_NUM_2 = "1-541-754-3010"; //$NON-NLS-1$

    private String US_NUM_3 = "001-541-754-3010"; //$NON-NLS-1$

    private String US_NUM_4 = "(541) 754-3010"; //$NON-NLS-1$

    private String US_NUM_5 = "754-3010"; //$NON-NLS-1$

    private String US_NUM_6 = "191 541 754 3010"; //$NON-NLS-1$

    private String US_NUM_7 = "(724) 203-2300"; //$NON-NLS-1$

    private String DE_NUM_1 = "+49-89-636-48018"; //$NON-NLS-1$

    private String DE_NUM_2 = "19-49-89-636-48018"; //$NON-NLS-1$

    private String DE_NUM_3 = "(089) / 636-48018"; //$NON-NLS-1$

    private String CN_NUM_1 = "18611281173"; //$NON-NLS-1$

    private String CN_NUM_2 = "13521588310"; //$NON-NLS-1$

    private String CN_NUM_3 = "1065267475"; //$NON-NLS-1$

    private String CN_NUM_4 = "07927234582"; //$NON-NLS-1$

    private String DE_NUM_4 = "636-48018"; //$NON-NLS-1$

    private String REGCODE_FR = "FR"; //$NON-NLS-1$

    private String REGCODE_US = "US"; //$NON-NLS-1$

    private String REGCODE_DE = "DE"; //$NON-NLS-1$

    private String REGCODE_CN = "CN"; //$NON-NLS-1$

    private PhoneNumberHandlerBase phoneNumberHandlerBase = new PhoneNumberHandlerBase();

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#isValidPhoneNumber(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testIsValidPhoneNumber() {
        assertFalse(phoneNumberHandlerBase.isValidPhoneNumber(null, REGCODE_FR));
        assertTrue(phoneNumberHandlerBase.isValidPhoneNumber(FR_NUM_1, REGCODE_FR));
        assertTrue(phoneNumberHandlerBase.isValidPhoneNumber(FR_NUM_2, REGCODE_FR));
        assertTrue(phoneNumberHandlerBase.isValidPhoneNumber(FR_NUM_5, REGCODE_FR));
        assertFalse(phoneNumberHandlerBase.isValidPhoneNumber(FR_NUM_3, REGCODE_FR));
        assertFalse(phoneNumberHandlerBase.isValidPhoneNumber(FR_NUM_4, REGCODE_FR));

        assertTrue(phoneNumberHandlerBase.isValidPhoneNumber(US_NUM_1, REGCODE_US));
        assertTrue(phoneNumberHandlerBase.isValidPhoneNumber(US_NUM_2, REGCODE_US));
        assertTrue(phoneNumberHandlerBase.isValidPhoneNumber(US_NUM_4, REGCODE_US));
        assertTrue(phoneNumberHandlerBase.isValidPhoneNumber(US_NUM_7, REGCODE_US));
        assertFalse(phoneNumberHandlerBase.isValidPhoneNumber(US_NUM_3, REGCODE_US));
        assertFalse(phoneNumberHandlerBase.isValidPhoneNumber(US_NUM_6, REGCODE_US));
        assertFalse(phoneNumberHandlerBase.isValidPhoneNumber(US_NUM_5, REGCODE_US));

        assertTrue(phoneNumberHandlerBase.isValidPhoneNumber(DE_NUM_1, REGCODE_DE));
        assertTrue(phoneNumberHandlerBase.isValidPhoneNumber(DE_NUM_3, REGCODE_DE));
        assertTrue(phoneNumberHandlerBase.isValidPhoneNumber(DE_NUM_4, REGCODE_DE));
        assertFalse(phoneNumberHandlerBase.isValidPhoneNumber(DE_NUM_2, REGCODE_DE));

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#parseToPhoneNumber(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testParseToPhoneNumber() {
        assertNull(phoneNumberHandlerBase.parseToPhoneNumber(null, REGCODE_FR));
        assertNull(phoneNumberHandlerBase.parseToPhoneNumber("", REGCODE_FR)); //$NON-NLS-1$
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_1, null));

        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_1, REGCODE_FR));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_2, REGCODE_FR));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_5, REGCODE_FR));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_3, REGCODE_FR));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_4, REGCODE_FR));

        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(US_NUM_1, REGCODE_US));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(US_NUM_2, REGCODE_US));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(US_NUM_4, REGCODE_US));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(US_NUM_7, REGCODE_US));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(US_NUM_3, REGCODE_US));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(US_NUM_6, REGCODE_US));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(US_NUM_5, REGCODE_US));

        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(DE_NUM_1, REGCODE_DE));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(DE_NUM_3, REGCODE_DE));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(DE_NUM_4, REGCODE_DE));
        assertNotNull(phoneNumberHandlerBase.parseToPhoneNumber(DE_NUM_2, REGCODE_DE));

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#isPossiblePhoneNumber(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testIsPossiblePhoneNumber() {
        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_1, null));
        assertFalse(phoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_3, null));
        assertFalse(phoneNumberHandlerBase.isPossiblePhoneNumber(null, REGCODE_FR));
        assertFalse(phoneNumberHandlerBase.isPossiblePhoneNumber("", REGCODE_FR));

        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_1, REGCODE_FR));
        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_2, REGCODE_FR));
        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_5, REGCODE_FR));
        assertFalse(phoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_3, REGCODE_FR));
        assertFalse(phoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_4, REGCODE_FR));

        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_1, REGCODE_US));
        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_2, REGCODE_US));
        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_4, REGCODE_US));
        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_7, REGCODE_US));
        assertFalse(phoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_3, REGCODE_US));
        assertFalse(phoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_6, REGCODE_US));

        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_5, REGCODE_US));

        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(DE_NUM_1, REGCODE_DE));
        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(DE_NUM_3, REGCODE_DE));
        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(DE_NUM_4, REGCODE_DE));

        assertTrue(phoneNumberHandlerBase.isPossiblePhoneNumber(DE_NUM_2, REGCODE_DE));

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#formatE164(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatE164() {
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.formatE164(null, REGCODE_FR)); // $NON-NLS-1$
        assertEquals("+33656965822", phoneNumberHandlerBase.formatE164(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33147554323", phoneNumberHandlerBase.formatE164(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33662965822", phoneNumberHandlerBase.formatE164(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("+15417543010", phoneNumberHandlerBase.formatE164(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+15417543010", phoneNumberHandlerBase.formatE164(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+15417543010", phoneNumberHandlerBase.formatE164(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("+498963648018", phoneNumberHandlerBase.formatE164(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+4919498963648018", phoneNumberHandlerBase.formatE164(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+498963648018", phoneNumberHandlerBase.formatE164(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#formatInternational(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatInternational() {
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.formatInternational(null, REGCODE_FR)); // $NON-NLS-1$

        assertEquals("+33 6 56 96 58 22", phoneNumberHandlerBase.formatInternational(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33 1 47 55 43 23", phoneNumberHandlerBase.formatInternational(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33 6 62 96 58 22", phoneNumberHandlerBase.formatInternational(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("+1 541-754-3010", phoneNumberHandlerBase.formatInternational(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+1 541-754-3010", phoneNumberHandlerBase.formatInternational(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+1 541-754-3010", phoneNumberHandlerBase.formatInternational(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("+49 89 63648018", phoneNumberHandlerBase.formatInternational(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+49 19498963648018", phoneNumberHandlerBase.formatInternational(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+49 89 63648018", phoneNumberHandlerBase.formatInternational(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#formatNational(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatNational() {
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.formatNational(null, REGCODE_FR)); // $NON-NLS-1$

        assertEquals("06 56 96 58 22", phoneNumberHandlerBase.formatNational(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("01 47 55 43 23", phoneNumberHandlerBase.formatNational(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("06 62 96 58 22", phoneNumberHandlerBase.formatNational(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("(541) 754-3010", phoneNumberHandlerBase.formatNational(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("(541) 754-3010", phoneNumberHandlerBase.formatNational(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("(541) 754-3010", phoneNumberHandlerBase.formatNational(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("089 63648018", phoneNumberHandlerBase.formatNational(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("19498963648018", phoneNumberHandlerBase.formatNational(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("089 63648018", phoneNumberHandlerBase.formatNational(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#formatRFC396(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatRFC396() {
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.formatRFC396(null, REGCODE_FR)); // $NON-NLS-1$

        assertEquals("tel:+33-6-56-96-58-22", phoneNumberHandlerBase.formatRFC396(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("tel:+33-1-47-55-43-23", phoneNumberHandlerBase.formatRFC396(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("tel:+33-6-62-96-58-22", phoneNumberHandlerBase.formatRFC396(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("tel:+1-541-754-3010", phoneNumberHandlerBase.formatRFC396(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("tel:+1-541-754-3010", phoneNumberHandlerBase.formatRFC396(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("tel:+1-541-754-3010", phoneNumberHandlerBase.formatRFC396(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("tel:+49-89-63648018", phoneNumberHandlerBase.formatRFC396(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("tel:+49-19498963648018", phoneNumberHandlerBase.formatRFC396(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("tel:+49-89-63648018", phoneNumberHandlerBase.formatRFC396(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$

    }

    /**
     * Test method for {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#getSupportedRegions()} .
     */
    @Test
    public void testGetSupportedRegions() {
        assertEquals(244, phoneNumberHandlerBase.getSupportedRegions().size());

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#getCountryCodeForRegion(java.lang.String)} .
     */
    @Test
    public void testExtractCountryCode() {

        assertEquals(33, phoneNumberHandlerBase.extractCountrycode(FR_NUM_1));
        assertEquals(33, phoneNumberHandlerBase.extractCountrycode(FR_NUM_2));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(FR_NUM_5));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(FR_NUM_3));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(FR_NUM_4));

        assertEquals(1, phoneNumberHandlerBase.extractCountrycode(US_NUM_1));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(US_NUM_2));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(US_NUM_4));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(US_NUM_7));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(US_NUM_3));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(US_NUM_6));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(US_NUM_5));

        assertEquals(49, phoneNumberHandlerBase.extractCountrycode(DE_NUM_1));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(DE_NUM_3));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(DE_NUM_4));
        assertEquals(0, phoneNumberHandlerBase.extractCountrycode(DE_NUM_2));

    }

    @Test
    public void testEtractRegionCode() {
        assertEquals("FR", phoneNumberHandlerBase.extractRegionCode(FR_NUM_1)); //$NON-NLS-1$
        assertEquals("FR", phoneNumberHandlerBase.extractRegionCode(FR_NUM_2)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(FR_NUM_5));
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(FR_NUM_3));
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(FR_NUM_4));

        assertEquals("US", phoneNumberHandlerBase.extractRegionCode(US_NUM_1)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(US_NUM_2));
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(US_NUM_4));
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(US_NUM_7));
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(US_NUM_3));
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(US_NUM_6));
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(US_NUM_5));

        assertEquals("DE", phoneNumberHandlerBase.extractRegionCode(DE_NUM_1)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(DE_NUM_3));
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(DE_NUM_4));
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.extractRegionCode(DE_NUM_2));

    }

    @Test
    public void testGetCarrierNameForNumber() {
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.getCarrierNameForNumber(null, REGCODE_US, Locale.UK));

        assertEquals("中国联通", phoneNumberHandlerBase.getCarrierNameForNumber(CN_NUM_1, REGCODE_CN, Locale.SIMPLIFIED_CHINESE));
        assertEquals("China Unicom", phoneNumberHandlerBase.getCarrierNameForNumber(CN_NUM_1, REGCODE_CN, Locale.UK)); //$NON-NLS-1$
        assertEquals("China Mobile", phoneNumberHandlerBase.getCarrierNameForNumber(CN_NUM_2, REGCODE_CN, Locale.UK)); //$NON-NLS-1$

        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.getCarrierNameForNumber(FR_NUM_3, REGCODE_FR, Locale.UK));
        assertEquals("Bouygues", phoneNumberHandlerBase.getCarrierNameForNumber(FR_NUM_5, REGCODE_FR, Locale.UK)); //$NON-NLS-1$
        assertEquals("Bouygues", phoneNumberHandlerBase.getCarrierNameForNumber(FR_NUM_5, REGCODE_FR, Locale.FRENCH)); //$NON-NLS-1$

        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.getCarrierNameForNumber(US_NUM_1, REGCODE_US, Locale.UK));

        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.getCarrierNameForNumber(DE_NUM_1, REGCODE_DE, Locale.UK));
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.getCarrierNameForNumber(DE_NUM_1, REGCODE_DE, Locale.GERMANY));

    }

    @Test
    public void testgetGeocoderDescriptionForNumber() {
        assertEquals(StringUtils.EMPTY, // $NON-NLS-1$
                phoneNumberHandlerBase.getGeocoderDescriptionForNumber(null, REGCODE_CN, Locale.SIMPLIFIED_CHINESE));

        assertEquals("北京市", //$NON-NLS-1$
                phoneNumberHandlerBase.getGeocoderDescriptionForNumber(CN_NUM_3, REGCODE_CN, Locale.SIMPLIFIED_CHINESE));
        assertEquals("Beijing", phoneNumberHandlerBase.getGeocoderDescriptionForNumber(CN_NUM_3, REGCODE_CN, Locale.UK)); //$NON-NLS-1$ //
        assertEquals("江西省九江市", //$NON-NLS-1$
                phoneNumberHandlerBase.getGeocoderDescriptionForNumber(CN_NUM_4, REGCODE_CN, Locale.SIMPLIFIED_CHINESE));
        assertEquals("Jiujiang, Jiangxi", //$NON-NLS-1$
                phoneNumberHandlerBase.getGeocoderDescriptionForNumber(CN_NUM_4, REGCODE_CN, Locale.UK));

        assertEquals("France", phoneNumberHandlerBase.getGeocoderDescriptionForNumber(FR_NUM_1, REGCODE_FR, Locale.FRANCE)); //$NON-NLS-1$
        assertEquals("Paris", phoneNumberHandlerBase.getGeocoderDescriptionForNumber(FR_NUM_2, REGCODE_FR, Locale.FRANCE)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY,
                phoneNumberHandlerBase.getGeocoderDescriptionForNumber(FR_NUM_3, REGCODE_FR, Locale.FRANCE));
        assertEquals(StringUtils.EMPTY,
                phoneNumberHandlerBase.getGeocoderDescriptionForNumber(FR_NUM_4, REGCODE_FR, Locale.FRANCE));
        assertEquals("France", phoneNumberHandlerBase.getGeocoderDescriptionForNumber(FR_NUM_5, REGCODE_FR, Locale.FRANCE)); //$NON-NLS-1$

        assertEquals("Corvallis, OR", phoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_1, REGCODE_US, Locale.US)); //$NON-NLS-1$
        assertEquals("Corvallis, OR", phoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_2, REGCODE_US, Locale.US)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_3, REGCODE_US, Locale.US));
        assertEquals("Corvallis, OR", phoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_4, REGCODE_US, Locale.US)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_5, REGCODE_US, Locale.US));
        assertEquals(StringUtils.EMPTY, phoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_6, REGCODE_US, Locale.US));
        assertEquals("Pennsylvania", phoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_7, REGCODE_US, Locale.US)); //$NON-NLS-1$

        assertEquals("München", phoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_1, REGCODE_DE, Locale.GERMANY)); //$NON-NLS-1$
        assertEquals("Munich", phoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_1, REGCODE_DE, Locale.ENGLISH)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY,
                phoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_2, REGCODE_DE, Locale.GERMANY));
        assertEquals("München", phoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_3, REGCODE_DE, Locale.GERMANY)); //$NON-NLS-1$
        assertEquals("Nußbach Pfalz", //$NON-NLS-1$
                phoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_4, REGCODE_DE, Locale.GERMANY));
        assertEquals("Nussbach Pfalz", //$NON-NLS-1$
                phoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_4, REGCODE_DE, Locale.ENGLISH));

    }

    @Test
    public void testGetCountryCodeForRegion() {
        assertEquals(0, phoneNumberHandlerBase.getCountryCodeForRegion(null));
        assertEquals(0, phoneNumberHandlerBase.getCountryCodeForRegion(StringUtils.EMPTY));
        assertEquals(33, phoneNumberHandlerBase.getCountryCodeForRegion("FR")); //$NON-NLS-1$
        assertEquals(1, phoneNumberHandlerBase.getCountryCodeForRegion("US")); //$NON-NLS-1$
        assertEquals(86, phoneNumberHandlerBase.getCountryCodeForRegion("CN")); //$NON-NLS-1$

    }

    @Test
    public void testGetTimeZonesForNumber() {
        assertEquals(PhoneNumberToTimeZonesMapper.getUnknownTimeZone(),
                phoneNumberHandlerBase.getTimeZonesForNumber(null, null).get(0)); // $NON-NLS-1$

        assertEquals(2, phoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_1, REGCODE_CN).size());
        assertEquals("[Asia/Shanghai, Asia/Urumqi]", //$NON-NLS-1$
                phoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_1, REGCODE_CN).toString());
        assertEquals("[Asia/Shanghai, Asia/Urumqi]", //$NON-NLS-1$
                phoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_2, REGCODE_CN).toString());
        assertEquals("[Asia/Shanghai]", phoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_4, REGCODE_CN).toString()); //$NON-NLS-1$

        assertEquals(1, phoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_1, REGCODE_FR).size());
        assertEquals("[Europe/Paris]", phoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_1, REGCODE_FR).toString()); //$NON-NLS-1$
        assertEquals("[Europe/Paris]", phoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_2, REGCODE_FR).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", phoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_3, REGCODE_FR).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", phoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_4, REGCODE_FR).toString()); //$NON-NLS-1$
        assertEquals("[Europe/Paris]", phoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_5, REGCODE_FR).toString()); //$NON-NLS-1$

        assertEquals("[America/Los_Angeles]", phoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_1, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[America/Los_Angeles]", phoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_2, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", phoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_3, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[America/Los_Angeles]", phoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_4, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", phoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_5, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", phoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_6, REGCODE_US).toString()); //$NON-NLS-1$

        assertEquals("[Europe/Berlin]", phoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_1, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", phoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_2, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", phoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_3, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", phoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_4, REGCODE_US).toString()); //$NON-NLS-1$

    }

    @Test
    public void testGetPhoneNumberType() {
        assertEquals(PhoneNumberTypeEnum.MOBILE, phoneNumberHandlerBase.getPhoneNumberType(FR_NUM_5, REGCODE_FR));
        assertEquals(PhoneNumberTypeEnum.UNKNOWN, phoneNumberHandlerBase.getPhoneNumberType(DE_NUM_4, REGCODE_US));
        assertEquals(PhoneNumberTypeEnum.FIXED_LINE, phoneNumberHandlerBase.getPhoneNumberType(CN_NUM_3, REGCODE_CN));
    }
}
