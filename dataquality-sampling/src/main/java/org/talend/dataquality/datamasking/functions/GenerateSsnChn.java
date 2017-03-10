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
package org.talend.dataquality.datamasking.functions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @author dprot
 * This class proposes a pure-random Chinese SSN number
 * The Chinese SSN has 4 fields : the first one, on 6 digits, stands for the birth place; the second one, with format
 * YYYYMMDD for the date of birth; the third one, with 3 digits; the last one, on one digit, is a checksum key
 */
public class GenerateSsnChn extends Function<String> {

    private static final long serialVersionUID = 8845031997964609626L;

    private static final Logger LOGGER = Logger.getLogger(GenerateSsnChn.class);

    public static final List<Integer> monthSize = Collections
            .unmodifiableList(Arrays.asList(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31));

    private static final List<Integer> keyWeight = Collections
            .unmodifiableList(Arrays.asList(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2));

    private static final int KEY_MOD = 11; // $NON-NLS-1$

    private static final List<String> keyString = Collections
            .unmodifiableList(Arrays.asList("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"));

    private String computeChineseKey(String ssnNumber) {
        int key = 0;
        for (int i = 0; i < 17; i++) {
            key += Character.getNumericValue(ssnNumber.charAt(i)) * keyWeight.get(i);
        }
        key = key % KEY_MOD;
        return keyString.get(key);
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);

        // Region code
        InputStream is = GenerateUniqueSsnChn.class.getResourceAsStream("RegionListChina.txt");
        List<String> places = null;
        try {
            places = IOUtils.readLines(is, "UTF-8");

        } catch (IOException e) {
            LOGGER.error("The file of chinese regions is not correctly loaded " + e.getMessage(), e);
        }

        result.append(places.get(rnd.nextInt(places.size())));

        // Year
        int yyyy = rnd.nextInt(200) + 1900;
        result.append(yyyy);
        // Month
        int mm = rnd.nextInt(12) + 1;
        if (mm < 10) {
            result.append("0"); //$NON-NLS-1$
        }
        result.append(mm);
        // Day
        int dd = 1 + rnd.nextInt(monthSize.get(mm - 1));
        if (dd < 10) {
            result.append("0"); //$NON-NLS-1$
        }
        // Birth rank
        result.append(dd);
        for (int i = 0; i < 3; ++i) {
            result.append(rnd.nextInt(10));
        }

        // Checksum

        String controlKey = computeChineseKey(result.toString());
        result.append(controlKey);

        return result.toString();
    }
}
