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

public class SampleDataConflictMostCommon2OtherSurvivedValue {

    public static final String PKG_NAME_CONFLICT = "org.talend.survivorship.conflict.fre_other_survived"; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT = {
            new RuleDefinition(Order.SEQ, "short_firstName", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Shortest, null, "firstName", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "most_common_city1", "city1", //$NON-NLS-1$
                    Function.MostCommon, null, "city1", false) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE = {
            new ConflictRuleDefinition(Order.CR, "more_recent_birthday", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MappingTo, null, "city1", false, null, false) }; //$NON-NLS-1$
}
