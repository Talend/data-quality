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
package org.talend.dataquality.record.linkage.utils;

import org.talend.dataquality.record.linkage.Messages;

/**
 * some fixed string: Strings which not BE EXTERNALIZED are used only in code, not on UI.
 */
public class MatchAnalysisConstant {

    // general need
    public static final String COLUMN = "MatchAnalysisConstant.Column"; //$NON-NLS-1$

    public static final String LABEL = "MatchAnalysisConstant.Label"; //$NON-NLS-1$

    public static final String COUNT = "MatchAnalysisConstant.Count"; //$NON-NLS-1$

    // this field need not to do internationalization
    public static final String PERCENTAGE = "%"; //$NON-NLS-1$

    public static final String GROUP_SIZE = "MatchAnalysisConstant.Group_Size"; //$NON-NLS-1$

    public static final String GROUP_COUNT = "MatchAnalysisConstant.Group_Count"; //$NON-NLS-1$

    public static final String RECORD_COUNT = "MatchAnalysisConstant.Record_Count"; //$NON-NLS-1$

    public static final String RECORDS_PERCENTAGE = "MatchAnalysisConstant.Records_Percentage"; //$NON-NLS-1$

    public static final String ISDIRTY_PROPERTY = "ISDIRTY_PROPERTY"; //$NON-NLS-1$

    public static final String NEED_REFRESH_DATA = "Need_Refresh_Data"; //$NON-NLS-1$

    public static final String HIDE_GROUPS = "HIDE_GROUPS"; //$NON-NLS-1$

    // match key need
    public static final String MATCH_KEY_NAME = "MatchAnalysisConstant.MATCH_KEY_NAME"; //$NON-NLS-1$

    public static final String INPUT_COLUMN = "MatchAnalysisConstant.INPUT_COLUMN"; //$NON-NLS-1$ 

    public static final String MATCHING_TYPE = "MatchAnalysisConstant.MATCHING_TYPE"; //$NON-NLS-1$

    public static final String CUSTOM_MATCHER = "MatchAnalysisConstant.CUSTOM_MATCHER"; //$NON-NLS-1$

    public static final String CONFIDENCE_WEIGHT = "MatchAnalysisConstant.CONFIDENCE_WEIGHT"; //$NON-NLS-1$

    public static final String HANDLE_NULL = "MatchAnalysisConstant.HANDLE_NULL"; //$NON-NLS-1$

    public static final String MATCH_RULE_TABLE_COMPOSITE = "MATCH_RULE_TABLE_COMPOSITE"; //$NON-NLS-1$

    public static final String THRESHOLD = "MatchAnalysisConstant.THRESHOLD"; //$NON-NLS-1$

    public static final String TOKENIZATION_TYPE = "MatchAnalysisConstant.TOKENIZATION_TYPE";// the value should keep same with IRecordGrouping.TOKENIZATION_TYPE; //$NON-NLS-1$

    // block key need
    public static final String BLOCKING_KEY_NAME = "MatchAnalysisConstant.BLOCKING_KEY_NAME"; //$NON-NLS-1$

    public static final String PRECOLUMN = "MatchAnalysisConstant.PRECOLUMN"; //$NON-NLS-1$ 

    public static final String PRE_ALGO = "MatchAnalysisConstant.PRE_ALGO"; //$NON-NLS-1$

    public static final String KEY_ALGO = "MatchAnalysisConstant.KEY_ALGO"; //$NON-NLS-1$

    public static final String POST_ALGO = "MatchAnalysisConstant.POST_ALGO"; //$NON-NLS-1$

    public static final String PRE_VALUE = "MatchAnalysisConstant.PRE_VALUE"; //$NON-NLS-1$

    public static final String KEY_VALUE = "MatchAnalysisConstant.KEY_VALUE"; //$NON-NLS-1$

    public static final String POST_VALUE = "MatchAnalysisConstant.POST_VALUE"; //$NON-NLS-1$

    public static final String GID = "GID"; //$NON-NLS-1$

    public static final String MASTER = "MASTER"; //$NON-NLS-1$

    public static final String GRP_SIZE = "GRP_SIZE"; //$NON-NLS-1$

    public static final String SCORE = "SCORE"; //$NON-NLS-1$

    public static final String GRP_QUALITY = "GRP_QUALITY"; //$NON-NLS-1$

    public static final String ATTRIBUTE_SCORES = "ATTRIBUTE_SCORES"; //$NON-NLS-1$

    public static final String BLOCK_KEY = "BLOCK_KEY"; //$NON-NLS-1$

    public static final String MATCHING_KEY_SECTION_NAME = Messages.getString("MatchAnalysisConstant.MATCHING_KEY_SECTION_NAME"); //$NON-NLS-1$

    public static final String BlOCKING_KEY_SECTION_NAME = Messages.getString("MatchAnalysisConstant.BlOCKING_KEY_SECTION_NAME"); //$NON-NLS-1$

    // match Rule definition

    public static final String BlOCKING_KEY_DEFINITION_SECTION_NAME = Messages
            .getString("MatchAnalysisConstant.BlOCKING_KEY_DEFINITION_SECTION_NAME"); //$NON-NLS-1$

    public static final String MATCHING_KEY_DEFINITION_SECTION_NAME = Messages
            .getString("MatchAnalysisConstant.MATCHING_KEY_DEFINITION_SECTION_NAME"); //$NON-NLS-1$

    // survivorship
    public static final String SURVIVORSHIP_KEY_NAME = "MatchAnalysisConstant.SurvivorshipRuleName"; //$NON-NLS-1$

    public static final String FUNCTION = "MatchAnalysisConstant.Function"; //$NON-NLS-1$

    public static final String PARAMETER = "MatchAnalysisConstant.Parameter"; //$NON-NLS-1$

    public static final String ALLOW_MANUAL_RESOLUTION = "MatchAnalysisConstant.AllowManualResolution"; //$NON-NLS-1$

    public static final String DATA_TYPE = "MatchAnalysisConstant.DataType"; //$NON-NLS-1$

    public static final String SURVIVIORSHIP_DEFINITION_SECTION_NAME = Messages
            .getString("MatchAnalysisConstant.SURVIVIORSHIP_DEFINITION_SECTION_NAME"); //$NON-NLS-1$

    public static final String SURVIVIORSHIP_DEFAULT_DEFINITION_SECTION_NAME = Messages
            .getString("MatchAnalysisConstant.SURVIVIORSHIP_DEFAULT_DEFINITION_SECTION_NAME"); //$NON-NLS-1$

    public static final String SURVIVORSHIP_KEY_DEFAULT_VALUE = Messages.getString("MatchAnalysisConstant.KEY_NAME"); //$NON-NLS-1$

    public static final String MATCH_RULE_TAB_SWITCH = "MATCH_RULE_TAB_SWITCH"; //$NON-NLS-1$

    public static final String DATA_SAMPLE_TABLE_COLUMN_SELECTION = "DATA_SAMPLE_TABLE_COLUMN_SELECTION"; //$NON-NLS-1$

    public static final String MATCHING_KEY_AND_SURVIVOR_DEFINITION_SECTION_NAME = Messages
            .getString("MatchAnalysisConstant.MATCHING_KEY_AND_SURVIVOR_DEFINITION_SECTION_NAME"); //$NON-NLS-1$

    private MatchAnalysisConstant() {

    }

}
