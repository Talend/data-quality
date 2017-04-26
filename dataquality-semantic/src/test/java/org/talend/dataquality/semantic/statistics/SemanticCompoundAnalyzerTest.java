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
package org.talend.dataquality.semantic.statistics;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

public class SemanticCompoundAnalyzerTest {

    private CategoryRecognizerBuilder builder;

    public static final String PHONE = "PHONE";

    final List<String[]> TEST_RECORDS_PHONE = new ArrayList<String[]>() {

        private static final long serialVersionUID = 1L;

        {
            add(new String[] { "0145689856", "02045689856", "15207777777", "15207777777", "0145689856" });
            add(new String[] { "0145689856", "02045689856", "15207777777", "02045689856", "0145689856" });

            add(new String[] { "0145689856", "02045689856", "15207777777", "15207777777", "15207777777" });
            add(new String[] { "0145689856", "02045689856", "15207777777", "15207777777", "02045689856" });

        }
    };

    final List<String> EXPECTED_CATEGORY_PHONE = Arrays.asList(new String[] { SemanticCategoryEnum.FR_PHONE.name(),
            SemanticCategoryEnum.DE_PHONE.name(), SemanticCategoryEnum.US_PHONE.name(), PHONE, PHONE });

    @Before
    public void setUp() throws Exception {
        final URI ddPath = this.getClass().getResource(CategoryRecognizerBuilder.DEFAULT_DD_PATH).toURI();
        final URI kwPath = this.getClass().getResource(CategoryRecognizerBuilder.DEFAULT_KW_PATH).toURI();
        final URI rePath = this.getClass().getResource(CategoryRecognizerBuilder.DEFAULT_RE_PATH).toURI();

        builder = CategoryRecognizerBuilder.newBuilder() //
                .ddPath(ddPath) //
                .kwPath(kwPath).regexPath(rePath) //
                .lucene();
    }

    @Test
    public void testPhone() {
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(builder);

        Analyzer<Analyzers.Result> analyzer = Analyzers.with(semanticAnalyzer);
        analyzer.init();
        for (String[] record : TEST_RECORDS_PHONE) {
            analyzer.analyze(record);
        }
        analyzer.end();

        for (int i = 0; i < EXPECTED_CATEGORY_PHONE.size(); i++) {
            Analyzers.Result result = analyzer.getResult().get(i);

            if (result.exist(SemanticType.class)) {
                final SemanticType semanticType = result.get(SemanticType.class);
                final String suggestedCategory = semanticType.getSuggestedCategory();
                assertEquals("Unexpected Category for i = " + i, EXPECTED_CATEGORY_PHONE.get(i), suggestedCategory);
            }
        }
    }

}
