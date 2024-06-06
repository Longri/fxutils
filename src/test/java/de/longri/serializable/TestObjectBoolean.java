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
public class TestObjectBoolean implements Serializable {

    protected boolean value1;
    protected boolean value2;
    protected boolean value3;


    @Override
    public void serialize(StoreBase writer) throws NotImplementedException {
        writer.write(value1);
        writer.write(value2);
        writer.write(value3);
    }

    @Override
    public void deserialize(StoreBase reader) throws NotImplementedException {
        value1 = reader.readBool();
        value2 = reader.readBool();
        value3 = reader.readBool();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TestObjectBoolean) {
            TestObjectBoolean obj = (TestObjectBoolean) other;

            if (obj.value1 != this.value1) return false;
            if (obj.value2 != this.value2) return false;
            if (obj.value3 != this.value3) return false;

            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("value1=" + value1 + "\n");
        sb.append("value2=" + value2 + "\n");
        sb.append("value3=" + value3 + "\n");
        return sb.toString();
    }
}
