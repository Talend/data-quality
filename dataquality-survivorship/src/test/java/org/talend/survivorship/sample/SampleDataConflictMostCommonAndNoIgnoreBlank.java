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
package org.talend.survivorship.sample;

import org.talend.survivorship.model.RuleDefinition;
import org.talend.survivorship.model.RuleDefinition.Function;
import org.talend.survivorship.model.RuleDefinition.Order;

public class SampleDataConflictMostCommonAndNoIgnoreBlank {

    public static final String PKG_NAME_CONFLICT_FRE_LONG_RECENT_WITHOUT_IGNORE_BLANK = "org.talend.survivorship.conflict.fre_long_recent_without_ignore_blank"; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_FRE_LONG_RECENT_NO_IGNORE_BLANK = {
            new RuleDefinition(Order.SEQ, "more_common_firstName", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "firstName", false) }; //$NON-NLS-1$

}
