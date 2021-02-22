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

import java.util.List;

import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.RecordGenerator;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQRecordIterator;

public class DQGoldenRecordIterator extends DQRecordIterator {

    public static final String FORBIDDENLISTCOLUMNNAME = "forbiddenList";

    /**
     * @param size
     * @param generators
     */
    public DQGoldenRecordIterator(int size, List<RecordGenerator> generators) {
        super(size, generators);
    }

    @Override
    protected Record createRecord(List<Attribute> attriVector, List<DQAttribute<?>> originalRow) {
        Record createRecord = super.createRecord(attriVector, originalRow);
        Attribute lastAttribute = attriVector.get(attriVector.size() - 1);
        String lastAttributeLabel = lastAttribute.getLabel();
        if (FORBIDDENLISTCOLUMNNAME.equalsIgnoreCase(lastAttributeLabel)) {
            String forbiddenListValue = lastAttribute.getValue();
            if (forbiddenListValue == null) {
                return createRecord;
            }
            for (String forbiddenID : forbiddenListValue.split(",")) {
                createRecord.getForbiddenList().add(forbiddenID);
            }
        }
        // setting forbidden list here
        return createRecord;
    }

}
