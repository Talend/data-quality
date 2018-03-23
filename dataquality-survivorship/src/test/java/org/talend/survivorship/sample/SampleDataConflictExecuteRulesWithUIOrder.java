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

public class SampleDataConflictExecuteRulesWithUIOrder {

    public static final String PKG_NAME_CONFLICT = "org.talend.survivorship.conflict.execute_with_ui_order"; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT = {
            new RuleDefinition(Order.SEQ, "more_common_city1", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "city1", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "more_common_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "city2", false) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE_SECOND_LONGEST_INVALID = {
            new ConflictRuleDefinition(Order.SEQ, "longest_city1", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Longest, null, "city1", false, null, false, 0), //$NON-NLS-1$
            new ConflictRuleDefinition(Order.SEQ, "revmove_duplicate_city2", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.RemoveDuplicate, null, "city2", false, null, false, 1), //$NON-NLS-1$
            new ConflictRuleDefinition(Order.SEQ, "longest_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Longest, null, "city2", false, null, false, 2) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE_REMOVE_DUPLICATE_INVALID = {
            new ConflictRuleDefinition(Order.SEQ, "revmove_duplicate_city2", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.RemoveDuplicate, null, "city2", false, null, false, 0), //$NON-NLS-1$
            new ConflictRuleDefinition(Order.SEQ, "longest_city1", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Longest, null, "city1", false, null, false, 1), //$NON-NLS-1$
            new ConflictRuleDefinition(Order.SEQ, "shortest_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Shortest, null, "city2", false, null, false, 2), }; //$NON-NLS-1$          

}
