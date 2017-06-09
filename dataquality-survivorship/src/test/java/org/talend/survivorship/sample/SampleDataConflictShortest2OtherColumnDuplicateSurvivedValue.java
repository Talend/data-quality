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

public class SampleDataConflictShortest2OtherColumnDuplicateSurvivedValue {

    public static final String PKG_NAME_CONFLICT_TWO_TARGET_SAME_RESULT_REFERENCE_COLUMN = "org.talend.survivorship.conflict.short_reference_column"; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_TWO_TARGET_SAME_RESULT_REFERENCE_COLUMN = {
            new RuleDefinition(Order.SEQ, "shortest_city1", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Shortest, null, "city1", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "shortest_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$

                    Function.Shortest, null, "city2", false) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE = {
            new ConflictRuleDefinition(Order.SEQ, "city1_removeDup_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.RemoveDuplicate, null, "city1", false, null, false), //$NON-NLS-1$
            new ConflictRuleDefinition(Order.SEQ, "city1_mappingTo_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.SurviveAs, null, "city1", false, null, false) }; //$NON-NLS-1$

}
