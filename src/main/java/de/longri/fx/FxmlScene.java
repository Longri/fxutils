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

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FxmlScene extends Scene implements I_ShowHeide {

    Logger log = LoggerFactory.getLogger(FxmlScene.class);

    public final I_ShowHeide controller;
    public final String NAME;
    public final boolean RESIZABLE;
    public String SceneTitel = null;
    private boolean firstShow = true;

    public FxmlScene(FXMLLoader fxmlLoader, String name, boolean resizeable) throws IOException {
        super(fxmlLoader.load(), -1.0D, -1.0D, false, SceneAntialiasing.BALANCED);

        Object controllerObject = fxmlLoader.getController();
        if (!(controllerObject instanceof I_ShowHeide)) {
            String msg = controllerObject == null ? "Controller object is NULL" : "Controller Class: " + controllerObject.getClass().getName() + " doesn't implement I_ShowHide";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        controller = (I_ShowHeide) controllerObject;
        NAME = name;
        RESIZABLE = resizeable;
    }

    public FxmlScene(SelfLoading_Controller selfLoadingController, String name, boolean resizeable) {
        super((Parent) selfLoadingController.getRootNode(), -1.0D, -1.0D, false, SceneAntialiasing.BALANCED);
        this.controller = selfLoadingController;
        NAME = name;
        RESIZABLE = resizeable;
    }

    @Override
    public void show(Object data, Stage stage) {
        if(controller instanceof SelfLoading_Controller selfLoadingController) {
            SceneTitel = selfLoadingController.getTitle();
        }
        if (firstShow) {controller.firstShow(); firstShow = false;}
        controller.show(data, stage);
    }

    @Override
    public void beforeHide() {
        controller.beforeHide();
    }

    @Override
    public void hide() {
        controller.hide();
    }

    @Override
    public double getMinWidth() {
        return controller.getMinWidth();
    }

    @Override
    public double getMinHeight() {
        return controller.getMinHeight();
    }

    @Override
    public double getMaxWidth() {
        return controller.getMaxWidth();
    }

    @Override
    public double getMaxHeight() {
        return controller.getMaxHeight();
    }

    public void fxStyleChanged(FxStyles style) {
        controller.fxStyleChanged(style);
    }

    @Override
    public String getTitle() {
        return SceneTitel;
    }

    public String getName() {
        return NAME;
    }

    public boolean isResizeable() {
        return RESIZABLE;
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FxmlScene: ");
        sb.append(this.NAME).append("  | ");

        if (this.RESIZABLE) {
            sb.append("Resizable ");
        } else {
            sb.append("Fix size ");
        }
        sb.append(String.format("width:%s | height:%s", getWidth(), getHeight()));
        return sb.toString();
    }

}
