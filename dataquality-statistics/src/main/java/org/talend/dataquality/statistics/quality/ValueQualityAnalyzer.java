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
import org.talend.dataquality.common.inference.Metadata;
import org.talend.dataquality.common.inference.QualityAnalyzer;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.statistics.type.DataTypeEnum;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class ValueQualityAnalyzer implements Analyzer<ValueQualityStatistics> {

    private static final long serialVersionUID = -5951511723860660263L;

    private static Logger log = LoggerFactory.getLogger(ValueQualityAnalyzer.class);

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

    /**
     * @deprecated use
     * {@link DataTypeQualityAnalyzer#DataTypeQualityAnalyzer(org.talend.datascience.common.inference.type.DataTypeEnum...)}
     * @param types
     */
    @Deprecated
    public ValueQualityAnalyzer(DataTypeEnum... types) {
        this(new DataTypeQualityAnalyzer(types), null);
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

    /**
     * @param another value quality analyzer Note: 1. if another is null, return this; 2. the type of another should be
     * ValueQualityAnalyzer.
     */
    @Override
    public Analyzer<ValueQualityStatistics> merge(Analyzer<ValueQualityStatistics> another) {

        if (another == null) {
            log.warn("Another analyzer is null, have nothing to merge!");
            return this;
        }

        if (!(another instanceof ValueQualityAnalyzer)) {
            throw new IllegalArgumentException("Worng type error! Expected type is ValueQualityAnalyzer");
        }

        QualityAnalyzer<ValueQualityStatistics, DataTypeEnum[]> anotherDataTypeQualityAnalyzer = ((ValueQualityAnalyzer) another).dataTypeQualityAnalyzer;
        QualityAnalyzer<ValueQualityStatistics, String[]> anotherSemanticQualityAnalyzer = ((ValueQualityAnalyzer) another).semanticQualityAnalyzer;

        Analyzer<ValueQualityStatistics> mergedDataTypeQualityAnalyzer = this.dataTypeQualityAnalyzer
                .merge(anotherDataTypeQualityAnalyzer);

        Analyzer<ValueQualityStatistics> mergedSemanticQualityAnalyzer = null;
        if (this.semanticQualityAnalyzer != null) {
            mergedSemanticQualityAnalyzer = this.semanticQualityAnalyzer.merge(anotherSemanticQualityAnalyzer);
        } else if (anotherSemanticQualityAnalyzer != null) {
            mergedSemanticQualityAnalyzer = anotherSemanticQualityAnalyzer;
        }

        return new ValueQualityAnalyzer((QualityAnalyzer<ValueQualityStatistics, DataTypeEnum[]>) mergedDataTypeQualityAnalyzer,
                (QualityAnalyzer<ValueQualityStatistics, String[]>) mergedSemanticQualityAnalyzer);
    }

    @Override
    public void close() throws Exception {
    }

}
