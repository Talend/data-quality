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
package org.talend.dataquality.semantic.classifier.custom;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.exception.DQSemanticRuntimeException;

public class UserDefinedRegexValidatorTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIsValidSEDOL() {
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("^(?<Sedol>[B-Db-dF-Hf-hJ-Nj-nP-Tp-tV-Xv-xYyZz\\d]{6}\\d)$");
        assertTrueDigits(validator);
        assertFalseDigits(validator);
        // Without checkout, these two digits are correct
        Assert.assertTrue(validator.isValid("5852844"));
        Assert.assertTrue(validator.isValid("5752842"));

        // Given correct sedol validator
        validator.setSubValidatorClassName("org.talend.dataquality.semantic.validator.impl.SedolValidator");
        assertTrueDigits(validator);
        assertFalseDigits(validator);
        // With checkout, these two digits are incorrect
        Assert.assertFalse(validator.isValid("5852844"));
        Assert.assertFalse(validator.isValid("5752842"));

        // Given wrong sedol validator, do same validator as to not set.
        try {
            validator.setSubValidatorClassName("org.talend.dataquality.semantic.validator.impl.SedolValidatorr");
            fail("Given validator name is invalid. An exception should be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
        assertTrueDigits(validator);
        assertFalseDigits(validator);
        // Without checkout, these two digits are correct
        Assert.assertTrue(validator.isValid("5852844"));
        Assert.assertTrue(validator.isValid("5752842"));

        // Given null sedol validator, do same validator as to not set.
        validator.setSubValidatorClassName(null);
        Assert.assertFalse(validator.isSetSubValidator());
        assertTrueDigits(validator);
        assertFalseDigits(validator);
        // Without checkout, these two digits are correct
        Assert.assertTrue(validator.isValid("5852844"));
        Assert.assertTrue(validator.isValid("5752842"));

        // Given empty sedol validator, do same validator as to not set.
        validator.setSubValidatorClassName("");
        Assert.assertFalse(validator.isSetSubValidator());
        assertTrueDigits(validator);
        assertFalseDigits(validator);
        // Without checkout, these two digits are correct
        Assert.assertTrue(validator.isValid("5852844"));
        Assert.assertTrue(validator.isValid("5752842"));
    }

    private void assertTrueDigits(UserDefinedRegexValidator validator) {
        Assert.assertTrue(validator.isValid("B0YBKL9"));
        Assert.assertTrue(validator.isValid("B000300"));
        Assert.assertTrue(validator.isValid("5852842"));
    }

    private void assertFalseDigits(UserDefinedRegexValidator validator) {
        Assert.assertFalse(validator.isValid("57.2842"));
        Assert.assertFalse(validator.isValid("*&JHE"));
        Assert.assertFalse(validator.isValid("hd8jsdf9"));
        Assert.assertFalse(validator.isValid(""));
        Assert.assertFalse(validator.isValid(" "));
        Assert.assertFalse(validator.isValid(null));
    }

    @Test
    public void testCasesenInsitive() {
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("^(?<Sedol>[B-Db-dF-Hf-hJ-Nj-nP-Tp-tV-Xv-xYyZz\\d]{6}\\d)$");
        Assert.assertTrue(validator.isValid("B0YBKL9"));
        Assert.assertTrue(validator.isValid("b0yBKL9"));
        validator.setCaseInsensitive(false);
        validator.setPatternString("^(?<Sedol>[B-Db-dF-Hf-hJ-Nj-nP-Tp-tV-Xv-xYyZz\\d]{6}\\d)$");
        // Since the regex itself is case sensitive considered, not match what value this parameter set, the result will
        // always be true.
        Assert.assertTrue(validator.isValid("b0yBKL9"));

        // If the pattern is not designed case-sensitive
        validator.setPatternString("^(?<Sedol>[B-DF-HJ-NP-TV-XYZ\\d]{6}\\d)$");
        Assert.assertFalse(validator.isValid("b0yBKL9"));

    }

    @Test
    public void testInvalidRegexString() {
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("1");
        Assert.assertFalse(validator.isValid("B0YBKL9"));
    }

    @Test
    public void testInvalidRegexStringNull() {
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        try {
            validator.setPatternString(null);
        } catch (DQSemanticRuntimeException e) {
            Assert.assertEquals(e.getMessage(), "null argument of patternString is not allowed.");
        }
    }

    @Test
    public void testInvalidRegexStringEmpty() {
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        try {
            validator.setPatternString("");
        } catch (RuntimeException e) {
            Assert.assertEquals(e.getMessage(), "null argument of patternString is not allowed.");
        }
    }
}
