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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.IRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.adapter.ComponentMatchParameterAdapter;
import org.talend.dataquality.record.linkage.grouping.adapter.MatchParameterAdapter;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * created by yyin on 2015年10月30日 Detailled comment
 *
 */
public class SurvivorshipUtils {

    public static final String NUMBER_ID = "id_"; //$NON-NLS-1$

    public static final String DEFAULT_CONCATENATE_PARAMETER = ","; //$NON-NLS-1$

    public static final String SURVIVORSHIP_FUNCTION = "SURVIVORSHIP_FUNCTION"; //$NON-NLS-1$

    public static final String PARAMETER = "PARAMETER"; //$NON-NLS-1$

    public static final String DATA_TYPE = "DATA_TYPE"; //$NON-NLS-1$

    /**
     * 
     * zshen Comment method "createSurvivorShipAlgorithmParams".
     * Same with {@link AnalysisRecordGroupingUtils#createSurvivorShipAlgorithmParams} so
     * that any modify need to synchronization them with same time
     * 
     * @param analysisMatchRecordGrouping
     * @param recordMatchingIndicator
     * @param columnMap
     * @return
     */
    public static SurvivorShipAlgorithmParams createSurvivorShipAlgorithmParams(
            AnalysisMatchRecordGrouping analysisMatchRecordGrouping, List<List<Map<String, String>>> joinKeyRules,
            List<Map<String, String>> defaultSurvivorshipRules,
            List<Map<String, String>> particularDefaultSurvivorshipDefinitions, Map<String, String> columnWithType,
            Map<String, String> columnWithIndex) {

        return createSurvivorShipAlgorithmParams(new ComponentMatchParameterAdapter(analysisMatchRecordGrouping, joinKeyRules,
                defaultSurvivorshipRules, particularDefaultSurvivorshipDefinitions, columnWithType, columnWithIndex));

    }

    /**
     * 
     * zshen Comment method "createSurvivorShipAlgorithmParams".
     * Same with {@link AnalysisRecordGroupingUtils#createSurvivorShipAlgorithmParams} so
     * that any modify need to synchronization them with same time
     * 
     * @param analysisMatchRecordGrouping
     * @param recordMatchingIndicator
     * @param columnMap
     * @return
     */
    public static SurvivorShipAlgorithmParams createSurvivorShipAlgorithmParams(
            AnalysisMatchRecordGrouping analysisMatchRecordGrouping, List<List<Map<String, String>>> joinKeyRules,
            List<Map<String, String>> defaultSurvivorshipRules, Map<String, String> columnWithType,
            Map<String, String> columnWithIndex) {

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams = new SurvivorShipAlgorithmParams();

        // Survivorship functions.
        List<SurvivorshipFunction> survFunctions = new ArrayList<>();
        for (List<Map<String, String>> survivorshipKeyDefs : joinKeyRules) {
            for (Map<String, String> survDef : survivorshipKeyDefs) {
                SurvivorshipFunction func = createSurvivorshipFunction(survivorShipAlgorithmParams, survDef);
                survFunctions.add(func);
            }

        }
        survivorShipAlgorithmParams.setSurviorShipAlgos(survFunctions.toArray(new SurvivorshipFunction[survFunctions.size()]));

        // Set default survivorship functions.
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<>();

        for (Entry<String, String> entry : columnWithType.entrySet()) {
            String columnName = entry.getKey();
            String dataTypeName = entry.getValue();

            for (Map<String, String> defSurvDef : defaultSurvivorshipRules) {
                // the column's data type start with id_, so need to add id_ ahead of the default survivorship's data
                // type before judging if they are equal
                if (StringUtils.equalsIgnoreCase(dataTypeName, NUMBER_ID + defSurvDef.get(DATA_TYPE))
                        || StringUtils.equalsIgnoreCase(defSurvDef.get(DATA_TYPE), "Number") //$NON-NLS-1$ 
                                && ArrayUtils.contains(NUMBERS, dataTypeName)) {
                    putNewSurvFunc(survivorShipAlgorithmParams, defaultSurvRules,
                            Integer.parseInt(columnWithIndex.get(columnName)), columnName, defSurvDef.get(PARAMETER),
                            defSurvDef.get(SURVIVORSHIP_FUNCTION));
                    break;
                }
            } // End for: if no func defined, then the value will be taken from one of the records in a group (1st
              // one ).
        }

        survivorShipAlgorithmParams.setDefaultSurviorshipRules(defaultSurvRules);

        // Set the record matcher
        CombinedRecordMatcher combinedRecordMatcher = analysisMatchRecordGrouping.getCombinedRecordMatcher();
        survivorShipAlgorithmParams.setRecordMatcher(combinedRecordMatcher);
        Map<IRecordMatcher, SurvivorshipFunction[]> survAlgos = new HashMap<>();
        Map<Integer, SurvivorshipFunction> colIdx2DefaultSurvFunc = survivorShipAlgorithmParams.getDefaultSurviorshipRules();
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
                        surFuncsInMatcher[idx] = survivorShipAlgorithmParams.new SurvivorshipFunction();
                        surFuncsInMatcher[idx].setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
                        // MOD TDQ-11774 set a default parameter
                        surFuncsInMatcher[idx].setParameter(SurvivorshipUtils.DEFAULT_CONCATENATE_PARAMETER);
                    }
                } else {
                    surFuncsInMatcher[idx] = createSurvivorshipFunction(survivorShipAlgorithmParams, mkDef);
                }
                idx++;
            }

            // Add the funcs to a specific record matcher. NOTE that the index of matcher must be coincidence to the
            // index of match rule.
            survAlgos.put(combinedRecordMatcher.getMatchers().get(matchRuleIdx), surFuncsInMatcher);

        }

        survivorShipAlgorithmParams.setSurvivorshipAlgosMap(survAlgos);

        return survivorShipAlgorithmParams;
    }

    /**
     * 
     * zshen Comment method "createSurvivorShipAlgorithmParams".
     * Same with {@link AnalysisRecordGroupingUtils#createSurvivorShipAlgorithmParams} so
     * that any modify need to synchronization them with same time
     * 
     * @param analysisMatchRecordGrouping
     * @param recordMatchingIndicator
     * @param columnMap
     * @return
     */
    public static SurvivorShipAlgorithmParams createSurvivorShipAlgorithmParams(MatchParameterAdapter parameterAdapter) {
        SurvivorShipAlgorithmParams survivorShipAlgorithmParams = new SurvivorShipAlgorithmParams();

        // Survivorship functions.
        List<SurvivorshipFunction> survFunctions = parameterAdapter.getAllSurvivorshipFunctions();

        survivorShipAlgorithmParams.setSurviorShipAlgos(survFunctions.toArray(new SurvivorshipFunction[survFunctions.size()]));

        // Set default survivorship functions.
        Map<Integer, SurvivorshipFunction> defaultSurvRules = parameterAdapter.getDefaultSurviorShipRules();

        survivorShipAlgorithmParams.setDefaultSurviorshipRules(defaultSurvRules);

        // Set the record matcher
        CombinedRecordMatcher combinedRecordMatcher = parameterAdapter.getCombinedRecordMatcher();
        survivorShipAlgorithmParams.setRecordMatcher(combinedRecordMatcher);
        Map<IRecordMatcher, SurvivorshipFunction[]> survAlgos = parameterAdapter.getSurvivorshipAlgosMap(defaultSurvRules,
                survFunctions);

        survivorShipAlgorithmParams.setSurvivorshipAlgosMap(survAlgos);

        return survivorShipAlgorithmParams;
    }

    /**
     * DOC talend Comment method "createSurvivorshipFunction".
     * 
     * @param survivorShipAlgorithmParams
     * @param survDef
     * @return
     */
    protected static SurvivorshipFunction createSurvivorshipFunction(SurvivorShipAlgorithmParams survivorShipAlgorithmParams,
            Map<String, String> survDef) {
        SurvivorshipFunction func = survivorShipAlgorithmParams.new SurvivorshipFunction();
        func.setSurvivorShipKey(survDef.get("ATTRIBUTE_NAME")); //$NON-NLS-1$
        func.setParameter(survDef.get(PARAMETER));
        String functionName = survDef.get(SURVIVORSHIP_FUNCTION);
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

    private static void putNewSurvFunc(SurvivorShipAlgorithmParams survivorShipAlgorithmParams,
            Map<Integer, SurvivorshipFunction> defaultSurvRules, int columnIndex, String columnName, String parameter,
            String algorithmType) {
        SurvivorshipFunction survFunc = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc.setSurvivorShipKey(columnName);
        survFunc.setParameter(parameter);
        survFunc.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.getTypeBySavedValue(algorithmType));
        defaultSurvRules.put(columnIndex, survFunc);
    }

    public final static String[] NUMBERS = new String[] { NUMBER_ID + Integer.class.getSimpleName(),
            NUMBER_ID + Float.class.getSimpleName(), NUMBER_ID + Double.class.getSimpleName(),
            NUMBER_ID + Long.class.getSimpleName(), NUMBER_ID + Short.class.getSimpleName(),
            NUMBER_ID + BigDecimal.class.getSimpleName(), NUMBER_ID + Byte.class.getSimpleName() };

    // <nodeData xsi:type="tdqmatching:MatchingData">
    // <ruleMatchers>
    // <joinkeys>
    // --<columnMap key="MATCHING_TYPE" value="Exact"/>
    // --<columnMap key="THRESHOLD" value="1"/>
    // --<columnMap key="SURVIVORSHIP_FUNCTION" value="0"/>
    // --<columnMap key="HANDLE_NULL" value="nullMatchNull"/>
    // --<columnMap key="CONFIDENCE_WEIGHT" value="1"/>
    // --<columnMap key="INPUT_COLUMN" value="customer_id"/>
    // --<columnMap key="PARAMETER" value=""/>
    // </joinkeys>
    // <matchParamMap key="INTERVAL_RULE" value="0.85"/>
    // <matchParamMap key="MATCHING_ALGORITHM" value="TSWOOSH_MATCHER"/>
    // </ruleMatchers>
    // <defaultSurvivorshipDefinitions>
    // --<columnMap key="SURVIVORSHIP_FUNCTION" value="CONCATENATE"/>
    // --<columnMap key="DATA_TYPE" value="DATA_BOOLEAN"/>
    // --<columnMap key="PARAMETER" value=""/>
    // </defaultSurvivorshipDefinitions>
    // <defaultSurvivorshipDefinitions>
    // --<columnMap key="SURVIVORSHIP_FUNCTION" value="MOST_ANCIENT"/>
    // --<columnMap key="DATA_TYPE" value="DATA_DATE"/>
    // --<columnMap key="PARAMETER" value="3"/>
    // </defaultSurvivorshipDefinitions>
    // </nodeData>
}
