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
package org.talend.survivorship.action.handler;

import java.util.List;
import java.util.Map;

import org.talend.survivorship.action.ISurvivoredAction;
import org.talend.survivorship.model.Column;
import org.talend.survivorship.model.DataSet;
import org.talend.survivorship.model.RuleDefinition.Function;

/**
 * create by zshen parameter of handler node
 */
public class HandlerParameter {

    /**
     * original dataset never copy
     */
    private DataSet dataset;

    /**
     * record which one is reference column
     */
    private Column refColumn;

    /**
     * record which one is target column
     */
    private Column tarColumn;

    /**
     * record which one is fill column
     */
    private String fillColumn;

    /**
     * record the name of rule
     */
    private String ruleName;

    /**
     * record mapping relation between column and it's index
     */
    private Map<String, Integer> columnIndexMap;

    /**
     * function information
     */
    private FunctionParameter functionPara;

    /**
     * 
     * create by zshen constructor of HandlerParameter.
     * 
     * @param dataset
     * @param refColumn
     * @param tarColumn
     * @param ruleName
     * @param columnIndexMap
     * @param fillColumn
     * @param funPar
     */
    public HandlerParameter(DataSet dataset, Column refColumn, Column tarColumn, String ruleName,
            Map<String, Integer> columnIndexMap, String fillColumn, FunctionParameter funPar) {
        this.dataset = dataset;
        this.functionPara = funPar;
        this.refColumn = refColumn;
        this.tarColumn = tarColumn;
        this.ruleName = ruleName;
        this.columnIndexMap = columnIndexMap;
        this.fillColumn = fillColumn;
    }

    /**
     * 
     * create by zshen get reference column value from input arrays
     * 
     * @param inputData input data arrays
     * @return reference column data
     */
    public Object getRefInputData(Object[] inputData) {
        return inputData[this.columnIndexMap.get(refColumn.getName())];
    }

    public Object getTarInputData(Object[] inputDatas) {
        return inputDatas[this.columnIndexMap.get(tarColumn.getName())];
    }

    /**
     * Getter for dataset.
     * 
     * @return the dataset
     */
    public DataSet getDataset() {
        return this.dataset;
    }

    /**
     * Getter for action.
     * 
     * @return the action
     */
    public ISurvivoredAction getAction() {
        return this.functionPara.getAction();
    }

    /**
     * Getter for refColumn.
     * 
     * @return the refColumn
     */
    public Column getRefColumn() {
        return this.refColumn;
    }

    /**
     * Getter for tarColumn.
     * 
     * @return the tarColumn
     */
    public Column getTarColumn() {
        return this.tarColumn;
    }

    /**
     * Getter for ruleName.
     * 
     * @return the ruleName
     */
    public String getRuleName() {
        return this.ruleName;
    }

    /**
     * Getter for expression.
     * 
     * @return the expression
     */
    public String getExpression() {
        return this.functionPara.getExpression();
    }

    /**
     * Getter for isIgnoreBlank.
     * 
     * @return the isIgnoreBlank
     */
    public boolean isIgnoreBlank() {
        return this.functionPara.isIgnoreBlank();
    }

    /**
     * Sets the dataset.
     * 
     * @param dataset the dataset to set
     */
    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }

    /**
     * Sets the refColumn.
     * 
     * @param refColumn the refColumn to set
     */
    public void setRefColumn(Column refColumn) {
        this.refColumn = refColumn;
    }

    /**
     * Sets the tarColumn.
     * 
     * @param tarColumn the tarColumn to set
     */
    public void setTarColumn(Column tarColumn) {
        this.tarColumn = tarColumn;
    }

    /**
     * Sets the ruleName.
     * 
     * @param ruleName the ruleName to set
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * Getter for columnIndexMap.
     * 
     * @return the columnIndexMap
     */
    public Map<String, Integer> getColumnIndexMap() {
        return this.columnIndexMap;
    }

    public boolean isConflictRow(Integer rowNum) {
        List<Integer> conflictDataIndexList = getConflictDataIndexList();
        return conflictDataIndexList != null && conflictDataIndexList.contains(rowNum);
    }

    public List<Integer> getConflictDataIndexList() {
        return this.getDataset().getConflictDataIndexList(this.getTarColumn().getName());
    }

    public void updateDataSet() {
        this.setDataset(this.getDataset().createSubDataSet(this.getTarColumn().getName()));
    }

    public void addConfDataIndex(Integer index) {
        this.getDataset().addConfDataIndex(this.getTarColumn().getName(), index);
    }

    /**
     * Create by zshen Comment method "getInputData".
     * 
     * @param index
     */
    public Object getInputData(Integer index, String columnName) {
        return this.getDataset().getRecordList().get(index).getAttribute(columnName).getValue();
    }

    /**
     * Sets the fillColumn.
     * 
     * @param fillColumn the fillColumn to set
     */
    public void setFillColumn(String fillColumn) {
        this.fillColumn = fillColumn;
    }

    /**
     * Getter for fillColumn.
     * 
     * @return the fillColumn
     */
    public String getFillColumn() {
        return this.fillColumn;
    }

    /**
     * Getter for isDealDup.
     * 
     * @return the isDealDup
     */
    public boolean isDealDup() {
        return this.functionPara.isDealDup();
    }

    /**
     * 
     * Getter for Function of functionParam.
     * 
     * @return The Function
     */
    public Function getFunction() {
        return this.functionPara.getFunction();
    }

}