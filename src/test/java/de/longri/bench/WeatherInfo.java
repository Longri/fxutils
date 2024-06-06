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
package de.longri.bench;

import de.longri.serializable.NotImplementedException;
import de.longri.serializable.Serializable;
import de.longri.serializable.StoreBase;

import java.util.Date;


/**
 * Created by Longri on 15.11.15.
 */
public class WeatherInfo implements Serializable {

    int temp;
    int IconId;
    int testInt;
    Date date;
    String string;


    @Override
    public void serialize(StoreBase writer) throws NotImplementedException {
        writer.write(temp);
        writer.write(IconId);
        writer.write(testInt);
        writer.write(date.getTime());
        writer.write(string);
    }

    @Override
    public void deserialize(StoreBase reader) throws NotImplementedException {
        temp = reader.readInt();
        IconId = reader.readInt();
        testInt = reader.readInt();
        date = new Date(reader.readLong());
        string = reader.readString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof WeatherInfo) {
            WeatherInfo obj = (WeatherInfo) other;

            if (obj.temp != this.temp) return false;
            if (obj.IconId != this.IconId) return false;
            if (obj.testInt != this.testInt) return false;
            if (obj.date.getTime() != this.date.getTime()) return false;
            if (!obj.string.equals(this.string)) return false;
            return true;
        }
        return false;
    }
}
