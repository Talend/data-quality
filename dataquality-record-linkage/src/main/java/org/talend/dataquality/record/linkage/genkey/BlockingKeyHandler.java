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
package org.talend.dataquality.record.linkage.genkey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * generate the blocking key for each selected columns
 */
public class BlockingKeyHandler {

    private List<Object[]> inputData = new ArrayList<Object[]>();

    private AbstractGenerateKey generateKeyAPI = new AbstractGenerateKey();

    private List<Map<String, String>> blockKeyDefinitions = null;

    protected Map<String, String> columnIndexMap = null;

    /**
     * Getter for inputData.
     * 
     * @return the inputData
     */
    public List<Object[]> getInputData() {
        return this.inputData;
    }

    /**
     * Sets the inputData.
     * 
     * @param inputData the inputData to set
     */
    public void setInputData(List<Object[]> inputData) {
        this.inputData = inputData;
    }

    public BlockingKeyHandler(List<Map<String, String>> blockKeyDefinitions, Map<String, String> columnMap) {
        this.blockKeyDefinitions = blockKeyDefinitions;
        this.columnIndexMap = columnMap;
    }

    /**
     * generate the blocking key for each columns
     */
    public void run() {
        for (Object[] inputObject : this.inputData) {
            process(inputObject);
        }
    }

    /**
     * 
     * @param inputObject
     * @return generation key of this input
     */
    public String process(Object[] inputObject) {
        String[] inputString = new String[inputObject.length];
        int index = 0;
        for (Object obj : inputObject) {
            inputString[index++] = obj == null ? null : obj.toString();
        }
        Map<String, String> columnValueMap = new HashMap<String, String>();
        for (Entry<String, String> entry : columnIndexMap.entrySet()) {
            String columnName = entry.getKey();
            columnValueMap.put(columnName, inputString[Integer.parseInt(entry.getValue())]);
        }
        String genKey = generateKeyAPI.getGenKey(blockKeyDefinitions, columnValueMap);
        generateKeyAPI.appendGenKeyResult(inputString, genKey);
        return genKey;
    }

    /**
     * Get blocking size given blocking key.
     * @param blockingKey
     * @return size of the block.
     */
    public int getBlockSize(String blockingKey) {
        return generateKeyAPI.getResultList().get(blockingKey).size();
    }

    /**
     * get the Result Data of block key definition.
     * 
     * @return
     */
    public List<Map<String, String>> getResultData() {
        return blockKeyDefinitions;
    }

    /**
     * get all keys of columns
     * 
     * @return
     */
    public Map<String, List<String[]>> getResultDatas() {
        return generateKeyAPI.getResultList();
    }

    /**
     * get all keys of columns
     * 
     * @return
     */
    public List<Object[]> getResultDataList() {
        List<Object[]> returnList = new ArrayList<Object[]>();
        for (String genKey : generateKeyAPI.getResultList().keySet()) {
            List<String[]> resultDatalistForGenKey = generateKeyAPI.getResultList().get(genKey);
            if (resultDatalistForGenKey != null) {
                returnList.addAll(resultDatalistForGenKey);
            }
        }
        return returnList;
    }

}
