/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.dataquality.matchmerge.mfb.mdm.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;
import org.talend.dataquality.record.linkage.utils.CustomAttributeMatcherClassNameConvert;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

@SuppressWarnings("nls")
public interface MatchMergeRule {

    Map<FieldMetadata, Declaration> getDeclarations();

    List<FieldMetadata> getMatchFields();

    List<FieldMetadata> getTaskFields();

    SurvivorShipAlgorithmEnum[] getMergeAlgorithms();

    float[] getThresholds();

    AttributeMatcherType[] getMatchAlgorithms();

    IAttributeMatcher.NullOption[] getHandleNulls();

    double[] getConfidenceWeights();

    /**
     * @param field The field for which the default merge applies (default merge may depend on the field's type).
     * @return The default merge strategy to apply for fields outside match & merge configuration.
     */
    SurvivorShipAlgorithmEnum getDefaultMergeAlgorithm(FieldMetadata field);

    String getDefaultMergeParameter(FieldMetadata field);

    SurvivorShipAlgorithmEnum getParticularDefaultSurvivorShipAlgorithm(FieldMetadata field);

    String getParticularDefaultSurvivorShipParameter(FieldMetadata field);

    SubString[] getMatchSubStrings();

    boolean[] getAllowManualResolutions();

    double getConfidentMatchThreshold();

    double getMatchThreshold();

    String[] getMergeParameters();

    String[] getMatchCustomJarPaths();

    String[] getMatchCustomClassNames();

    TokenizedResolutionMethod[] getTokenMethods();

    RecordMatcherType getRecordLinkageAlgorithm();

    public static class Declaration {

        public double confidenceWeight;

        public IAttributeMatcher.NullOption handleNull;

        public float threshold;

        public TokenizedResolutionMethod tokenMethod;

        public AttributeMatcherType matchAlgorithm;

        public String matchParameter;

        public SurvivorShipAlgorithmEnum mergeAlgorithm;

        public String mergeParameter;

        public boolean allowManualResolution;

        public Declaration() {
            this(AttributeMatcherType.JARO_WINKLER, SurvivorShipAlgorithmEnum.MOST_COMMON);
        }

        public Declaration(AttributeMatcherType matchAlgorithm, SurvivorShipAlgorithmEnum mergeAlgorithm) {
            this(1, IAttributeMatcher.NullOption.nullMatchAll, 0.85f, TokenizedResolutionMethod.NO, matchAlgorithm,
                    StringUtils.EMPTY, mergeAlgorithm, StringUtils.EMPTY, true);
        }

        public Declaration(double confidenceWeight, IAttributeMatcher.NullOption handleNull, float threshold,
                TokenizedResolutionMethod tokenMethod, AttributeMatcherType matchAlgorithm, String matchParameter,
                SurvivorShipAlgorithmEnum mergeAlgorithm, String mergeParameter, boolean allowManualResolution) {
            this.confidenceWeight = confidenceWeight;
            this.handleNull = handleNull;
            this.threshold = threshold;
            this.tokenMethod = tokenMethod;
            this.matchAlgorithm = matchAlgorithm;
            this.matchParameter = matchParameter;
            this.mergeAlgorithm = mergeAlgorithm;
            this.mergeParameter = mergeParameter;
            this.allowManualResolution = allowManualResolution;
        }

        public String getMatchCustomJarPath() {
            if (matchAlgorithm == AttributeMatcherType.CUSTOM && StringUtils.isNotBlank(matchParameter)) {
                return StringUtils.substringBeforeLast(matchParameter, "||");
            } else {
                return StringUtils.EMPTY;
            }
        }

        public String getMatchCustomClassName() {
            if (matchAlgorithm == AttributeMatcherType.CUSTOM && StringUtils.isNotBlank(matchParameter)) {
                return CustomAttributeMatcherClassNameConvert.getClassName(matchParameter);
            } else {
                return matchParameter;
            }
        }

    }
}