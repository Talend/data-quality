// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;

/**
 * This class links a frequency to an object K.
 * The first elements are the most frequent.
 * When you increment the frequency of a value, it will keep the list sorted.
 *
 * @param <K>
 */
public class SortedList<K> {

    private final List<MutablePair<K, Integer>> list = new ArrayList<>();

    /**
     * Add a new value to the list.
     * Careful not to add an existing value.
     * @param value to add
     */
    public void add(K value) {
        list.add(MutablePair.of(value, 0));
    }

    public K get(int index) {
        return list.get(index).getLeft();
    }

    public int size() {
        return list.size();
    }

    /**
     * Increment the frequency of the value at this index.
     * And reorder the list.
     * @param foundIndex of the value to increment
     */
    public void increment(int foundIndex) {
        //Increment the frequency
        int newFrequency = list.get(foundIndex).getRight() + 1;
        list.get(foundIndex).setRight(newFrequency);

        //Find the index where the value should be
        int currentIndex = foundIndex - 1;
        while (currentIndex >= 0 && list.get(currentIndex).getRight() < newFrequency)
            currentIndex--;

        //Move the value to the right index
        //Use swap is ok since the swapped frequency difference is always 1
        if (currentIndex + 1 != foundIndex)
            Collections.swap(list, currentIndex + 1, foundIndex);
    }
}