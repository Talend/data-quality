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

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.record.linkage.utils.AlgorithmSwitch;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;

/**
 * created by zshen on Aug 7, 2013 generating the blocking keys.
 * 
 */
public class AbstractGenerateKey {

    public static final String TGENKEY_ALL_COLUMN_NAMES = "tgenkey_all_column_names";//$NON-NLS-1$

    public static final String ALGO_KEY_PREFIX = "algo";//$NON-NLS-1$

    private Map<String, List<String[]>> genKeyToBlockResult = new HashMap<String, List<String[]>>();

    private static final String[] parameters = { MatchAnalysisConstant.PRE_ALGO, MatchAnalysisConstant.PRE_VALUE,
            MatchAnalysisConstant.KEY_ALGO, MatchAnalysisConstant.KEY_VALUE, MatchAnalysisConstant.POST_ALGO,
            MatchAnalysisConstant.POST_VALUE };

    /**
     * generate the blocking key for each columns
     * 
     * @param BlockKeyDefinitions
     * @param dataMap
     * @param inputString
     */
    public void generateKey(List<Map<String, String>> BlockKeyDefinitions, Map<String, String> dataMap, String[] inputString) {
        String genKey = getGenKey(BlockKeyDefinitions, dataMap);
        appendGenKeyResult(inputString, genKey);

    }

    /**
     * Append the input to the list of given generation key.
     * 
     * @param inputString
     * @param genKey
     */
    public void appendGenKeyResult(String[] inputString, String genKey) {
        String[] resultArray = new String[inputString.length + 1];
        for (int index = 0; index < inputString.length; index++) {
            resultArray[index] = inputString[index];
        }
        resultArray[inputString.length] = genKey;
        if (genKeyToBlockResult.get(genKey) != null) {
            List<String[]> resultInBlock = genKeyToBlockResult.get(genKey);
            resultInBlock.add(resultArray);
        } else {
            // Put the result which has same generating key in one block
            List<String[]> resultInNewBlock = new ArrayList<String[]>();
            resultInNewBlock.add(resultArray);
            genKeyToBlockResult.put(genKey, resultInNewBlock);
        }
    }

    /**
     * get the block key of each column.
     * 
     * @param blockKeyDefinitions the key defintion.
     * @param dataMap the column to valueOfColumn map.
     * @return the concatenated string of blocking keys.
     */
    public String getGenKey(List<Map<String, String>> blockKeyDefinitions, Map<String, String> dataMap) {
        String[] blockKeys = getGenKeyArray(blockKeyDefinitions, dataMap);
        return StringUtils.join(blockKeys);

    }

    /**
     * get the block key of each column.
     * 
     * @param blockKeyDefinitions the key defintion.
     * @param dataMap the column to valueOfColumn map.
     * @return the array of blocking keys.
     */
    public String[] getGenKeyArray(List<Map<String, String>> blockKeyDefinitions, Map<String, String> dataMap) {
        String[] blockKeyArray = new String[blockKeyDefinitions.size()];
        // get algos for each columns
        int idx = 0;
        for (Map<String, String> blockKey : blockKeyDefinitions) {
            String colName = blockKey.get(MatchAnalysisConstant.PRECOLUMN);
            // TODO zshen Check if the parameter can be internally set.
            String colValue = getAlgoForEachColumn(dataMap.get(colName), blockKey, parameters);
            blockKeyArray[idx] = colValue;
            idx++;
        }
        return blockKeyArray;
    }

    /**
     * 
     * getthe Algo value For Each Column.
     * 
     * @param originalValue
     * @param blockKey
     * @param params
     * @return
     */
    public String getAlgoForEachColumn(String originalValue, Map<String, String> blockKey, String[] params) {
        String tempVar;
        String colValue = originalValue;
        String preAlgoName = blockKey.get(params[0]);
        String preAlgoPara = blockKey.get(params[1]);
        String keyAlgoName = blockKey.get(params[2]);
        String keyAlgoPara = blockKey.get(params[3]);
        String postAlgoName = blockKey.get(params[4]);
        String postAlgoPara = blockKey.get(params[5]);

        if (colValue == null) {
            colValue = StringUtils.EMPTY;
        }

        tempVar = AlgorithmSwitch.getPreAlgoResult(preAlgoName, preAlgoPara, colValue);
        if (StringUtils.isNotEmpty(tempVar)) {
            colValue = tempVar;
        }

        tempVar = AlgorithmSwitch.getAlgoResult(keyAlgoName, keyAlgoPara, colValue);
        if (StringUtils.isNotEmpty(tempVar)) {
            colValue = tempVar;
        }

        tempVar = AlgorithmSwitch.getPostAlgoResult(postAlgoName, postAlgoPara, colValue);
        if (StringUtils.isNotEmpty(tempVar)) {
            colValue = tempVar;
        }
        return colValue;
    }

    /**
     * Getter for resultList.
     * 
     * @return the resultList
     */
    public Map<String, List<String[]>> getResultList() {
        return this.genKeyToBlockResult;
    }

}
