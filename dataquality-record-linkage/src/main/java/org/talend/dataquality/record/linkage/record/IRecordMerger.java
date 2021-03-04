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
package org.talend.dataquality.record.linkage.record;

import org.talend.dataquality.matchmerge.Record;

/**
 * A record merger takes records as input and builds a new record based on the values provided as input.
 */
public interface IRecordMerger {

    /**
     * Merges together 2 {@link org.talend.dataquality.matchmerge.Record records} and return a merged record built using
     * values from <code>record1</code> and/or <code>record2</code>.
     * 
     * @param record1 One of the record to be merged. This is not expected to be null.
     * @param record2 One of the record to be merged. This is not expected to be null.
     * @return A record based on values of <code>record1</code> and/or <code>record2</code>.
     * @throws java.lang.IllegalArgumentException if any of the parameter is <code>null</code>.
     */
    Record merge(Record record1, Record record2);

}
