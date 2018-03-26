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

import org.talend.survivorship.action.ExcludeValuesAction;
import org.talend.survivorship.action.ExpressionAction;
import org.talend.survivorship.action.FillEmptyAction;
import org.talend.survivorship.action.ISurvivorshipAction;
import org.talend.survivorship.action.LargestAction;
import org.talend.survivorship.action.LongestAction;
import org.talend.survivorship.action.MatchRegexAction;
import org.talend.survivorship.action.MostAncientAction;
import org.talend.survivorship.action.MostCommonAction;
import org.talend.survivorship.action.MostCompleteAction;
import org.talend.survivorship.action.MostRecentAction;
import org.talend.survivorship.action.RemoveDuplicateAction;
import org.talend.survivorship.action.ShortestAction;
import org.talend.survivorship.action.SmallestAction;
import org.talend.survivorship.action.SurviveAsAction;

/**
 * represents definition of a user rule from the component.
 */
public class RuleDefinition {

    private final Order order;

    private final String ruleName;

    private final String targetColumn;

    private final Function function;

    private final String referenceColumn;

    private final String operation;

    private final boolean ignoreBlanks;

    /**
     * @param order
     * @param ruleName
     * @param referenceColumn
     * @param function
     * @param targetColumn
     * @param operator
     * @param operationValue
     * @param ignoreBlanks
     */
    public RuleDefinition(Order order, String ruleName, String referenceColumn, Function function, String operation,
            String targetColumn, boolean ignoreBlanks) {
        super();
        this.order = order;
        this.ruleName = ruleName;
        this.referenceColumn = referenceColumn;
        this.function = function;
        this.targetColumn = targetColumn;
        this.operation = operation;
        this.ignoreBlanks = ignoreBlanks;
    }

    /**
     * @param order
     * @param ruleName
     * @param referenceColumn
     * @param function
     * @param targetColumn
     * @param operator
     * @param operationValue
     * @param ignoreBlanks
     * @param funParameter
     */
    public RuleDefinition(DefFunParameter funParameter, Order order, String ruleName, boolean ignoreBlanks) {
        super();
        this.order = order;
        this.ruleName = ruleName;
        this.referenceColumn = funParameter.getReferenceColumn();
        this.function = funParameter.getFunction();
        this.targetColumn = funParameter.getTargetColumn();
        this.operation = funParameter.getOperation();
        this.ignoreBlanks = ignoreBlanks;
    }

    /**
     * Getter for order.
     * 
     * @return the order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Getter for ruleName.
     * 
     * @return the ruleName
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Getter for targetColumn.
     * 
     * @return the targetColumn
     */
    public String getTargetColumn() {
        return targetColumn;
    }

    /**
     * Getter for function.
     * 
     * @return the function
     */
    public Function getFunction() {
        return function;
    }

    /**
     * Getter for referenceColumn.
     * 
     * @return the referenceColumn
     */
    public String getReferenceColumn() {
        return referenceColumn;
    }

    /**
     * Getter for operation.
     * 
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Getter for ignoreBlanks.
     * 
     * @return the ignoreBlanks
     */
    public boolean isIgnoreBlanks() {
        return ignoreBlanks;
    }

    /**
     * The relation between a rule definition and its precedent one.
     */
    public enum Order {
        SEQ("SEQ"), // Sequential //$NON-NLS-1$
        MC("MC"), // Multi-condition //$NON-NLS-1$
        MT("MT"), // Multi-target //$NON-NLS-1$
        CR("CR");// conflict resolved //$NON-NLS-1$

        private final String label;

        Order(String label) {
            this.label = label;
        }

        public static Order get(String label) {

            if (label.equals(SEQ.label)) {
                return SEQ;
            } else if (label.equals(MC.label)) {
                return MC;
            } else if (label.equals(MT.label)) {
                return MT;
            } else {
                return null;
            }
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * Predefined rule types to choose in rule table.
     */
    public enum Function {

        ExcludeValues("ExcludeValues", new ExcludeValuesAction()), //$NON-NLS-1$

        Expression("Expression", new ExpressionAction()), // User defined rules. //$NON-NLS-1$

        FillEmpty("FillEmpty", new FillEmptyAction()), //$NON-NLS-1$

        Largest("Largest", new LargestAction()), //$NON-NLS-1$

        Longest("Longest", new LongestAction()), //$NON-NLS-1$

        SurviveAs("SurviveAs", new SurviveAsAction()), //$NON-NLS-1$

        MatchRegex("MatchRegex", new MatchRegexAction()), //$NON-NLS-1$

        MostAncient("MostAncient", new MostAncientAction()), //$NON-NLS-1$

        MostCommon("MostCommon", new MostCommonAction()), //$NON-NLS-1$

        MostComplete("MostComplete", new MostCompleteAction()), //$NON-NLS-1$

        MostRecent("MostRecent", new MostRecentAction()), //$NON-NLS-1$

        RemoveDuplicate("RemoveDuplicate", new RemoveDuplicateAction()), //$NON-NLS-1$

        Shortest("Shortest", new ShortestAction()), //$NON-NLS-1$

        Smallest("Smallest", new SmallestAction()); //$NON-NLS-1$

        private final String label;

        private final ISurvivorshipAction action;

        Function(String label, ISurvivorshipAction action) {
            this.label = label;
            this.action = action;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return this.label;
        }

        /**
         * Getter for action.
         * 
         * @return the action
         */
        public ISurvivorshipAction getAction() {
            return this.action;
        }

        /**
         * Method get function by label.
         * 
         * @param label the label of the Function
         * @return the Function type given the label or null
         */
        public static Function get(String label) {
            for (Function fun : Function.values()) {
                if (label.equals(fun.label)) {
                    return fun;
                }
            }
            return null;
        }

        /**
         * Get Function by survivored action.
         * 
         * @param label the label of the Function
         * @return the Function type given the label or null
         */
        public static Function getFunction(ISurvivorshipAction action) {
            for (Function fun : Function.values()) {
                if (action.equals(fun.action)) {
                    return fun;

                }
            }
            return null;
        }
    }

}
