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

import java.io.IOException;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.exception.DQSemanticRuntimeException;
import org.talend.dataquality.semantic.validator.ISemanticValidator;

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
    public void testIsValidURL() throws IOException {
        // this testcase is to test SemanticCategoryEnum.URL's Validator can
        // TDQ-14551: Support URLs with Asian characters
        // and will test URL step by step, the order is testIsValidURLPrepare1,2,3,4 and this.

        // protocal://username:password@hostname
        String[] urls = { "https://www.talend.com", "https://www.talend.com:8580/",
                "https://www.talend.com:8580/fr_di_introduction_metadatabridge.html?region=FR&type=visual",
                "http://user@www.talend.com/", "http://user@www.talend.com:8580", "ftp://user:pass@www.talend.com",
                "ftp://user:pass@www.talend.com:8080", "ftp://user:pass@www.talend.com:8080/metadata.html", "https://例子.卷筒纸",
                "http://引き割り.引き割り", "https://baike.baidu.com/item/中文", "http://하하하하.하하하하/", "ftp://user@例子.中華人民共和國",
                "ftp://user:pass@引き割り.引き割り", "ftp://user:pass@引き割り.引き割り/metadata.html", "http://user:pass@하하하하.하하하하",
                "http://例子:pass@例子.卷筒纸", "http://例子:例子@例子.卷筒纸", "http://user:引き割り@引き割り.引き割り",
                "ftp://user:pass@引き割り.引き割り/引き割metadata.html", "http://하하:하하@하하하하.하하하하", "http://하하:하하@하하하하.하하하하/하하하하.html",
                "https://用户:pass@例子.卷筒纸:8580/fr_di_introduction_metadatabridge.html?region=FR&type=visual" };

        ISemanticValidator validator = null;
        UserDefinedClassifier userDefinedClassifier = new UDCategorySerDeser().readJsonFile();
        Set<ISubCategory> classifiers = userDefinedClassifier.getClassifiers();
        for (ISubCategory iSubCategory : classifiers) {
            String name = iSubCategory.getLabel();
            if (SemanticCategoryEnum.URL.getDisplayName().equals(name)) {
                validator = iSubCategory.getValidator();
                break;
            }
        }
        if (validator != null) {
            for (String url : urls) {
                Assert.assertTrue(validator.isValid(url));
            }
            Assert.assertFalse(validator.isValid("abc"));
            Assert.assertFalse(validator.isValid("123.html"));
            Assert.assertFalse(validator.isValid("http://@123.html"));
        }
    }

    public void testIsValidURLPrepare4() {
        // TDQ-14551: Support URLs with Asian characters
        // username:password@hostname
        String[] logins = { "www.talend.com", "user@www.talend.com", "user:pass@www.talend.com", "例子.卷筒纸", "引き割り.引き割り",
                "하하하하.하하하하", "user@例子.卷筒纸", "user:pass@引き割り.引き割り", "user:pass@하하하하.하하하하", "例子:pass@例子.卷筒纸", "user:引き割り@引き割り.引き割り",
                "하하:하하@하하하하.하하하하" };
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString(
                "^((([a-zA-Z0-9\\-\\._]|[\\u2E80-\\uFFFD])+(:[a-zA-Z0-9\\-\\._]+|:[\\u2E80-\\uFFFD]+)?@)?((?:([a-zA-Z0-9\\-\\._]|[\\u2E80-\\uFFFD])+(?:\\.([a-zA-Z0-9\\-\\._]|[\\u2E80-\\uFFFD])+)+)|localhost)(\\/?)([\\u2E80-\\uFFFD]*|[a-zA-Z0-9\\-\\.\\,\\'\\/\\+\\&%\\$_\\\\]*)?([\\d\\w\\.\\/\\%\\+\\-\\=\\&\\?\\:\\\"\\'\\,\\|\\~\\;#\\\\]*))$");

        for (String login : logins) {
            Assert.assertTrue(validator.isValid(login));
        }
        Assert.assertFalse(validator.isValid("."));
        Assert.assertFalse(validator.isValid("@a"));
    }

    public void testIsValidURLPrepare3() {
        // TDQ-14551: Support URLs with Asian characters
        // username:password@
        String[] logins = { "", "user:pass@", "user@", "例子:例子@", "例子:pass@", "user:引き割り@", "하하:하하@", "하하@" };

        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("(([a-zA-Z0-9\\-\\._]|[\\u2E80-\\uFFFD])+(:[a-zA-Z0-9\\-\\._]+|[\\u2E80-\\uFFFD]+)?@)?");
        for (String login : logins) {
            Assert.assertTrue(validator.isValid(login));
        }
    }

    public void testIsValidURLPrepare2() {
        // TDQ-14551: Support URLs with Asian characters
        // protocal
        String[] protocals = { "https://", "ftp://", "http://" };
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("^((?:ht|f)tps?)\\:\\/\\/$");
        for (String protocal : protocals) {
            Assert.assertTrue(validator.isValid(protocal));
        }
        Assert.assertFalse(validator.isValid("1234"));
        Assert.assertFalse(validator.isValid("abc"));
    }

    public void testIsValidURLPrepare1() {
        // TDQ-14551: Support URLs with Asian characters
        // host or username or password with Asian characters
        String[] asianCharacters = { "呵呵", "中華人民共和國", "引き割り", "하하하하" };
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("[\\u2E80-\\uFFFD]+");

        for (String asianCharacter : asianCharacters) {
            Assert.assertTrue(validator.isValid(asianCharacter));
        }
        Assert.assertFalse(validator.isValid("מבשרת"));
    }
}
