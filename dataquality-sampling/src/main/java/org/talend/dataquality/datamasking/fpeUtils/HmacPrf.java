package org.talend.dataquality.datamasking.fpeUtils;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HmacPrf implements PseudoRandomFunction {

    private static final String MAC_ALGORITHM = "HmacSHA256";

    private static final String KEY_ALGORITHM_NAME = "PBEWithHmacSHA256AndAES_128";

    private Mac hmac;

    public HmacPrf() {
        try {
            hmac = Mac.getInstance(MAC_ALGORITHM);
            byte[] key = new byte[16];
            new SecureRandom().nextBytes(key);
            hmac.init(new SecretKeySpec(key, KEY_ALGORITHM_NAME));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

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

    public HmacPrf(int key) {
        try {
            hmac = Mac.getInstance(MAC_ALGORITHM);
            hmac.init(generateKey(key));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public HmacPrf(SecretKey key) {
        try {
            hmac = Mac.getInstance(MAC_ALGORITHM);
            hmac.init(key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] apply(byte[] text) {
        return hmac.doFinal(text);
    }

    private static SecretKey generateKey(int key) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(MAC_ALGORITHM);
            ByteBuffer buf = ByteBuffer.allocate(4);
            buf.putInt(key);
            keyGen.init(new SecureRandom(buf.array()));
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
