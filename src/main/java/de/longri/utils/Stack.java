/*
 * Copyright (C) 2015 Longri
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


/**
 * @param <T>
 * @author Longri
 */
public class Stack<T> {
    Array<T> list;

    public Stack() {
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
     * get and remove from the first
     *
     * @return
     */
    public T pop() {
        T temp = null;
        if (list.size > 0) {
            temp = list.get(0);
            list.removeIndex(0);
        }
        return temp;
    }

    /**
     * get and remove from the last
     *
     * @return
     */
    public T peek() {
        T temp = null;
        if (list.size > 0) {
            temp = list.get(list.size-1);
            list.removeIndex(list.size-1);
        }
        return temp;
    }

    public int size() {
        return list.size;
    }

    public T get(int i) {
        T temp = null;
        if (list.size > 0) {
            temp = list.get(i);
        }
        return temp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Stack of " + list.get(0).getClass().getName());
        for (T t : list) {
            sb.append("  > " + t.toString());
        }
        return sb.toString();
    }

    public void clear() {
        list.clear();
    }

}
