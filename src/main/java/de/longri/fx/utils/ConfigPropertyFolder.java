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

import java.util.ArrayList;

public abstract class ConfigPropertyFolder implements I_ConfigProperty {

    protected ArrayList<ConfigProperty> properties = new ArrayList<>();
    protected ArrayList<ConfigPropertyFolder> folders = new ArrayList<>();

    public ConfigPropertyFolder() {
        initial();
    }

    protected abstract void initial();

    public void addChild(ConfigPropertyFolder folder) {
        folders.add(folder);
    }

    public void addChild(ConfigProperty property) {
        properties.add(property);
    }

    @Override
    public String getPreferencesId() {
        return null; // don't need this
    }
}
