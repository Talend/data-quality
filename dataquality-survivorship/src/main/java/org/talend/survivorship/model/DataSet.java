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

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.talend.survivorship.SurvivorshipManager;
import org.talend.survivorship.action.ISurvivoredAction;
import org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler;
import org.talend.survivorship.action.handler.CRCRHandler;
import org.talend.survivorship.action.handler.HandlerParameter;
import org.talend.survivorship.services.CompletenessService;
import org.talend.survivorship.services.FrequencyService;
import org.talend.survivorship.services.NumberService;
import org.talend.survivorship.services.StringService;
import org.talend.survivorship.services.TimeService;
import org.talend.survivorship.utils.ChainNodeMap;

/**
 * Collection of a group of data. This class will be instantiated and insert into the rule engine as global variable.
 */
public class DataSet {

    private List<Record> recordList;

    private List<Column> columnList;

    private ChainNodeMap chainMap;

    private HashMap<String, Object> survivorMap;

    private List<HashSet<String>> conflictList;

    private HashSet<String> conflictsOfSurvivor;

    private FrequencyService fs;

    private StringService ss;

    private TimeService ts;

    private CompletenessService cs;

    private NumberService ns;

    private SoftReference<SurvivorshipManager> survivorManager;

    /**
     * DataSet constructor.
     * 
     * @param columns
     * @param input
     */
    public DataSet(List<Column> columns) {
        columnList = columns;
        recordList = new ArrayList<Record>();
        survivorMap = new HashMap<String, Object>();
        conflictList = new ArrayList<HashSet<String>>();
        conflictsOfSurvivor = new HashSet<String>();
        chainMap = new ChainNodeMap();

        initServices();
    }

    public void initData(Object[][] input) {

        for (int j = 0; j < columnList.size(); j++) {
            columnList.get(j).init();
        }
        for (int i = 0; i < input.length; i++) {
            conflictList.add(new HashSet<String>());
            Record rec = new Record();
            rec.setId(i);
            Attribute attribute;
            for (int j = 0; j < columnList.size(); j++) {
                Column col = columnList.get(j);
                attribute = new Attribute(rec, col, input[i][j]);
                rec.putAttribute(col.getName(), attribute);
                col.putAttribute(rec, attribute);
            }
            recordList.add(rec);
        }
    }

    private void initServices() {
        // TODO use the same services instead of creating new instance for each data group.
        fs = new FrequencyService(this);
        ss = new StringService(this);
        ts = new TimeService(this);
        cs = new CompletenessService(this);
        ns = new NumberService(this);
    }

    public void reset() {
        recordList.clear();
        survivorMap.clear();
        conflictList.clear();
        conflictsOfSurvivor.clear();
        fs.init();
        ss.init();
        ts.init();
        cs.init();
        ns.init();
        for (Column col : columnList) {
            col.init();
        }
    }

    /**
     * Retrieve all attributes of a column.
     * 
     * @param columnName
     * @return
     */
    public Collection<Attribute> getAttributesByColumn(String columnName) {
        for (Column col : columnList) {
            if (col.getName().equals(columnName)) {
                return col.getAttributes();
            }
        }
        return null;
    }

    /**
     * Gets the columnList.
     * 
     * @return
     */
    public List<Column> getColumnList() {
        return columnList;
    }

    /**
     * Survive an attribute by record number and column name.
     * 
     * @param recNum
     * @param colName
     * @deprecated this method is kept for backward compatibility of existing rules
     */
    @Deprecated
    public void survive(int recNum, String colName) {
        Record record = recordList.get(recNum);
        Attribute attribute = record.getAttribute(colName);
        if (attribute.isAlive()) {
            attribute.setSurvived(true);
        }
    }

    /**
     * Survive an attribute by record number and column name.
     * 
     * @param recNum
     * @param colName
     * @param ruleName
     */
    public void survive(int recNum, String colName, String ruleName) {
        Record record = recordList.get(recNum);
        Attribute attribute = record.getAttribute(colName);
        if (attribute.isAlive()) {
            Column column = attribute.getColumn();
            // TDQ-12742 when there are 2 or more rules on a column, one rule can work only if the previous one does
            // not producer any survivor
            if (column.getSurvivingRuleName() == null || ruleName.equals(column.getSurvivingRuleName())) {

                if (column.getSurvivingRuleName() != null) {
                    // conflict generated
                    CRCRHandler crcrHandler = (CRCRHandler) this.chainMap.get(column.getName());
                    // If we don't do that maybe we can store conflict data number form here
                    if (crcrHandler == null) {
                        for (RuleDefinition ruleDef : column.getConflictResolveList()) {
                            ISurvivoredAction action = ruleDef.getFunction().getAction();
                            Column refColumn = record.getAttribute(ruleDef.getReferenceColumn()).getColumn();
                            Column tarColumn = column;
                            String expression = ruleDef.getOperation();
                            boolean isIgnoreBlank = ruleDef.isIgnoreBlanks();
                            CRCRHandler newCrcrHandler = new CRCRHandler(new HandlerParameter(this, action, refColumn, tarColumn,
                                    ruleName, expression, isIgnoreBlank, this.getColumnIndexMap()));
                            if (crcrHandler == null) {
                                this.chainMap.put(column.getName(), newCrcrHandler);
                            }
                            crcrHandler = crcrHandler == null ? newCrcrHandler
                                    : (CRCRHandler) crcrHandler.linkSuccessor(newCrcrHandler);
                        }
                    }
                }

                // modify this attribute after conflict resolve
                attribute.setSurvived(true);
                column.setSurvivingRuleName(ruleName);
            }
        }
    }

    /**
     * DOC zshen Comment method "getColumnIndexMap".
     * 
     * @return
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
     * Survive an attribute if another attribute is still alive.
     * 
     * @param recNum
     * @param col
     * @param aliveField
     */
    public void surviveByAliveField(int recNum, String col, String aliveField) {
        Record record = recordList.get(recNum);
        Attribute attribute = record.getAttribute(col);
        Attribute another = record.getAttribute(aliveField);
        if (attribute.isAlive() && another.isAlive()) {
            attribute.setSurvived(true);
        }
    }

    /**
     * Eliminate an attribute.
     * 
     * @param recNum
     * @param col
     */
    public void eliminate(int recNum, String col) {
        Record record = recordList.get(recNum);
        Attribute attribute = record.getAttribute(col);
        if (attribute.isAlive()) {
            attribute.setAlive(false);
        }
    }

    /**
     * Compute all the attributes to see if they are alive.
     */
    public void finalizeComputation() {
        // this variable should be same with record.getID()
        int rowNumber = 0;
        for (Record record : recordList) {
            for (Column col : columnList) {
                AbstractChainResponsibilityHandler crcrHandler = this.chainMap.get(col.getName());
                if (crcrHandler != null) {
                    crcrHandler.handleRequest(record, rowNumber);
                }
                Attribute a = record.getAttribute(col.getName());
                if (a.isSurvived()) {
                    conflictRecord(col, a);
                }
            }
            rowNumber++;
        }
        for (Record record : recordList) {
            HashSet<String> hashSet = conflictList.get(record.getId());
        }

        Iterator<String> iterator = conflictsOfSurvivor.iterator();
        while (iterator.hasNext()) {
            String conflictCol = iterator.next();
            CRCRHandler crcrHandler = (CRCRHandler) this.chainMap.get(conflictCol);
            if (crcrHandler != null) {
                int survivoredRowNum = crcrHandler.getSurvivoredRowNum();
                Attribute attribute = recordList.get(survivoredRowNum).getAttribute(conflictCol);
                survivorMap.put(conflictCol, attribute.getValue());
            }
        }

    }

    /**
     * DOC zshen Comment method "conflictResolve".
     * 
     * @param col
     * @param a
     */
    private void conflictRecord(Column col, Attribute a) {
        // default case get first one
        if (survivorMap.get(col.getName()) == null) {
            survivorMap.put(col.getName(), a.getValue());
        } else {

            //

            Object survivor = survivorMap.get(col.getName());
            if (a.getValue() != null && !a.getValue().equals(survivor)) {
                HashSet<String> desc = conflictList.get(a.getRecordID());
                desc.add(col.getName());
                conflictsOfSurvivor.add(col.getName());
            }
        }
    }

    /**
     * determine if a value is the most common one of a given column. Used only in rules.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isMostCommon(Object var, String column, boolean ignoreBlanks) {
        if (var == null) {
            return false;
        }
        if (fs.getMostCommonValue(column, ignoreBlanks).contains(var)) {
            return true;
        }
        return false;
    }

    /**
     * determine if a record is the most complete. Used only in rules.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isMostComplete(int recNum) {
        if (cs.getMostCompleteRecNumList().contains(recNum)) {
            return true;
        }
        return false;
    }

    /**
     * determine if a value is the longest one of a given column. Used only in rules.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isLongest(Object var, String column, boolean ignoreBlanks) {
        if (var == null) {
            return false;
        }
        return ss.isLongestValue(var, column, ignoreBlanks);
    }

    /**
     * determine if a value is the shortest one of a given column. Used only in rules.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isShortest(Object var, String column, boolean ignoreBlanks) {
        if (var == null) {
            return false;
        }
        return ss.isShortestValue(var, column, ignoreBlanks);
    }

    /**
     * determine if a value is the latest one of a given column. Used only in rules.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isLatest(Object var, String column) {
        if (var == null) {
            return false;
        }
        return ts.isLatestValue(var, column);
    }

    /**
     * determine if a value is the earliest one of a given column. Used only in rules.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isEarliest(Object var, String column) {
        if (var == null) {
            return false;
        }
        return ts.isEarliestValue(var, column);
    }

    /**
     * determine if a value is the largest one of a given column. Used only in rules.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isLargest(Object var, String column) {
        if (var == null) {
            return false;
        }
        return ns.isLargestValue(var, column);
    }

    /**
     * determine if a value is the smallest one of a given column. Used only in rules.
     * 
     * @param var
     * @param column
     * @return
     */
    public boolean isSmallest(Object var, String column) {
        if (var == null) {
            return false;
        }
        return ns.isSmallestValue(var, column);
    }

    /**
     * Getter for survivorMap.
     * 
     * @return the survivorMap
     */
    public HashMap<String, Object> getSurvivorMap() {
        return survivorMap;
    }

    /**
     * Getter for conflictList.
     * 
     * @return the conflictList
     */
    public List<HashSet<String>> getConflictList() {
        return conflictList;
    }

    /**
     * Getter for conflictsOfSurvivor.
     * 
     * @return the conflictsOfSurvivor
     */
    public HashSet<String> getConflictsOfSurvivor() {
        return conflictsOfSurvivor;
    }

    /**
     * Getter for recordList
     * 
     * @return
     */
    public List<Record> getRecordList() {
        return recordList;
    }

    /**
     * Sets the survivorManager.
     * 
     * @param survivorManager the survivorManager to set
     */
    public void setSurvivorManager(SoftReference<SurvivorshipManager> survivorManager) {
        this.survivorManager = survivorManager;
    }

}
