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
package org.talend.dataquality.statistics.semantic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.AnalyzerSupplier;
import org.talend.dataquality.common.inference.ConcurrentAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;
import org.talend.dataquality.statistics.type.DataTypeOccurences;

public class ConcurrentAnalyzerTest extends SemanticStatisticsTestBase {

    private static Logger log = LoggerFactory.getLogger(ConcurrentAnalyzerTest.class);

    private AtomicBoolean errorOccurred = new AtomicBoolean();

    protected final List<List<String[]>> INPUT_RECORDS = new ArrayList<List<String[]>>() {

        private static final long serialVersionUID = 1L;

        {
            add(getRecords(SemanticStatisticsTestBase.class.getResourceAsStream("customers_100_bug_TDQ10380.csv")));
            add(getRecords(SemanticStatisticsTestBase.class.getResourceAsStream("avengers.csv")));
            add(getRecords(SemanticStatisticsTestBase.class.getResourceAsStream("gender.csv")));
            add(getRecords(SemanticStatisticsTestBase.class.getResourceAsStream("dataset_with_invalid_records.csv")));

        }
    };

    protected final List<DataTypeEnum[]> EXPECTED_CATEGORIES = new ArrayList<DataTypeEnum[]>() {

        private static final long serialVersionUID = 1L;

        {
            add(new DataTypeEnum[] { // dataset[0]
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.INTEGER, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE,
                    DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.DATE,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.DATE,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.DATE,
                    DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.DATE, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.DOUBLE,
                    DataTypeEnum.DOUBLE });
            add(new DataTypeEnum[] { // dataset[1]
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING });
            add(new DataTypeEnum[] { // dataset[2]
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING });
            add(new DataTypeEnum[] { // dataset[3]
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.STRING,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.INTEGER,
                    DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER });
        }
    };

    @Before
    public void setUp() {
        errorOccurred.set(false);
    }

    @Test
    public void testThreadSafeConcurrentAccess() {
        try {
            AnalyzerSupplier<Analyzer<DataTypeOccurences>> supplier = () -> new DataTypeAnalyzer();
            final Analyzer<DataTypeOccurences> analyzer = ConcurrentAnalyzer.make(supplier, 2);
            Runnable r = () -> doConcurrentAccess(analyzer, true);
            List<Thread> workers = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                workers.add(new Thread(r));
            }
            for (Thread worker : workers) {
                worker.start();
            }
            for (Thread worker : workers) {
                worker.join();
            }
            assertFalse("ConcurrentAccess not failed", errorOccurred.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Thread has been interrupted");
        }
    }

    @Test
    @Ignore
    // FIXME this test is unstable, to be investigated with TDQ-18024
    public void testThreadUnsafeConcurrentAccess() throws Exception {
        try (Analyzer<DataTypeOccurences> analyzer = new DataTypeAnalyzer()) {
            Runnable r = () -> doConcurrentAccess(analyzer, false);
            List<Thread> workers = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                workers.add(new Thread(r));
            }
            for (Thread worker : workers) {
                worker.start();
            }
            for (Thread worker : workers) {
                worker.join();
            }
            assertTrue("ConcurrentAccess failed", errorOccurred.get());
        }
    }

    private void doConcurrentAccess(Analyzer<DataTypeOccurences> analyzer, boolean isLogEnabled) {
        analyzer.init();
        int datasetID = 2;//(int) Math.floor(Math.random() * 4);

        try {

            for (String[] data : INPUT_RECORDS.get(datasetID)) {
                try {
                    analyzer.analyze(data);
                } catch (Throwable e) {
                    errorOccurred.set(true);
                    if (isLogEnabled) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            analyzer.end();
            List<DataTypeOccurences> results = analyzer.getResult();
            if (results.isEmpty()) {
                errorOccurred.set(true);
                if (isLogEnabled) {
                    log.error("result is empty");
                }
            }

            int columnIndex = 0;
            for (DataTypeOccurences column : results) {
                if (!EXPECTED_CATEGORIES.get(datasetID)[columnIndex].equals(column.getSuggestedType())) {
                    errorOccurred.set(true);
                    if (isLogEnabled) {
                        log.error("assertion fails on column[" + columnIndex + "] of dataset[" + datasetID
                                + "]. expected: " + EXPECTED_CATEGORIES.get(datasetID)[columnIndex] + " actual: "
                                + column.getSuggestedType());
                    }
                }
                columnIndex++;
            }
        } catch (Exception e) {
            errorOccurred.set(true);
            if (isLogEnabled) {
                log.error(e.getMessage(), e);
            }
        } finally {
            try {
                analyzer.close();
            } catch (Exception e) {
                // TODO : Solve this issue
                throw new RuntimeException(e);
            }
        }
    }

    /*private void doConcurrentAccess(Analyzer<SemanticType> semanticAnalyzer, boolean isLogEnabled) {
        semanticAnalyzer.init();
        int datasetID = (int) Math.floor(Math.random() * 4);
    
        try {
    
            for (String[] data : INPUT_RECORDS.get(datasetID)) {
                try {
                    semanticAnalyzer.analyze(data);
                } catch (Throwable e) {
                    errorOccurred.set(true);
                    if (isLogEnabled) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            semanticAnalyzer.end();
            List<SemanticType> result = semanticAnalyzer.getResult();
            int columnIndex = 0;
    
            if (result.isEmpty()) {
                errorOccurred.set(true);
                if (isLogEnabled) {
                    log.error("result is empty");
                }
            }
            for (SemanticType columnSemanticType : result) {
                if (!EXPECTED_CATEGORIES.get(datasetID)[columnIndex].equals(columnSemanticType.getSuggestedCategory())) {
                    errorOccurred.set(true);
                    if (isLogEnabled) {
                        log.error("assertion fails on column[" + columnIndex + "] of dataset[" + datasetID + "}. expected: "
                                + EXPECTED_CATEGORIES.get(datasetID)[columnIndex] + " actual: "
                                + columnSemanticType.getSuggestedCategory());
                    }
                }
                columnIndex++;
            }
        } catch (Exception e) {
            errorOccurred.set(true);
            if (isLogEnabled) {
                log.error(e.getMessage(), e);
            }
        } finally {
            try {
                semanticAnalyzer.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }*/
}
