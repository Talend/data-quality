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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.talend.dataquality.sampling.exception.DQException;

/**
 * DOC qzhao class global comment. Detailled comment
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MaskFullEmailDomainByXTest {

    private String output;

    private String mail = "hehe.hehe@çéhœh.啊hش.cn";

    private String spemail = "hehe@telecom-bretagne.eu";

    private String spemails = "hehe@tel-ecom-bretagne.hy-p-en.eu";

    private MaskFullEmailDomainByX maskEmailDomainByX = new MaskFullEmailDomainByX();

    @Test
    public void testEmpty() throws DQException {
        maskEmailDomainByX.setKeepEmpty(true);
        output = maskEmailDomainByX.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void test1Good() throws DQException {
        maskEmailDomainByX.parse("", false, new Random());
        output = maskEmailDomainByX.generateMaskedRow(mail);
        Assert.assertEquals("hehe.hehe@XXXXX.XXX.XX", output);
    }

    @Test
    public void testReal() throws DQException {
        maskEmailDomainByX.parse("", true, new Random(Long.valueOf(12345678)));
        output = maskEmailDomainByX.generateMaskedRow("dewitt.julio@hotmail.com");
        Assert.assertEquals("dewitt.julio@XXXXXXX.XXX", output);

    }

    @Test
    public void testSpecialEmail() throws DQException {
        maskEmailDomainByX.parse("", true, new Random(Long.valueOf(12345678)));
        output = maskEmailDomainByX.generateMaskedRow(spemail);
        Assert.assertEquals("hehe@XXXXXXXXXXXXXXXX.XX", output);

    }

    @Test
    public void testSpecialEmails() throws DQException {
        maskEmailDomainByX.parse("", true, new Random(Long.valueOf(12345678)));
        output = maskEmailDomainByX.generateMaskedRow(spemails);
        Assert.assertEquals("hehe@XXXXXXXXXXXXXXXXX.XXXXXXX.XX", output);

    }

    @Test
    public void test2WithInput() throws DQException {
        maskEmailDomainByX.parse("hehe", false, new Random());
        output = maskEmailDomainByX.generateMaskedRow(mail);
        Assert.assertEquals("hehe.hehe@XXXXX.XXX.XX", output);
    }

    @Test
    public void test2WithOneCharacter() throws DQException {
        maskEmailDomainByX.parse("A", false, new Random());
        output = maskEmailDomainByX.generateMaskedRow(mail);
        Assert.assertEquals("hehe.hehe@AAAAA.AAA.AA", output);
    }

    @Test
    public void test2WithOneDigit() throws DQException {
        maskEmailDomainByX.parse("1", false, new Random());
        output = maskEmailDomainByX.generateMaskedRow(mail);
        Assert.assertEquals("hehe.hehe@XXXXX.XXX.XX", output);
    }

    @Test
    public void test3NullEmail() throws DQException {
        maskEmailDomainByX.parse("", false, new Random());
        output = maskEmailDomainByX.generateMaskedRow(null);
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void test3KeepNullEmail() throws DQException {
        maskEmailDomainByX.parse("", true, new Random());
        output = maskEmailDomainByX.generateMaskedRow(null);
        Assert.assertTrue(output == null);
    }

    @Test
    public void test4EmptyEmail() throws DQException {
        maskEmailDomainByX.parse("", false, new Random());
        output = maskEmailDomainByX.generateMaskedRow("");
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void test5WrongFormat() throws DQException {
        maskEmailDomainByX.parse("", false, new Random());
        output = maskEmailDomainByX.generateMaskedRow("hehe");
        Assert.assertEquals("XXXX", output);
    }

}
