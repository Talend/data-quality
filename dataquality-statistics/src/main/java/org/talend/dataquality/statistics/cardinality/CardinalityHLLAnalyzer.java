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

import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.ResizableList;

import com.clearspring.analytics.stream.cardinality.HyperLogLog;

/**
 * Using hyperloglog estimating cardinalities (distinct count)<br/>
 * Parameter <code>rsd</code> can be set in order to have a better balance between precision and space. by {
 * {@link #setRelativeStandardDeviation(int)}<br/>
 * See more description about this parameter at <a href=
 * "https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/stream/cardinality/HyperLogLog.java#L93"
 * >Hyper log log parameter</a>
 *
 * @author zhao
 */
public class CardinalityHLLAnalyzer implements Analyzer<CardinalityHLLStatistics> {

    private static final long serialVersionUID = -5813206492367921798L;

    int rsd = 20; // relative standard deviation

    private ResizableList<CardinalityHLLStatistics> cardinalityStatistics = null;

    @Override
    public void init() {
        cardinalityStatistics = new ResizableList<>(CardinalityHLLStatistics.class);
    }

    /**
     * Set the hyper log log parameter
     *
     * @param rsd
     */
    public void setRelativeStandardDeviation(int rsd) {
        this.rsd = rsd;
    }

    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            return true;
        }
        cardinalityStatistics.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final CardinalityHLLStatistics cardStats = cardinalityStatistics.get(i);
            if (cardStats.getHyperLogLog() == null) {
                cardStats.setHyperLogLog(new HyperLogLog(rsd));
            }
            cardStats.add(record[i]);
            cardStats.incrementCount();
        }
        return true;
    }

    @Override
    public void end() {
    }

    @Override
    public List<CardinalityHLLStatistics> getResult() {
        return cardinalityStatistics;
    }

    @Override
    public void close() throws Exception {
    }

}
