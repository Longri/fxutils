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
package de.longri.mapsforge;

/**
 * Created by Longri on 21.01.2016.
 */
public class MemoryUsage {
    private static long maxMemory;

    private static int GcCount = 0;

    public static void chekMemory() {
        if (GcCount++ > 1000) {
            System.gc();
            GcCount = 0;
        }

        maxMemory = Math.max(maxMemory, getMemoryUsage());
    }

    public static void resetMemoryUsage() {
        System.gc();
        maxMemory = 0;
        System.gc();
    }

    public static long getMaxMemory() {
        return maxMemory;
    }

    private static long getMemoryUsage() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }


}
