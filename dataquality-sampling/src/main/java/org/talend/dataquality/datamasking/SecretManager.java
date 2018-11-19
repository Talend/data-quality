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
package org.talend.dataquality.datamasking;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.utils.crypto.AesPrf;
import org.talend.dataquality.datamasking.utils.crypto.CryptoConstants;
import org.talend.dataquality.datamasking.utils.crypto.HmacPrf;
import org.talend.dataquality.datamasking.generic.patterns.GenerateFormatPreservingPatterns;
import org.talend.dataquality.datamasking.generic.patterns.GenerateUniqueRandomPatterns;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

/**
 * This class handles the keys and secrets used for datamasking.
 * Instead of storing the password directly for FPE encryption,
 * the class has an instance of the keyed pseudo-random function to use when performing FF1 encryption.
 * <br>
 * When the password is changed, a new pseudo-random function is created according to the method and
 * its key will be derivated form the password.
 *
 * <br>
 * TODO : When {@link SecretManager#getPseudoRandomFunction()} is called :
 * TODO : - What to do when the method is set to Basic ?
 * TODO : - What to do when the method the prf attribute is null ?
 *
 * @author afournier
 * @see AesPrf
 * @see HmacPrf
 * @see CryptoConstants
 * @see GenerateFormatPreservingPatterns
 * @see GenerateUniqueRandomPatterns
 */
public class SecretManager {

    /**
     * Identifier for Talend internal method.
     */
    public static final int BASIC = 0;

    /**
     * Identifier for using FF1 with AES as an underlying pseudo-random function.
     */
    public static final int AES_CBC_PRF = 1;

    /**
     * Identifier for using FF1 with SHA-2 as an underlying pseudo-random function.
     */
    public static final int HMAC_SHA2_PRF = 2;

    private static final Logger LOGGER = LoggerFactory.getLogger(SecretManager.class);

    /**
     * The Key to use with Talend internal method.
     */
    private Integer key;

    /**
     * {@code Integer} value that corresponds to the type of pseudo-random function used by the secretManager
     */
    private Integer method;

    /**
     * The keyed pseudo-random function used to build a Format-Preserving Encrypter
     */
    private PseudoRandomFunction pseudoRandomFunction;

    public SecretManager() {

    }

    public SecretManager(int method, String password) {
        setPseudoRandomFunction(method, password);
    }

    /**
     * getter for the {@link #BASIC} method key.
     */
    public int getKey() {
        if (key == null)
            key = (new SecureRandom()).nextInt(Integer.MAX_VALUE - 1000000) + 1000000;

        return key;
    }

    /**
     * getter for the method used
     */
    public int getMethod() {
        return method;
    }

    /**
     * setter for the {@link #BASIC} method key.
     */
    public void setKey(int newKey) {
        this.key = newKey;
    }

    /**
     * This method returns the pseudo-random function of the current instance of {@code SecretManager}.
     * If the pseudo-random function is null, create a new one with a random key.
     * <br>
     * If the method has not been set, it should return an {@code IllegalStateException}
     * because this is not a behavior we want.
     *
     * @return the current pseudo-random function of this {@code SecretManager}.
     */
    public PseudoRandomFunction getPseudoRandomFunction() {
        if (pseudoRandomFunction == null) {

            if (method == null) {
                throw new IllegalStateException("No pseudo random algorithm set for this secret manager.");
            }

            switch (method) {
            case BASIC:
                return null;
            case AES_CBC_PRF:
                SecretKey aesKey = generateRandomSecretKey(CryptoConstants.KEY_LENGTH);
                pseudoRandomFunction = new AesPrf(aesKey);
                break;
            case HMAC_SHA2_PRF:
                SecretKey hmacKey = generateRandomSecretKey(CryptoConstants.KEY_LENGTH);
                pseudoRandomFunction = new HmacPrf(hmacKey);
                break;
            default:
                return null;
            }
        }
        return this.pseudoRandomFunction;
    }

    /**
     * This method sets the pseudo-random function of the current instance of {@code SecretManager}.
     * If the password is null / not set, it will generate a completely random key and create
     * the PRF instance corresponding to the method value.
     * If the method is set to {@link #BASIC}, then no PRF is instantiated.
     *
     * @param method the masking method to use.
     * @param password the password
     */
    public void setPseudoRandomFunction(int method, String password) {
        this.method = method;

        if (method != BASIC) {

            SecretKey secret;
            if (password == null || "".equals(password)) {
                secret = generateRandomSecretKey(CryptoConstants.KEY_LENGTH);
            } else {
                secret = generateSecretKeyFromPassword(password);
            }

            if (method == AES_CBC_PRF) {
                pseudoRandomFunction = new AesPrf(secret);
            } else if (method == HMAC_SHA2_PRF) {
                pseudoRandomFunction = new HmacPrf(secret);
            }
        }
    }

    /**
     * This method generates a key in an array of bytes of length {@code length}.
     * The array of bytes is randomly filled using a {@code SecureRandom} object.
     *
     * @param length the number of bytes of the key.
     * @return a randomly generated key.
     */
    private SecretKey generateRandomSecretKey(int length) {
        byte[] randomKey = new byte[length];
        SecureRandom srand = new SecureRandom();
        srand.nextBytes(randomKey);
        return new SecretKeySpec(randomKey, CryptoConstants.KEY_ALGORITHM);
    }

    /**
     * This method generates a secret Key using the key-stretching algorithm PBKDF2 of
     * <a href="https://docs.oracle.com/javase/7/docs/api/javax/crypto/package-summary.html">javax.crypto</a>.
     * It is basically a hashing algorithm slow by design, in order to increase the time
     * required for an attacker to try a lot of passwords in a bruteforce attack.
     *
     * @param password a password given as a {@code String}.
     * @return a {@code SecretKey} securely generated.
     */
    private SecretKey generateSecretKeyFromPassword(String password) {
        SecretKey secret = null;

        try {
            byte[] salt = new byte[CryptoConstants.KEY_LENGTH];
            new Random(123456789 + password.length()).nextBytes(salt);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(CryptoConstants.KEY_ALGORITHM);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, CryptoConstants.KEY_LENGTH << 3);
            secret = factory.generateSecret(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            LOGGER.error("Invalid crypto constants have been set, see values of KEY_LENGTH and KEY_ALGORITHM. ", e);
        }

        if (secret == null) {
            throw new IllegalArgumentException("This password can't be used for Format-Preserving Encryption.");
        }

        return secret;
    }
}
