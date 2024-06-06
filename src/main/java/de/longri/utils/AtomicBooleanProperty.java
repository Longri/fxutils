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

import de.longri.gdx_utils.Array;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanProperty {

    private final Array<ChangeListener> changeListeners = new Array<>();
    private AtomicBoolean value = new AtomicBoolean(false);

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }


    public void set(boolean newValue) {
        value.set(newValue);
        for (ChangeListener change:changeListeners){
            change.stateChanged(new ChangeEvent(this));
        }
    }

    public boolean get() {
        return value.get();
    }


}
