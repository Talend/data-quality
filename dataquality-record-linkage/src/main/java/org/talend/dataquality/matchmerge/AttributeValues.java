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
package org.talend.dataquality.matchmerge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang.ObjectUtils;

public class AttributeValues<T extends Comparable<T>> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = 5884510185079632780L;

    private final TreeSet<Entry<T>> values = new TreeSet<Entry<T>>();

    private int size = 0;

    public synchronized Entry<T> get(T value) {
        for (Entry<T> tEntry : values) {
            if (ObjectUtils.equals(tEntry.value, value)) {
                return tEntry;
            }
        }
        Entry<T> newEntry = new Entry<T>(value);
        values.add(newEntry);
        return newEntry;
    }

    public void merge(AttributeValues<T> other) {
        // Prevent concurrent modifications in case of self merge.
        TreeSet<Entry<T>> valuesToMerge = other == this ? new TreeSet<Entry<T>>(other.values) : other.values;
        if (other == this) {
            values.clear(); // Prevent growth of occurrence count in case of self merge.
        }
        for (Entry<T> value : valuesToMerge) {
            Entry<T> valueEntry = get(value.value);
            valueEntry.add(value.occurrence);
            values.add(valueEntry); // Forces reorder of occurrences in tree.
        }
    }

    public T mostCommon() {
        if (values.isEmpty()) {
            return null;
        }
        Entry<T> mostCommon = values.first();
        for (Entry<T> entry : values) {
            if (entry.getOccurrence() > mostCommon.getOccurrence()) {
                mostCommon = entry;
            }
        }
        return mostCommon.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttributeValues)) {
            return false;
        }
        AttributeValues<T> that = (AttributeValues<T>) o;
        if (!values.equals(that.values)) {
            for (Entry<T> value : values) {
                Entry entry = that.get(value.value);
                if (entry.occurrence != value.occurrence) {
                    return false;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<Entry<T>> iterator = values.iterator();
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next().value;
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    public List<T> asList() {
        List<T> list = new ArrayList<T>(size);
        for (Entry<T> value : values) {
            for (int i = 0; i < value.occurrence; i++) {
                list.add(value.value);
            }
        }
        return list;
    }

    public boolean hasMultipleValues() {
        Iterator<Entry<T>> iterator = values.iterator();
        boolean asOneElement = iterator.hasNext();
        boolean asMoreElements = false;
        if (iterator.hasNext()) {
            asMoreElements = iterator.hasNext();
        }
        return asOneElement && asMoreElements;
    }

    public int size() {
        return size;
    }

    public class Entry<E extends Comparable<T>> implements Comparable<Entry<E>>, Serializable {

        private static final long serialVersionUID = -4577080103453107543L;

        private final E value;

        private int occurrence = 0;

        public Entry(E value) {
            this.value = value;
        }

        public void add(int occurrence) {
            updateOccurrence(occurrence);
        }

        private void updateOccurrence(int occurrence) {
            this.occurrence += occurrence;
            size += occurrence;
        }

        public void increment() {
            updateOccurrence(1);
        }

        @Override
        public int compareTo(Entry<E> tEntry) {
            return ObjectUtils.compare(value, tEntry.value);
        }

        @Override
        public boolean equals(Object obj) {
            return ObjectUtils.equals(this, obj);
        }

        @Override
        public int hashCode() {
            return occurrence * value.hashCode();
        }

        public int getOccurrence() {
            return occurrence;
        }
    }

}
