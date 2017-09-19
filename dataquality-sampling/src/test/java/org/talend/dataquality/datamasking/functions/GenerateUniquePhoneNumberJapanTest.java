package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.talend.dataquality.sampling.exception.DQException;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberJapanTest {

    private String output;

    private AbstractGenerateUniquePhoneNumber gnj = new GenerateUniquePhoneNumberJapan();

    private GeneratePhoneNumberJapan gpn = new GeneratePhoneNumberJapan();

    private static PhoneNumberUtil GOOGLE_PHONE_UTIL = PhoneNumberUtil.getInstance();

    public GenerateUniquePhoneNumberJapanTest() throws DQException {
        super();
    }

    @Before
    public void setUp() throws Exception {
        gnj.setRandom(new Random(56));
        gnj.setKeepFormat(true);
    }

    @Test
    public void testValidWithFormat() throws DQException {
        output = gnj.generateMaskedRow("49-92 8 7895");
        assertEquals("49-01 0 9355", output);
    }

    @Test
    public void testValidWithoutFormat() throws DQException {
        gnj.setKeepFormat(false);
        output = gnj.generateMaskedRow("49-92 8 7895");
        assertEquals("490109355", output);
    }

    @Test
    public void testInvalid() throws DQException {
        // without a number
        output = gnj.generateMaskedRow("35686");
        assertEquals("42445", output);
        gnj.setKeepInvalidPattern(true);
        // with a letter
        output = gnj.generateMaskedRow("35686");
        assertEquals("35686", output);
    }

    @Test
    public void testValidAfterMasking() throws DQException {
        gnj.setKeepFormat(false);
        String input;
        String output;

        for (int i = 0; i < 100; i++) {
            gpn.setRandom(new Random());
            input = gpn.doGenerateMaskedField(null);
            if (isValidPhoneNumber(input)) {
                for (int j = 0; j < 1000; j++) {
                    long rgenseed = System.nanoTime();
                    gnj.setRandom(new Random(rgenseed));
                    output = gnj.generateMaskedRow(input);
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
            phonenumber = GOOGLE_PHONE_UTIL.parse(data.toString(), Locale.JAPAN.getCountry());
        } catch (Exception e) {
            return false;
        }
        return GOOGLE_PHONE_UTIL.isValidNumberForRegion(phonenumber, Locale.JAPAN.getCountry());
    }
}