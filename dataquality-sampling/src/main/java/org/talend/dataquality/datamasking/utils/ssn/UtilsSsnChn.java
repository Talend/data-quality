package org.talend.dataquality.datamasking.utils.ssn;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.functions.GenerateUniqueSsnChn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UtilsSsnChn {

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsSsnChn.class);

    private static final List<Integer> keyWeight = Collections
            .unmodifiableList(Arrays.asList(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2));

    private static final int KEYMOD = 11; // $NON-NLS-1$

    private static final List<String> keyString = Collections
            .unmodifiableList(Arrays.asList("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"));

    public static final List<String> readChinaRegionFile() {
        // Region code
        InputStream is = GenerateUniqueSsnChn.class.getResourceAsStream("RegionListChina.txt");
        List<String> places = null;
        try {
            places = IOUtils.readLines(is, "UTF-8");

        } catch (IOException e) {
            LOGGER.error("The file of chinese regions is not correctly loaded " + e.getMessage(), e);
        }

        return places;
    }

    public static final String computeChineseKey(StringBuilder str) {
        int key = 0;
        for (int i = 0; i < 17; i++) {
            key += Character.getNumericValue(str.charAt(i)) * keyWeight.get(i);
        }
        key = key % KEYMOD;
        return keyString.get(key);
    }
}
