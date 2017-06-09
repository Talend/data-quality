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

import org.talend.survivorship.action.ISurvivorshipAction;
import org.talend.survivorship.model.RuleDefinition.Function;

/**
 * Create by zshen parameter of survived function
 */
public class FunctionParameter {

    private ISurvivorshipAction action;

    private String expression;

    private boolean isIgnoreBlank;

    private boolean isRemoveDuplicate;

    private Function function;

    public FunctionParameter(ISurvivorshipAction action, String expression, boolean isIgnoreBlank, boolean isRemoveDuplicate) {
        this.action = action;
        this.function = Function.getFunction(action);
        this.expression = expression;
        this.isIgnoreBlank = isIgnoreBlank;
        this.isRemoveDuplicate = isRemoveDuplicate;
    }

    public FunctionParameter(Function function, String expression, boolean isIgnoreBlank, boolean isRemoveDuplicate) {
        this(function.getAction(), expression, isIgnoreBlank, isRemoveDuplicate);
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
     * Getter for expression.
     * 
     * @return the expression
     */
    public String getExpression() {
        return this.expression;
    }

    /**
     * Getter for isIgnoreBlank.
     * 
     * @return the isIgnoreBlank
     */
    public boolean isIgnoreBlank() {
        return this.isIgnoreBlank;
    }

    /**
     * Getter for isRemoveDuplicate.
     * 
     * @return the isRemoveDuplicate
     */
    public boolean isRemoveDuplicate() {
        return this.isRemoveDuplicate;
    }

    /**
     * Getter for function.
     * 
     * @return the function
     */
    protected Function getFunction() {
        return this.function;
    }

}
