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
package de.longri.fx;

import de.longri.fx.translation.Language;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class TRANSLATION {

    private static FxTranslationBinding binding;
    private static ArrayList<Language> languages = new ArrayList<>();

    /**
     * Use Initial and bundle over @loadBundle()
     *
     * @param bundleName
     */
    @Deprecated
    public static void INITIAL(String bundleName) {
        binding = new FxTranslationBinding(bundleName);
    }

    public static void INITIAL(Language... langs) {
        binding = new FxTranslationBinding("");
        languages.addAll(Arrays.stream(langs).toList());
    }

    public static ArrayList<Language> getLanguages() {
        return languages;
    }

    public static void loadBundle(String name, Locale... locals) {
        binding.loadBundle(name, locals);
    }

    public static void setLocale(Locale locale) {
        Platform.runLater(() -> binding.setLocale(locale));
    }

    public static Locale getLocale() {
        return binding.getLocale();
    }

    public static void addChangeListener(ChangeListener<Locale> listener) {
        binding.addChangeListener(listener);
    }

    public static void removeChangeListener(ChangeListener<Locale> listener) {
        binding.removeChangeListener(listener);
    }

    // Label
    public static void bind(Label lbl, final String key, Tooltip tooltip, final Object... args) {
        binding.bind(lbl, key, tooltip, args);
    }

    public static void bind(Label lbl, final String key, final Object... args) {
        binding.bind(lbl, key, args);
    }

    public static void bind(Label lbl, final Object... args) {
        binding.bind(lbl, lbl.getId(), args);
    }

    public static void unbind(Label lbl) {
        binding.unbind(lbl);
    }

    // Button
    public static void bind(Button btn, final String key, final Object... args) {
        binding.bind(btn, key, args);
    }

    public static void bind(Button btn, final Object... args) {
        binding.bind(btn, btn.getId(), args);
    }

    public static void unbind(Button btn) {
        binding.unbind(btn);
    }

    public static void bind(TableColumn col, final String key, final Object... args) {
        binding.bind(col, key, args);
    }

    public static void bind(TableColumn col, final Object... args) {
        binding.bind(col, col.getId(), args);
    }

    public static void unbind(TableColumn col) {
        binding.unbind(col);
    }

    public static void bind(Stage stage, final String key, final Object... args) {
        binding.bind(stage, key, args);
    }

    public static void unbind(Stage stage) {
        binding.unbind(stage);
    }

    public static void bind(Menu mnu, final String key, final Object... args) {
        binding.bind(mnu, key, args);
    }

    public static void unbind(Menu mnu) {
        binding.unbind(mnu);
    }

    public static void bind(MenuItem mnu, final String key, final Object... args) {
        binding.bind(mnu, key, args);
    }

    public static void unbind(MenuItem mnu) {
        binding.unbind(mnu);
    }

    public static void bind(Text txt, final String key, final Object... args) {
        binding.bind(txt, key, args);
    }

    public static void unbind(Text txt) {
        binding.unbind(txt);
    }

    public static String getTranslation(final String key, final Object... args) {
        return binding.get(key, args);
    }

    public static void set(Label lbl, final String key, final Object... args) {
        binding.set(lbl, key, args);
    }

    public static void set(Button btn, final String key, final Object... args) {
        binding.set(btn, key, args);
    }

    public static void set(TableColumn col, final String key, final Object... args) {
        binding.set(col, key, args);
    }

    public static void set(Stage stage, final String key, final Object... args) {
        binding.set(stage, key, args);
    }

    public static void set(Menu mnu, final String key, final Object... args) {
        binding.set(mnu, key, args);
    }

    public static void set(MenuItem mnu, final String key, final Object... args) {
        binding.set(mnu, key, args);
    }

    public static void bind(ToggleButton btn, final String key, final String keySelected, final Object... args) {
        binding.bind(btn, key, keySelected, args);
    }

    public static void unbind(ToggleButton btn) {
        binding.unbind(btn);
    }

    public static void bind(CheckBox chk, final String key, final Object... args) {
        binding.bind(chk, key, args);
    }

    public static void bind(CheckBox chk, final Object... args) {
        binding.bind(chk, chk.getId(), args);
    }

    public static void unbind(CheckBox chk) {
        binding.unbind(chk);
    }

    public static void bind(Labeled labeled, final String key, final Object... args) {
        binding.bind(labeled, key, args);
    }

    public static void bind(Labeled labeled, final Object... args) {
        binding.bind(labeled, labeled.getId(), args);
    }

    public static void unbind(Labeled labeled) {
        binding.unbind(labeled);
    }

    public static void bind(StringProperty property, final String key, final Object... args) {
        binding.bind(property, key, args);
    }

    public static void unbind(StringProperty property) {
        binding.unbind(property);
    }

    public static void bind(Tab tab, final String key, final Object... args) {
        binding.bind(tab, key, args);
    }

    public static void bind(Tab tab, final Object... args) {
        binding.bind(tab, tab.getId(), args);
    }

    public static void unbind(Tab tab) {
        binding.unbind(tab);
    }


}
