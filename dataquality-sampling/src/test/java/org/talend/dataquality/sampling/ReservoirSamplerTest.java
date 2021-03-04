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
package org.talend.dataquality.sampling;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.duplicating.TestConstants;

public class ReservoirSamplerTest {

    private static final int SAMPLE_SIZE = 10;

    private static final int ORIGINAL_COUNT = 100;

    private ReservoirSampler<TestRowStruct> sampler;

    private TestRowStruct[] testers;

    private static final Integer[] EXPECTED_SAMPLES = { 28, 2, 92, 4, 87, 46, 75, 74, 23, 36 };

    @Before
    public void init() {
        testers = new TestRowStruct[ORIGINAL_COUNT];
        for (int j = 0; j < ORIGINAL_COUNT; j++) {
            TestRowStruct struct = new TestRowStruct();
            struct.id = j + 1;
            testers[j] = struct;
        }
    }

    @Test
    public void testSample() {
        sampler = new ReservoirSampler<TestRowStruct>(SAMPLE_SIZE, TestConstants.RANDOM_SEED);
        sampler.clear();
        for (TestRowStruct row : testers) {
            sampler.onNext(row);
        }
        sampler.onCompleted(true);

        List<TestRowStruct> sampleList = sampler.sample();
        for (int i = 0; i < sampleList.size(); i++) {
            assertEquals(EXPECTED_SAMPLES[i], sampleList.get(i).getId());
        }

    }

    class TestRowStruct {

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
}
