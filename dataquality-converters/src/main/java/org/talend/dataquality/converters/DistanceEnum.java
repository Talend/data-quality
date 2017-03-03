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

/**
 * Distance enum.
 */
public enum DistanceEnum {
    MILLIMETER("millimeter", "mm", 0.001, 1000.0), //$NON-NLS-1$ //$NON-NLS-2$
    CENTIMETER("centimeter", "cm", 0.01, 100.0), //$NON-NLS-1$ //$NON-NLS-2$
    DECIMETER("decimeter", "dm", 0.1, 10.0), //$NON-NLS-1$ //$NON-NLS-2$
    METER("meter", "m", 1.0, 1.0), //$NON-NLS-1$ //$NON-NLS-2$
    DEKAMETER("dekameter", "dam", 10.0, 0.1), //$NON-NLS-1$ //$NON-NLS-2$
    HECTOMETER("hectometer", "hm", 100.0, 0.01), //$NON-NLS-1$ //$NON-NLS-2$
    KILOMETER("kilometer", "km", 1000.0, 0.001), //$NON-NLS-1$ //$NON-NLS-2$
    INCH("inch", "in", 0.0254, 39.3700787401574803), //$NON-NLS-1$ //$NON-NLS-2$
    FOOT("foot", "ft", 0.3048, 3.28083989501312336), //$NON-NLS-1$ //$NON-NLS-2$
    YARD("yard", "yd", 0.9144, 1.09361329833770779), //$NON-NLS-1$ //$NON-NLS-2$
    MILE("mile", "mi", 1609.344, 0.00062137119223733397), //$NON-NLS-1$ //$NON-NLS-2$
    NAUTICAL_MILE("nautical mile", "nm", 1852.0, 0.000539956803455723542), //$NON-NLS-1$ //$NON-NLS-2$
    LIGHT_YEAR("light-year", "ly", 9460730472580800.0, 0.0000000000000001057000834024615463709); //$NON-NLS-1$ //$NON-NLS-2$

    private String displayName;

    private String shortName;

    private double conversionToBase;

    private double conversionFromBase;

    DistanceEnum(String displayName, String shortName, double conversionToBase, double conversionFromBase) {
        this.displayName = displayName;
        this.shortName = shortName;
        this.conversionToBase = conversionToBase;
        this.conversionFromBase = conversionFromBase;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortName() {
        return shortName;
    }

    public double getConversionToBase() {
        return conversionToBase;
    }

    public double getConversionFromBase() {
        return conversionFromBase;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
