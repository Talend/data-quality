package org.talend.dataquality.datamasking.generic.patterns;

import java.math.BigInteger;
import java.util.List;

import org.talend.dataquality.datamasking.SecretManager;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;

import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;

public class EncryptionPatterns extends GenerateFormatPreservingPatterns {

    public EncryptionPatterns(List<AbstractField> fields) {
        super(fields);
    }

    public EncryptionPatterns(int radix, List<AbstractField> fields) {
        super(radix, fields);
    }

    /**
     * This method generates a unique pattern using FF1 encryption.
     * <br>
     * If the encrypted result is not a valid pattern, it is re-encrypted until the output is a valid pattern.
     * This method is called cycle-walking and ensures that the output is valid and unique for the original input.
     * <br>
     *
     * However this method can be slow if there are a lot of invalid values
     * in the domain used ({@code [0, radix^numeralRank.length[ }).
     * That is why we {@link #computeOptimalRadix(BigInteger)} is called during instantiation.
     *
     * @param strs the string fields to encode
     * @param secretMng, the SecretManager instance providing the secrets to generate a unique string
     *
     * @see #transform(int[])
     * @see #transform(List)
     */
    @Override
    public StringBuilder generateUniquePattern(List<String> strs, SecretManager secretMng) {
        int[] data = transform(strs);

        if (data.length == 0) {
            return null;
        }

        byte[] tweak = new byte[] {};
        PseudoRandomFunction prf = secretMng.getPseudoRandomFunction();

        int[] result = cipher.encrypt(data, radix, tweak, prf);

        while (!isValid(result)) {
            result = cipher.encrypt(result, radix, tweak, prf);
        }

        return transform(result);
    }
}
