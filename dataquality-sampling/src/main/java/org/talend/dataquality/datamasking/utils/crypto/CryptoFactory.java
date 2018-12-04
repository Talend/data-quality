package org.talend.dataquality.datamasking.utils.crypto;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import org.talend.dataquality.datamasking.FormatPreservingMethod;

import javax.crypto.SecretKey;

/**
 * Factory for constructing the {@link AbstractPrf} and {@link AbstractCryptoSpec}
 * according to the context given by the {@link FormatPreservingMethod}.
 *
 * @author afournier
 * @see AbstractCryptoSpec
 * @see AbstractPrf
 * @see org.talend.dataquality.datamasking.SecretManager
 */
public class CryptoFactory {

    /**
     * Returns the correct instance of {@link AbstractCryptoSpec}
     * according to the context given by the {@link FormatPreservingMethod}.
     */
    public AbstractCryptoSpec getPrfSpec(FormatPreservingMethod method) {
        AbstractCryptoSpec cryptoSpec = null;

        switch (method) {
        case AES_CBC_PRF:
            cryptoSpec = new AesCbcCryptoSpec();
            break;
        case SHA2_HMAC_PRF:
            cryptoSpec = new HmacSha2CryptoSpec();
            break;
        default:
            break;
        }

        return cryptoSpec;
    }

    /**
     * Returns the correct keyed instance of {@link AbstractPrf)
     * according to the instance of {@link AbstractCryptoSpec}
     */
    public PseudoRandomFunction getPrf(AbstractCryptoSpec spec, SecretKey secret) {
        PseudoRandomFunction prf = null;

        if (spec instanceof AesCbcCryptoSpec) {
            prf = new AesPrf(spec, secret);
        } else if (spec instanceof HmacSha2CryptoSpec) {
            prf = new HmacPrf(spec, secret);
        }

        return prf;
    }
}
