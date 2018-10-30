package org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class AesPrf implements PseudoRandomFunction {

    private static final String AES_ALGORITHM = CryptoConstants.AES_ALGORITHM;

    private static final String KEY_ALGORITHM_NAME = CryptoConstants.AES_KEY_ALGORITHM;

    private Cipher cipher;

    private byte[] initializationVector = Arrays.copyOf(new byte[] { 0x00 }, 16);

    public AesPrf(byte[] key) {
        try {
            cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM_NAME),
                    new IvParameterSpec(initializationVector));
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public AesPrf(SecretKey secret) {
        try {
            cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(initializationVector));
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] apply(byte[] text) {
        try {
            byte[] result = cipher.doFinal(text);
            return Arrays.copyOfRange(result, result.length - initializationVector.length, result.length);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
