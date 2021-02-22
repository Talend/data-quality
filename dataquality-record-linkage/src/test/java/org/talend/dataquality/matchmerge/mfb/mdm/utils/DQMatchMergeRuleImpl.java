// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.matchmerge.mfb.mdm.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher.NullOption;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

public class DQMatchMergeRuleImpl implements MatchMergeRule {

    protected final List<FieldMetadata> matchFields = new LinkedList<FieldMetadata>();

    @SuppressWarnings("unchecked")
    private final Map<FieldMetadata, Declaration> declarations = new ListOrderedMap();

    private double matchThreshlod = 0d;

    @Override
    public Map<FieldMetadata, Declaration> getDeclarations() {
        return declarations;
    }

    @Override
    public List<FieldMetadata> getMatchFields() {
        List<FieldMetadata> matchFields = new ArrayList<FieldMetadata>(declarations.size());
        for (FieldMetadata field : declarations.keySet()) {
            matchFields.add(field);
        }
        return matchFields;
    }

    @Override
    public List<FieldMetadata> getTaskFields() {
        return new ArrayList<FieldMetadata>();
    }

    @Override
    public SurvivorShipAlgorithmEnum[] getMergeAlgorithms() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float[] getThresholds() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AttributeMatcherType[] getMatchAlgorithms() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NullOption[] getHandleNulls() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double[] getConfidenceWeights() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SurvivorShipAlgorithmEnum getDefaultMergeAlgorithm(FieldMetadata field) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDefaultMergeParameter(FieldMetadata field) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SurvivorShipAlgorithmEnum getParticularDefaultSurvivorShipAlgorithm(FieldMetadata field) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getParticularDefaultSurvivorShipParameter(FieldMetadata field) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SubString[] getMatchSubStrings() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean[] getAllowManualResolutions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getConfidentMatchThreshold() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getMatchThreshold() {
        return matchThreshlod;
    }

    public void setMatchThreshold(double matchThreshlod) {
        this.matchThreshlod = matchThreshlod;
    }

    @Override
    public String[] getMergeParameters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getMatchCustomJarPaths() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getMatchCustomClassNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TokenizedResolutionMethod[] getTokenMethods() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RecordMatcherType getRecordLinkageAlgorithm() {
        return RecordMatcherType.T_SwooshAlgorithm;
    }

}
