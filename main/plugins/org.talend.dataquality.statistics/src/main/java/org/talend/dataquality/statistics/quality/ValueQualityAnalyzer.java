// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import org.apache.log4j.Logger;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.QualityAnalyzer;
import org.talend.datascience.common.inference.ValueQualityStatistics;
import org.talend.datascience.common.inference.type.DataType;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class ValueQualityAnalyzer implements Analyzer<ValueQualityStatistics> {

    private static final long serialVersionUID = -5951511723860660263L;

    private final QualityAnalyzer<ValueQualityStatistics, DataType.Type[]> dataTypeQualityAnalyzer;

    private final QualityAnalyzer<ValueQualityStatistics, String[]> semanticQualityAnalyzer;

    private static Logger log = Logger.getLogger(ValueQualityAnalyzer.class);

    public ValueQualityAnalyzer(QualityAnalyzer<ValueQualityStatistics, DataType.Type[]> dataTypeQualityAnalyzer,
            QualityAnalyzer<ValueQualityStatistics, String[]> semanticQualityAnalyzer, boolean isStoreInvalidValues) {

        if (dataTypeQualityAnalyzer == null)
            throw new NullArgumentException("dataTypeQualityAnalyzer");

        this.dataTypeQualityAnalyzer = dataTypeQualityAnalyzer;
        this.semanticQualityAnalyzer = semanticQualityAnalyzer;
        setStoreInvalidValues(isStoreInvalidValues);
    }

    public ValueQualityAnalyzer(QualityAnalyzer<ValueQualityStatistics, DataType.Type[]> dataTypeQualityAnalyzer,
            QualityAnalyzer<ValueQualityStatistics, String[]> semanticQualityAnalyzer) {
        this(dataTypeQualityAnalyzer, semanticQualityAnalyzer, true);
    }

    /**
     * @deprecated use
     * {@link DataTypeQualityAnalyzer#DataTypeQualityAnalyzer(org.talend.datascience.common.inference.type.DataType.Type[], boolean)}
     * instead
     * @param types
     * @param isStoreInvalidValues
     */
    public ValueQualityAnalyzer(DataType.Type[] types, boolean isStoreInvalidValues) {
        this(new DataTypeQualityAnalyzer(types, isStoreInvalidValues), null, isStoreInvalidValues);
    }

    /**
     * @deprecated use
     * {@link DataTypeQualityAnalyzer#DataTypeQualityAnalyzer(org.talend.datascience.common.inference.type.DataType.Type...)}
     * @param types
     */
    public ValueQualityAnalyzer(DataType.Type... types) {
        this(new DataTypeQualityAnalyzer(types), null);
    }

    public void init() {
        dataTypeQualityAnalyzer.init();
        if (semanticQualityAnalyzer != null)
            semanticQualityAnalyzer.init();

    }

    public void setStoreInvalidValues(boolean isStoreInvalidValues) {
        dataTypeQualityAnalyzer.setStoreInvalidValues(isStoreInvalidValues);
        if (semanticQualityAnalyzer != null)
            semanticQualityAnalyzer.setStoreInvalidValues(isStoreInvalidValues);

    }

    /**
     * Analyze record of Array of string type, this method is used in scala library which not support parameterized
     * array type.
     * 
     * @param record
     * @return
     */
    public boolean analyzeArray(String[] record) {
        return analyze(record);
    }

    public boolean analyze(String... record) {
        boolean status = this.dataTypeQualityAnalyzer.analyze(record);
        if (status && this.semanticQualityAnalyzer != null) {
            status = this.semanticQualityAnalyzer.analyze(record);
        }
        return status;
    }

    public void end() {
        // Nothing to do.
    }

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
     * @param another value quality analyzer
     * Note: 1. if another is null, return this; 2. the type of another should be ValueQualityAnalyzer.
     */
    public Analyzer<ValueQualityStatistics> merge(Analyzer<ValueQualityStatistics> another) {

        if (another == null) {
            log.warn("Another analyzer is null, have nothing to merge!");
            return this;
        }

        if (!(another instanceof ValueQualityAnalyzer)) {
            throw new IllegalArgumentException("Worng type error! Expected type is ValueQualityAnalyzer");
        }

        QualityAnalyzer<ValueQualityStatistics, DataType.Type[]> anotherDataTypeQualityAnalyzer = ((ValueQualityAnalyzer) another).dataTypeQualityAnalyzer;
        QualityAnalyzer<ValueQualityStatistics, String[]> anotherSemanticQualityAnalyzer = ((ValueQualityAnalyzer) another).semanticQualityAnalyzer;

        Analyzer<ValueQualityStatistics> mergedDataTypeQualityAnalyzer = this.dataTypeQualityAnalyzer
                .merge(anotherDataTypeQualityAnalyzer);

        Analyzer<ValueQualityStatistics> mergedSemanticQualityAnalyzer = null;
        if (this.semanticQualityAnalyzer != null) {
            mergedSemanticQualityAnalyzer = this.semanticQualityAnalyzer.merge(anotherSemanticQualityAnalyzer);
        } else if (anotherSemanticQualityAnalyzer != null) {
            mergedSemanticQualityAnalyzer = anotherSemanticQualityAnalyzer;
        }

        return new ValueQualityAnalyzer((QualityAnalyzer<ValueQualityStatistics, DataType.Type[]>) mergedDataTypeQualityAnalyzer,
                (QualityAnalyzer<ValueQualityStatistics, String[]>) mergedSemanticQualityAnalyzer);
    }

    @Override
    public void close() throws Exception {
    }

}
