package org.talend.dataquality.datamasking;

/**
 * Enumeration that lists the different format-preserving masking methods.
 *
 * @author afournier
 * @see SecretManager
 */
public enum FormatPreservingMethod {
    BASIC,
    AES_CBC_PRF,
    SHA2_HMAC_PRF
}
