package org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * @author afournier
 *
 * This class is a pseduo-random function used in FF1 encryption.
 * It relies on the HMAC algorithm combined with SHA-2 hashing function.
 */
public class HmacPrf implements PseudoRandomFunction {

    private static final String MAC_ALGORITHM = CryptoConstants.HMAC_ALGORITHM;

    private static final String KEY_ALGORITHM_NAME = CryptoConstants.HMAC_KEY_ALGORITHM;

    private Mac hmac;

    public HmacPrf(byte[] key) {
        try {
            hmac = Mac.getInstance(MAC_ALGORITHM);
            hmac.init(new SecretKeySpec(key, KEY_ALGORITHM_NAME));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public HmacPrf(SecretKey secret) {
        try {
            hmac = Mac.getInstance(MAC_ALGORITHM);
            hmac.init(secret);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public byte[] apply(byte[] text) {
        return hmac.doFinal(text);
    }

}
