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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;

public class OrderedHashMap<K, V> implements Iterable<V> {

    private Array<K> keys;
    private Array<V> values;


    /**
     *
     */
    public OrderedHashMap() {
    }

    public OrderedHashMap(Array.IDENTITY keyIdentity, Array.IDENTITY valueIdentity) {
        keys = new Array<>(keyIdentity);
        values = new Array<>(valueIdentity);
    }


    public int size() {
        if (keys == null) return 0;
        return keys.size;
    }

    public void add(K key, V value) {

        if (keys == null) {
            if (key instanceof String) {
                keys = new Array<>(Array.IDENTITY.EQUALS);
            } else {
                keys = new Array<>(Array.IDENTITY.SAME);
            }
        }

        if (values == null) {
            if (value instanceof String) {
                values = new Array<>(Array.IDENTITY.EQUALS);
            } else {
                values = new Array<>(Array.IDENTITY.SAME);
            }
        }


        keys.add(key);
        values.add(value);
    }

    public V get(int index) {
        if (values == null) return null;
        return values.get(index);
    }

    public V get(K key) {
        if (key == null) return null;
        int index = keys.indexOf(key);
        if (index < 0) return null;
        return values.get(index);
    }

    public void clear() {
        if (keys == null) return;
        keys.clear();
        values.clear();
    }


    private class ValueIterator implements Iterator<V> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            if (values == null) return false;
            return index < values.size;
        }

        @Override
        public V next() {
            if (values == null) return null;
            V element = values.get(index);
            index++;
            return element;
        }
    }


    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<V> iterator() {
        return new ValueIterator();
    }

}
