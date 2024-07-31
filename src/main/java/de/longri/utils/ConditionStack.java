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
package de.longri.utils;

import de.longri.gdx_utils.Array;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ConditionStack<T extends iCondition> implements Iterator<T> {

    private Array<T> list;


    public ConditionStack() {
        list = new Array<T>();
    }

    /**
     * push the item to the first
     *
     * @param value
     */
    public void push(T value) {
        list.insert(0, value);
    }

    /**
     * Add the item to the last
     *
     * @param value
     */
    public void add(T value) {
        list.add(value);
    }


    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        T temp = null;
        if (list.size > 0) {
            temp = list.get(0);

            if (temp.isConditionMet()) {
                list.removeIndex(0);
                return hasNext();
            }
        }
        return temp != null;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public T next() {
        T temp = list.get(0);
        if (temp == null) {
            throw new NoSuchElementException("Stack underflow");
        }
        return temp;
    }
}
