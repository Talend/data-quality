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

public class SampleDataConflictTwoNoConflictColumnGetOneSameSurvivedValue {

    public static final String PKG_NAME_CONFLICT_TWO_TARGET_ONE_COLUMN = "org.talend.survivorship.conflict.two_target_one_column"; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_TWO_TARGET_ONE_COLUMN = {
            new RuleDefinition(Order.SEQ, "more_recent_birthday", "birthday", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostRecent, null, "birthday", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "Longest_birthday", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Longest, null, "birthday", false) }; //$NON-NLS-1$
}
