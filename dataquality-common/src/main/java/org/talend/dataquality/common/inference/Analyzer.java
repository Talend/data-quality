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

import java.io.Serializable;
import java.util.List;

/**
 * Implements analysis on array of Strings ("row" of values). Implementations are expected to be:
 * <ul>
 * <li>Stateful.</li>
 * <li>Not thread safe (no need to enforce thread safety in implementations).</li>
 * </ul>
 * To combine several {@link Analyzer} together see {@link Analyzers}.
 * 
 * @param <T> The type of results built by the implementation.
 */
public interface Analyzer<T> extends Serializable, AutoCloseable {

    /**
     * Prepare implementation for analysis. Implementations may perform various tasks like internal initialization or
     * connection establishment. This method should only be called once.
     */
    void init();

    /**
     * Analyze a single record (row).
     * 
     * @param record A record (row) with a value in each column.
     * @return <code>true</code> if analyze was ok, <code>false</code> otherwise.
     */
    boolean analyze(String... record);

    /**
     * Ends the analysis (implementations may perform result optimizations after the repeated call to
     * {@link #analyze(String[])}).
     */
    void end();

    /**
     * Get the analysis result based on values submitted in {@link #analyze(String[])}.
     *
     * @return The analysis result for each columns in records, item <i> n </i> in list corresponds to <i> nth </i>
     * column in record.
     */
    List<T> getResult();

    /**
     * Merge this analyzer with another one.<br>
     * 
     * @return new analyzer with this and another analyzer merged together.
     */
    Analyzer<T> merge(Analyzer<T> another);

}
