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
package org.talend.dataquality.libraries.converters;

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
public final class ConvertDateUtils {

    private static Logger LOG = Logger.getLogger(ConvertDateUtils.class);

    public static final String DEFAULT_INPUT_PATTERN = "yyyy-MM-dd";//$NON-NLS-1$

    public static final String DEFAULT_OUTPUT_PATTERN = "yyyy-MM-dd";//$NON-NLS-1$

    /**
     * Convert an inputFormatPattern "yyyy-MM-dd" date text from inputChronologyType to outputChronologyType with "yyyy-MM-dd".
     * 
     * @param dateStr - the input date text.
     * @param inputChronologyType - the input date text Chronology type.
     * @param outputChronologyType - the output date text Chronology type.
     * @return a outputChronologyType text with the outputFormatPattern. note: if can not parse the dateStr with the
     * inputFormatPattern, will return "".
     */
    public static String convertDate(String dateStr, Chronology inputChronologyType, Chronology outputChronologyType) {
        return convertDate(dateStr, DEFAULT_INPUT_PATTERN, DEFAULT_OUTPUT_PATTERN, inputChronologyType, outputChronologyType);
    }

    /**
     * Convert an inputFormatPattern date text from inputChronologyType to outputChronologyType with outputFormatPattern.
     * 
     * @param dateStr - the input date text.
     * @param inputFormatPattern - the input date text format pattern.if is null, use default "yyyy-MM-dd".
     * @param outputFormatPattern - the output date text format pattern.if is null, use default "yyyy-MM-dd".
     * @param inputChronologyType - the input date text Chronology type.
     * @param outputChronologyType - the output date text Chronology type.
     * @return a outputChronologyType text with the outputFormatPattern. note: if can not parse the dateStr with the
     * inputFormatPattern, will return "".
     */
    public static String convertDate(String dateStr, String inputFormatPattern, String outputFormatPattern,
            Chronology inputChronologyType, Chronology outputChronologyType) {
        if (dateStr == null || dateStr.trim().equals("")) { //$NON-NLS-1$
            return dateStr;
        }

        if (inputFormatPattern == null) {
            inputFormatPattern = DEFAULT_INPUT_PATTERN;
        }

        if (outputFormatPattern == null) {
            outputFormatPattern = DEFAULT_OUTPUT_PATTERN;
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
     * @param chrono
     * @return
     */
    public static String formatDateToString(LocalDate localDate, Chronology chrono) {
        return formatDateToString(localDate, chrono, DEFAULT_OUTPUT_PATTERN);
    }

    /**
     * Converts a LocalDate (ISO) value to a ChronoLocalDate date
     * using the provided Chronology, and then formats the
     * ChronoLocalDate to a String using a DateTimeFormatter with a
     * SHORT pattern based on the Chronology and the current Locale.
     *
     * @param localDate - the ISO date to convert and format.
     * @param chrono - an optional Chronology. If null, then IsoChronology is used.
     * @param outputFormatPattern - the output date text format pattern. if is null, use default "yyyy-MM-dd".
     * @return string
     */
    public static String formatDateToString(LocalDate localDate, Chronology chrono, String outputFormatPattern) {
        return formatDateToString(localDate, chrono,
                DateTimeFormatter.ofPattern(outputFormatPattern == null ? DEFAULT_OUTPUT_PATTERN : outputFormatPattern));
    }

    /**
     * Converts a LocalDate (ISO) value to a ChronoLocalDate date
     * using the provided Chronology, and then formats the
     * ChronoLocalDate to a String using a DateTimeFormatter with a
     * SHORT pattern based on the Chronology and the current Locale.
     *
     * @param localDate - the ISO date to convert and format.
     * @param chrono - an optional Chronology. If null, then IsoChronology is used.
     * @param outputDateTimeFormatter - the output DateTimeFormatter. If null, then DateTimeFormatter.ofPattern("yyyy-MM-dd") is
     * used.
     * @return string
     */
    public static String formatDateToString(LocalDate localDate, Chronology chrono, DateTimeFormatter outputDateTimeFormatter) {
        if (localDate != null) {
            Locale locale = Locale.getDefault(Locale.Category.FORMAT);
            ChronoLocalDate cDate;
            if (chrono == null) {
                chrono = IsoChronology.INSTANCE;
            }
            if (outputDateTimeFormatter == null) {
                outputDateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_OUTPUT_PATTERN);
            }
            try {
                cDate = chrono.date(localDate);
            } catch (DateTimeException ex) {
                LOG.error(ex, ex);
                chrono = IsoChronology.INSTANCE;
                cDate = localDate;
            }
            DateTimeFormatter dateFormatter = outputDateTimeFormatter.withChronology(chrono).withLocale(locale)
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
     * @param dateStr - the input date text
     * for the Chronology and the current Locale.
     * @param inputFormatPattern - the input date text format pattern.
     * @param chrono - an optional Chronology. If null, then IsoChronology
     * is used.
     * @return LocalDate
     */
    public static LocalDate parseStringToDate(String dateStr, String inputFormatPattern, Chronology chrono) {
        if (dateStr != null && !dateStr.isEmpty()) {
            Locale locale = Locale.getDefault(Locale.Category.FORMAT);
            DateTimeFormatter inputDateTimeFormatter = new DateTimeFormatterBuilder().parseLenient()
                    .appendPattern(inputFormatPattern).toFormatter().withChronology(chrono)
                    .withDecimalStyle(DecimalStyle.of(locale));

            return parseStringToDate(dateStr, inputDateTimeFormatter, chrono);
        }
        return null;
    }

    /**
     * Parses a String to a ChronoLocalDate using a DateTimeFormatter
     * with a inputFormatPattern based on the current Locale and the
     * provided Chronology, then converts this to a LocalDate (ISO)
     * value.
     *
     * @param dateStr - the input date text
     * for the Chronology and the current Locale.
     * @param inputDateTimeFormatter - the input DateTimeFormatter.
     * @param chrono - an optional Chronology. If null, then IsoChronology
     * is used.
     * @return LocalDate
     */
    public static LocalDate parseStringToDate(String dateStr, DateTimeFormatter inputDateTimeFormatter, Chronology chrono) {
        if (dateStr != null && !dateStr.isEmpty()) {
            if (chrono == null) {
                chrono = IsoChronology.INSTANCE;
            }
            try {
                TemporalAccessor temporal = inputDateTimeFormatter.parse(dateStr);
                ChronoLocalDate cDate = chrono.date(temporal);
                return LocalDate.from(cDate);
            } catch (Exception e) {
                LOG.error(e, e);
                return null;
            }
        }
        return null;
    }

}
