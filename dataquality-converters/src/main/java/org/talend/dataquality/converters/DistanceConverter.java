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
package org.talend.dataquality.converters;

import java.math.BigDecimal;

/**
 * this class is used for Converting a distance from one unit to another.<br/>
 * millimeter (mm)<br/>
 * centimeter (cm)<br/>
 * decimeter (dm)<br/>
 * meter (m)<br/>
 * dekameter (dam)<br/>
 * hectometer (hm)<br/>
 * kilometer (km)<br/>
 * inch (in)<br/>
 * foot (ft)<br/>
 * yard (yd)<br/>
 * mile (mi)<br/>
 * nautical mile (nm)<br/>
 * light-year (ly)<br/>
 * input default value is mile (mi)<br/>
 * output default value is kilometer (km)<br/>
 * Created by xqliu on 2017-02-24.
 */
public class DistanceConverter {

    public static final DistanceEnum DEFAULT_DISTANCE_FROM = DistanceEnum.MILE;

    public static final DistanceEnum DEFAULT_DISTANCE_TO = DistanceEnum.KILOMETER;

    public static double convert(double value, DistanceEnum from, DistanceEnum to) {
        double result = value;

        DistanceEnum deFrom = from;
        if (deFrom == null) {
            deFrom = DEFAULT_DISTANCE_FROM;
        }

        DistanceEnum deTo = to;
        if (deTo == null) {
            deTo = DEFAULT_DISTANCE_TO;
        }

        if (!deFrom.equals(deTo)) {
            // use BigDecimal to eliminate multiplication rounding errors
            BigDecimal multiplier = new BigDecimal(String.valueOf(deFrom.getConversionToBase()))
                    .multiply(new BigDecimal(String.valueOf(deTo.getConversionFromBase())));
            BigDecimal bdResult = new BigDecimal(String.valueOf(value)).multiply(multiplier);
            result = bdResult.doubleValue();
        }

        return result;
    }
}

enum DistanceEnum {
    MILLIMETER("mm", 0.001, 1000.0), //$NON-NLS-1$
    CENTIMETER("cm", 0.01, 100.0), //$NON-NLS-1$
    DECIMETER("dm", 0.1, 10.0), //$NON-NLS-1$
    METER("m", 1.0, 1.0), //$NON-NLS-1$
    DEKAMETER("dam", 10.0, 0.1), //$NON-NLS-1$
    HECTOMETER("hm", 100.0, 0.01), //$NON-NLS-1$
    KILOMETER("km", 1000.0, 0.001), //$NON-NLS-1$
    INCH("in", 0.0254, 39.3700787401574803), //$NON-NLS-1$
    FOOT("ft", 0.3048, 3.28083989501312336), //$NON-NLS-1$
    YARD("yd", 0.9144, 1.09361329833770779), //$NON-NLS-1$
    MILE("mi", 1609.344, 0.00062137119223733397), //$NON-NLS-1$
    NAUTICAL_MILE("nm", 1852.0, 0.000539956803455723542), //$NON-NLS-1$
    LIGHT_YEAR("ly", 9460730472580800.0, 0.0000000000000001057000834024615463709); //$NON-NLS-1$

    private String name;

    private double conversionToBase;

    private double conversionFromBase;

    DistanceEnum(String name, double conversionToBase, double conversionFromBase) {
        this.name = name;
        this.conversionToBase = conversionToBase;
        this.conversionFromBase = conversionFromBase;
    }

    public String getName() {
        return name;
    }

    public double getConversionToBase() {
        return conversionToBase;
    }

    public double getConversionFromBase() {
        return conversionFromBase;
    }
}