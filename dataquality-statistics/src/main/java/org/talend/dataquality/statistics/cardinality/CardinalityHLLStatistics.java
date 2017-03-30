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
package org.talend.dataquality.statistics.cardinality;

import com.clearspring.analytics.stream.cardinality.HyperLogLog;

/**
 * Cardianlity statistics bean of hypper log log .
 * 
 * @author zhao
 *
 */
public class CardinalityHLLStatistics {

    private long count = 0L;

    private HyperLogLog hyperLogLog = null;

    public CardinalityHLLStatistics() {
    }

    public HyperLogLog getHyperLogLog() {
        return hyperLogLog;
    }

    public long getDuplicateCount() {
        return count - getDistinctCount();
    }

    public long getCount() {
        return count;
    }

    public long getDistinctCount() {
        return hyperLogLog.cardinality();
    }

    public void incrementCount() {
        count += 1;
    }

    public void setHyperLogLog(HyperLogLog hyperLogLog2) {
        this.hyperLogLog = hyperLogLog2;
    }
    
    public void setCount(long count) {
        this.count = count;
    }
}
