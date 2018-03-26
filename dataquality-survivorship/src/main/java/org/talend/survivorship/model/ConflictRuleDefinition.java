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
 * create by zshen rule definition of conflict resolve
 */
public class ConflictRuleDefinition extends RuleDefinition {

    private String fillColumn;

    private boolean duplicateSurCheck;

    private boolean disableRule;

    // The init value is -1 mean that first index is start from 0
    private int indexOrder = -1;

    /**
     * constructor of ConflictRuleDefinition .
     * 
     * @param order The type of rule
     * @param ruleName The name of rule
     * @param referenceColumn The name of reference column
     * @param function The action of current rule
     * @param operation The parameter of function
     * @param targetColumn The name of target column
     * @param ignoreBlanks Whether ignore blanks case in the rule
     * @param fillColumn The name of fill column
     * @param duplicateSurCheck Whether deal with duplicate case in the survived value
     */
    public ConflictRuleDefinition(Order order, String ruleName, String referenceColumn, Function function, String operation,
            String targetColumn, boolean ignoreBlanks, String fillColumn, boolean duplicateSurCheck) {
        super(order, ruleName, referenceColumn, function, operation, targetColumn, ignoreBlanks);
        this.fillColumn = fillColumn;
        this.duplicateSurCheck = duplicateSurCheck;
        this.disableRule = false;
    }

    /**
     * constructor of ConflictRuleDefinition .
     * 
     * @param order The type of rule
     * @param ruleName The name of rule
     * @param referenceColumn The name of reference column
     * @param function The action of current rule
     * @param operation The parameter of function
     * @param targetColumn The name of target column
     * @param ignoreBlanks Whether ignore blanks case in the rule
     * @param fillColumn The name of fill column
     * @param duplicateSurCheck Whether deal with duplicate case in the survived value
     * @param executeIndex The index which current rule execute order
     */
    public ConflictRuleDefinition(DefFunParameter funParameter, Order order, String ruleName, boolean ignoreBlanks,
            boolean duplicateSurCheck, int executeIndex) {
        this(order, ruleName, funParameter.getReferenceColumn(), funParameter.getFunction(), funParameter.getOperation(),
                funParameter.getTargetColumn(), ignoreBlanks, funParameter.getFillColumn(), duplicateSurCheck);
        this.indexOrder = executeIndex;
    }

    /**
     * constructor of ConflictRuleDefinition .
     * 
     * @param order The type of rule
     * @param ruleName The name of rule
     * @param referenceColumn The name of reference column
     * @param function The action of current rule
     * @param operation The parameter of function
     * @param targetColumn The name of target column
     * @param ignoreBlanks Whether ignore blanks case in the rule
     * @param fillColumn The name of fill column
     * @param duplicateSurCheck Whether deal with duplicate case in the survived value
     */
    public ConflictRuleDefinition(DefFunParameter funParameter, Order order, String ruleName, boolean ignoreBlanks,
            boolean duplicateSurCheck, boolean disableRule, int executeIndex) {
        this(order, ruleName, funParameter.getReferenceColumn(), funParameter.getFunction(), funParameter.getOperation(),
                funParameter.getTargetColumn(), ignoreBlanks, duplicateSurCheck, disableRule);
        this.indexOrder = executeIndex;
    }

    /**
     * constructor of ConflictRuleDefinition .
     * 
     * @param order The type of rule
     * @param ruleName The name of rule
     * @param referenceColumn The name of reference column
     * @param function The action of current rule
     * @param operation The parameter of function
     * @param targetColumn The name of target column
     * @param ignoreBlanks Whether ignore blanks case in the rule
     * @param fillColumn The name of fill column
     * @param duplicateSurCheck Whether deal with duplicate case in the survived value
     * @param duplicateSurCheck Whether deal with duplicate case in the survived value
     */
    public ConflictRuleDefinition(Order order, String ruleName, String referenceColumn, Function function, String operation,
            String targetColumn, boolean ignoreBlanks, boolean duplicateSurCheck, boolean disableRule) {
        super(order, ruleName, referenceColumn, function, operation, targetColumn, ignoreBlanks);
        this.fillColumn = null;
        this.duplicateSurCheck = duplicateSurCheck;
        this.disableRule = disableRule;
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
     * Getter for duplicateSurCheck.
     * 
     * @return the duplicateSurCheck
     */
    public boolean isDuplicateSurCheck() {
        return this.duplicateSurCheck;
    }

    /**
     * Getter for disableRule.
     * 
     * @return the disableRule
     */
    public boolean isDisableRule() {
        return this.disableRule;
    }

    /**
     * Getter for indexOrder.
     * 
     * @return the indexOrder
     */
    public int getIndexOrder() {
        return indexOrder;
    }

    /**
     * Sets the indexOrder.
     * 
     * @param indexOrder the indexOrder to set
     */
    public void setIndexOrder(int indexOrder) {
        this.indexOrder = indexOrder;
    }

}
