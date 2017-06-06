// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

    /** if it convert a calendar date to Numerical days */
    private boolean convertCalendarToTemporal = false;

    /**
     * input TemporalField like as JulianFields and ChronoField.
     */
    private TemporalField inputTemporalField = null;

    /**
     * output outputTemporFiled like as JulianFields and ChronoField.
     */
    private TemporalField outputTemporalField = null;

    /**
     * 
     * Convert Chronology to TemporalField and using default pattern{@link super.DEFAULT_INPUT_PATTERN} to parse date.
     * 
     * @param inputChronologyType Chronology of the input date.
     * @param outputTemporalField Output TemproalField like as JulianFields and ChronoField.
     */
    public JulianDayConverter(Chronology inputChronologyType, TemporalField outputTemporalField) {
        this(inputChronologyType, null, null, outputTemporalField);

    }

    /**
     * 
     * Convert Chronology to TemporalField and using given inputFormatPattern to parse date.
     * 
     * @param inputChronologyType Chronology of the input date.
     * @param outputTemporalField Output TemproalField like as JulianFields and ChronoField.
     * @param inputFormatPattern Pattern of the input date to convert.
     * @param inputLocale Locale of the input date.
     */
    public JulianDayConverter(Chronology inputChronologyType, String inputFormatPattern, Locale inputLocale,
            TemporalField outputTemporalField) {
        this.convertCalendarToTemporal = true;
        super.inputChronologyType = inputChronologyType;
        super.inputFormatPattern = inputFormatPattern != null ? inputFormatPattern : DEFAULT_INPUT_PATTERN;
        this.outputTemporalField = outputTemporalField;
        Locale locale = inputLocale != null ? inputLocale : Locale.getDefault(Locale.Category.FORMAT);
        super.inputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendPattern(super.inputFormatPattern)
                .toFormatter(locale).withChronology(super.inputChronologyType).withDecimalStyle(DecimalStyle.of(locale));

    }

    /**
     * 
     * Convert TemporalField to Chronology and output String use default locale and pattern.
     * 
     * @param inputTemporalField Input TemporalField like as JulianFields and ChronoField.
     * @param outputChronologyType Chronology we want to use to convert the date.
     */
    public JulianDayConverter(TemporalField inputTemporalField, Chronology outputChronologyType) {
        this(inputTemporalField, outputChronologyType, null, null);

    }

    /**
     * 
     * Convert TemporalField to Chronology and output String use given locale and pattern.
     * 
     * @param inputTemporalField Input TemporalField like as JulianFields and ChronoField.
     * @param outputChronologyType Chronology we want to use to convert the date.
     * @param outputFormatPattern Pattern of the Chronology date.if it is null,use default pattern "yyyy-MM-dd G"
     * @param outputLocale Locale of the converted date.
     */
    public JulianDayConverter(TemporalField inputTemporalField, Chronology outputChronologyType, String outputFormatPattern,
            Locale outputLocale) {
        this.convertCalendarToTemporal = false;
        this.inputTemporalField = inputTemporalField;
        super.outputChronologyType = outputChronologyType;
        super.outputFormatPattern = outputFormatPattern != null ? outputFormatPattern : "yyyy-MM-dd G"; //$NON-NLS-1$
        Locale locale = outputLocale != null ? outputLocale : Locale.getDefault(Locale.Category.FORMAT);
        super.inputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendValue(inputTemporalField).toFormatter()
                .withDecimalStyle(DecimalStyle.of(locale));
        super.outputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendPattern(super.outputFormatPattern)
                .toFormatter(locale).withChronology(super.outputChronologyType).withDecimalStyle(DecimalStyle.of(locale));

    }

    /**
     * 
     * Convert a TemporalField to another TemporalField
     * 
     * @param inputTemporalField Input TemporalField like as JulianFields and ChronoField.
     * @param outputTemporalField Output TemporalField like as JulianFields and ChronoField.
     */
    public JulianDayConverter(TemporalField inputTemporalField, TemporalField outputTemporalField) {
        this.convertCalendarToTemporal = false;
        this.inputTemporalField = inputTemporalField;
        this.outputTemporalField = outputTemporalField;
        super.inputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendValue(inputTemporalField).toFormatter()
                .withDecimalStyle(DecimalStyle.of(Locale.getDefault(Locale.Category.FORMAT)));
    }

    /**
     * 1.Calendar convert to TemporalField
     * 2.TemporalField convert to Calendar
     * 3.TemporalField convert to another TemporalField
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
        if (convertCalendarToTemporal) {// Calendar->TemporalField
            outputDateStr = Long.toString(localDate.getLong(outputTemporalField));
        } else {
            if (inputTemporalField != null && outputTemporalField != null) {// TemporalField->another TemporalField
                outputDateStr = Long.toString(localDate.getLong(outputTemporalField));
            } else {// TemporalField->Calendar
                outputDateStr = formatDateToString(localDate);
            }
        }
        return outputDateStr;
    }

}
