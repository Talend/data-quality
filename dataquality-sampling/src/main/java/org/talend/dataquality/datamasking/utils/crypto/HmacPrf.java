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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * This class is a pseduo-random function used in FF1 encryption.
 * It relies on the HMAC algorithm combined with SHA-2 hashing function.
 *
 * @author afournier
 * @see AesPrf
 * @see HmacSha2CryptoSpec
 * @see org.talend.dataquality.datamasking.SecretManager
 */
public class HmacPrf extends AbstractPrf {

    private static final Logger LOGGER = LoggerFactory.getLogger(HmacPrf.class);

    private Mac hmac;

    public HmacPrf(AbstractCryptoSpec cryptoSpec, SecretKey secret) {
        super(cryptoSpec);
        init(secret);
    }

    @Override
    protected void init(SecretKey secret) {
        try {
            hmac = Mac.getInstance(cryptoSpec.getCipherAlgorithm());
            hmac.init(secret);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.error("Invalid crypto constant have been set for HMAC, see value of HMAC_ALGORITHM. ", e);
        }
    }

    @Override
    public byte[] apply(byte[] text) {
        return hmac.doFinal(text);
    }

}
