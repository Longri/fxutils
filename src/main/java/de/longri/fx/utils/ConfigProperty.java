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
package de.longri.fx.utils;

import de.longri.utils.FileStoredPreferences;
import de.longri.utils.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.awt.*;

public abstract class ConfigProperty<T> extends SimpleObjectProperty<T> implements I_ConfigProperty {

    private final Preferences PREF;
    private T overrideValue;

    public ConfigProperty(Preferences preferences, T defaultValue, String propertyName) {
        super(defaultValue, propertyName);
        initial();
        this.PREF = preferences;

        //read value from preferences
        if (PREF.contains(getPreferencesId())) {
            Object v = getValue();
            Class<T> calzz = (Class<T>) v.getClass();
            if (v instanceof Integer) {
                this.set(calzz.cast(PREF.getInteger(getPreferencesId())));
            } else if (v instanceof Float) {
                this.set(calzz.cast(PREF.getFloat(getPreferencesId())));
            } else if (v instanceof Double) {
                this.set(calzz.cast(PREF.getDouble(getPreferencesId())));
            } else if (v instanceof Long) {
                this.set(calzz.cast(PREF.getLong(getPreferencesId())));
            } else if (v instanceof String) {
                this.set(calzz.cast(PREF.getString(getPreferencesId())));
            } else if (v instanceof Boolean) {
                this.set(calzz.cast(PREF.getBoolean(getPreferencesId())));
            } else if (v instanceof Dimension) {
                this.set(calzz.cast(PREF.getDimension(getPreferencesId())));
            } else if (v instanceof byte[]) {
                this.set(calzz.cast(PREF.getBytes(getPreferencesId())));
            }
        }

        this.addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observableValue, T t, T t1) {
                // write changes to preferences
                Object v = getValue();
                if (v instanceof Integer) {
                    PREF.put(getPreferencesId(), (Integer) v);
                } else if (v instanceof Float) {
                    PREF.put(getPreferencesId(), (Float) v);
                } else if (v instanceof Double) {
                    PREF.put(getPreferencesId(), (Double) v);
                } else if (v instanceof Long) {
                    PREF.put(getPreferencesId(), (Long) v);
                } else if (v instanceof String) {
                    PREF.put(getPreferencesId(), (String) v);
                } else if (v instanceof Boolean) {
                    PREF.put(getPreferencesId(), (Boolean) v);
                } else if (v instanceof Dimension) {
                    PREF.put(getPreferencesId(), (Dimension) v);
                } else if (v instanceof byte[]) {
                    PREF.put(getPreferencesId(), (byte[]) v);
                }
            }
        });
    }

    protected abstract void initial();

    public String getPreferencesId() {
        return getName();
    }

    public T get() {
        if (overrideValue == null)
            return super.get();
        return overrideValue;
    }

    public void setOverrideValue(T valu) {
        this.overrideValue = valu;
    }

}
