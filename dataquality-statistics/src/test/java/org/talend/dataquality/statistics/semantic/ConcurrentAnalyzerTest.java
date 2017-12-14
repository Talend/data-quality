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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.AnalyzerSupplier;
import org.talend.dataquality.common.inference.ConcurrentAnalyzer;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;
import org.talend.dataquality.semantic.statistics.SemanticAnalyzer;
import org.talend.dataquality.semantic.statistics.SemanticType;

public class ConcurrentAnalyzerTest extends SemanticStatisticsTestBase {

    private static Logger log = LoggerFactory.getLogger(ConcurrentAnalyzerTest.class);

    private AtomicBoolean errorOccurred = new AtomicBoolean();

    @Before
    public void setUp() throws Exception {
        errorOccurred.set(false);
    }

    @Test
    public void testThreadSafeConcurrentAccess() {
        try {
            final DictionarySnapshot dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
            AnalyzerSupplier<Analyzer<SemanticType>> supplier = new AnalyzerSupplier<Analyzer<SemanticType>>() {

                @Override
                public Analyzer<SemanticType> get() {
                    return new SemanticAnalyzer(dictionarySnapshot);
                }

            };
            final Analyzer<SemanticType> analyzer = ConcurrentAnalyzer.make(supplier, 2);
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    doConcurrentAccess(analyzer, true);
                }
            };
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
            assertEquals("ConcurrentAccess not failed", false, errorOccurred.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Thread has been interrupted");
        }
    }

    @Test
    public void testThreadUnsafeConcurrentAccess() throws Exception {
        final DictionarySnapshot dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
        try (Analyzer<SemanticType> analyzer = new SemanticAnalyzer(dictionarySnapshot)) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    doConcurrentAccess(analyzer, false);
                }

            };
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
            assertEquals("ConcurrentAccess failed", true, errorOccurred.get());
        }
    }

    private void doConcurrentAccess(Analyzer<SemanticType> semanticAnalyzer, boolean isLogEnabled) {
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
    }
}
