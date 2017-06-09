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
package org.talend.survivorship.action;

import org.talend.survivorship.model.DataSet;

/**
 * create by zshen parameter of action
 */
public class ActionParameter {

    /**
     * The dataSet store all of data in here
     */
    private DataSet dataset;

    /**
     * Current input data
     */
    private Object inputData;

    /**
     * The row number of current input data
     */
    private int rowNum;

    /**
     * The name of current column
     */
    private String column;

    /**
     * The name of current rule
     */
    private String ruleName;

    /**
     * Record that whether we should ignore blanks
     */
    private boolean ignoreBlanks;

    /**
     * The parameter of some special action(Expression,MatchRegex,SurviveAs)
     */
    private String expression;

    /**
     * constructor of ActionParameter.
     */
    public ActionParameter(DataSet dataset, Object inputData, int rowNum, String column, String ruleName, String expression,
            boolean ignoreBlanks) {
        this.dataset = dataset;
        this.inputData = inputData;
        this.rowNum = rowNum;
        this.column = column;
        this.ruleName = ruleName;
        this.ignoreBlanks = ignoreBlanks;
        this.expression = expression;
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
     * Getter for inputData.
     * 
     * @return the inputData
     */
    public Object getInputData() {
        return this.inputData;
    }

    /**
     * Getter for rowNum.
     * 
     * @return the rowNum
     */
    public int getRowNum() {
        return this.rowNum;
    }

    /**
     * Getter for column.
     * 
     * @return the column
     */
    public String getColumn() {
        return this.column;
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
     * Getter for ignoreBlanks.
     * 
     * @return the ignoreBlanks
     */
    public boolean isIgnoreBlanks() {
        return this.ignoreBlanks;
    }

    /**
     * Getter for expression.
     * 
     * @return the expression
     */
    public String getExpression() {
        return this.expression;
    }

}