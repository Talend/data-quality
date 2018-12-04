package org.talend.dataquality.datamasking.functions;

import org.talend.dataquality.datamasking.FormatPreservingMethod;
import org.talend.dataquality.datamasking.SecretManager;

/**
 * This class is for Functions using a secret that needs to be handled by a {@link SecretManager}, such as a password
 * for Format-Preserving Encryption.
 *
 * @author afournier
 * @see SecretManager
 */
public abstract class AbstractGenerateWithSecret extends Function<String> {

    private static final long serialVersionUID = -4582206371693548066L;

    /**
     * The SecretManager handles keys and secret used to generate masked values.
     */
    protected SecretManager secretMng = new SecretManager();

    /**
     * This method overrides {@link Function#setSecret(String, String)} method which throws an exception by default.
     *
     * A new instance of {@link SecretManager} is created with the desired method and password for the format-preserving masking operation.
     *
     * @param method a String referring to a {@link FormatPreservingMethod}.
     * @param password the password entered by the user
     */
    @Override
    public void setSecret(String method, String password) {
        secretMng = new SecretManager(method, password);
    }
}
