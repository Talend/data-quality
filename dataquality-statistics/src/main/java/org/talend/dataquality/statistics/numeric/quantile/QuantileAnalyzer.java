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
package org.talend.dataquality.statistics.numeric.quantile;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * Analyzer quantile with apache commons match library.<br>
 * See more details refer to {@link Median}
 * 
 * @author zhao
 *
 */
public class QuantileAnalyzer extends AbstractQuantileAnalyzer<QuantileStatistics> {

    private static final long serialVersionUID = 6841816568752139978L;

    private final ResizableList<QuantileStatistics> stats = new ResizableList<>(QuantileStatistics.class);

    public QuantileAnalyzer(DataTypeEnum[] types) {
        super(types);
    }

    @Override
    public void end() {
        for (QuantileStatistics qs : stats) {
            qs.endAddValue();
        }
    }

    @Override
    public ResizableList<QuantileStatistics> getStats() {
        return stats;
    }

}
