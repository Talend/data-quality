package org.talend.dataquality.datamasking.functions;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.sampling.exception.DQException;

public class MaskEmailLocalPartRandomlyTest {

    private String output;

    private MaskEmailLocalPartRandomly maskEmailLocalPart = new MaskEmailLocalPartRandomly();

    private String mail = "jugonzalez@talend.com";

    @Test
    public void testEmpty() throws DQException {
        maskEmailLocalPart.setKeepEmpty(true);
        output = maskEmailLocalPart.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testOneGoodInput() throws DQException {
        maskEmailLocalPart.parse("test.com", false, new Random(42));
        output = maskEmailLocalPart.generateMaskedRow(mail);
        Assert.assertEquals(output, "test.com@talend.com");
    }

    @Test
    public void test1OneGoodInputWithSpace() throws DQException {
        maskEmailLocalPart.parse("", false, new Random(42));
        output = maskEmailLocalPart.generateMaskedRow(mail);
        Assert.assertEquals(output, "@talend.com");
    }

    @Test
    public void testServeralGoodInputs() throws DQException {
        maskEmailLocalPart.parse("aol.com, att.net, comcast.net, facebook.com, gmail.com, gmx.com", false, new Random(42));
        for (int i = 0; i < 20; i++) {
            output = maskEmailLocalPart.generateMaskedRow(mail);
            Assert.assertTrue(!output.equals(mail));
        }
    }

    @Test
    public void testServeralGoodInputsWithSpace() throws DQException {
        maskEmailLocalPart.parse("nelson  ,  quentin, ", false, new Random(42));
        List<String> results = Arrays.asList("nelson@talend.com", "quentin@talend.com");
        for (int i = 0; i < 20; i++) {
            output = maskEmailLocalPart.generateMaskedRow(mail);
            Assert.assertTrue(results.contains(output));
        }
    }

    @Test
    public void test1GoodLocalFile() throws URISyntaxException, DQException {
        String path = this.getClass().getResource("data/domain.txt").toURI().getPath();
        maskEmailLocalPart.parse(path, false, new Random(42));
        for (int i = 0; i < 20; i++) {
            output = maskEmailLocalPart.generateMaskedRow(mail);
            Assert.assertTrue(!output.equals(mail));
        }
    }

    @Test
    public void testNullEmail() throws DQException {
        maskEmailLocalPart.parse("hehe", false, new Random(42));
        output = maskEmailLocalPart.generateMaskedRow(null);
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testNotKeepNullEmail() throws DQException {
        maskEmailLocalPart.parse("hehe", true, new Random(42));
        output = maskEmailLocalPart.generateMaskedRow(null);
        Assert.assertTrue(output == null);
    }

    @Test
    public void testEmptyEmail() throws DQException {
        output = maskEmailLocalPart.generateMaskedRow("");
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testWrongFormat() throws DQException {
        maskEmailLocalPart.parse("replace", true, new Random(42));
        output = maskEmailLocalPart.generateMaskedRow("hehe");
        Assert.assertEquals("replace", output);
    }
}
