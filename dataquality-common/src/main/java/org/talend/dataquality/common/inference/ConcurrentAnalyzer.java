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
package org.talend.dataquality.common.inference;

import java.util.List;

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.talend.dataquality.common.exception.DQCommonRuntimeException;

/**
 * A {@link Analyzer} implementation that allows use of an Analyzer pool by several threads. Please note analyzer
 * instance is <b>only</b> returned to the pool on {@link #close()} call.
 */
public class ConcurrentAnalyzer<T> implements Analyzer<T> {

    private static final long serialVersionUID = 6896234073310039985L;

    private final ThreadLocal<Analyzer<T>> threadLocal;

    private ConcurrentAnalyzer(int maxSize, AnalyzerSupplier<Analyzer<T>> supplier) {
        GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();
        config.maxTotal = maxSize;
        config.maxActive = maxSize;
        config.maxIdle = maxSize / 2;
        config.minIdle = maxSize / 2;
        config.maxWait = -1;
        // #1: Initialize a ThreadLocal backed with a generic object pool -> allows getting previously borrowed instance
        // and return to pool on remove() call.
        // #2: Pool is expected to be thread safe.
        final KeyedObjectPool<Thread, Analyzer<T>> pool = new GenericKeyedObjectPool<>(new Factory<>(supplier), config);
        this.threadLocal = new ThreadLocal<Analyzer<T>>() {

            @Override
            protected Analyzer<T> initialValue() {
                try {
                    return pool.borrowObject(Thread.currentThread());
                } catch (Exception e) {
                    throw new DQCommonRuntimeException(e);
                }
            }

            @Override
            public void remove() {
                try {
                    // Order matters here as remove() causes get() to return null.
                    pool.returnObject(Thread.currentThread(), get());
                } catch (Exception e) {
                    throw new DQCommonRuntimeException(e);
                } finally {
                    // Thread local keeps a lot of references, make sure everything gets cleaned up on error.
                    super.remove();
                }
            }
        };
    }

    public static <T> Analyzer<T> make(AnalyzerSupplier<Analyzer<T>> supplier, int maxSize) {
        return new ConcurrentAnalyzer<>(maxSize, supplier);
    }

    @Override
    public void init() {
        Analyzer<T> analyzer = threadLocal.get();
        analyzer.init();
    }

    @Override
    public boolean analyze(String... record) {
        Analyzer<T> analyzer = threadLocal.get();
        return analyzer.analyze(record);
    }

    @Override
    public void end() {
        Analyzer<T> analyzer = threadLocal.get();
        analyzer.end();
    }

    @Override
    public List<T> getResult() {
        Analyzer<T> analyzer = threadLocal.get();
        return analyzer.getResult();
    }

    @Override
    public void close() throws Exception {
        // Return previously borrowed instance to pool
        threadLocal.remove();
    }

    private static class Factory<T> implements KeyedPoolableObjectFactory<Thread, Analyzer<T>> {

        private final AnalyzerSupplier<Analyzer<T>> supplier;

        public Factory(AnalyzerSupplier<Analyzer<T>> supplier) {
            this.supplier = supplier;
        }

        @Override
        public synchronized Analyzer<T> makeObject(Thread key) throws Exception {
            return supplier.get();
        }

        @Override
        public void destroyObject(Thread key, Analyzer<T> obj) throws Exception {
            // do nothing
        }

        @Override
        public boolean validateObject(Thread key, Analyzer<T> obj) {
            return true;
        }

        @Override
        public void activateObject(Thread key, Analyzer<T> obj) throws Exception {
            // do nothing
        }

        @Override
        public void passivateObject(Thread key, Analyzer<T> obj) throws Exception {
            // do nothing
        }
    }

}
