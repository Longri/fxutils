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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;


public class LanguageListCell extends ListCell<Language> {
    private final HBox graphic;
    private final ImageView image;
    private final Label text;

    public LanguageListCell() {
        this(false);
    }

    public LanguageListCell(boolean withText) {
        super();
        this.graphic = new HBox(5.0);
//        this.graphic = new HBox(0.0);
        this.image = new ImageView();
        this.text = new Label();

        this.graphic.setAlignment(Pos.CENTER_LEFT);
        if (withText) this.graphic.getChildren().setAll(this.image, this.text);
        if (!withText) this.graphic.getChildren().setAll(this.image);
    }

    @Override
    protected void updateItem(Language language, boolean empty) {
        super.updateItem(language, empty);
        if (!empty && language != null) {
            this.image.imageProperty().bind(language.imageProperty());
            this.text.textProperty().bind(language.nameProperty());

            setGraphic(this.graphic);
        } else {
            this.image.imageProperty().unbind();
            this.text.textProperty().unbind();

            setGraphic(null);
        }
    }
}
