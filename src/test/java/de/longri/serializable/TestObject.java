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

/**
 * Created by Longri on 03.11.15.
 */
public class TestObject implements Serializable {

    protected int IntegerValue1;
    protected int IntegerValue2;
    protected boolean bool1;
    protected boolean bool2;
    protected boolean bool3;


    @Override
    public void serialize(StoreBase writer) throws NotImplementedException {


        BooleanStore booleanStore = new BooleanStore();
        booleanStore.store(BooleanStore.Bitmask.BIT_0, bool1);
        booleanStore.store(BooleanStore.Bitmask.BIT_1, bool2);
        booleanStore.store(BooleanStore.Bitmask.BIT_2, bool3);

        writer.write(booleanStore);
        writer.write(IntegerValue1);
        writer.write(IntegerValue2);

    }

    @Override
    public void deserialize(StoreBase reader) throws NotImplementedException {
        BooleanStore booleanStore = new BooleanStore(reader.readByte());

        bool1 = booleanStore.get(BooleanStore.Bitmask.BIT_0);
        bool2 = booleanStore.get(BooleanStore.Bitmask.BIT_1);
        bool3 = booleanStore.get(BooleanStore.Bitmask.BIT_2);

        IntegerValue1 = reader.readInt();
        IntegerValue2 = reader.readInt();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TestObject) {
            TestObject obj = (TestObject) other;

            if (obj.IntegerValue1 != this.IntegerValue1) return false;
            if (obj.IntegerValue2 != this.IntegerValue2) return false;
            if (obj.bool1 != this.bool1) return false;
            if (obj.bool2 != this.bool2) return false;
            if (obj.bool3 != this.bool3) return false;

            return true;
        }
        return false;
    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(IntegerValue1);
        stringBuilder.append(" / ");
        stringBuilder.append(IntegerValue2);
        stringBuilder.append(" / ");
        if (bool3) {
            stringBuilder.append('1');
        } else {
            stringBuilder.append('0');
        }
        if (bool2) {
            stringBuilder.append('1');
        } else {
            stringBuilder.append('0');
        }
        if (bool1) {
            stringBuilder.append('1');
        } else {
            stringBuilder.append('0');
        }
        return stringBuilder.toString();
    }

}
