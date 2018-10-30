package org.talend.dataquality.datamasking;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions.AesPrf;
import org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions.CryptoConstants;
import org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions.HmacPrf;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

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
    private int prfAlgo;

    /**
     * The keyed pseudo random function used to build a Format-Preserving Encrypter
     */
    private PseudoRandomFunction pseudoRandomFunction;

    public SecretManager() {
    }

    public SecretManager(int prfAlgo) {
        this.prfAlgo = prfAlgo;
    }

    public int getKey() {
        if (key == null)
            key = (new SecureRandom()).nextInt(Integer.MAX_VALUE - 1000000) + 1000000;

        return key;
    }

    public void setKey(int newKey) {
        this.key = newKey;
    }

    public PseudoRandomFunction getPseudoRandomFunction() {
        if (pseudoRandomFunction == null) {
            // TODO : Should a null password return an error ?
            if (prfAlgo == AES_CBC_PRF) {
                byte[] key = generateKey(16);
                pseudoRandomFunction = new AesPrf(key);
            } else if (prfAlgo == HMAC_SHA2_PRF) {
                byte[] key = generateKey(16);
                pseudoRandomFunction = new HmacPrf(key);
            }
        }

        return this.pseudoRandomFunction;
    }

    public void setPassword(String password) {
        if (prfAlgo == AES_CBC_PRF) {
            pseudoRandomFunction = new AesPrf(
                    generateSecretKey(password, CryptoConstants.AES_ALGORITHM, CryptoConstants.AES_KEY_ALGORITHM));
        } else if (prfAlgo == HMAC_SHA2_PRF) {
            pseudoRandomFunction = new HmacPrf(
                    generateSecretKey(password, CryptoConstants.HMAC_ALGORITHM, CryptoConstants.HMAC_KEY_ALGORITHM));
        }
    }

    public void setPrfAlgo(int prfAlgo) {
        this.prfAlgo = prfAlgo;
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
    private static SecretKey generateSecretKey(String password, String ciphAlgo, String keyAlgo) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(keyAlgo);
            KeySpec spec = new PBEKeySpec(password.toCharArray());
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), ciphAlgo);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
