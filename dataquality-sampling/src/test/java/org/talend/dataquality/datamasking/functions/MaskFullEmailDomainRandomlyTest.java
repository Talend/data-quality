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
package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * DOC qzhao class global comment. Detailled comment
 * 
 * This class tests with different email addresses and replaces the original domains
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MaskFullEmailDomainRandomlyTest {

    private String output;

    private MaskFullEmailDomainRandomly maskEmailDomainName = new MaskFullEmailDomainRandomly();

    private String mail = "jugonzalez@talend.com";

    @Before
    public void setUp() throws Exception {
        maskEmailDomainName.setRandom(new Random(42));
    }

    @Test
    public void testEmpty() {
        maskEmailDomainName.setKeepEmpty(true);
        output = maskEmailDomainName.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testOneGoodInput() {
        maskEmailDomainName.parse("test.com", false);
        output = maskEmailDomainName.generateMaskedRow(mail);
        Assert.assertEquals(output, "jugonzalez@test.com");
    }

    @Test
    public void test1OneGoodInputWithSpace() {
        maskEmailDomainName.parse("", false);
        output = maskEmailDomainName.generateMaskedRow(mail);
        Assert.assertEquals(output, "jugonzalez@");
    }

    @Test
    public void testServeralGoodInputs() {
        maskEmailDomainName.parse("aol.com, att.net, comcast.net, facebook.com, gmail.com, gmx.com", false);
        for (int i = 0; i < 20; i++) {
            output = maskEmailDomainName.generateMaskedRow(mail);
            Assert.assertTrue(!output.equals(mail));
        }
    }

    @Test
    public void testServeralGoodInputsWithSpace() {
        maskEmailDomainName.parse("aol.com, att.net, ", false);
        List<String> results = Arrays.asList("jugonzalez@aol.com", "jugonzalez@att.net");
        for (int i = 0; i < 20; i++) {
            output = maskEmailDomainName.generateMaskedRow(mail);
            Assert.assertTrue(results.contains(output));
        }
    }

    @Test
    public void test1GoodLocalFile() throws URISyntaxException {
        String path = this.getClass().getResource("data/domain.txt").toURI().getPath();
        maskEmailDomainName.parse(path, false);
        for (int i = 0; i < 20; i++) {
            output = maskEmailDomainName.generateMaskedRow(mail);
            Assert.assertTrue(!output.equals(mail));
        }
    }

    @Test
    public void testNullEmail() {
        maskEmailDomainName.parse("hehe", false);
        output = maskEmailDomainName.generateMaskedRow(null);
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testNotKeepNullEmail() {
        maskEmailDomainName.parse("hehe", true);
        output = maskEmailDomainName.generateMaskedRow(null);
        Assert.assertTrue(output == null);
    }

    @Test
    public void testEmptyEmail() {
        output = maskEmailDomainName.generateMaskedRow("");
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testWrongFormat() {
        maskEmailDomainName.parse("replace", true);
        output = maskEmailDomainName.generateMaskedRow("hehe");
        Assert.assertEquals("replace", output);
    }
}
