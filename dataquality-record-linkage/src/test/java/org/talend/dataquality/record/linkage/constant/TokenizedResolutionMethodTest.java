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
package org.talend.dataquality.record.linkage.constant;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class TokenizedResolutionMethodTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod#getTypeByValueWithDefault(java.lang.String)}
     * case1: all of normal case
     */
    @Test
    public void testGetTypeByValueWithDefaultCase1() {
        for (TokenizedResolutionMethod type : TokenizedResolutionMethod.values()) {
            TokenizedResolutionMethod TokenizedResolutionMethodByName = TokenizedResolutionMethod
                    .getTypeByValueWithDefault(type.name());
            TokenizedResolutionMethod TokenizedResolutionMethodByComponentValue = TokenizedResolutionMethod
                    .getTypeByValueWithDefault(type.getComponentValue());
            TokenizedResolutionMethod TokenizedResolutionMethodByLName = TokenizedResolutionMethod
                    .getTypeByValueWithDefault(type.name().toLowerCase());
            TokenizedResolutionMethod TokenizedResolutionMethodByLComponentValue = TokenizedResolutionMethod
                    .getTypeByValueWithDefault(type.getComponentValue().toLowerCase());
            TokenizedResolutionMethod TokenizedResolutionMethodByUName = TokenizedResolutionMethod
                    .getTypeByValueWithDefault(type.name().toUpperCase());
            TokenizedResolutionMethod TokenizedResolutionMethodByUComponentValue = TokenizedResolutionMethod
                    .getTypeByValueWithDefault(type.getComponentValue().toUpperCase());

            assertEquals(TokenizedResolutionMethodByName, TokenizedResolutionMethod.NO);
            assertEquals(TokenizedResolutionMethodByLName, TokenizedResolutionMethod.NO);
            assertEquals(TokenizedResolutionMethodByUName, TokenizedResolutionMethod.NO);

            assertEquals(TokenizedResolutionMethodByComponentValue, type);
            assertEquals(TokenizedResolutionMethodByLComponentValue, type);
            assertEquals(TokenizedResolutionMethodByUComponentValue, type);

        }
    }

    /**
     * Test method for
     * {@org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod#getTypeByValue(java.lang.String)
     * 
     * 
     * } case2: input
     * is null empty or some other words
     */
    @Test
    public void testGetTypeByValueWithDefaultCase2() {
        TokenizedResolutionMethod TokenizedResolutionMethodByNull = TokenizedResolutionMethod.getTypeByValueWithDefault(null);
        assertEquals(TokenizedResolutionMethod.NO, TokenizedResolutionMethodByNull);
        TokenizedResolutionMethod TokenizedResolutionMethodByEmpty = TokenizedResolutionMethod.getTypeByValueWithDefault(""); //$NON-NLS-1$
        assertEquals(TokenizedResolutionMethod.NO, TokenizedResolutionMethodByEmpty);
        TokenizedResolutionMethod TokenizedResolutionMethodByOtherWord = TokenizedResolutionMethod
                .getTypeByValueWithDefault("111111"); //$NON-NLS-1$
        assertEquals(TokenizedResolutionMethod.NO, TokenizedResolutionMethodByOtherWord);
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod#getAllTypes()} .
     */
    @Test
    public void testGetAllTypes() {
        String[] allTypes = TokenizedResolutionMethod.getAllTypes();
        assertNotNull(allTypes);
        assertEquals(4, allTypes.length);
        assertEquals(4, TokenizedResolutionMethod.values().length);
    }
}
