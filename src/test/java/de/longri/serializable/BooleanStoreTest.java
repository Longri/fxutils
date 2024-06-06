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
package de.longri.serializable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Created by Longri on 03.11.15.
 */
public class BooleanStoreTest {

    @Test
    public void testBooleanStore() throws Exception {

        BooleanStore booleanStore = new BooleanStore();

        assertEquals("00000000", booleanStore.toString());
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_0, true);
        assertEquals("00000001", booleanStore.toString());
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_7));


        booleanStore.store(BooleanStore.Bitmask.BIT_1, true);
        assertEquals("00000011", booleanStore.toString());
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_2, true);
        assertEquals("00000111", booleanStore.toString());
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_3, true);
        assertEquals("00001111", booleanStore.toString());
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_4, true);
        assertEquals("00011111", booleanStore.toString());
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_5, true);
        assertEquals("00111111", booleanStore.toString());
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_6, true);
        assertEquals("01111111", booleanStore.toString());
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_7, true);
        assertEquals("11111111", booleanStore.toString());
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_7));


        booleanStore.store(BooleanStore.Bitmask.BIT_0, false);
        assertEquals("11111110", booleanStore.toString());
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_1, false);
        assertEquals("11111100", booleanStore.toString());
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_2, false);
        assertEquals("11111000", booleanStore.toString());
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_3, false);
        assertEquals("11110000", booleanStore.toString());
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_4, false);
        assertEquals("11100000", booleanStore.toString());
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_5, false);
        assertEquals("11000000", booleanStore.toString());
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_6, false);
        assertEquals("10000000", booleanStore.toString());
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertTrue(booleanStore.get(BooleanStore.Bitmask.BIT_7));

        booleanStore.store(BooleanStore.Bitmask.BIT_7, false);
        assertEquals("00000000", booleanStore.toString());
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_0));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_1));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_2));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_3));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_4));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_5));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_6));
        assertFalse(booleanStore.get(BooleanStore.Bitmask.BIT_7));

    }
}