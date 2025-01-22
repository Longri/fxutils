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
import javafx.collections.ListChangeListener;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class AbstractFilteredListTest {

    static private class TestClass {
        final int ID;
        final String Name;

        private TestClass(int id, String name) {
            ID = id;
            Name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o instanceof TestClass) {
                return ((TestClass) o).ID == this.ID;
            }
            return false;
        }

        @Override
        public String toString() {
            return Name;
        }
    }

    private TestClass testClass1 = new TestClass(1, "EINS");
    private TestClass testClass2 = new TestClass(2, "ZWEI");
    private TestClass testClass3 = new TestClass(3, "DREI");
    private TestClass testClass4 = new TestClass(4, "VIER");
    private TestClass testClass5 = new TestClass(5, "FÜNF");
    private TestClass testClass6 = new TestClass(6, "SECHS");
    private TestClass testClass7 = new TestClass(7, "SIEBEN");
    private TestClass testClass8 = new TestClass(8, "ACHT");
    private TestClass testClass9 = new TestClass(9, "NEUN");
    private TestClass testClass10 = new TestClass(10, "ZEHN");

    @Test
    void add() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return true;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        assertEquals(0, testList.size);
        assertEquals(0, testList.size());

        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(0));

        testList.add(testClass1);
        assertEquals(1, testList.size);
        assertEquals(1, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(1));

        testList.add(testClass2);
        assertEquals(2, testList.size);
        assertEquals(2, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(2));
    }

    @Test
    void addAll() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return true;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        assertEquals(0, testList.size);
        assertEquals(0, testList.size());
        assertTrue(testList.isEmpty());

        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(0));

        testList.add(testClass1);
        assertEquals(1, testList.size);
        assertEquals(1, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(1));

        testList.addAll(testClass2, testClass3, testClass4);
        assertEquals(4, testList.size);
        assertEquals(4, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass3, testList.get(2));
        assertEquals(testClass4, testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(4));
        assertFalse(testList.isEmpty());
    }

    @Test
    void AddAllCollection() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return true;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        assertEquals(0, testList.size);
        assertEquals(0, testList.size());
        assertTrue(testList.isEmpty());

        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(0));

        testList.add(testClass1);
        assertEquals(1, testList.size);
        assertEquals(1, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(1));

        Collection<TestClass> collection = new ArrayList<>();
        collection.add(testClass2);
        collection.add(testClass3);
        collection.add(testClass4);


        testList.addAll(collection);
        assertEquals(4, testList.size);
        assertEquals(4, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass3, testList.get(2));
        assertEquals(testClass4, testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(4));
        assertFalse(testList.isEmpty());
    }

    @Test
    void remove() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return true;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4, testClass5, testClass6, testClass7, testClass8, testClass9, testClass10);
        assertEquals(10, testList.size);
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass3, testList.get(2));
        assertEquals(testClass4, testList.get(3));
        assertEquals(testClass5, testList.get(4));
        assertEquals(testClass6, testList.get(5));
        assertEquals(testClass7, testList.get(6));
        assertEquals(testClass8, testList.get(7));
        assertEquals(testClass9, testList.get(8));
        assertEquals(testClass10, testList.get(9));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(10));

        testList.remove(testClass3);
        assertEquals(9, testList.size);
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertEquals(testClass5, testList.get(3));
        assertEquals(testClass6, testList.get(4));
        assertEquals(testClass7, testList.get(5));
        assertEquals(testClass8, testList.get(6));
        assertEquals(testClass9, testList.get(7));
        assertEquals(testClass10, testList.get(8));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(9));

    }

    @Test
    void removeAll() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return true;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4, testClass5, testClass6, testClass7, testClass8, testClass9, testClass10);
        assertEquals(10, testList.size);
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass3, testList.get(2));
        assertEquals(testClass4, testList.get(3));
        assertEquals(testClass5, testList.get(4));
        assertEquals(testClass6, testList.get(5));
        assertEquals(testClass7, testList.get(6));
        assertEquals(testClass8, testList.get(7));
        assertEquals(testClass9, testList.get(8));
        assertEquals(testClass10, testList.get(9));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(10));

        testList.removeAll(testClass3, testClass7, testClass8);
        assertEquals(7, testList.size);
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertEquals(testClass5, testList.get(3));
        assertEquals(testClass6, testList.get(4));
        assertEquals(testClass9, testList.get(5));
        assertEquals(testClass10, testList.get(6));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(7));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(8));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(9));
    }

    @Test
    void removeAllCollection() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return true;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4, testClass5, testClass6, testClass7, testClass8, testClass9, testClass10);
        assertEquals(10, testList.size);
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass3, testList.get(2));
        assertEquals(testClass4, testList.get(3));
        assertEquals(testClass5, testList.get(4));
        assertEquals(testClass6, testList.get(5));
        assertEquals(testClass7, testList.get(6));
        assertEquals(testClass8, testList.get(7));
        assertEquals(testClass9, testList.get(8));
        assertEquals(testClass10, testList.get(9));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(10));

        Collection<TestClass> collection = new ArrayList<>();
        collection.add(testClass3);
        collection.add(testClass7);
        collection.add(testClass8);

        testList.removeAll(collection);
        assertEquals(7, testList.size);
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertEquals(testClass5, testList.get(3));
        assertEquals(testClass6, testList.get(4));
        assertEquals(testClass9, testList.get(5));
        assertEquals(testClass10, testList.get(6));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(7));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(8));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(9));
    }

    @Test
    void addFiltered() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return object.ID != 2;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        assertEquals(0, testList.size);
        assertEquals(0, testList.size());

        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(0));

        testList.add(testClass1);
        assertEquals(1, testList.size);
        assertEquals(1, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(1));

        testList.add(testClass2);
        assertEquals(2, testList.size);
        assertEquals(1, testList.filteredSize);
        assertEquals(1, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(1));

        testList.add(testClass3);
        assertEquals(3, testList.size);
        assertEquals(2, testList.filteredSize);
        assertEquals(2, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass3, testList.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(2));
    }

    @Test
    void addAllFiltered() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return object.ID != 2;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        assertEquals(0, testList.size);
        assertEquals(0, testList.size());

        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(0));

        testList.add(testClass1);
        assertEquals(1, testList.size);
        assertEquals(1, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(1));

        testList.addAll(testClass2, testClass3, testClass4);
        assertEquals(4, testList.size);
        assertEquals(3, testList.filteredSize);
        assertEquals(3, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass3, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(3));
    }

    @Test
    void AddAllCollectionFiltered() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return object.ID != 2;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        assertEquals(0, testList.size);
        assertEquals(0, testList.size());

        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(0));

        testList.add(testClass1);
        assertEquals(1, testList.size);
        assertEquals(1, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(1));

        Collection<TestClass> collection = new ArrayList<>();
        collection.add(testClass2);
        collection.add(testClass3);
        collection.add(testClass4);


        testList.addAll(collection);
        assertEquals(4, testList.size);
        assertEquals(3, testList.filteredSize);
        assertEquals(3, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass3, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(4));
    }

    @Test
    void removeFiltered() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return object.ID != 2;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4, testClass5, testClass6, testClass7, testClass8, testClass9, testClass10);
        assertEquals(10, testList.size);
        assertEquals(9, testList.filteredSize);
        assertEquals(9, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass3, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertEquals(testClass5, testList.get(3));
        assertEquals(testClass6, testList.get(4));
        assertEquals(testClass7, testList.get(5));
        assertEquals(testClass8, testList.get(6));
        assertEquals(testClass9, testList.get(7));
        assertEquals(testClass10, testList.get(8));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(9));

        testList.remove(testClass3);
        assertEquals(9, testList.size);
        assertEquals(8, testList.filteredSize);
        assertEquals(8, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass4, testList.get(1));
        assertEquals(testClass5, testList.get(2));
        assertEquals(testClass6, testList.get(3));
        assertEquals(testClass7, testList.get(4));
        assertEquals(testClass8, testList.get(5));
        assertEquals(testClass9, testList.get(6));
        assertEquals(testClass10, testList.get(7));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(8));

    }

    @Test
    void removeAllFiltered() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return object.ID != 2;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4, testClass5, testClass6, testClass7, testClass8, testClass9, testClass10);
        assertEquals(10, testList.size);
        assertEquals(9, testList.filteredSize);
        assertEquals(9, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass3, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertEquals(testClass5, testList.get(3));
        assertEquals(testClass6, testList.get(4));
        assertEquals(testClass7, testList.get(5));
        assertEquals(testClass8, testList.get(6));
        assertEquals(testClass9, testList.get(7));
        assertEquals(testClass10, testList.get(8));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(9));

        testList.removeAll(testClass3, testClass7, testClass8);
        assertEquals(7, testList.size);
        assertEquals(6, testList.filteredSize);
        assertEquals(6, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass4, testList.get(1));
        assertEquals(testClass5, testList.get(2));
        assertEquals(testClass6, testList.get(3));
        assertEquals(testClass9, testList.get(4));
        assertEquals(testClass10, testList.get(5));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(6));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(7));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(8));
    }

    @Test
    void removeAllCollectionFiltered() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return object.ID != 2;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4, testClass5, testClass6, testClass7, testClass8, testClass9, testClass10);
        assertEquals(9, testList.filteredSize);
        assertEquals(10, testList.size);
        assertEquals(9, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass3, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertEquals(testClass5, testList.get(3));
        assertEquals(testClass6, testList.get(4));
        assertEquals(testClass7, testList.get(5));
        assertEquals(testClass8, testList.get(6));
        assertEquals(testClass9, testList.get(7));
        assertEquals(testClass10, testList.get(8));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(9));

        Collection<TestClass> collection = new ArrayList<>();
        collection.add(testClass3);
        collection.add(testClass7);
        collection.add(testClass8);

        testList.removeAll(collection);
        assertEquals(6, testList.filteredSize);
        assertEquals(7, testList.size);
        assertEquals(6, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass4, testList.get(1));
        assertEquals(testClass5, testList.get(2));
        assertEquals(testClass6, testList.get(3));
        assertEquals(testClass9, testList.get(4));
        assertEquals(testClass10, testList.get(5));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(6));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(7));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(8));
    }

    @Test
    void set() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return true;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4);
        assertEquals(4, testList.size);
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass3, testList.get(2));
        assertEquals(testClass4, testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(4));

        assertEquals(testClass2, testList.set(1, testClass5));
        assertEquals(testClass4, testList.set(3, testClass7));

        assertEquals(4, testList.size);
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass5, testList.get(1));
        assertEquals(testClass3, testList.get(2));
        assertEquals(testClass7, testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(4));

        assertThrows(IndexOutOfBoundsException.class, () -> testList.set(5, testClass9));

    }

    @Test
    void setAll() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return true;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4);
        assertEquals(4, testList.size);
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass3, testList.get(2));
        assertEquals(testClass4, testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(4));

        testList.setAll(testClass6, testClass7, testClass8);

        assertEquals(3, testList.size);
        assertEquals(3, testList.filteredSize);
        assertEquals(3, testList.size());
        assertEquals(testClass6, testList.get(0));
        assertEquals(testClass7, testList.get(1));
        assertEquals(testClass8, testList.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(3));

        assertThrows(NullPointerException.class, () -> testList.setAll(testClass1, null, testClass2));

    }

    @Test
    void SetAllCollection() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return true;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4);
        assertEquals(4, testList.size);
        assertEquals(4, testList.filteredSize);
        assertEquals(4, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass3, testList.get(2));
        assertEquals(testClass4, testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(4));

        var ref = new Object() {
            Collection<TestClass> collection = new ArrayList<>();
        };
        ref.collection.add(testClass6);
        ref.collection.add(testClass7);
        ref.collection.add(testClass8);
        testList.setAll(ref.collection);

        assertEquals(3, testList.size);
        assertEquals(testClass6, testList.get(0));
        assertEquals(testClass7, testList.get(1));
        assertEquals(testClass8, testList.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(3));

        ref.collection = new ArrayList<>();
        ref.collection.add(testClass1);
        ref.collection.add(null);
        ref.collection.add(testClass2);

        assertThrows(NullPointerException.class, () -> testList.setAll(ref.collection));

    }

    @Test
    void setFiltered() {
        final boolean[] filterReset = {false};
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {


            @Override
            public boolean predictTest(TestClass object) {
                if (filterReset[0]) return true;
                return object.ID != 2;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }


        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4);
        assertEquals(4, testList.size);
        assertEquals(3, testList.filteredSize);
        assertEquals(3, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass3, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(3));

        assertEquals(testClass1, testList.set(0, testClass5));
        assertEquals(testClass4, testList.set(2, testClass7));

        assertEquals(4, testList.size);
        assertEquals(3, testList.filteredSize);
        assertEquals(3, testList.size());
        assertEquals(testClass5, testList.get(0));
        assertEquals(testClass3, testList.get(1));
        assertEquals(testClass7, testList.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.set(3, testClass9));

        //reset filter of list
        filterReset[0] = true;
        testList.reindex();

        assertEquals(4, testList.size);
        assertEquals(4, testList.filteredSize);
        assertEquals(4, testList.size());
        assertEquals(testClass5, testList.get(0));
        assertEquals(testClass2, testList.get(1));
        assertEquals(testClass3, testList.get(2));
        assertEquals(testClass7, testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(4));

    }

    @Test
    void removeFilteredChangeListener() throws InterruptedException {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return object.ID != 2;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4, testClass5, testClass6, testClass7, testClass8, testClass9, testClass10);
        assertEquals(10, testList.size);
        assertEquals(9, testList.filteredSize);
        assertEquals(9, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass3, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertEquals(testClass5, testList.get(3));
        assertEquals(testClass6, testList.get(4));
        assertEquals(testClass7, testList.get(5));
        assertEquals(testClass8, testList.get(6));
        assertEquals(testClass9, testList.get(7));
        assertEquals(testClass10, testList.get(8));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(9));

        AtomicBoolean listenerReturnEquals = new AtomicBoolean(false);
        AtomicBoolean listenerThrows = new AtomicBoolean(false);

        testList.addListener(new ListChangeListener<TestClass>() {
            @Override
            public void onChanged(Change<? extends TestClass> c) {
                listenerReturnEquals.set(testClass4.equals(testList.get(1)));
                try {
                    Object o = testList.get(8);
                } catch (IndexOutOfBoundsException e) {
                    listenerThrows.set(true);
                }
            }
        });

        testList.remove(testClass3);

        Thread.sleep(100);

        assertEquals(9, testList.size);
        assertEquals(8, testList.filteredSize);
        assertEquals(8, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass4, testList.get(1));
        assertEquals(testClass5, testList.get(2));
        assertEquals(testClass6, testList.get(3));
        assertEquals(testClass7, testList.get(4));
        assertEquals(testClass8, testList.get(5));
        assertEquals(testClass9, testList.get(6));
        assertEquals(testClass10, testList.get(7));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(8));

        Thread.sleep(100);

        assertTrue(listenerReturnEquals.get());
        assertTrue(listenerThrows.get());

    }

    @Test
    void setAllFiltered() {
        final boolean[] filterReset = {false};
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {


            @Override
            public boolean predictTest(TestClass object) {
                if (filterReset[0]) return true;
                return object.ID != 2;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }


        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4);
        assertEquals(4, testList.size);
        assertEquals(3, testList.filteredSize);
        assertEquals(3, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass3, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(3));

        assertTrue(testList.setAll(testClass2, testClass5, testClass6, testClass7));

        assertEquals(4, testList.size);
        assertEquals(3, testList.filteredSize);
        assertEquals(3, testList.size());
        assertEquals(testClass5, testList.get(0));
        assertEquals(testClass6, testList.get(1));
        assertEquals(testClass7, testList.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.set(3, testClass9));

        //reset filter of list
        filterReset[0] = true;
        testList.reindex();

        assertEquals(4, testList.size);
        assertEquals(4, testList.filteredSize);
        assertEquals(4, testList.size());
        assertEquals(testClass2, testList.get(0));
        assertEquals(testClass5, testList.get(1));
        assertEquals(testClass6, testList.get(2));
        assertEquals(testClass7, testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(4));
    }

    @Test
    void SetAllCollectionFiltered() {
        final boolean[] filterReset = {false};
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {


            @Override
            public boolean predictTest(TestClass object) {
                if (filterReset[0]) return true;
                return object.ID != 2;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }


        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4);
        assertEquals(4, testList.size);
        assertEquals(3, testList.filteredSize);
        assertEquals(3, testList.size());
        assertEquals(testClass1, testList.get(0));
        assertEquals(testClass3, testList.get(1));
        assertEquals(testClass4, testList.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(3));

        var ref = new Object() {
            Collection<TestClass> collection = new ArrayList<>();
        };
        ref.collection.add(testClass2);
        ref.collection.add(testClass5);
        ref.collection.add(testClass6);
        ref.collection.add(testClass7);
        assertTrue(testList.setAll(ref.collection));

        assertEquals(4, testList.size);
        assertEquals(3, testList.filteredSize);
        assertEquals(3, testList.size());
        assertEquals(testClass5, testList.get(0));
        assertEquals(testClass6, testList.get(1));
        assertEquals(testClass7, testList.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.set(3, testClass9));

        //reset filter of list
        filterReset[0] = true;
        testList.reindex();

        assertEquals(4, testList.size);
        assertEquals(4, testList.filteredSize);
        assertEquals(4, testList.size());
        assertEquals(testClass2, testList.get(0));
        assertEquals(testClass5, testList.get(1));
        assertEquals(testClass6, testList.get(2));
        assertEquals(testClass7, testList.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(4));
    }

    @Test
    void retainAll() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return true;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);


    }

    @Test
    void contains() {
    }

    @Test
    void containsAll() {
    }

    @Test
    void removeIf() {
    }

    @Test
    void sort() {
    }

    @Test
    void clear() {
    }

    @Test
    void indexOf() {
    }

    @Test
    void lastIndexOf() {
    }

    @Test
    void copy() {
    }

    @Test
    void itterator() {

        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                return object.ID != 2;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4, testClass5, testClass6, testClass7, testClass8, testClass9, testClass10);
        assertEquals(10, testList.size);
        assertEquals(9, testList.filteredSize);
        assertEquals(9, testList.size());
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(9));
        Array.ArrayIterator<TestClass> iterator = testList.iterator();

        TestClass[] testClassArray = new TestClass[]{testClass1, testClass3, testClass4, testClass5, testClass6, testClass7, testClass8, testClass9, testClass10};

        int idx = 0;
        for (TestClass testClass : iterator) {
            assertEquals(testClass, testClassArray[idx++]);
        }

        testList.remove(testClass3);
        assertEquals(9, testList.size);
        assertEquals(8, testList.filteredSize);
        assertEquals(8, testList.size());
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(8));
        testClassArray = new TestClass[]{testClass1, testClass4, testClass5, testClass6, testClass7, testClass8, testClass9, testClass10};

        iterator = testList.iterator();
        idx = 0;
        for (TestClass testClass : iterator) {
            assertEquals(testClass, testClassArray[idx++]);
        }


    }

    @Test
    void toArry() {
        AbstractFilteredList<TestClass> testList = new AbstractFilteredList<TestClass>() {
            @Override
            public boolean predictTest(TestClass object) {
                if (object.ID == 2 || object.ID == 3 || object.ID == 7 || object.ID == 8) return false;
                return true;
            }

            @Override
            public ListChangeListener.Change<TestClass> getChange() {
                return null;
            }

            @Override
            public int compare(TestClass o1, TestClass o2) {
                return 0;
            }
        };
        assertNotNull(testList);
        testList.addAll(testClass1, testClass2, testClass3, testClass4, testClass5, testClass6, testClass7, testClass8, testClass9, testClass10);
        assertEquals(10, testList.size);
        assertEquals(6, testList.filteredSize);
        assertEquals(6, testList.size());
        assertThrows(IndexOutOfBoundsException.class, () -> testList.get(6));

        Object[] array = testList.toArray();
        assertEquals(6, array.length);

        TestClass[] testClassArray = new TestClass[]{testClass1, testClass4, testClass5, testClass6, testClass9, testClass10};

        assertArrayEquals(testClassArray, array);
    }

}