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
public class ListObject implements Serializable {

    protected BooleanStore booleanStore = new BooleanStore();


    @Override
    public void serialize(StoreBase writer) throws NotImplementedException {
        writer.write(booleanStore);
    }

    @Override
    public void deserialize(StoreBase reader) throws NotImplementedException {
        BooleanStore Store = new BooleanStore(reader.readByte());

        booleanStore.store(BooleanStore.Bitmask.BIT_0, Store.get(BooleanStore.Bitmask.BIT_0));
        booleanStore.store(BooleanStore.Bitmask.BIT_1, Store.get(BooleanStore.Bitmask.BIT_1));
        booleanStore.store(BooleanStore.Bitmask.BIT_2, Store.get(BooleanStore.Bitmask.BIT_2));
        booleanStore.store(BooleanStore.Bitmask.BIT_3, Store.get(BooleanStore.Bitmask.BIT_3));
        booleanStore.store(BooleanStore.Bitmask.BIT_4, Store.get(BooleanStore.Bitmask.BIT_4));
        booleanStore.store(BooleanStore.Bitmask.BIT_5, Store.get(BooleanStore.Bitmask.BIT_5));
        booleanStore.store(BooleanStore.Bitmask.BIT_6, Store.get(BooleanStore.Bitmask.BIT_6));
        booleanStore.store(BooleanStore.Bitmask.BIT_7, Store.get(BooleanStore.Bitmask.BIT_7));

    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ListObject) {
            ListObject obj = (ListObject) other;
            if (obj.booleanStore.mValue != this.booleanStore.mValue) return false;
            return true;
        }
        return false;
    }

    public String toString() {
        return booleanStore.mValue + " : " + booleanStore.toString();
    }

}
