/*
 * Copyright (C) 2024 Longri
 *
 * This file is part of fxutils.
 *
 * fxutils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * fxutils is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with fxutils. If not, see <https://www.gnu.org/licenses/>.
 */
package de.longri.fx.utils;

import de.longri.gdx_utils.Array;
import de.longri.gdx_utils.ArrayReflection;
import de.longri.gdx_utils.ObservableArray;
import de.longri.gdx_utils.ObservableArrayChange;
import javafx.collections.ListChangeListener;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractFilteredList<T> extends ObservableArray<T> {
    protected int[] filtered;
    public int filteredSize;
    protected final AtomicBoolean reindexStop = new AtomicBoolean();
    protected Filter<T> FILTER;

    public AbstractFilteredList() {
        this(Array.IDENTITY.EQUALS);
    }

    public AbstractFilteredList(Array.IDENTITY identity) {
        super(identity);
    }

    public void transferValuesTo(AbstractFilteredList<T> other) {
        super.transferValuesTo(other);
        if (filtered == null) reindex();

        if (other.filtered == null || other.filtered.length < filtered.length) {
            other.filtered = new int[super.size()];
        }
        System.arraycopy(filtered, 0, other.filtered, 0, filteredSize);
        other.filteredSize = this.filteredSize;
        other.chkChange();
        other.CHANGE.insertPermutation(0, filteredSize);
        other.fireChange();
    }


    public boolean predictTest(T object) {
        return true;
    }

    public abstract ListChangeListener.Change<T> getChange();

    public void clear() {
        synchronized (reindexStop) {
            filteredSize = 0;
            super.clear();
            if (filtered != null) Arrays.fill(filtered, -1);
        }
    }

    /**
     * Stopped the reindex with ListChanged event.
     * Use this for add items without reindex with every item.
     * And start reindex if you are added all items with .startReindex()
     */
    public void stopReindex() {
        reindexStop.set(true);
    }

    public void startReindex() {
        reindexStop.set(false);
        reindex();
    }

    private boolean getPredict(T o) {
        if (FILTER != null) {
            if (FILTER.isDisabled())
                return true; // ignore disabled filter

            return FILTER.predictTest(o);
        }
        return predictTest(o);
    }

    protected void reindex() {
        synchronized (reindexStop) {

            if (filtered == null || filtered.length < super.size()) {
                filtered = new int[super.size()];
            }

            Arrays.fill(filtered, -1);

            filteredSize = 0;
            int idx = 0;

            if (FILTER != null)
                FILTER.startPredictTest();
            for (int i = 0; i < super.size; i++) {
                T item = super.get(i);
                if (getPredict(item)) {
                    filtered[filteredSize] = idx;
                    filteredSize++;
                }
                idx++;
            }
            if (FILTER != null)
                FILTER.finishPredictTest();

            chkChange();
            CHANGE.insertPermutation(0, filteredSize);
            fireChange();
        }
    }


    /**
     * Returns the number of elements in this replace.  If this list contains
     * more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        synchronized (reindexStop) {
            return filteredSize;
        }
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        synchronized (reindexStop) {
            return filteredSize == 0;
        }
    }

    /**
     * Returns the element at the specified position in this replace.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   ({@code index < 0 || index >= size()})
     */
    @Override
    public T get(int index) {
        synchronized (reindexStop) {
            if (index >= filteredSize) {
                throw new IndexOutOfBoundsException();
            }
            return super.get(filtered[index]);
        }
    }

    public T getUnfiltered(int idx) {
        return items[idx];
    }


    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one element {@code e} such that
     * {@code Objects.equals(o, e)}.
     *
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     * @throws ClassCastException   if the type of the specified element
     *                              is incompatible with this list
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *                              list does not permit null elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    public boolean contains(Object o) {
        synchronized (reindexStop) {
            try {
                T obj = (T) o;

                for (int i = 0; i < super.size(); i++) {
                    if (identity == IDENTITY.EQUALS) {
                        if (super.get(i).equals(obj)) return true;
                    } else {
                        if (super.get(i) == obj) return true;
                    }
                }
                return false;
            } catch (Exception exception) {
                return false;
            }
        }
    }


    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one element {@code e} such that
     * {@code Objects.equals(o, e)}.
     *
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     * @throws NullPointerException if the specified element is null and this
     *                              list does not permit null elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    public boolean containsEquals(Object o) {
        synchronized (reindexStop) {
            T obj = (T) o;

            for (int i = 0; i < super.size(); i++) {
                if (super.get(i).equals(obj)) return true;
            }
            return false;
        }
    }


    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    public ArrayIterator<T> unFilteredIterator() {
        synchronized (reindexStop) {
            return new ArrayIterator<T>(this) {

                Object[] items = toArray();
                int count = 0;

                /**
                 * Returns {@code true} if the iteration has more elements.
                 * (In other words, returns {@code true} if {@link #next} would
                 * return an element rather than throwing an exception.)
                 *
                 * @return {@code true} if the iteration has more elements
                 */
                @Override
                public boolean hasNext() {
                    return items.length > count;
                }

                /**
                 * Returns the next element in the iteration.
                 *
                 * @return the next element in the iteration
                 * @throws NoSuchElementException if the iteration has no more elements
                 */
                @Override
                public T next() {
                    return (T) items[count++];
                }
            };
        }
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    @Override
    public ArrayIterator<T> iterator() {
        synchronized (reindexStop) {
            return new ArrayIterator<T>(this) {

                AbstractFilteredList<T> that = AbstractFilteredList.this;
                int count = 0;

                /**
                 * Returns {@code true} if the iteration has more elements.
                 * (In other words, returns {@code true} if {@link #next} would
                 * return an element rather than throwing an exception.)
                 *
                 * @return {@code true} if the iteration has more elements
                 */
                @Override
                public boolean hasNext() {
                    return that.filteredSize > count;
                }

                /**
                 * Returns the next element in the iteration.
                 *
                 * @return the next element in the iteration
                 * @throws NoSuchElementException if the iteration has no more elements
                 */
                @Override
                public T next() {
                    return that.get(count++);
                }
            };
        }
    }


    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this replace.  (In other words, this method must
     * allocate a new array even if this list is backed by an array).
     * The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list in proper
     * sequence
     * @see Arrays#asList(Object[])
     */
    @Override
    public T[] toArray() {
        synchronized (reindexStop) {
            T[] arr = (T[]) ArrayReflection.newInstance(items.getClass().getComponentType(), filteredSize);
            for (int i = 0; i < filteredSize; i++) arr[i] = get(i);
            return arr;
        }
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this replace.  (In other words, this method must
     * allocate a new array even if this list is backed by an array).
     * The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list in proper
     * sequence
     * @see Arrays#asList(Object[])
     */
    public T[] toUnfilteredArray() {
        synchronized (reindexStop) {
            T[] arr = (T[]) ArrayReflection.newInstance(items.getClass().getComponentType(), size);
            for (int i = 0; i < size; i++) arr[i] = getUnfiltered(i);
            return arr;
        }
    }

    //#################  Quick sort  implementation  #################################

    public void sort() {
        synchronized (reindexStop) {
            quickSort(filtered, 0, filteredSize - 1);
        }
    }

    public abstract int compare(T o1, T o2);

    protected void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    protected int partition(int[] arr, int low, int high) {

        int i = low - 1;
        for (int j = low; j < high; j++) {
            int compareResult = compare(super.get(arr[j]), super.get(arr[high]));
            if (compareResult < 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    protected void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public boolean add(T t) {
        synchronized (reindexStop) {
            boolean ret = super.add(t);
            if (!reindexStop.get()) reindex();
            return ret;
        }
    }

    public boolean addAll(T... elements) {
        synchronized (reindexStop) {
            boolean ret = super.addAll(elements);
            if (!reindexStop.get()) reindex();
            return ret;
        }
    }

    public boolean addAll(Collection<? extends T> c) {
        synchronized (reindexStop) {
            boolean ret = super.addAll(c);
            if (!reindexStop.get()) reindex();
            return ret;
        }
    }

    public boolean remove(Object o) {
        synchronized (reindexStop) {
            boolean ret = super.remove(o);
            if (!reindexStop.get()) reindex();
            return ret;
        }
    }

    public boolean removeAll(T... elements) {
        synchronized (reindexStop) {
            boolean ret = super.removeAll(elements);
            if (!reindexStop.get()) reindex();
            return ret;
        }
    }

    public boolean removeAll(Collection<?> c) {
        synchronized (reindexStop) {
            boolean ret = super.removeAll(c);
            if (!reindexStop.get()) reindex();
            return ret;
        }
    }

    /**
     * Clears the ObservableList and adds all the elements passed as var-args.
     *
     * @param elements the elements to set
     * @return true (as specified by Collection.add(E))
     * @throws NullPointerException if the specified arguments contain one or more null elements
     */
    public boolean setAll(T... elements) {
        for (T t : elements)
            if (t == null) throw new NullPointerException("specified arguments contain one or more null elements");
        synchronized (reindexStop) {
            boolean ret = super.setAll(elements);
            if (!reindexStop.get()) reindex();
            return ret;
        }
    }

    /**
     * Clears the ObservableList and adds all elements from the collection.
     *
     * @param col the collection with elements that will be added to this observableArrayList
     * @return true (as specified by Collection.add(E))
     * @throws NullPointerException if the specified collection contains one or more null elements
     */
    public boolean setAll(Collection<? extends T> col) {
        for (T t : col)
            if (t == null) throw new NullPointerException("specified arguments contain one or more null elements");
        synchronized (reindexStop) {
            boolean ret = super.setAll(col);
            if (!reindexStop.get()) reindex();
            return ret;
        }
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element (optional operation).
     *
     * @param index   index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the {@code set} operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of the specified element
     *                                       prevents it from being added to this list
     * @throws NullPointerException          if the specified element is null and
     *                                       this list does not permit null elements
     * @throws IllegalArgumentException      if some property of the specified
     *                                       element prevents it from being added to this list
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       ({@code index < 0 || index >= size()})
     */
    @Override
    public T set(int index, T element) {
        synchronized (reindexStop) {

            //get filtered Index
            T ret = super.set(filtered[index], element);
            if (!reindexStop.get()) reindex();
            return ret;
        }
    }

    FilterChangeListener changeListener = new FilterChangeListener() {
        @Override
        public void changed() {
            reindex();
        }
    };

    public void setFilter(Filter<T> filter) {
        if (this.FILTER != null) {
            this.FILTER.removeChangeListener(changeListener);
        }

        this.FILTER = filter;
        reindex();
        FILTER.addChangeListener(changeListener);
    }

    @Override
    public void fireChange() {

        if (reindexStop.get()) return; // no Change event if reindex stop

        for (ListChangeListener<? super T> listener : listeners) {
            final ObservableArrayChange<T> finaleChange = new ObservableArrayChange<>(CHANGE);
            try {
                listener.onChanged(finaleChange);
            } catch (Exception ignore) {
                // and call other listener
            }
        }
        CHANGE = null;
    }

}