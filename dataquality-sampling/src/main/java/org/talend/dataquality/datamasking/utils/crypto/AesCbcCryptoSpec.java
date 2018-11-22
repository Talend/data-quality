package org.talend.dataquality.datamasking.utils.crypto;

/**
 * This class contains the specifications for {@link AesPrf} pseudo-random function
 *
 * @author afournier
 * @see AesPrf
 */
public class AesCbcCryptoSpec extends AbstractCryptoSpec {

    @Override
    public String getCipherAlgorithm() {
        return "AES/CBC/NoPadding";
    }

    @Override
    public String getKeyAlgorithm() {
        return "AES";
    }

    @Override
    public int getKeyLength() {
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
