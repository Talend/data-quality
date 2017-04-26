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
package org.talend.survivorship.model;

import java.util.Collection;
import java.util.List;

/**
 * Create by zshen a sub dataset of original dataset
 */
public class SubDataSet extends DataSet {

    private List<Integer> dataSetIndex;

    /**
     * Create by zshen Create a new sub dataset.
     * 
     * @param dataSet original dataset
     * @param conflictDataIndexList index list all of conflict data
     */
    public SubDataSet(DataSet dataSet, List<Integer> conflictDataIndexList) {
        super(dataSet.getColumnList(), dataSet.getRecordList());
        dataSetIndex = conflictDataIndexList;
        this.survivorIndexMap = dataSet.survivorIndexMap;
        this.setColumnOrder(dataSet.getColumnOrder());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.model.DataSet#getAttributesByColumn(java.lang.String)
     */
    @Override
    public Collection<Attribute> getAttributesByColumn(String columnName) {
        for (Column col : this.getColumnList()) {
            if (col.getName().equals(columnName)) {
                return col.getAttributesByFilter(dataSetIndex);
            }
        }
        return null;
    }

    /**
     * Getter for dataSetIndex.
     * 
     * @return the dataSetIndex
     */
    public List<Integer> getDataSetIndex() {
        return this.dataSetIndex;
    }

}
