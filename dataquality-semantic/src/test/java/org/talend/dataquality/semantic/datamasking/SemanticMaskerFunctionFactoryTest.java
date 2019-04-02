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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.talend.dataquality.datamasking.FunctionType;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.semantic.CategoryRegistryManagerAbstract;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.CustomDictionaryHolder;

@PrepareForTest({ CustomDictionaryHolder.class, CategoryRegistryManager.class })
public class SemanticMaskerFunctionFactoryTest extends CategoryRegistryManagerAbstract {

    @Test
    public void createMaskerFunctionForSemanticCategory() {
        Function<String> function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory("DE_POSTAL_CODE", null);
        assertEquals("ReplaceCharactersWithGeneration", function.getClass().getSimpleName());
    }

    @Test
    public void createDecimalMaskerFunctionForSemanticCategory() {
        Function<String> function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory("INVALID_NAME",
                "decimal");
        assertEquals("FluctuateNumericString", function.getClass().getSimpleName());
    }

    @Test
    public void createDateMaskerFunctionForSemanticCategory() {
        Function<String> function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory("INVALID_NAME", "date");
        assertEquals("DateFunctionAdapter", function.getClass().getSimpleName());
    }

    @Test
    public void createStringMaskerFunctionForSemanticCategory() {
        Function<String> function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory("INVALID_NAME",
                "string");
        assertEquals("ReplaceCharactersWithGeneration", function.getClass().getSimpleName());
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
        assertTrue("The Function should be instance of GenerateFromRegex class", //$NON-NLS-1$
                generateFromRegexFunction instanceof GenerateFromRegex);
        String generateMaskedRow = generateFromRegexFunction.generateMaskedRow("any input string"); //$NON-NLS-1$
        assertEquals("The mask result should be 02779", "02779", generateMaskedRow); //$NON-NLS-1$//$NON-NLS-2$

        // when input data from name change to id

        generateFromRegexFunction = SemanticMaskerFunctionFactory
                .createMaskerFunctionForSemanticCategory("583edc44ec06957a34fa643c", "integer", null, null); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse("The Function should not be instance of GenerateFromRegex class", //$NON-NLS-1$
                generateFromRegexFunction instanceof GenerateFromRegex);

        // category and dataType is not exist case
        try {
            generateFromRegexFunction = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory("aaaaa", "bigdata", //$NON-NLS-1$//$NON-NLS-2$
                    null, null);
        } catch (IllegalArgumentException e) {
            assertTrue("There should be a IllegalArgumentException", IllegalArgumentException.class.equals(e.getClass())); //$NON-NLS-1$
            return;
        }
        fail("expected to get an exception but actually not"); //$NON-NLS-1$
    }

    @Test
    public void testGetFunctionByType() {
        Function function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.BETWEEN_INDEXES_KEEP,
                "string", "2,10");
        assertEquals(FunctionType.BETWEEN_INDEXES_KEEP.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.BETWEEN_INDEXES_REMOVE, "string",
                "2,10");
        assertEquals(FunctionType.BETWEEN_INDEXES_REMOVE.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.BETWEEN_INDEXES_REPLACE, "string",
                "2,10");
        assertEquals(FunctionType.BETWEEN_INDEXES_REPLACE.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.KEEP_YEAR, "string", "2,10");
        assertNotEquals(FunctionType.KEEP_YEAR.getClazz(), function.getClass());
        assertEquals(org.talend.dataquality.datamasking.semantic.DateFunctionAdapter.class, function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.GENERATE_FROM_PATTERN, "String",
                "2,10");
        assertEquals(FunctionType.GENERATE_FROM_PATTERN.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.REPLACE_ALL, "String", "X");
        assertEquals(FunctionType.REPLACE_ALL.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.REPLACE_CHARACTERS_BIJECTIVE,
                "String", "X");
        assertEquals(FunctionType.REPLACE_CHARACTERS.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.REPLACE_FIRST_CHARS, "string",
                "10");
        assertEquals(FunctionType.REPLACE_FIRST_CHARS_STRING.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.REPLACE_LAST_CHARS, "string", "5");
        assertEquals(FunctionType.REPLACE_LAST_CHARS_STRING.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.REPLACE_NUMERIC, "integer", "5");
        assertEquals(FunctionType.REPLACE_NUMERIC_INT.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.KEEP_FIRST_AND_GENERATE, "string",
                "2");
        assertEquals(FunctionType.KEEP_FIRST_AND_GENERATE_STRING.getClazz(), function.getClass());

        function = SemanticMaskerFunctionFactory.getMaskerFunctionByFunctionName(FunctionType.KEEP_LAST_AND_GENERATE, "string",
                "2");
        assertEquals(FunctionType.KEEP_LAST_AND_GENERATE_STRING.getClazz(), function.getClass());

    }

}
