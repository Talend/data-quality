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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public abstract class AbstractReservoirSampleCollector<T> implements Collector<T, List<T>, List<T>> {

    protected abstract void addIt(final List<T> in, T s);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.stream.Collector#supplier()
     */
    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.stream.Collector#accumulator()
     */
    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return this::addIt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.stream.Collector#combiner()
     */
    @Override
    public BinaryOperator<List<T>> combiner() {
        return (left, right) -> {
            left.addAll(right);
            return left;
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.stream.Collector#characteristics()
     */
    @Override
    public Set<java.util.stream.Collector.Characteristics> characteristics() {
        return EnumSet.of(Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.stream.Collector#finisher()
     */
    @Override
    public Function<List<T>, List<T>> finisher() {
        return (i) -> i;
    }
}
