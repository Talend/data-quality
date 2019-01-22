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

import jdk.nashorn.internal.ir.annotations.Ignore;

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
    public void testCaseInsensitive() {
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
        // TDQ-14551: Support URLs with Any other characters
        // and will test URL step by step, the order is testIsValidURLPrepare1,2,3,4 and this.

        // protocal://username:password@hostname
        String[] validURLs = { "https://www.talend.com", "https://www.talend.com:8580/", "http://192.168.1.1",
                "http://192.168.1.1:8580", "http://user:132@192.168.1.1:8580", "http://user:a123@192.168.1.1:8580/index0.html",
                "https://www.talend.com:8580/fr_di_introduction_metadatabridge.html?region=FR&type=visual",
                "http://user@www.talend.com/", "http://user@www.talend.com:8580", "ftp://user:pass@www.talend.com",
                "ftp://user:pass@www.talend.com:8080", "ftp://user:pass@www.talend.com:8080/metadata.html", "https://例子.卷筒纸",
                "http://引き割り.引き割り", "https://baike.baidu.com/item/中文", "http://하하하하.하하하하/", "ftp://user@例子.中華人民共和國",
                "ftp://user:pass@引き割り.引き割り", "ftp://user:pass@引き割り.引き割り/metadata.html", "http://user:pass@하하하하.하하하하",
                "http://例子:pass@例子.卷筒纸", "http://例子:例子@例子.卷筒纸", "http://user:引き割り@引き割り.引き割り",
                "ftp://user:pass@引き割り.引き割り/metadata.html", "https://user:pass@引き割り.引き割り:8080/metadata.html",
                "http://하하:하하@하하하하.하하하하", "http://하하:하하@하하하하.하하하하/하하하하.html",
                "https://用户:pass@例子.卷筒纸:8580/fr_di_introduction_metadatabridge.html?region=FR&type=visual",
                "http://www.baidu.com/s?wd=", "http://www.baidu.com/s?wd=春节", "http://hehe:例子@吉田あいうえお.卷筒纸",
                "http://𠁁𠁂𠁃@www.talend.com", "ftp://𠀀𠀁𠀂𠀃𠀄:𠁁𠁂𠁃@www.talend.com", "ftp://𠀀𠀁𠀂𠀃𠀄:𠁁𠁂𠁃@𠁁𠁂.𠀂𠀃𠀄",
                "http://site.com/Μία_Σελίδα", "ftp://Σελίδα@site.com/Μία_Σελίδα", "http://site.com/מבשרת",
                "https://www.talend.com.cn", "https://www.talend-cn.com", "https://www.talend_cn.com",
                "ftp://user-cn:pass@www.talend.com:8080", "ftp://user_cn:pass@www.talend.com:8080",
                "ftp://user:pass_cn@www.talend-cn.com:8080", "ftp://user:pass-cn@www.talend_cn.com:8080",
                "sftp://user:pass@引き割り.引き割り", "http://localhost", "http://localhost:8580", "http://localhost:8580/index.html",
                "http://aa:bb@localhost:8580/index.html", "http://用户:密码@localhost:8580/index.html" };

        String[] invalidURLs = { "https://.....com", "http://____.___", "", "-", "abc", "123.html", "http://@123.html",
                "www.talend.com", "user:pass@www.talend.com", "例子.卷筒纸", "user@例子.卷筒纸", "用户:pass@例子.卷筒纸", "引き割り.引き割り",
                "例子.卷筒纸@引き割り.引き割り", "하하:하하@하하하하.하하하하", "https://引き割り.引き割り:8080/引き割metadata.html",
                "ftp://login:motdepasse@adresse:443/nomdufichier" };

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
            for (String validURL : validURLs) {
                Assert.assertTrue("Invalid URL expected to be valid: " + validURL, validator.isValid(validURL));
            }
            for (String invalidURL : invalidURLs) {
                Assert.assertFalse("Valid URL expected to be invalid: " + invalidURL, validator.isValid(invalidURL));
            }
        }
    }

    @Ignore
    public void testIsValidURLPrepare4() {
        // TDQ-14551: Support URLs with Any other characters
        // username:password@hostname
        String[] logins = { "a:b@192.168.30.10", "a:123@192.168.30.10", "a123:123@192.168.30.10", "www.talend.com",
                "user@www.talend.com", "user:pass@www.talend.com", "例子.卷筒纸", "引き割り.引き割り", "하하하하.하하하하", "user@例子.卷筒纸",
                "user:pass@引き割り.引き割り", "user:pass@하하하하.하하하하", "例子:pass@例子.卷筒纸", "user:引き割り@引き割り.引き割り", "하하:하하@하하하하.하하하하" };
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString(
                "^(((\\p{L}|\\p{N})+(:(\\p{L}|\\p{N})+)?@)?((?:(\\p{L}|\\p{N})+(?:\\.(\\p{L}|\\p{N})+)+)|localhost)(\\/?)((\\p{L}|\\p{N})*)(([\\d\\w\\.\\/\\%\\+\\-\\=\\&\\?\\:\\\"\\'\\,\\|\\~\\;#\\\\]*(\\p{L}|\\p{N})*)|(\\p{L}|\\p{N})*)?)$");
        for (String login : logins) {
            Assert.assertTrue(validator.isValid(login));
        }
        Assert.assertFalse(validator.isValid("."));
        Assert.assertFalse(validator.isValid("@a"));

    }

    @Ignore
    public void testIsValidURLPrepare3() {
        // TDQ-14551: Support URLs with Any other characters
        // username:password@
        String[] logins = { "", "user:pass@", "user@", "user:1a123456@", "a123:123456@", "123:123456@", "例子:例子@", "例子:pass@",
                "user:引き割り@", "하하:하하@", "하하@" };
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("^((\\p{L}|\\p{N})+(:(\\p{L}|\\p{N})+)?@)?$");
        for (String login : logins) {
            Assert.assertTrue(validator.isValid(login));
        }
    }

    @Ignore
    public void testIsValidURLPrepare2() {
        // TDQ-14551: Support URLs with Any other characters
        // protocal
        String[] protocals = { "https://", "ftp://", "http://", "sftp://" };
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("^((?:ht|s?f)tps?)\\:\\/\\/$");
        for (String protocal : protocals) {
            Assert.assertTrue(validator.isValid(protocal));
        }
        Assert.assertFalse(validator.isValid("1234"));
        Assert.assertFalse(validator.isValid("abc"));
    }

    @Ignore
    public void testIsValidURLPrepare1() {
        // TDQ-14551: Support URLs witsh Any other characters
        // host or username or password with any other characters
        String[] anyCharacters = { "呵呵", "中華人民共和國", "引き割り", "하하하하", "吉田あいうえお", "𠀡𠀢", "𠁁𠁂𠁃", "𠀀𠀁𠀂𠀃𠀄", "Μία_Σελίδα",
                "呵呵-._:@,'/+&%$\\=\"|~;#", "מבשרת", "11", "123呵呵123", "a123" };
        UserDefinedRegexValidator validator = new UserDefinedRegexValidator();
        validator.setPatternString("(\\p{L}|\\p{N})*");
        for (String asianCharacter : anyCharacters) {
            Assert.assertTrue(validator.isValid(asianCharacter));
        }
    }

}
