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

import java.util.Properties;

public enum SystemType {
    MAC, WIN, LINUX, UNKNOWN;

    public static SystemType getSystemType() {
        Properties sysprops = System.getProperties();
        String osName = ((String) sysprops.get("os.name")).toLowerCase();
        if (osName.matches(".*win.*")) return SystemType.WIN;
        if (osName.matches(".*mac.*")) return SystemType.MAC;
        if (osName.matches(".*Linux.*")) return SystemType.LINUX;
        SystemType unknown = SystemType.UNKNOWN;
        return unknown;
    }

    public static boolean isWindows() {
        return getSystemType() == WIN;
    }

    public static boolean isLinux() {
        return getSystemType() == LINUX;
    }

    public static boolean isMac() {
        return getSystemType() == MAC;
    }

    public static boolean isUnknown() {
        return getSystemType() == UNKNOWN;
    }
}
