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

    /**
     * Suggests a type according to the specified integer threshold and a default <tt>typeThreshold</tt> set to 0.5.
     *
     * @param integerThreshold pecifies the minimum occurrence ratio (w.r.t. the number of occurrences of numerical
     *                         values which exceeds the <tt>typeThreshold</tt>) before integer type is returned as suggested type
     * @return the suggested type
     */
    @Deprecated
    public DataTypeEnum getSuggestedType(double integerThreshold) {
        return getSuggestedType(0.5, integerThreshold);
    }

    /**
     * Suggests a type according to the so far listed types occurrences. This methods returns a type which is different
     * with <tt>Empty type</tt>. <tt>String type</tt> is the default type which is suggested when another type can not
     * be suggested. For instance, for following values, "", "", "", "", "1.2" DOUBLE type is returned.
     * <p>
     * </p>
     * Before a non-numerical type (all types but integer and double) is returned its ratio (according to the non empty
     * types) must be greater than <tt>typeThreshold</tt>. When the ratio (w.r.t. the number of occurrences of non empty
     * types) of numerical types is greater than <tt>typeThreshold</tt> then, the integer ratio ( according to the
     * number of occurrences of numerical types) must be greater than <tt>integerThreshold</tt>. If not the double type
     * is returned. <br/>
     * For instance, for following values "1.2","3.5","2","6","7" and with an <tt>integerThreshold</tt> of 0.55 the
     * suggested type will be INTEGER, whereas with an <tt>integerThreshold</tt> of 0.6 DOUBLE type is returned.
     *
     * @param typeThreshold    specifies the minimum occurrence ratio (w.r.t. the number of occurrences of non empty types)
     *                         reached by the suggested type (except for String type which is the default type).
     * @param integerThreshold specifies the minimum occurrence ratio (w.r.t. the number of occurrences of numerical
     *                         values which exceeds the <tt>typeThreshold</tt>) before integer type is returned as suggested type
     * @return the suggested type
     */

    @Deprecated
    public DataTypeEnum getSuggestedType(double typeThreshold, double integerThreshold) {
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
        Comparator<Map.Entry<DataTypeEnum, Long>> decreasingOccurrenceComparator = new Comparator<Map.Entry<DataTypeEnum, Long>>() {

            @Override
            public int compare(Map.Entry<DataTypeEnum, Long> o1, Map.Entry<DataTypeEnum, Long> o2) {
                return Long.compare(o2.getValue(), o1.getValue());
            }
        };
        // sort the non empty types by decreasing occurrences number
        Collections.sort(sortedTypeOccurrences, decreasingOccurrenceComparator);

        final double occurrenceThreshold = typeThreshold * count;

        // if non empty types exist
        if (count > 0) {
            final long mostFrequentTypeOccurrence = sortedTypeOccurrences.get(0).getValue();

            final long doubleOccurrences = typeOccurences.containsKey(DataTypeEnum.DOUBLE)
                    ? typeOccurences.get(DataTypeEnum.DOUBLE) : 0;
            final long integerOccurrences = typeOccurences.containsKey(DataTypeEnum.INTEGER)
                    ? typeOccurences.get(DataTypeEnum.INTEGER) : 0;

            final long numericalOccurrences = doubleOccurrences + integerOccurrences;

            // The column is numeric (both double and integer types) and the numerical occurrences is dominant
            if (numericalOccurrences > occurrenceThreshold && numericalOccurrences > mostFrequentTypeOccurrence) {
                if (integerOccurrences > integerThreshold * numericalOccurrences) {
                    return DataTypeEnum.INTEGER;
                } else {
                    return DataTypeEnum.DOUBLE;
                }
            } else { // otherwise
                final long secondMostFrequentTypeOccurrence = sortedTypeOccurrences.size() > 1
                        ? sortedTypeOccurrences.get(1).getValue() : 0;
                // return the most frequent type if it reaches the threshold and has strictly more occurrences than the
                // second most frequent types
                if (mostFrequentTypeOccurrence >= occurrenceThreshold
                        && mostFrequentTypeOccurrence != secondMostFrequentTypeOccurrence) {
                    return sortedTypeOccurrences.get(0).getKey();
                }
            }
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
