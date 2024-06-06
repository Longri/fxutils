/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.longri.gdx_utils;


import java.util.*;


/**
 * A resizable, ordered or unordered array of objects. If unordered, this class avoids a memory copy when removing elements (the
 * last element is moved to the removed element's position).
 *
 * @author Nathan Sweet
 */
public class Array<T> implements Iterable<T>, Collection<T> {


    public enum IDENTITY {
        EQUALS, SAME
    }


    /**
     * When true, {@link Iterable#iterator()} for {@link Array}, {@link ObjectMap}, and other collections will allocate a new
     * iterator for each invocation. When false, the iterator is reused and nested use will throw an exception. Default is
     * false.
     */
    public static boolean allocateIterators;


    /**
     * Provides direct access to the underlying array. If the Array's generic type is not Object, this field may only be accessed
     * if the  constructor was used.
     */
    public T[] items;

    public final IDENTITY identity;
    public int size;

    private ArrayIterable iterable;
    private Predicate.PredicateIterable<T> predicateIterable;

    /**
     * Creates an ordered array with a capacity of 16.
     */
    public Array() {
        this(IDENTITY.SAME, 16);
    }

    public Array(IDENTITY identity) {
        this(identity, 16);
    }

    /**
     * Creates an ordered array with the specified capacity.
     */
    public Array(int capacity) {
        this(IDENTITY.SAME, capacity);
    }

    /**
     * If false, methods that remove elements may change the order of other elements in the array, which avoids a
     * memory copy.
     *
     * @param capacity Any elements added beyond this will cause the backing array to be grown.
     */
    public Array(IDENTITY identity, int capacity) {
        this.identity = identity;
        items = (T[]) new Object[capacity];
    }


    /**
     * Creates a new array containing the elements in the specified array. The new array will have the same type of backing array
     * and will be ordered if the specified array is ordered. The capacity is set to the number of elements, so any subsequent
     * elements added will cause the backing array to be grown.
     */
    public Array(Array<? extends T> array) {
        this(array.identity, array.size, array.items.getClass().getComponentType());
        size = array.size;
        System.arraycopy(array.items, 0, items, 0, size);
    }

    /**
     * Creates a new array with {@link #items} of the specified type.
     *
     * @param capacity Any elements added beyond this will cause the backing array to be grown.
     */
    public Array(IDENTITY identity, int capacity, Class arrayType) {
        this.identity = identity;
        items = (T[]) ArrayReflection.newInstance(arrayType, capacity);
    }

    public boolean addIfNotExist(T newValue) {
        if (indexOf(newValue) < 0)
            return this.add(newValue);
        return false;
    }


    public boolean add(T value) {
        T[] items = this.items;
        if (size == items.length) items = resize(Math.max(8, (int) (size * 1.75f)));
        items[size++] = value;
        return true;
    }

    public boolean add(T value1, T value2) {
        T[] items = this.items;
        if (size + 1 >= items.length) items = resize(Math.max(8, (int) (size * 1.75f)));
        items[size] = value1;
        items[size + 1] = value2;
        size += 2;
        return true;
    }

    public boolean add(T value1, T value2, T value3) {
        T[] items = this.items;
        if (size + 2 >= items.length) items = resize(Math.max(8, (int) (size * 1.75f)));
        items[size] = value1;
        items[size + 1] = value2;
        items[size + 2] = value3;
        size += 3;
        return true;
    }

    public boolean add(T value1, T value2, T value3, T value4) {
        T[] items = this.items;
        if (size + 3 >= items.length)
            items = resize(Math.max(8, (int) (size * 1.8f))); // 1.75 isn't enough when size=5.
        items[size] = value1;
        items[size + 1] = value2;
        items[size + 2] = value3;
        items[size + 3] = value4;
        size += 4;
        return true;
    }

    public boolean addAll(Array<? extends T> array) {
        return addAll(array.items, 0, array.size);
    }

    public boolean addAll(Array<? extends T> array, int start, int count) {
        if (start + count > array.size)
            throw new IllegalArgumentException("start + count must be <= size: " + start + " + " + count + " <= " + array.size);
        return addAll(array.items, start, count);
    }

    public boolean addAll(T... array) {
        return addAll(array, 0, array.length);
    }

    public boolean addAll(T[] array, int start, int count) {
        T[] items = this.items;
        int sizeNeeded = size + count;
        if (sizeNeeded > items.length) items = resize(Math.max(Math.max(8, sizeNeeded), (int) (size * 1.75f)));
        System.arraycopy(array, start, items, size, count);
        size = sizeNeeded;
        return true;
    }

    public T get(int index) {
        if (index > size) throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + size);
        return items[index];
    }

    public T set(int index, T value) {
        if (index > size) throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + size);
        T previously = items[index];
        items[index] = value;
        return previously;
    }

    public void insert(int index, T value) {
        if (index > size) throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + size);
        T[] items = this.items;
        if (size == items.length) items = resize(Math.max(8, (int) (size * 1.75f)));
        System.arraycopy(items, index, items, index + 1, size - index);
        size++;
        items[index] = value;
    }

    /**
     * Inserts the specified number of items at the specified index. The new items will have values equal to the values at those
     * indices before the insertion.
     */
    public void insertRange(int index, int count) {
        if (index > size) throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + size);
        int sizeNeeded = size + count;
        if (sizeNeeded > items.length) items = resize(Math.max(Math.max(8, sizeNeeded), (int) (size * 1.75f)));
        System.arraycopy(items, index, items, index + count, size - index);
        size = sizeNeeded;
    }

    public void swap(int first, int second) {
        if (first > size) throw new IndexOutOfBoundsException("first can't be > size: " + first + " > " + size);
        if (second > size) throw new IndexOutOfBoundsException("second can't be > size: " + second + " > " + size);
        T[] items = this.items;
        T firstValue = items[first];
        items[first] = items[second];
        items[second] = firstValue;
    }


    /**
     * Returns true if this array contains all the specified values.
     *
     * @param values May contains nulls.
     *               If true, == comparison will be used. If false, .equals() comparison will be used.
     */
    public boolean containsAll(Array<? extends T> values) {
        T[] items = values.items;
        for (int i = 0, n = values.size; i < n; i++)
            if (!contains(items[i])) return false;
        return true;
    }

    /**
     * Returns true if this array contains any the specified values.
     *
     * @param values May contains nulls.
     *               If true, == comparison will be used. If false, .equals() comparison will be used.
     */
    public boolean containsAny(Array<? extends T> values) {
        T[] items = values.items;
        for (int i = 0, n = values.size; i < n; i++)
            if (contains(items[i])) return true;
        return false;
    }

    /**
     * Returns the index of first occurrence of value in the array, or -1 if no such value exists.
     *
     * @param value May be null.
     *              true, == comparison will be used. If false, .equals() comparison will be used.
     * @return An index of first occurrence of value in array or -1 if no such value exists
     */
    public int indexOf(T value) {
        T[] items = this.items;
        if (identity == IDENTITY.SAME || value == null) {
            for (int i = 0, n = size; i < n; i++)
                if (items[i] == value) return i;
        } else {
            for (int i = 0, n = size; i < n; i++)
                if (value.equals(items[i])) return i;
        }
        return -1;
    }

    /**
     * Returns an index of last occurrence of value in array or -1 if no such value exists. Search is started from the end of an
     * array.
     *
     * @param value May be null.
     *              true, == comparison will be used. If false, .equals() comparison will be used.
     * @return An index of last occurrence of value in array or -1 if no such value exists
     */
    public int lastIndexOf(T value) {
        T[] items = this.items;
        if (identity == IDENTITY.SAME || value == null) {
            for (int i = size - 1; i >= 0; i--)
                if (items[i] == value) return i;
        } else {
            for (int i = size - 1; i >= 0; i--)
                if (value.equals(items[i])) return i;
        }
        return -1;
    }


    /**
     * Removes and returns the item at the specified index.
     */
    public T removeIndex(int index) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        T[] items = this.items;
        T value = items[index];
        size--;

        System.arraycopy(items, index + 1, items, index, size - index);

        items[size] = null;
        return value;
    }

    /**
     * Removes the items between the specified indices, inclusive.
     */
    public void removeRange(int start, int end) {
        int n = size;
        if (end >= n) throw new IndexOutOfBoundsException("end can't be >= size: " + end + " >= " + size);
        if (start > end) throw new IndexOutOfBoundsException("start can't be > end: " + start + " > " + end);
        T[] items = this.items;
        int count = end - start + 1, lastIndex = n - count;

        System.arraycopy(items, start + count, items, start, n - (start + count));

        for (int i = lastIndex; i < n; i++)
            items[i] = null;
        size = n - count;
    }

    /**
     * Removes from this array all of elements contained in the specified array.
     * <p>
     * True to use ==, false to use .equals().
     *
     * @return true if this array was modified.
     */
    public boolean removeAll(Array<? extends T> array) {
        int size = this.size;
        int startSize = size;
        T[] items = this.items;
        if (identity == IDENTITY.SAME) {
            for (int i = 0, n = array.size; i < n; i++) {
                T item = array.get(i);
                for (int ii = 0; ii < size; ii++) {
                    if (item == items[ii]) {
                        removeIndex(ii);
                        size--;
                        break;
                    }
                }
            }
        } else {
            for (int i = 0, n = array.size; i < n; i++) {
                T item = array.get(i);
                for (int ii = 0; ii < size; ii++) {
                    if (item.equals(items[ii])) {
                        removeIndex(ii);
                        size--;
                        break;
                    }
                }
            }
        }
        return size != startSize;
    }

    /**
     * Removes and returns the last item.
     */
    public T pop() {
        if (size == 0) throw new IllegalStateException("Array is empty.");
        --size;
        T item = items[size];
        items[size] = null;
        return item;
    }

    /**
     * Returns the last item.
     */
    public T peek() {
        if (size == 0) throw new IllegalStateException("Array is empty.");
        return items[size - 1];
    }

    /**
     * Returns the first item.
     */
    public T first() {
        if (size == 0) throw new IllegalStateException("Array is empty.");
        return items[0];
    }

    /**
     * Returns true if the array has one or more items.
     */
    public boolean notEmpty() {
        return size > 0;
    }

    /**
     * Returns true if the array is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        Arrays.fill(items, 0, size, null);
        size = 0;
    }

    /**
     * Reduces the size of the backing array to the size of the actual items. This is useful to release memory when many items
     * have been removed, or if it is known that more items will not be added.
     *
     * @return {@link #items}
     */
    public T[] shrink() {
        if (items.length != size) resize(size);
        return items;
    }

    /**
     * Increases the size of the backing array to accommodate the specified number of additional items. Useful before adding many
     * items to avoid multiple backing array resizes.
     *
     * @return {@link #items}
     */
    public T[] ensureCapacity(int additionalCapacity) {
        if (additionalCapacity < 0)
            throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
        int sizeNeeded = size + additionalCapacity;
        if (sizeNeeded > items.length) resize(Math.max(Math.max(8, sizeNeeded), (int) (size * 1.75f)));
        return items;
    }

    /**
     * Sets the array size, leaving any values beyond the current size null.
     *
     * @return {@link #items}
     */
    public T[] setSize(int newSize) {
        truncate(newSize);
        if (newSize > items.length) resize(Math.max(8, newSize));
        size = newSize;
        return items;
    }

    /**
     * Creates a new backing array with the specified size containing the current items.
     */
    protected T[] resize(int newSize) {
        T[] items = this.items;
        T[] newItems = (T[]) ArrayReflection.newInstance(items.getClass().getComponentType(), newSize);
        System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    /**
     * Sorts this array. The array elements must implement {@link Comparable}. This method is not thread safe (uses
     * {@link Sort#instance()}).
     */
    public void sort() {
        Sort.instance().sort(items, 0, size);
    }

    /**
     * Sorts the array. This method is not thread safe (uses {@link Sort#instance()}).
     */
    public void sort(Comparator<? super T> comparator) {
        Sort.instance().sort(items, comparator, 0, size);
    }

    /**
     * Selects the nth-lowest element from the Array according to Comparator ranking. This might partially sort the Array. The
     * array must have a size greater than 0, or a {@link RuntimeException} will be thrown.
     *
     * @param comparator used for comparison
     * @param kthLowest  rank of desired object according to comparison, n is based on ordinal numbers, not array indices. for min
     *                   value use 1, for max value use size of array, using 0 results in runtime exception.
     * @return the value of the Nth lowest ranked object.
     * @see Select
     */
    public T selectRanked(Comparator<T> comparator, int kthLowest) {
        if (kthLowest < 1) {
            throw new RuntimeException("nth_lowest must be greater than 0, 1 = first, 2 = second...");
        }
        return Select.instance().select(items, comparator, kthLowest, size);
    }

    /**
     * @param comparator used for comparison
     * @param kthLowest  rank of desired object according to comparison, n is based on ordinal numbers, not array indices. for min
     *                   value use 1, for max value use size of array, using 0 results in runtime exception.
     * @return the index of the Nth lowest ranked object.
     * @see Array#selectRanked(Comparator, int)
     */
    public int selectRankedIndex(Comparator<T> comparator, int kthLowest) {
        if (kthLowest < 1) {
            throw new RuntimeException("nth_lowest must be greater than 0, 1 = first, 2 = second...");
        }
        return Select.instance().selectIndex(items, comparator, kthLowest, size);
    }

    public void reverse() {
        T[] items = this.items;
        for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
            int ii = lastIndex - i;
            T temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    public void shuffle() {
        T[] items = this.items;
        for (int i = size - 1; i >= 0; i--) {
            int ii = MathUtils.random(i);
            T temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    protected ListIterator<T> iterator(int index) {
        if (allocateIterators) {
            ArrayIterator arrayIterator = new ArrayIterator(this, true);
            arrayIterator.index = index;
            return arrayIterator;
        }
        if (iterable == null) {
            iterable = new ArrayIterable(this);
            iterable.iterator().index = index;
        }
        return iterable.iterator();
    }


    /**
     * Returns an iterator for the items in the array. Remove is supported.
     * <p>
     * If {@link this#allocateIterators} is false, the same iterator instance is returned each time this method is called.
     * Use the {@link ArrayIterator} constructor for nested or multithreaded iteration.
     */
    public ArrayIterator<T> iterator() {
        if (allocateIterators) return new ArrayIterator(this, true);
        if (iterable == null) iterable = new ArrayIterable(this);
        return iterable.iterator();
    }

    /**
     * Returns an iterable for the selected items in the array. Remove is supported, but not between hasNext() and next().
     * <p>
     * If { allocateIterators} is false, the same iterable instance is returned each time this method is called.
     * Use the {@link Predicate.PredicateIterable} constructor for nested or multithreaded iteration.
     */
    public Iterable<T> select(Predicate<T> predicate) {
        if (allocateIterators) return new Predicate.PredicateIterable<T>(this, predicate);
        if (predicateIterable == null)
            predicateIterable = new Predicate.PredicateIterable<T>(this, predicate);
        else
            predicateIterable.set(this, predicate);
        return predicateIterable;
    }

    /**
     * Reduces the size of the array to the specified size. If the array is already smaller than the specified size, no action is
     * taken.
     */
    public void truncate(int newSize) {
        if (newSize < 0) throw new IllegalArgumentException("newSize must be >= 0: " + newSize);
        if (size <= newSize) return;
        for (int i = newSize; i < size; i++)
            items[i] = null;
        size = newSize;
    }

    /**
     * Returns a random item from the array, or null if the array is empty.
     */
    public T random() {
        if (size == 0) return null;
        return items[MathUtils.random(0, size - 1)];
    }

    /**
     * Returns the items as an array. Note the array is typed, so the  constructor must have been used.
     * Otherwise use {@link #toArray(Class)} to specify the array type.
     */
    public T[] toArray() {
        return (T[]) toArray(items.getClass().getComponentType());
    }

    public <V> V[] toArray(Class<V> type) {
        V[] result = (V[]) ArrayReflection.newInstance(type, size);
        System.arraycopy(items, 0, result, 0, size);
        return result;
    }

    public int hashCode() {
        Object[] items = this.items;
        int h = 1;
        for (int i = 0, n = size; i < n; i++) {
            h *= 31;
            Object item = items[i];
            if (item != null) h += item.hashCode();
        }
        return h;
    }

    /**
     * Returns false if either array is unordered.
     */
    public boolean equals(Object object) {
        if (object == this) return true;
        if (!(object instanceof Array)) return false;
        Array array = (Array) object;
        int n = size;
        if (n != array.size) return false;
        Object[] items1 = this.items, items2 = array.items;
        for (int i = 0; i < n; i++) {
            Object o1 = items1[i], o2 = items2[i];
            if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
        }
        return true;
    }

    public String toString() {
        if (size == 0) return "[]";
        T[] items = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(items[0]);
        for (int i = 1; i < size; i++) {
            buffer.append(", ");
            buffer.append(items[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    public String toString(String separator) {
        if (size == 0) return "";
        T[] items = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append(items[0]);
        for (int i = 1; i < size; i++) {
            buffer.append(separator);
            buffer.append(items[i]);
        }
        return buffer.toString();
    }

    static public class ArrayIterator<T> implements Iterator<T>, Iterable<T>, ListIterator<T> {
        private final Array<T> array;
        private final boolean allowRemove;
        int index;
        boolean valid = true;

// ArrayIterable<T> iterable;

        public ArrayIterator(Array<T> array) {
            this(array, true);
        }

        public ArrayIterator(Array<T> array, boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }

        public boolean hasNext() {
            if (!valid) {
// System.out.println(iterable.lastAcquire);
                throw new RuntimeException("#iterator() cannot be used nested.");
            }
            return index < array.size;
        }

        public T next() {
            if (index >= array.size) throw new NoSuchElementException(String.valueOf(index));
            if (!valid) {
// System.out.println(iterable.lastAcquire);
                throw new RuntimeException("#iterator() cannot be used nested.");
            }
            return array.items[index++];
        }

        /**
         * Returns {@code true} if this list iterator has more elements when
         * traversing the list in the reverse direction.  (In other words,
         * returns {@code true} if {@link #previous} would return an element
         * rather than throwing an exception.)
         *
         * @return {@code true} if the list iterator has more elements when
         * traversing the list in the reverse direction
         */
        @Override
        public boolean hasPrevious() {
            return previousIndex() > 0;
        }

        /**
         * Returns the previous element in the list and moves the cursor
         * position backwards.  This method may be called repeatedly to
         * iterate through the list backwards, or intermixed with calls to
         * {@link #next} to go back and forth.  (Note that alternating calls
         * to {@code next} and {@code previous} will return the same
         * element repeatedly.)
         *
         * @return the previous element in the list
         * @throws NoSuchElementException if the iteration has no previous
         *                                element
         */
        @Override
        public T previous() {
            if (!hasPrevious()) throw new NoSuchElementException();
            return array.items[previousIndex()];
        }

        /**
         * Returns the index of the element that would be returned by a
         * subsequent call to {@link #next}. (Returns list size if the list
         * iterator is at the end of the list.)
         *
         * @return the index of the element that would be returned by a
         * subsequent call to {@code next}, or list size if the list
         * iterator is at the end of the list
         */
        @Override
        public int nextIndex() {
            return Math.min(array.size, index + 1);
        }

        /**
         * Returns the index of the element that would be returned by a
         * subsequent call to {@link #previous}. (Returns -1 if the list
         * iterator is at the beginning of the list.)
         *
         * @return the index of the element that would be returned by a
         * subsequent call to {@code previous}, or -1 if the list
         * iterator is at the beginning of the list
         */
        @Override
        public int previousIndex() {
            return index - 1;
        }

        public void remove() {
            if (!allowRemove) throw new RuntimeException("Remove not allowed.");
            index--;
            array.removeIndex(index);
        }

        /**
         * Replaces the last element returned by {@link #next} or
         * {@link #previous} with the specified element (optional operation).
         * This call can be made only if neither {@link #remove} nor {@link
         * #add} have been called after the last call to {@code next} or
         * {@code previous}.
         *
         * @param t the element with which to replace the last element returned by
         *          {@code next} or {@code previous}
         * @throws UnsupportedOperationException if the {@code set} operation
         *                                       is not supported by this list iterator
         * @throws ClassCastException            if the class of the specified element
         *                                       prevents it from being added to this list
         * @throws IllegalArgumentException      if some aspect of the specified
         *                                       element prevents it from being added to this list
         * @throws IllegalStateException         if neither {@code next} nor
         *                                       {@code previous} have been called, or {@code remove} or
         *                                       {@code add} have been called after the last call to
         *                                       {@code next} or {@code previous}
         */
        @Override
        public void set(T t) {
            throw new UnsupportedOperationException();
        }

        /**
         * Inserts the specified element into the list (optional operation).
         * The element is inserted immediately before the element that
         * would be returned by {@link #next}, if any, and after the element
         * that would be returned by {@link #previous}, if any.  (If the
         * list contains no elements, the new element becomes the sole element
         * on the list.)  The new element is inserted before the implicit
         * cursor: a subsequent call to {@code next} would be unaffected, and a
         * subsequent call to {@code previous} would return the new element.
         * (This call increases by one the value that would be returned by a
         * call to {@code nextIndex} or {@code previousIndex}.)
         *
         * @param t the element to insert
         * @throws UnsupportedOperationException if the {@code add} method is
         *                                       not supported by this list iterator
         * @throws ClassCastException            if the class of the specified element
         *                                       prevents it from being added to this list
         * @throws IllegalArgumentException      if some aspect of this element
         *                                       prevents it from being added to this list
         */
        @Override
        public void add(T t) {
            throw new UnsupportedOperationException();
        }

        public void reset() {
            index = 0;
        }

        public ArrayIterator<T> iterator() {
            return this;
        }
    }

    static public class ArrayIterable<T> implements Iterable<T> {
        private final Array<T> array;
        private final boolean allowRemove;
        private ArrayIterator iterator1, iterator2;

// java.io.StringWriter lastAcquire = new java.io.StringWriter();

        public ArrayIterable(Array<T> array) {
            this(array, true);
        }

        public ArrayIterable(Array<T> array, boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }

        /**
         * see allocateIterators
         */
        public ArrayIterator<T> iterator() {
            if (allocateIterators) return new ArrayIterator(array, allowRemove);
// lastAcquire.getBuffer().setLength(0);
// new Throwable().printStackTrace(new java.io.PrintWriter(lastAcquire));
            if (iterator1 == null) {
                iterator1 = new ArrayIterator(array, allowRemove);
                iterator2 = new ArrayIterator(array, allowRemove);
// iterator1.iterable = this;
// iterator2.iterable = this;
            }
            if (!iterator1.valid) {
                iterator1.index = 0;
                iterator1.valid = true;
                iterator2.valid = false;
                return iterator1;
            }
            iterator2.index = 0;
            iterator2.valid = true;
            iterator1.valid = false;
            return iterator2;
        }
    }


    public void transferValuesTo(Array<T> other) {
        // ensure other capacity
        other.ensureCapacity(this.size);
        System.arraycopy(items, 0, other.items, 0, size);
        other.size = this.size;
    }


    // collection implementations

    /**
     * @param t1s
     * @param <T1>
     * @return
     */
    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        System.arraycopy(items, 0, t1s, 0, size);
        return t1s;
    }

    /**
     * @param o
     * @return
     */
    @Override
    public boolean contains(Object o) {
//        if (size <= 0 || o == null || o.getClass() != items[0].getClass()) return false;
        if (size <= 0) return false; // don't check class, because it is possible extended item inside
        T value = (T) o;
        T[] items = this.items;
        int i = size - 1;
        if (identity == IDENTITY.SAME || value == null) {
            while (i >= 0)
                if (items[i--] == value) return true;
        } else {
            while (i >= 0)
                if (value.equals(items[i--])) return true;
        }
        return false;
    }

    /**
     * @return
     */
    @Override
    public int size() {
        return size;
    }


    /**
     * @param idx index of removed item
     * @return
     */
    public T remove(int idx) {
        return removeIndex(idx);
    }


    /**
     * @param o
     * @return
     */
    @Override
    public boolean remove(Object o) {
        if (size <= 0 || o == null || o.getClass() != items[0].getClass()) return false;
        T value = (T) o;
        T[] items = this.items;
        if (identity == IDENTITY.SAME || value == null) {
            for (int i = 0, n = size; i < n; i++) {
                if (items[i] == value) {
                    removeIndex(i);
                    return true;
                }
            }
        } else {
            for (int i = 0, n = size; i < n; i++) {
                if (value.equals(items[i])) {
                    removeIndex(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param collection
     * @return
     */
    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object v : collection)
            if (!contains(v)) return false;

        return true;
    }

    /**
     * @param collection
     * @return
     */
    @Override
    public boolean addAll(Collection<? extends T> collection) {
        boolean all = true;
        for (Object v : collection)
            if (!add((T) v))
                all = false;
        return all;
    }

    /**
     * @param collection
     * @return
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean all = true;
        for (Object v : collection)
            if (!remove((T) v))
                all = false;
        return all;
    }

    /**
     * @param collection
     * @return
     */
    @Override
    public boolean retainAll(Collection<?> collection) {

        Array<T> delList = new Array<>();

        for (T item : items)
            if (!collection.contains(item)) {
                delList.add(item);
            }

        if (delList.size > 0) {
            removeAll(delList);
            return true;
        }
        return false;
    }


}
