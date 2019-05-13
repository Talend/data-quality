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

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This class is a pseudo-random function to use with FF1 encryption.
 * It relies on the AES-CBC algorithm.
 *
 * @author afournier
 * @see org.talend.dataquality.datamasking.SecretManager
 * @see HmacPrf
 * @see AbstractCryptoSpec
 */
public class AesPrf extends AbstractPrf {

    private static final long serialVersionUID = -8700226840771252555L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AesPrf.class);

    private byte[] initializationVector = Arrays.copyOf(new byte[] { 0x00 }, 16);

    private Cipher cipher;

    public AesPrf(AbstractCryptoSpec cryptoSpec, SecretKey secret) {
        super(cryptoSpec, secret);
    }

    protected boolean init() {
        try {
            cipher = Cipher.getInstance(cryptoSpec.getCipherAlgorithm());
            SecretKeySpec spec = new SecretKeySpec(secret.getEncoded(), cryptoSpec.getKeyAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, spec, new IvParameterSpec(initializationVector));
            return true;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.error("Invalid algorithm name defined in the specifications : " + cryptoSpec.getCipherAlgorithm(), e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            LOGGER.error("Illegal key size or parameters. This is because AES-256 is not supported by the current JVM version: "
                    + System.getProperty("java.version")
                    + " Please update to a newer JVM version or install the Java Cryptography Extension (JCE) to support it.", e);
        }
        return false;
    }

    @Override
    public byte[] apply(byte[] text) {
        try {
            byte[] result = cipher.doFinal(text);
            result = Arrays.copyOfRange(result, result.length - initializationVector.length, result.length);
            return result;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.error("Problem with the input block to encrypt, may be due to bad plaintext split. Input = "
                    + new String(text, StandardCharsets.UTF_8), e);
        }
        return new byte[] {};
    }
}
