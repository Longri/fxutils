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

import de.longri.gdx_utils.files.FileHandle;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;

public class FileStoredPreferences extends Preferences {

    final FileHandle propertiesFile;
    protected final Properties properties;


    public FileStoredPreferences(FileHandle propertieFile) {
        this.propertiesFile = propertieFile;
        this.properties = new Properties();

        if (propertieFile.exists()) {
            try {
                properties.loadFromXML(propertieFile.read());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            propertiesFile.parent().mkdirs();
            this.store();
        }
    }

    @Override
    public void put(String key, Float value) {
        properties.setProperty(key, Float.toString(value));
        store();
    }

    @Override
    public void put(String key, Double value) {
        properties.setProperty(key, Double.toString(value));
        store();
    }

    @Override
    public void put(String key, Long value) {
        properties.setProperty(key, Long.toString(value));
        store();
    }

    @Override
    public void put(String key, Integer value) {
        properties.setProperty(key, Integer.toString(value));
        store();
    }

    @Override
    public void put(String key, String value) {
        properties.setProperty(key, value);
        store();
    }

    @Override
    public void put(String key, Dimension value) {
        StringBuilder sb = new StringBuilder();
        sb.append(value.width).append(':').append(value.height);
        properties.setProperty(key, sb.toString());
        store();
    }

    @Override
    public void put(String key, Boolean value) {
        properties.setProperty(key, Boolean.toString(value));
        store();
    }

    @Override
    public void put(String key, byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Byte.toString(b)).append(":");
        }
        properties.setProperty(key, sb.toString());
        store();
    }

    @Override
    public String getString(String key) {
        return properties.getProperty(key);
    }

    @Override
    public int getInteger(String key) {
        String propertyString = properties.getProperty(key);
        if (propertyString == null || propertyString.isEmpty()) return 0;
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getLong(String key) {
        String propertyString = properties.getProperty(key);
        if (propertyString == null || propertyString.isEmpty()) return 0;
        try {
            return Long.parseLong(properties.getProperty(key));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getDouble(String key) {
        String propertyString = properties.getProperty(key);
        if (propertyString == null || propertyString.isEmpty()) return 0;
        try {
            return Double.parseDouble(properties.getProperty(key));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public float getFloat(String key) {
        String propertyString = properties.getProperty(key);
        if (propertyString == null || propertyString.isEmpty()) return 0;
        try {
            return Float.parseFloat(properties.getProperty(key));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public boolean getBoolean(String key) {
        String propertyString = properties.getProperty(key);
        if (propertyString == null || propertyString.isEmpty()) return false;
        try {
            return Boolean.parseBoolean(properties.getProperty(key));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public byte[] getBytes(String key) {

        String v = properties.getProperty(key);
        String[] byteArr = v.split(":");
        byte[] bytes = new byte[byteArr.length];

        for (int i = 0; i < byteArr.length; i++) {
            bytes[i] = Byte.parseByte(byteArr[i]);
        }
        return bytes;
    }

    @Override
    public boolean contains(String key) {
        return this.properties.containsKey(key);
    }

    private void store() {
        try {
            properties.storeToXML(propertiesFile.write(false), null);
        } catch (IOException e) {
            log.error("Can't write preferences!", e);
        }
    }

    @Override
    public void remove(String key) {
        properties.remove(key);
        store();
    }

}