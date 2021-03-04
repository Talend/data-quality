// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class AbstractDuplicatorTest {

    private static final String CITY_NAME = "CITY_NAME"; //$NON-NLS-1$

    private static final double grp_size_expectation = 5;

    private static final double duplicate_percentage = 0.7;

    private static final String[] distroNames = { "BERNOULLI", "POISSON", "GEOMETRIC" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static final int original_count = 1000;

    private static final int expectedDupCount = 334;

    private static final int[] expectedDupCountSum = { 1693, 1680, 1706 };

    private static final int[] expectedCountSum = { 2359, 2346, 2372 };

    /**
     * Test the record count after duplication for the three types of distributions with a fixed random seed.
     * <p/>
     * The row1Struct and row2Struct classes simulate the usage in components.
     */
    @Test
    public void testDistro() {

        for (int i = 0; i < distroNames.length; i++) {

            AbstractDuplicator<row1Struct, row2Struct> duplicator = new AbstractDuplicator<row1Struct, row2Struct>(
                    grp_size_expectation, duplicate_percentage, distroNames[i], TestConstants.RANDOM_SEED) {

                @Override
                protected row2Struct generateOutput(row1Struct v, boolean isOriginal) {

                    row2Struct tmpStruct = new row2Struct();
                    tmpStruct.id = v.id;
                    tmpStruct.city = v.city;

                    if (isOriginal) {
                        tmpStruct.ORIGINAL_MARK = false;
                    } else {
                        tmpStruct.ORIGINAL_MARK = true;
                    }
                    return tmpStruct;
                }

            };

            int[] result = getCountResult(duplicator);
            assertEquals(expectedDupCount, result[0]);
            assertEquals(expectedDupCountSum[i], result[1]);
            assertEquals(expectedCountSum[i], result[2]);

        }
    }

    private int[] getCountResult(AbstractDuplicator<row1Struct, row2Struct> duplicator) {
        row1Struct[] testers = new row1Struct[original_count];
        for (int j = 0; j < original_count; j++) {
            row1Struct struct = new row1Struct();
            struct.id = j + 1;
            struct.city = CITY_NAME;
            testers[j] = struct;
        }

        int countSum = 0;
        int dupCount = 0;
        int dupCountSum = 0;

        List<row2Struct> duplicateResult = new ArrayList<row2Struct>();
        for (row1Struct tester : testers) {
            List<row2Struct> res = duplicator.process(tester);
            duplicateResult.addAll(res);
            if (res.size() == 1) {
                countSum++;
            } else {
                dupCount++;
                dupCountSum += res.size();
                countSum += res.size();
            }
        }

        int[] result = { dupCount, dupCountSum, countSum };

        return result;
    }

    /**
     * Test the record count after duplication for the three types of distributions with a fixed random seed.
     * <p/>
     * The row1Struct and row2Struct classes simulate the usage in components.
     */
    @Test
    public void testDistroWithoutSeed() {
        for (int i = 0; i < distroNames.length; i++) {

            AbstractDuplicator<row1Struct, row2Struct> duplicator = new AbstractDuplicator<row1Struct, row2Struct>(
                    grp_size_expectation, duplicate_percentage, distroNames[i]) {

                @Override
                protected row2Struct generateOutput(row1Struct v, boolean isOriginal) {

                    row2Struct tmpStruct = new row2Struct();
                    tmpStruct.id = v.id;
                    tmpStruct.city = v.city;

                    if (isOriginal) {
                        tmpStruct.ORIGINAL_MARK = false;
                    } else {
                        tmpStruct.ORIGINAL_MARK = true;
                    }
                    return tmpStruct;
                }

            };

            int[] result = getCountResult(duplicator);

            Random random = duplicator.getRandom();
            long seed = ((RandomWrapper) random).getSeed();

            AbstractDuplicator<row1Struct, row2Struct> duplicator2 = new AbstractDuplicator<row1Struct, row2Struct>(
                    grp_size_expectation, duplicate_percentage, distroNames[i], seed) {

                @Override
                protected row2Struct generateOutput(row1Struct v, boolean isOriginal) {

                    row2Struct tmpStruct = new row2Struct();
                    tmpStruct.id = v.id;
                    tmpStruct.city = v.city;

                    if (isOriginal) {
                        tmpStruct.ORIGINAL_MARK = false;
                    } else {
                        tmpStruct.ORIGINAL_MARK = true;
                    }
                    return tmpStruct;
                }

            };

            int[] result2 = getCountResult(duplicator2);

            assertEquals(result[0], result2[0]);
            assertEquals(result[1], result2[1]);
            assertEquals(result[2], result2[2]);

        }
    }

}

class row1Struct {

    public Integer id;

    public Integer getId() {
        return this.id;
    }

    public String city;

    public String getCity() {
        return this.city;
    }

    @Override
    public String toString() {
        return id + " -> " + city; //$NON-NLS-1$
    }
}

class row2Struct {

    public Integer id;

    public Integer getId() {
        return this.id;
    }

    public String city;

    public String getCity() {
        return this.city;
    }

    public Boolean ORIGINAL_MARK;

    public Boolean getORIGINAL_MARK() {
        return this.ORIGINAL_MARK;
    }

    @Override
    public String toString() {
        return id + " -> " + city + " -> " + ORIGINAL_MARK; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
