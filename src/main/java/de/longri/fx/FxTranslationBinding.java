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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * inspired by https://riptutorial.com/javafx/example/23068/switching-language-dynamically-when-the-application-is-running
 */
public class FxTranslationBinding {

    private final static Logger log = LoggerFactory.getLogger(FxTranslationBinding.class);


    /**
     * the current selected Locale.
     */
    private final ObjectProperty<Locale> locale = createNewLocale();

    private ObjectProperty<Locale> createNewLocale() {
        SimpleObjectProperty<Locale> locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
        return locale;
    }


    private final MultiResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle();

    public FxTranslationBinding(String baseName) {
        loadBundle(baseName);
    }

    public void loadBundle(String baseName, Locale... locales) {
        RESOURCE_BUNDLE.loadBundle(baseName, locales);
    }

    /**
     * get the supported Locales.
     *
     * @return List of Locale objects.
     */
    public List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(Locale.ENGLISH, Locale.GERMAN));
    }

    /**
     * get the default locale. This is the systems default if contained in the supported locales, english otherwise.
     *
     * @return
     */
    public Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }

    public Locale getLocale() {
        return locale.get();
    }

    public void setLocale(Locale locale) {
        RESOURCE_BUNDLE.setLocale(locale);
        localeProperty().set(locale);
        Locale.setDefault(locale);
    }

    public void addChangeListener(ChangeListener<Locale> listener) {
        localeProperty().addListener(listener);
    }

    public void removeChangeListener(ChangeListener<Locale> listener) {
        localeProperty().removeListener(listener);
    }

    private ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    /**
     * gets the string with the given key from the resource bundle for the current locale and uses it as first argument
     * to MessageFormat.format, passing in the optional args and returning the result.
     *
     * @param key  message key
     * @param args optional arguments for the message
     * @return localized formatted string
     */
    public String get(final String key, final Object... args) {
        String value = RESOURCE_BUNDLE.getString(key);
        return MessageFormat.format(value, args);
    }

    /**
     * creates a String binding to a localized String for the given message bundle key
     *
     * @param key key
     * @return String binding
     */
    private StringBinding createStringBinding(final String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), locale);
    }

    /**
     * creates a String Binding to a localized String that is computed by calling the given func
     *
     * @param func function called on every change
     * @return StringBinding
     */
    private StringBinding createStringBinding(Callable<String> func) {
        return Bindings.createStringBinding(func, locale);
    }

    public void bind(Label lbl, final String key, final Object... args) {
        lbl.textProperty().bind(createStringBinding(key, args));
        setTranslationToolTip(this, lbl, key);
    }

    public void unbind(Label lbl) {
        lbl.textProperty().unbind();
    }

    public void bind(Button btn, final String key, final Object... args) {
        btn.textProperty().bind(createStringBinding(key, args));
        setTranslationToolTip(this, btn, key);
    }

    public void unbind(Button btn) {
        btn.textProperty().unbind();
    }

    public void bind(TableColumn col, final String key, final Object... args) {
        col.textProperty().bind(createStringBinding(key, args));

        if (col.getTableView() != null) {
            Node node = col.getStyleableNode();
            if (node != null)
                setTranslationToolTip(this, node, key);
        }


    }

    public void unbind(TableColumn col) {
        col.textProperty().unbind();
    }

    public void bind(Stage stage, final String key, final Object... args) {
        stage.titleProperty().bind(createStringBinding(key, args));
    }

    public void unbind(Stage stage) {
        stage.titleProperty().unbind();
    }

    public void bind(Menu mnu, final String key, final Object... args) {
        mnu.textProperty().bind(createStringBinding(key, args));
    }

    public void unbind(Menu mnu) {
        mnu.textProperty().unbind();
    }

    public void bind(MenuItem mnu, final String key, final Object... args) {
        mnu.textProperty().bind(createStringBinding(key, args));
    }

    public void unbind(MenuItem mnu) {
        mnu.textProperty().unbind();
    }

    public void bind(Text txt, final String key, final Object... args) {
        txt.textProperty().bind(createStringBinding(key, args));
        setTranslationToolTip(this, txt, key);
    }

    public void unbind(Text txt) {
        txt.textProperty().unbind();
    }

    public void bind(CheckBox chk, final String key, final Object... args) {
        chk.textProperty().bind(createStringBinding(key, args));
        setTranslationToolTip(this, chk, key);
    }

    public void unbind(CheckBox chk) {
        chk.textProperty().unbind();
    }

    public void set(Label lbl, final String key, final Object... args) {
        lbl.textProperty().set(get(key, args));
    }

    public void set(Button btn, final String key, final Object... args) {
        btn.textProperty().set(get(key, args));
    }

    public void set(TableColumn col, final String key, final Object... args) {
        col.textProperty().set(get(key, args));
    }

    public void set(Stage stage, final String key, final Object... args) {
        stage.titleProperty().set(get(key, args));
    }

    public void set(Menu mnu, final String key, final Object... args) {
        mnu.textProperty().set(get(key, args));
    }

    public void set(MenuItem mnu, final String key, final Object... args) {
        mnu.textProperty().set(get(key, args));
    }

    HashMap<ToggleButton, ChangeListener<Boolean>> toggleButtonChangeListenerHashMap = new HashMap<>();

    public void bind(final ToggleButton btn, String key, String keySelected, Object... args) {
        setTranslationToolTip(this, btn, key);
        ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (btn.isSelected()) {
                    btn.textProperty().unbind();
                    btn.textProperty().bind(createStringBinding(keySelected, args));
                } else {
                    btn.textProperty().unbind();
                    btn.textProperty().bind(createStringBinding(key, args));
                }
            }
        };
        toggleButtonChangeListenerHashMap.put(btn, changeListener);
        btn.selectedProperty().addListener(changeListener);
        if (btn.isSelected()) {
            btn.textProperty().unbind();
            btn.textProperty().bind(createStringBinding(keySelected, args));
        } else {
            btn.textProperty().unbind();
            btn.textProperty().bind(createStringBinding(key, args));
        }
    }

    public void unbind(ToggleButton btn) {
        btn.textProperty().unbind();
        btn.selectedProperty().removeListener(toggleButtonChangeListenerHashMap.get(btn));
        toggleButtonChangeListenerHashMap.remove(btn);
    }

    public void bind(Labeled labeled, final String key, final Object... args) {
        labeled.textProperty().bind(createStringBinding(key, args));
        setTranslationToolTip(this, labeled, key);
    }

    public void unbind(Labeled labeled) {
        labeled.textProperty().unbind();
    }

    public void bind(StringProperty property, final String key, final Object... args) {
        property.bind(createStringBinding(key, args));
    }

    public void unbind(StringProperty property) {
        property.unbind();
    }

    public void bind(Tab tab, final String key, final Object... args) {
        tab.textProperty().bind(createStringBinding(key, args));
    }

    public void unbind(Tab tab) {
        tab.textProperty().unbind();
    }


    /**
     * show translation tool tip if mouse over the Node and 'Alt' is pressed!
     *
     * @param node
     * @param key
     */
    private static void setTranslationToolTip(FxTranslationBinding binding, Node node, String key) {

        if (binding == null || node == null | key == null || key.isEmpty()) return;

        Tooltip tooltip = new Tooltip(key + " = " + binding.get(key));

        tooltip.setStyle("-fx-font-size: 24px;");

        final AtomicBoolean mouseOver = new AtomicBoolean(false);
        final BooleanProperty showToolTip = new SimpleBooleanProperty(false);

        node.setOnMouseEntered(event -> {
            mouseOver.set(true);
            if (event.isAltDown()) {
                //log.debug("Mouse over Label");
                showToolTip.set(true);
            }
        });

        node.setOnKeyPressed(event -> {
            if (event.isAltDown()) {
                if (mouseOver.get()) {
                    // log.debug("Mouse over Label");
                    showToolTip.set(true);
                }
            }
        });

        node.setOnMouseExited(event -> {
            //log.debug("Mouse exited Label");
            mouseOver.set(false);
            showToolTip.set(false);
        });

        showToolTip.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    Window parent = node.getScene().getWindow();
                    tooltip.show(node, parent.getX() + node.localToScene(0.0, 0.0).getX() + node.getScene().getX(), parent.getY() + node.localToScene(0.0, 0.0).getY() + node.getScene().getY() + node.getBoundsInParent().getHeight());
                } else {
                    tooltip.hide();
                }
            }
        });
    }

}
