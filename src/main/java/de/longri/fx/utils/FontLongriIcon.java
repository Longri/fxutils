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

public enum FontLongriIcon implements GlyphIcons {
    KEY_OUTLINE("A"),
    COPY("B"),
    HEART_OUTLINE("D"),
    HEART_FILL("E"),
    SAVE_DISK("F"),
    RELOAD("G"),
    SEARCH("H"),
    EXPORT("I"),
    IMPORT("C"),
    ATANTION("J"),
    KEY_FILLED_EDIT("W"),
    KEY_FILLED("K"),
    KEY_FILLED_ADD("X"),
    KEY_FILLED_DELETE("Y"),
    PEN("L"),
    SETTINGS("M"),
    SETTINGS2("d"),
    PRINT("N"),
    SYNC("O"),
    EYE("P"),
    EYE_NOT("U"),
    TRASH("Q"),
    EDIT("R"),
    LOCK_CLOSE("S"),
    LOCK_OPEN("T"),
    FOLDER_OPEN("a"),
    FOLDER_CLOSE("Z"),
    HDD("V"),
    FILE_DOC("e"),
    FILE_PDF("f"),
    FILE_TXT("g"),
    FILE_JPG("h"),
    FILE_MP4("i"),
    FILE_PNG("j"),
    FILE_XLS("k"),
    FILE_ZIP("l");


    private final String unicode;
    public final static String TTF_PATH = "/de/longri/fx/utils/LongriSvgFont.ttf";

    FontLongriIcon(String unicode) {
        this.unicode = unicode;
    }

    @Override
    public String unicode() {
        return unicode;
    }

    @Override
    public String fontFamily() {
        return "LongriSvgFont";
    }
}
