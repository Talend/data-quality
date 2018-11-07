package org.talend.dataquality.datamasking;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions.AesPrf;
import org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions.CryptoConstants;
import org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions.HmacPrf;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;

public class SecretManager {

    public static final int TALEND_CUSTOM = 0;

    public static final int AES_CBC_PRF = 1;

    public static final int HMAC_SHA2_PRF = 2;

    /**
     * The random key to make impossible the decoding
     */
    private Integer key;

    /**
     * {@code Integer} value that corresponds to the type of pseudo-random function used by the secretManager
     */
    private Integer method;

    /**
     * The keyed pseudo random function used to build a Format-Preserving Encrypter
     */
    private PseudoRandomFunction pseudoRandomFunction;

    public SecretManager() {

    }

    public SecretManager(int method, String password) {
        setMethod(method);
        if (method != TALEND_CUSTOM) {
            setPassword(password);
        }
    }

    public int getKey() {
        if (key == null)
            key = (new SecureRandom()).nextInt(Integer.MAX_VALUE - 1000000) + 1000000;

        return key;
    }

    public int getMethod() {
        return method;
    }

    public void setKey(int newKey) {
        this.key = newKey;
    }

    public PseudoRandomFunction getPseudoRandomFunction() {
        // TODO : Should a null PRF return an error ?
        if (pseudoRandomFunction == null) {
            if (method == null) {
                throw new IllegalStateException("No pseudo random algorithm set for this secret manager.");
            }

            if (method == AES_CBC_PRF) {
                byte[] key = generateKey(CryptoConstants.KEY_LENGTH);
                pseudoRandomFunction = new AesPrf(key);
            } else if (method == HMAC_SHA2_PRF) {
                byte[] key = generateKey(CryptoConstants.KEY_LENGTH);
                pseudoRandomFunction = new HmacPrf(key);
            }
        }

        return this.pseudoRandomFunction;
    }

    public void setPassword(String password) {
        // TODO : Should a null Password return an error ?
        if (password != null) {
            SecretKey secret = generateSecretKey(password);
            if (secret == null) {
                throw new IllegalArgumentException("This password can't be used for Format-Preserving Encryption !");
            }
            if (method == AES_CBC_PRF) {
                pseudoRandomFunction = new AesPrf(secret);
            } else if (method == HMAC_SHA2_PRF) {
                pseudoRandomFunction = new HmacPrf(secret);
            }
        }
    }

    public void setMethod(int method) {
        this.method = method;
        pseudoRandomFunction = null;
        pseudoRandomFunction = getPseudoRandomFunction();
    }

    private byte[] generateKey(int length) {
        byte[] key = new byte[length];
        SecureRandom srand = new SecureRandom();
        srand.nextBytes(key);
        return key;
    }

    /**
     * @param password a password given as a {@code String}
     * @return a {@code SecretKey} securely generated using
     *         <a href="https://docs.oracle.com/javase/7/docs/api/javax/crypto/package-summary.html">javax.crypto</a>.
     */
    private SecretKey generateSecretKey(String password) {
        try {
            byte[] salt = new byte[CryptoConstants.KEY_LENGTH];
            new Random(123456789 + password.length()).nextBytes(salt);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(CryptoConstants.KEY_ALGORITHM);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, CryptoConstants.KEY_LENGTH << 3);
            return factory.generateSecret(spec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
