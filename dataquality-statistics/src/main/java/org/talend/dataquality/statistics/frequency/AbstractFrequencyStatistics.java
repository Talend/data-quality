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
package org.talend.dataquality.statistics.frequency;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Frequency statistics bean which delegate the computation to evaluator.
 * 
 * @author mzhao
 *
 */
public abstract class AbstractFrequencyStatistics {

    private Map<String, Long> value2freq = new HashMap<>();

    public void add(String value) {
        Long freq = value2freq.get(value);
        if (freq == null) {
            freq = 0l;
        }
        value2freq.put(value, freq + 1);
    }

    public Map<String, Long> getTopK(int topk) {
        return value2freq.entrySet().stream().sorted(Map.Entry.<String, Long> comparingByValue().reversed()).limit(topk)
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (v1, v2) -> v2, LinkedHashMap::new));
    }

    public long getFrequency(String item) {
        return value2freq.get(item);
    }
}
