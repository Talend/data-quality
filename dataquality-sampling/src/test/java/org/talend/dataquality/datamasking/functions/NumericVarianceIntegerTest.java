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

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */

public class NumericVarianceIntegerTest {

    private String output;

    private Integer input = 123;

    private NumericVarianceInteger nvi = new NumericVarianceInteger();

    @Test
    public void testGood() {
        nvi.parse("10", false, new Random(42));
        output = nvi.generateMaskedRow(input).toString();
        assertEquals(-7, nvi.rate);
        assertEquals(output, String.valueOf(115));
    }

    @Test
    public void testNullParameter() {
        nvi.parse(null, false, new Random(8766));
        output = nvi.generateMaskedRow(input).toString();
        assertEquals(9, nvi.rate);
        assertEquals(output, String.valueOf(134));
    }

    @Test
    public void testDummy() {
        nvi.parse("-10", false, new Random(42));
        output = nvi.generateMaskedRow(input).toString();
        assertEquals(-7, nvi.rate);
        assertEquals(String.valueOf(115), output);
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#doGenerateMaskedField(Integer)}
     */
    @Test
    public void testOverFlowCase() {
        // Before OverFlow Case 99999999+20*99999999/100=119999998
        nvi.parse("30", false, new Random(42));
        output = nvi.generateMaskedRow(99999999).toString();
        assertEquals(20, nvi.rate);
        assertEquals(String.valueOf(119999998), output);
        // over flow case for -237*99999999
        nvi.parse("3000", false, new Random(42));
        output = nvi.generateMaskedRow(99999999).toString();
        assertEquals(-1870, nvi.rate);
        assertEquals(String.valueOf(79000000), output);
        // over flow case for 1248*99999999
        nvi.parse("30000", false, new Random(42));
        output = nvi.generateMaskedRow(99999999).toString();
        assertEquals(1130, nvi.rate);
        assertEquals(String.valueOf(120999998), output);
        // over flow case for 18884*-99999999
        nvi.parse("30000", false, new Random(42));
        output = nvi.generateMaskedRow(-99999999).toString();
        assertEquals(1130, nvi.rate);
        assertEquals(String.valueOf(-120999998), output);
        // over flow case for -2030*-99999999
        nvi.parse("-4000", false, new Random(42));
        output = nvi.generateMaskedRow(-99999999).toString();
        assertEquals(1130, nvi.rate);
        assertEquals(String.valueOf(-120999998), output);
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverAddResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case1 Integer.MAX_VALUE+Integer.MIN_VALUE=-1
     */
    @Test
    public void testGetNonOverAddResultCase1() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverAddResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverAddResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverAddResultMethod.setAccessible(true);
        Object invoke = getNonOverAddResultMethod.invoke(new NumericVarianceInteger(),
                new Object[] { Integer.MAX_VALUE, Integer.MIN_VALUE });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be -1 but it is " + invoke.toString(), -1, invoke); //$NON-NLS-1$
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverAddResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case2 Integer.MAX_VALUE+Integer.MAX_VALUE==Integer.MAX_VALUE-Integer.MAX_VALUE==0
     */
    @Test
    public void testGetNonOverAddResultCase2() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverAddResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverAddResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverAddResultMethod.setAccessible(true);
        Object invoke = getNonOverAddResultMethod.invoke(new NumericVarianceInteger(),
                new Object[] { Integer.MAX_VALUE, Integer.MAX_VALUE });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be 0 but it is " + invoke.toString(), 0, invoke); //$NON-NLS-1$
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverAddResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case3 Integer.MAX_VALUE+0==Integer.MAX_VALUE
     */
    @Test
    public void testGetNonOverAddResultCase3() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverAddResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverAddResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverAddResultMethod.setAccessible(true);
        Object invoke = getNonOverAddResultMethod.invoke(new NumericVarianceInteger(), new Object[] { Integer.MAX_VALUE, 0 });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be Integer.MAX_VALUE but it is " + invoke.toString(), Integer.MAX_VALUE, //$NON-NLS-1$
                invoke);
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverAddResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case4 Integer.MAX_VALUE+1==Integer.MAX_VALUE-1=2147483646
     */
    @Test
    public void testGetNonOverAddResultCase4() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverAddResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverAddResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverAddResultMethod.setAccessible(true);
        Object invoke = getNonOverAddResultMethod.invoke(new NumericVarianceInteger(), new Object[] { Integer.MAX_VALUE, 1 });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be 2147483646 but it is " + invoke.toString(), 2147483646, invoke); //$NON-NLS-1$
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverAddResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case5 Integer.MIN_VALUE+Integer.MIN_VALUE==Integer.MIN_VALUE-Integer.MIN_VALUE=0
     */
    @Test
    public void testGetNonOverAddResultCase5() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverAddResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverAddResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverAddResultMethod.setAccessible(true);
        Object invoke = getNonOverAddResultMethod.invoke(new NumericVarianceInteger(),
                new Object[] { Integer.MIN_VALUE, Integer.MIN_VALUE });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be 0 but it is " + invoke.toString(), 0, invoke); //$NON-NLS-1$
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverAddResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case6 Integer.MIN_VALUE+-1==Integer.MIN_VALUE+1=-2147483647
     */
    @Test
    public void testGetNonOverAddResultCase6() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverAddResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverAddResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverAddResultMethod.setAccessible(true);
        Object invoke = getNonOverAddResultMethod.invoke(new NumericVarianceInteger(), new Object[] { Integer.MIN_VALUE, -1 });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be -2147483647 but it is " + invoke.toString(), -2147483647, invoke); //$NON-NLS-1$
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverAddResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case7 Integer.MIN_VALUE+0==Integer.MIN_VALUE
     */
    @Test
    public void testGetNonOverAddResultCase7() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverAddResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverAddResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverAddResultMethod.setAccessible(true);
        Object invoke = getNonOverAddResultMethod.invoke(new NumericVarianceInteger(), new Object[] { Integer.MIN_VALUE, 0 });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be Integer.MIN_VALUE but it is " + invoke.toString(), -2147483648, invoke); //$NON-NLS-1$
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverMultiResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case1 0*any==any*0==0
     */
    @Test
    public void testGetNonOverMultiResultCase1() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverMultiResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverMultiResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverMultiResultMethod.setAccessible(true);
        // Integer.MIN_VALUE*0
        Object invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(), new Object[] { Integer.MIN_VALUE, 0 });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be 0 but it is " + invoke.toString(), 0, invoke); //$NON-NLS-1$
        // 0*Integer.MAX_VALUE
        invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(), new Object[] { 0, Integer.MAX_VALUE });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be 0 but it is " + invoke.toString(), 0, invoke); //$NON-NLS-1$
        // 0*0
        invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(), new Object[] { 0, 0 });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be 0 but it is " + invoke.toString(), 0, invoke); //$NON-NLS-1$
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverMultiResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case2 Integer.MIN_VALUE*Integer.MAX_VALUE==Integer.MIN_VALUE=-2147483648
     */
    @Test
    public void testGetNonOverMultiResultCase2() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverMultiResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverMultiResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverMultiResultMethod.setAccessible(true);
        Object invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(),
                new Object[] { Integer.MIN_VALUE, Integer.MAX_VALUE });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be Integer.MIN_VALUE but it is " + invoke.toString(), -2147483648, invoke); //$NON-NLS-1$
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverMultiResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case3 Integer.MIN_VALUE*Integer.MIN_VALUE==Integer.MAX_VALUE=2147483647
     */
    @Test
    public void testGetNonOverMultiResultCase3() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverMultiResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverMultiResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverMultiResultMethod.setAccessible(true);
        Object invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(),
                new Object[] { Integer.MIN_VALUE, Integer.MIN_VALUE });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be Integer.MAX_VALUE but it is " + invoke.toString(), 2147483647, invoke); //$NON-NLS-1$
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverMultiResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case4 Integer.MAX_VALUE*Integer.MAX_VALUE==Integer.MAX_VALUE=2147483647
     */
    @Test
    public void testGetNonOverMultiResultCase4() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverMultiResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverMultiResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverMultiResultMethod.setAccessible(true);
        Object invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(),
                new Object[] { Integer.MAX_VALUE, Integer.MAX_VALUE });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be Integer.MAX_VALUE but it is " + invoke.toString(), 2147483647, invoke); //$NON-NLS-1$
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverMultiResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case5 Integer.MIN_VALUE*-2==Integer.MAX_VALUE=2147483647
     */
    @Test
    public void testGetNonOverMultiResultCase5() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverMultiResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverMultiResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverMultiResultMethod.setAccessible(true);
        Object invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(), new Object[] { Integer.MIN_VALUE, -2 });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be Integer.MAX_VALUE but it is " + invoke.toString(), 2147483647, invoke); //$NON-NLS-1$
        // when -2*Integer.MIN_VALUE we should ge same result
        invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(), new Object[] { -2, Integer.MIN_VALUE });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be Integer.MAX_VALUE but it is " + invoke.toString(), 2147483647, invoke); //$NON-NLS-1$
    }

    /**
     * 
     * {@link org.talend.dataquality.datamasking.functions.NumericVarianceInteger#getNonOverMultiResult(int,int)}
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * 
     * case6 99999999*100==100*99999999 99999999*-100==-100*99999999
     */
    @Test
    public void testGetNonOverMultiResultCase6() throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            SecurityException, IllegalArgumentException, InvocationTargetException {
        Class<NumericVarianceInteger> reflectNVIClass = NumericVarianceInteger.class;
        Method getNonOverMultiResultMethod = reflectNVIClass.getDeclaredMethod("getNonOverMultiResult", //$NON-NLS-1$
                new Class[] { int.class, int.class });
        getNonOverMultiResultMethod.setAccessible(true);
        // 99999999*100
        Object invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(), new Object[] { 99999999, 100 });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be Integer.MAX_VALUE but it is " + invoke.toString(), 2099999979, invoke); //$NON-NLS-1$
        // 100*99999999
        invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(), new Object[] { 100, 99999999 });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be Integer.MAX_VALUE but it is " + invoke.toString(), 2099999979, invoke); //$NON-NLS-1$
        // -100*99999999
        invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(), new Object[] { -100, 99999999 });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be Integer.MAX_VALUE but it is " + invoke.toString(), -2099999979, invoke); //$NON-NLS-1$
        // 99999999*-100
        invoke = getNonOverMultiResultMethod.invoke(new NumericVarianceInteger(), new Object[] { -100, 99999999 });
        Assert.assertNotNull("Current result should be null", invoke); //$NON-NLS-1$
        Assert.assertTrue("Current type of result should be Integer but it is " + invoke.getClass().getSimpleName(), invoke //$NON-NLS-1$
                .getClass().getSimpleName().equals("Integer")); //$NON-NLS-1$
        Assert.assertEquals("Current result should be Integer.MAX_VALUE but it is " + invoke.toString(), -2099999979, invoke); //$NON-NLS-1$
    }

}
