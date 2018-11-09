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
package org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions;

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
    public static final int KEY_LENGTH = 32;

}
