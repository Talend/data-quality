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

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

/**
 * Classic reservoir sampling implementation on Java Stream.
 */
public class ReservoirSampleCollector<T> extends AbstractReservoirSampleCollector<T> {

    private final Random rand;

    private final int nbSamples;

    private int count = 0;

    /**
     * ReservoirSampleCollector constructor.
     * 
     * @param size
     * @param seed
     */
    public ReservoirSampleCollector(int size, long seed) {
        this.nbSamples = size;
        this.rand = new Random(seed);
    }

    /**
     * ReservoirSampleCollector constructor.
     * 
     * @param size
     */
    public ReservoirSampleCollector(int size) {
        this.nbSamples = size;
        this.rand = new SecureRandom();
    }

    @Override
    protected void addIt(final List<T> in, T s) {
        if (in.size() < nbSamples) {
            in.add(s);
        } else {
            int replaceInIndex = (int) (rand.nextDouble() * (nbSamples + (count++) + 1));
            if (replaceInIndex < nbSamples) {
                in.set(replaceInIndex, s);
            }
        }
    }

}
