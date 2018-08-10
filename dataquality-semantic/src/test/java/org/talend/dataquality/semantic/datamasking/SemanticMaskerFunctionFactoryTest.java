// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.semantic.GenerateFromRegex;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.CustomDictionaryHolder;

@PrepareForTest({ CustomDictionaryHolder.class, CategoryRegistryManager.class })
public class SemanticMaskerFunctionFactoryTest extends CategoryRegistryManagerAbstract {

    @Test
    public void createMaskerFunctionForSemanticCategory() {
        Function<String> function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory("DE_POSTAL_CODE", null);
        Assert.assertEquals("ReplaceCharactersWithGeneration", function.getClass().getSimpleName());
    }

    @Test
    public void createDecimalMaskerFunctionForSemanticCategory() {
        Function<String> function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory("INVALID_NAME",
                "decimal");
        Assert.assertEquals("FluctuateNumericString", function.getClass().getSimpleName());
    }

    @Test
    public void createDateMaskerFunctionForSemanticCategory() {
        Function<String> function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory("INVALID_NAME", "date");
        Assert.assertEquals("DateFunctionAdapter", function.getClass().getSimpleName());
    }

    @Test
    public void createStringMaskerFunctionForSemanticCategory() {
        Function<String> function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory("INVALID_NAME",
                "string");
        Assert.assertEquals("ReplaceCharactersWithGeneration", function.getClass().getSimpleName());
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.semantic.datamasking.SemanticMaskerFunctionFactory#createMaskerFunctionForSemanticCategory(java.lang.String, java.lang.String, java.util.List, org.talend.dataquality.semantic.snapshot.DictionarySnapshot)}
     * .
     */
    @Test
    public void testCreateMaskerFunctionForSemanticCategoryStringStringListOfString() {
        // normal case
        Function<String> generateFromRegexFunction = SemanticMaskerFunctionFactory
                .createMaskerFunctionForSemanticCategory("FR_POSTAL_CODE", "integer", null, null); //$NON-NLS-1$ //$NON-NLS-2$
        generateFromRegexFunction.setRandom(new Random(100L));
        Assert.assertTrue("The Function should be instance of GenerateFromRegex class", //$NON-NLS-1$
                generateFromRegexFunction instanceof GenerateFromRegex);
        String generateMaskedRow = generateFromRegexFunction.generateMaskedRow("any input string"); //$NON-NLS-1$
        Assert.assertEquals("The mask result should be 02779", "02779", generateMaskedRow); //$NON-NLS-1$//$NON-NLS-2$

        // when input data from name change to id

        generateFromRegexFunction = SemanticMaskerFunctionFactory
                .createMaskerFunctionForSemanticCategory("583edc44ec06957a34fa643c", "integer", null, null); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.assertFalse("The Function should not be instance of GenerateFromRegex class", //$NON-NLS-1$
                generateFromRegexFunction instanceof GenerateFromRegex);

        // category and dataType is not exist case
        try {
            generateFromRegexFunction = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory("aaaaa", "bigdata", //$NON-NLS-1$//$NON-NLS-2$
                    null, null);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("There should be a IllegalArgumentException", true); //$NON-NLS-1$
            return;
        }
        Assert.assertTrue("this case there should be a exception", false); //$NON-NLS-1$
    }

}
