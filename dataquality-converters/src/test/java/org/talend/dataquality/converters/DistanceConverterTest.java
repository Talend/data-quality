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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for class {@link DistanceConverter}.
 *
 * Created by xqliu on 2017-02-27
 */
public class DistanceConverterTest {

    private double delta = 1.0E-34;

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

        assertEquals(cm, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.CENTIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.DECIMETER), delta);
        assertEquals(m, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.METER), delta);
        assertEquals(dam, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.DEKAMETER), delta);
        assertEquals(hm, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.HECTOMETER), delta);
        assertEquals(km, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.KILOMETER), delta);
        assertEquals(in, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.INCH), delta);
        assertEquals(ft, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.FOOT), delta);
        assertEquals(yd, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.YARD), delta);
        assertEquals(mi, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.MILE), delta);
        assertEquals(nm, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.NAUTICAL_MILE), delta);
        assertEquals(ly, DistanceConverter.convert(mm, DistanceEnum.MILLIMETER, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.MILLIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.DECIMETER), delta);
        assertEquals(m, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.METER), delta);
        assertEquals(dam, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.DEKAMETER), delta);
        assertEquals(hm, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.HECTOMETER), delta);
        assertEquals(km, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.KILOMETER), delta);
        assertEquals(in, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.INCH), delta);
        assertEquals(ft, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.FOOT), delta);
        assertEquals(yd, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.YARD), delta);
        assertEquals(mi, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.MILE), delta);
        assertEquals(nm, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.NAUTICAL_MILE), delta);
        assertEquals(ly, DistanceConverter.convert(cm, DistanceEnum.CENTIMETER, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.MILLIMETER), delta);
        assertEquals(cm, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.CENTIMETER), delta);
        assertEquals(m, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.METER), delta);
        assertEquals(dam, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.DEKAMETER), delta);
        assertEquals(hm, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.HECTOMETER), delta);
        assertEquals(km, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.KILOMETER), delta);
        assertEquals(in, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.INCH), delta);
        assertEquals(ft, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.FOOT), delta);
        assertEquals(yd, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.YARD), delta);
        assertEquals(mi, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.MILE), delta);
        assertEquals(nm, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.NAUTICAL_MILE), delta);
        assertEquals(ly, DistanceConverter.convert(dm, DistanceEnum.DECIMETER, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.MILLIMETER), delta);
        assertEquals(cm, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.CENTIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.DECIMETER), delta);
        assertEquals(dam, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.DEKAMETER), delta);
        assertEquals(hm, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.HECTOMETER), delta);
        assertEquals(km, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.KILOMETER), delta);
        assertEquals(in, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.INCH), delta);
        assertEquals(ft, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.FOOT), delta);
        assertEquals(yd, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.YARD), delta);
        assertEquals(mi, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.MILE), delta);
        assertEquals(nm, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.NAUTICAL_MILE), delta);
        assertEquals(ly, DistanceConverter.convert(m, DistanceEnum.METER, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.MILLIMETER), delta);
        assertEquals(cm, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.CENTIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.DECIMETER), delta);
        assertEquals(m, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.METER), delta);
        assertEquals(hm, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.HECTOMETER), delta);
        assertEquals(km, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.KILOMETER), delta);
        assertEquals(in, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.INCH), delta);
        assertEquals(ft, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.FOOT), delta);
        assertEquals(yd, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.YARD), delta);
        assertEquals(mi, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.MILE), delta);
        assertEquals(nm, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.NAUTICAL_MILE), delta);
        assertEquals(ly, DistanceConverter.convert(dam, DistanceEnum.DEKAMETER, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.MILLIMETER), delta);
        assertEquals(cm, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.CENTIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.DECIMETER), delta);
        assertEquals(m, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.METER), delta);
        assertEquals(dam, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.DEKAMETER), delta);
        assertEquals(km, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.KILOMETER), delta);
        assertEquals(in, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.INCH), delta);
        assertEquals(ft, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.FOOT), delta);
        assertEquals(yd, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.YARD), delta);
        assertEquals(mi, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.MILE), delta);
        assertEquals(nm, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.NAUTICAL_MILE), delta);
        assertEquals(ly, DistanceConverter.convert(hm, DistanceEnum.HECTOMETER, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.MILLIMETER), delta);
        assertEquals(cm, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.CENTIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.DECIMETER), delta);
        assertEquals(m, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.METER), delta);
        assertEquals(hm, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.HECTOMETER), delta);
        assertEquals(dam, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.DEKAMETER), delta);
        assertEquals(in, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.INCH), delta);
        assertEquals(ft, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.FOOT), delta);
        assertEquals(yd, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.YARD), delta);
        assertEquals(mi, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.MILE), delta);
        assertEquals(nm, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.NAUTICAL_MILE), delta);
        assertEquals(ly, DistanceConverter.convert(km, DistanceEnum.KILOMETER, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.MILLIMETER), delta);
        assertEquals(cm, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.CENTIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.DECIMETER), delta);
        assertEquals(m, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.METER), delta);
        assertEquals(hm, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.HECTOMETER), delta);
        assertEquals(dam, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.DEKAMETER), delta);
        assertEquals(km, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.KILOMETER), delta);
        assertEquals(ft, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.FOOT), delta);
        assertEquals(yd, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.YARD), delta);
        assertEquals(mi, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.MILE), delta);
        assertEquals(nm, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.NAUTICAL_MILE), delta);
        assertEquals(ly, DistanceConverter.convert(in, DistanceEnum.INCH, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.MILLIMETER), delta);
        assertEquals(cm, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.CENTIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.DECIMETER), delta);
        assertEquals(m, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.METER), delta);
        assertEquals(hm, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.HECTOMETER), delta);
        assertEquals(dam, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.DEKAMETER), delta);
        assertEquals(km, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.KILOMETER), delta);
        assertEquals(in, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.INCH), delta);
        assertEquals(yd, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.YARD), delta);
        assertEquals(mi, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.MILE), delta);
        assertEquals(nm, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.NAUTICAL_MILE), delta);
        assertEquals(ly, DistanceConverter.convert(ft, DistanceEnum.FOOT, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.MILLIMETER), delta);
        assertEquals(cm, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.CENTIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.DECIMETER), delta);
        assertEquals(m, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.METER), delta);
        assertEquals(hm, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.HECTOMETER), delta);
        assertEquals(dam, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.DEKAMETER), delta);
        assertEquals(km, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.KILOMETER), delta);
        assertEquals(in, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.INCH), delta);
        assertEquals(ft, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.FOOT), delta);
        assertEquals(mi, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.MILE), delta);
        assertEquals(nm, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.NAUTICAL_MILE), delta);
        assertEquals(ly, DistanceConverter.convert(yd, DistanceEnum.YARD, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.MILLIMETER), delta);
        assertEquals(cm, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.CENTIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.DECIMETER), delta);
        assertEquals(m, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.METER), delta);
        assertEquals(hm, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.HECTOMETER), delta);
        assertEquals(dam, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.DEKAMETER), delta);
        assertEquals(km, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.KILOMETER), delta);
        assertEquals(in, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.INCH), delta);
        assertEquals(ft, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.FOOT), delta);
        assertEquals(yd, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.YARD), delta);
        assertEquals(nm, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.NAUTICAL_MILE), delta);
        assertEquals(ly, DistanceConverter.convert(mi, DistanceEnum.MILE, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.MILLIMETER), delta);
        assertEquals(cm, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.CENTIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.DECIMETER), delta);
        assertEquals(m, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.METER), delta);
        assertEquals(hm, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.HECTOMETER), delta);
        assertEquals(dam, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.DEKAMETER), delta);
        assertEquals(km, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.KILOMETER), delta);
        assertEquals(in, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.INCH), delta);
        assertEquals(ft, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.FOOT), delta);
        assertEquals(yd, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.YARD), delta);
        assertEquals(mi, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.MILE), delta);
        assertEquals(ly, DistanceConverter.convert(nm, DistanceEnum.NAUTICAL_MILE, DistanceEnum.LIGHT_YEAR), delta);
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

        assertEquals(mm, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.MILLIMETER), delta);
        assertEquals(cm, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.CENTIMETER), delta);
        assertEquals(dm, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.DECIMETER), delta);
        assertEquals(m, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.METER), delta);
        assertEquals(hm, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.HECTOMETER), delta);
        assertEquals(dam, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.DEKAMETER), delta);
        assertEquals(km, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.KILOMETER), delta);
        assertEquals(in, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.INCH), delta);
        assertEquals(ft, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.FOOT), delta);
        assertEquals(yd, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.YARD), delta);
        assertEquals(mi, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.MILE), delta);
        assertEquals(nm, DistanceConverter.convert(ly, DistanceEnum.LIGHT_YEAR, DistanceEnum.NAUTICAL_MILE), delta);
    }
}
