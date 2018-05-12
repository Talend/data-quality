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
package org.talend.dataquality.statistics.quality;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.QualityAnalyzer;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class ValueQualityAnalyzer implements Analyzer<ValueQualityStatistics> {

    private static final long serialVersionUID = -5951511723860660263L;

    private final QualityAnalyzer<ValueQualityStatistics, DataTypeEnum[]> dataTypeQualityAnalyzer;

    private final QualityAnalyzer<ValueQualityStatistics, String[]> semanticQualityAnalyzer;

    public ValueQualityAnalyzer(QualityAnalyzer<ValueQualityStatistics, DataTypeEnum[]> dataTypeQualityAnalyzer,
            QualityAnalyzer<ValueQualityStatistics, String[]> semanticQualityAnalyzer, boolean isStoreInvalidValues) {

        if (dataTypeQualityAnalyzer == null)
            throw new NullArgumentException("dataTypeQualityAnalyzer");

        this.dataTypeQualityAnalyzer = dataTypeQualityAnalyzer;
        this.semanticQualityAnalyzer = semanticQualityAnalyzer;
        setStoreInvalidValues(isStoreInvalidValues);
    }

    public ValueQualityAnalyzer(QualityAnalyzer<ValueQualityStatistics, DataTypeEnum[]> dataTypeQualityAnalyzer,
            QualityAnalyzer<ValueQualityStatistics, String[]> semanticQualityAnalyzer) {
        this(dataTypeQualityAnalyzer, semanticQualityAnalyzer, true);
    }

    /**
     * @deprecated use
     * {@link DataTypeQualityAnalyzer#DataTypeQualityAnalyzer(org.talend.datascience.common.inference.type.DataTypeEnum[], boolean)}
     * instead
     * @param types
     * @param isStoreInvalidValues
     */
    @Deprecated
    public ValueQualityAnalyzer(DataTypeEnum[] types, boolean isStoreInvalidValues) {
        this(new DataTypeQualityAnalyzer(types, isStoreInvalidValues), null, isStoreInvalidValues);
    }

    @Override
    public void init() {
        dataTypeQualityAnalyzer.init();
        if (semanticQualityAnalyzer != null) {
            semanticQualityAnalyzer.init();
        }
    }

    /**
     * @deprecated use {@link #addParameters(java.util.Map)} instead
     * @param isStoreInvalidValues
     */
    public void setStoreInvalidValues(boolean isStoreInvalidValues) {
        dataTypeQualityAnalyzer.setStoreInvalidValues(isStoreInvalidValues);
        if (semanticQualityAnalyzer != null)
            semanticQualityAnalyzer.setStoreInvalidValues(isStoreInvalidValues);

    }

    @Override
    public boolean analyze(String... record) {
        boolean status = this.dataTypeQualityAnalyzer.analyze(record);
        if (status && this.semanticQualityAnalyzer != null) {
            status = this.semanticQualityAnalyzer.analyze(record);
        }
        return status;
    }

    @Override
    public void end() {
        // Nothing to do.
    }

    @Override
    public List<ValueQualityStatistics> getResult() {
        if (semanticQualityAnalyzer == null) {
            return dataTypeQualityAnalyzer.getResult();
        } else {
            List<ValueQualityStatistics> aggregatedResult = new ArrayList<ValueQualityStatistics>();
            List<ValueQualityStatistics> dataTypeQualityResult = dataTypeQualityAnalyzer.getResult();
            List<ValueQualityStatistics> semanticQualityResult = semanticQualityAnalyzer.getResult();

            for (int i = 0; i < dataTypeQualityResult.size(); i++) {
                if ("UNKNOWN".equals(semanticQualityAnalyzer.getTypes()[i])) {
                    aggregatedResult.add(dataTypeQualityResult.get(i));
                } else {
                    aggregatedResult.add(semanticQualityResult.get(i));
                }
            }
            return aggregatedResult;
        }
    }

    @Override
    public void close() throws Exception {
    }

}
