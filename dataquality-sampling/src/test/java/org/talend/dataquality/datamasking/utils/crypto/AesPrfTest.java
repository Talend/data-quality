package org.talend.dataquality.datamasking.utils.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

@RunWith(MockitoJUnitRunner.class)
public class AesPrfTest {

    @Mock
    private AesCbcCryptoSpec spec;

    private byte[] input = "16-byteTestInput".getBytes();

    @Test
    public void displayIncorrectAlgorithmWhenNoSuchAlgorithmException() {
        Mockito.when(spec.getCipherAlgorithm()).thenReturn("WrongAlgorithm");

        SecretKey secret = generateRandomSecretKey();

        AesPrf prf = new AesPrf(spec, secret);
        prf.apply(input);

        // This method should be called to display the incorrect algorithm name after the catch of 'NoSuchAlgorithmException'.
        Mockito.verify(spec, Mockito.atLeast(2)).getCipherAlgorithm();
    }

    @Test
    public void displayIncorrectAlgorithmWhenNoSuchPaddingException() {
        Mockito.when(spec.getCipherAlgorithm()).thenReturn("AES/CBC/BadPadding");

        SecretKey secret = generateRandomSecretKey();

        AesPrf prf = new AesPrf(spec, secret);
        prf.apply(input);

        // This method should be called to display the incorrect algorithm name after the catch of 'NoSuchPaddingException'.
        Mockito.verify(spec, Mockito.atLeast(2)).getCipherAlgorithm();
    }

    @Test
    public void displayIncorrectKeyAlgorithmWhenInvalidKeyException() {
        Mockito.when(spec.getCipherAlgorithm()).thenCallRealMethod();
        Mockito.when(spec.getKeyAlgorithm()).thenReturn("BadKeyAlgorithm");
        SecretKey secret = generateRandomSecretKey();

        AesPrf prf = new AesPrf(spec, secret);
        prf.apply(input);

        // This method should be called to display the incorrect key algorithm name after the catch of 'InvalidKeyException'.
        Mockito.verify(spec, Mockito.atLeast(1)).getKeyAlgorithm();
    }

    // Copy of the method SecretManager method which is private
    private SecretKey generateRandomSecretKey() {
        AesCbcCryptoSpec aesSpec = new AesCbcCryptoSpec();
        byte[] randomKey = new byte[aesSpec.getKeyLength()];
        SecureRandom srand = new SecureRandom();
        srand.nextBytes(randomKey);
        return new SecretKeySpec(randomKey, aesSpec.getKeyAlgorithm());
    }
}
