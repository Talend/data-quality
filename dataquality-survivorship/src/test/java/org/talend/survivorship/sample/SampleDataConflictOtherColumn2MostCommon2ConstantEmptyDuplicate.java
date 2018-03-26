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
import org.talend.survivorship.model.DefFunParameter;
import org.talend.survivorship.model.RuleDefinition;
import org.talend.survivorship.model.RuleDefinition.Function;
import org.talend.survivorship.model.RuleDefinition.Order;

public class SampleDataConflictOtherColumn2MostCommon2ConstantEmptyDuplicate {

    public static final String PKG_NAME = "org.talend.survivorship.conflict.fre_other_fill_dup_sur"; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT = {
            new RuleDefinition(Order.SEQ, "most_common_firstName", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "firstName", true), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "most_common_lastName", "lastName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "lastName", false) }; //$NON-NLS-1$

    public static final ConflictRuleDefinition[] RULES_CONFLICT_RESOLVE = {
            new ConflictRuleDefinition(new DefFunParameter("city2", Function.Shortest, null, "firstName", null), Order.SEQ, //$NON-NLS-1$//$NON-NLS-2$
                    "shortest_city2", false, false, 0),
            new ConflictRuleDefinition(
                    new DefFunParameter("lastName", Function.ExcludeValues, "Green,Blue", "lastName", "firstName"), Order.SEQ, //$NON-NLS-1$//$NON-NLS-2$
                    "exclusiveness_lastName", false, true, 1) };

}
