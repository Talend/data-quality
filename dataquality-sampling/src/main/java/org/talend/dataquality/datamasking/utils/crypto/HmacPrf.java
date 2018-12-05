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

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * This class is a pseudo-random function used in FF1 encryption.
 * It relies on the HMAC algorithm combined with SHA-2 hashing function.
 *
 * @author afournier
 * @see AesPrf
 * @see HmacSha2CryptoSpec
 * @see org.talend.dataquality.datamasking.SecretManager
 */
public class HmacPrf extends AbstractPrf {

    private static final long serialVersionUID = 5826316461622875148L;

    private static final Logger LOGGER = LoggerFactory.getLogger(HmacPrf.class);

    public HmacPrf(AbstractCryptoSpec cryptoSpec, SecretKey secret) {
        super(cryptoSpec, secret);
    }

    @Override
    public byte[] apply(byte[] text) {
        try {
            Mac hmac = Mac.getInstance(cryptoSpec.getCipherAlgorithm());
            hmac.init(secret);
            return hmac.doFinal(text);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Invalid algorithm name defined in the specifications : " + cryptoSpec.getCipherAlgorithm(), e);
        } catch (InvalidKeyException e) {
            try {
                LOGGER.error("Invalid specifications for the cipher ! " + "Wrong key : "
                        + new String(secret.getEncoded(), secret.getFormat()) + " or wrong key algorithm :"
                        + cryptoSpec.getKeyAlgorithm(), e);
            } catch (UnsupportedEncodingException e1) {
                LOGGER.error("The secret has a format unsupported by java.String : " + secret.getFormat(), e1);
            }
        }
        return new byte[] {};
    }

}
