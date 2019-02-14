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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.i18n.phonenumbers.PhoneNumberToTimeZonesMapper;
import com.google.i18n.phonenumbers.Phonenumber;

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

    private String JP_NUM_1 = "03-1258-2584"; //$NON-NLS-1$

    private String JP_NUM_2 = "8025879512"; //$NON-NLS-1$

    private String JP_NUM_3 = "0463-25-8888"; //$NON-NLS-1$

    private String JP_NUM_4 = "070-3333-3222"; //$NON-NLS-1$

    private String JP_NUM_5 = "052-2451-4455"; //$NON-NLS-1$

    private String DE_NUM_4 = "636-48018"; //$NON-NLS-1$

    private String REGCODE_FR = "FR"; //$NON-NLS-1$

    private String REGCODE_US = "US"; //$NON-NLS-1$

    private String REGCODE_DE = "DE"; //$NON-NLS-1$

    private String REGCODE_CN = "CN"; //$NON-NLS-1$

    private String REGCODE_JP = "JP"; //$NON-NLS-1$

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#isValidPhoneNumber(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testIsValidPhoneNumber() {
        assertFalse(PhoneNumberHandlerBase.isValidPhoneNumber(null, REGCODE_FR));
        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(FR_NUM_1, REGCODE_FR));
        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(FR_NUM_2, REGCODE_FR));
        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(FR_NUM_5, REGCODE_FR));
        assertFalse(PhoneNumberHandlerBase.isValidPhoneNumber(FR_NUM_3, REGCODE_FR));
        assertFalse(PhoneNumberHandlerBase.isValidPhoneNumber(FR_NUM_4, REGCODE_FR));

        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(US_NUM_1, REGCODE_US));
        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(US_NUM_2, REGCODE_US));
        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(US_NUM_4, REGCODE_US));
        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(US_NUM_7, REGCODE_US));
        assertFalse(PhoneNumberHandlerBase.isValidPhoneNumber(US_NUM_3, REGCODE_US));
        assertFalse(PhoneNumberHandlerBase.isValidPhoneNumber(US_NUM_6, REGCODE_US));
        assertFalse(PhoneNumberHandlerBase.isValidPhoneNumber(US_NUM_5, REGCODE_US));

        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(DE_NUM_1, REGCODE_DE));
        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(DE_NUM_3, REGCODE_DE));
        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(DE_NUM_4, REGCODE_DE));
        assertFalse(PhoneNumberHandlerBase.isValidPhoneNumber(DE_NUM_2, REGCODE_DE));

        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(JP_NUM_1, REGCODE_JP));
        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(JP_NUM_2, REGCODE_JP));
        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(JP_NUM_3, REGCODE_JP));
        assertTrue(PhoneNumberHandlerBase.isValidPhoneNumber(JP_NUM_4, REGCODE_JP));
        assertFalse(PhoneNumberHandlerBase.isValidPhoneNumber(JP_NUM_5, REGCODE_JP));

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#parseToPhoneNumber(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testParseToPhoneNumber() {
        assertNull(PhoneNumberHandlerBase.parseToPhoneNumber(null, REGCODE_FR));
        assertNull(PhoneNumberHandlerBase.parseToPhoneNumber("", REGCODE_FR)); //$NON-NLS-1$
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_1, null));

        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_1, REGCODE_FR));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_2, REGCODE_FR));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_5, REGCODE_FR));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_3, REGCODE_FR));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_4, REGCODE_FR));

        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(US_NUM_1, REGCODE_US));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(US_NUM_2, REGCODE_US));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(US_NUM_4, REGCODE_US));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(US_NUM_7, REGCODE_US));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(US_NUM_3, REGCODE_US));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(US_NUM_6, REGCODE_US));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(US_NUM_5, REGCODE_US));

        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(DE_NUM_1, REGCODE_DE));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(DE_NUM_3, REGCODE_DE));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(DE_NUM_4, REGCODE_DE));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(DE_NUM_2, REGCODE_DE));

        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(JP_NUM_1, REGCODE_JP));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(JP_NUM_2, REGCODE_JP));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(JP_NUM_3, REGCODE_JP));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(JP_NUM_4, REGCODE_JP));
        assertNotNull(PhoneNumberHandlerBase.parseToPhoneNumber(JP_NUM_5, REGCODE_JP));

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#isPossiblePhoneNumber(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testIsPossiblePhoneNumber() {
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_1, null));
        assertFalse(PhoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_3, null));
        assertFalse(PhoneNumberHandlerBase.isPossiblePhoneNumber(null, REGCODE_FR));
        assertFalse(PhoneNumberHandlerBase.isPossiblePhoneNumber("", REGCODE_FR));

        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_1, REGCODE_FR));
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_2, REGCODE_FR));
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_5, REGCODE_FR));
        assertFalse(PhoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_3, REGCODE_FR));
        assertFalse(PhoneNumberHandlerBase.isPossiblePhoneNumber(FR_NUM_4, REGCODE_FR));

        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_1, REGCODE_US));
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_2, REGCODE_US));
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_4, REGCODE_US));
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_7, REGCODE_US));
        assertFalse(PhoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_3, REGCODE_US));
        assertFalse(PhoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_6, REGCODE_US));

        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(US_NUM_5, REGCODE_US));

        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(DE_NUM_1, REGCODE_DE));
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(DE_NUM_3, REGCODE_DE));
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(DE_NUM_4, REGCODE_DE));

        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(DE_NUM_2, REGCODE_DE));

        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(JP_NUM_1, REGCODE_JP));
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(JP_NUM_2, REGCODE_JP));
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(JP_NUM_3, REGCODE_JP));
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(JP_NUM_4, REGCODE_JP));
        assertTrue(PhoneNumberHandlerBase.isPossiblePhoneNumber(JP_NUM_5, REGCODE_JP));

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#formatE164(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatE164() {
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.formatE164(null, REGCODE_FR)); // $NON-NLS-1$
        assertEquals("+33656965822", PhoneNumberHandlerBase.formatE164(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33147554323", PhoneNumberHandlerBase.formatE164(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33662965822", PhoneNumberHandlerBase.formatE164(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("+15417543010", PhoneNumberHandlerBase.formatE164(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+15417543010", PhoneNumberHandlerBase.formatE164(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+15417543010", PhoneNumberHandlerBase.formatE164(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("+498963648018", PhoneNumberHandlerBase.formatE164(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+4919498963648018", PhoneNumberHandlerBase.formatE164(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+498963648018", PhoneNumberHandlerBase.formatE164(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$

        assertEquals("+81312582584", PhoneNumberHandlerBase.formatE164(JP_NUM_1, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("+818025879512", PhoneNumberHandlerBase.formatE164(JP_NUM_2, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("+81463258888", PhoneNumberHandlerBase.formatE164(JP_NUM_3, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("+817033333222", PhoneNumberHandlerBase.formatE164(JP_NUM_4, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("+815224514455", PhoneNumberHandlerBase.formatE164(JP_NUM_5, REGCODE_JP)); //$NON-NLS-1$

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#formatInternational(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatInternational() {
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.formatInternational(null, REGCODE_FR)); // $NON-NLS-1$

        assertEquals("+33 6 56 96 58 22", PhoneNumberHandlerBase.formatInternational(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33 1 47 55 43 23", PhoneNumberHandlerBase.formatInternational(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33 6 62 96 58 22", PhoneNumberHandlerBase.formatInternational(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("+1 541-754-3010", PhoneNumberHandlerBase.formatInternational(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+1 541-754-3010", PhoneNumberHandlerBase.formatInternational(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+1 541-754-3010", PhoneNumberHandlerBase.formatInternational(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("+49 89 63648018", PhoneNumberHandlerBase.formatInternational(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+49 19498963648018", PhoneNumberHandlerBase.formatInternational(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+49 89 63648018", PhoneNumberHandlerBase.formatInternational(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$

        assertEquals("+81 3-1258-2584", PhoneNumberHandlerBase.formatInternational(JP_NUM_1, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("+81 80-2587-9512", PhoneNumberHandlerBase.formatInternational(JP_NUM_2, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("+81 463-25-8888", PhoneNumberHandlerBase.formatInternational(JP_NUM_3, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("+81 70-3333-3222", PhoneNumberHandlerBase.formatInternational(JP_NUM_4, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("+81 52-2451-4455", PhoneNumberHandlerBase.formatInternational(JP_NUM_5, REGCODE_JP)); //$NON-NLS-1$

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#formatNational(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatNational() {
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.formatNational(null, REGCODE_FR)); // $NON-NLS-1$

        assertEquals("06 56 96 58 22", PhoneNumberHandlerBase.formatNational(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("01 47 55 43 23", PhoneNumberHandlerBase.formatNational(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("06 62 96 58 22", PhoneNumberHandlerBase.formatNational(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("(541) 754-3010", PhoneNumberHandlerBase.formatNational(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("(541) 754-3010", PhoneNumberHandlerBase.formatNational(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("(541) 754-3010", PhoneNumberHandlerBase.formatNational(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("089 63648018", PhoneNumberHandlerBase.formatNational(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("19498963648018", PhoneNumberHandlerBase.formatNational(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("089 63648018", PhoneNumberHandlerBase.formatNational(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$

        assertEquals("03-1258-2584", PhoneNumberHandlerBase.formatNational(JP_NUM_1, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("080-2587-9512", PhoneNumberHandlerBase.formatNational(JP_NUM_2, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("0463-25-8888", PhoneNumberHandlerBase.formatNational(JP_NUM_3, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("070-3333-3222", PhoneNumberHandlerBase.formatNational(JP_NUM_4, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("052-2451-4455", PhoneNumberHandlerBase.formatNational(JP_NUM_5, REGCODE_JP)); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#formatRFC396(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatRFC396() {
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.formatRFC396(null, REGCODE_FR)); // $NON-NLS-1$

        assertEquals("tel:+33-6-56-96-58-22", PhoneNumberHandlerBase.formatRFC396(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("tel:+33-1-47-55-43-23", PhoneNumberHandlerBase.formatRFC396(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("tel:+33-6-62-96-58-22", PhoneNumberHandlerBase.formatRFC396(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("tel:+1-541-754-3010", PhoneNumberHandlerBase.formatRFC396(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("tel:+1-541-754-3010", PhoneNumberHandlerBase.formatRFC396(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("tel:+1-541-754-3010", PhoneNumberHandlerBase.formatRFC396(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("tel:+49-89-63648018", PhoneNumberHandlerBase.formatRFC396(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("tel:+49-19498963648018", PhoneNumberHandlerBase.formatRFC396(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("tel:+49-89-63648018", PhoneNumberHandlerBase.formatRFC396(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$

        assertEquals("tel:+81-3-1258-2584", PhoneNumberHandlerBase.formatRFC396(JP_NUM_1, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("tel:+81-80-2587-9512", PhoneNumberHandlerBase.formatRFC396(JP_NUM_2, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("tel:+81-463-25-8888", PhoneNumberHandlerBase.formatRFC396(JP_NUM_3, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("tel:+81-70-3333-3222", PhoneNumberHandlerBase.formatRFC396(JP_NUM_4, REGCODE_JP)); //$NON-NLS-1$
        assertEquals("tel:+81-52-2451-4455", PhoneNumberHandlerBase.formatRFC396(JP_NUM_5, REGCODE_JP)); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#getSupportedRegions()} .
     */
    @Test
    public void testGetSupportedRegions() {
        assertEquals(245, PhoneNumberHandlerBase.getSupportedRegions().size());

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandlerBase#getCountryCodeForRegion(java.lang.String)} .
     */
    @Test
    public void testExtractCountryCode() {

        assertEquals(33, PhoneNumberHandlerBase.extractCountrycode(FR_NUM_1));
        assertEquals(33, PhoneNumberHandlerBase.extractCountrycode(FR_NUM_2));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(FR_NUM_5));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(FR_NUM_3));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(FR_NUM_4));

        assertEquals(1, PhoneNumberHandlerBase.extractCountrycode(US_NUM_1));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(US_NUM_2));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(US_NUM_4));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(US_NUM_7));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(US_NUM_3));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(US_NUM_6));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(US_NUM_5));

        assertEquals(49, PhoneNumberHandlerBase.extractCountrycode(DE_NUM_1));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(DE_NUM_3));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(DE_NUM_4));
        assertEquals(0, PhoneNumberHandlerBase.extractCountrycode(DE_NUM_2));

    }

    @Test
    public void testEtractRegionCode() {
        assertEquals("FR", PhoneNumberHandlerBase.extractRegionCode(FR_NUM_1)); //$NON-NLS-1$
        assertEquals("FR", PhoneNumberHandlerBase.extractRegionCode(FR_NUM_2)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(FR_NUM_5));
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(FR_NUM_3));
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(FR_NUM_4));

        assertEquals("US", PhoneNumberHandlerBase.extractRegionCode(US_NUM_1)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(US_NUM_2));
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(US_NUM_4));
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(US_NUM_7));
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(US_NUM_3));
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(US_NUM_6));
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(US_NUM_5));

        assertEquals("DE", PhoneNumberHandlerBase.extractRegionCode(DE_NUM_1)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(DE_NUM_3));
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(DE_NUM_4));
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.extractRegionCode(DE_NUM_2));

    }

    @Test
    public void testGetCarrierNameForNumber() {
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.getCarrierNameForNumber(null, REGCODE_US, Locale.UK));

        assertEquals("中国联通", PhoneNumberHandlerBase.getCarrierNameForNumber(CN_NUM_1, REGCODE_CN, Locale.SIMPLIFIED_CHINESE));
        assertEquals("China Unicom", PhoneNumberHandlerBase.getCarrierNameForNumber(CN_NUM_1, REGCODE_CN, Locale.UK)); //$NON-NLS-1$
        assertEquals("China Mobile", PhoneNumberHandlerBase.getCarrierNameForNumber(CN_NUM_2, REGCODE_CN, Locale.UK)); //$NON-NLS-1$

        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.getCarrierNameForNumber(FR_NUM_3, REGCODE_FR, Locale.UK));
        assertEquals("Bouygues", PhoneNumberHandlerBase.getCarrierNameForNumber(FR_NUM_5, REGCODE_FR, Locale.UK)); //$NON-NLS-1$
        assertEquals("Bouygues", PhoneNumberHandlerBase.getCarrierNameForNumber(FR_NUM_5, REGCODE_FR, Locale.FRENCH)); //$NON-NLS-1$

        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.getCarrierNameForNumber(US_NUM_1, REGCODE_US, Locale.UK));

        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.getCarrierNameForNumber(DE_NUM_1, REGCODE_DE, Locale.UK));
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.getCarrierNameForNumber(DE_NUM_1, REGCODE_DE, Locale.GERMANY));

    }

    @Test
    public void testgetGeocoderDescriptionForNumber() {
        assertEquals(StringUtils.EMPTY, // $NON-NLS-1$
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(null, REGCODE_CN, Locale.SIMPLIFIED_CHINESE));

        assertEquals("北京市", //$NON-NLS-1$
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(CN_NUM_3, REGCODE_CN, Locale.SIMPLIFIED_CHINESE));
        assertEquals("Beijing", PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(CN_NUM_3, REGCODE_CN, Locale.UK)); //$NON-NLS-1$ //
        assertEquals("江西省九江市", //$NON-NLS-1$
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(CN_NUM_4, REGCODE_CN, Locale.SIMPLIFIED_CHINESE));
        assertEquals("Jiujiang, Jiangxi", //$NON-NLS-1$
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(CN_NUM_4, REGCODE_CN, Locale.UK));

        assertEquals("France", PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(FR_NUM_1, REGCODE_FR, Locale.FRANCE)); //$NON-NLS-1$
        assertEquals("Paris", PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(FR_NUM_2, REGCODE_FR, Locale.FRANCE)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY,
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(FR_NUM_3, REGCODE_FR, Locale.FRANCE));
        assertEquals(StringUtils.EMPTY,
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(FR_NUM_4, REGCODE_FR, Locale.FRANCE));
        assertEquals("France", PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(FR_NUM_5, REGCODE_FR, Locale.FRANCE)); //$NON-NLS-1$

        assertEquals("Corvallis, OR", PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_1, REGCODE_US, Locale.US)); //$NON-NLS-1$
        assertEquals("Corvallis, OR", PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_2, REGCODE_US, Locale.US)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_3, REGCODE_US, Locale.US));
        assertEquals("Corvallis, OR", PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_4, REGCODE_US, Locale.US)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_5, REGCODE_US, Locale.US));
        assertEquals(StringUtils.EMPTY, PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_6, REGCODE_US, Locale.US));
        assertEquals("Pennsylvania", PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(US_NUM_7, REGCODE_US, Locale.US)); //$NON-NLS-1$

        assertEquals("München", PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_1, REGCODE_DE, Locale.GERMANY)); //$NON-NLS-1$
        assertEquals("Munich", PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_1, REGCODE_DE, Locale.ENGLISH)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY,
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_2, REGCODE_DE, Locale.GERMANY));
        assertEquals("München", PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_3, REGCODE_DE, Locale.GERMANY)); //$NON-NLS-1$
        assertEquals("Nußbach Pfalz", //$NON-NLS-1$
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_4, REGCODE_DE, Locale.GERMANY));
        assertEquals("Nussbach Pfalz", //$NON-NLS-1$
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(DE_NUM_4, REGCODE_DE, Locale.ENGLISH));

        assertEquals("東京", //$NON-NLS-1$
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(JP_NUM_1, REGCODE_JP, Locale.JAPAN));
        assertEquals("日本", //$NON-NLS-1$
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(JP_NUM_2, REGCODE_JP, Locale.JAPAN));
        assertEquals("平塚", //$NON-NLS-1$
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(JP_NUM_3, REGCODE_JP, Locale.JAPAN));
        assertEquals("日本", //$NON-NLS-1$
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(JP_NUM_4, REGCODE_JP, Locale.JAPAN));
        assertEquals(StringUtils.EMPTY, //$NON-NLS-1$
                PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(JP_NUM_5, REGCODE_JP, Locale.JAPAN));

    }

    @Test
    public void testGetCountryCodeForRegion() {
        assertEquals(0, PhoneNumberHandlerBase.getCountryCodeForRegion(null));
        assertEquals(0, PhoneNumberHandlerBase.getCountryCodeForRegion(StringUtils.EMPTY));
        assertEquals(33, PhoneNumberHandlerBase.getCountryCodeForRegion("FR")); //$NON-NLS-1$
        assertEquals(1, PhoneNumberHandlerBase.getCountryCodeForRegion("US")); //$NON-NLS-1$
        assertEquals(86, PhoneNumberHandlerBase.getCountryCodeForRegion("CN")); //$NON-NLS-1$
        assertEquals(81, PhoneNumberHandlerBase.getCountryCodeForRegion("JP")); //$NON-NLS-1$

    }

    @Test
    public void testGetCountryCodeForPhoneNumber() {
        assertEquals(0, PhoneNumberHandlerBase.getCountryCodeForPhoneNumber(new Phonenumber.PhoneNumber()));
        assertEquals(33,
                PhoneNumberHandlerBase.getCountryCodeForPhoneNumber(PhoneNumberHandlerBase.parseToPhoneNumber(FR_NUM_1, null))); // $NON-NLS-1$
        assertEquals(1,
                PhoneNumberHandlerBase.getCountryCodeForPhoneNumber(PhoneNumberHandlerBase.parseToPhoneNumber(US_NUM_1, null))); // $NON-NLS-1$
        assertEquals(49,
                PhoneNumberHandlerBase.getCountryCodeForPhoneNumber(PhoneNumberHandlerBase.parseToPhoneNumber(DE_NUM_1, null))); // $NON-NLS-1$
    }

    @Test
    public void testGetTimeZonesForNumber() {
        assertEquals(PhoneNumberToTimeZonesMapper.getUnknownTimeZone(),
                PhoneNumberHandlerBase.getTimeZonesForNumber(null, null).get(0)); // $NON-NLS-1$

        assertEquals(1, PhoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_1, REGCODE_CN).size());
        assertEquals("[Asia/Shanghai]", //$NON-NLS-1$
                PhoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_1, REGCODE_CN).toString());
        assertEquals("[Asia/Shanghai]", //$NON-NLS-1$
                PhoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_2, REGCODE_CN).toString());
        assertEquals("[Asia/Shanghai]", PhoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_4, REGCODE_CN).toString()); //$NON-NLS-1$

        assertEquals("[Asia/Tokyo]", PhoneNumberHandlerBase.getTimeZonesForNumber(JP_NUM_1, REGCODE_JP).toString());
        assertEquals("[Asia/Tokyo]", PhoneNumberHandlerBase.getTimeZonesForNumber(JP_NUM_2, REGCODE_JP).toString());
        assertEquals("[Asia/Tokyo]", PhoneNumberHandlerBase.getTimeZonesForNumber(JP_NUM_3, REGCODE_JP).toString());
        assertEquals("[Asia/Tokyo]", PhoneNumberHandlerBase.getTimeZonesForNumber(JP_NUM_4, REGCODE_JP).toString());
        assertEquals("[Etc/Unknown]", PhoneNumberHandlerBase.getTimeZonesForNumber(JP_NUM_5, REGCODE_JP).toString());

        assertEquals(1, PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_1, REGCODE_FR).size());
        assertEquals("[Europe/Paris]", PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_1, REGCODE_FR).toString()); //$NON-NLS-1$
        assertEquals("[Europe/Paris]", PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_2, REGCODE_FR).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_3, REGCODE_FR).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_4, REGCODE_FR).toString()); //$NON-NLS-1$
        assertEquals("[Europe/Paris]", PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_5, REGCODE_FR).toString()); //$NON-NLS-1$

        assertEquals("[America/Los_Angeles]", PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_1, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[America/Los_Angeles]", PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_2, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_3, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[America/Los_Angeles]", PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_4, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_5, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_6, REGCODE_US).toString()); //$NON-NLS-1$

        assertEquals("[Europe/Berlin]", PhoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_1, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", PhoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_2, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", PhoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_3, REGCODE_US).toString()); //$NON-NLS-1$
        assertEquals("[Etc/Unknown]", PhoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_4, REGCODE_US).toString()); //$NON-NLS-1$

    }

    @Test
    public void testGetTimeZonesForNumberWithoutUnknown() {

        assertEquals(1, PhoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_1, REGCODE_CN, false).size());
        assertEquals("[Asia/Shanghai]", //$NON-NLS-1$
                PhoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_1, REGCODE_CN, false).toString());
        assertEquals("[Asia/Shanghai]", //$NON-NLS-1$
                PhoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_2, REGCODE_CN, false).toString());
        assertEquals("[Asia/Shanghai]", PhoneNumberHandlerBase.getTimeZonesForNumber(CN_NUM_4, REGCODE_CN, false).toString()); //$NON-NLS-1$

        assertEquals("[Asia/Tokyo]", PhoneNumberHandlerBase.getTimeZonesForNumber(JP_NUM_1, REGCODE_JP, false).toString());
        assertEquals("[Asia/Tokyo]", PhoneNumberHandlerBase.getTimeZonesForNumber(JP_NUM_2, REGCODE_JP, false).toString());
        assertEquals("[Asia/Tokyo]", PhoneNumberHandlerBase.getTimeZonesForNumber(JP_NUM_3, REGCODE_JP, false).toString());
        assertEquals("[Asia/Tokyo]", PhoneNumberHandlerBase.getTimeZonesForNumber(JP_NUM_4, REGCODE_JP, false).toString());
        assertEquals(0, PhoneNumberHandlerBase.getTimeZonesForNumber(JP_NUM_5, REGCODE_JP, false).size());

        assertEquals(1, PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_1, REGCODE_FR, false).size());
        assertEquals("[Europe/Paris]", PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_1, REGCODE_FR, false).toString()); //$NON-NLS-1$
        assertEquals("[Europe/Paris]", PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_2, REGCODE_FR, false).toString()); //$NON-NLS-1$
        assertEquals(0, PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_3, REGCODE_FR, false).size()); // $NON-NLS-1$
        assertEquals(0, PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_4, REGCODE_FR, false).size()); // $NON-NLS-1$
        assertEquals("[Europe/Paris]", PhoneNumberHandlerBase.getTimeZonesForNumber(FR_NUM_5, REGCODE_FR, false).toString()); //$NON-NLS-1$

        assertEquals("[America/Los_Angeles]", //$NON-NLS-1$
                PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_1, REGCODE_US, false).toString());
        assertEquals("[America/Los_Angeles]", //$NON-NLS-1$
                PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_2, REGCODE_US, false).toString());
        assertEquals(0, PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_3, REGCODE_US, false).size()); // $NON-NLS-1$
        assertEquals("[America/Los_Angeles]", //$NON-NLS-1$
                PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_4, REGCODE_US, false).toString());
        assertEquals(0, PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_5, REGCODE_US, false).size()); // $NON-NLS-1$
        assertEquals(0, PhoneNumberHandlerBase.getTimeZonesForNumber(US_NUM_6, REGCODE_US, false).size()); // $NON-NLS-1$

        assertEquals("[Europe/Berlin]", PhoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_1, REGCODE_US, false).toString()); //$NON-NLS-1$
        assertEquals(0, PhoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_2, REGCODE_US, false).size()); // $NON-NLS-1$
        assertEquals(0, PhoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_3, REGCODE_US, false).size()); // $NON-NLS-1$
        assertEquals(0, PhoneNumberHandlerBase.getTimeZonesForNumber(DE_NUM_4, REGCODE_US, false).size()); // $NON-NLS-1$

    }

    @Test
    public void testGetPhoneNumberType() {
        assertEquals(PhoneNumberTypeEnum.MOBILE, PhoneNumberHandlerBase.getPhoneNumberType(FR_NUM_5, REGCODE_FR));
        assertEquals(PhoneNumberTypeEnum.UNKNOWN, PhoneNumberHandlerBase.getPhoneNumberType(DE_NUM_4, REGCODE_US));
        assertEquals(PhoneNumberTypeEnum.FIXED_LINE, PhoneNumberHandlerBase.getPhoneNumberType(CN_NUM_3, REGCODE_CN));
        assertEquals(PhoneNumberTypeEnum.FIXED_LINE, PhoneNumberHandlerBase.getPhoneNumberType(JP_NUM_1, REGCODE_JP));
        assertEquals(PhoneNumberTypeEnum.MOBILE, PhoneNumberHandlerBase.getPhoneNumberType(JP_NUM_2, REGCODE_JP));
        assertEquals(PhoneNumberTypeEnum.UNKNOWN, PhoneNumberHandlerBase.getPhoneNumberType(JP_NUM_5, REGCODE_JP));
    }
}
