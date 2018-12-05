package org.talend.dataquality.datamasking.utils.crypto;

/**
 * This class contains the specifications for {@link HmacPrf} pseudo-random function.
 *
 * @author afournier
 * @see HmacPrf
 */
public class HmacSha2CryptoSpec implements AbstractCryptoSpec {

    private static final long serialVersionUID = 255044036901853895L;

    private static final String SHA2_HMAC_ALGORITHM = "HmacSHA256";

    private static final String SHA2_HMAC_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final int SHA2_HMAC_KEY_LENGTH = 32;

    @Override
    public String getCipherAlgorithm() {
        return SHA2_HMAC_ALGORITHM;
    }

    @Override
    public String getKeyAlgorithm() {
        return SHA2_HMAC_KEY_ALGORITHM;
    }

    @Override
    public int getKeyLength() {
        return SHA2_HMAC_KEY_LENGTH;
    }
}
