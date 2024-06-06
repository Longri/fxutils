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

import de.longri.fx.TRANSLATION;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SelectCheckPopupControlSkin implements Skin<SelectCheckPopupControl> {

    public static final String CHECKBOX_WITH_CHECK = "☑";
    public static final String CHECKBOX_WITHOUT_CHECK = "☐";

    final SelectCheckPopupControl control;

    SelectCheckPopupControlSkin(SelectCheckPopupControl control) {
        this.control = control;
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
    public SelectCheckPopupControl getSkinnable() {
        return this.control;
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
        RadioButton radioWithout = new RadioButton();
        RadioButton radioButtonCheck = new RadioButton();
        RadioButton radioNoFilter = new RadioButton();


        Label lblWithout = new Label(CHECKBOX_WITHOUT_CHECK);
        lblWithout.setStyle("-fx-font-size: 25px;");
        Label lblWithCheck = new Label(CHECKBOX_WITH_CHECK);
        lblWithCheck.setStyle("-fx-font-size: 25px;");

        Label lblNoFilter = new Label(TRANSLATION.getTranslation("nofilter"));

        VBox vBox1 = new VBox(lblWithout, radioWithout);
        VBox vBox2 = new VBox(lblWithCheck, radioButtonCheck);
        VBox vBox3 = new VBox(lblNoFilter, radioNoFilter);

        ToggleGroup toggleGroup = new ToggleGroup();
        radioWithout.setToggleGroup(toggleGroup);
        radioButtonCheck.setToggleGroup(toggleGroup);
        radioNoFilter.setToggleGroup(toggleGroup);

        vBox1.setSpacing(10);
        vBox2.setSpacing(10);
        vBox3.setSpacing(18);

        vBox1.setAlignment(Pos.CENTER);
        vBox2.setAlignment(Pos.CENTER);
        vBox3.setAlignment(Pos.CENTER);

        HBox hBox = new HBox(vBox1, vBox2, vBox3);
        hBox.getStyleClass().add("myPopupRound");
        hBox.setSpacing(20);

        EventHandler mouseListener = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Object source = event.getSource();
                if (source == lblWithout) {
                    radioWithout.setSelected(true);
                } else if (source == lblWithCheck) {
                    radioButtonCheck.setSelected(true);
                } else {
                    radioNoFilter.setSelected(true);
                }
            }
        };

        lblWithout.setOnMouseClicked(mouseListener);
        lblWithCheck.setOnMouseClicked(mouseListener);
        lblNoFilter.setOnMouseClicked(mouseListener);

        //pre select
        String txt = this.control.SEARCH_FIELD.getText();
        if (txt.equals(CHECKBOX_WITHOUT_CHECK))
            radioWithout.setSelected(true);
        else if (txt.equals(CHECKBOX_WITH_CHECK))
            radioButtonCheck.setSelected(true);
        else
            radioNoFilter.setSelected(true);

        //Selection changed
        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == radioWithout) SelectCheckPopupControlSkin.this.control.SEARCH_FIELD.setText(CHECKBOX_WITHOUT_CHECK);
                else if (newValue == radioButtonCheck) SelectCheckPopupControlSkin.this.control.SEARCH_FIELD.setText(CHECKBOX_WITH_CHECK);
                else SelectCheckPopupControlSkin.this.control.SEARCH_FIELD.setText("");
            }
        });

        return hBox;
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
