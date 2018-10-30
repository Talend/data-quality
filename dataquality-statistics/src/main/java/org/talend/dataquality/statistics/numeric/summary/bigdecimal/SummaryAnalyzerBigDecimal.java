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
package org.talend.dataquality.statistics.numeric.summary.bigdecimal;

import org.talend.daikon.number.BigDecimalParser;
import org.talend.dataquality.common.inference.ResizableList;
import org.talend.dataquality.statistics.numeric.NumericalStatisticsAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;
import org.talend.dataquality.statistics.type.TypeInferenceUtils;

import java.util.List;

/**
 * Analyzer for summary statistics with large numbers.
 * 
 */
public class SummaryAnalyzerBigDecimal extends NumericalStatisticsAnalyzer<SummaryStatisticsBigDecimal> {

    private static final long serialVersionUID = 8369753525474844077L;

    private final ResizableList<SummaryStatisticsBigDecimal> summaryStats = new ResizableList<>(
            SummaryStatisticsBigDecimal.class);

    public SummaryAnalyzerBigDecimal(DataTypeEnum[] types) {
        super(types);
    }

    @Override
    public void init() {
        super.init();
        summaryStats.clear();
    }

    @Override
    public boolean analyze(String... record) {
        DataTypeEnum[] types = getTypes();

        if (record.length != types.length)
            throw new IllegalArgumentException("Each column of the record should be declared a DataType.Type corresponding! \n"
                    + types.length + " type(s) declared in this summary analyzer but " + record.length
                    + " column(s) was found in this record. \n"
                    + "Using method: setTypes(DataType.Type[] types) to set the types.");

        summaryStats.resize(record.length);

        for (int idx : this.getStatColIdx()) {// analysis each numerical column in the record
            if (!TypeInferenceUtils.isValid(types[idx], record[idx])) {
                continue;
            }
            final SummaryStatisticsBigDecimal stats = summaryStats.get(idx);
            try {
                stats.addData(BigDecimalParser.toBigDecimal(record[idx]));
            } catch (NumberFormatException e) {
                // The value is ignored
            }
        }
        return true;

    }

    @Override
    public void end() {
        // nothing to do
    }

    @Override
    public List<SummaryStatisticsBigDecimal> getResult() {
        return summaryStats;
    }

}
