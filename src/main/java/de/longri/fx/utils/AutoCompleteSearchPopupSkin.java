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

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseButton;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoCompleteSearchPopupSkin implements Skin<AutoCompleteSearchPopup> {
    private static final Logger log = LoggerFactory.getLogger(AutoCompleteSearchPopupSkin.class);
    private final AutoCompleteSearchPopup control;
    private final ListView<String> suggestionList;
    final int LIST_CELL_HEIGHT;


    static final String CSS = AutoCompleteSearchPopupSkin.class.getResource("/skin/autocompletion.css").toExternalForm();

    public AutoCompleteSearchPopupSkin(AutoCompleteSearchPopup control) {
        this.LIST_CELL_HEIGHT = 24;
        this.control = control;
        this.suggestionList = new ListView(control.SEARCH_LIST);
        this.suggestionList.getStyleClass().add("auto-complete-popup");
        this.suggestionList.getStylesheets().add(CSS);
        this.suggestionList.minHeightProperty().setValue(150);
        this.suggestionList.prefHeightProperty().bind(Bindings.min(control.visibleRowCountProperty(), Bindings.size(this.suggestionList.getItems())).multiply(24).add(18));
        this.suggestionList.prefWidthProperty().bind(control.prefWidthProperty());
        this.suggestionList.maxWidthProperty().bind(control.maxWidthProperty());
        this.suggestionList.minWidthProperty().bind(control.minWidthProperty());

        this.registerEventListener();
    }

    private void registerEventListener() {
        this.suggestionList.setOnMouseClicked((me) -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                this.onSuggestionChoosen(this.suggestionList.getSelectionModel().getSelectedItem());
            } else if (me.getButton() == MouseButton.SECONDARY) {
                this.onSuggestionRightClick(this.suggestionList.getSelectionModel().getSelectedItem());
            }
        });
        this.suggestionList.setOnKeyPressed((ke) -> {
            switch (ke.getCode()) {
                case TAB:
                case ENTER:
                    this.onSuggestionChoosen(this.suggestionList.getSelectionModel().getSelectedItem());
                    break;
                case ESCAPE:
                    if (this.control.isHideOnEscape()) {
                        this.control.hide();
                    }
            }

        });
    }

    private void onSuggestionChoosen(String suggestion) {
        if (suggestion != null) {
            log.debug("onSuggestionChoosen: {}", suggestion);
            this.control.SEARCH_FIELD.setText(suggestion);

            new SleepCall(50, control::hide, true);

            if (!control.suggestionChosenListeners.isEmpty()) {
                for (AutoCompleteSearchPopup.SuggestionChosenListener listener : control.suggestionChosenListeners) {
                    new SleepCall(10, () -> listener.suggestionChosen(suggestion), true);
                }
            }
        }
    }

    private void onSuggestionRightClick(String suggestion) {
        if (suggestion != null) {
            log.debug("onSuggestionRightClick: {}", suggestion);
            if (!control.suggestionChosenListeners.isEmpty()) {
                for (AutoCompleteSearchPopup.SuggestionChosenListener listener : control.suggestionChosenListeners) {
                    new SleepCall(10, () -> listener.suggestionRightClick(suggestion), true);
                }
            }
        }
    }

    public Node getNode() {
        return this.suggestionList;
    }

    public AutoCompleteSearchPopup getSkinnable() {
        return this.control;
    }

    public void dispose() {
    }
}