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

import java.awt.*;
import java.util.prefs.BackingStoreException;

public class SystemStoredPreferences extends Preferences {

    private final java.util.prefs.Preferences prefs;

    public SystemStoredPreferences() {
        prefs = java.util.prefs.Preferences.userRoot().node(this.getClass().getName());
    }

    @Override
    public void put(String key, Float value) {
        prefs.putFloat(key, value);
    }

    @Override
    public void put(String key, Double value) {
        prefs.putDouble(key, value);
    }

    @Override
    public void put(String key, Long value) {
        prefs.putLong(key, value);
    }

    @Override
    public void put(String key, Integer value) {
        prefs.putInt(key, value);
    }

    @Override
    public void put(String key, String value) {
        prefs.put(key, value);
    }

    @Override
    public void put(String key, Dimension value) {
        String v = "" + value.width + ":" + value.height;
        prefs.put(key, v);
    }

    @Override
    public void put(String key, Boolean value) {
        prefs.putBoolean(key, value);
    }

    @Override
    public void put(String key, byte[] bytes) {
        prefs.putByteArray(key, bytes);
    }

    @Override
    public String getString(String key) {
        return prefs.get(key, "");
    }

    @Override
    public int getInteger(String key) {
        return prefs.getInt(key, 0);
    }

    @Override
    public long getLong(String key) {
        return prefs.getLong(key, 0);
    }

    @Override
    public double getDouble(String key) {
        return prefs.getDouble(key, 0.0);
    }

    @Override
    public float getFloat(String key) {
        return prefs.getFloat(key, 0.0f);
    }


    @Override
    public boolean getBoolean(String key) {
        return prefs.getBoolean(key, false);
    }

    @Override
    public byte[] getBytes(String key) {
        return prefs.getByteArray(key, null);
    }

    @Override
    public boolean contains(String key) {

        try {
            for (String k : this.prefs.keys()) {
                if (k.equals(key)) return true;
            }
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void remove(String key) {
        this.prefs.remove(key);
    }
}
