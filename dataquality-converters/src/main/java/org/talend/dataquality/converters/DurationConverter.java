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
import java.math.RoundingMode;
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
     * @param from - the from ChronoUnit, default value is ChronoUnit.MILE.
     * @param to - the to ChronoUnit, default value is ChronoUnit.second.
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
    public long convert(long value) {
        if (Long.MAX_VALUE == value || Long.MIN_VALUE == value) {
            return value;
        }
        if (this.fromUnit.equals(this.toUnit)) {
            return value;
        }

        // get the days first, then use it as base to convert to the target value.
        long days = 0;
        switch (this.fromUnit) {
        case MILLIS:
            days = value / 24 / 60 / 60 / 1000;
            break;
        case SECONDS:
            days = value / 24 / 60 / 60;
            break;
        case MINUTES:
            days = value / 24 / 60;
            break;
        case HOURS:
            days = value / 24;
            break;
        case DAYS:
            days = value;
            break;
        case YEARS:
            days = value * 365;
            break;
        case MONTHS:
            days = value * 30;
            break;
        case WEEKS:
            days = value * 7;
            break;
        }

        switch (this.toUnit) {
        case MILLIS:
            return getDays(value, days) * 24 * 60 * 60 * 1000;
        case SECONDS:
            return getDays(value, days) * 24 * 60 * 60;
        case MINUTES:
            return getDays(value, days) * 24 * 60;
        case HOURS:
            return getDays(value, days) * 24;
        case DAYS:
            return getDays(value, days);
        case YEARS:
            return new BigDecimal(days).divide(new BigDecimal(365), RoundingMode.HALF_UP).longValue();
        case MONTHS:
            return new BigDecimal(days).divide(new BigDecimal(30), RoundingMode.HALF_UP).longValue();
        case WEEKS:
            return new BigDecimal(days).divide(new BigDecimal(7), RoundingMode.UP).longValue();
        }
        return value;
    }

    /**
     * get the days more exactly with what we want.
     * 
     * @param value
     * @param days
     * @return
     */
    protected long getDays(long value, long days) {
        if (this.fromUnit == ChronoUnit.MONTHS) {
            int yea = (int) (value / 12);
            int mon = (int) (value % 12);
            return yea * 365 + mon * 30;
        } else if (this.fromUnit == ChronoUnit.WEEKS) {
            int yea = (int) (value / 52);
            int wek = (int) (value % 52);
            return yea * 365 + wek * 7;
        } else {
            return days;
        }
    }
}
