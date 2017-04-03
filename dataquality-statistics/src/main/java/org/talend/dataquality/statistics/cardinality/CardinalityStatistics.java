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

import java.util.HashSet;
import java.util.Set;

/**
 * Compute cardinalities in memory, use {@link CardinalityHLLAnalyzer} instead if large data set are computed.
 * 
 * @author zhao
 *
 */
public class CardinalityStatistics extends AbstractCardinalityStatistics {

    private final Set<String> distinctData = new HashSet<>();

    public void add(String colStr) {
        distinctData.add(colStr);
    }

    public long getDistinctCount() {
        return distinctData.size();
    }

}
