package org.talend.dataquality.datamasking.utils.crypto;

/**
 * This class contains the bound and offset to use when generating a random key for the basic method.
 *
 * @author afournier
 */
public abstract class BasicSpec {

    private BasicSpec() {

    }

    public static final int BASIC_KEY_OFFSET = 1000;

    public static final int BASIC_KEY_BOUND = 10000;

}
