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

class StoreBaseTest {

    @Test
    void Base64String() throws NotImplementedException {

        TestObjectByte obj = new TestObjectByte();

        obj.value1 = -36;
        obj.value2 = 117;
        obj.value3 = 36;
        obj.value4 = Byte.MIN_VALUE;
        obj.value5 = Byte.MAX_VALUE;
        obj.value6 = 0;
        obj.value7 = Byte.MIN_VALUE + 1;
        obj.value8 = Byte.MAX_VALUE - 1;

        obj.value9 = Byte.MIN_VALUE + (Byte.MAX_VALUE / 2);
        obj.value10 = Byte.MAX_VALUE - (Byte.MAX_VALUE / 2);

        obj.value11 = Byte.MIN_VALUE + (Byte.MAX_VALUE / 4);
        obj.value12 = Byte.MAX_VALUE - (Byte.MAX_VALUE / 4);

        obj.value13 = Byte.MIN_VALUE + (Byte.MAX_VALUE / 8);
        obj.value14 = Byte.MAX_VALUE - (Byte.MAX_VALUE / 8);

        TestObjectByte obj2 = new TestObjectByte();

        BitStore writer = new BitStore();
        obj.serialize(writer);

        String base64String = writer.getBase64String();
        BitStore bitStore = new BitStore(base64String);

        obj2.deserialize(bitStore);
        assertEquals(obj, obj2);

    }
}