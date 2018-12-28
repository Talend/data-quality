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
package org.talend.dataquality.record.linkage.grouping.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.IRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * Create a adapter for component of match parameter class
 */
public class ComponentMatchParameterAdapter extends MatchParameterAdapter {

    private AnalysisMatchRecordGrouping analysisMatchRecordGrouping;

    private List<List<Map<String, String>>> joinKeyRules;

    private List<Map<String, String>> defaultSurvivorshipRules;

    private List<Map<String, String>> particularDefaultSurvivorshipDefinitions;

    private Map<String, String> columnWithType;

    private Map<String, String> columnWithIndex;

    public ComponentMatchParameterAdapter(AnalysisMatchRecordGrouping analysisMatchRecordGrouping,
            List<List<Map<String, String>>> joinKeyRules, List<Map<String, String>> defaultSurvivorshipRules,
            List<Map<String, String>> particularDefaultSurvivorshipDefinitions, Map<String, String> columnWithType,
            Map<String, String> columnWithIndex) {

        this.analysisMatchRecordGrouping = analysisMatchRecordGrouping;
        this.joinKeyRules = joinKeyRules;
        this.defaultSurvivorshipRules = defaultSurvivorshipRules;
        this.particularDefaultSurvivorshipDefinitions = particularDefaultSurvivorshipDefinitions;
        this.columnWithType = columnWithType;
        this.columnWithIndex = columnWithIndex;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.adapter.MatchParameterAdapter#getAllSurvivorshipFunctions()
     */
    @Override
    public List<SurvivorshipFunction> getAllSurvivorshipFunctions() {
        // Survivorship functions.
        List<SurvivorshipFunction> survFunctions = new ArrayList<>();
        for (List<Map<String, String>> survivorshipKeyDefs : joinKeyRules) {
            for (Map<String, String> survDef : survivorshipKeyDefs) {
                SurvivorshipFunction func = createSurvivorshipFunction(survDef);
                survFunctions.add(func);
            }

        }
        return survFunctions;
    }

    /**
     * Create Survivorship Function
     * 
     * @param survivorShipAlgorithmParams
     * @param survDef
     * @return
     */
    private SurvivorshipFunction createSurvivorshipFunction(Map<String, String> survDef) {
        SurvivorshipFunction func = new SurvivorShipAlgorithmParams().new SurvivorshipFunction();
        func.setSurvivorShipKey(survDef.get("ATTRIBUTE_NAME")); //$NON-NLS-1$
        func.setParameter(survDef.get(SurvivorshipUtils.PARAMETER));
        String functionName = survDef.get(SurvivorshipUtils.SURVIVORSHIP_FUNCTION);
        SurvivorShipAlgorithmEnum surAlgo = SurvivorShipAlgorithmEnum.getTypeBySavedValue(functionName);
        if (surAlgo == null) {
            Integer typeIndex = 0;
            if (functionName != null && functionName.trim().length() > 0) {
                typeIndex = Integer.parseInt(functionName);
            }
            surAlgo = SurvivorShipAlgorithmEnum.getTypeByIndex(typeIndex);
        }
        func.setSurvivorShipAlgoEnum(surAlgo);
        return func;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.adapter.MatchParameterAdapter#getDefaultSurviorShipRules()
     */
    @Override
    public Map<Integer, SurvivorshipFunction> getDefaultSurviorShipRules() {
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<>();
        for (Entry<String, String> entry : columnWithType.entrySet()) {
            String columnName = entry.getKey();
            String dataTypeName = entry.getValue();

            for (Map<String, String> pdefaultSurvivdef : particularDefaultSurvivorshipDefinitions) {
                if (pdefaultSurvivdef.get(IRecordGrouping.INPUT_COLUMN).equals(columnName)) {
                    // try to get reference column index and setting into new function
                    String referenceColumnName = pdefaultSurvivdef.get(IRecordGrouping.REFERENCE_COLUMN);
                    String refColumnIndex = columnWithIndex.get(referenceColumnName);
                    int colIndex = Integer.parseInt(columnWithIndex.get(columnName));
                    int referenceColumnIndex = colIndex;
                    if (refColumnIndex != null) {
                        referenceColumnIndex = Integer.parseInt(refColumnIndex);
                    }
                    putNewSurvFunc(defaultSurvRules, colIndex, columnName, pdefaultSurvivdef.get(SurvivorshipUtils.PARAMETER),
                            pdefaultSurvivdef.get(SurvivorshipUtils.SURVIVORSHIP_FUNCTION), referenceColumnName,
                            referenceColumnIndex);
                    break;
                }
            }
            // default survivorship has been handle by Particular
            if (defaultSurvRules.get(Integer.parseInt(columnWithIndex.get(columnName))) != null) {
                continue;
            }
            for (Map<String, String> defSurvDef : defaultSurvivorshipRules) {
                // the column's data type start with id_, so need to add id_ ahead of the default survivorship's data
                // type before judging if they are equal

                if (SurvivorshipUtils.isMappingDataType(dataTypeName, defSurvDef.get(SurvivorshipUtils.DATA_TYPE))) {
                    // try to get reference column index and setting into new function
                    String referenceColumnName = defSurvDef.get(IRecordGrouping.REFERENCE_COLUMN);
                    String refColumnIndex = columnWithIndex.get(referenceColumnName);
                    int colIndex = Integer.parseInt(columnWithIndex.get(columnName));
                    int referenceColumnIndex = colIndex;
                    if (refColumnIndex != null) {
                        referenceColumnIndex = Integer.parseInt(refColumnIndex);
                    }
                    putNewSurvFunc(defaultSurvRules, colIndex, columnName, defSurvDef.get(SurvivorshipUtils.PARAMETER),
                            defSurvDef.get(SurvivorshipUtils.SURVIVORSHIP_FUNCTION), referenceColumnName, referenceColumnIndex);
                    break;
                }
            } // End for: if no func defined, then the value will be taken from one of the records in a group (1st
              // one ).
        }
        return defaultSurvRules;
    }

    private void putNewSurvFunc(Map<Integer, SurvivorshipFunction> defaultSurvRules, int columnIndex, String columnName,
            String parameter, String algorithmType, String refColName, int refColIndex) {
        SurvivorshipFunction survFunc = new SurvivorShipAlgorithmParams().new SurvivorshipFunction();
        survFunc.setSurvivorShipKey(columnName);
        survFunc.setParameter(parameter);
        survFunc.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.getTypeBySavedValue(algorithmType));
        survFunc.setReferenceColumnName(refColName);
        survFunc.setReferenceColumnIndex(refColIndex);
        defaultSurvRules.put(columnIndex, survFunc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.adapter.MatchParameterAdapter#getSurvivorshipAlgosMap()
     * 
     * SurvFunctions is never used by this method but it is useful on analysis side so that we keep this parameter.
     * If we want to remove it.Please think about how to remove it on AnalysisMatchParameterAdapter class with same time
     */
    @Override
    public Map<IRecordMatcher, SurvivorshipFunction[]> getSurvivorshipAlgosMap(
            Map<Integer, SurvivorshipFunction> colIdx2DefaultSurvFunc, List<SurvivorshipFunction> survFunctions) {
        Map<IRecordMatcher, SurvivorshipFunction[]> survAlgos = new HashMap<>();
        int matchRuleIdx = -1;
        for (List<Map<String, String>> matchrule : joinKeyRules) {
            matchRuleIdx++;
            if (matchrule == null) {
                continue;
            }

            SurvivorshipFunction[] surFuncsInMatcher = new SurvivorshipFunction[matchrule.size()];
            int idx = 0;
            for (Map<String, String> mkDef : matchrule) {
                String matcherType = mkDef.get(IRecordGrouping.MATCHING_TYPE);
                if (AttributeMatcherType.DUMMY.name().equalsIgnoreCase(matcherType)) {
                    // Find the func from default survivorship rule.
                    surFuncsInMatcher[idx] = colIdx2DefaultSurvFunc.get(Integer.valueOf(mkDef.get(IRecordGrouping.COLUMN_IDX)));
                    if (surFuncsInMatcher[idx] == null) {
                        // Use CONCATENATE by default if not specified .
                        surFuncsInMatcher[idx] = new SurvivorShipAlgorithmParams().new SurvivorshipFunction();
                        surFuncsInMatcher[idx].setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
                        // MOD TDQ-11774 set a default parameter
                        surFuncsInMatcher[idx].setParameter(SurvivorshipUtils.DEFAULT_CONCATENATE_PARAMETER);
                    }
                } else {
                    surFuncsInMatcher[idx] = createSurvivorshipFunction(mkDef);
                }
                idx++;
            }

            // Add the funcs to a specific record matcher. NOTE that the index of matcher must be coincidence to the
            // index of match rule.
            survAlgos.put(getCombinedRecordMatcher().getMatchers().get(matchRuleIdx), surFuncsInMatcher);

        }

        return survAlgos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.adapter.MatchParameterAdapter#getCombinedRecordMatcher()
     */
    @Override
    public CombinedRecordMatcher getCombinedRecordMatcher() {
        return this.analysisMatchRecordGrouping.getCombinedRecordMatcher();
    }

}
