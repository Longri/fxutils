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
package de.longri.gdx_utils;


import de.longri.utils.Stack;

import java.util.ArrayList;
import java.util.List;

public class ObservableArrayChange<T> extends javafx.collections.ListChangeListener.Change<T> {

    final ObservableArray<T> changeList;
    final Stack<IterableChange<T>> CHANGES = new Stack<>();

    private IterableChange<T> nextIterableChange = null;


    static class IterableChange<T> {
        int from = -1;
        int to = -1;
        List<T> items = new ArrayList<>();
    }

    static class Remove<T> extends IterableChange<T> {
        public Remove(T item) {
            items.add(item);
        }

        public Remove(Array<T> elements) {
            for (T item : elements)
                items.add(item);
        }
    }

    static class Add<T> extends IterableChange<T> {
        public Add(T item, int from, int to) {
            this.from = from;
            this.to = to;
            items.add(item);
        }
    }

    static class Permut<T> extends IterableChange<T> {
        public Permut(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    public ObservableArrayChange(ObservableArray<T> changeList) {
        super(changeList);
        this.changeList = (ObservableArray<T>) getList();
    }

    public ObservableArrayChange(ObservableArrayChange other) {
        super(other.changeList);
        this.changeList = (ObservableArray<T>) getList();

        for (int i = 0; i < other.CHANGES.size(); i++) {
            this.CHANGES.add((IterableChange<T>) other.CHANGES.get(i));
        }


    }

    @Override
    public int getRemovedSize() {
        List<T> removed = getRemoved();
        if (removed != null) {
            return removed.size();
        }
        return 0;
    }

    public void insertRemove(T item) {
        CHANGES.push(new Remove<>(item));
    }

    public void insertRemove(Array<T> elements) {
        CHANGES.push(new Remove<>(elements));
    }

    public void insertAdd(T item, int from, int to) {
        CHANGES.push(new Add<>(item, from, to));
    }

    public void insertPermutation(int from, int to) {
        CHANGES.push(new Permut<>(from, to));
    }

    @Override
    public boolean next() {
        nextIterableChange = CHANGES.pop();
        return nextIterableChange != null;
    }

    /**
     * Resets to the initial stage. After this call, {@link #next()} must be called
     * before working with the first change.
     */
    @Override
    public void reset() {

    }

    /**
     * If {@link #wasAdded()} is true, the interval contains all the values that were added.
     * If {@link #wasPermutated()} is true, the interval marks the values that were permutated.
     * If {@link #wasRemoved()} is true and {@code wasAdded} is false, {@link #getFrom()} and {@link #getTo()}
     * should return the same number - the place where the removed elements were positioned in the list.
     *
     * @return a beginning (inclusive) of an interval related to the change
     * @throws IllegalStateException if this Change instance is in initial state
     */
    @Override
    public int getFrom() {
        if (nextIterableChange == null) return -1;
        return nextIterableChange.from;
    }

    /**
     * The end of the change interval.
     *
     * @return an end (exclusive) of an interval related to the change
     * @throws IllegalStateException if this Change instance is in initial state
     * @see #getFrom()
     */
    @Override
    public int getTo() {
        if (nextIterableChange == null) return -1;
        return nextIterableChange.to;
    }

    /**
     * An immutable list of removed/replaced elements. If no elements
     * were removed from the list, an empty list is returned.
     *
     * @return a list with all the removed elements
     * @throws IllegalStateException if this Change instance is in initial state
     */
    @Override
    public List<T> getRemoved() {

        if(nextIterableChange!=null){
            return nextIterableChange.items;
        }
        return null;
    }

    /**
     * Indicates if elements were removed during this change.
     * Note that using set will also produce a change with {@code wasRemoved()} returning
     * true. See {@link #wasReplaced()}.
     *
     * @return {@code true} if something was removed from the list
     * @throws IllegalStateException if this Change instance is in initial state
     */
    public boolean wasRemoved() {
        return nextIterableChange instanceof ObservableArrayChange.Remove<T>;
    }

    /**
     * Indicates if elements were added during this change.
     *
     * @return {@code true} if something was added to the list
     * @throws IllegalStateException if this Change instance is in initial state
     */
    public boolean wasAdded() {
        return nextIterableChange instanceof ObservableArrayChange.Add<T>;
    }


    /**
     * If this change is a permutation, it returns an integer array
     * that describes the permutation.
     * This array maps directly from the previous indexes to the new ones.
     * This method is not publicly accessible and therefore can return an array safely.
     * The 0 index of the array corresponds to index {@link #getFrom()} of the list. The same applies
     * for the last index and {@link #getTo()}.
     * The method is used by {@link #wasPermutated() } and {@link #getPermutation(int)} methods.
     *
     * @return empty array if this is not permutation or an integer array containing the permutation
     * @throws IllegalStateException if this Change instance is in initial state
     */
    @Override
    protected int[] getPermutation() {
        return new int[0];
    }
}
