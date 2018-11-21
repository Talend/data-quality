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
package org.talend.dataquality.datamasking.utils.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This class contains constant values about FPE encryption.
 *
 * @author afournier
 * @see AesPrf
 * @see HmacPrf
 * @see org.talend.dataquality.datamasking.SecretManager
 */
public class CryptoConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoConstants.class);

    /**
     * Algorithm for AES encryption.
     * CBC stands for Chaining block cipher : Each block is encrypted using the previous one.
     * There is no padding because this part is done in FF1 algorithm, after the call of this algorithm.
     */
    public static final String AES_ALGORITHM = "AES/CBC/NoPadding";

    /**
     * Algorithm used by the Hmac PRF, which uses SHA-2 hash function.
     */
    public static final String HMAC_ALGORITHM = "HmacSHA256";

    /**
     * This algorithm is a key stretching method used to derivate a password into a stronger cipher key.
     * This algorithm is used to generate keys for both AES adn HMAC Prfs.
     */
    public static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * AES key algorithm name used only to generate the secret specifications for the AES Cipher.
     * This is not used to generate a key.
     */
    public static final String AES_KEY_ALGORITHM = "AES";

    /**
     * Length in bytes of the key used for FPE.
     */
    public static final int KEY_LENGTH = getKeyLength();

    public static final int BASIC_KEY_BOUND = 10000;

    public static final int BASIC_KEY_OFFSET = 1000;

    /**
     * This method computes the length of PRF keys depending on the Java version on the machine.
     * This check needs to be done because Java version below 1.8.161 does not support 32-byte keys.
     * See : <a href="http://opensourceforgeeks.blogspot.com/2014/09/how-to-install-java-cryptography.html">this blog post</a>.
     *
     * @return the key length to use depending on java version
     */
    private static int getKeyLength() {
        String major1, major2, update;
        String[] javaVersionElements = System.getProperty("java.runtime.version").split("\\.|_|-b");
        major1 = javaVersionElements[0];
        major2 = javaVersionElements[1];
        update = javaVersionElements[3];

        double specVersion = Double.parseDouble(major1 + "." + major2);
        double implVersion = Double.parseDouble(update);

        LOGGER.info("Java Runtime version : " + System.getProperty("java.runtime.version"));

        int supportedLength;
        if (specVersion > 1.8 || (specVersion == 1.8 && implVersion >= 161)) {
            supportedLength = 32;
        } else {
            supportedLength = 16;
        }

        LOGGER.info("FPE supported key length for current java version is " + supportedLength + " bytes");

        return supportedLength;
    }
}
