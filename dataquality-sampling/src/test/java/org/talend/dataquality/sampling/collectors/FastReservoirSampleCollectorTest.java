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

public class FastReservoirSampleCollectorTest {

    private static final int SAMPLE_SIZE = 10;

    private static final int ORIGINAL_COUNT = 100;

    private static final Integer[] EXPECTED_SAMPLES = { 83, 19, 36, 98, 74, 5, 91, 93, 22, 85 };

    @Test
    public void testSample() {
        final Stream<Integer> stream = IntStream.range(0, ORIGINAL_COUNT).boxed();
        long t0 = System.nanoTime();
        final List<Integer> sampleList =
                stream.collect(new FastReservoirSampleCollector<>(SAMPLE_SIZE, TestConstants.RANDOM_SEED));

        assertEquals("Unexpected sample size!", EXPECTED_SAMPLES.length, sampleList.size());
        for (int i = 0; i < sampleList.size(); i++) {
            assertEquals(EXPECTED_SAMPLES[i], sampleList.get(i));
        }
    }

}
