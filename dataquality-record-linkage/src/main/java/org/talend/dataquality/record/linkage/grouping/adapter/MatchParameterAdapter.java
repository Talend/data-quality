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

import java.util.List;
import java.util.Map;

import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;

/**
 * supper class of match parameter adapter
 */
public abstract class MatchParameterAdapter {

    /**
     * 
     * Get all of Survivorship Functions.
     * 
     * @return
     */
    public abstract List<SurvivorshipFunction> getAllSurvivorshipFunctions();

    /**
     * Get Default SurviorShip Rules.
     * 
     * @return
     */
    public abstract Map<Integer, SurvivorshipFunction> getDefaultSurviorShipRules();

    /**
     * Get Survivorship Algos Map.
     * 
     * @return
     */
    public abstract Map<IRecordMatcher, SurvivorshipFunction[]> getSurvivorshipAlgosMap(
            Map<Integer, SurvivorshipFunction> colIdx2DefaultSurvFunc, List<SurvivorshipFunction> survFunctions);

    /**
     * 
     * Get CombinedRecord Matcher.
     * 
     * @return
     */
    public abstract CombinedRecordMatcher getCombinedRecordMatcher();
}
