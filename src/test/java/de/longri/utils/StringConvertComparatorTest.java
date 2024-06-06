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

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class StringConvertComparatorTest {

    @Test
    void compareInt() {
        StringConvertComparator comparator = new StringConvertComparator(StringConvertComparator.Type.INT) {
            @Override
            public String processString(String s) {
                return s.replaceAll(Pattern.quote("*"), "").trim();
            }
        };

        assertEquals(0, comparator.compare(null, null));
        assertEquals(1, comparator.compare("10", null));
        assertEquals(-1, comparator.compare(null, "10"));
        assertEquals(0, comparator.compare("NULL", "NULL"));
        assertEquals(-1, comparator.compare("NULL", "10"));
        assertEquals(1, comparator.compare("10", "NULL"));
        assertEquals(0, comparator.compare("10", "10"));
        assertEquals(-90, comparator.compare("10", "100"));
        assertEquals(90, comparator.compare("100", "10"));
        assertEquals(0, comparator.compare("10 *", "10*"));
        assertEquals(-90, comparator.compare("10  *  ", "*100"));
        assertEquals(90, comparator.compare(" 100*", " * 10"));
    }


    @Test
    void compareDate() {
        StringConvertComparator comparator = new StringConvertComparator(StringConvertComparator.Type.DATE) {
            @Override
            public String processString(String s) {
                return s;
            }
        };
        assertEquals(0, comparator.compare(null, null));
        assertEquals(1, comparator.compare("4.1.2022 10:22:00", null));
        assertEquals(-1, comparator.compare(null, "4.1.2022 10:22:00"));
        assertEquals(0, comparator.compare("NULL", "NULL"));
        assertEquals(-1, comparator.compare("NULL", "4.1.2022 10:22:00"));
        assertEquals(1, comparator.compare("4.1.2022 10:22:00", "NULL"));

        assertEquals(0, comparator.compare("4.1.2022 10:22:00", "4.1.2022 10:22:00"));
        assertEquals(1, comparator.compare("4.1.2022 10:22:00", "4.1.2021 10:22:00"));
        assertEquals(-1, comparator.compare("4.1.2021 10:22:00", "4.1.2022 10:22:00"));
    }
}