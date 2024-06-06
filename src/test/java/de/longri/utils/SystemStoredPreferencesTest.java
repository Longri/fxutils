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

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class SystemStoredPreferencesTest {

    @Test
    void prefStoreRestore() {

        SystemStoredPreferences pref = new SystemStoredPreferences();

        pref.put("Integer", 12345);
        pref.put("Long", 101234567891234L);
        pref.put("Double", 1.234567891);
        pref.put("Float", 1.234567890123444F);
        pref.put("String", "StringString");
        pref.put("Dimension", new Dimension(1234, 5678));
        pref.put("ByteArray", new byte[]{12,123,17,35, (byte) 254});


        assertEquals("12345", pref.getString("Integer"));
        assertEquals(12345, pref.getInteger("Integer"));

        assertEquals("101234567891234", pref.getString("Long"));
        assertEquals(101234567891234L, pref.getLong("Long"));

        assertEquals("1.234567891", pref.getString("Double"));
        assertEquals(1.234567891, pref.getDouble("Double"));

        assertEquals("1.2345679", pref.getString("Float"));
        assertEquals(1.2345679F, pref.getFloat("Float"));

        assertEquals(new Dimension(1234, 5678), pref.getDimension("Dimension"));
        assertArrayEquals(new byte[]{12,123,17,35, (byte) 254},pref.getBytes("ByteArray"));

        assertEquals("StringString", pref.getString("String"));

    }

}