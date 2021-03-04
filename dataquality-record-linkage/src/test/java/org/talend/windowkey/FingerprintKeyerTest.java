// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.windowkey;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * created by scorreia on Jul 10, 2012 Detailled comment
 * 
 */
public class FingerprintKeyerTest {

    @SuppressWarnings("nls")
    private static final String testStr[][] = { { "Acorn", "acorn" }, { "Woodpecker", "woodpecker" },
            { "Bird Conservation Region", "bird conservation region" }, { "15", "15" }, { "PT-r2", "ptr2" },
            { "élément", "element" }, { "32 €", "32 €" }, { "Acorn", "acorn" }, { "Aret Big Cust", "aret big cust" },
            { "Big Arêt Cust", "aret big cust" }, { "Cust Aret Big ", "aret big cust" },
            { "Cust-Aret Big ", "big custaret" }, { "Big Data for big business", "big business data for" },
            { "Data for big business", "big business data for" }, { "A A A", "a" }, { "I.BM.", "ibm" },
            { "I.B.M.", "ibm" }, { "IBM", "ibm" }, { "Bird Conservation Region", "bird conservation region" },
            { "Bird bird Conservation Region", "bird conservation region" }, { "15", "15" }, { "PT-r2", "ptr2" },
            { "élément", "element" }, { "32 €", "32 €" } };

    @SuppressWarnings("nls")
    private static final String asciiTestStr[][] = { { "Acorn", "Acorn" }, { "Woodpecker", "Woodpecker" },
            { "Bird Conservation Region", "Bird Conservation Region" }, { "15", "15" }, { "PT-r2", "PT-r2" },
            { "élément", "element" }, { "32 €", "32 €" } };

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.contribs.algorithm.FingerprintKeyer#key(java.lang.String)}.
     */
    @Test
    public void testKey() {
        FingerprintKeyer keyer = new FingerprintKeyer();
        for (String[] element : testStr) {
            assertEquals(element[1], keyer.key(element[0]));
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.contribs.algorithm.FingerprintKeyer#asciify(java.lang.String)}.
     */
    @Test
    public void testAsciify() {
        FingerprintKeyer keyer = new FingerprintKeyer();
        for (String[] element : asciiTestStr) {
            assertEquals(element[1], keyer.asciify(element[0]));
        }
    }

    @Test
    public void testTransalationWitha() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();

        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);

        char[] input = { '\u00C0', '\u00C1', '\u00C2', '\u00C3', '\u00C4', '\u00C5', '\u00E0', '\u00E1', '\u00E2',
                '\u00E3', '\u00E4', '\u00E5', '\u0100', '\u0101', '\u0102', '\u0103', '\u0104', '\u0105' };
        char expectedValue = 'a';

        for (int i = 0; i < input.length; i++) {

            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithc() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input =
                { '\u00C7', '\u00E7', '\u0106', '\u0107', '\u0108', '\u0109', '\u010A', '\u010B', '\u010C', '\u010D' };
        char expectedValue = 'c';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithd() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u00D0', '\u00F0', '\u010E', '\u010F', '\u0110', '\u0111' };
        char expectedValue = 'd';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithe() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u00C8', '\u00C9', '\u00CA', '\u00CB', '\u00E8', '\u00E9', '\u00EA', '\u00EB', '\u0112',
                '\u0113', '\u0114', '\u0115', '\u0116', '\u0117', '\u0118', '\u0119', '\u011A', '\u011B' };
        char expectedValue = 'e';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithg() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u011C', '\u011D', '\u011E', '\u011F', '\u0120', '\u0121', '\u0122', '\u0123' };
        char expectedValue = 'g';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithh() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u0124', '\u0125', '\u0126', '\u0127' };
        char expectedValue = 'h';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithi() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u00CC', '\u00CD', '\u00CE', '\u00CF', '\u00EC', '\u00ED', '\u00EE', '\u00EF', '\u0128',
                '\u0129', '\u012A', '\u012B', '\u012C', '\u012D', '\u012E', '\u012F', '\u0130', '\u0131' };
        char expectedValue = 'i';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithj() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u0134', '\u0135' };
        char expectedValue = 'j';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithk() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u0136', '\u0137', '\u0138' };
        char expectedValue = 'k';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithl() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input =
                { '\u0139', '\u013A', '\u013B', '\u013C', '\u013D', '\u013E', '\u013F', '\u0140', '\u0141', '\u0142' };
        char expectedValue = 'l';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithn() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u00D1', '\u00F1', '\u0143', '\u0144', '\u0145', '\u0146', '\u0147', '\u0148', '\u0149',
                '\u014A', '\u014B' };
        char expectedValue = 'n';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWitho() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u00D2', '\u00D3', '\u00D4', '\u00D5', '\u00D6', '\u00D8', '\u00F2', '\u00F3', '\u00F4',
                '\u00F5', '\u00F6', '\u00F8', '\u014C', '\u014D', '\u014E', '\u014F', '\u0150', '\u0151' };
        char expectedValue = 'o';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithr() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u0154', '\u0155', '\u0156', '\u0157', '\u0158', '\u0159' };
        char expectedValue = 'r';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWiths() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u015A', '\u015B', '\u015C', '\u015D', '\u015E', '\u015F', '\u0160', '\u0161', '\u017F' };
        char expectedValue = 's';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWitht() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u0162', '\u0163', '\u0164', '\u0165', '\u0166', '\u0167' };
        char expectedValue = 't';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithu() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u00D9', '\u00DA', '\u00DB', '\u00DC', '\u00F9', '\u00FA', '\u00FB', '\u00FC', '\u0168',
                '\u0169', '\u016A', '\u016B', '\u016C', '\u016D', '\u016E', '\u016F', '\u0170', '\u0171', '\u0172',
                '\u0173' };
        char expectedValue = 'u';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithw() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u0174', '\u0175' };
        char expectedValue = 'w';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithy() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u00DD', '\u00FD', '\u00FF', '\u0176', '\u0177', '\u0178' };
        char expectedValue = 'y';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }

    @Test
    public void testTransalationWithz() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        FingerprintKeyer keyer = new FingerprintKeyer();
        Method method = keyer.getClass().getDeclaredMethod("translate", new Class[] { char.class });
        method.setAccessible(true);
        char[] input = { '\u0179', '\u017A', '\u017B', '\u017C', '\u017D', '\u017E' };
        char expectedValue = 'z';
        for (int i = 0; i < input.length; i++) {
            Object invoke1 = method.invoke(keyer, input[i]);
            Assert.assertEquals(expectedValue, invoke1.toString().codePointAt(0));
        }

    }
}
