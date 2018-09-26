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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.dataquality.utils.MockRandom;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.assertj.core.api.Assertions.*;

/**
 * created by jgonzalez on 20 août 2015 Detailled comment
 */
public class GenerateSsnUsTest {

    private String output;

    private GenerateSsnUs gsus = new GenerateSsnUs();

    @Before
    public void setUp() throws Exception {
        gsus.setRandom(new Random(42));
    }

    @Test
    public void testEmpty() {
        gsus.setKeepEmpty(true);
        output = gsus.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gsus.generateMaskedRow(null);
        assertEquals("530-49-7984", output); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gsus.keepNull = true;
        output = gsus.generateMaskedRow(null);
        assertNull(output);
    }

    @Test
    public void firstGroupCantBeTripleZero() {
        Random random = Mockito.mock(Random.class);
        Mockito.when(random.nextInt(anyInt())).thenReturn(0, 1);
        gsus.setRandom(random);
        output = gsus.generateMaskedRow(null);
        assertThat(output).startsWith("001");
    }

    @Test
    public void firstGroupCantBeTripleSix() {
        Random random = Mockito.mock(Random.class);
        Mockito.when(random.nextInt(anyInt())).thenReturn(666, 444);
        gsus.setRandom(random);
        output = gsus.generateMaskedRow(null);
        assertThat(output).startsWith("444");
    }

    @Test
    public void firstGroupCantBe9xx() {
        MockRandom random = new MockRandom();
        random.setNext(900);
        gsus.setRandom(random);
        output = gsus.generateMaskedRow(null);
        assertThat(output).startsWith("001");
    }

    @Test
    public void secondGroupCantBeDoubleZero() {
        Random random = Mockito.mock(Random.class);
        Mockito.when(random.nextInt(anyInt())).thenReturn(111, // first group
                0);
        gsus.setRandom(random);
        output = gsus.generateMaskedRow(null);
        assertThat(output).doesNotContain("-00-");
    }

    @Test
    public void secondGroupContainsOnlyTwoDigits() {
        MockRandom random = new MockRandom();
        random.setNext(98);
        gsus.setRandom(random);
        output = gsus.generateMaskedRow(null);
        assertThat(output.split("-")[1]).hasSize(2);
    }

    @Test
    public void secondGroupCantBeFourZero() {
        Random random = Mockito.mock(Random.class);
        Mockito.when(random.nextInt(anyInt())).thenReturn(1, // first group
                1, // second group
                0);
        gsus.setRandom(random);
        output = gsus.generateMaskedRow(null);
        assertThat(output).doesNotContain("-0000");
    }

    @Test
    public void secondGroupContainsOnlyFourDigits() {
        MockRandom random = new MockRandom();
        random.setNext(9997);
        gsus.setRandom(random);
        output = gsus.generateMaskedRow(null);
        assertThat(output).contains("-0001");
    }
}
