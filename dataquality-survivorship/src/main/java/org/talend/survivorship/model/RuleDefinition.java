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

import org.talend.survivorship.action.ExclusivenessAction;
import org.talend.survivorship.action.ExpressionAction;
import org.talend.survivorship.action.ISurvivoredAction;
import org.talend.survivorship.action.LargestAction;
import org.talend.survivorship.action.LongestAction;
import org.talend.survivorship.action.MatchRegexAction;
import org.talend.survivorship.action.MostAncientAction;
import org.talend.survivorship.action.MostCommonAction;
import org.talend.survivorship.action.MostCompleteAction;
import org.talend.survivorship.action.MostRecentAction;
import org.talend.survivorship.action.ShortestAction;
import org.talend.survivorship.action.SmallestAction;
import org.talend.survivorship.action.SurvivedValuePassAction;

/**
 * represents definition of a user rule from the component.
 */
public class RuleDefinition {

    private Order order;

    private String ruleName;

    private String targetColumn;

    private Function function;

    private String referenceColumn;

    private String operation;

    private boolean ignoreBlanks;

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
        CR("CR");//conflict resolved  //$NON-NLS-1$

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

        Expression("Expression", new ExpressionAction()), // User defined rules. //$NON-NLS-1$

        MostCommon("MostCommon", new MostCommonAction()), //$NON-NLS-1$

        MostRecent("MostRecent", new MostRecentAction()), //$NON-NLS-1$

        MostAncient("MostAncient", new MostAncientAction()), //$NON-NLS-1$

        MostComplete("MostComplete", new MostCompleteAction()), //$NON-NLS-1$

        Longest("Longest", new LongestAction()), //$NON-NLS-1$

        Shortest("Shortest", new ShortestAction()), //$NON-NLS-1$

        Largest("Largest", new LargestAction()), //$NON-NLS-1$

        Smallest("Smallest", new SmallestAction()), //$NON-NLS-1$

        MatchRegex("MatchRegex", new MatchRegexAction()), //$NON-NLS-1$

        MappingTo("MappingTo", new SurvivedValuePassAction()), //$NON-NLS-1$

        Exclusiveness("Exclusiveness", new ExclusivenessAction()), //$NON-NLS-1$

        MatchIndex("MatchIndex", null); //$NON-NLS-1$

        private final String label;

        private final ISurvivoredAction action;

        Function(String label, ISurvivoredAction action) {
            this.label = label;
            this.action = action;
        }

        Function(String label) {
            this.label = label;
            this.action = null;
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
        public ISurvivoredAction getAction() {
            return this.action;
        }

        /**
         * Method "get".
         * 
         * @param label the label of the Function
         * @return the Function type given the label or null
         */
        public static Function get(String label) {

            if (label.equals(Expression.label)) {
                return Expression;
            } else if (label.equals(MostCommon.label)) {
                return MostCommon;
            } else if (label.equals(MostComplete.label)) {
                return MostComplete;
            } else if (label.equals(Longest.label)) {
                return Longest;
            } else if (label.equals(Shortest.label)) {
                return Shortest;
            } else if (label.equals(Largest.label)) {
                return Largest;
            } else if (label.equals(Smallest.label)) {
                return Smallest;
            } else if (label.equals(MostRecent.label)) {
                return MostRecent;
            } else if (label.equals(MostAncient.label)) {
                return MostAncient;
            } else if (label.equals(MatchRegex.label)) {
                return MatchRegex;
            } else if (label.equals(MatchIndex.label)) {
                return MatchIndex;
            } else {
                return null;
            }
        }
    }

    /**
     * Sets the referenceColumn.
     * 
     * @param referenceColumn the referenceColumn to set
     */
    public void setReferenceColumn(String referenceColumn) {
        this.referenceColumn = referenceColumn;
    }

}
