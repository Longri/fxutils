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

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Hoepfner on 11.11.2015.
 */
public class ByteArrayTest  {


    @Test
    public void testByteArrayConstructors() throws Exception {


        short s = -1;

        ByteArray ba = new ByteArray(s);
        assertTrue(ba.bitLength() == 16);
        assertTrue(ba.toByteArray().length == 2);
        assertTrue(ba.byteValue() == -1);
        assertTrue(ba.shortValue() == -1);
        assertTrue(ba.intValue() == 65535);

        ba.shiftLeft(1);
        assertTrue(ba.bitLength() == 16);
        assertTrue(ba.toByteArray().length == 2);
        assertTrue(ba.byteValue() == -2);
        assertTrue(ba.shortValue() == -2);
        assertTrue(ba.intValue() == 65534);

        ba = new ByteArray(3, s);
        assertTrue(ba.bitLength() == 16);
        assertTrue(ba.toByteArray().length == 3);
        assertTrue(ba.byteValue() == -1);
        assertTrue(ba.shortValue() == -1);
        assertTrue(ba.intValue() == 65535);

        ba.shiftLeft(1);
        assertTrue(ba.bitLength() == 17);
        assertTrue(ba.toByteArray().length == 3);
        assertTrue(ba.shortValue() == -2);
        assertTrue(ba.intValue() == 131070);
        assertTrue(ba.toByteArray()[0] == 1);


        ba = new ByteArray(4, s);
        assertTrue(ba.bitLength() == 16);
        assertTrue(ba.toByteArray().length == 4);
        assertTrue(ba.intValue() == 65535);


        int i = -1;

        ba = new ByteArray(i);
        assertTrue(ba.bitLength() == 32);
        assertTrue(ba.toByteArray().length == 4);
        assertTrue(ba.intValue() == -1);

        ba.shiftLeft(1);
        assertTrue(ba.bitLength() == 32);
        assertTrue(ba.toByteArray().length == 4);
        assertTrue(ba.intValue() == -2);

        ba = new ByteArray(5, i);
        assertTrue(ba.bitLength() == 32);
        assertTrue(ba.toByteArray().length == 5);
        assertTrue(ba.intValue() == -1);
        assertTrue(ba.longValue() == 4294967295L);

        ba.shiftLeft(1);
        assertTrue(ba.bitLength() == 33);
        assertTrue(ba.toByteArray().length == 5);
        assertTrue(ba.intValue() == -2);
        assertTrue(ba.toByteArray()[0] == 1);


        ba = new ByteArray(6, i);
        assertTrue(ba.bitLength() == 32);
        assertTrue(ba.toByteArray().length == 6);
        assertTrue(ba.intValue() == -1);

        long l = -1;

        ba = new ByteArray(l);
        assertTrue(ba.bitLength() == 64);
        assertTrue(ba.toByteArray().length == 8);
        assertTrue(ba.longValue() == -1);

        ba.shiftLeft(1);
        assertTrue(ba.bitLength() == 64);
        assertTrue(ba.toByteArray().length == 8);
        assertTrue(ba.intValue() == -2);


        ba = new ByteArray(9, l);
        assertTrue(ba.bitLength() == 64);
        assertTrue(ba.toByteArray().length == 9);
        assertTrue(ba.longValue() == -1);

        ba.shiftLeft(1);
        assertTrue(ba.bitLength() == 65);
        assertTrue(ba.toByteArray().length == 9);
        assertTrue(ba.intValue() == -2);
        assertTrue(ba.toByteArray()[0] == 1);

        ba = new ByteArray(10, l);
        assertTrue(ba.bitLength() == 64);
        assertTrue(ba.toByteArray().length == 10);
        assertTrue(ba.longValue() == -1);

    }

    @Test
    public void testByteArrayLeftShift() throws Exception {

        int i = -1;
        ByteArray ba = new ByteArray(i);

        ba.shiftLeft(10);
        assertTrue(ba.intValue() == -1024);

        ba = new ByteArray(i);

        ba.shiftLeft(20);
        assertTrue(ba.intValue() == -1048576);

        ba = new ByteArray(i);

        ba.shiftLeft(32);
        assertTrue(ba.intValue() == 0);

        ba.shiftLeft(48);
        assertTrue(ba.intValue() == 0);

    }

    @Test
    public void testByteArrayRightShift() throws Exception {

        int i = Integer.MAX_VALUE;

        ByteArray ba = new ByteArray(i);

        ba.shiftRight(1);
        assertTrue(ba.intValue() == 1073741823);


        ba = new ByteArray(i);

        ba.shiftRight(10);
        assertTrue(ba.intValue() == 2097151);

        ba = new ByteArray(i);

        ba.shiftRight(20);
        assertTrue(ba.intValue() == 2047);

        ba = new ByteArray(i);

        ba.shiftRight(32);
        assertTrue(ba.intValue() == 0);

        ba.shiftRight(48);
        assertTrue(ba.intValue() == 0);

    }


}