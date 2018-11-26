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
import org.talend.dataquality.datamasking.utils.crypto.*;
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
 * @author afournier
 * @see AesPrf
 * @see HmacPrf
 * @see AbstractCryptoSpec
 * @see CryptoFactory
 * @see GenerateFormatPreservingPatterns
 * @see GenerateUniqueRandomPatterns
 */
public class SecretManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecretManager.class);

    /**
     * Factory for constructing the {@link #pseudoRandomFunction} and {@link #cryptoSpec}
     * according to the context given by the {@link #method}.
     */
    private static final CryptoFactory cryptoFactory = new CryptoFactory();

    /**
     * The Key to use with Talend internal method.
     */
    private Integer key;

    /**
     * Enumeration that corresponds to the type of pseudo-random function used by the secretManager
     */
    private FormatPreservingMethod method;

    /**
     * This attribute contains all the specifications relative to the pseudo-random function.
     */
    private AbstractCryptoSpec cryptoSpec;

    /**
     * The keyed pseudo-random function used to build a Format-Preserving Cipher
     */
    private PseudoRandomFunction pseudoRandomFunction;

    public SecretManager() {

    }

    public SecretManager(FormatPreservingMethod method, String password) {
        this.method = method;
        cryptoSpec = cryptoFactory.getPrfSpec(method);
        setPseudoRandomFunction(password);
    }

    /**
     * This method sets the pseudo-random function of the current instance of {@link SecretManager}.
     * If the password is null / not set, it will generate a cryptographically secure random key and create
     * the PRF instance corresponding to the method value.
     * If the method is set to {@link FormatPreservingMethod#BASIC}, then no PRF is instantiated.
     *
     * @param password the password used to generate the pseudo-random function's key
     */
    public void setPseudoRandomFunction(String password) {
        if (method != FormatPreservingMethod.BASIC) {

            SecretKey secret;
            if (password == null || "".equals(password)) {
                secret = generateRandomSecretKey(cryptoSpec.getKeyLength());
            } else {
                secret = generateSecretKeyFromPassword(password);
            }

            pseudoRandomFunction = cryptoFactory.getPrf(cryptoSpec, secret);
        }
    }

    /**
     * setter for the {@link FormatPreservingMethod#BASIC} method key.
     */
    public void setKey(int newKey) {
        this.key = newKey;
    }

    /**
     * getter for the {@link FormatPreservingMethod#BASIC} method key.
     */
    public int getKey() {
        if (key == null)
            key = (new SecureRandom()).nextInt(Integer.MAX_VALUE - 1000000) + 1000000;

        return key;
    }

    /**
     * getter for the method used.
     */
    public FormatPreservingMethod getMethod() {
        return method;
    }

    /**
     * getter for the crypto specifications of this secret manager.
     */
    public AbstractCryptoSpec getCryptoSpec() {
        return cryptoSpec;
    }

    /**
     * This method returns the pseudo-random function of the current instance of {@code SecretManager}.
     * If the pseudo-random function is null, create a new one with a cryptographically secure random key.
     * <br>
     * If the method has not been set, it should return an {@code IllegalStateException}
     * because this is not a behavior we want.
     *
     * @return the current pseudo-random function of this {@link SecretManager}.
     */
    public PseudoRandomFunction getPseudoRandomFunction() {
        if (pseudoRandomFunction == null) {

            if (method == null || method == FormatPreservingMethod.BASIC) {
                throw new IllegalStateException("This secret manager is not set to handle a pseudo-random function");
            }

            SecretKey secret = generateRandomSecretKey(cryptoSpec.getKeyLength());
            pseudoRandomFunction = cryptoFactory.getPrf(cryptoSpec, secret);
        }
        return pseudoRandomFunction;
    }

    /**
     * This method generates a key in an array of bytes of length {@code length}.
     * The array of bytes is randomly filled using a {@code SecureRandom} object to ensure cryptographic security.
     *
     * @param length the number of bytes of the key.
     * @return a randomly generated key.
     */
    private SecretKey generateRandomSecretKey(int length) {
        byte[] randomKey = new byte[length];
        SecureRandom srand = new SecureRandom();
        srand.nextBytes(randomKey);
        return new SecretKeySpec(randomKey, cryptoSpec.getKeyAlgorithm());
    }

    /**
     * This method generates a secret Key using the key-stretching algorithm PBKDF2 of
     * <a href="https://docs.oracle.com/javase/7/docs/api/javax/crypto/package-summary.html">javax.crypto</a>.
     * It is basically a hashing algorithm slow by design, in order to increase the time
     * required for an attacker to try a lot of passwords in a bruteforce attack.
     * <br>
     *     About the salt :
     *     <ul>
     *         <li>The salt is not secret, the use of Random is not critical.</li>
     *         <li>The salt is important to avoid rainbow table attacks.</li>
     *         <li>The salt should be generated with SecureRandom() in case the passwords are stored.</li>
     *         <li>In that case the salt should be stored in plaintext next to the password and a unique user identifier.</li>
     *     </ul>
     * @param password a password given as a {@code String}.
     * @return a {@code SecretKey} securely generated.
     */
    private SecretKey generateSecretKeyFromPassword(String password) {
        SecretKey secret = null;

        try {
            byte[] salt = new byte[cryptoSpec.getKeyLength()];
            new Random(password.hashCode()).nextBytes(salt);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, cryptoSpec.getKeyLength() << 3);
            secret = factory.generateSecret(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            LOGGER.error("Invalid cipher or key algorithm set in " + cryptoSpec.getClass(), e);
        }

        if (secret == null) {
            throw new IllegalArgumentException("This password can't be used for Format-Preserving Encryption.");
        }

        return secret;
    }
}
