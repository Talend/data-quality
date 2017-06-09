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
import java.util.List;
import java.util.Map;

import org.talend.survivorship.action.ISurvivoredAction;
import org.talend.survivorship.action.handler.CRCRHandler;
import org.talend.survivorship.action.handler.FunctionParameter;
import org.talend.survivorship.action.handler.HandlerFactory;
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

    protected HashMap<String, Integer> survivorIndexMap;

    private List<HashSet<String>> conflictList;

    private List<Column> columnOrder;

    private HashSet<String> conflictsOfSurvivor;

    private FrequencyService fs;

    private StringService ss;

    private TimeService ts;

    private CompletenessService cs;

    private NumberService ns;

    private SoftReference<HashMap<String, List<Integer>>> conflictDataMap = new SoftReference<>(
            new HashMap<String, List<Integer>>());

    /**
     * DataSet constructor.
     * 
     * @param columns
     * @param input
     */
    public DataSet(List<Column> columns) {
        columnList = columns;
        recordList = new ArrayList<>();
        survivorMap = new HashMap<>();
        conflictList = new ArrayList<>();
        conflictsOfSurvivor = new HashSet<>();
        chainMap = new ChainNodeMap();
        survivorIndexMap = new HashMap<>();

        initServices();
    }

    /**
     * DataSet constructor.
     * 
     * @param columns
     * @param input
     */
    protected DataSet(List<Column> columns, List<Record> recordList) {
        this(columns);
        this.recordList = recordList;
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
        // only data is keep use same one
        fs = new FrequencyService(this);
        ss = new StringService(this);
        ts = new TimeService(this);
        cs = new CompletenessService(this);
        ns = new NumberService(this);
    }

    public void reset() {
        recordList.clear();
        survivorMap.clear();
        survivorIndexMap.clear();
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
                String columnName = column.getName();

                // conflict generated
                CRCRHandler crcrHandler = (CRCRHandler) this.chainMap.get(columnName);
                // If we don't do that maybe we can store conflict data number form here
                if (crcrHandler == null) {
                    for (ConflictRuleDefinition ruleDef : column.getConflictResolveList()) {
                        //If current rule has been disable then continue next one
                        boolean disableRule = ruleDef.isDisableRule();
                        if (disableRule) {
                            continue;
                        }
                        ISurvivoredAction action = ruleDef.getFunction().getAction();
                        Column refColumn = record.getAttribute(ruleDef.getReferenceColumn()).getColumn();
                        Column tarColumn = column;
                        String expression = ruleDef.getOperation();
                        String cRuleName = ruleDef.getRuleName();
                        boolean isIgnoreBlank = ruleDef.isIgnoreBlanks();
                        String fillColumn = ruleDef.getFillColumn();
                        boolean isDealDup = ruleDef.isDuplicateSurCheck();
                        FunctionParameter functionParameter = new FunctionParameter(action, expression, isIgnoreBlank, isDealDup);
                        CRCRHandler newCrcrHandler = HandlerFactory.getInstance().createCRCRHandler(new HandlerParameter(this,
                                refColumn, tarColumn, cRuleName, this.getColumnIndexMap(), fillColumn, functionParameter));
                        if (crcrHandler == null) {
                            this.chainMap.put(columnName, newCrcrHandler);
                        }
                        crcrHandler = crcrHandler == null ? newCrcrHandler
                                : (CRCRHandler) crcrHandler.linkSuccessor(newCrcrHandler);
                    }
                }
                // store conflict data
                if (crcrHandler != null) {
                    this.addConfDataIndex(columnName, recNum);
                }

                // modify this attribute after conflict resolve
                attribute.setSurvived(true);
                column.setSurvivingRuleName(ruleName);
            }
        }
    }

    /**
     * Create by zshen Get a mapping between column name and column index
     * 
     * @return a mapping map between column name and column index
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
        for (Record record : recordList) {
            for (Column col : getColumnOrder() == null ? this.getColumnList() : getColumnOrder()) {
                Attribute a = record.getAttribute(col.getName());
                if (a.isSurvived()) {
                    // defause case first one
                    conflictRecord(col, a);
                }
            }
        }
        if (getColumnOrder() == null) {
            return;
        }
        for (Column nextCol : getColumnOrder()) {
            String nextColName = nextCol.getName();
            if (conflictsOfSurvivor.contains(nextColName)) {
                String conflictCol = nextColName;
                CRCRHandler crcrHandler = (CRCRHandler) this.chainMap.get(conflictCol);
                List<Integer> conflictDataIndexList = this.getConflictDataIndexList(conflictCol);
                if (crcrHandler != null && conflictDataIndexList != null) {
                    crcrHandler.handleRequest();
                }
                if (crcrHandler != null) {
                    SurvivedResult survivoredRowNum = crcrHandler.getSurvivoredRowNum();
                    if (survivoredRowNum != null) {
                        Object survivedVlaue = this.getValueAfterFiled(survivoredRowNum.getRowNum(),
                                survivoredRowNum.getColumnName());
                        conflictsOfSurvivor.remove(conflictCol);
                        survivorMap.put(conflictCol, survivedVlaue);
                        survivorIndexMap.put(conflictCol, survivoredRowNum.getRowNum());
                    }
                }
            }
        }
    }

    /**
     * Create by zshen check whether new value has been exist in the survivorMap.
     * 
     * @param value The new value
     * @return true when exist duplicate else false
     */
    public boolean checkDupSurValue(Object value, String colName) {
        Object refSurvive = survivorMap.get(colName);
        if (value.equals(refSurvive)) {
            return true;
        }
        return false;
    }

    /**
     * Create by zshen record conflict resolved result
     * 
     * @param col The column current result is come from
     * @param a the value which should be record by survived value
     */
    private void conflictRecord(Column col, Attribute a) {
        // default case get first one
        if (survivorMap.get(col.getName()) == null) {
            survivorMap.put(col.getName(), a.getValue());
            survivorIndexMap.put(col.getName(), a.getRecordID());
        } else {
            survivorIndexMap.remove(col.getName());
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
     * @param var the value which need to be check
     * @param column the column which var belong
     * @return true when var is the most common else false
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
     * @param var The input data
     * @param column The column which input data belong
     * @return true if it is esle false
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
     * @param var The value which need to be check
     * @param column The column which input data belong
     * @return True if it is esle false
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
     * @param var The value which need to be check
     * @param column The column which input data belong
     * @return True if it is esle false
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
     * @param var The value which need to be check
     * @param column The column which input data belong
     * @return true If it is esle false
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
     * @param var The value which need to be check
     * @param column The column which input data belong
     * @return true If it is esle false
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
     * @param var The value which need to be check
     * @param column The column which input data belong
     * @return true If it is esle false
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
     * @param var The value which need to be check
     * @param column The column which input data belong
     * @return true If it is esle false
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
     * 
     * Get the value which special rowNum and colName
     * 
     * @param rowNum
     * @param colName
     * @return The value which special rowNum and colName
     */
    public Object getValueAfterFiled(int rowNum, String colName) {
        Record record = this.getRecordList().get(rowNum);
        return record.getAttribute(colName).getValue();
    }

    /**
     * Getter for conflictDataMap.
     * 
     * @return the conflictDataMap
     */
    public SoftReference<HashMap<String, List<Integer>>> getConflictDataMap() {
        return this.conflictDataMap;
    }

    /**
     * 
     * Get all of conflict data
     * 
     * @param colName the name of column which conflict data come from
     * @return The list of conflict data
     */
    public List<Integer> getConflictDataIndexList(String colName) {
        Map<String, List<Integer>> tempConflictDataMap = this.getConflictDataMap().get();
        if (tempConflictDataMap != null) {
            return tempConflictDataMap.get(colName);
        } else {
            return null;
        }

    }

    /**
     * 
     * Create by zshen store conflict row num of every column.
     * 
     * @param colName The name of column which generate conflict
     * @param index The row number of conflic generate
     */
    public void addConfDataIndex(String colName, Integer index) {
        Map<String, List<Integer>> tempConflictDataMap = this.getConflictDataMap().get();
        if (tempConflictDataMap != null) {
            List<Integer> indexList = tempConflictDataMap.get(colName);
            if (indexList == null) {
                indexList = new ArrayList<>();
                tempConflictDataMap.put(colName, indexList);
            }
            indexList.add(index);
        } else {
            return;
        }

    }

    /**
     * 
     * Create by zshen create subset of current dataset by speciala column name.
     * 
     * @param colName The column name of new subset
     * @return a sebset of current dataset not new data be created in the process
     */
    public SubDataSet createSubDataSet(String colName) {
        List<Integer> conflictDataIndexList = this.getConflictDataIndexList(colName);
        if (conflictDataIndexList == null) {
            return null;
        }
        return new SubDataSet(this, conflictDataIndexList);
    }

    /**
     * Getter for survivorIndexMap.
     * 
     * @return the survivorIndexMap
     */
    public HashMap<String, Integer> getSurvivorIndexMap() {
        return this.survivorIndexMap;
    }

    /**
     * Getter for columnOrder.
     * 
     * @return the columnOrder
     */
    public List<Column> getColumnOrder() {
        return this.columnOrder;
    }

    /**
     * Sets the columnOrder.
     * 
     * @param columnOrder the columnOrder to set
     */
    public void setColumnOrder(List<Column> columnOrder) {
        this.columnOrder = columnOrder;
    }

}
