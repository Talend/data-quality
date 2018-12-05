package org.talend.dataquality.datamasking.utils.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the specifications for {@link AesPrf} pseudo-random function
 *
 * @author afournier
 * @see AesPrf
 */
public class AesCbcCryptoSpec implements AbstractCryptoSpec {

    private static final long serialVersionUID = -4824912287609169178L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCryptoSpec.class);

    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/NoPadding";

    private static final String AES_CIPHER_KEY_ALGORITHM = "AES";

    private int runtimeKeyLength;

    public AesCbcCryptoSpec() {
        runtimeKeyLength = computeKeyLength();
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
        return runtimeKeyLength;
    }

    private int computeKeyLength() {
        String javaVersion = System.getProperty("java.runtime.version");
        String[] javaVersionElements = javaVersion.split("\\.|_|-b");

        String major1 = javaVersionElements[0];
        String major2 = javaVersionElements[1];
        String update = javaVersionElements[3];

        double specVersion = Double.parseDouble(major1 + "." + major2);
        double implVersion = Double.parseDouble(update);

        LOGGER.info("Java Runtime version : " + javaVersion);
        int supportedLength;
        if (specVersion > 1.8 || (Double.valueOf(1.8).equals(specVersion) && implVersion >= 161)) {
            supportedLength = 32;
        } else {
            supportedLength = 16;
        }
        LOGGER.info("FPE supported key length for current java version is " + runtimeKeyLength + " bytes");

        return supportedLength;
    }
}
