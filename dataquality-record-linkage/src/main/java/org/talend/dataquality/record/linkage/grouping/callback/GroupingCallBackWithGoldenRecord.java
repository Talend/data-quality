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
package org.talend.dataquality.record.linkage.grouping.callback;

import java.util.Set;

import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping;
import org.talend.dataquality.record.linkage.utils.BidiMultiMap;

public class GroupingCallBackWithGoldenRecord<T> extends GroupingCallBack<T> {

    /**
     * @param oldGID2New
     * @param recordGrouping
     */
    public GroupingCallBackWithGoldenRecord(BidiMultiMap<String, String> oldGID2New,
            AbstractRecordGrouping<T> recordGrouping) {
        super(oldGID2New, recordGrouping);
    }

    /**
     * Copy related row with special groupID
     */
    public void outputRelatedRow(Record parentRecord) {
        String parentID = parentRecord.getId();
        Set<String> relatedIds = parentRecord.getRelatedIds();
        if (this.recordGrouping instanceof ComponentSwooshMatchRecordGrouping) {
            for (String relatedID : relatedIds) {
                if (relatedID == parentID && (!parentRecord.isClone())) {
                    continue;
                }
                ((ComponentSwooshMatchRecordGrouping) this.recordGrouping).repeatOutReleatedRecord(relatedID,
                        parentRecord.getGroupId());

            }
        }

    }

}
