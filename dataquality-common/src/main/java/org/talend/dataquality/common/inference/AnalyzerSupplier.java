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
package org.talend.dataquality.common.inference;

/**
 * Suplier that provide analyzer instance.
 * 
 * @author mzhao
 * @since 1.3
 * @param <T> Type of {{@link Analyzer}
 */
public interface AnalyzerSupplier<T> {

    /**
     * @return type of analyzer
     */
    public T get();
}
