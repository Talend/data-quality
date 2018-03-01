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
package org.talend.dataquality.statistics.semantic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
class SemanticStatisticsTestBase {

    protected final List<List<String[]>> INPUT_RECORDS = new ArrayList<List<String[]>>() {

        private static final long serialVersionUID = 1L;

        {
            add(getRecords(SemanticStatisticsTestBase.class.getResourceAsStream("customers_100_bug_TDQ10380.csv")));
            add(getRecords(SemanticStatisticsTestBase.class.getResourceAsStream("avengers.csv")));
            add(getRecords(SemanticStatisticsTestBase.class.getResourceAsStream("gender.csv")));
            add(getRecords(SemanticStatisticsTestBase.class.getResourceAsStream("dataset_with_invalid_records.csv")));

        }
    };

    protected final List<String[]> EXPECTED_CATEGORIES = new ArrayList<String[]>() {

        private static final long serialVersionUID = 1L;

        {
            add(new String[] { // dataset[0]
                    "", //
                    SemanticCategoryEnum.FIRST_NAME.getId(), //
                    SemanticCategoryEnum.LAST_NAME.getId(), //
                    SemanticCategoryEnum.US_STATE_CODE.getId(), //
                    "", //
                    SemanticCategoryEnum.AIRPORT.getId(), //
                    "", //
                    "", //
                    "" //
            });
            add(new String[] { // dataset[1]
                    "", //
                    SemanticCategoryEnum.FIRST_NAME.getId(), //
                    SemanticCategoryEnum.LAST_NAME.getId(), //
                    "", //
                    SemanticCategoryEnum.FR_COMMUNE.getId() //
            });
            add(new String[] { // dataset[2]
                    SemanticCategoryEnum.FIRST_NAME.getId(), //
                    "", //
                    SemanticCategoryEnum.GENDER.getId() //
            });
            add(new String[] { // dataset[3]
                    SemanticCategoryEnum.FIRST_NAME.getId(), //
                    ""//
            });
        }
    };

    protected static List<String[]> getRecords(InputStream inputStream) {
        return getRecords(inputStream, ";");
    }

    protected static List<String[]> getRecords(InputStream inputStream, String lineSeparator) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null.");
        }
        try {
            List<String[]> records = new ArrayList<String[]>();
            final List<String> lines = IOUtils.readLines(inputStream);
            for (String line : lines) {
                String[] record = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, lineSeparator);
                records.add(record);
            }
            return records;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Silent ignore
                e.printStackTrace();
            }
        }
    }
}
