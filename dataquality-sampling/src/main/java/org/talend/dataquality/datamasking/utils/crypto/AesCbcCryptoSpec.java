package org.talend.dataquality.datamasking.utils.crypto;

public class AesCbcCryptoSpec extends AbstractCryptoSpec {

    @Override
    protected String cipherAlgo() {
        return "AES/CBC/NoPadding";
    }

    @Override
    protected String keyAlgo() {
        return "AES";
    }

    @Override
    protected int keyLength() {
        String major1, major2, update;
        String[] javaVersionElements = System.getProperty("java.runtime.version").split("\\.|_|-b");
        major1 = javaVersionElements[0];
        major2 = javaVersionElements[1];
        update = javaVersionElements[3];

        double specVersion = Double.parseDouble(major1 + "." + major2);
        double implVersion = Double.parseDouble(update);

        LOGGER.info("Java Runtime version : " + System.getProperty("java.runtime.version"));

        int supportedLength;
        if (specVersion > 1.8 || (specVersion == 1.8 && implVersion >= 161)) {
            supportedLength = 32;
        } else {
            supportedLength = 16;
        }

        LOGGER.info("FPE supported key length for current java version is " + supportedLength + " bytes");

        return supportedLength;
    }
}
