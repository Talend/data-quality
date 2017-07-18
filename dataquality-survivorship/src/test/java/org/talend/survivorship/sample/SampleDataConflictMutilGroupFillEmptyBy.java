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

public class SampleDataConflictMutilGroupFillEmptyBy {

    public static final String PKG_NAME_CONFLICT = "org.talend.survivorship.conflict.fill_empty_by_notIgnoreBlank"; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT = { new RuleDefinition(Order.SEQ, "get FirstName", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
            Function.MostCommon, null, "firstName", false) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE = {
            new ConflictRuleDefinition(Order.CR, "CR1", "lastName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.FillEmpty, null, "firstName", false, null, false), //$NON-NLS-1$
            new ConflictRuleDefinition(Order.CR, "CR2", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "firstName", false, null, false) }; //$NON-NLS-1$
}
