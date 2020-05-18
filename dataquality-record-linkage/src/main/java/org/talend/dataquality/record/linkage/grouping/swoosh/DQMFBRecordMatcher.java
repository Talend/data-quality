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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.MFBRecordMatcher;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class DQMFBRecordMatcher extends MFBRecordMatcher {

    /**
     * DOC zshen DQMFBRecordMatcher constructor comment.
     * 
     * @param minConfidenceValue
     */
    public DQMFBRecordMatcher(double minConfidenceValue) {
        super(minConfidenceValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.matchmerge.mfb.MFBRecordMatcher#synRecord2ConFidence(org.talend.dataquality.matchmerge.Record,
     * double)
     */
    @Override
    protected void synRecord2ConFidence(Record record2, double normalizedConfidence) {
        // not need any implements
    }

}
