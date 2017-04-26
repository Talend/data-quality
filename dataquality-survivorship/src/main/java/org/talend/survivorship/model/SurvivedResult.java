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

/**
 * create by zshen Temp store survived result
 */
public class SurvivedResult {

    /**
     * The row number of current survived value
     */
    private Integer rowNum;

    /**
     * The column name of current survived value
     */
    private String columnName;

    /**
     * Create by zshen survivedResult constructor comment.
     * 
     * @param rowNum
     * @param columnName
     */
    public SurvivedResult(Integer rowNum, String columnName) {
        super();
        this.rowNum = rowNum;
        this.columnName = columnName;
    }

    /**
     * Getter for rowNum.
     * 
     * @return the rowNum
     */
    public Integer getRowNum() {
        return this.rowNum;
    }

    /**
     * Getter for columnName.
     * 
     * @return the columnName
     */
    public String getColumnName() {
        return this.columnName;
    }

}
