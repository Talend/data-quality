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

import org.talend.survivorship.action.ISurvivoredAction;

/**
 * Create by zshen parameter of survived function
 */
public class FunctionParameter {

    private ISurvivoredAction action;

    private String expression;

    private boolean isIgnoreBlank;

    private boolean isDealDup;

    public FunctionParameter(ISurvivoredAction action, String expression, boolean isIgnoreBlank, boolean isDealDup) {
        this.action = action;
        this.expression = expression;
        this.isIgnoreBlank = isIgnoreBlank;
        this.isDealDup = isDealDup;
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
     * Getter for isDealDup.
     * 
     * @return the isDealDup
     */
    public boolean isDealDup() {
        return this.isDealDup;
    }

}
