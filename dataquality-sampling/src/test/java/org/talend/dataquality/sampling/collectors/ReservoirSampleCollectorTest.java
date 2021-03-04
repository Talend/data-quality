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
package org.talend.dataquality.sampling.collectors;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.talend.dataquality.duplicating.TestConstants;

public class ReservoirSampleCollectorTest {

    private static final int SAMPLE_SIZE = 10;

    private static final int ORIGINAL_COUNT = 100;

    private static final Integer[] EXPECTED_SAMPLES = { 27, 1, 91, 3, 86, 45, 74, 73, 22, 35 };

    @Test
    public void testSample() {
        final Stream<Integer> stream = IntStream.range(0, ORIGINAL_COUNT).boxed();

        final List<Integer> sampleList =
                stream.collect(new ReservoirSampleCollector<>(SAMPLE_SIZE, TestConstants.RANDOM_SEED));

        assertEquals("Unexpected sample size!", EXPECTED_SAMPLES.length, sampleList.size());
        for (int i = 0; i < sampleList.size(); i++) {
            assertEquals(EXPECTED_SAMPLES[i], sampleList.get(i));
        }
    }
}
