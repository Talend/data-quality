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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.talend.survivorship.model.RuleDefinition;
import org.talend.survivorship.model.RuleDefinition.Function;
import org.talend.survivorship.model.RuleDefinition.Order;

public class SampleDataRegexFunction {

    public static final String PKG_NAME = "org.talend.survivorship.sample.regexFunction"; //$NON-NLS-1$

    public static final Object[][] SAMPLE_INPUT = { { "Lili" }, //$NON-NLS-1$ 
            { "Tony" }, //$NON-NLS-1$ 
            { "Tom" }, //$NON-NLS-1$ 
            { "li" } }; //$NON-NLS-1$ 

    public static final LinkedHashMap<String, String> COLUMNS = new LinkedHashMap<String, String>() {

        private static final long serialVersionUID = 1L;
        {
            put("firstName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    };

    public static final RuleDefinition[] RULES = {
            new RuleDefinition(Order.SEQ, "firstNameRule", "firstName", Function.MatchRegex, "^\\\\w{2}$", "firstName", true) }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    public static final HashMap<String, Object> EXPECTED_SURVIVOR = new HashMap<String, Object>() {

        private static final long serialVersionUID = 1L;
        {
            put("firstName", "li"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    };

    public static final HashSet<String> EXPECTED_CONFLICT_OF_SURVIVOR = new HashSet<String>() {

        private static final long serialVersionUID = 1L;
        {
        }
    };
}
