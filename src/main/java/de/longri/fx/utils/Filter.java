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


import de.longri.gdx_utils.Array;

public class Filter<T> {

    private final Array<FilterChangeListener> listeners = new Array<>();

    private boolean disabled = false;

    public Filter() {
        super();
    }

    public void addChangeListener(FilterChangeListener listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(FilterChangeListener listener) {
        listeners.remove(listener);
    }

    public void startPredictTest() {
    }

    public boolean predictTest(T test) {
        return true;
    }

    public void finishPredictTest() {
    }

    public void fireFilterChanged() {
        for (FilterChangeListener l : listeners) {
            l.changed();
        }
    }

    public void disable(boolean disable) {
        if (disabled == disable) return;
        this.disabled = disable;
        fireFilterChanged();
    }

    public boolean isDisabled() {
        return this.disabled;
    }

}
