package org.talend.dataquality.datamasking.utils.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

@RunWith(MockitoJUnitRunner.class)
public class HmacPrfTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AesPrfTest.class);

    @Mock
    HmacSha2CryptoSpec spec;

    private HmacPrf hmacPrf;

    @Test
    public void displayIncorrectAlgorithmWhenNoSuchAlgorithmException() {
        Mockito.when(spec.getCipherAlgorithm()).thenReturn("WrongAlgorithm");

        SecretKey secret = generateRandomSecretKey();

        // Will call init() method
        hmacPrf = new HmacPrf(spec, secret);

        // This method should be called to display the incorrect algorithm name after the catch of 'NoSuchAlgorithmException'.
        Mockito.verify(spec, Mockito.atLeast(2)).getCipherAlgorithm();
    }

    // Copy of the method SecretManager method which is private
    private SecretKey generateRandomSecretKey() {
        HmacSha2CryptoSpec hmacSpec = new HmacSha2CryptoSpec();
        byte[] randomKey = new byte[hmacSpec.getKeyLength()];
        SecureRandom srand = new SecureRandom();
        srand.nextBytes(randomKey);
        return new SecretKeySpec(randomKey, hmacSpec.getKeyAlgorithm());
    }
}
