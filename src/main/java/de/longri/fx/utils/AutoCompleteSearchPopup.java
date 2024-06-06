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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class AutoCompleteSearchPopup extends PopupControl {

    private static final Logger log = LoggerFactory.getLogger(AutoCompleteSearchPopup.class);
    public static final String DEFAULT_STYLE_CLASS = "auto-complete-popup";
    final IntegerProperty visibleRowCount = new SimpleIntegerProperty(this, "visibleRowCount", 3);
    final TextField SEARCH_FIELD;
    final AbstractFilteredList<String> SEARCH_LIST;
    final Filter<String> FILTER = new Filter<>() {
        public boolean predictTest(String test) {
            return test.toLowerCase().contains(SEARCH_FIELD.getText().toLowerCase());
        }
    };

    public AutoCompleteSearchPopup(TextField searchField, AbstractFilteredList<String> searchList) {
        if (searchList == null) throw new NullPointerException("Searchlist can't be NULL");
        if (searchField == null) throw new NullPointerException("Search field can't be NULL");

        this.setAutoFix(true);
        this.setAutoHide(true);
        this.setHideOnEscape(true);
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        this.SEARCH_FIELD = searchField;
        this.SEARCH_LIST = searchList;
        initial();
    }

    void initial() {
        this.SEARCH_FIELD.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newFocused) {
                if (!newFocused) {
                    log.debug("hide popup from node: {}", SEARCH_FIELD.getId());
                    AutoCompleteSearchPopup.this.hide();
                } else {
                    show(AutoCompleteSearchPopup.this.SEARCH_FIELD);
                }
            }
        });
        this.SEARCH_FIELD.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!AutoCompleteSearchPopup.this.isShowing()) {
                    show(AutoCompleteSearchPopup.this.SEARCH_FIELD);
                }
                FILTER.fireFilterChanged();
            }
        });

        this.SEARCH_FIELD.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (!isShowing()) {
                show(AutoCompleteSearchPopup.this.SEARCH_FIELD);
            }
            SEARCH_FIELD.requestFocus();
            SEARCH_FIELD.positionCaret(SEARCH_FIELD.getText().length());

        });

        SEARCH_LIST.setFilter(FILTER);
    }

    @Override
    public void hide() {
        super.hide();
    }


    public void show(Node node) {
        if (DONT_SHOW_POPUP.get()) return;
        if (node.getScene() != null && node.getScene().getWindow() != null) {
            if (!this.isShowing()) {
                Window parent = node.getScene().getWindow();
                this.getScene().setNodeOrientation(node.getEffectiveNodeOrientation());
                if (node.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
                    this.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_RIGHT);
                } else {
                    this.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
                }
                log.debug("Show popup on node: {}", node.getId());
                this.show(parent, parent.getX() + node.localToScene(0.0, 0.0).getX() + node.getScene().getX(), parent.getY() + node.localToScene(0.0, 0.0).getY() + node.getScene().getY() + node.getBoundsInParent().getHeight());
            }
        } else {
            throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window.");
        }
    }

    protected Skin<?> createDefaultSkin() {
        return new AutoCompleteSearchPopupSkin(this);
    }

    public final AutoCompleteSearchPopup setVisibleRowCount(int value) {
        this.visibleRowCount.set(value);
        return this;
    }

    public final int getVisibleRowCount() {
        return this.visibleRowCount.get();
    }

    public final IntegerProperty visibleRowCountProperty() {
        return this.visibleRowCount;
    }

    Array<SuggestionChosenListener> suggestionChosenListeners = new Array<>();

    public void addSuggestionChosenListener(SuggestionChosenListener listener) {
        suggestionChosenListeners.add(listener);
    }

    private AtomicBoolean DONT_SHOW_POPUP = new AtomicBoolean(false);

    public void setText(String text) {
        DONT_SHOW_POPUP.set(true);
        SEARCH_FIELD.setText(text);
        new SleepCall(100, () -> {
            DONT_SHOW_POPUP.set(false);
        });
    }

    public interface SuggestionChosenListener {
        void suggestionChosen(String chosen);

        void suggestionRightClick(String chosen);
    }
}
