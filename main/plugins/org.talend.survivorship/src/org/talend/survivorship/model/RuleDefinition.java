// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
        SEQ("SEQ"), // Sequential
        MC("MC"), // Multi-condition
        MT("MT");// Multi-target

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

        Expression("Expression"), // User defined rules.

        MostCommon("MostCommon"),

        MostRecent("MostRecent"),

        MostAncient("MostAncient"),

        MostComplete("MostComplete"),

        Longest("Longest"),

        Shortest("Shortest"),

        Largest("Largest"),

        Smallest("Smallest"),

        MatchRegex("MatchRegex"),

        MatchIndex("MatchIndex");

        private final String label;

        Function(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return this.label;
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
