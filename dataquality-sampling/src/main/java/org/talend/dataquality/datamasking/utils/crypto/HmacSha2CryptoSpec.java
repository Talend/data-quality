package org.talend.dataquality.datamasking.utils.crypto;

public class HmacSha2CryptoSpec extends AbstractCryptoSpec {

    @Override
    protected String cipherAlgo() {
        return "HmacSHA256";
    }

    @Override
    protected String keyAlgo() {
        return "PBKDF2WithHmacSHA256";
    }

    @Override
    protected int keyLength() {
        return 32;
    }
}
