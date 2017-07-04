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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.drools.core.util.StringUtils;
import org.talend.survivorship.action.ExcludeValuesAction;
import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.DataSet;
import org.talend.survivorship.model.FilledAttribute;
import org.talend.survivorship.model.InputConvertResult;
import org.talend.survivorship.model.SubDataSet;
import org.talend.survivorship.model.SurvivedResult;

/**
 * create by zshen conflict resolve handler
 */
public class CRCRHandler extends AbstractChainOfResponsibilityHandler {

    protected Map<Integer, String> conflictingRowNumbers = new HashMap<>();

    /**
     * CRCRHandler constructor.
     * 
     * @param handlerParameter relation parameter
     */
    public CRCRHandler(HandlerParameter handlerParameter) {
        super(handlerParameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#doHandle(java.lang.Object, int,
     * java.lang.String, java.lang.String)
     */
    protected void doHandle(Integer rowNum, String columnName) {
        this.conflictingRowNumbers.put(rowNum, columnName);
    }

    @Override
    protected void initConflictRowNum(Map<Integer, String> preConflictRowNum) {
        this.conflictingRowNumbers.putAll(preConflictRowNum);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#handleRequest(java.lang.Object, int)
     */
    @Override
    public void handleRequest(Object inputData, int rowNum) {
        if (conflictingRowNumbers.size() == 0) {
            handleRequest();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#handleRequest(java.lang.Object, int,
     * java.lang.String)
     */
    @Override
    public void handleRequest() {
        // do process all of data and init next node
        // 1.create new dataSet by new method of dataset class

        // ConflictDataIndexList is old data
        List<Integer> conflictDataIndexList = this.getHandlerParameter().getConflictDataIndexList();
        if (conflictDataIndexList == null) {
            return;
        }
        // ConflictDataIndexList be clear
        this.getHandlerParameter().updateDataSet();
        if (this.getPreSuccessor() != null) {
            ((SubDataSet) this.getHandlerParameter().getDataset()).getFillAttributeMap()
                    .putAll(((SubDataSet) this.getPreSuccessor().getHandlerParameter().getDataset()).getFillAttributeMap());
        }
        for (Integer index : conflictDataIndexList) {
            InputConvertResult inputData = getInputData(index);
            if (this.canHandle(inputData.getInputData(), getHandlerParameter().getExpression(), index)) {
                doHandle(index, getRealColName(index));
                if (this.getSuccessor() != null) {
                    // 3.generate new conflict data for next node
                    // init ConflictDataIndexList for next one
                    this.getHandlerParameter().addConfDataIndex(index);
                }
            }
        }

        // 4.next handleRequest
        if (this.getSuccessor() == null) {
            return;
        }
        List<Integer> newConflictDataIndexList = this.getHandlerParameter().getConflictDataIndexList();
        if (newConflictDataIndexList.size() <= 0) {
            this.getHandlerParameter().getConflictDataIndexList().addAll(conflictDataIndexList);
            this.getSuccessor().getHandlerParameter().getConflictDataIndexList().clear();
        } else if (newConflictDataIndexList.size() == 1) {
            return;
        } else {
            this.getSuccessor().getHandlerParameter().getConflictDataIndexList().clear();
            this.getSuccessor().getHandlerParameter().getConflictDataIndexList().addAll(newConflictDataIndexList);
        }
        this.getSuccessor().handleRequest();
    }

    /**
     * Get the column name which is filled column name or target column name
     * 
     * @return
     */
    private String getRealColName(int index) {
        DataSet dataset = this.getHandlerParameter().getDataset();
        String tarName = this.getHandlerParameter().getTargetColumn().getName();
        if (dataset instanceof SubDataSet) {

            Attribute attribute = this.getHandlerParameter().getDataset().getRecordList().get(index).getAttribute(tarName);
            FilledAttribute filledAttribute = ((SubDataSet) dataset).getFillAttributeMap().get(attribute);
            if (filledAttribute != null) {
                return filledAttribute.getColumn().getName();
            }
        }
        return tarName;
    }

    /**
     * Get inputData by rowNum and the name of the filled column
     * 
     * @return The data for current row number if some data need to be fill then fill them
     */
    private InputConvertResult getInputData(Integer rowNum) {
        InputConvertResult inputResult = new InputConvertResult();
        Map<String, Integer> columnIndexMap = handlerParameter.getColumnIndexMap();
        Object[] dataArray = new Object[columnIndexMap.size()];
        for (Entry<String, Integer> entry : columnIndexMap.entrySet()) {
            String colName = entry.getKey();
            Object value = handlerParameter.getDataset().getValueAfterFiled(rowNum, colName);
            dataArray[entry.getValue()] = value;
        }
        inputResult.setInputData(dataArray);
        return inputResult;
    }

    /**
     * Judge whether the value should be filled
     * 
     * @param value The value which will be check
     * @param columnName The name of column
     * @return true when the value need to be fill else false
     */
    protected boolean needFillColumn(Object value, String columnName) {
        boolean ignoreBlank = handlerParameter.isIgnoreBlank();
        return inputDataIsEmpty(value, ignoreBlank);
    }

    /**
     * Judge whether input data is empty (null/empty/blank)
     * 
     * @param value The value which we will check
     * @param ignoreBlank decide blank whether be ignored
     * @return true when input data is empty (null/empty/blank) else false
     */
    private boolean inputDataIsEmpty(Object value, boolean ignoreBlank) {
        return value == null || StringUtils.EMPTY.equals(ignoreBlank ? value.toString().trim() : value.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#isContinue(java.lang.Object,
     * java.lang.String, int)
     */
    @Override
    protected boolean needContinue(Object inputData, int rowNum) {
        // Skip conflict has been resolved case
        if (!this.getHandlerParameter().isConflictRow(rowNum)) {
            return false;
        }
        return this.canHandle(inputData, getHandlerParameter().getExpression(), rowNum);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#getSuccessor()
     */
    @Override
    public CRCRHandler getSuccessor() {
        return (CRCRHandler) super.getSuccessor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#linkSuccessor(org.talend.survivorship.action.
     * handler.AbstractChainResponsibilityHandler)
     */
    @Override
    public AbstractChainOfResponsibilityHandler linkSuccessor(AbstractChainOfResponsibilityHandler nextHandler) {
        // CRCRHandler link CRCRHandler only else will link fail and return itself
        if (nextHandler instanceof CRCRHandler) {
            return super.linkSuccessor(nextHandler);
        } else {
            return this;
        }
    }

    /**
     * 
     * get row number of survivored value
     * 
     * @return The row number and column name. Note that the column name is not target column alaways when current row has been
     * fill by other column
     */
    public SurvivedResult getSurvivedRowNum() {
        if (this.conflictingRowNumbers.size() == 1 || isAllConflictingRowWithSameData()) {
            return CreateSurvivedResult();
        } else if (this.getSuccessor() != null) {
            return this.getSuccessor().getSurvivedRowNum();
        } else {
            return CreateSurvivedResult();
        }
    }

    /**
     * zshen Create survived reulst
     * 
     * @return use first one data to Create survived reulst
     */
    private SurvivedResult CreateSurvivedResult() {
        Iterator<Integer> iterator = this.conflictingRowNumbers.keySet().iterator();
        if (iterator.hasNext()) {
            Integer index = iterator.next();
            return new SurvivedResult(index, conflictingRowNumbers.get(index));
        }
        return null;

    }

    /**
     * 
     * fill column case return empty directly.
     * So that all of column should be tarColumn
     * 
     * @return The longest value in the conflict values
     */
    public Object getLongestResult(Object result) {
        Object finalResult = StringUtils.EMPTY;
        SubDataSet dataSet = (SubDataSet) this.getHandlerParameter().getDataset();
        List<Integer> dataSetIndex = dataSet.getDataSetIndex();
        for (Integer rowNum : dataSetIndex) {
            String targetName = this.getHandlerParameter().getTargetColumn().getName();
            Object targetInputData = this.getHandlerParameter().getInputData(rowNum, targetName);
            // if value is come from other column need to get from the column
            if (needFillColumn(targetInputData, targetName)) {
                targetInputData = this.getHandlerParameter().getInputData(rowNum, handlerParameter.getFillColumn());
            }
            // if the value is unexpected constant then skip it
            if (isUnexpectedValue(targetInputData)) {
                continue;
            }
            // whether new value is same as old one
            if (result.equals(targetInputData)) {
                continue;
            }
            if (finalResult.toString().length() < targetInputData.toString().length()) {
                finalResult = targetInputData;
            }

        }
        return finalResult;

    }

    /**
     * check whether parameter value is unexpected
     * 
     * @return true if it is unexpected value else false
     */
    private boolean isUnexpectedValue(Object targetInputData) {
        if (targetInputData == null) {
            return true;
        }

        if (this.getHandlerParameter().getAction() instanceof ExcludeValuesAction) {
            return ExcludeValuesAction.checkUnexpectedValue(this.getHandlerParameter().getExpression(), targetInputData);
        }

        return false;
    }

    /**
     * 
     * Get no duplicate with other survived values value.
     * 
     * @param result current survived value
     * @return No duplicate value
     */
    public Object getNonDuplicatedResult(Object result) {
        String fillColumn = this.getHandlerParameter().getFillColumn();
        if (fillColumn != null && !fillColumn.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return getLongestResult(result);
    }

    /**
     * check whether all the conflicting rows contain the same value
     * 
     * @return true when all of data are same else return false
     */
    private boolean isAllConflictingRowWithSameData() {
        Set<Object> setContainer = new HashSet<>();
        Iterator<Integer> iterator = this.conflictingRowNumbers.keySet().iterator();
        while (iterator.hasNext()) {
            Integer index = iterator.next();
            setContainer.add(
                    this.getHandlerParameter().getDataset().getValueAfterFiled(index, this.conflictingRowNumbers.get(index)));
        }
        return setContainer.size() == 1;
    }

}
