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
package org.talend.dataquality.duplicating;

import java.util.Date;
import java.util.Random;

/**
 * This class provides the following ways to modify a date:
 * <p>
 * 1. modify one value among the day, month and the year.
 * <p>
 * 2. switch the day and month value. if the day value is greater than 12, then use day%12 as month value.
 * <p>
 * 3. replace with a random date.
 */
public class DateChanger {

    private static final Random random = new Random();

    /**
     * Set random seed
     * 
     * @param seed
     */
    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    @SuppressWarnings("deprecation")
    Date modifyDateValue(Date date) {
        if (date == null) {
            return null;
        }

        int choice = random.nextInt(3);
        switch (choice) {
        case 0:
            date.setYear(random.nextInt(200));
            break;
        case 1:
            date.setMonth(random.nextInt(12));
            break;
        case 2:
            date.setDate(random.nextInt(31) + 1);
            break;
        default:
            break;
        }
        return date;
    }

    @SuppressWarnings("deprecation")
    Date switchDayMonthValue(Date date) {
        if (date == null) {
            return null;
        }
        int tempMonth = date.getMonth();
        date.setMonth((date.getDate() - 1) % 12);
        date.setDate(tempMonth + 1);
        return date;
    }

    @SuppressWarnings("deprecation")
    Date replaceWithRandomDate(Date date) {
        Date newDate = date;
        if (newDate == null) {
            newDate = new Date();
        }
        newDate.setYear(random.nextInt(100));
        newDate.setMonth(random.nextInt(12));
        newDate.setDate(random.nextInt(31));
        return newDate;
    }
}
