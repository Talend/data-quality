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

import jdk.nashorn.internal.ir.annotations.Ignore;

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
    public void isInvalidRe2J() {
        UserDefinedRegexValidator validator = new UserDefinedRE2JRegexValidator();
        validator.setPatternString("^(?!01000|99999)(0[1-9]\\d{3}|[1-9]\\d{4})$"); // regex of DE_POSTAL_CODE
        Assert.assertFalse(validator.isValid("12345"));

        UserDefinedRegexValidator validatorJava = new UserDefinedRegexValidator();
        validatorJava.setPatternString("^(?!01000|99999)(0[1-9]\\d{3}|[1-9]\\d{4})$"); // regex of DE_POSTAL_CODE
        Assert.assertTrue(validatorJava.isValid("12345"));
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

    @Test
    public void testIsValidMailtoURL() throws IOException {
        // this testcase is to test SemanticCategoryEnum.MAILTO_URL's Validator can
        // TDQ-15407: Support Asian characters in URL semantic types
        // follow http://www.ietf.org/rfc/rfc2368.txt

        String[] validMailtoURLs = { "mailto:chris@example.com", "mailto:infobot@example.com?subject=current-issue",
                "mailto:infobot@example.com?body=send%20current-issue",
                "mailto:infobot@example.com?body=send%20current-" + "\\n" + "issue%0D%0Asend%20index",
                "mailto:majordomo@example.com?body=subscribe%20bamboo-l", "mailto:joe@example.com?cc=bob@example.com&body=hello",
                "mailto:?to=joe@example.com&cc=bob@example.com&body=hello",
                "mailto:?to=joe@xyz.com&amp;cc=bob@xyz.com&amp;body=hello",

                // comes from https://wiki.suikawiki.org/n/mailto
                // Within mailto URLs, the characters "?", "=", "&" are reserved.
                "mailto:gorby%25kremvax@example.com", "mailto:unlikely%3Faddress@example.com",
                "mailto:unlikely%3Faddress@example.com?blat=foop", "mailto:Mike%26family@example.org",
                "mailto:%22not%40me%22@example.org", "mailto:%22oh%5C%5Cno%22@example.org",
                "mailto:%22%5C%5C%5C%22it's%5C%20ugly%5C%5C%5C%22%22@example.org",

                // not ASCII
                "mailto:user@example.org?subject=caf%C3%A9", "mailto:user@example.org?subject=caf%C3%A9&body=caf%C3%A9", // caf=C3=A9
                "mailto:user@example.org?subject=%3D%3Futf-8%3FQ%3Fcaf%3DC3%3DA9%3F%3D", // utf-8 encoded
                "mailto:user@example.org?subject=%3D%3Fiso-8859-1%3FQ%3Fcaf%3DE9%3F%3D", // ISO-8859-1 encoded
                "mailto:user@%E7%B4%8D%E8%B1%86.example.org?subject=Test&body=NATTO", // user@xn--99zt52a.example.org

                // not ASCII
                "mailto:haorooms@Σελ.Σελδα", "mailto:Σελίδα@126.com", "mailto:haorooms@126.com?subject=α_Σελίδα",
                "mailto:Σελίδα@126.com?subject=α_Σελίδα", "mailto:user@例子.卷筒纸", "mailto:user@引き割り.引き割り", "mailto:user@하하하하.하하하하",
                "mailto:user@例子.中華人民共和國", "mailto:haorooms@126.com?subject=春节",

                "mailto:user@talend.com.cn", "mailto:user_cn@talend.com", "mailto:user-cn@talend.com",
                "mailto:user-cn@talend-cn.com", "mailto:user@example.com?subject=MessageTitle&body=MessageContent",
                "mailto:haorooms@126.com?subject=The%20subject%20of%20the%20mail",
                "mailto:haorooms@126.com?cc=name2@rapidtables.com&bcc=name3@rapidtables.com&subject=The%20subject%20of%20the%20email&body=The%20body%20of%20the%20email" };

        String[] invalidMailtoURLs = {
                // special charactors
                "mailto:unlikely?address@example.com?blat=foop", "mailto:Mike&family@example.org", "mailto:oh\\no@example.org",
                "mailto:\\\"it's ugly\\\"@example.org", "mailto:user@talend_cn.com", "mailto:user_cn@talend_cn.com",
                "mailto:user@example.com?subject=Message Title&body=Message Content",

                "user@192.168.1.1", "mailto:user@____.___", "", "-", "mailto:user", "user", "www.talend.com",
                "user@www.talend.com", "例子.卷筒纸", "user@例子.卷筒纸", "用户:pass@例子.卷筒纸", "例子@引き割り.引き割り", "하하@하하하하.하하하하" };

        ISemanticValidator validator = null;
        UserDefinedClassifier userDefinedClassifier = new UDCategorySerDeser().readJsonFile();
        Set<ISubCategory> classifiers = userDefinedClassifier.getClassifiers();
        for (ISubCategory iSubCategory : classifiers) {
            String name = iSubCategory.getLabel();
            if (SemanticCategoryEnum.MAILTO_URL.getDisplayName().equals(name)) {
                validator = iSubCategory.getValidator();
                break;
            }
        }
        if (validator != null) {
            for (String validMailtoURL : validMailtoURLs) {
                Assert.assertTrue("Invalid MailTo URL expected to be valid: " + validMailtoURL,
                        validator.isValid(validMailtoURL));
            }
            for (String invalidMailtoURL : invalidMailtoURLs) {
                Assert.assertFalse("Valid MailTo URL expected to be invalid: " + invalidMailtoURL,
                        validator.isValid(invalidMailtoURL));
            }
        }
    }

    @Test
    public void testIsValidFileURL() throws IOException {
        // this testcase is to test SemanticCategoryEnum.FILE_URL's Validator can
        // TDQ-15407: Support Asian characters in URL semantic types
        // follow https://tools.ietf.org/html/rfc8089

        String[] validFileURLs = { "file:///u/lai", "file:///u/lai/tik/tik76002/public_html/lermanfiles/chaps",
                "file:///u/lai/tik/tik76002/public_html/lerman.files/chaps",
                // Unix
                "file://localhost/etc/fstab", "file:///etc/fstab",

                // Windows
                "file://localhost/c$/WINDOWS/clock.avi", "file:///c:/WINDOWS/clock.avi", "file://hostname/path/to/the%20file.txt",
                "file:///c:/path/to/the%20file.txt",

                "file:///c:/WINDOWS/",

                // A traditional file URI for a local file with an empty authority
                "file:///c:/bar.txt", "file:///C:/MyDocuments/",
                // The minimal representation of a local file with no authority field
                // and an absolute path that begins with a slash "/"
                "file:/path/to/file", "file:c:/path/to/file",

                // old URI
                "file:///c|/path/to/file", "file:/c|/path/to/file", "file:c|/path/to/file",

                // For a network location
                "file://hostname/path/to/the%20file.txt",

                // the UNC String
                "file://host.example.com/Share/path/to/file.txt",

                // Non-local files with an explicit authority
                "file://host.example.com/path/to/file",
                // Non-local files with an empty authority and a complete (transformed) UNC string in the path
                "file:////host.example.com/path/to/file",
                // Non-local files with an extra slash between the empty authority and the transformed UNC
                // string
                "file://///host.example.com/path/to/file",

                // a network location
                "file://例子.卷筒纸/path/to/file", "file://引き割り.引き割り/path/引き/引き", "file://하하하하.하하하하/path/to/file",
                "file://例子.中華人民共和國/path/to/file", "file://例子.卷筒纸/path/納豆/file", "file://引き割り.引き割り/path/to/见佳琪",

                // local non-english
                "file:///引き割り/中華人民共和國/", "file:///C:/引き割り.引き割り/", "file:///C:/Σελδα.引き割り/", "file:///例子.卷筒纸/",
                "file:///Users/talend/例子", "file:///C:/Users/talend/例子/a_b/测试.txt", "file:///C:/Users/talend/例子/c-d/测试.txt" };

        String[] invalidFileURLs = { "", " ", "\\", "C:/My Documents/", "file:///C:/My Documents/", "/MyDocuments/",
                "file:///C:/My Documents/ALetter.html",

                // a file name can't contain any of the following characters:\/:*?"<>| at least on windows.
                "file:///C:/a?b.txt", "file:///C:/ab/e<f.txt", "file:///C:/ab/e>f.txt", "file:///C:/a*b/ef.txt",
                "file:///C:/a\"b.txt" };

        ISemanticValidator validator = null;
        UserDefinedClassifier userDefinedClassifier = new UDCategorySerDeser().readJsonFile();
        Set<ISubCategory> classifiers = userDefinedClassifier.getClassifiers();
        for (ISubCategory iSubCategory : classifiers) {
            String name = iSubCategory.getLabel();
            if (SemanticCategoryEnum.FILE_URL.getDisplayName().equals(name)) {
                validator = iSubCategory.getValidator();
                break;
            }
        }
        if (validator != null) {
            for (String validFileURL : validFileURLs) {
                Assert.assertTrue("Invalid File URL expected to be valid: " + validFileURL, validator.isValid(validFileURL));
            }
            for (String invalidFileURL : invalidFileURLs) {
                Assert.assertFalse("Valid File URL expected to be invalid: " + invalidFileURL, validator.isValid(invalidFileURL));
            }
        }
    }

    @Test
    public void testIsValidHdfsURL() throws IOException {
        // this testcase is to test SemanticCategoryEnum.HDFS_URL's Validator can
        // TDQ-15407: Support Asian characters in URL semantic types

        String[] validHdfsURLs = { "hdfs:///", "hdfs://hadoop1:9000", "hdfs://hadoopNS/data/file.csv", "hdfs://192.168.1.2:9000",
                "hdfs://192.168.1.2:9000/", "hdfs://localhost:9000/user",

                "hdfs://localhost:9000", "hdfs://localhost:9000/user/hadoop/test.txt",

                // special characters
                "hdfs://localhost:9000/user/app-logs/test.txt", "hdfs://localhost/user/spark_logs/spark_logs.txt",
                "hdfs://localhost:9000/user/hadoop/the%20file.txt",

                "hdfs://localhost:54310/user/hadoop/test.txt", "hdfs://tal-qa173.talend.lan:8020/user/automation/test/test.csv",
                "hdfs://tal-qa173.talend.lan/user/automation/test/test.csv", "hdfs:///user/automation/test/test.csv",

                // non-english
                "hdfs://192.168.1.2:9000/user/msjian/王府井.txt", "hdfs://192.168.1.2:9000/user/hadoop/Σελδα.txt",

                "hdfs://例子.卷筒纸:9000/user/msjian/王府井.txt", "hdfs://例子.卷筒纸/王府井/msjian/王府井.txt", "hdfs://引き割り.引き割り/引き/引き",
                "hdfs://하하하하.하하하하:9000/하하하하/하하하하.txt", "hdfs://例子.中華人民共和國/path/to/file",

                // local non-english
                "hdfs:///msjian/王府井.txt", "hdfs:///王府井/王府井", "hdfs:///引き/引き", "hdfs:///하하하하/하하하하.txt",

                "hdfs:///引き割り/中華人民共和國/", "hdfs:///C:/引き割り.引き割り/", "hdfs:///C:/Σελδα.引き割り/", "hdfs:///例子.卷筒纸/",
                "hdfs:///Users/talend/例子", "hdfs:///C:/Users/talend/例子/a_b/测试.txt", "hdfs:///C:/Users/talend/例子/c-d/测试.txt" };

        String[] invalidHdfsURLs = { "", " ", "\\", "//hadoop1:9000", "//localhost/test.csv", "hds://localhost:9000/user",
                "hdfs://localhost:9000/user/hadoop/the file.txt", "hdfs://192.168.1.2:9000/user/hadoop/Σελδα - Copy.txt",
                "C:/My Documents/", "hdfs:///C:/My Documents/", "/MyDocuments/", "hdfs:///C:/My Documents/ALetter.html",

                // a file name can't contain any of the following characters:\/:*?"<>| at least on windows.
                "hdfs:///C:/a?b.txt", "hdfs:///ab/e<f.txt", "hdfs:///ab/e>f.txt", "hdfs:///a*b/ef.txt", "hdfs:///a\"b.txt" };

        ISemanticValidator validator = null;
        UserDefinedClassifier userDefinedClassifier = new UDCategorySerDeser().readJsonFile();
        Set<ISubCategory> classifiers = userDefinedClassifier.getClassifiers();
        for (ISubCategory iSubCategory : classifiers) {
            String name = iSubCategory.getLabel();
            if (SemanticCategoryEnum.HDFS_URL.getDisplayName().equals(name)) {
                validator = iSubCategory.getValidator();
                break;
            }
        }
        if (validator != null) {
            for (String validHdfsURL : validHdfsURLs) {
                Assert.assertTrue("Invalid HDFS URL expected to be valid: " + validHdfsURL, validator.isValid(validHdfsURL));
            }
            for (String invalidHdfsURL : invalidHdfsURLs) {
                Assert.assertFalse("Valid HDFS URL expected to be invalid: " + invalidHdfsURL, validator.isValid(invalidHdfsURL));
            }
        }
    }
}
