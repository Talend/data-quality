package org.talend.dataquality.datamasking.utils.crypto;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;

import javax.crypto.SecretKey;
import java.io.Serializable;

/**
 * Abstract class of the keyed pseudo-random functions used by {@link org.talend.dataquality.datamasking.SecretManager}
 * to generate unique {@link org.talend.dataquality.datamasking.generic.patterns.AbstractGeneratePattern}.
 *
 * @author afournier
 * @see org.talend.dataquality.datamasking.SecretManager
 * @see org.talend.dataquality.datamasking.generic.patterns.AbstractGeneratePattern
 * @see AbstractCryptoSpec
 * @see CryptoFactory
 */
public abstract class AbstractPrf implements PseudoRandomFunction, Serializable {

    private static final long serialVersionUID = -1056892850423616130L;

    protected AbstractCryptoSpec cryptoSpec;

    protected SecretKey secret;

    protected AbstractPrf(AbstractCryptoSpec cryptoSpec, SecretKey secret) {
        this.cryptoSpec = cryptoSpec;
        this.secret = secret;
    }
}
