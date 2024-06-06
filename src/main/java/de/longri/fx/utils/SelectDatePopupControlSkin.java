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

import de.longri.UTILS;
import de.longri.fx.TRANSLATION;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class SelectDatePopupControlSkin implements Skin<SelectDatePopupControl> {

    final SelectDatePopupControl selectDatePopupControl;

    public SelectDatePopupControlSkin(SelectDatePopupControl selectDatePopupControl) {
        this.selectDatePopupControl = selectDatePopupControl;
    }

    /**
     * Gets the Skinnable to which this Skin is assigned. A Skin must be created
     * for one and only one Skinnable. This value will only ever go from a
     * non-null to null value when the Skin is removed from the Skinnable, and
     * only as a consequence of a call to {@link #dispose()}.
     * <p>
     * The caller who constructs a Skinnable must also construct a Skin and
     * properly establish the relationship between the Control and its Skin.
     *
     * @return A non-null Skinnable, or null value if disposed.
     */
    @Override
    public SelectDatePopupControl getSkinnable() {
        return this.selectDatePopupControl;
    }

    /**
     * Gets the Node which represents this Skin. This must never be null, except
     * after a call to {@link #dispose()}, and must never change except when
     * changing to null.
     *
     * @return A non-null Node, except when the Skin has been disposed.
     */
    @Override
    public Node getNode() {
        DatePicker datePickerFrom = new DatePicker(null);

        DatePickerSkin datePickerSkinFrom = new DatePickerSkin(datePickerFrom);
        Node popupContentFrom = datePickerSkinFrom.getPopupContent();

        DatePicker datePickerTo = new DatePicker(null);
        DatePickerSkin datePickerSkinTo = new DatePickerSkin(datePickerTo);
        Node popupContentTo = datePickerSkinTo.getPopupContent();

        Label fromLbl = new Label(TRANSLATION.getTranslation("fromDate"));
        Button fromDelButton = new Button(TRANSLATION.getTranslation("fromDateDelete"));


        Pane placeFrom = new Pane();
        HBox fromHbox = new HBox(fromLbl, placeFrom, fromDelButton);
        HBox.setHgrow(placeFrom, Priority.ALWAYS);
        fromHbox.getStyleClass().add("myPopup");

        VBox fromVbox = new VBox(fromHbox, popupContentFrom);
        fromVbox.setSpacing(10);


        Label toLbl = new Label(TRANSLATION.getTranslation("toDate"));
        Button toDelButton = new Button(TRANSLATION.getTranslation("toDateDelete"));
        Pane placeTo = new Pane();
        HBox toHbox = new HBox(toLbl, placeTo, toDelButton);
        HBox.setHgrow(placeTo, Priority.ALWAYS);
        toHbox.getStyleClass().add("myPopup");


        Pane placeSet = new Pane();
        HBox.setHgrow(placeSet, Priority.ALWAYS);
        Button apply = new Button(TRANSLATION.getTranslation("dialog.apply"));
        HBox hBoxSet = new HBox(placeSet, apply);

        VBox toVbox = new VBox(toHbox, popupContentTo, hBoxSet);
        toVbox.setSpacing(10);


        HBox hBox = new HBox(fromVbox, toVbox);
        hBox.getStyleClass().add("myPopupRound");

        fromDelButton.setOnAction(event -> {
            datePickerFrom.setValue(null);
        });
        toDelButton.setOnAction(event -> {
            datePickerTo.setValue(null);
        });

        apply.setOnAction(event -> {
            if (applyDate(datePickerFrom.getValue(), datePickerTo.getValue())) {
                this.selectDatePopupControl.hide();
            }
        });
        datePickerFrom.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyDate(datePickerFrom.getValue(), datePickerTo.getValue());
        });
        datePickerTo.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyDate(datePickerFrom.getValue(), datePickerTo.getValue());
        });

        return hBox;
    }


    private boolean applyDate(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            this.selectDatePopupControl.SEARCH_FIELD.setText("");
        } else if (from != null && to != null) {

            if (from.isEqual(to))
                this.selectDatePopupControl.SEARCH_FIELD.setText(UTILS.FormatDate(from));
            else {
                if (from.isAfter(to)) {
                    this.selectDatePopupControl.SEARCH_FIELD.setText("wrong");
                    return false;
                }
                this.selectDatePopupControl.SEARCH_FIELD.setText(UTILS.FormatDate(from) + "-" + UTILS.FormatDate(to));
            }
        } else if (from == null) {
            this.selectDatePopupControl.SEARCH_FIELD.setText("-" + UTILS.FormatDate(to));
        } else if (to == null) {
            this.selectDatePopupControl.SEARCH_FIELD.setText(UTILS.FormatDate(from)+"-");
        }
        return true;
    }


    /**
     * Called once when {@code Skin} is set.  This method is called after the previous skin,
     * if any, has been uninstalled via its {@link #dispose()} method.
     * The skin can now safely make changes to its associated control, like registering listeners,
     * adding child nodes, and modifying properties and event handlers.
     * <p>
     * Application code must not call this method.
     * <p>
     * The default implementation of this method does nothing.
     *
     * @implNote Skins only need to implement {@code install} if they need to make direct changes to the control
     * like overwriting properties or event handlers. Such skins should ensure these changes are undone in
     * their {@link #dispose()} method.
     * @since 20
     */
    @Override
    public void install() {
        Skin.super.install();
    }

    /**
     * Called when a previously installed skin is about to be removed from its associated control.
     * This allows the skin to do clean up, like removing listeners and bindings, and undo any changes
     * to the control's properties.
     * After this method completes, {@link #getSkinnable()} and {@link #getNode()} should return {@code null}.
     * <p>
     * Calling {@link #dispose()} more than once has no effect.
     */
    @Override
    public void dispose() {

    }
}
