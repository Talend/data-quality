// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.JulianFields;
import java.time.temporal.TemporalField;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * * This class is used to convert a date from a calendar Chronology to Numerical days or vice versa.<br/>
 * <p>
 * For example: the date Chronology type and date string as follow:<br/>
 * HijrahChronology 1432-09-19<br/>
 * IsoChronology 2011/08/19<br/>
 * JapaneseChronology 0023-08-19<br/>
 * MinguoChronology 0100 08 19<br/>
 * ThaiBuddhistChronology 2554-08-19<br/>
 * The Numerical days Type as follow:<br/>
 * {@link ChronoField#EPOCH_DAY}<br/>
 * {@link JulianFields#JULIAN_DAY}<br/>
 * {@link JulianFields#MODIFIED_JULIAN_DAY}<br/>
 * {@link JulianFields#RATA_DIE}<br/>
 */
public class JulianDayConverter extends DateCalendarConverter {

    /** if it covert a calendar date to Numerical days */
    private boolean convertCalendarToTemporal = false;

    /**
     * input TemporalField
     */
    private TemporalField inputTemporFiled = null;

    /**
     * output outputTemporFiled
     */
    private TemporalField outputTemporFiled = null;


    /**
     * 
     * Convert Chronology to TemporalField and using default pattern{@link super.DEFAULT_INPUT_PATTERN} to parse date.
     * 
     * @param inputChronologyType
     * @param outputjulianField
     */
    public JulianDayConverter(Chronology inputChronologyType, TemporalField outputjulianField) {
        this(inputChronologyType, null, outputjulianField);

    }

    /**
     * 
     * Convert Chronology to TemporalField and using given inputFormatPattern to parse date.
     * 
     * @param inputChronologyType
     * @param inputFormatPattern
     * @param outputjulianField
     */
    public JulianDayConverter(Chronology inputChronologyType, String inputFormatPattern, TemporalField outputjulianField) {
        convertCalendarToTemporal = true;
        this.inputFormatPattern = inputFormatPattern != null ? inputFormatPattern : DEFAULT_INPUT_PATTERN;
        this.inputChronologyType = inputChronologyType;
        this.outputTemporFiled = outputjulianField;
        inputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendPattern(this.inputFormatPattern)
                .toFormatter().withChronology(this.inputChronologyType)
                .withDecimalStyle(DecimalStyle.of(Locale.getDefault(Locale.Category.FORMAT)));

    }

    /**
     * 
     * Convert TemporalField to Chronology.
     * 
     * @param inputJulianField
     * @param outputChronologyType
     */
    public JulianDayConverter(TemporalField inputJulianField, Chronology outputChronologyType) {
        convertCalendarToTemporal = false;
        this.inputTemporFiled = inputJulianField;
        this.outputChronologyType = outputChronologyType;
        inputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendValue(inputTemporFiled).toFormatter()
        // .withChronology(this.inputChronologyType)
                .withDecimalStyle(DecimalStyle.of(Locale.getDefault(Locale.Category.FORMAT)));
        outputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendPattern(this.outputFormatPattern)
                .toFormatter().withChronology(this.outputChronologyType)
                .withDecimalStyle(DecimalStyle.of(Locale.getDefault(Locale.Category.FORMAT)));

    }
    

    /**
     * 
     * Convert a TemporalField to another TemporalField
     * 
     * @param inputJulianField
     * @param outputJulianField
     */
    public JulianDayConverter(TemporalField inputJulianField, TemporalField outputJulianField) {
        convertCalendarToTemporal = false;
        this.inputTemporFiled = inputJulianField;
        this.outputTemporFiled = outputJulianField;
        inputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendValue(inputTemporFiled).toFormatter()
                .withChronology(this.inputChronologyType)
                .withDecimalStyle(DecimalStyle.of(Locale.getDefault(Locale.Category.FORMAT)));
    }


    @Override
    public String convert(String inputDateStr) {
        if (StringUtils.isEmpty(inputDateStr)) {
            return inputDateStr;
        }
        String outputDateStr = inputDateStr;
        LocalDate localDate = super.parseStringToDate(inputDateStr);
        if (localDate == null) {
            return outputDateStr;
        }
        if (convertCalendarToTemporal) {// Calendar->TemporalFiled
            outputDateStr = Long.toString(localDate.getLong(outputTemporFiled));
        } else {
            if (inputTemporFiled != null && outputTemporFiled != null) {// TemporalFiled->another TemporalFiled
                outputDateStr = Long.toString(localDate.getLong(outputTemporFiled));
            } else {// TemporalFiled->Calendar
                outputDateStr = formatDateToString(localDate);
            }
        }
        return outputDateStr;
    }


}
