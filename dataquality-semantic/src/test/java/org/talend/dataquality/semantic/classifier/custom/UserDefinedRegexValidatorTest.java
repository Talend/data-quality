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

import static org.junit.Assert.*;

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

    @Test
    public void testIsValidURL() {
        // TDQ-14551: Support URLs with Asian characters
        // TDQ-14551: protocal://username:password@hostname
        String url0 = "https://www.talend.com";
        String url00 = "https://www.talend.com:8580";
        String url000 = "https://www.talend.com:8580/fr_di_introduction_metadatabridge.html?region=FR&type=visual";
        String url1 = "http://user@www.talend.com";
        String url11 = "http://user@www.talend.com:8580";
        String url2 = "ftp://user:pass@www.talend.com";
        String url22 = "ftp://user:pass@www.talend.com:8080";
        String url222 = "ftp://user:pass@www.talend.com:8080/metadata.html";
        String url03 = "https://例子.卷筒纸";
        String url04 = "http://引き割り.引き割り";
        String url05 = "http://하하하하.하하하하";
        String url3 = "ftp://user@例子.中華人民共和國";
        String url4 = "ftp://user:pass@引き割り.引き割り";
        String url44 = "ftp://user:pass@引き割り.引き割り/metadata.html";
        String url5 = "http://user:pass@하하하하.하하하하";
        String url6 = "http://例子:pass@例子.卷筒纸";
        String url66 = "http://例子:例子@例子.卷筒纸";
        String url7 = "http://user:引き割り@引き割り.引き割り";
        String url77 = "ftp://user:pass@引き割り.引き割り/引き割metadata.html";
        String url8 = "http://하하:하하@하하하하.하하하하";
        String url88 = "http://하하:하하@하하하하.하하하하/하하하하.html";
        String url888 = "https://用户:pass@例子.卷筒纸:8580/fr_di_introduction_metadatabridge.html?region=FR&type=visual";

        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString(
                "^(((?:ht|f)tps?)\\:\\/\\/)((([a-zA-Z0-9\\-\\._]|[\\u2E80-\\uFFFD])+(:[a-zA-Z0-9\\-\\._]+|:[\\u2E80-\\uFFFD]+)?@)?((?:([a-zA-Z0-9\\-\\._]|[\\u2E80-\\uFFFD])+(?:\\.([a-zA-Z0-9\\-\\._]|[\\u2E80-\\uFFFD])+)+)|localhost)(\\/?)([\\u2E80-\\uFFFD]*|[a-zA-Z0-9\\-\\.\\,\\'\\/\\+\\&%\\$_\\\\]*)?([\\d\\w\\.\\/\\%\\+\\-\\=\\&\\?\\:\\\"\\'\\,\\|\\~\\;#\\\\]*))$");

        Assert.assertTrue(validator.isValid(url0));
        Assert.assertTrue(validator.isValid(url00));
        Assert.assertTrue(validator.isValid(url000));
        Assert.assertTrue(validator.isValid(url1));
        Assert.assertTrue(validator.isValid(url11));
        Assert.assertTrue(validator.isValid(url2));
        Assert.assertTrue(validator.isValid(url22));
        Assert.assertTrue(validator.isValid(url222));
        Assert.assertTrue(validator.isValid(url03));
        Assert.assertTrue(validator.isValid(url04));
        Assert.assertTrue(validator.isValid(url05));
        Assert.assertTrue(validator.isValid(url3));
        Assert.assertTrue(validator.isValid(url4));
        Assert.assertTrue(validator.isValid(url44));
        Assert.assertTrue(validator.isValid(url5));
        Assert.assertTrue(validator.isValid(url6));
        Assert.assertTrue(validator.isValid(url66));
        Assert.assertTrue(validator.isValid(url7));
        Assert.assertTrue(validator.isValid(url77));
        Assert.assertTrue(validator.isValid(url8));
        Assert.assertTrue(validator.isValid(url88));
        Assert.assertTrue(validator.isValid(url888));
    }

    @Test
    public void testIsValidURL2() {
        // TDQ-14551: Support URLs with Asian characters
        // TDQ-14551: username:password@hostname
        String url0 = "www.talend.com";
        String url1 = "user@www.talend.com";
        String url2 = "user:pass@www.talend.com";
        String url03 = "例子.卷筒纸";
        String url04 = "引き割り.引き割り";
        String url05 = "하하하하.하하하하";
        String url3 = "user@例子.卷筒纸";
        String url4 = "user:pass@引き割り.引き割り";
        String url5 = "user:pass@하하하하.하하하하";
        String url6 = "例子:pass@例子.卷筒纸";
        String url7 = "user:引き割り@引き割り.引き割り";
        String url8 = "하하:하하@하하하하.하하하하";
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString(
                "^(([a-zA-Z0-9\\-\\._]|[\\u2E80-\\uFFFD])+(:[a-zA-Z0-9\\-\\._]+|[\\u2E80-\\uFFFD]+)?@)?((?:([a-zA-Z0-9\\-\\._]|[\\u2E80-\\uFFFD])+(?:\\.([a-zA-Z0-9\\-\\._]|[\\u2E80-\\uFFFD])+)+)|localhost)(\\/?)([a-zA-Z0-9\\-\\.\\,\\'\\/\\+\\&%\\$_\\\\]*)?([\\d\\w\\.\\/\\%\\+\\-\\=\\&\\?\\:\\\"\\'\\,\\|\\~\\;#\\\\]*)$");
        Assert.assertTrue(validator.isValid(url0));
        Assert.assertTrue(validator.isValid(url1));
        Assert.assertTrue(validator.isValid(url2));
        Assert.assertTrue(validator.isValid(url03));
        Assert.assertTrue(validator.isValid(url04));
        Assert.assertTrue(validator.isValid(url05));
        Assert.assertTrue(validator.isValid(url3));
        Assert.assertTrue(validator.isValid(url4));
        Assert.assertTrue(validator.isValid(url5));
        Assert.assertTrue(validator.isValid(url6));
        Assert.assertFalse(validator.isValid(url7));
        Assert.assertFalse(validator.isValid(url8));
    }

    @Test
    public void testIsValidURL3() {
        // TDQ-14551: Support URLs with Asian characters
        // TDQ-14551: username:password@
        String url0 = "";
        String url1 = "user:pass@";
        String url2 = "user@";
        String url3 = "例子:例子@";
        String url4 = "例子:pass@";
        String url5 = "user:引き割り@";
        String url6 = "하하:하하@";
        String url7 = "하하@";

        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("(([a-zA-Z0-9\\-\\._]|[\\u2E80-\\uFFFD])+(:[a-zA-Z0-9\\-\\._]+|[\\u2E80-\\uFFFD]+)?@)?");
        Assert.assertTrue(validator.isValid(url0));
        Assert.assertTrue(validator.isValid(url1));
        Assert.assertTrue(validator.isValid(url2));
        Assert.assertTrue(validator.isValid(url3));
        Assert.assertTrue(validator.isValid(url4));
        Assert.assertTrue(validator.isValid(url5));
        Assert.assertTrue(validator.isValid(url6));
        Assert.assertTrue(validator.isValid(url7));
    }

    @Test
    public void testIsValidURL4() {
        // TDQ-14551: Support URLs with Asian characters
        // TDQ-14551: protocal
        String url1 = "https://";
        String url2 = "ftp://";
        String url3 = "http://";
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("^((?:ht|f)tps?)\\:\\/\\/$");
        Assert.assertTrue(validator.isValid(url1));
        Assert.assertTrue(validator.isValid(url2));
        Assert.assertTrue(validator.isValid(url3));
    }

    @Test
    public void testIsValidURL5() {
        // TDQ-14551: Support URLs with Asian characters
        // TDQ-14551: host or username or password with Asian characters
        String url0 = "呵呵";
        String url1 = "中華人民共和國";
        String url2 = "引き割り";
        String url3 = "하하하하";
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("[\\u2E80-\\uFFFD]+");
        Assert.assertTrue(validator.isValid(url0));
        Assert.assertTrue(validator.isValid(url1));
        Assert.assertTrue(validator.isValid(url2));
        Assert.assertTrue(validator.isValid(url3));
    }
}
