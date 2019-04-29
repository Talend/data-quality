package org.talend.dataquality.datamasking.utils.crypto;

/**
 * This class contains the specifications for {@link AesPrf} pseudo-random function
 *
 * @author afournier
 * @see AesPrf
 */
public class AesCbcCryptoSpec implements AbstractCryptoSpec {

    private static final long serialVersionUID = -4824912287609169178L;

    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/NoPadding";

    private static final String AES_CIPHER_KEY_ALGORITHM = "AES";

    private static final int AES_CIPHER_KEY_LENGTH = 32;

    public AesCbcCryptoSpec() {
    }

    @Override
    public String getCipherAlgorithm() {
        return AES_CIPHER_ALGORITHM;
    }

    @Override
    public String getKeyAlgorithm() {
        return AES_CIPHER_KEY_ALGORITHM;
    }

    @Override
    public int getKeyLength() {
        return AES_CIPHER_KEY_LENGTH;
    }
}
