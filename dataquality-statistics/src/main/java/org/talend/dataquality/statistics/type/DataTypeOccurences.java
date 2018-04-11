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
package org.talend.dataquality.statistics.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Data type bean hold type to frequency and type to value maps.
 */
public class DataTypeOccurences implements Serializable {

    private static final long serialVersionUID = -736825123668340428L;

    private Map<DataTypeEnum, Long> typeOccurences = new EnumMap<>(DataTypeEnum.class);

    public Map<DataTypeEnum, Long> getTypeFrequencies() {
        return typeOccurences;
    }

    /**
     * The default method to get a suggested type. <tt>typeThreshold</tt> is set to 0.5
     *
     * @return the most frequent type (return String if the most frequent type has a frequency lower than 0.5)
     */
    public DataTypeEnum getSuggestedType() {
        return getDominantType(0.5);
    }

    private DataTypeEnum getDominantType(double typeThreshold) {
        final List<Map.Entry<DataTypeEnum, Long>> sortedTypeOccurrences = new ArrayList<>();
        long count = 0;
        // retrieve the occurrences non empty types,
        for (Map.Entry<DataTypeEnum, Long> entry : typeOccurences.entrySet()) {
            final DataTypeEnum type = entry.getKey();
            if (!DataTypeEnum.EMPTY.equals(type)) {
                count += entry.getValue();
                sortedTypeOccurrences.add(entry);
            }
        }

        if (count != 0) {
            // --- Any integer is a double
            if (typeOccurences.containsKey(DataTypeEnum.DOUBLE) && typeOccurences.containsKey(DataTypeEnum.INTEGER)) {
                final long doubleOccurrences = typeOccurences.get(DataTypeEnum.DOUBLE) + typeOccurences.get(DataTypeEnum.INTEGER);
                typeOccurences.put(DataTypeEnum.DOUBLE, doubleOccurrences);
            }

            Comparator<Map.Entry<DataTypeEnum, Long>> decreasingOccurrenceComparator = new Comparator<Map.Entry<DataTypeEnum, Long>>() {

                @Override
                public int compare(Map.Entry<DataTypeEnum, Long> o1, Map.Entry<DataTypeEnum, Long> o2) {
                    return Long.compare(o2.getValue(), o1.getValue());
                }
            };
            // sort the non empty types by decreasing occurrences number
            Collections.sort(sortedTypeOccurrences, decreasingOccurrenceComparator);

            final double occurrenceThreshold = typeThreshold * count;
            if (sortedTypeOccurrences.get(0).getValue() >= occurrenceThreshold)
                return sortedTypeOccurrences.get(0).getKey();
        }
        // fallback to string as default choice
        return DataTypeEnum.STRING;
    }

    public void increment(DataTypeEnum type) {
        if (!typeOccurences.containsKey(type)) {
            typeOccurences.put(type, 1L);
        } else {
            typeOccurences.put(type, typeOccurences.get(type) + 1);
        }
    }

}
