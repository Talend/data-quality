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
package org.talend.dataquality.converters;

import java.time.temporal.ChronoUnit;

/**
 * this class is used for Converting duration from one unit to another.<br/>
 * year<br/>
 * month<br/>
 * week<br/>
 * day<br/>
 * hour<br/>
 * minute<br/>
 * second<br/>
 * millisecond<br/>
 * input default value is day<br/>
 * output default value is hour<br/>
 * Created by msjian on 2017-03-28.
 */
public class DurationConverter {

    /**
     * 1 day = 24 hours.
     */
    private static final double NUM_24 = 24;

    /**
     * 1 minite = 60 seconds
     */
    private static final double NUM_60 = 60;

    /**
     * 1 second = 1000 milliseconds.
     */
    private static final double NUM_1000 = 1000;

    /**
     * 1 year = 365 days.
     */
    private static final double NUM_365 = 365;

    /**
     * 1 month = 30 days.
     */
    private static final double NUM_30 = 30;

    /**
     * 1 week = 7 days.
     */
    private static final double NUM_7 = 7;

    /**
     * 1 year = 52 weeks.
     */
    private static final double NUM_52 = 52;

    /**
     * 1 year = 12 months.
     */
    private static final double NUM_12 = 12;

    public static final ChronoUnit DEFAULT_FROM_UNIT = ChronoUnit.DAYS;

    public static final ChronoUnit DEFAULT_TO_UNIT = ChronoUnit.HOURS;

    private ChronoUnit fromUnit;

    private ChronoUnit toUnit;

    /**
     * Default constructor, the default from unit is ChronoUnit.DAYS, the default to unit is
     * ChronoUnit.HOURS.
     */
    public DurationConverter() {
        this(DEFAULT_FROM_UNIT, DEFAULT_TO_UNIT);
    }

    /**
     * ConverterDuration Constructor.
     *
     * @param from - the from ChronoUnit, default value is ChronoUnit.DAYS.
     * @param to - the to ChronoUnit, default value is ChronoUnit.HOURS.
     */
    public DurationConverter(ChronoUnit from, ChronoUnit to) {
        this.fromUnit = from == null ? DEFAULT_FROM_UNIT : from;
        this.toUnit = to == null ? DEFAULT_TO_UNIT : to;
    }

    /**
     * convert the value from fromUnit type to toUnit type.
     * 
     * @param value
     * @return long
     */
    public double convert(double value) {

        if (noNeedConvert(value)) {
            return value;
        }

        double days = handleDays(value);
        return handleFinalResult(value, days);
    }

    private double handleFinalResult(double value, double days) {
        double result = value;
        switch (this.toUnit) {
        case MILLIS:
            result = getExactDays(value, days) * NUM_24 * NUM_60 * NUM_60 * NUM_1000;
            break;
        case SECONDS:
            result = getExactDays(value, days) * NUM_24 * NUM_60 * NUM_60;
            break;
        case MINUTES:
            result = getExactDays(value, days) * NUM_24 * NUM_60;
            break;
        case HOURS:
            result = getExactDays(value, days) * NUM_24;
            break;
        case DAYS:
            result = getExactDays(value, days);
            break;
        case YEARS:
            result = days / NUM_365;
            break;
        case MONTHS:
            result = days / NUM_30;
            break;
        case WEEKS:
            result = days / NUM_7;
            break;
        default:
            break;
        }
        return result;
    }

    private double handleDays(double value) {
        // get the days first, then use it as base to convert to the target value.
        double days = 0;
        switch (this.fromUnit) {
        case MILLIS:
            days = value / NUM_24 / NUM_60 / NUM_60 / NUM_1000;
            break;
        case SECONDS:
            days = value / NUM_24 / NUM_60 / NUM_60;
            break;
        case MINUTES:
            days = value / NUM_24 / NUM_60;
            break;
        case HOURS:
            days = value / NUM_24;
            break;
        case DAYS:
            days = value;
            break;
        case YEARS:
            days = value * NUM_365;
            break;
        case MONTHS:
            days = value * NUM_30;
            break;
        case WEEKS:
            days = value * NUM_7;
            break;
        default:
            break;
        }
        return days;
    }

    private boolean noNeedConvert(double value) {
        return Double.isNaN(value) || Double.compare(Double.MAX_VALUE, value) == 0
                || Double.compare(Double.MIN_VALUE, value) == 0 || this.fromUnit.equals(this.toUnit);
    }

    /**
     * get the days more exactly with what we want. because:
     * 1 year = 365 days = 12 months (!= 12 * 30)
     * 1 month = 30 days
     * 1 week = 7 days
     * 
     * for example:
     * 13 months = 1*365+1*30 != 13*30.
     * 5 weeks = 5*7 != 1*30+1*7
     * 
     * @param value
     * @param days
     * @return
     */
    protected double getExactDays(double value, double days) {
        if (this.fromUnit == ChronoUnit.MONTHS) {
            int year = (int) (value / NUM_12);
            int month = (int) (value % NUM_12);
            return year * NUM_365 + month * NUM_30;
        } else if (this.fromUnit == ChronoUnit.WEEKS) {
            int year = (int) (value / NUM_52);
            int week = (int) (value % NUM_52);
            return year * NUM_365 + week * NUM_7;
        }
        return days;
    }
}
