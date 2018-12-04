package org.talend.dataquality.datamasking.utils.crypto;

/**
 * This class contains the specifications for {@link HmacPrf} pseudo-random function.
 *
 * @author afournier
 * @see HmacPrf
 */
public class HmacSha2CryptoSpec implements AbstractCryptoSpec {

    @Override
    public String getCipherAlgorithm() {
        return "HmacSHA256";
    }

    @Override
    public String getKeyAlgorithm() {
        return "PBKDF2WithHmacSHA256";
    }

    @Override
    public int getKeyLength() {
        return 32;
    }
}
