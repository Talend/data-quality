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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A sub dataset of original dataset
 */
public class SubDataSet extends DataSet {

    private List<Integer> dataSetIndex;

    private Map<Attribute, FilledAttribute> fillAttributeMap;

    private DataSet orignialDataSet = null;

    /**
     * Create by zshen Create a new sub dataset.
     * 
     * @param dataSet original dataset
     * @param conflictDataIndexList index list all of conflict data
     */
    public SubDataSet(DataSet dataSet, List<Integer> conflictDataIndexList) {
        super(dataSet.getColumnList(), dataSet.getRecordList());
        copyConflictDataMap(dataSet);
        dataSetIndex = conflictDataIndexList;
        this.survivorIndexMap = dataSet.survivorIndexMap;
        orignialDataSet = dataSet;
        this.setColumnOrder(dataSet.getColumnOrder());
    }

    /**
     * Copy conflict data map from the dataSet
     * 
     * @param dataSet
     */
    private void copyConflictDataMap(DataSet dataSet) {
        if (dataSet instanceof SubDataSet) {
            this.fillAttributeMap = ((SubDataSet) dataSet).getFillAttributeMap();
        }
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
                return col.getAttributesByFilter(dataSetIndex, this.fillAttributeMap);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.model.DataSet#getValue(int, java.lang.String)
     */
    @Override
    public Object getValueAfterFiled(int rowNum, String colName) {
        Record record = this.getRecordList().get(rowNum);
        Attribute originalAttribute = record.getAttribute(colName);
        if (this.fillAttributeMap != null) {
            FilledAttribute filledAttribute = this.fillAttributeMap.get(originalAttribute);
            if (filledAttribute != null) {
                return filledAttribute.getValue();
            }
        }
        return originalAttribute.getValue();
    }

    /**
     * Getter for dataSetIndex.
     * 
     * @return the dataSetIndex
     */
    public List<Integer> getDataSetIndex() {
        return this.dataSetIndex;
    }

    public void addFillAttributeMap(FilledAttribute filledAttribute) {

        getFillAttributeMap().put(filledAttribute.getOrignalAttribute(), filledAttribute);
    }

    /**
     * Getter for fillAttributeMap.
     * 
     * @return the fillAttributeMap
     */
    public Map<Attribute, FilledAttribute> getFillAttributeMap() {
        if (fillAttributeMap == null) {
            fillAttributeMap = new HashMap<>();
        }
        return this.fillAttributeMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.model.DataSet#checkDupSurValue(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean checkDupSurValue(Object value, String colName) {
        return this.orignialDataSet.checkDupSurValue(value, colName);
    }

}
