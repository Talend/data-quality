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
package org.talend.dataquality.record.linkage.analyzer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Holds all the clusters for a column.
 */
public class StringClusters implements Iterable<StringClusters.StringCluster> {

    private final Set<StringCluster> allClusters = new HashSet<StringCluster>();

    public void addCluster(StringCluster cluster) {
        allClusters.add(cluster);
    }

    @Override
    public Iterator<StringCluster> iterator() {
        return allClusters.iterator();
    }

    public static class StringCluster {

        public String survivedValue;

        public String[] originalValues;

    }
}
