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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.survivorship.action.handler.AbstractChainOfResponsibilityHandler;
import org.talend.survivorship.action.handler.FunctionParameter;
import org.talend.survivorship.action.handler.HandlerParameter;
import org.talend.survivorship.action.handler.MCCRHandler;
import org.talend.survivorship.action.handler.MTCRHandler;
import org.talend.survivorship.model.Column;
import org.talend.survivorship.model.ConflictRuleDefinition;
import org.talend.survivorship.model.DataSet;
import org.talend.survivorship.model.RuleDefinition;
import org.talend.survivorship.model.RuleDefinition.Function;
import org.talend.survivorship.model.RuleDefinition.Order;
import org.talend.survivorship.utils.ChainNodeMap;

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

    private static final Logger LOGGER = LoggerFactory.getLogger("SurvivorshipManager");

    /**
     * Base of executable knowledge.
     */
    protected KnowledgeBase kbase;

    /**
     * collection of data and informations.
     */
    protected DataSet dataset;

    protected List<AbstractChainOfResponsibilityHandler> chainList;

    private ChainNodeMap chainMap;

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
        String packagePath;
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
     * 
     * Add InputStream as resource.
     * 
     * @param resourceStreamsMap key is ResourceType, value is a List with File input stream
     */
    public void initKnowledgeBase(Map<org.kie.api.io.ResourceType, List<InputStream>> streamsMap) {
        if (streamsMap.isEmpty()) {
            System.err.println("The resouce of DRL streams is Empty!"); //$NON-NLS-1$
            return;
        }
        List<InputStream> ruleFiles = streamsMap.get(ResourceType.DRL);
        List<InputStream> workFlowFiles = streamsMap.get(ResourceType.BPMN2);

        if (ruleFiles.isEmpty()) {
            System.err.println("The resouce of DRL streams is Empty!"); //$NON-NLS-1$
            return;
        }

        if (workFlowFiles.isEmpty()) {
            System.err.println("The resouce of BPMN2 streams is Empty!"); //$NON-NLS-1$
            return;
        }
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        for (InputStream stream : ruleFiles) {
            if (stream != null) {
                kbuilder.add(newResource(stream), ResourceType.DRL);
            }
        }
        for (InputStream stream : workFlowFiles) {
            if (stream != null) {
                kbuilder.add(newResource(stream), ResourceType.BPMN2);
            }
        }

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
     * 
     * DOC talend Comment method "newResource".
     * 
     * @param stream
     * @return
     */
    public static Resource newResource(InputStream stream) {
        return ResourceFactory.newInputStreamResource(stream);
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
    public boolean runSessionWithJava(Object[][] data) {

        dataset.reset();
        dataset.initData(data);
        initChainResponsibilityHandler();
        executeSurvivored(data);
        dataset.finalizeComputation();
        return true;
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
            LOGGER.error(e.getMessage(), e);
            System.err.println("!!! " + e.getMessage()); //$NON-NLS-1$
            return false;
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
            // failed to create new recordInType instance
            System.err.println("!!! " + e.getMessage()); //$NON-NLS-1$
            return false;
        }

        ksession.startProcess(packageName + "." + SurvivorshipConstants.SURVIVOR_FLOW); //$NON-NLS-1$
        ksession.fireAllRules();
        ksession.dispose();
        kbase.getStatefulKnowledgeSessions().clear();

        dataset.finalizeComputation();
        return true;
    }

    /**
     * 
     * create by zshen check whether conflict rule is valid
     * 
     * @return invalid column map key is column name value is error message
     */
    public Map<String, List<String>> checkConflictRuleValid() {
        Map<String, List<String>> returnMap = new HashMap<>();
        List<String> ruleColumnList = new ArrayList<>();
        List<Column> executeOrder = new ArrayList<>();
        Map<String, List<String>> cycDepenStatus = new HashMap<>();
        for (RuleDefinition ruleDef : this.getRuleDefinitionList()) {
            ruleColumnList.add(ruleDef.getTargetColumn());
        }
        for (Column col : this.getColumnList()) {
            String conflictTargetColumnName = col.getName();
            for (ConflictRuleDefinition conRuleDef : col.getConflictResolveList()) {

                if (!ruleColumnList.contains(conflictTargetColumnName)) {
                    List<String> messageList = returnMap.get(conflictTargetColumnName);
                    if (messageList == null) {
                        messageList = new ArrayList<>();
                        returnMap.put(conflictTargetColumnName, messageList);
                    }
                    String errorMessage = conflictTargetColumnName + " does not contain any survived value"; //$NON-NLS-1$
                    if (!messageList.contains(errorMessage)) {
                        messageList.add(errorMessage);
                    }
                }
                if (Function.SurviveAs == conRuleDef.getFunction()) {
                    String conflictRefColumnName = conRuleDef.getReferenceColumn();
                    if (!ruleColumnList.contains(conflictRefColumnName)) {
                        List<String> messageList = returnMap.get(conflictRefColumnName);
                        if (messageList == null) {
                            messageList = new ArrayList<>();
                            returnMap.put(conflictRefColumnName, messageList);
                        }
                        String errorMessage = conflictRefColumnName + " does not contain any survived value"; //$NON-NLS-1$
                        if (!messageList.contains(errorMessage)) {
                            messageList.add(errorMessage);
                        }
                    }
                    if (cycDepenStatus.get(conflictTargetColumnName) == null) {
                        cycDepenStatus.put(conflictTargetColumnName, new ArrayList<String>());
                    }
                    boolean checkCycDependency = checkCycDependency(conflictTargetColumnName, conflictRefColumnName, executeOrder,
                            cycDepenStatus);
                    if (!checkCycDependency) {
                        List<String> messageList = returnMap.get(conflictRefColumnName);
                        if (messageList == null) {
                            messageList = new ArrayList<>();
                            returnMap.put(conflictRefColumnName, messageList);
                        }
                        String errorMessage = conflictTargetColumnName + " cannot be survived as " + conflictRefColumnName //$NON-NLS-1$
                                + " because of circular dependency"; //$NON-NLS-1$
                        if (!messageList.contains(errorMessage)) {
                            messageList.add(errorMessage);
                        }
                    }
                    break;
                }
            }
        }
        if (returnMap.size() == 0) {
            this.getDataSet().setColumnOrder(fillOtherColumn(executeOrder));
        }
        return returnMap;
    }

    /**
     * Create by zshen fill other no order column
     * 
     * @param executeOrder The column list which take order
     * @return full column list
     */
    private List<Column> fillOtherColumn(List<Column> executeOrder) {
        for (Column col : this.getColumnList()) {
            if (!executeOrder.contains(col)) {
                executeOrder.add(col);
            }
        }
        return executeOrder;
    }

    /**
     * Create by zshen check whether exist Circular dependency
     * 
     * @param source of column name
     */
    private boolean checkCycDependency(String sourceColumnName, String targetColumnName, List<Column> executeOrder,
            Map<String, List<String>> cycDepenStatus) {
        Column dependencyColumn = this.getColumnByName(targetColumnName);
        List<String> dependencyList = cycDepenStatus.get(sourceColumnName);
        if (!dependencyList.contains(targetColumnName)) {
            dependencyList.add(targetColumnName);
        }
        boolean checkStatusMap = checkStatusMap(cycDepenStatus, sourceColumnName, targetColumnName);
        if (!checkStatusMap) {
            return false;
        }
        for (ConflictRuleDefinition conRuleDef : dependencyColumn.getConflictResolveList()) {
            if (Function.SurviveAs == conRuleDef.getFunction()) {
                if (cycDepenStatus.get(targetColumnName) == null) {
                    cycDepenStatus.put(targetColumnName, new ArrayList<String>());
                }
                if (!checkCycDependency(targetColumnName, conRuleDef.getReferenceColumn(), executeOrder, cycDepenStatus)) {
                    return false;
                }
            }
        }
        if (!executeOrder.contains(dependencyColumn)) {

            executeOrder.add(dependencyColumn);
        }
        return true;
    }

    /**
     * Create by zshen check dependency status
     * 
     * @param cycDepenStatus
     * @param sourceColumnName
     * @param targetColumnName
     */
    private boolean checkStatusMap(Map<String, List<String>> cycDepenStatus, String sourceColumnName, String targetColumnName) {
        List<String> list = cycDepenStatus.get(targetColumnName);
        if (list == null) {
            return true;
        }
        for (String depCol : list) {
            if (depCol.equals(sourceColumnName)) {
                return false;
            }
            if (!checkStatusMap(cycDepenStatus, sourceColumnName, depCol)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create by zshen init the handler of chain Responsibility
     */
    private void initChainResponsibilityHandler() {
        String currentRuleColumn = null;

        AbstractChainOfResponsibilityHandler currentMCHandler = null;
        AbstractChainOfResponsibilityHandler firstTargetNode = null;
        AbstractChainOfResponsibilityHandler lastTargetNode = null;
        initChainMap();
        initChainList();
        RuleDefinition perviousSEQRd = null;
        for (RuleDefinition rd : this.getRuleDefinitionList()) {
            Order order = rd.getOrder();
            if (order == Order.SEQ) {
                // next SEQ case inseart first MTCR node into queue
                if (currentMCHandler != null && firstTargetNode != null) {
                    currentMCHandler.linkSuccessor(firstTargetNode);
                    firstTargetNode = null;
                    lastTargetNode = null;
                }
                currentRuleColumn = rd.getRuleName();
                perviousSEQRd = rd;
            } else if (order == Order.MT && lastTargetNode != null) {
                lastTargetNode = lastTargetNode.linkSuccessor(createMTHandler(perviousSEQRd, rd));
                continue;
            }

            currentMCHandler = chainMap.get(currentRuleColumn);
            MCCRHandler newMCHandler = createMCHandler(rd);
            // SEQ case only
            if (currentMCHandler == null) {
                currentMCHandler = newMCHandler;
                firstTargetNode = new MTCRHandler(newMCHandler);
                lastTargetNode = firstTargetNode;
                chainMap.put(currentRuleColumn, newMCHandler);
                chainList.add(newMCHandler);
            } else {
                // MC case only
                currentMCHandler = currentMCHandler.linkSuccessor(newMCHandler);
            }

        }
        if (currentMCHandler != null && firstTargetNode != null) {
            currentMCHandler.linkSuccessor(firstTargetNode);
            firstTargetNode = null;
            lastTargetNode = null;
        }
    }

    /**
     * Create by zshen chain responsibility list
     */
    private void initChainList() {

        if (chainList == null) {
            chainList = new ArrayList<>();
        } else {
            chainList.clear();
        }
    }

    /**
     * Create by zshen Create new mutli-condiation handler
     * 
     * @param rd The rule definition of handler
     * @return new mutli-condiation handler
     */
    private MCCRHandler createMCHandler(RuleDefinition rd) {
        FunctionParameter functionParameter = new FunctionParameter(rd.getFunction().getAction(), rd.getOperation(),
                rd.isIgnoreBlanks(), false);
        HandlerParameter handlerParameter = new HandlerParameter(dataset, getColumnByName(rd.getReferenceColumn()),
                getColumnByName(rd.getTargetColumn()), rd.getRuleName(), getColumnIndexMap(), null, functionParameter);
        return new MCCRHandler(handlerParameter);
    }

    /**
     * Create by zshen Create new mutli-target handler
     * 
     * @param rd The rule definition of handler
     * @return new mutli-target handler
     */
    private MTCRHandler createMTHandler(RuleDefinition perviousSEQRd, RuleDefinition rd) {
        FunctionParameter functionParameter = new FunctionParameter(perviousSEQRd.getFunction().getAction(), rd.getOperation(),
                rd.isIgnoreBlanks(), false);
        return new MTCRHandler(new HandlerParameter(dataset, getColumnByName(perviousSEQRd.getReferenceColumn()),
                getColumnByName(rd.getTargetColumn()), perviousSEQRd.getRuleName(), getColumnIndexMap(), null,
                functionParameter));
    }

    /**
     * Create by zshen init chain map
     */
    private void initChainMap() {
        if (chainMap == null) {
            chainMap = new ChainNodeMap();
        } else {
            chainMap.clear();
        }

    }

    /**
     * Create by zshen Make chainMap execute handler of every column one by one
     */
    private void executeSurvivored(Object[][] data) {
        for (int i = data.length - 1; i >= 0; i--) {
            chainMap.handleRequest(data[i], i);
        }
    }

    /**
     * Create by zshen Get a map which mapping the name of column and it's index
     * 
     * @return a map which mapping the name of column and it's index
     */
    private Map<String, Integer> getColumnIndexMap() {
        Map<String, Integer> columnIndexMap = new HashMap<>();
        int index = 0;
        for (Column col : this.columnList) {
            columnIndexMap.put(col.getName(), index++);
        }
        return columnIndexMap;
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
