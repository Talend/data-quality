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

import java.util.HashMap;
import java.util.Map;

import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * A concreate class that hold all parameters that is needed by the swoosh algorithm.
 */
public class SurvivorShipAlgorithmParams {

    private IRecordMatcher recordMatcher = null;

    protected Map<IRecordMatcher, SurvivorshipFunction[]> survivorshipAlgosMap = new HashMap<IRecordMatcher, SurvivorshipFunction[]>();

    /**
     * The order in array is conform to the matching key order choosen.
     */
    private SurvivorshipFunction[] surviorShipAlgos;

    /**
     * The survivorship function at specific column (the key of map is the column index)
     */
    private Map<Integer, SurvivorshipFunction> defaultSurviorshipRules;

    /**
     * Getter for surviorShipAlgos.
     * 
     * @return the surviorShipAlgos
     */
    public SurvivorshipFunction[] getSurviorShipAlgos() {
        return this.surviorShipAlgos;
    }

    /**
     * Sets the surviorShipAlgos.
     * 
     * @param surviorShipAlgos the surviorShipAlgos to set
     */
    public void setSurviorShipAlgos(SurvivorshipFunction[] surviorShipAlgos) {
        this.surviorShipAlgos = surviorShipAlgos;
    }

    /**
     * Getter for defaultSurviorshipRules.
     * 
     * @return the defaultSurviorshipRules
     */
    public Map<Integer, SurvivorshipFunction> getDefaultSurviorshipRules() {
        return this.defaultSurviorshipRules;
    }

    /**
     * Sets the defaultSurviorshipRules.
     * 
     * @param defaultSurviorshipRules the defaultSurviorshipRules to set
     */
    public void setDefaultSurviorshipRules(Map<Integer, SurvivorshipFunction> defaultSurviorshipRules) {
        this.defaultSurviorshipRules = defaultSurviorshipRules;
    }

    /**
     * Getter for recordMatcher.
     * 
     * @return the recordMatcher
     */
    public IRecordMatcher getRecordMatcher() {
        return this.recordMatcher;
    }

    /**
     * Sets the recordMatcher.
     * 
     * @param recordMatcher the recordMatcher to set
     */
    public void setRecordMatcher(IRecordMatcher recordMatcher) {
        this.recordMatcher = recordMatcher;
    }

    /**
     * Getter for survivorshipAlgosMap.
     * 
     * @return the survivorshipAlgosMap
     */
    public Map<IRecordMatcher, SurvivorshipFunction[]> getSurvivorshipAlgosMap() {
        return this.survivorshipAlgosMap;
    }

    /**
     * Sets the survivorshipAlgosMap.
     * 
     * @param survivorshipAlgosMap the survivorshipAlgosMap to set
     */
    public void setSurvivorshipAlgosMap(Map<IRecordMatcher, SurvivorshipFunction[]> survivorshipAlgosMap) {
        this.survivorshipAlgosMap = survivorshipAlgosMap;
    }

    public class SurvivorshipFunction {

        String survivorShipKey;

        SurvivorShipAlgorithmEnum survivorShipAlgorithmEnum;

        // Will be usefull when the function is SurvivorShipAlgorithmEnum.MOST_TRUSTED_SOURCE.
        String parameter;

        /**
         * Getter for survivorShipFunction.
         * 
         * @return the survivorShipFunction
         */
        public SurvivorShipAlgorithmEnum getSurvivorShipAlgoEnum() {
            return this.survivorShipAlgorithmEnum;
        }

        /**
         * Sets the survivorShipFunction.
         * 
         * @param survivorShipFunction the survivorShipFunction to set
         */
        public void setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum survivorShipFunction) {
            this.survivorShipAlgorithmEnum = survivorShipFunction;
        }

        /**
         * Getter for parameter.
         * 
         * @return the parameter
         */
        public String getParameter() {
            return this.parameter;
        }

        /**
         * Sets the parameter.
         * 
         * @param parameter the parameter to set
         */
        public void setParameter(String parameter) {
            this.parameter = parameter;
        }

        /**
         * Getter for survivorShipKey.
         * 
         * @return the survivorShipKey
         */
        public String getSurvivorShipKey() {
            return this.survivorShipKey;
        }

        /**
         * Sets the survivorShipKey.
         * 
         * @param survivorShipKey the survivorShipKey to set
         */
        public void setSurvivorShipKey(String survivorShipKey) {
            this.survivorShipKey = survivorShipKey;
        }

    }
}
