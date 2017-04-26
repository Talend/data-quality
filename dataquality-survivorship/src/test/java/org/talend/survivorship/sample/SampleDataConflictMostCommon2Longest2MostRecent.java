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

import org.talend.survivorship.model.ConflictRuleDefinition;
import org.talend.survivorship.model.RuleDefinition;
import org.talend.survivorship.model.RuleDefinition.Function;
import org.talend.survivorship.model.RuleDefinition.Order;

public class SampleDataConflictMostCommon2Longest2MostRecent {

    public static final String PKG_NAME_CONFLICT_FRE_LONG_RECENT = "org.talend.survivorship.conflict.fre_long_recent"; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_FRE_LONG_RECENT = {
            new RuleDefinition(Order.SEQ, "more_common_firstName", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "firstName", true) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE = {
            new ConflictRuleDefinition(Order.SEQ, "longest_firstName", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Longest, null, "firstName", false, null, false), //$NON-NLS-1$
            new ConflictRuleDefinition(Order.SEQ, "more_recent_birthday", "birthday", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostRecent, null, "firstName", false, null, false) }; //$NON-NLS-1$

}
