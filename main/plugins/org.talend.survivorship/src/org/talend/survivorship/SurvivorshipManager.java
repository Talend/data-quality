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
package org.talend.survivorship;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.talend.survivorship.model.Column;
import org.talend.survivorship.model.DataSet;
import org.talend.survivorship.model.RuleDefinition;
import org.talend.survivorship.model.RuleDefinition.Order;

/**
 * This class is provided for component runtime.
 * <p>
 * Usage:
 * <p>
 * 1. Instantiate <code>SurvivorshipManager</code>.
 * <p>
 * 2. Add column informations and rule definitions.
 * <p>
 * 3. Initialize <code>KnowlegeBase</code>.
 * <p>
 * 4. Run a new session for each group to merge.
 */
public class SurvivorshipManager extends KnowledgeManager {

    /**
     * Base of executable knowledge.
     */
    protected KnowledgeBase kbase;

    /**
     * collection of data and informations.
     */
    protected DataSet dataset;

    /**
     * SurvivorshipManager constructor.
     * 
     * @param columnMap
     * @param ruleDefinitions
     */
    public SurvivorshipManager(String rulePath, String packageName) {
        super(rulePath, packageName);
    }

    /**
     * Getter for kbase. Used only in junits.
     * 
     * @return the kbase
     */
    public KnowledgeBase getKnowledgeBase() {
        return kbase;
    }

    /**
     * Getter for dataSet. Used only in junits.
     * 
     * @return the dataSet
     */
    public DataSet getDataSet() {
        return dataset;
    }

    /**
     * initialize knowledge base
     */
    public void initKnowledgeBase() {

        int pos = rulePath.lastIndexOf('/');
        boolean isClassPathResource = false;
        if (pos > 0 && pos < rulePath.length() + 1) {
            String projectName = rulePath.substring(pos + 1);
            if (projectName != null) {
                String itemsFolder = "items/"; //$NON-NLS-1$
                File f = new File(itemsFolder);
                ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                URL resource = contextClassLoader.getResource(itemsFolder);
                if (f.exists()) { // running exported job
                    // since path is case sensitive in linux, call toLowerCase() here to correct the spelling.
                    rulePath = itemsFolder + projectName.toLowerCase();
                    f = new File(rulePath + "/metadata/survivorship/"); //$NON-NLS-1$
                    if (!f.exists()) {
                        System.err.println("[INFO] This error may appear if you did not export the dependencies of the job."); //$NON-NLS-1$
                    }
                } else if (resource != null) {// running exported OSGI bundle or on TIC.the 'items' folder is in jar file.
                    isClassPathResource = true;
                    rulePath = resource.getFile().concat(projectName.toLowerCase());
                    if (null == getClass().getResource(rulePath + "/metadata/survivorship/")) { //$NON-NLS-1$
                        System.err.println("[INFO] This error may appear if you did not export the dependencies of the job."); //$NON-NLS-1$
                    }
                } else {
                    itemsFolder = "jobox/work/" + getJobName() + "_" + getJobVersion() + "/" + getJobName() + "/items/"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    f = new File(itemsFolder);
                    if (f.exists()) {
                        // since path is case sensitive in linux, call toLowerCase() here to correct the spelling.
                        rulePath = itemsFolder + projectName.toLowerCase();
                        f = new File(rulePath + "/metadata/survivorship/"); //$NON-NLS-1$
                        if (!f.exists()) {
                            System.err.println("[INFO] This error may appear if you did not export the dependencies of the job."); //$NON-NLS-1$
                        }
                    } else { // running job in studio
                        // same reason for calling toUpperCase()
                        rulePath = rulePath.substring(0, pos + 1).concat(projectName.toUpperCase());
                    }
                }
            }
        }
        String packagePath = null;
        // TDQ-12588 for a real spark mode, the rule files are uploaded to spark node in javajet.
        if ("Real_spark_relative_path".equals(rulePath)) { //$NON-NLS-1$
            rulePath = ""; //$NON-NLS-1$
            packagePath = ""; //$NON-NLS-1$
        } else {
            packagePath = rulePath + "/metadata/survivorship/" + packageName + "/"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        // KnowledgeBuilderConfiguration config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        // config.setProperty("drools.dialect.mvel.strict", "false");
        // KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(config);

        // add package declaration including declarative model
        kbuilder.add(newResource(packagePath //
                + SurvivorshipConstants.DROOLS //
                + SurvivorshipConstants.VERSION_SUFFIX //
                + SurvivorshipConstants.PKG_ITEM_EXTENSION, isClassPathResource), ResourceType.DRL);

        // add rule definitions
        for (RuleDefinition definition : ruleDefinitionList) {
            if (definition.getOrder().equals(Order.SEQ)) {
                kbuilder.add(newResource(packagePath //
                        + definition.getRuleName() //
                        + SurvivorshipConstants.VERSION_SUFFIX //
                        + SurvivorshipConstants.RULE_ITEM_EXTENSION, isClassPathResource), ResourceType.DRL);
            }
        }

        // add survivorship work flow
        kbuilder.add(newResource(packagePath //
                + SurvivorshipConstants.SURVIVOR_FLOW //
                + SurvivorshipConstants.VERSION_SUFFIX //
                + SurvivorshipConstants.FLOW_ITEM_EXTENSION, isClassPathResource), ResourceType.BPMN2);

        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.err.println(error.getMessage());
            }
            throw new IllegalArgumentException("Could not parse knowledge."); //$NON-NLS-1$
        }
        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        dataset = new DataSet(columnList);
    }

    /**
     * Support to create resource from jar file
     */
    public static Resource newResource(String packagePath, boolean isClassPathResource) {
        if (isClassPathResource) {// the resource is in ClassPath,e.g, OSGI bundle or TIC
            return ResourceFactory.newClassPathResource(packagePath);
        } else {
            return ResourceFactory.newFileResource(packagePath);
        }
    }

    /**
     * Retrieve a column by name.
     * 
     * @param columnName
     * @return
     */
    public Column getColumnByName(String columnName) {
        for (Column col : columnList) {
            if (col.getName().equals(columnName)) {
                return col;
            }
        }
        return null;
    }

    /**
     * create and run a new session for a survivor group.
     * 
     * @param data A 2-dimension array containing input records.
     */
    public boolean runSession(Object[][] data) {

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        dataset.reset();
        dataset.initData(data);
        ksession.setGlobal("dataset", dataset); //$NON-NLS-1$

        // go !
        try {
            FactType recordInType = kbase.getFactType(packageName, SurvivorshipConstants.RECORD_IN);
            for (int i = data.length - 1; i >= 0; i--) {
                Object input = recordInType.newInstance();
                recordInType.set(input, SurvivorshipConstants.TALEND_INTERNAL_ID, i);
                for (int j = 0; j < columnList.size(); j++) {
                    Column column = columnList.get(j);
                    recordInType.set(input, column.getName().toLowerCase(), data[i][j]);
                }
                ksession.insert(input);
            }
        } catch (InstantiationException e) {
            // failed to create new recordInType instance
            System.err.println("!!! " + e.getMessage()); //$NON-NLS-1$
            return false;
        } catch (IllegalAccessException e) {
            // failed to create new recordInType instance
            System.err.println("!!! " + e.getMessage()); //$NON-NLS-1$
            return false;
        }

        ksession.startProcess(packageName + "." + SurvivorshipConstants.SURVIVOR_FLOW); //$NON-NLS-1$
        ksession.fireAllRules();
        ksession.dispose();
        // kbase.getStatefulKnowledgeSessions().clear();

        dataset.finalizeComputation();
        return true;
    }

    /**
     * get survivor map.
     * 
     * @return
     */
    public Map<String, Object> getSurvivorMap() {
        return dataset.getSurvivorMap();
    }

    /**
     * get conflict list.
     * 
     * @return
     */
    public List<HashSet<String>> getConflictList() {
        return dataset.getConflictList();
    }

    /**
     * get CONFLICTS of survivor.
     * 
     * @return
     */
    public HashSet<String> getConflictsOfSurvivor() {
        return dataset.getConflictsOfSurvivor();
    }
}
