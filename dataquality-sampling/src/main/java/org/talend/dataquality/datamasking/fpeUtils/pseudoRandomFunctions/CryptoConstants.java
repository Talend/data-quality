package org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions;

public class CryptoConstants {

    public static final String AES_ALGORITHM = "AES/CBC/NoPadding";

    // Does not work in Java 1.7, this is a reported bug.
    public static final String AES_KEY_ALGORITHM = "AES";

    public static final int AES_KEY_LENGTH = 16;

    public static final String HMAC_ALGORITHM = "HmacSHA256";

    public static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Length in bytes of the key used for FPE.
     */
    public static final int KEY_LENGTH = 32;

}
