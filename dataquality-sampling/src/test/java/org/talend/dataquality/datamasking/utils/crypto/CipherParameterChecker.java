package org.talend.dataquality.datamasking.utils.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CipherParameterChecker {

    public static boolean IS_AES256_SUPPORTED = true;

    static {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec spec = new SecretKeySpec(new byte[32], "AES");
            cipher.init(Cipher.ENCRYPT_MODE, spec, new SecureRandom());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            IS_AES256_SUPPORTED = false;
        }
    }
}
