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
package org.talend.dataquality.datamasking.fpeUtils.pseudoRandomFunctions;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * This class is a pseduo-random function used in FF1 encryption.
 * It relies on the HMAC algorithm combined with SHA-2 hashing function.
 *
 * @author afournier
 * @see AesPrf
 * @see CryptoConstants
 * @see org.talend.dataquality.datamasking.SecretManager
 */
public class HmacPrf implements PseudoRandomFunction {

    private static final String MAC_ALGORITHM = CryptoConstants.HMAC_ALGORITHM;

    private static final String KEY_ALGORITHM_NAME = CryptoConstants.KEY_ALGORITHM;

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
