package org.talend.dataquality.datamasking.fpeUtils;

import com.idealista.fpe.config.Alphabet;

/**
 * @author afournier
 *
 * Binary Alphabet.
 */
public class BinaryAlphabet implements Alphabet {

    private static final char[] LOWER_CASE_CHARS = new char[] { '0', '1' };

    @Override
    public char[] availableCharacters() {
        return LOWER_CASE_CHARS;
    }

    @Override
    public Integer radix() {
        return LOWER_CASE_CHARS.length;
    }
}
