package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.sampling.exception.DQException;

public class MaskEmailLocalPartByXTest {

    private String output;

    private String mail = "çéhœ.h啊hش@uestc.edu.cn";

    private String spemail = "hehe@telecom-bretagne.eu";

    private MaskEmailLocalPartByX maskEmailLocalPartByX = new MaskEmailLocalPartByX();

    @Test
    public void testEmpty() throws DQException {
        maskEmailLocalPartByX.setKeepEmpty(true);
        output = maskEmailLocalPartByX.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void test1Good() throws DQException {
        maskEmailLocalPartByX.parse("", false, new Random());
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("XXXXXXXXX@uestc.edu.cn", output);
    }

    @Test
    public void testNullParameter() throws DQException {
        maskEmailLocalPartByX.parse(null, false, new Random());
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("XXXXXXXXX@uestc.edu.cn", output);
    }

    @Test
    public void testSpecialEmail() throws DQException {
        maskEmailLocalPartByX.parse("", true, new Random(Long.valueOf(12345678)));
        output = maskEmailLocalPartByX.generateMaskedRow(spemail);
        Assert.assertEquals("XXXX@telecom-bretagne.eu", output);

    }

    @Test
    public void test2WithInput() throws DQException {
        maskEmailLocalPartByX.parse("hehe", false, new Random());
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("XXXXXXXXX@uestc.edu.cn", output);
    }

    @Test
    public void test2WithOneCharacter() throws DQException {
        maskEmailLocalPartByX.parse("A", false, new Random());
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("AAAAAAAAA@uestc.edu.cn", output);
    }

    @Test
    public void test2WithOneDigit() throws DQException {
        maskEmailLocalPartByX.parse("1", false, new Random());
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("XXXXXXXXX@uestc.edu.cn", output);
    }

    @Test
    public void test3NullEmail() throws DQException {
        maskEmailLocalPartByX.parse("", false, new Random());
        output = maskEmailLocalPartByX.generateMaskedRow(null);
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void test3KeepNullEmail() throws DQException {
        maskEmailLocalPartByX.parse("", true, new Random());
        output = maskEmailLocalPartByX.generateMaskedRow(null);
        Assert.assertTrue(output == null);
    }

    @Test
    public void test4EmptyEmail() throws DQException {
        maskEmailLocalPartByX.parse("", false, new Random());
        output = maskEmailLocalPartByX.generateMaskedRow("");
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void test5WrongFormat() throws DQException {
        maskEmailLocalPartByX.parse("", false, new Random());
        output = maskEmailLocalPartByX.generateMaskedRow("hehe");
        Assert.assertEquals("XXXX", output);
    }
}
