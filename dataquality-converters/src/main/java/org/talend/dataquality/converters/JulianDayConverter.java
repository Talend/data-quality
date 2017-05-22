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
 * {@link ChronoField#EPOCH_DAY} 17304<br/>
 * {@link JulianFields#JULIAN_DAY} 2457892<br/>
 * {@link JulianFields#MODIFIED_JULIAN_DAY} 57891<br/>
 * {@link JulianFields#RATA_DIE} 736467<br/>
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
     * @param inputChronologyType Chronology of the input date.
     * @param outputjulianField Output TemproalFiled.
     */
    public JulianDayConverter(Chronology inputChronologyType, TemporalField outputjulianField) {
        this(inputChronologyType, null, null, outputjulianField);

    }

    /**
     * 
     * Convert Chronology to TemporalField and using given inputFormatPattern to parse date.
     * 
     * @param inputChronologyType Chronology of the input date.
     * @param outputjulianField Output TemproalFiled.
     * @param inputFormatPattern Pattern of the input date to convert.
     * @param inputLocal Locale of the input date.
     */
    public JulianDayConverter(Chronology inputChronologyType, String inputFormatPattern, Locale inputLocal,
            TemporalField outputjulianField) {
        convertCalendarToTemporal = true;
        this.inputChronologyType = inputChronologyType;
        this.inputFormatPattern = inputFormatPattern != null ? inputFormatPattern : DEFAULT_INPUT_PATTERN;
        this.outputTemporFiled = outputjulianField;
        Locale locale = inputLocal;
        if (locale == null) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        inputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendPattern(this.inputFormatPattern)
                .toFormatter(locale).withChronology(this.inputChronologyType).withDecimalStyle(DecimalStyle.of(locale));

    }

    /**
     * 
     * Convert TemporalField to Chronology and output String use default locale and pattern.
     * 
     * @param inputJulianField Input TemporalField.
     * @param outputChronologyType Chronology we want to use to convert the date.
     */
    public JulianDayConverter(TemporalField inputJulianField, Chronology outputChronologyType) {
        this(inputJulianField, outputChronologyType, null, null);

    }

    /**
     * 
     * Convert TemporalField to Chronology and output String use given locale and pattern.
     * 
     * @param inputJulianField Input TemporalField.
     * @param outputChronologyType Chronology we want to use to convert the date.
     * @param outputFormatPattern Pattern of the Chronology date.
     * @param outputLocale Locale of the converted date
     */
    public JulianDayConverter(TemporalField inputJulianField, Chronology outputChronologyType, String outputFormatPattern,
            Locale outputLocale) {
        convertCalendarToTemporal = false;
        this.inputTemporFiled = inputJulianField;
        this.outputChronologyType = outputChronologyType;
        this.outputFormatPattern = outputFormatPattern != null ? outputFormatPattern : DEFAULT_OUTPUT_PATTERN;
        Locale locale = outputLocale;
        if (locale == null) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        inputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendValue(inputTemporFiled).toFormatter()
                .withDecimalStyle(DecimalStyle.of(locale));
        outputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendPattern(this.outputFormatPattern)
                .toFormatter(locale).withChronology(this.outputChronologyType).withDecimalStyle(DecimalStyle.of(locale));

    }

    /**
     * 
     * Convert a TemporalField to another TemporalField
     * 
     * @param inputTemporFiled Input TemporalField.
     * @param outputTemporFiled Output TemporalField.
     */
    public JulianDayConverter(TemporalField inputTemporFiled, TemporalField outputTemporFiled) {
        convertCalendarToTemporal = false;
        this.inputTemporFiled = inputTemporFiled;
        this.outputTemporFiled = outputTemporFiled;
        inputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendValue(inputTemporFiled).toFormatter()
                // .withChronology(this.inputChronologyType)
                .withDecimalStyle(DecimalStyle.of(Locale.getDefault(Locale.Category.FORMAT)));
    }

    /**
     * 1.Calendar convert to TemporalFiled
     * 2.TemporalFiled convert to Calendar
     * 3.TemporalFiled convert to another TemporalFiled
     * if fail to parse a String,return original value.
     */
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
