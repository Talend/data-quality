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

/**
 * This demo shows how to use the survivorship API in the component.
 */
public class ComponentRuntimeDemo {

    private static SurvivorshipManager manager;

    public static void main(String[] args) {

        // 1. Instantiate <code>SurvivorshipManager</code>.
        manager = new SurvivorshipManager(SampleData.RULE_PATH, SampleData.PKG_NAME);

        // 2. Add column informations and rule definitions.
        for (String str : SampleData.COLUMNS.keySet()) {
            manager.addColumn(str, SampleData.COLUMNS.get(str));
        }
        for (int i = 0; i < SampleData.RULES.length; i++) {
            manager.addRuleDefinition(SampleData.RULES[i]);
        }

        // 3. Initialize <code>KnowlegeBase</code>.
        manager.initKnowledgeBase();

        // 4. Run a new session for each group to merge.
        manager.runSession(SampleData.SAMPLE_INPUT);

        // 5. Retrieve results
        List<HashSet<String>> conflicts = manager.getConflictList();

        for (int i = 0; i < SampleData.SAMPLE_INPUT.length; i++) {
            System.out.print("Record " + i + " -> " + Arrays.asList(SampleData.SAMPLE_INPUT[i]));
            System.out.println(conflicts.get(i));
        }

        Map<String, Object> survivors = manager.getSurvivorMap();
        System.out.println("\nSurvivors:");
        for (String col : SampleData.COLUMNS.keySet()) {
            if (survivors.get(col) != null) {
                System.out.println(col + " -> " + survivors.get(col));
            }
        }
        System.out.println("CONFLICTS -> " + manager.getConflictsOfSurvivor());

    }
}
