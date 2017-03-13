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
package org.talend.dataquality.duplicating;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.log4j.Logger;

public abstract class AbstractDuplicator<TIn, TOut> {

    private static final Logger LOG = Logger.getLogger(AbstractDuplicator.class);

    private RandomWrapper rnd;

    protected double uniquePercentageOfOriginal;

    protected double expectation;

    protected AbstractIntegerDistribution distribution;

    private static final double EPSILON = 1e-6;

    public AbstractDuplicator(double expectation, double duplicatesPercentage, String distributionName) {
        this.expectation = expectation;
        if (Math.abs(duplicatesPercentage - 1) < EPSILON) {
            uniquePercentageOfOriginal = 0;
        } else {
            uniquePercentageOfOriginal = expectation / (expectation - 1 + 1 / (1 - duplicatesPercentage));
        }
        distribution = DistributionFactory.createDistribution(distributionName, expectation);
    }

    public AbstractDuplicator(double expectation, double duplicatesPercentage, String distributionName, long distributionSeed) {
        this(expectation, duplicatesPercentage, distributionName);
        rnd = new RandomWrapper(distributionSeed);
        distribution.reseedRandomGenerator(distributionSeed);
        if (LOG.isInfoEnabled()) {
            LOG.info("Seed for random generator has been set to: " + rnd.getSeed()); //$NON-NLS-1$
        }
    }

    protected Random getRandom() {
        if (rnd == null) {
            rnd = new RandomWrapper();
            distribution.reseedRandomGenerator(rnd.getSeed());
            if (LOG.isInfoEnabled()) {
                LOG.info(
                        "A seed is generated for Random generator. If a fixed seed is needed, set it in the advanced parameters of the tDuplicateRow component"); //$NON-NLS-1$
            }
        }
        return rnd;
    }

    public List<TOut> process(TIn v) {
        List<TOut> result = new ArrayList<TOut>();
        int grpSize = getRandomGroupSize();
        for (int i = 0; i < grpSize; i++) {
            result.add(generateOutput(v, i == 0));
        }
        return result;
    }

    protected abstract TOut generateOutput(TIn v, boolean isOriginal);

    private int getRandomGroupSize() {
        if (uniquePercentageOfOriginal > getRandom().nextDouble()) {
            return 1;
        } else {
            return distribution.sample() + 2;
        }
    }

    public void setSeed(long seed) {
        getRandom().setSeed(seed);
        distribution.reseedRandomGenerator(seed);
    }
}
