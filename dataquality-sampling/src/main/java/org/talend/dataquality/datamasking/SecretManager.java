package org.talend.dataquality.datamasking;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions.AesPrf;
import org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions.CryptoConstants;
import org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions.HmacPrf;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

public class SecretManager {

    public static final int AES_CBC_PRF = 1;

    public static final int HMAC_SHA2_PRF = 2;

    /**
     * The random key to make impossible the decoding
     */
    private Integer key;

    /**
     * {@code Integer} value that corresponds to the type of pseudo-random function used by the secretManager
     */
    private Integer prfAlgo;

    /**
     * The keyed pseudo random function used to build a Format-Preserving Encrypter
     */
    private PseudoRandomFunction pseudoRandomFunction;

    public int getKey() {
        if (key == null)
            key = (new SecureRandom()).nextInt(Integer.MAX_VALUE - 1000000) + 1000000;

        return key;
    }

    public void setKey(int newKey) {
        this.key = newKey;
    }

    public PseudoRandomFunction getPseudoRandomFunction() {
        // TODO : Should a null PRF return an error ?
        if (pseudoRandomFunction == null) {
            if (prfAlgo == null) {
                throw new IllegalStateException("No pseudo random algorithm set for this secret manager.");
            }

            if (prfAlgo == AES_CBC_PRF) {
                byte[] key = generateKey(CryptoConstants.KEY_LENGTH);
                pseudoRandomFunction = new AesPrf(key);
            } else if (prfAlgo == HMAC_SHA2_PRF) {
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
            if (prfAlgo == AES_CBC_PRF) {
                pseudoRandomFunction = new AesPrf(secret);
            } else if (prfAlgo == HMAC_SHA2_PRF) {
                pseudoRandomFunction = new HmacPrf(secret);
            }
        }
    }

    public void setPrfAlgo(int prfAlgo) {
        this.prfAlgo = prfAlgo;
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
