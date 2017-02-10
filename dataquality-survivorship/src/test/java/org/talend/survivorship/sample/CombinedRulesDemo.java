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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.talend.survivorship.SurvivorshipManager;
import org.talend.survivorship.model.RuleDefinition;

/**
 * This demo shows how to use the survivorship API in the component.
 */
public class CombinedRulesDemo {

    private static SurvivorshipManager manager;

    public static void main(String[] args) {
        run(CombinedRulesTestData.RULES_1, CombinedRulesTestData.SAMPLE_INPUT_1);
        run(CombinedRulesTestData.RULES_2, CombinedRulesTestData.SAMPLE_INPUT_1);
        run(CombinedRulesTestData.RULES_1, CombinedRulesTestData.SAMPLE_INPUT_2);
        run(CombinedRulesTestData.RULES_2, CombinedRulesTestData.SAMPLE_INPUT_2);
    }

    public static void run(RuleDefinition[] ruleSet, Object[][] sampleInput) {
        // 1. Instantiate <code>SurvivorshipManager</code>.
        manager = new SurvivorshipManager(CombinedRulesTestData.RULE_PATH, CombinedRulesTestData.PKG_NAME);

        // 2. Add column informations and rule definitions.
        for (String str : CombinedRulesTestData.COLUMNS.keySet()) {
            manager.addColumn(str, CombinedRulesTestData.COLUMNS.get(str));
        }
        for (int i = 0; i < ruleSet.length; i++) {
            manager.addRuleDefinition(ruleSet[i]);
        }

        // 3. Initialize <code>KnowlegeBase</code>.
        manager.initKnowledgeBase();

        // 4. Run a new session for each group to merge.
        manager.runSession(sampleInput);

        // 5. Retrieve results
        List<HashSet<String>> conflicts = manager.getConflictList();

        for (int i = 0; i < sampleInput.length; i++) {
            System.out.print("Record " + i + " -> " + Arrays.asList(sampleInput[i]));
            System.out.println(conflicts.get(i));
        }

        Map<String, Object> survivors = manager.getSurvivorMap();
        System.out.println("\nSurvivors:");
        for (String col : CombinedRulesTestData.COLUMNS.keySet()) {
            if (survivors.get(col) != null) {
                System.out.println(col + " -> " + survivors.get(col));
            }
        }
        System.out.println("CONFLICTS -> " + manager.getConflictsOfSurvivor());

    }
}
