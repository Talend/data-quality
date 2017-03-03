// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SAps
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.converters;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for class {@link DistanceConverter}.
 * <p>
 * Created by xqliu on 2017-02-27
 */
public class DistanceConverterTest {

    private double delta = 1.0E-34;

    @Test
    public void testConvertDoubleNan() {
        double nan = Double.NaN;
        DistanceConverter converter = new DistanceConverter(DistanceConverter.DEFAULT_DISTANCE_FROM,
                DistanceConverter.DEFAULT_DISTANCE_TO);
        assertEquals(nan, converter.convert(nan), delta);
    }

    @Test
    public void testConvertZero() {
        double zero = 0.0;
        DistanceConverter converter = new DistanceConverter(DistanceConverter.DEFAULT_DISTANCE_FROM,
                DistanceConverter.DEFAULT_DISTANCE_TO);
        assertEquals(zero, converter.convert(zero), delta);
    }

    @Test
    public void testConvertMaxValue() {
        double max = Double.MAX_VALUE;
        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.MILLIMETER);
        assertEquals(Double.POSITIVE_INFINITY, converter0.convert(max), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.LIGHT_YEAR);
        assertEquals(1.900163142869793E289, converter1.convert(max), delta);
    }

    @Test
    public void testConvertMinValue() {
        double min = Double.MIN_VALUE;
        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.LIGHT_YEAR);
        assertEquals(0.0, converter0.convert(min), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.MILLIMETER);
        assertEquals(0.0, converter1.convert(min), delta);
    }

    @Test
    public void testConvertMillimeter() {
        double mm = 1.0;
        double cm = 0.1;
        double dm = 0.01;
        double m = 0.001;
        double dam = 0.0001;
        double hm = 0.00001;
        double km = 0.000001;
        double in = 0.03937007874015748;
        double ft = 0.0032808398950131233;
        double yd = 0.0010936132983377078;
        double mi = 6.213711922373339E-7;
        double nm = 5.399568034557235E-7;
        double ly = 1.0570008340246155E-19;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter0.convert(mm), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter1.convert(mm), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.DECIMETER);
        assertEquals(dm, converter2.convert(mm), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.METER);
        assertEquals(m, converter3.convert(mm), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter4.convert(mm), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(mm), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.KILOMETER);
        assertEquals(km, converter6.convert(mm), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.INCH);
        assertEquals(in, converter7.convert(mm), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.FOOT);
        assertEquals(ft, converter8.convert(mm), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.YARD);
        assertEquals(yd, converter9.convert(mm), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.MILE);
        assertEquals(mi, converter10.convert(mm), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter11.convert(mm), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.MILLIMETER, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(mm), delta);
    }

    @Test
    public void testConvertCentimeter() {
        double cm = 1.0;
        double mm = 10.0;
        double dm = 0.1;
        double m = 0.01;
        double dam = 0.001;
        double hm = 0.0001;
        double km = 0.00001;
        double in = 0.3937007874015748;
        double ft = 0.032808398950131233;
        double yd = 0.010936132983377077;
        double mi = 6.213711922373339E-6;
        double nm = 5.399568034557236E-6;
        double ly = 1.0570008340246153E-18;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter0.convert(cm), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(cm), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.DECIMETER);
        assertEquals(dm, converter2.convert(cm), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.METER);
        assertEquals(m, converter3.convert(cm), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter4.convert(cm), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(cm), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.KILOMETER);
        assertEquals(km, converter6.convert(cm), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.INCH);
        assertEquals(in, converter7.convert(cm), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.FOOT);
        assertEquals(ft, converter8.convert(cm), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.YARD);
        assertEquals(yd, converter9.convert(cm), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.MILE);
        assertEquals(mi, converter10.convert(cm), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter11.convert(cm), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.CENTIMETER, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(cm), delta);
    }

    @Test
    public void testConvertDecimeter() {
        double dm = 1.0;
        double mm = 100.0;
        double cm = 10.0;
        double m = 0.1;
        double dam = 0.01;
        double hm = 0.001;
        double km = 0.0001;
        double in = 3.937007874015748;
        double ft = 0.32808398950131233;
        double yd = 0.10936132983377078;
        double mi = 6.213711922373339E-5;
        double nm = 5.399568034557236E-5;
        double ly = 1.0570008340246154E-17;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.DECIMETER);
        assertEquals(dm, converter0.convert(dm), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(dm), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter2.convert(dm), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.METER);
        assertEquals(m, converter3.convert(dm), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter4.convert(dm), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(dm), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.KILOMETER);
        assertEquals(km, converter6.convert(dm), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.INCH);
        assertEquals(in, converter7.convert(dm), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.FOOT);
        assertEquals(ft, converter8.convert(dm), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.YARD);
        assertEquals(yd, converter9.convert(dm), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.MILE);
        assertEquals(mi, converter10.convert(dm), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter11.convert(dm), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.DECIMETER, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(dm), delta);
    }

    @Test
    public void testConvertMeter() {
        double m = 1.0;
        double mm = 1000.0;
        double cm = 100.0;
        double dm = 10.0;
        double dam = 0.1;
        double hm = 0.01;
        double km = 0.001;
        double in = 39.37007874015748;
        double ft = 3.2808398950131235;
        double yd = 1.0936132983377078;
        double mi = 6.213711922373339E-4;
        double nm = 5.399568034557236E-4;
        double ly = 1.0570008340246154E-16;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.METER);
        assertEquals(m, converter0.convert(m), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(m), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter2.convert(m), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.DECIMETER);
        assertEquals(dm, converter3.convert(m), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter4.convert(m), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(m), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.KILOMETER);
        assertEquals(km, converter6.convert(m), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.INCH);
        assertEquals(in, converter7.convert(m), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.FOOT);
        assertEquals(ft, converter8.convert(m), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.YARD);
        assertEquals(yd, converter9.convert(m), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.MILE);
        assertEquals(mi, converter10.convert(m), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter11.convert(m), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.METER, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(m), delta);
    }

    @Test
    public void testConvertDekameter() {
        double dam = 1.0;
        double mm = 10000.0;
        double cm = 1000.0;
        double dm = 100.0;
        double m = 10;
        double hm = 0.1;
        double km = 0.01;
        double in = 393.7007874015748;
        double ft = 32.808398950131235;
        double yd = 10.936132983377078;
        double mi = 0.006213711922373339;
        double nm = 0.005399568034557236;
        double ly = 1.0570008340246154E-15;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter0.convert(dam), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(dam), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter2.convert(dam), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.DECIMETER);
        assertEquals(dm, converter3.convert(dam), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.METER);
        assertEquals(m, converter4.convert(dam), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(dam), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.KILOMETER);
        assertEquals(km, converter6.convert(dam), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.INCH);
        assertEquals(in, converter7.convert(dam), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.FOOT);
        assertEquals(ft, converter8.convert(dam), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.YARD);
        assertEquals(yd, converter9.convert(dam), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.MILE);
        assertEquals(mi, converter10.convert(dam), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter11.convert(dam), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.DEKAMETER, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(dam), delta);
    }

    @Test
    public void testConvertHectometer() {
        double hm = 1.0;
        double mm = 100000.0;
        double cm = 10000.0;
        double dm = 1000.0;
        double m = 100.0;
        double dam = 10.0;
        double km = 0.1;
        double in = 3937.007874015748;
        double ft = 328.0839895013124;
        double yd = 109.36132983377078;
        double mi = 0.06213711922373339;
        double nm = 0.05399568034557236;
        double ly = 1.0570008340246154E-14;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter0.convert(hm), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(hm), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter2.convert(hm), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.DECIMETER);
        assertEquals(dm, converter3.convert(hm), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.METER);
        assertEquals(m, converter4.convert(hm), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter5.convert(hm), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.KILOMETER);
        assertEquals(km, converter6.convert(hm), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.INCH);
        assertEquals(in, converter7.convert(hm), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.FOOT);
        assertEquals(ft, converter8.convert(hm), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.YARD);
        assertEquals(yd, converter9.convert(hm), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.MILE);
        assertEquals(mi, converter10.convert(hm), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter11.convert(hm), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.HECTOMETER, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(hm), delta);
    }

    @Test
    public void testConvertKilometer() {
        double km = 1.0;
        double mm = 1000000.0;
        double cm = 100000.0;
        double dm = 10000.0;
        double m = 1000.0;
        double dam = 100.0;
        double hm = 10.0;
        double in = 39370.07874015748;
        double ft = 3280.8398950131236;
        double yd = 1093.6132983377078;
        double mi = 0.6213711922373339;
        double nm = 0.5399568034557236;
        double ly = 1.0570008340246153E-13;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.KILOMETER);
        assertEquals(km, converter0.convert(km), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(km), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter2.convert(km), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.DECIMETER);
        assertEquals(dm, converter3.convert(km), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.METER);
        assertEquals(m, converter4.convert(km), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(km), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter6.convert(km), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.INCH);
        assertEquals(in, converter7.convert(km), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.FOOT);
        assertEquals(ft, converter8.convert(km), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.YARD);
        assertEquals(yd, converter9.convert(km), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.MILE);
        assertEquals(mi, converter10.convert(km), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter11.convert(km), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.KILOMETER, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(km), delta);
    }

    @Test
    public void testConvertInch() {
        double in = 1.0;
        double mm = 25.4;
        double cm = 2.54;
        double dm = 0.254;
        double m = 0.0254;
        double dam = 0.00254;
        double hm = 0.000254;
        double km = 2.54E-5;
        double ft = 0.08333333333333334;
        double yd = 0.02777777777777778;
        double mi = 1.578282828282828E-5;
        double nm = 1.371490280777538E-5;
        double ly = 2.684782118422523E-18;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.INCH);
        assertEquals(in, converter0.convert(in), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(in), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter2.convert(in), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.DECIMETER);
        assertEquals(dm, converter3.convert(in), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.METER);
        assertEquals(m, converter4.convert(in), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(in), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter6.convert(in), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.KILOMETER);
        assertEquals(km, converter7.convert(in), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.FOOT);
        assertEquals(ft, converter8.convert(in), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.YARD);
        assertEquals(yd, converter9.convert(in), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.MILE);
        assertEquals(mi, converter10.convert(in), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter11.convert(in), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.INCH, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(in), delta);
    }

    @Test
    public void testConvertFoot() {
        double ft = 1.0;
        double mm = 304.8;
        double cm = 30.48;
        double dm = 3.048;
        double m = 0.3048;
        double dam = 0.03048;
        double hm = 0.003048;
        double km = 3.048E-4;
        double in = 12;
        double yd = 0.3333333333333333;
        double mi = 1.8939393939393937E-4;
        double nm = 1.6457883369330455E-4;
        double ly = 3.221738542107028E-17;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.FOOT);
        assertEquals(ft, converter0.convert(ft), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(ft), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter2.convert(ft), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.DECIMETER);
        assertEquals(dm, converter3.convert(ft), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.METER);
        assertEquals(m, converter4.convert(ft), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(ft), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter6.convert(ft), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.KILOMETER);
        assertEquals(km, converter7.convert(ft), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.INCH);
        assertEquals(in, converter8.convert(ft), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.YARD);
        assertEquals(yd, converter9.convert(ft), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.MILE);
        assertEquals(mi, converter10.convert(ft), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter11.convert(ft), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.FOOT, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(ft), delta);
    }

    @Test
    public void testConvertYard() {
        double yd = 1.0;
        double mm = 914.4;
        double cm = 91.44;
        double dm = 9.144;
        double m = 0.9144;
        double dam = 0.09144;
        double hm = 0.009144;
        double km = 0.0009144;
        double in = 36.0;
        double ft = 3.0;
        double mi = 5.681818181818182E-4;
        double nm = 4.937365010799136E-4;
        double ly = 9.665215626321083E-17;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.YARD);
        assertEquals(yd, converter0.convert(yd), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(yd), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter2.convert(yd), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.DECIMETER);
        assertEquals(dm, converter3.convert(yd), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.METER);
        assertEquals(m, converter4.convert(yd), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(yd), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter6.convert(yd), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.KILOMETER);
        assertEquals(km, converter7.convert(yd), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.INCH);
        assertEquals(in, converter8.convert(yd), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.FOOT);
        assertEquals(ft, converter9.convert(yd), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.MILE);
        assertEquals(mi, converter10.convert(yd), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter11.convert(yd), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.YARD, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(yd), delta);
    }

    @Test
    public void testConvertMile() {
        double mi = 1.0;
        double mm = 1609344;
        double cm = 160934.4;
        double dm = 16093.44;
        double m = 1609.344;
        double dam = 160.9344;
        double hm = 16.09344;
        double km = 1.609344;
        double in = 63360.0;
        double ft = 5280.0;
        double yd = 1760.0;
        double nm = 0.868976241900648;
        double ly = 1.7010779502325107E-13;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.MILE);
        assertEquals(mi, converter0.convert(mi), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(mi), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter2.convert(mi), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.DECIMETER);
        assertEquals(dm, converter3.convert(mi), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.METER);
        assertEquals(m, converter4.convert(mi), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(mi), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter6.convert(mi), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.KILOMETER);
        assertEquals(km, converter7.convert(mi), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.INCH);
        assertEquals(in, converter8.convert(mi), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.FOOT);
        assertEquals(ft, converter9.convert(mi), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.YARD);
        assertEquals(yd, converter10.convert(mi), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter11.convert(mi), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.MILE, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(mi), delta);
    }

    @Test
    public void testConvertNauticalMile() {
        double nm = 1.0;
        double mm = 1852000.0;
        double cm = 185200.0;
        double dm = 18520.0;
        double m = 1852.0;
        double dam = 185.2;
        double hm = 18.52;
        double km = 1.852;
        double in = 72913.38582677166;
        double ft = 6076.115485564304;
        double yd = 2025.3718285214347;
        double mi = 1.1507794480235425;
        double ly = 1.9575655446135877E-13;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter0.convert(nm), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(nm), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter2.convert(nm), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.DECIMETER);
        assertEquals(dm, converter3.convert(nm), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.METER);
        assertEquals(m, converter4.convert(nm), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(nm), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter6.convert(nm), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.KILOMETER);
        assertEquals(km, converter7.convert(nm), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.INCH);
        assertEquals(in, converter8.convert(nm), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.FOOT);
        assertEquals(ft, converter9.convert(nm), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.YARD);
        assertEquals(yd, converter10.convert(nm), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.MILE);
        assertEquals(mi, converter11.convert(nm), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.NAUTICAL_MILE, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter12.convert(nm), delta);
    }

    @Test
    public void testConvertLightYear() {
        double ly = 1.0;
        double mm = 9.4607304725808E18;
        double cm = 9.4607304725808E17;
        double dm = 9.4607304725808E16;
        double m = 9.4607304725808E15;
        double dam = 9.4607304725808E14;
        double hm = 9.4607304725808E13;
        double km = 9.4607304725808E12;
        double in = 3.7246970364491341E17;
        double ft = 3.1039141970409452E16;
        double yd = 1.034638065680315E16;
        double mi = 5.878625373183607E12;
        double nm = 5.108385784330886E12;

        DistanceConverter converter0 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.LIGHT_YEAR);
        assertEquals(ly, converter0.convert(ly), delta);
        DistanceConverter converter1 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.MILLIMETER);
        assertEquals(mm, converter1.convert(ly), delta);
        DistanceConverter converter2 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.CENTIMETER);
        assertEquals(cm, converter2.convert(ly), delta);
        DistanceConverter converter3 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.DECIMETER);
        assertEquals(dm, converter3.convert(ly), delta);
        DistanceConverter converter4 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.METER);
        assertEquals(m, converter4.convert(ly), delta);
        DistanceConverter converter5 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.HECTOMETER);
        assertEquals(hm, converter5.convert(ly), delta);
        DistanceConverter converter6 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.DEKAMETER);
        assertEquals(dam, converter6.convert(ly), delta);
        DistanceConverter converter7 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.KILOMETER);
        assertEquals(km, converter7.convert(ly), delta);
        DistanceConverter converter8 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.INCH);
        assertEquals(in, converter8.convert(ly), delta);
        DistanceConverter converter9 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.FOOT);
        assertEquals(ft, converter9.convert(ly), delta);
        DistanceConverter converter10 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.YARD);
        assertEquals(yd, converter10.convert(ly), delta);
        DistanceConverter converter11 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.MILE);
        assertEquals(mi, converter11.convert(ly), delta);
        DistanceConverter converter12 = new DistanceConverter(DistanceEnum.LIGHT_YEAR, DistanceEnum.NAUTICAL_MILE);
        assertEquals(nm, converter12.convert(ly), delta);
    }
}
