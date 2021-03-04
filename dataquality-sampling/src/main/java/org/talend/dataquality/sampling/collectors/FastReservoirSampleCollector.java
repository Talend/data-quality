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
 * Fast reservoir sampling implementation on Java Stream.
 */
public class FastReservoirSampleCollector<T> extends AbstractReservoirSampleCollector<T> {

    private final Random rand;

    private final int nbSamples;

    private int count = 0;

    private final int threshold;

    private int g = 0;

    /**
     * FastReservoirSampleCollector constructor.
     * 
     * @param nbSamples
     * @param seed
     */
    public FastReservoirSampleCollector(int nbSamples, long seed) {
        this.nbSamples = nbSamples;
        this.threshold = 4 * nbSamples;
        this.rand = new Random(seed);
    }

    /**
     * FastReservoirSampleCollector constructor.
     * 
     * @param nbSamples
     */
    public FastReservoirSampleCollector(int nbSamples) {
        this.nbSamples = nbSamples;
        this.threshold = 4 * nbSamples;
        this.rand = new SecureRandom();
    }

    @Override
    protected void addIt(final List<T> candidates, T v) {

        if (count < nbSamples) {
            // for the first n elements.
            candidates.add(v);
            count++;
            return;
        }

        if (count < threshold) {
            // do classic reservoir sampling.
            // rand.nextDouble gets a pseudo random value between 0.0 and 1.0
            long replace = (long) Math.floor(count * rand.nextDouble());
            if (replace < nbSamples) {
                // probability says replace.
                candidates.set((int) replace, v);
            }
            // else keep the current sample reservoir
        } else {
            if (g == 0) {
                // draw gap size (g) from geometric distribution with probability p = R/j
                double p = (double) nbSamples / count;
                double u = rand.nextDouble(); // random float > 0 and <= 1
                g = (int) Math.floor(Math.log10(u) / Math.log10(1 - p));

            } else {
                g--;
                if (g == 0) {
                    int replace = (int) Math.floor(nbSamples * rand.nextDouble());
                    candidates.set(replace, v);
                }
            }

        }

        count++;

    }

}
