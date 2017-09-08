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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.talend.dataquality.sampling.exception.DQException;

/**
 * DOC qzhao class global comment. Detailled comment
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MaskTopEmailDomainRandomlyTest {

    private String output;

    private String mailStandard = "hehe@gmail.com";

    private String mailMultipalDomaim = "hehe.haha@uestc.in.edu.cn";

    private MaskTopEmailDomainRandomly maskTopEmailDomainRandomly = new MaskTopEmailDomainRandomly();

    @Test
    public void testEmpty() throws DQException {
        maskTopEmailDomainRandomly.setKeepEmpty(true);
        output = maskTopEmailDomainRandomly.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testOneGoodStandard() throws DQException {
        maskTopEmailDomainRandomly.parse("gmail,test", false, new Random(24));
        output = maskTopEmailDomainRandomly.generateMaskedRow(mailStandard);
        Assert.assertEquals("hehe@test.com", output);
    }

    @Test
    public void testOneGoodStandardWithSpace() throws DQException {
        maskTopEmailDomainRandomly.parse("", false, new Random(24));
        output = maskTopEmailDomainRandomly.generateMaskedRow(mailStandard);
        Assert.assertEquals("hehe@.com", output);
    }

    @Test
    public void testSeveralGoodStandard() throws DQException {
        maskTopEmailDomainRandomly.parse("test1, test2, test3", false, new Random(24));
        List<String> results = Arrays.asList("hehe@test1.com", "hehe@test2.com", "hehe@test3.com");

        for (int i = 0; i < 20; i++) {
            output = maskTopEmailDomainRandomly.generateMaskedRow(mailStandard);
            Assert.assertTrue(results.contains(output));
        }
    }

    @Test
    public void test1SeveralGoodStandardWithSpace() throws DQException {
        maskTopEmailDomainRandomly.parse("test1, test2, ", false, new Random(24));
        List<String> results = Arrays.asList("hehe@test1.com", "hehe@test2.com");

        for (int i = 0; i < 20; i++) {
            output = maskTopEmailDomainRandomly.generateMaskedRow(mailStandard);
            Assert.assertTrue(results.contains(output));
        }
    }

    @Test
    public void testSeveralGoodMultipalDomaim() throws DQException {
        maskTopEmailDomainRandomly.parse("test1, test2, test3", false, new Random(24));
        List<String> results = Arrays.asList("hehe.haha@test1.cn", "hehe.haha@test2.cn", "hehe.haha@test3.cn");

        for (int i = 0; i < 20; i++) {
            output = maskTopEmailDomainRandomly.generateMaskedRow(mailMultipalDomaim);
            Assert.assertTrue(results.contains(output));
        }
    }

    @Test
    public void testOneGoodFromFile() throws URISyntaxException, DQException {
        String filePath = this.getClass().getResource("data/top-domain.txt").toURI().getPath();
        maskTopEmailDomainRandomly.parse(filePath, false, new Random(24));

        for (int i = 0; i < 20; i++) {
            output = maskTopEmailDomainRandomly.generateMaskedRow(mailStandard);
            Assert.assertTrue(!output.equals(mailStandard));
        }
    }

    @Test
    public void testServeralGoodFromFile() throws URISyntaxException, DQException {
        String filePath = this.getClass().getResource("data/top-domain.txt").toURI().getPath();
        maskTopEmailDomainRandomly.parse(filePath, false, new Random(24));

        for (int i = 0; i < 20; i++) {
            output = maskTopEmailDomainRandomly.generateMaskedRow(mailMultipalDomaim);
            Assert.assertTrue(!output.equals(mailStandard));
        }
    }

    @Test
    public void testNullEmail() throws DQException {
        maskTopEmailDomainRandomly.parse("", false, new Random());
        output = maskTopEmailDomainRandomly.generateMaskedRow(null);
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testKeepNullEmail() throws DQException {
        maskTopEmailDomainRandomly.parse("", true, new Random());
        output = maskTopEmailDomainRandomly.generateMaskedRow(null);
        Assert.assertTrue(output == null);
    }

    @Test
    public void testEmptyEmail() throws DQException {
        maskTopEmailDomainRandomly.parse("", false, new Random());
        output = maskTopEmailDomainRandomly.generateMaskedRow("");
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testWrongFormat() throws DQException {
        maskTopEmailDomainRandomly.parse("replace", false, new Random());
        output = maskTopEmailDomainRandomly.generateMaskedRow("hehe");
        Assert.assertEquals("replace", output);
    }
}
