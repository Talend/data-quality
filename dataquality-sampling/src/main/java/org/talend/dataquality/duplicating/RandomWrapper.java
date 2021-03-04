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

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Wrapper of java.util.Random with possibility of getting the seed.
 */
class RandomWrapper extends Random {

    private static final long serialVersionUID = 1L;

    private Random random;

    private final AtomicLong seed;

    private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);

    RandomWrapper() {
        this(seedUniquifier() ^ System.nanoTime());
    }

    RandomWrapper(long seed) {
        this.seed = new AtomicLong(seed);
        random = new Random(seed);
    }

    private static long seedUniquifier() {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        for (;;) {
            long current = seedUniquifier.get();
            long next = current * 181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    long getSeed() {
        return seed.get();
    }

    @Override
    protected int next(int bits) {
        // Ugh, can't delegate this method -- it's protected
        // Callers can't use it and other methods are delegated, so shouldn't matter
        throw new UnsupportedOperationException();
    }

    @Override
    public void nextBytes(byte[] bytes) {
        random.nextBytes(bytes);
    }

    @Override
    public int nextInt() {
        return random.nextInt();
    }

    @Override
    public int nextInt(int n) {
        return random.nextInt(n);
    }

    @Override
    public long nextLong() {
        return random.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return random.nextFloat();
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }

    @Override
    public double nextGaussian() {
        return random.nextGaussian();
    }

}
