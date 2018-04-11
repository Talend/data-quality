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
package org.talend.dataquality.statistics.text;

import java.util.List;

import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.ResizableList;

/**
 * Text length analyzer compute the length of min,max and average length of the record sets.<br>
 * For more details please refer to documentation: <a
 * href="https://help.talend.com/pages/viewpage.action?pageId=261412880&thc_login=done">Text statistics</a>
 * 
 * @author zhao
 *
 */
public class TextLengthAnalyzer implements Analyzer<TextLengthStatistics> {

    private static final long serialVersionUID = -9106960246571082963L;

    private ResizableList<TextLengthStatistics> textStatistics = new ResizableList<>(TextLengthStatistics.class);

    @Override
    public void init() {
        textStatistics.clear();
    }

    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            return true;
        }
        textStatistics.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            TextLengthStatistics freqStats = textStatistics.get(i);
            freqStats.add(record[i]);
        }
        return true;
    }

    @Override
    public void end() {
        // Nothing to be done.

    }

    @Override
    public List<TextLengthStatistics> getResult() {
        return textStatistics;
    }

    @Override
    public void close() throws Exception {

    }

}
