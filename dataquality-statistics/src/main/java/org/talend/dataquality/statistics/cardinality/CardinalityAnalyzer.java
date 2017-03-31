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

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.ResizableList;

/**
 * Be caution that this implementation will lead to serious memory issues when data becoming large. Use {
 * {@link CardinalityHLLAnalyzer} instead by loose the precision.
 *
 * @author zhao
 */
public class CardinalityAnalyzer implements Analyzer<CardinalityStatistics> {

    private static final long serialVersionUID = 1386109348901204598L;

    private ResizableList<CardinalityStatistics> cardinalityStatistics = new ResizableList<>(CardinalityStatistics.class);

    @Override
    public boolean analyze(String... record) {

        if (record == null) {
            return true;
        }
        cardinalityStatistics.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final CardinalityStatistics cardStats = cardinalityStatistics.get(i);
            cardStats.add(record[i]);
            cardStats.incrementCount();
        }
        return true;

    }

    @Override
    public Analyzer<CardinalityStatistics> merge(Analyzer<CardinalityStatistics> another) {
        throw new NotImplementedException();
    }

    @Override
    public void init() {
        cardinalityStatistics.clear();
    }

    @Override
    public void end() {
    }

    @Override
    public List<CardinalityStatistics> getResult() {
        return cardinalityStatistics;
    }

    @Override
    public void close() throws Exception {
    }
}
