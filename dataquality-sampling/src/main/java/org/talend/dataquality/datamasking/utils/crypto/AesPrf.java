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

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(AesPrf.class);

    private Cipher cipher;

    private byte[] initializationVector = Arrays.copyOf(new byte[] { 0x00 }, 16);

    public AesPrf(AbstractCryptoSpec cryptoSpec, SecretKey secret) {
        super(cryptoSpec);
        init(secret);
    }

    @Override
    protected void init(SecretKey secret) {
        try {
            cipher = Cipher.getInstance(cryptoSpec.getCipherAlgorithm());
            SecretKeySpec spec = new SecretKeySpec(secret.getEncoded(), cryptoSpec.getKeyAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, spec, new IvParameterSpec(initializationVector));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.error("Invalid algorithm name defined in the specifications : " + cryptoSpec.getCipherAlgorithm(), e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            try {
                LOGGER.error("Invalid specifications for the cipher ! " + "Wrong key : "
                        + new String(secret.getEncoded(), secret.getFormat()) + " or wrong key algorithm :"
                        + cryptoSpec.getKeyAlgorithm() + " or wrong Initial Vector" + Arrays.toString(initializationVector), e);
            } catch (UnsupportedEncodingException e1) {
                LOGGER.error("The secret has a format unsupported by java.String : " + secret.getFormat(), e1);
            }
        }
    }

    @Override
    public byte[] apply(byte[] text) {
        byte[] result = new byte[] {};

        try {
            result = cipher.doFinal(text);
            result = Arrays.copyOfRange(result, result.length - initializationVector.length, result.length);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.error("Problem with the input block to encrypt, may be due to bad plaintext split.", e);
        }
        return result;
    }
}
