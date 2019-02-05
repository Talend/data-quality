// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic.datamasking;

import com.mifmif.common.regex.Generex;
import org.apache.commons.lang3.StringUtils;
import org.talend.dataquality.datamasking.functions.FunctionString;
import org.talend.dataquality.semantic.utils.RegexUtils;

import java.util.Random;

import static org.talend.dataquality.datamasking.FunctionMode.CONSISTENT;

/**
 * Generate masking data from regex str
 */
public class GenerateFromRegex extends FunctionString {

    private static final long serialVersionUID = 2315410175790920472L;

    protected transient Generex generex = null;

    private transient String patternStr;

    private static final String[] invalidKw = { "(?:", "(?!", "(?=", "[[:space:]]", "[[:digit:]]", "\\u" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#doGenerateMaskedFieldWithRandom(java.lang.Object)
     */
    @Override
    protected String doGenerateMaskedField(String inputValue) {
        return doGenerateMaskedFieldWithRandom(inputValue, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        if (keepNull && str == null) {
            return null;
        }
        if (StringUtils.isEmpty(str)) {
            return EMPTY_STRING;
        }

        if (CONSISTENT == this.maskingMode) {
            Generex gen = new Generex(this.patternStr, r);
            return gen.random();
        } else {
            return generex.random();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#parse(java.lang.String, boolean, java.util.Random)
     */
    @Override
    public void parse(String extraParameter, boolean keepNullValues) {
        if (extraParameter != null) {
            patternStr = RegexUtils.removeStartingAndEndingAnchors(extraParameter);
            generex = new Generex(patternStr);
            setKeepNull(keepNullValues);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#setRandom(java.util.Random)
     */
    @Override
    public void setRandom(Random rand) {
        super.setRandom(rand);
        if (generex != null) {
            generex.setSeed(rnd.nextLong());
        }
    }

    public static boolean isValidPattern(String patternString) {
        if (patternString == null) {
            return false;
        }
        for (String keyWord : invalidKw) {
            if (patternString.contains(keyWord)) {
                return false;
            }
        }
        return Generex.isValidPattern(patternString);
    }

}