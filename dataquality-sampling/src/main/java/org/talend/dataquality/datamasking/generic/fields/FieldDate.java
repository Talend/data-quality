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
package org.talend.dataquality.datamasking.generic.fields;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author dprot
 * <p>
 * A FieldDate is a field containing a date with format YYYYMMDD
 */

public class FieldDate extends AbstractField {

    private static final long serialVersionUID = -4061095254204934437L;

    public static final List<Integer> cumulativeMonthSize = Collections
            .unmodifiableList(Arrays.asList(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365));

    public static final List<Integer> cumulativeMonthSizeLeapYear = Collections
            .unmodifiableList(Arrays.asList(0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366));

    public static final List<Integer> monthSize = Collections
            .unmodifiableList(Arrays.asList(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31));

    public static final List<Integer> monthSizeLeapYear = Collections
            .unmodifiableList(Arrays.asList(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31));

    private int firstYear;

    private int lastYear;

    private List<Integer> numberDaysPerYear = new ArrayList<Integer>();

    /**
     * Create a field corresponding to a date with format YYYYMMDD, from year 1900 included to 2100 excluded
     */
    public FieldDate() {
        this(1900, 2100);
    }

    /**
     * Create a field corresponding to a date with format YYYYMMDD, firstYear included, lastYear excluded
     *
     * @param firstYear
     * @param lastYear
     */
    public FieldDate(int firstYear, int lastYear) {
        super();
        length = 8;
        this.firstYear = firstYear;
        this.lastYear = lastYear;
        computeDaysPerYear();
    }

    /**
     * Initialize the attribute numberDaysPerYear
     */
    private void computeDaysPerYear() {

        int count = 0;
        for (int year = firstYear; year <= lastYear; year++) {
            numberDaysPerYear.add(count);
            if (isLeapYear(year))
                count += 366;
            else
                count += 365;
        }
    }

    /**
     * @param year
     * @return true if a given year is a leap year
     */
    private boolean isLeapYear(int year) {

        if (year % 4 == 0 && year % 100 != 0)
            return true;
        if (year % 100 == 0 && year % 400 == 0)
            return true;
        return false;

    }

    @Override
    public BigInteger getWidth() {
        return BigInteger.valueOf(numberDaysPerYear.get(numberDaysPerYear.size() - 1));
    }

    public long getWidthLong() {
        return numberDaysPerYear.get(numberDaysPerYear.size() - 1);
    }

    @Override
    public BigInteger encode(String str) {

        Long dayNumber = 0L;
        try {
            int year = Integer.valueOf(str.substring(0, 4));
            int month = Integer.valueOf(str.substring(4, 6));
            int day = Integer.valueOf(str.substring(6, 8));

            // Check if the date exists
            if (year < firstYear || year >= lastYear)
                return BigInteger.valueOf(-1);
            if (month < 1 || month > 12)
                return BigInteger.valueOf(-1);
            if (isLeapYear(year)) {
                if (day < 1 || day > monthSizeLeapYear.get(month - 1))
                    return BigInteger.valueOf(-1);
            } else {
                if (day < 1 || day > monthSize.get(month - 1))
                    return BigInteger.valueOf(-1);
            }

            dayNumber = (long) numberDaysPerYear.get(year - firstYear);
            dayNumber += cumulativeMonthSize.get(month - 1);
            dayNumber += (day - 1);
            if (isLeapYear(year))
                dayNumber++;

        } catch (NumberFormatException e) {
            return BigInteger.valueOf(-1);
        }

        return BigInteger.valueOf(dayNumber);
    }

    @Override
    public String decode(BigInteger numberBig) {
        int number = numberBig.intValue();
        if (number >= getWidthLong() || number < 0)
            return "";

        int year = findNearest(number, numberDaysPerYear);
        long remainingDays = number - numberDaysPerYear.get(year);
        int month;
        int days;
        if (isLeapYear(year + firstYear)) {
            month = findNearest(remainingDays, cumulativeMonthSizeLeapYear);
            days = (int) (remainingDays - cumulativeMonthSizeLeapYear.get(month));
        } else {
            month = findNearest(remainingDays, cumulativeMonthSize);
            days = (int) (remainingDays - cumulativeMonthSize.get(month));
        }

        String res = "";
        res += String.valueOf(year + firstYear);
        String monthString = String.valueOf(month + 1);
        // Write month on two digits
        if (monthString.length() < 2)
            res += "0";
        res += monthString;
        String dayString = String.valueOf(days + 1);
        // Write days on two digits
        if (dayString.length() < 2)
            res += "0";
        res += dayString;

        return res;
    }

    /**
     * Find the nearest entry in numberList smaller than number (with a dichotomic search)
     *
     * @param number
     * @param numberList
     * @return
     */
    private int findNearest(long number, List<Integer> numberList) {

        int minIndex = 0;
        int maxIndex = numberList.size() - 1;

        int mean = (maxIndex + minIndex) / 2;

        do {
            if (numberList.get(mean) <= number) {
                minIndex = mean;
            } else {
                maxIndex = mean;
            }
            mean = (maxIndex + minIndex) / 2;
        } while (minIndex + 1 < maxIndex);

        return minIndex;
    }

}
