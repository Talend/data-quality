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

import org.talend.survivorship.model.RuleDefinition.Function;

/**
 * The parameter of Function
 */
public class DefFunParameter {

    private String referenceColumn;

    private Function function;

    private String operation;

    private String targetColumn;

    private String fillColumn;

    /**
     * The constructor of DefFunParameter class.
     * 
     * @param referenceColumn
     * @param function
     * @param operation
     * @param targetColumn
     * @param fillColumn
     */
    public DefFunParameter(String referenceColumn, Function function, String operation, String targetColumn, String fillColumn) {
        super();
        this.referenceColumn = referenceColumn;
        this.function = function;
        this.operation = operation;
        this.targetColumn = targetColumn;
        this.fillColumn = fillColumn;
    }

    /**
     * Getter for referenceColumn.
     * 
     * @return the referenceColumn
     */
    protected String getReferenceColumn() {
        return this.referenceColumn;
    }

    /**
     * Getter for function.
     * 
     * @return the function
     */
    protected Function getFunction() {
        return this.function;
    }

    /**
     * Getter for operation.
     * 
     * @return the operation
     */
    protected String getOperation() {
        return this.operation;
    }

    /**
     * Getter for targetColumn.
     * 
     * @return the targetColumn
     */
    protected String getTargetColumn() {
        return this.targetColumn;
    }

    /**
     * Getter for fillColumn.
     * 
     * @return the fillColumn
     */
    protected String getFillColumn() {
        return this.fillColumn;
    }

}
