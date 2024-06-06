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

import javafx.geometry.Insets;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewablePasswordHandler {
    private static final Logger log = LoggerFactory.getLogger(ViewablePasswordHandler.class);

    private final TextField textField;
    private final PasswordField passwordField;
    private final ImageView imageView = new ImageView();

    private Image noView;
    private Image noView_dark;
    private Image view;
    private Image view_dark;

    public ViewablePasswordHandler(AnchorPane anchorPane, TextField textField, PasswordField passwordField, ToggleButton toggleButton) {

        try {
            noView = new Image(getClass().getResourceAsStream("/icons/no_view.png"), 32, 32, true, true);
            noView_dark = new Image(getClass().getResourceAsStream("/icons/no_view_dark.png"), 32, 32, true, true);
            view = new Image(getClass().getResourceAsStream("/icons/view.png"), 32, 32, true, true);
            view_dark = new Image(getClass().getResourceAsStream("/icons/view_dark.png"), 32, 32, true, true);
        } catch (Exception e) {
            log.error("Error with load image", e);
        }


        imageView.setFitHeight(18);
        imageView.setPreserveRatio(true);
        toggleButton.setGraphic(imageView);
        toggleButton.setText(null);
        toggleButton.setPadding(new Insets(-6, 1, -6, 1));

        toggleButton.setPrefHeight(9);

        toggleButton.selectedProperty().addListener((observableValue, aBoolean, t1) -> setButtonImage(aBoolean));
        this.textField = textField;
        this.passwordField = passwordField;
        textField.textProperty().bindBidirectional(passwordField.textProperty());

        toggleButton.getStyleClass().clear();
        toggleButton.getStyleClass().add("pw-toggle-button");

        FX_Application.INSTANCE.runLater(() -> setButtonImage(true), true);
        FX_Application.INSTANCE.runLater(() -> toggleButton.selectedProperty().set(false), true);

    }

    private void setButtonImage(boolean pwHidden) {
        if (pwHidden) {
            this.textField.setVisible(false);
            this.passwordField.setVisible(true);
            if (view_dark != null && view != null) imageView.setImage(FX_Application.INSTANCE.style == FxStyles.DARK ? view_dark : view);
        } else {
            this.textField.setVisible(true);
            this.passwordField.setVisible(false);
            if (noView_dark != null && noView != null) imageView.setImage(FX_Application.INSTANCE.style == FxStyles.DARK ? noView_dark : noView);
        }
    }

}
