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

/**
 * DOC zshen class global comment. Detailled comment
 */
public class SampleDataConflictCheckRule {

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE = {
            new ConflictRuleDefinition(Order.SEQ, "longest_city1", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MappingTo, null, "city1", false, null, false), //$NON-NLS-1$
            new ConflictRuleDefinition(Order.SEQ, "longest_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Longest, null, "lastName", false, null, true) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE_CASE4 = {
            new ConflictRuleDefinition(Order.SEQ, "longest_city1", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MappingTo, null, "city1", false, null, false), //$NON-NLS-1$
            new ConflictRuleDefinition(Order.SEQ, "longest_city2", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MappingTo, null, "city2", false, null, true), //$NON-NLS-1$
            new ConflictRuleDefinition(Order.SEQ, "longest_city2", "city2", //$NON-NLS-1$
                    Function.MappingTo, null, "id", false, null, true) }; //$NON-NLS-1$

    public static final RuleDefinition[] RULES = {
            new RuleDefinition(Order.SEQ, "more_common_city1", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "city1", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "more_common_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "city2", false) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE_CASE2 = {
            new ConflictRuleDefinition(Order.SEQ, "longest_city1", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MappingTo, null, "city2", false, null, false), //$NON-NLS-1$
            new ConflictRuleDefinition(Order.SEQ, "longest_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MappingTo, null, "city1", false, null, true) }; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CASE3 = {
            new RuleDefinition(Order.SEQ, "more_common_city1", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "city1", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "more_common_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "city2", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "more_common_city2", "lastName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "lastName", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "more_common_city2", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "firstName", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "more_common_city2", "middleName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "middleName", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "more_common_city2", "id", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "id", false) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE_CASE3 = {
            new ConflictRuleDefinition(Order.SEQ, "longest_city1", "city1", Function.MappingTo, null, "city2", false, null, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                    false),
            new ConflictRuleDefinition(Order.SEQ, "longest_city1", "minddleName", Function.MappingTo, null, "city2", false, null, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                    false),
            new ConflictRuleDefinition(Order.SEQ, "longest_city2", "id", Function.MappingTo, null, "city1", false, null, true), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new ConflictRuleDefinition(Order.SEQ, "longest_city2", "firstName", Function.MappingTo, null, "city1", false, null, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    true),
            new ConflictRuleDefinition(Order.SEQ, "longest_city2", "city2", Function.MappingTo, null, "id", false, null, true), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new ConflictRuleDefinition(Order.SEQ, "longest_city2", "lastName", Function.MappingTo, null, "id", false, null, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                    true) };

}
