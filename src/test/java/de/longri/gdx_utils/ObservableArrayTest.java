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

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ObservableArrayTest {

    @Test
    public void testAdd() {
        ObservableArray<String> list = new ObservableArray<>();
        list.add("Item1");
        assertEquals(1, list.size());
        assertEquals("Item1", list.get(0));
    }

    @Test
    public void testRemove() {
        ObservableArray<String> list = new ObservableArray<>();
        list.add("Item1");
        list.remove("Item1");
        assertTrue(list.isEmpty());
    }

    @Test
    public void testGet() {
        ObservableArray<String> list = new ObservableArray<>();
        list.add("Item1");
        list.add("Item2");
        assertEquals("Item2", list.get(1));
    }

    @Test
    public void testSize() {
        ObservableArray<String> list = new ObservableArray<>();
        assertEquals(0, list.size());
        list.add("Item1");
        assertEquals(1, list.size());
    }

    @Test
    public void testContains() {
        ObservableArray<String> list = new ObservableArray<>();
        list.add("Item1");
        assertTrue(list.contains("Item1"));
        assertFalse(list.contains("Item2"));
    }

    @Test
    public void testIterator() {
        ObservableArray<String> list = new ObservableArray<>();
        list.add("Item1");
        list.add("Item2");

        Iterator<String> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("Item1", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("Item2", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testAddWithChangeListener() {
        AddWithChangeListener(FXCollections.observableArrayList());
        AddWithChangeListener(new ObservableArray<>(Array.IDENTITY.EQUALS));
    }

    private void AddWithChangeListener(ObservableList<String> list) {
        List<String> addedItems = new ArrayList<>();

        assertEquals(0, list.size());
        list.addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    addedItems.addAll(change.getAddedSubList());
                }
            }
        });

        list.add("Item1");
        assertEquals(1, list.size());
        list.add("Item2");
        assertEquals(2, list.size());

        assertEquals(2, addedItems.size());
        assertTrue(addedItems.contains("Item1"));
        assertTrue(addedItems.contains("Item2"));

        addedItems.clear();
        list.addAll("Item3", "Item4");

        assertEquals(4, list.size());
        assertEquals(2, addedItems.size());
        assertTrue(addedItems.contains("Item3"));
        assertTrue(addedItems.contains("Item4"));


        addedItems.clear();
        Collection<String> collection = new ArrayList<>();
        collection.add("Item5");
        collection.add("Item6");
        list.addAll(collection);

        assertEquals(6, list.size());
        assertEquals(2, addedItems.size());
        assertTrue(addedItems.contains("Item5"));
        assertTrue(addedItems.contains("Item6"));
    }

    @Test
    public void testRemoveWithChangeListener()  {
        RemoveWithChangeListener(FXCollections.observableArrayList());
        RemoveWithChangeListener(new ObservableArray<>(Array.IDENTITY.EQUALS));
    }

    private void RemoveWithChangeListener(ObservableList<String> list) {
        List<String> removedItems = new ArrayList<>();
        assertEquals(0, list.size());
        list.add("Item1");
        assertTrue(list.contains("Item1"));
        assertEquals(1, list.size());
        list.add("Item2");
        assertTrue(list.contains("Item2"));
        assertEquals(2, list.size());
        list.add("Item3");
        assertTrue(list.contains("Item3"));
        assertEquals(3, list.size());
        list.add("Item4");
        assertEquals(4, list.size());
        assertTrue(list.contains("Item4"));

        Collection<String> containsList = new ArrayList<>();
        containsList.add("Item1");
        containsList.add("Item2");
        containsList.add("Item3");
        containsList.add("Item4");

        assertTrue(list.containsAll(containsList));

        AtomicBoolean added = new AtomicBoolean();
        AtomicBoolean replaced = new AtomicBoolean();
        AtomicBoolean permuted = new AtomicBoolean();
        AtomicBoolean updated = new AtomicBoolean();

        list.addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                added.set(change.wasAdded());
                replaced.set(change.wasReplaced());
                permuted.set(change.wasPermutated());
                updated.set(change.wasUpdated());
                if (change.wasRemoved()) {
                    removedItems.addAll(change.getRemoved());
                }
            }
        });


        list.remove("Item1");

        assertEquals(3, list.size());
        assertFalse(added.get());
        assertFalse(replaced.get());
        assertFalse(permuted.get());
        assertFalse(updated.get());
        assertEquals(1, removedItems.size());
        assertTrue(removedItems.contains("Item1"));

        removedItems.clear();
        list.removeAll("Item3", "Item4");

        assertEquals(1, list.size());
        assertFalse(added.get());
        assertFalse(replaced.get());
        assertFalse(permuted.get());
        assertFalse(updated.get());
        assertEquals(2, removedItems.size());
        assertTrue(removedItems.contains("Item3"));
        assertTrue(removedItems.contains("Item4"));
    }


}