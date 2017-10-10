package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberFrTest {

    private String output;

    private AbstractGenerateUniquePhoneNumber gnf = new GenerateUniquePhoneNumberFr();

    private GeneratePhoneNumberFrench gpn = new GeneratePhoneNumberFrench();

    private static PhoneNumberUtil GOOGLE_PHONE_UTIL = PhoneNumberUtil.getInstance();

    @Before
    public void setUp() throws Exception {
        gnf.setRandom(new Random(42));
        gnf.setKeepFormat(true);
    }

    @Test
    public void testValidWithFormat() {
        output = gnf.generateMaskedRow("01.42.95.45.24");
        assertEquals("01.42.97.75.96", output);
        output = gnf.generateMaskedRow("(0033) 6 48 98 75 12");
        assertEquals("(0033) 6 48 06 10 48", output);
    }

    @Test
    public void testValidWithoutFormat() {
        gnf.setKeepFormat(false);
        // with spaces
        output = gnf.generateMaskedRow("01.42.95.45.24");
        assertEquals("0142977596", output);
    }

    @Test
    public void testInvalid() {
        // without a number
        output = gnf.generateMaskedRow("35686");
        assertEquals("30807", output);
        gnf.setKeepInvalidPattern(true);
        // with a letter
        output = gnf.generateMaskedRow("35686");
        assertEquals("35686", output);
    }

    @Test
    public void testValidAfterMasking() {
        gnf.setKeepFormat(false);
        String input;
        String output;
        for (int i = 0; i < 100; i++) {
            gpn.setRandom(new Random());
            input = gpn.doGenerateMaskedField(null);
            if (isValidPhoneNumber(input)) {
                for (int j = 0; j < 1000; j++) {
                    long rgenseed = System.nanoTime();
                    gnf.setRandom(new Random(rgenseed));
                    output = gnf.generateMaskedRow(input);
                    Assert.assertTrue("Don't worry, report this line to the Data Quality team: with a seed = " + rgenseed + ", "
                            + input + " is valid, but after the masking " + output + " is not valid", isValidPhoneNumber(output));
                }
            }
        }
    }

    /**
     *
     * whether a phone number is valid for a certain region.
     *
     * @param data the data that we want to validate
     * @return a boolean that indicates whether the number is of a valid pattern
     */
    public static boolean isValidPhoneNumber(Object data) {
        Phonenumber.PhoneNumber phonenumber = null;
        try {
            phonenumber = GOOGLE_PHONE_UTIL.parse(data.toString(), Locale.FRANCE.getCountry());
        } catch (Exception e) {
            return false;
        }
        return GOOGLE_PHONE_UTIL.isValidNumberForRegion(phonenumber, Locale.FRANCE.getCountry());
    }
}
