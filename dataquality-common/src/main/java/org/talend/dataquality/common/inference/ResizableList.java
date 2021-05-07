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
package org.talend.dataquality.common.inference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

import org.talend.dataquality.common.exception.DQCommonRuntimeException;

/**
 * A {@link List} that can increase its size to ensure that all index in list have an instance of <i>T</i>.
 * It relies on a {@link Supplier} to create new instances when increasing its size.
 * 
 * @param <T> the type of list's elements.
 * @see #resize(int)
 */
public class ResizableList<T> implements List<T>, Serializable {

    private static final long serialVersionUID = -4643753633617225999L;

    private final Supplier<T> supplier;

    private final List<T> innerList;

    /**
     * Creates a list with explicit {@link #resize(int) resize} that contains instances of <i>T</i>.
     * 
     * @param supplier the {@link Supplier} object that creates new instances of type <i>T</i>.
     */
    public ResizableList(Supplier<T> supplier) {
        this.supplier = supplier;
        this.innerList = new ArrayList<>();
    }

    /**
     * Resize the list so it contains <code>size</code> instances of <i>T</i>. Method only scales up, never down.
     * 
     * @param size The new size for the list. Must be a positive number.
     * @return {@code true} if new elements were added to the list (i.e. the list was resized), {@code false} otherwise.
     */
    public boolean resize(int size) {
        if (size < 0) {
            throw new DQCommonRuntimeException("Unable to resize list of items: Size must be a positive number.");
        }
        final int missing = size - innerList.size();
        boolean addedMissing = missing > 0;
        for (int i = 0; i < missing; i++) {
            innerList.add(supplier.get());
        }
        return addedMissing;
    }

    @Override
    public int size() {
        return innerList.size();
    }

    @Override
    public boolean isEmpty() {
        return innerList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return innerList.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return innerList.iterator();
    }

    @Override
    public Object[] toArray() {
        return innerList.toArray();
    }

    @Override
    public <Q> Q[] toArray(Q[] qs) {
        return innerList.toArray(qs);
    }

    @Override
    public boolean add(T t) {
        return innerList.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return innerList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return innerList.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return innerList.addAll(collection);
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        return innerList.addAll(i, collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return innerList.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return innerList.retainAll(collection);
    }

    @Override
    public void clear() {
        innerList.clear();
    }

    @Override
    public boolean equals(Object o) {
        return innerList.equals(o);
    }

    @Override
    public int hashCode() {
        return innerList.hashCode();
    }

    @Override
    public T get(int i) {
        return innerList.get(i);
    }

    @Override
    public T set(int i, T t) {
        return innerList.set(i, t);
    }

    @Override
    public void add(int i, T t) {
        innerList.add(i, t);
    }

    @Override
    public T remove(int i) {
        return innerList.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return innerList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return innerList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return innerList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return innerList.listIterator(i);
    }

    @Override
    public List<T> subList(int i, int i1) {
        return innerList.subList(i, i1);
    }

}
