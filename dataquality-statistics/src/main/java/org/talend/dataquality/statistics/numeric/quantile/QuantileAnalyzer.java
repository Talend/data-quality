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
package org.talend.dataquality.statistics.numeric.quantile;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.talend.daikon.number.BigDecimalParser;
import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.statistics.numeric.NumericalStatisticsAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;
import org.talend.dataquality.statistics.type.TypeInferenceUtils;

/**
 * Analyzer quantile with apache commons match library.<br>
 * See more details refer to {@link Median}
 * 
 * @author zhao
 *
 */
public class QuantileAnalyzer extends NumericalStatisticsAnalyzer<QuantileStatistics> {

    private static final long serialVersionUID = 6841816568752139978L;

    private final ResizableList<QuantileStatistics> stats = new ResizableList<>(QuantileStatistics.class);

    public QuantileAnalyzer(DataTypeEnum[] types) {
        super(types);
    }

    @Override
    public void init() {
        super.init();
        stats.clear();
    }

    @Override
    public boolean analyze(String... record) {
        DataTypeEnum[] types = getTypes();
        if (record.length != types.length)
            throw new IllegalArgumentException("Each column of the record should be declared a DataType.Type corresponding! \n"
                    + types.length + " type(s) declared in this quantile analyzer but " + record.length
                    + " column(s) was found in this record. \n"
                    + "Using method: setTypes(DataType.Type[] types) to set the types.");

        stats.resize(record.length);
        for (int idx : this.getStatColIdx()) {// analysis each numerical column in the record
            if (!TypeInferenceUtils.isValid(types[idx], record[idx])) {
                continue;
            }
            QuantileStatistics freqStats = stats.get(idx);
            try {
                freqStats.add(BigDecimalParser.toBigDecimal(record[idx]).doubleValue());
            } catch (NumberFormatException e) {
                continue;
            }
        }
        return true;
    }

    @Override
    public void end() {
        for (QuantileStatistics qs : stats) {
            qs.endAddValue();
        }
    }

    @Override
    public List<QuantileStatistics> getResult() {
        return stats;
    }

}
