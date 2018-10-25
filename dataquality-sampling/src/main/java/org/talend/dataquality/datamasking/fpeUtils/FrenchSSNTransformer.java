package org.talend.dataquality.datamasking.fpeUtils;

import org.talend.dataquality.datamasking.generic.GenerateUniqueRandomPatterns;
import org.talend.dataquality.datamasking.ssnUtils;

import java.math.BigInteger;
import java.util.List;

/**
 * @author afournier
 *
 * Transformer for French SSNs.
 */
public class FrenchSSNTransformer extends PatternTransformer {

    public FrenchSSNTransformer(int radix, GenerateUniqueRandomPatterns ssnPattern) {
        super(radix, ssnPattern);
    }

    public BigInteger getRank(String str) {

        // TODO :  Call UtilsSsnFr instead of ssnUtils
        List<String> strs = ssnUtils.splitFields(str);

        List<BigInteger> intFields = pattern.encodeFields(strs);

        return pattern.getRank(intFields);
    }
}
