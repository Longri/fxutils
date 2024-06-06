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
public class BooleanStore {


    public enum Bitmask {

        BIT_0((byte) (1 << 0)), BIT_1((byte) (1 << 1)), BIT_2((byte) (1 << 2)), BIT_3((byte) (1 << 3)),
        BIT_4((byte) (1 << 4)), BIT_5((byte) (1 << 5)), BIT_6((byte) (1 << 6)), BIT_7((byte) (1 << 7));
        private int value;

        Bitmask(byte value) {
            this.value = value;
        }

    }

     byte mValue;

    public BooleanStore(byte b) {
        mValue = b;
    }

    public BooleanStore() {
        mValue = (byte) 0;
    }

    public Byte getByte() {
        return mValue;
    }

    public void store(Bitmask bit, boolean value) {
        if (get(bit) == value) return;

        if (value) {
            mValue |= bit.value;
        } else {
            mValue &= ~bit.value;
        }
    }

    public boolean get(Bitmask bit) {
        return (mValue & bit.value) == bit.value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof BooleanStore) {
            BooleanStore obj = (BooleanStore) other;
            if (obj.mValue == ((BooleanStore) other).mValue) return true;
        }
        return false;
    }


    public String toString() {
        byte[] masks = {-128, 64, 32, 16, 8, 4, 2, 1};
        StringBuilder builder = new StringBuilder();
        for (byte m : masks) {
            if ((mValue & m) == m) {
                builder.append('1');
            } else {
                builder.append('0');
            }
        }
        return builder.toString();
    }

}
