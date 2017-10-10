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
 * Created by jdenantes on 21/09/16.
 */
public class GenerateUniquePhoneNumberUsTest {

    private String output;

    private AbstractGenerateUniquePhoneNumber gnu = new GenerateUniquePhoneNumberUs();

    private GeneratePhoneNumberUS gpn = new GeneratePhoneNumberUS();

    private static PhoneNumberUtil GOOGLE_PHONE_UTIL = PhoneNumberUtil.getInstance();

    @Before
    public void setUp() throws Exception {
        gnu.setRandom(new Random(42));
        gnu.setKeepFormat(true);
    }

    @Test
    public void testKeepInvalidPatternTrue() {
        gnu.setKeepInvalidPattern(true);
        output = gnu.generateMaskedRow(null);
        assertEquals(null, output);
        output = gnu.generateMaskedRow("");
        assertEquals("", output);
        output = gnu.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void testKeepInvalidPatternFalse() {
        gnu.setKeepInvalidPattern(false);
        output = gnu.generateMaskedRow(null);
        assertEquals(null, output);
        output = gnu.generateMaskedRow("");
        assertEquals("", output);
        output = gnu.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void testGood1() {
        output = gnu.generateMaskedRow("35-6/42-5/9 865");
        assertEquals("35-6/45-0/7 585", output);
    }

    @Test
    public void testGood2() {
        gnu.setKeepFormat(false);
        // with spaces
        output = gnu.generateMaskedRow("356-425-9865");
        assertEquals("3564507585", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gnu.setKeepInvalidPattern(false);
        // with two 1 at the fifth and the sixth position
        output = gnu.generateMaskedRow("465 311 9856");
        assertEquals("308 075 2722", output);
    }

    @Test
    public void testValidAfterMasking() {
        gnu.setKeepFormat(false);
        String input;
        String output;
        for (int i = 0; i < 100; i++) {
            gpn.setRandom(new Random());
            input = gpn.doGenerateMaskedField(null);
            if (isValidPhoneNumber(input) && !(input.charAt(5) == '1' && input.charAt(6) == '1')) {
                for (int j = 0; j < 1000; j++) {
                    long rgenseed = System.nanoTime();
                    gnu.setRandom(new Random(rgenseed));
                    output = gnu.generateMaskedRow(input);
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
            phonenumber = GOOGLE_PHONE_UTIL.parse(data.toString(), Locale.US.getCountry());
        } catch (Exception e) {
            return false;
        }
        return GOOGLE_PHONE_UTIL.isValidNumberForRegion(phonenumber, Locale.US.getCountry());
    }

}
