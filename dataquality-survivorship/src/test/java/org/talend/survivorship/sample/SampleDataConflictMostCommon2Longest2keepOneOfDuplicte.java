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
package org.talend.survivorship.sample;

import org.talend.survivorship.model.ConflictRuleDefinition;
import org.talend.survivorship.model.DefFunParameter;
import org.talend.survivorship.model.RuleDefinition;
import org.talend.survivorship.model.RuleDefinition.Function;
import org.talend.survivorship.model.RuleDefinition.Order;

public class SampleDataConflictMostCommon2Longest2keepOneOfDuplicte {

    public static final String PKG_NAME_CONFLICT_TWO_TARGET_SAME_VALUE =
            "org.talend.survivorship.conflict.two_target_same_value"; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_TWO_TARGET_SAME_RESULT = {
            new RuleDefinition(Order.SEQ, "more_common_city1", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "city1", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "more_common_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "city2", false) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE = {
            new ConflictRuleDefinition(new DefFunParameter("city1", Function.Longest, null, "city1", null), Order.SEQ, //$NON-NLS-1$//$NON-NLS-2$
                    "longest_city1", false, false, 0),
            new ConflictRuleDefinition(new DefFunParameter("city1", Function.RemoveDuplicate, null, "city2", null), //$NON-NLS-1$//$NON-NLS-2$
                    Order.SEQ, "longest_city2", false, false, 1),
            new ConflictRuleDefinition(new DefFunParameter("city2", Function.Longest, null, "city2", null), Order.SEQ, //$NON-NLS-1$//$NON-NLS-2$
                    "longest_city2", false, false, 2) };

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE2 = {
            new ConflictRuleDefinition(new DefFunParameter("city1", Function.Shortest, null, "city1", null), Order.SEQ, //$NON-NLS-1$//$NON-NLS-2$
                    "longest_city1", false, false, 0),
            new ConflictRuleDefinition(new DefFunParameter("city1", Function.RemoveDuplicate, null, "city2", null), //$NON-NLS-1$//$NON-NLS-2$
                    Order.SEQ, "longest_city2", false, false, 1),
            new ConflictRuleDefinition(new DefFunParameter("city2", Function.Shortest, null, "city2", null), Order.SEQ, //$NON-NLS-1$//$NON-NLS-2$
                    "longest_city2", false, false, 2) };
}
