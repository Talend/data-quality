package org.talend.dataquality.datamasking.utils.crypto;

/**
 * Abstract class that contains the specifications of an {@link AbstractPrf} instance.
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
public interface AbstractCryptoSpec {

    String getCipherAlgorithm();

    String getKeyAlgorithm();

    int getKeyLength();
}
