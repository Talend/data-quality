package org.talend.dataquality.datamasking.functions;

import java.util.Locale;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.talend.dataquality.datamasking.FormatPreservingMethod;

import static org.junit.Assert.*;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberGermanyTest {

    private String output;

    private AbstractGenerateUniquePhoneNumber gng = new GenerateUniquePhoneNumberGermany();

    private GeneratePhoneNumberGermany gpn = new GeneratePhoneNumberGermany();

    private static PhoneNumberUtil GOOGLE_PHONE_UTIL = PhoneNumberUtil.getInstance();

    @Before
    public void setUp() throws Exception {
        gng.setRandom(new Random(56));
        gng.setSecret(FormatPreservingMethod.BASIC.name(), "");
        gng.setKeepFormat(true);
    }

    @Test
    public void testKeepInvalidPatternTrue() {
        gng.setKeepInvalidPattern(true);
        output = gng.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void outputsNullWhenInputNull() {
        gng.setKeepInvalidPattern(false);
        output = gng.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void outputsNullWhenInputEmpty() {
        gng.setKeepInvalidPattern(false);
        output = gng.generateMaskedRow("");
        assertNull(output);
    }

    @Test
    public void outputsNullWhenInputInvalid() {
        gng.setKeepInvalidPattern(false);
        output = gng.generateMaskedRow("AHDBNSKD");
        assertNull(output);
    }

    @Test
    public void testValidWithFormat() {
        output = gng.generateMaskedRow("(089) / 636-48018");
        assertEquals("(089) / 829-42714", output);
    }

    @Test
    public void testValidWithoutFormat() {
        gng.setKeepFormat(false);
        output = gng.generateMaskedRow("(089) / 636-48018");
        assertEquals("08982942714", output);
    }

    @Test
    public void unreproducibleWhenNoPasswordSet() {
        String input = "(089) / 636-48018";
        gng.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF.name(), "");
        String result1 = gng.generateMaskedRow(input);

        gng.setSecret(FormatPreservingMethod.SHA2_HMAC_PRF.name(), "");
        String result2 = gng.generateMaskedRow(input);

        assertNotEquals(String.format("The result should not be reproducible when no password is set. Input value is %s.", input),
                result1, result2);
    }

    @Test
    public void testValidAfterMasking() {
        gng.setKeepFormat(false);
        String input;
        String output;
        for (int i = 0; i < 100; i++) {
            gpn.setRandom(new Random());
            input = gpn.doGenerateMaskedField(null);
            if (isValidPhoneNumber(input)) {
                for (int j = 0; j < 1000; j++) {
                    long rgenseed = System.nanoTime();
                    gng.setRandom(new Random(rgenseed));
                    output = gng.generateMaskedRow(input);
                    Assert.assertTrue("Don't worry, report this line to Data Quality team: with a seed = " + rgenseed + ", "
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
            phonenumber = GOOGLE_PHONE_UTIL.parse(data.toString(), Locale.GERMANY.getCountry());
        } catch (Exception e) {
            return false;
        }
        return GOOGLE_PHONE_UTIL.isValidNumberForRegion(phonenumber, Locale.GERMANY.getCountry());
    }

}
