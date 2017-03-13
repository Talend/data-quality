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

/**
 * DOC zshen class global comment. Detailled comment
 */
public class SwooshConstants {

    /**
     * The score which current attribute
     */
    public static final String ATTRIBUTE_SCORES = "Attribute scores"; //$NON-NLS-1$

    /**
     * The score which current group
     */
    public static final String GROUP_QUALITY = "Group quality"; //$NON-NLS-1$

    /**
     * The score
     */
    public static final String SCORE2 = "Score"; //$NON-NLS-1$

    /**
     * Show whether current attribute is master
     */
    public static final String IS_MASTER = "Is master"; //$NON-NLS-1$

    /**
     * Show the size of current group
     */
    public static final String GROUP_SIZE = "Group size"; //$NON-NLS-1$

    /**
     * Show the ID of group
     */
    public static final String GID = "GID"; //$NON-NLS-1$

    /**
     * Defualt value when current attribute is not a master
     */
    public static final String SUB_ITEM_GROUP_QUALITY_DEFAULT_VALUE = "0.0"; //$NON-NLS-1$

    /**
     * Defualt value when current attribute not group
     */
    public static final String ALONE_ITEM_GROUP_QUALITY_DEFAULT_VALUE = "1.0"; //$NON-NLS-1$

    /**
     * One String which contents is "null" javajet alaways get this value if it is null variable
     */
    public static final String NULL_STR = "null"; //$NON-NLS-1$

    public static final String ORIGINAL_RECORD = "ORIGINAL_RECORD"; //$NON-NLS-1$

    private SwooshConstants() {

    }
}
