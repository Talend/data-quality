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

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.tuple.MutablePair;

/**
 * This class links a frequency to an object K.
 * The first elements are the most frequent.
 * When you increment the frequency of a value, it will keep the list sorted.
 *
 * @param <K>
 */
public class SortedList<K> extends ArrayList<MutablePair<K, Integer>> {

    /**
     * Add a new value to the list.
     * Careful not to add an existing value.
     * @param value to add
     */
    public boolean addNewValue(K value) {
        return add(MutablePair.of(value, 0));
    }

    /**
     * Increment the frequency of the value at this index.
     * And reorder the list.
     * @param foundIndex of the value to increment
     */
    public void increment(int foundIndex) {
        //increment the frequency
        int newFrequency = get(foundIndex).getRight() + 1;
        get(foundIndex).setRight(newFrequency);

        //Find the index where the value should be
        int currentIndex = foundIndex - 1;
        while (currentIndex >= 0 && get(currentIndex).getRight() < newFrequency)
            currentIndex--;

        //Move the value to the right index
        //Use swap is ok since the swapped frequency difference is always 1
        if (currentIndex + 1 != foundIndex)
            Collections.swap(this, currentIndex + 1, foundIndex);
    }
}