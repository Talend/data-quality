package org.talend.dataquality.datamasking.utils.crypto;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;

import javax.crypto.SecretKey;

public abstract class AbstractPrf implements PseudoRandomFunction {

    protected AbstractCryptoSpec cryptoSpec;

    protected AbstractPrf(AbstractCryptoSpec cryptoSpec) {
        this.cryptoSpec = cryptoSpec;
    }

    protected abstract void init(SecretKey secret);
}
