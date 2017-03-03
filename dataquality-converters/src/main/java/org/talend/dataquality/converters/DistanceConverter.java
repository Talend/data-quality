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

    private DistanceEnum deFrom;

    private DistanceEnum deTo;

    private BigDecimal multiplier;

    /**
     * Default constructor, the default distance from unit is DistanceEnum.MILE, the default distance to unit is DistanceEnum.KILOMETER.
     */
    public DistanceConverter() {
        this(DEFAULT_DISTANCE_FROM, DEFAULT_DISTANCE_TO);
    }

    /**
     * Constructor.
     *
     * @param from the distance from unit, default value is DistanceEnum.MILE
     * @param to   the distance to unit, default value is DistanceEnum.KILOMETER
     */
    public DistanceConverter(DistanceEnum from, DistanceEnum to) {
        this.deFrom = from == null ? DEFAULT_DISTANCE_FROM : from;
        this.deTo = to == null ? DEFAULT_DISTANCE_TO : to;
        this.multiplier = new BigDecimal(String.valueOf(this.deFrom.getConversionToBase()))
                .multiply(new BigDecimal(String.valueOf(this.deTo.getConversionFromBase())));
    }

    public double convert(double value) {
        if (Double.isNaN(value)) {
            return value;
        }
        if (this.deFrom.equals(this.deTo)) {
            return value;
        }
        BigDecimal bdResult = new BigDecimal(String.valueOf(value)).multiply(this.multiplier);
        return bdResult.doubleValue();
    }
}
