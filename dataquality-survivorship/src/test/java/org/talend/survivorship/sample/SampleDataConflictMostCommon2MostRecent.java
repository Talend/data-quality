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

public class SampleDataConflictMostCommon2MostRecent {

    public static final String PKG_NAME_CONFLICT = "org.talend.survivorship.conflict"; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT = { new RuleDefinition(Order.SEQ, "more_common_birthday", "birthday", //$NON-NLS-1$ //$NON-NLS-2$
            Function.MostCommon, null, "birthday", false) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE = {
            new ConflictRuleDefinition(Order.SEQ, "more_recent_birthday", "birthday", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostRecent, null, "birthday", false, null, false) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE_DISABLE = {
            new ConflictRuleDefinition(Order.SEQ, "more_recent_birthday", "birthday", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostRecent, null, "birthday", false, null, false, true) }; //$NON-NLS-1$

}
