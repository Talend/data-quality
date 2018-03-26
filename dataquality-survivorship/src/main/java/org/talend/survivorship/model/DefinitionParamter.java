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

import org.talend.survivorship.model.RuleDefinition.Order;

/**
 * The parameter of Rule Definition
 */
public class DefinitionParamter {

    private Order order;

    private String ruleName;

    private boolean ignoreBlanks;

    private boolean duplicateSurCheck;

    /**
     * The constructor of DefinitionParamter.
     * 
     * @param order
     * @param ruleName
     * @param ignoreBlanks
     * @param duplicateSurCheck
     */
    public DefinitionParamter(Order order, String ruleName, boolean ignoreBlanks, boolean duplicateSurCheck) {
        super();
        this.order = order;
        this.ruleName = ruleName;
        this.ignoreBlanks = ignoreBlanks;
        this.duplicateSurCheck = duplicateSurCheck;
    }

}
