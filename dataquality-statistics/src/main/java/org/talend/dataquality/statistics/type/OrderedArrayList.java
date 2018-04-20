package org.talend.dataquality.statistics.type;// ============================================================================

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

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.tuple.MutablePair;

public class OrderedArrayList<K> extends ArrayList<MutablePair<K, Integer>> {

    public void addAndIncrement(K e) {
        int i = 0;
        for (; i < size(); i++)
            if (e.equals(get(i).getLeft()))
                break;
        if (i < size()) //found
            increment(i);
        else //not found
            add(MutablePair.of(e, 0));
    }

    public void increment(int index) {
        int newFrequency = get(index).getRight() + 1;
        get(index).setRight(newFrequency);
        int i = index - 1;
        for (; i >= 0; i--)
            if (get(i).getRight() >= newFrequency)
                break;
        if (i + 1 != index) //we have to move the value
            Collections.swap(this, i + 1, index);
    }

}