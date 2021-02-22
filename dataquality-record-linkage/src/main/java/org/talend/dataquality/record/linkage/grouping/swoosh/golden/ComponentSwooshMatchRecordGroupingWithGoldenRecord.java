// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.grouping.swoosh.golden;

import org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;

public class ComponentSwooshMatchRecordGroupingWithGoldenRecord extends ComponentSwooshMatchRecordGrouping {

    @Override
    protected void doSwooshMatchWithSingle() {
        swooshGrouping.swooshMatchWithGolden(combinedRecordMatcher, survivorShipAlgorithmParams);
    }

    @Override
    protected void addNewRow(RichRecord row) {
        if (row.isClone()) {
            return;
        }
        super.addNewRow(row);
    }

}
