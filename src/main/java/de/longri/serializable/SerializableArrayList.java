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


import java.util.ArrayList;

/**
 * Created by Longri on 04.11.15.
 */
public class SerializableArrayList<T extends Serializable> implements Serializable {

    private ArrayList<T> list = new ArrayList<T>();
    private final Class<T> tClass;

    public SerializableArrayList(Class<T> tClass) {
        this.tClass = tClass;
    }


    @Override
    public void serialize(StoreBase writer) {

        try {
            writer.write(list.size());
            ArrayList<Byte> byteArrayList = new ArrayList<Byte>();

            for (T t : list) {
                t.serialize(writer);
            }
        } catch (NotImplementedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void deserialize(StoreBase reader) throws NotImplementedException {
        list = reader.readList(tClass);
    }

    public void add(T object) {
        list.add(object);
    }

    public T get(int index) {
        if (list.size() < index) return null;
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

}
