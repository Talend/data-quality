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
import java.util.Set;

import org.drools.core.util.StringUtils;
import org.talend.survivorship.action.ExclusivenessAction;
import org.talend.survivorship.model.InputConvertResult;
import org.talend.survivorship.model.Record;
import org.talend.survivorship.model.SubDataSet;
import org.talend.survivorship.model.SurvivedResult;

/**
 * create by zshen conflict resolve handler
 */
public class CRCRHandler extends AbstractChainResponsibilityHandler {

    protected Map<Integer, String> conflictRowNum = new HashMap<>();

    /**
     * Create by zshen CRCRHandler constructor.
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
        this.conflictRowNum.put(rowNum, columnName);
    }

    @Override
    protected void initConflictRowNum(Map<Integer, String> preConflictRowNum) {
        this.conflictRowNum.putAll(preConflictRowNum);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.survivorship.action.handler.AbstractChainResponsibilityHandler#handleRequest(java.lang.Object, int)
     */
    @Override
    public void handleRequest(Object inputData, int rowNum) {
        if (conflictRowNum.size() == 0) {
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
        // 2.loop all of data which make sure on the previous node
        for (Integer index : conflictDataIndexList) {
            InputConvertResult inputData = getInputData(index);
            if (this.canHandler(inputData.getInputData(), getHandlerParameter().getExpression(), index)) {
                doHandle(index, inputData.isIsfilled() ? this.getHandlerParameter().getFillColumn()
                        : this.getHandlerParameter().getTarColumn().getName());
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
        } else if (newConflictDataIndexList.size() == 1) {
            return;
        }
        this.getSuccessor().handleRequest();
    }

    /**
     * create by zshen get inputData by rowNum and the name of fill column
     * 
     * @return The data for current row number if some data need to be fill then fill them
     */
    private InputConvertResult getInputData(Integer rowNum) {
        InputConvertResult inputResult = new InputConvertResult();
        Map<String, Integer> columnIndexMap = handlerParameter.getColumnIndexMap();
        Object[] dataArray = new Object[columnIndexMap.size()];
        for (String colName : columnIndexMap.keySet()) {
            Record record = handlerParameter.getDataset().getRecordList().get(rowNum);
            Object value = record.getAttribute(colName).getValue();

            if (isNeedFillColumn(value, colName)) {
                value = record.getAttribute(handlerParameter.getFillColumn()).getValue();
                inputResult.setIsfilled(true);
            }
            dataArray[columnIndexMap.get(colName)] = value;
        }
        inputResult.setInputData(dataArray);
        return inputResult;
    }

    /**
     * create by zshen judge whether the value should be fill
     * 
     * @param value The value which will be check
     * @param columnName The name of column
     * @return true when the value need to be fill else false
     */
    private boolean isNeedFillColumn(Object value, String columnName) {
        if (!fillColumnIsValid() || !columnName.equals(handlerParameter.getRefColumn().getName())) {
            return false;
        }
        boolean ignoreBlank = handlerParameter.isIgnoreBlank();
        return inputDataIsEmpty(value, ignoreBlank);
    }

    private boolean fillColumnIsValid() {
        return handlerParameter.getFillColumn() != null && !handlerParameter.getFillColumn().trim().isEmpty();
    }

    /**
     * create by zshen judge whether input data is empty(null/empty/blank )
     * 
     * @param value The value which we will check
     * @param ignoreBlank decide blank whether be ignored
     * @return true when input data is empty(null/empty/blank ) else false
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
    protected boolean isContinue(Object inputData, int rowNum) {
        // Skip conflict has been resolved case
        if (!this.getHandlerParameter().isConflictRow(rowNum)) {
            return false;
        }
        return this.canHandler(inputData, getHandlerParameter().getExpression(), rowNum);
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
    public AbstractChainResponsibilityHandler linkSuccessor(AbstractChainResponsibilityHandler nextHandler) {
        // CRCRHandler link CRCRHandler only else will link fail and return itself
        if (nextHandler instanceof CRCRHandler) {
            return super.linkSuccessor(nextHandler);
        } else {
            return this;
        }
    }

    /**
     * 
     * Create by zshen get row number of survivored value
     * 
     * @return The row number and column name. Note that the column name is not target column alaways when current row has been
     * fill by other column
     */
    public SurvivedResult getSurvivoredRowNum() {
        if (this.conflictRowNum.size() == 1 || allNumWithSameData()) {
            Iterator<Integer> iterator = this.conflictRowNum.keySet().iterator();
            Integer index = iterator.next();
            return new SurvivedResult(index, conflictRowNum.get(index));
        } else if (this.getSuccessor() != null) {
            return this.getSuccessor().getSurvivoredRowNum();
        } else {
            return null;
        }
    }

    /**
     * 
     * create by zshen fill column case return empty directly.
     * So that all of column should be tarColumn
     * 
     * @return The longest value in the conflict values
     */
    public Object getLongestResult(Object result) {
        Object finalResult = StringUtils.EMPTY;
        SubDataSet dataSet = (SubDataSet) this.getHandlerParameter().getDataset();
        List<Integer> dataSetIndex = dataSet.getDataSetIndex();
        for (Integer rowNum : dataSetIndex) {
            String targetName = this.getHandlerParameter().getTarColumn().getName();
            Object tarInputData = this.getHandlerParameter().getInputData(rowNum, targetName);
            // if value is come from other column need to get from the column
            if (isNeedFillColumn(tarInputData, targetName)) {
                tarInputData = this.getHandlerParameter().getInputData(rowNum, handlerParameter.getFillColumn());
            }
            // if the value is unexpect constant then skip it
            if (isUnExpectValue(tarInputData)) {
                continue;
            }
            if (isCurrSurValue(result, tarInputData)) {
                continue;
            }
            if (finalResult.toString().length() < tarInputData.toString().length()) {
                finalResult = tarInputData;
            }

        }
        return finalResult;

    }

    /**
     * Create by zshen Judge whether new value is same with old one
     * 
     * @param result
     * @param tarInputData
     * @return
     */
    private boolean isCurrSurValue(Object result, Object tarInputData) {
        return result.equals(tarInputData);
    }

    /**
     * Create by zshen check whether parameter value is unexpect
     * 
     * @return true if it is unexpect value else false
     */
    private boolean isUnExpectValue(Object tarInputData) {
        if (tarInputData == null) {
            return true;
        }

        if (this.getHandlerParameter().getAction() instanceof ExclusivenessAction) {
            return ExclusivenessAction.checkUnExpect(this.getHandlerParameter().getExpression(), tarInputData);
        }

        return false;
    }

    /**
     * 
     * create by zshen Get no duplicate with other survived values value.
     * 
     * @param result current survived value
     * @return No duplicate value
     */
    public Object getNonDupResult(Object result) {
        String fillColumn = this.getHandlerParameter().getFillColumn();
        if (fillColumn != null && !fillColumn.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return getLongestResult(result);
    }

    /**
     * create by zshen check whether all of conflict row num for same value
     * 
     * @return true when all of data are same else return false
     */
    private boolean allNumWithSameData() {
        Set<Object> setContainer = new HashSet<>();
        Iterator<Integer> iterator = this.conflictRowNum.keySet().iterator();
        while (iterator.hasNext()) {
            Integer index = iterator.next();
            setContainer.add(this.getHandlerParameter().getInputData(index, this.conflictRowNum.get(index)));
        }
        return setContainer.size() == 1;
    }

}
