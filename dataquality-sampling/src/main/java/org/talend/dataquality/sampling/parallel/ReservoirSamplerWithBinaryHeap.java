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
package org.talend.dataquality.sampling.parallel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * An implementation of ReservoirSampling with a PriorityQueue as the reservoir.
 */
public class ReservoirSamplerWithBinaryHeap<T> {

    private int nbSamples; // number of elements to sample.

    private int replaceCount = 0;

    private boolean done = false;

    private Random rand;

    private PriorityQueue<ImmutablePair<Double, T>> buffer;

    private Double minRandom;

    public ReservoirSamplerWithBinaryHeap(int nbSamples, long seed) {
        this.nbSamples = nbSamples;
        this.rand = new Random(seed);
        this.minRandom = 1.0;
        buffer = new PriorityQueue<ImmutablePair<Double, T>>(nbSamples, new Comparator<ImmutablePair<Double, T>>() {

            @Override
            public int compare(ImmutablePair<Double, T> o1, ImmutablePair<Double, T> o2) {
                if (o1.left < o2.left) {
                    return -1;
                } else if (o1.left > o2.left) {
                    return 1;
                } else {
                    return 0;
                }
            }

        });
    }

    public ReservoirSamplerWithBinaryHeap(int nbSamples) {
        this(nbSamples, new Random().nextLong());
    }

    public void onCompleted(boolean b) {
        done = b;
    }

    public void onNext(T v) {
        if (done) {
            return;
        }

        // rand.nextDouble gets a pseudo random value between 0.0 and 1.0
        double r = rand.nextDouble();

        if (buffer.size() < nbSamples) {
            // for the first n elements.
            ImmutablePair<Double, T> pair = ImmutablePair.of(r, v);
            buffer.add(pair);
            if (r < minRandom) {
                minRandom = r;
            }
            return;
        }

        if (r > minRandom) {
            // do reservoir sampling.
            replaceCount++;

            ImmutablePair<Double, T> pair = ImmutablePair.of(r, v);
            buffer.add(pair);
            ImmutablePair<Double, T> nextPair = buffer.poll();
            minRandom = nextPair.left;
        }
    }

    public List<T> sample() {
        Iterator<ImmutablePair<Double, T>> it = buffer.iterator();
        List<T> samples = new ArrayList<T>();
        while (it.hasNext()) {
            ImmutablePair<Double, T> pair = it.next();
            samples.add(pair.getRight());
        }
        return samples;
    }

    public Iterable<ImmutablePair<Double, T>> samplePairs() {
        return buffer;
    }

    public void clear() {
        done = false;
        minRandom = 1.0;
        replaceCount = 0;
        buffer.clear();
    }

}
