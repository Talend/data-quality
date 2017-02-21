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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * this class is used for Converting a date from one Chronology to another with the format pattern.<br/>
 * created by msjian on 2017.1.20 <br/>
 * <br/>
 * 
 * for example: the date Chronology type and date string as follows:<br/>
 * HijrahChronology 1432-09-19<br/>
 * IsoChronology 2011/08/19<br/>
 * JapaneseChronology 0023-08-19<br/>
 * MinguoChronology 0100 08 19<br/>
 * ThaiBuddhistChronology 2554-08-19<br/>
 * 
 */
public class DateCalendarConverter {

    private static final Logger LOG = Logger.getLogger(DateCalendarConverter.class);

    public static final String DEFAULT_INPUT_PATTERN = "yyyy-MM-dd";//$NON-NLS-1$

    public static final String DEFAULT_OUTPUT_PATTERN = "yyyy-MM-dd";//$NON-NLS-1$

    /**
     * the date text need to parse.
     */
    private String dateStr;

    /**
     * the input date text format pattern, default is "yyyy-MM-dd".
     */
    private String inputFormatPattern = DEFAULT_INPUT_PATTERN;

    /**
     * the output date text format pattern, default is "yyyy-MM-dd".
     */
    private String outputFormatPattern = DEFAULT_OUTPUT_PATTERN;

    /**
     * an optional input Chronology. default is IsoChronology
     */
    private Chronology inputChronologyType = IsoChronology.INSTANCE;

    /**
     * an optional output Chronology. default is IsoChronology
     */
    private Chronology outputChronologyType = IsoChronology.INSTANCE;

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public void setInputFormatPattern(String inputFormatPattern) {
        this.inputFormatPattern = inputFormatPattern;
    }

    public void setOutputFormatPattern(String outputFormatPattern) {
        this.outputFormatPattern = outputFormatPattern;
    }

    public void setInputChronologyType(Chronology inputChronologyType) {
        this.inputChronologyType = inputChronologyType;
    }

    public void setOutputChronologyType(Chronology outputChronologyType) {
        this.outputChronologyType = outputChronologyType;
    }

    public DateCalendarConverter() {
        super();
    }

    /**
     * DateCalendarConverter constructor.
     * 
     * @param dateStr
     * @param inputChronologyType
     * @param outputChronologyType
     */
    public DateCalendarConverter(String dateStr, Chronology inputChronologyType, Chronology outputChronologyType) {
        this();
        this.dateStr = dateStr;
        this.inputChronologyType = inputChronologyType == null ? IsoChronology.INSTANCE : inputChronologyType;
        this.outputChronologyType = outputChronologyType == null ? IsoChronology.INSTANCE : outputChronologyType;
    }

    /**
     * DateCalendarConverter constructor.
     * 
     * @param dateStr
     * @param inputFormatPattern
     * @param outputFormatPattern
     * @param inputChronologyType
     * @param outputChronologyType
     */
    public DateCalendarConverter(String dateStr, String inputFormatPattern, String outputFormatPattern,
            Chronology inputChronologyType, Chronology outputChronologyType) {
        this(dateStr, inputChronologyType, outputChronologyType);
        this.inputFormatPattern = inputFormatPattern == null ? DEFAULT_INPUT_PATTERN : inputFormatPattern;
        this.outputFormatPattern = outputFormatPattern == null ? DEFAULT_OUTPUT_PATTERN : outputFormatPattern;
    }

    /**
     * Convert an inputFormatPattern date text from inputChronologyType to outputChronologyType with outputFormatPattern.
     * 
     * @return a outputChronologyType text with the outputFormatPattern. note: if can not parse the dateStr with the
     * inputFormatPattern, will return "".
     */
    public String convert() {
        if (dateStr == null || "".equals(dateStr.trim())) { //$NON-NLS-1$
            return dateStr;
        }

        if (inputChronologyType.equals(outputChronologyType) && inputFormatPattern.equals(outputFormatPattern)) {
            return dateStr;
        }

        LocalDate inputDate = parseStringToDate(dateStr, inputFormatPattern, inputChronologyType);
        return formatDateToString(inputDate, outputChronologyType, outputFormatPattern);
    }

    /**
     * format a LocalDate to a string with defaultOutputFormatPattern: "yyyy-MM-dd".
     * 
     * @param localDate
     * @param outputChronology
     * @return
     */
    public String formatDateToString(LocalDate localDate, Chronology outputChronology) {
        return formatDateToString(localDate, outputChronology, DEFAULT_OUTPUT_PATTERN);
    }

    /**
     * Converts a LocalDate (ISO) value to a ChronoLocalDate date
     * using the provided Chronology, and then formats the
     * ChronoLocalDate to a String using a DateTimeFormatter with a
     * SHORT pattern based on the Chronology and the current Locale.
     *
     * @param localDate - the ISO date to convert and format.
     * @param outputChronology - an optional Chronology. If null, then IsoChronology is used.
     * @param outputPattern - the output date text format pattern. if is null, use default "yyyy-MM-dd".
     * @return string
     */
    public String formatDateToString(LocalDate localDate, Chronology outputChronology, String outputPattern) {
        return formatDateToString(localDate, outputChronology,
                DateTimeFormatter.ofPattern(outputPattern == null ? DEFAULT_OUTPUT_PATTERN : outputPattern));
    }

    /**
     * Converts a LocalDate (ISO) value to a ChronoLocalDate date
     * using the provided Chronology, and then formats the
     * ChronoLocalDate to a String using a DateTimeFormatter with a
     * SHORT pattern based on the Chronology and the current Locale.
     *
     * @param localDate - the ISO date to convert and format.
     * @param outputChronology - an optional Chronology. If null, then IsoChronology is used.
     * @param outputDateTimeFormatter - the output DateTimeFormatter. If null, then DateTimeFormatter.ofPattern("yyyy-MM-dd") is
     * used.
     * @return string
     */
    public String formatDateToString(LocalDate localDate, Chronology outputChronology, DateTimeFormatter outputDateTimeFormatter) {
        if (localDate != null) {
            Locale locale = Locale.getDefault(Locale.Category.FORMAT);
            ChronoLocalDate cDate;
            Chronology chronology = outputChronology == null ? IsoChronology.INSTANCE : outputChronology;
            DateTimeFormatter dateTimeFormatter = outputDateTimeFormatter == null ? DateTimeFormatter
                    .ofPattern(DEFAULT_OUTPUT_PATTERN) : outputDateTimeFormatter;
            try {
                cDate = chronology.date(localDate);
            } catch (DateTimeException ex) {
                LOG.error(ex, ex);
                chronology = IsoChronology.INSTANCE;
                cDate = localDate;
            }
            DateTimeFormatter dateFormatter = dateTimeFormatter.withChronology(chronology).withLocale(locale)
                    .withDecimalStyle(DecimalStyle.of(locale));
            return dateFormatter.format(cDate);
        } else {
            return ""; //$NON-NLS-1$
        }
    }

    /**
     * Parses a String to a ChronoLocalDate using a DateTimeFormatter
     * with a inputFormatPattern based on the current Locale and the
     * provided Chronology, then converts this to a LocalDate (ISO)
     * value.
     *
     * @param inputDateStr - the input date text
     * for the Chronology and the current Locale.
     * @param inputPattern - the input date text format pattern.
     * @param inputChronology - an optional Chronology. If null, then IsoChronology
     * is used.
     * @return LocalDate
     */
    public LocalDate parseStringToDate(String inputDateStr, String inputPattern, Chronology inputChronology) {
        if (inputDateStr != null && !inputDateStr.isEmpty()) {
            Locale locale = Locale.getDefault(Locale.Category.FORMAT);
            DateTimeFormatter inputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().appendPattern(inputPattern)
                    .toFormatter().withChronology(inputChronology).withDecimalStyle(DecimalStyle.of(locale));
            return parseStringToDate(inputDateStr, inputDateTimeFormatter, inputChronology);
        }
        return null;
    }

    /**
     * Parses a String to a ChronoLocalDate using a DateTimeFormatter
     * with a inputFormatPattern based on the current Locale and the
     * provided Chronology, then converts this to a LocalDate (ISO)
     * value.
     *
     * @param inputDateStr - the input date text
     * for the Chronology and the current Locale.
     * @param inputDateTimeFormatter - the input DateTimeFormatter.
     * @param inputChronology - an optional Chronology. If null, then IsoChronology
     * is used.
     * @return LocalDate
     */
    public LocalDate parseStringToDate(String inputDateStr, DateTimeFormatter inputDateTimeFormatter, Chronology inputChronology) {
        if (inputDateStr != null && !inputDateStr.isEmpty()) {
            Chronology chronology = inputChronology == null ? IsoChronology.INSTANCE : inputChronology;
            try {
                TemporalAccessor temporal = inputDateTimeFormatter.parse(inputDateStr);
                ChronoLocalDate cDate = chronology.date(temporal);
                return LocalDate.from(cDate);
            } catch (Exception e) {
                LOG.error(e, e);
                return null;
            }
        }
        return null;
    }

}
