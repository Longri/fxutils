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
package de.longri.fx.translation;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Language {

    public static final Language ENG = new Language(Locale.ENGLISH, "/de/longri/fx/translation/en.png");
    public static final Language GER = new Language(Locale.GERMAN, "/de/longri/fx/translation/de.png");

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<Image> image;
    private final Locale LOCALE;

    public Language(Locale locale, String image) {
        this.LOCALE = locale;
        this.name = new ReadOnlyStringWrapper(locale.getLanguage());
        this.image = new ReadOnlyObjectWrapper<>(
                new Image(getClass().getResourceAsStream(image))
        );
    }

    public ReadOnlyStringProperty nameProperty() {
        return this.name.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Image> imageProperty() {
        return this.image.getReadOnlyProperty();
    }

    public Locale getLocale() {
        return LOCALE;
    }

    @Override
    public String toString() {
        return this.LOCALE.getLanguage();
    }

    public static Language getUserLanguage() {
        String lng = System.getProperty("user.language");
        switch (lng.toLowerCase()) {
            case "de":
                return GER;
            case "en":
                return ENG;
        }
        return ENG;
    }

    public static Language getUserPrefLanguage() {
        Preferences systemPref = Preferences.userNodeForPackage(Language.class);
        String lng = systemPref.get("fxSystem.language", "unknown");
        switch (lng.toLowerCase()) {
            case "unknown":
                return getUserLanguage();
            case "de":
                return GER;
            case "en":
                return ENG;
        }
        return ENG;
    }

    public static void storeSystem(Locale newValue) {
        String lng = newValue.getLanguage();
        Preferences systemPref = Preferences.userNodeForPackage(Language.class);
        systemPref.put("fxSystem.language", lng);
        try {
            systemPref.flush();
        } catch (BackingStoreException e) {

        }
    }
}
