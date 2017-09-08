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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.sampling.exception.DQException;

/**
 * DOC qzhao class global comment. Detailled comment
 */
public class MaskTopEmailDomainByXTest {

    private String output;

    private String mailStandard = "hehe@gmail.com";

    private String mailWithPointsInLocal = "hehe.haha@çéhœش.com";

    private String mailMultipalDomaim = "hehe.haha@uestc.in.edu.cn";

    private MaskTopEmailDomainByX maskTopEmailDomainByX = new MaskTopEmailDomainByX();

    @Test
    public void testEmpty() throws DQException {
        maskTopEmailDomainByX.setKeepEmpty(true);
        output = maskTopEmailDomainByX.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGoodStandard() throws DQException {
        maskTopEmailDomainByX.parse("", false, new Random());
        output = maskTopEmailDomainByX.generateMaskedRow(mailStandard);
        Assert.assertEquals("hehe@XXXXX.com", output);
    }

    @Test
    public void testGoodWithPointsInLocal() throws DQException {
        maskTopEmailDomainByX.parse("", false, new Random());
        output = maskTopEmailDomainByX.generateMaskedRow(mailWithPointsInLocal);
        Assert.assertEquals("hehe.haha@XXXXX.com", output);
    }

    @Test
    public void testMultipalDomaim() throws DQException {
        maskTopEmailDomainByX.parse("", false, new Random());
        output = maskTopEmailDomainByX.generateMaskedRow(mailMultipalDomaim);
        Assert.assertEquals("hehe.haha@XXXXX.XX.XXX.cn", output);
    }

    @Test
    public void testOneCharacter() throws DQException {
        maskTopEmailDomainByX.parse("Z", false, new Random());
        output = maskTopEmailDomainByX.generateMaskedRow(mailMultipalDomaim);
        Assert.assertEquals("hehe.haha@ZZZZZ.ZZ.ZZZ.cn", output);
    }

    @Test
    public void testString() throws DQException {
        maskTopEmailDomainByX.parse("Zed", false, new Random());
        output = maskTopEmailDomainByX.generateMaskedRow(mailMultipalDomaim);
        Assert.assertEquals("hehe.haha@XXXXX.XX.XXX.cn", output);
    }

    @Test
    public void testOneDigit() throws DQException {
        maskTopEmailDomainByX.parse("Zed", false, new Random());
        output = maskTopEmailDomainByX.generateMaskedRow(mailMultipalDomaim);
        Assert.assertEquals("hehe.haha@XXXXX.XX.XXX.cn", output);
    }

    @Test
    public void testNullEmail() throws DQException {
        maskTopEmailDomainByX.parse("", false, new Random());
        output = maskTopEmailDomainByX.generateMaskedRow(null);
        Assert.assertEquals("", output);
    }

    @Test
    public void testKeepNullEmail() throws DQException {
        maskTopEmailDomainByX.parse("", true, new Random());
        output = maskTopEmailDomainByX.generateMaskedRow(null);
        Assert.assertEquals(output, output);
    }

    @Test
    public void testEmptyEmail() throws DQException {
        maskTopEmailDomainByX.parse("", false, new Random());
        output = maskTopEmailDomainByX.generateMaskedRow("");
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testWrongFormat() throws DQException {
        maskTopEmailDomainByX.parse("", false, new Random());
        output = maskTopEmailDomainByX.generateMaskedRow("hehe");
        Assert.assertEquals("XXXX", output);
    }

}
