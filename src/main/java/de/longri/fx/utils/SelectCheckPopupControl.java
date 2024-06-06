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

public class SelectCheckPopupControl extends PopupControl {

    final static Logger log = LoggerFactory.getLogger(SelectCheckPopupControl.class);

    final TextField SEARCH_FIELD;

    public SelectCheckPopupControl(TextField searchField) {
        SEARCH_FIELD = searchField;
        SEARCH_FIELD.setText("");
        initial();
    }

    void initial() {
        this.SEARCH_FIELD.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newFocused) {
                if (!newFocused) {
                    log.debug("hide popup from node: {}", SEARCH_FIELD.getId());
                    SelectCheckPopupControl.this.hide();
                } else {
                    show(SelectCheckPopupControl.this.SEARCH_FIELD);
                }
            }
        });
        this.SEARCH_FIELD.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!SelectCheckPopupControl.this.isShowing()) {
                    show(SelectCheckPopupControl.this.SEARCH_FIELD);
                }
            }
        });

        this.SEARCH_FIELD.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (!isShowing()) {
                show(SelectCheckPopupControl.this.SEARCH_FIELD);
            }
            SEARCH_FIELD.requestFocus();
            SEARCH_FIELD.positionCaret(SEARCH_FIELD.getText().length());

        });
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
        return new SelectCheckPopupControlSkin(this);
    }

    final AtomicBoolean DONT_SHOW_POPUP = new AtomicBoolean(false);

    public void setText(String text) {
        DONT_SHOW_POPUP.set(true);
        SEARCH_FIELD.setText(text);
        new SleepCall(100, () -> {
            DONT_SHOW_POPUP.set(false);
        });
    }
}
