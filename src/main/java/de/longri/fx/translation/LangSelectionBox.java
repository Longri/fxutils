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

import de.longri.fx.TRANSLATION;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Locale;

public class LangSelectionBox extends ComboBox<Language> {

    public LangSelectionBox() {
        this(false);
    }

    public LangSelectionBox(boolean withText) {
        this(null, withText);
    }

    public LangSelectionBox(ComboBox replacedComboBox) {
        this(replacedComboBox, false);
    }

    public LangSelectionBox(ComboBox replacedComboBox, boolean withText) {

        this.getSelectionModel().selectedItemProperty().addListener(changeListener);
        this.valueProperty().addListener(changeListener);

        this.setCellFactory((param) -> new LanguageListCell());
        this.setButtonCell(new LanguageListCell(true));
        ArrayList<Language> list = TRANSLATION.getLanguages();
        this.getItems().setAll(list);

        TRANSLATION.addChangeListener((observable, oldValue, newValue) -> {
            for (Language la : list) {
                if (la.getLocale().equals(newValue)) {
                    LangSelectionBox.this.setValue(la);
                }
            }
        });

        Locale translationSetedLocale = TRANSLATION.getLocale();
        for (Language lang : list) {
            if (lang.getLocale().equals(translationSetedLocale)) {
                this.setValue(lang);
                break;
            }
        }

        this.setPrefWidth(20);

        if (replacedComboBox == null) return;

        Node parent = replacedComboBox.getParent();
        if (parent instanceof AnchorPane) {
            AnchorPane anchorPane = (AnchorPane) parent;
            anchorPane.getChildren().remove(replacedComboBox);
            anchorPane.getChildren().add(this);
            AnchorPane.setLeftAnchor(this, AnchorPane.getLeftAnchor(replacedComboBox));
            AnchorPane.setRightAnchor(this, AnchorPane.getRightAnchor(replacedComboBox));
            AnchorPane.setTopAnchor(this, AnchorPane.getTopAnchor(replacedComboBox));
            AnchorPane.setBottomAnchor(this, AnchorPane.getBottomAnchor(replacedComboBox));
        }


    }

    ChangeListener changeListener = (ChangeListener<Language>) (options, oldValue, newValue) -> TRANSLATION.setLocale(newValue.getLocale());

}
