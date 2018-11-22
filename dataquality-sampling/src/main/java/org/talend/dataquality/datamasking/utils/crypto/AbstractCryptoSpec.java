package org.talend.dataquality.datamasking.utils.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class that contains the specifications of a {@link AbstractPrf}.
 * This specifications include :
 * <br>
 *     <ul><
 *       <li>The cipher algorithm</li>
 *       <li>The key algorithm used by the cipher</li>
 *       <li>The length of the key in bytes</li>
 *     /ul>
 *
 * @author afournier
 * @see CryptoFactory
 * @see AbstractPrf
 */
public abstract class AbstractCryptoSpec {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractCryptoSpec.class);

    /**
     * Algorithm used to encrypt data.
     */
    private final String cipherAlgorithm = getCipherAlgorithm();

    /**
     * Algorithm used to generate the Key.
     */
    private final String keyAlgorithm = getKeyAlgorithm();

    /**
     * Length in bytes of the key used for FPE.
     */
    private final int keyLength = getKeyLength();

    public abstract String getCipherAlgorithm();

    public abstract String getKeyAlgorithm();

    public abstract int getKeyLength();
}
