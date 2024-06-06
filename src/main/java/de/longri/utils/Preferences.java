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

import de.longri.fx.FxmlScene;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public abstract class Preferences {
    final Logger log = LoggerFactory.getLogger(Preferences.class);

    public static final String SPLIT1 = "#-#";

    public abstract void put(String key, Float value);

    public abstract void put(String key, Double value);

    public abstract void put(String key, Long value);

    public abstract void put(String key, Integer value);

    public abstract void put(String key, String value);

    public abstract void put(String key, Dimension value);

    public abstract void put(String key, Boolean value);

    public abstract void put(String key, byte[] bytes);

    public abstract String getString(String key);

    public abstract int getInteger(String key);

    public abstract long getLong(String key);

    public abstract double getDouble(String key);

    public abstract float getFloat(String key);

    public abstract boolean getBoolean(String key);

    public abstract byte[] getBytes(String key);

    public void put(String key, Stage stage) {
        StringBuilder builder = new StringBuilder();
        builder.append("stageX:").append(stage.getX()).append(FileStoredPreferences.SPLIT1);
        builder.append("stageY:").append(stage.getY()).append(FileStoredPreferences.SPLIT1);
        builder.append("stageWidth:").append(stage.getWidth()).append(FileStoredPreferences.SPLIT1);
        builder.append("stageHeight:").append(stage.getHeight()).append(FileStoredPreferences.SPLIT1);
        log.debug("store stage bounds {} => {}", key, builder.toString());
        put(key, builder.toString());
    }

    public void setStageBounds(FxmlScene scene, Stage stage) {
        String key = scene.getName();
        setStageBounds(key, stage, scene.RESIZABLE);
    }

    /**
     * @param key
     * @param stage
     * @param isResizeable scene.RESIZABLE
     */
    public void setStageBounds(String key, Stage stage, boolean isResizeable) {
        String str = this.getString(key);
        if (str == null) {
            // center on primary screen
            Rectangle2D primaryBounds = Screen.getPrimary().getBounds();
            double x = (primaryBounds.getWidth() - stage.getWidth()) / 2;
            double y = (primaryBounds.getHeight() - stage.getHeight()) / 2;
            log.debug("Center stage bounds x:{} | y:{} | width:{} | height:{}", x, y, stage.getWidth(), stage.getHeight());
            stage.setX(x);
            stage.setY(y);
            return;
        }

        double oldWidth = stage.getWidth();
        double oldHeight = stage.getHeight();
        String[] strArr = str.split(FileStoredPreferences.SPLIT1);

        if (strArr.length != 4) return;
        double x = 0;
        double y = 0;
        double w = 0;
        double h = 0;
        try {
            x = Double.parseDouble(strArr[0].split(":")[1]);
            y = Double.parseDouble(strArr[1].split(":")[1]);
            w = isResizeable ? Double.parseDouble(strArr[2].split(":")[1]) : oldWidth;
            h = isResizeable ? Double.parseDouble(strArr[3].split(":")[1]) : oldHeight;
        } catch (NumberFormatException e) {
            log.error("can't set Stage bounds from: {}", str);
            return;
        }

        Rectangle2D completeBounds = new Rectangle2D(0, 0, 0, 0);
        ObservableList<Screen> screens = Screen.getScreens();

        for (Screen screen : screens) {

            Rectangle2D screenBounds = screen.getBounds();
            double minx = Math.min(completeBounds.getMinX(), screenBounds.getMinX());
            double miny = Math.min(completeBounds.getMinY(), screenBounds.getMinY());
            double maxx = Math.max(completeBounds.getMaxX(), screenBounds.getMaxX());
            double maxy = Math.max(completeBounds.getMaxY(), screenBounds.getMaxY());
            completeBounds = new Rectangle2D(minx, miny, maxx - minx, maxy - miny);
        }

        if (!completeBounds.contains(x, y, w, h)) { //todo if outside
            log.debug("Stage outside of visible screen, so Center Stage on Screen");
            stage.setWidth(w);
            stage.setHeight(h);
            stage.centerOnScreen();
            return;
        }

        log.debug("set stage bounds x:{} | y:{} | width:{} | height:{}", x, y, w, h);
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(w);
        stage.setHeight(h);
    }

    public Dimension getDimension(String key) {
        String value = getString(key);
        if (value == null || value.isEmpty() || !value.contains(":")) return new Dimension();
        String[] values = value.split(":");
        return new Dimension(Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim()));
    }

    public abstract boolean contains(String key);

    public abstract void remove(String key);


}
