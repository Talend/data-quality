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

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
 * @see CryptoConstants
 */
public class AesPrf implements PseudoRandomFunction {

    private static final String AES_ALGORITHM = CryptoConstants.AES_ALGORITHM;

    private static final String KEY_ALGORITHM_NAME = CryptoConstants.AES_KEY_ALGORITHM;

    private static final Logger LOGGER = LoggerFactory.getLogger(AesPrf.class);

    private Cipher cipher;

    private final byte[] initializationVector = Arrays.copyOf(new byte[] { 0x00 }, 16);

    public AesPrf(SecretKey secret) {
        try {
            cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec spec = new SecretKeySpec(secret.getEncoded(), KEY_ALGORITHM_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, spec, new IvParameterSpec(initializationVector));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            LOGGER.error("Invalid crypto constants have been set for AES, see values of AES_KEY_ALGORITHM and AES_ALGORITHM. ",
                    e);
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
