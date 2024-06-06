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
package de.longri.fx.utils.view;

import de.longri.fx.FxStyles;
import de.longri.fx.SelfLoading_Controller;
import de.longri.fx.TRANSLATION;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ChoiceEnumType<E extends Enum<E>> extends SelfLoading_Controller {

    @FXML
    AnchorPane fxRoot;
    @FXML
    ChoiceBox fxChoice;
    @FXML
    Label fxLblChoiceText;
    @FXML
    Button fxBtnCancel;
    @FXML
    Button fxBtnOK;
    public String SceneTitel = null;

    public interface ReturnListener<E extends Enum<E>> {
        void returnValue(E value);
    }

    private Class<E> enumClass;
    private final ReturnListener returnListener;

    private String choiceTextTranslationKey = "choice_enum_view.choice_value";

    /**
     * Constructor
     *
     * @param listener       return listener
     * @param defaultValue   defaultValue
     * @param translationKey translation Key
     */
    public ChoiceEnumType(ReturnListener listener, Enum<E> defaultValue, String translationKey) {
        super("/de/longri/fx/utils/views/ChoiceEnumView.fxml");
        enumClass = defaultValue.getDeclaringClass();

        this.returnListener = listener;

        var en = defaultValue.getDeclaringClass();

        String[] typeNames = Arrays.stream(en.getEnumConstants()).map(Enum::name).toArray(String[]::new);
        for (String type : typeNames)
            fxChoice.getItems().add(type);

        fxChoice.setValue(defaultValue);

        if (translationKey != null) choiceTextTranslationKey = translationKey;
    }

    @Override
    public Node getRootNode() {
        return fxRoot;
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    @Override
    public void show(Object o, Stage stage) {
        bindTranslations();
    }

    @Override
    public void beforeHide() {
        unbindTranslations();
    }

    @Override
    public void hide() {

    }

    @Override
    public double getMinWidth() {
        return 0;
    }

    @Override
    public double getMinHeight() {
        return 0;
    }

    @Override
    public void fxStyleChanged(FxStyles fxStyles) {

    }

    @Override
    public String getTitle() {
        return SceneTitel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void onOkClicked(ActionEvent event) {
        event.consume();
        ((Stage) fxRoot.getScene().getWindow()).close();

        Object selected = fxChoice.getValue();

        if (selected instanceof String) {
            selected = Enum.valueOf(enumClass, (String) selected);
        }

        this.returnListener.returnValue((Enum<E>) selected);
    }

    @FXML
    public void OnCancelClicked(ActionEvent event) {
        event.consume();
        ((Stage) fxRoot.getScene().getWindow()).close();
        this.returnListener.returnValue(null);
    }

    private void bindTranslations() {
        TRANSLATION.loadBundle("de/longri/fx/utils/views/choice_enum_view");
        TRANSLATION.bind(fxBtnOK, "choice_enum_view.ok");
        TRANSLATION.bind(fxBtnCancel, "choice_enum_view.cancel");
        TRANSLATION.bind(fxLblChoiceText, choiceTextTranslationKey);
    }

    private void unbindTranslations() {
        TRANSLATION.unbind(fxBtnOK);
        TRANSLATION.unbind(fxBtnCancel);
        TRANSLATION.unbind(fxLblChoiceText);
    }
}
