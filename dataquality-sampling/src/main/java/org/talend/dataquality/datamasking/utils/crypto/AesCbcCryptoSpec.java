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

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractCryptoSpec.class);

    private int runtimeKeyLength;

    public AesCbcCryptoSpec() {
        runtimeKeyLength = computeKeyLength();
    }

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
        if (specVersion > 1.8 || (specVersion == 1.8 && implVersion >= 161)) {
            supportedLength = 32;
        } else {
            supportedLength = 16;
        }
        LOGGER.info("FPE supported key length for current java version is " + runtimeKeyLength + " bytes");

        return supportedLength;
    }
}
