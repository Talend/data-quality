package org.talend.dataquality.datamasking.fpeUtils;

import org.talend.dataquality.datamasking.generic.GenerateUniqueRandomPatterns;
import java.math.BigInteger;

/**
 * @author afournier
 *
 * Transformer for French phone numbers.
 */
public class FrenchPhoneTransformer extends PatternTransformer {

    public FrenchPhoneTransformer(int radix, GenerateUniqueRandomPatterns ssnPattern) {
        super(radix, ssnPattern);
    }

    @Override
    public BigInteger getRank(String str) {
        return null;
    }
}
