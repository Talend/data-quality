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
package org.talend.survivorship;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.talend.survivorship.model.Column;
import org.talend.survivorship.model.RuleDefinition;

/**
 * This class contains basic configurations. It is the super class of <code>SurvivorshipManager</code> and
 * <code>RuleGenerationManager</code>.
 */
public abstract class KnowledgeManager extends Observable {

    protected String rulePath;

    /**
     * Package name for rule and flow generation.
     */
    protected String packageName;

    /**
     * List of input columns.
     */
    protected List<Column> columnList;

    /**
     * Getter for columnList.
     * 
     * @return the columnList
     */
    public List<Column> getColumnList() {
        return columnList;
    }

    /**
     * List of rule definitions.
     */
    protected List<RuleDefinition> ruleDefinitionList;

    /**
     * Getter for rulePath.
     * 
     * @return the rulePath
     */
    public String getRulePath() {
        return rulePath;
    }

    /**
     * KnowledgeManager constructor.
     */
    protected KnowledgeManager(String rulePath, String packageName) {
        this.rulePath = rulePath;
        this.packageName = packageName;

        // MOD 21-10-2011 to fix TDQ-3986
        System.setProperty("mvel2.disable.jit", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        // MOD 22-10-2012 to fix TDQ-4649
        System.setProperty("drools.dialect.java.compiler.lnglevel", "1.6"); //$NON-NLS-1$ //$NON-NLS-2$

        columnList = new ArrayList<Column>();
        ruleDefinitionList = new ArrayList<RuleDefinition>();
    }

    /**
     * Getter for packageName.
     * 
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Getter for ruleDefinitionList.
     * 
     * @return the ruleDefinitionList
     */
    public List<RuleDefinition> getRuleDefinitionList() {
        return ruleDefinitionList;
    }

    /**
     * Add a rule definition to ruleDefinitionList.
     * 
     * @param ruleDefinition
     */
    public void addRuleDefinition(RuleDefinition ruleDefinition) {
        ruleDefinitionList.add(ruleDefinition);
    }

    /**
     * Add a rule definition to columnList.
     * 
     * @param name
     * @param type
     */
    public void addColumn(String name, String type) {
        columnList.add(new Column(name, type));
    }
}
