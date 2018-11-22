package org.talend.dataquality.datamasking.utils.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCryptoSpec {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractCryptoSpec.class);

    /**
     * Algorithm used to encrypt data.
     */
    private final String cipherAlgorithm = cipherAlgo();

    /**
     * Algorithm used to generate the Key.
     */
    private final String keyAlgorithm = keyAlgo();

    /**
     * Length in bytes of the key used for FPE.
     */
    private final int keyLength = keyLength();

    protected abstract String cipherAlgo();

    protected abstract String keyAlgo();

    protected abstract int keyLength();

    public String getCipherAlgorithm() {
        return cipherAlgorithm;
    }

    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public int getKeyLength() {
        return keyLength;
    }
}
