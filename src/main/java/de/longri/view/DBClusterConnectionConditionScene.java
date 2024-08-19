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
package de.longri.view;

import de.longri.fx.FxmlConditionScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DBClusterConnectionConditionScene extends FxmlConditionScene {

    protected final MyConnectView myConnectView;

    public DBClusterConnectionConditionScene(String name, DBClusterConnectionView.SettingReturnListener settingReturnListener) {
        super(new MyConnectView(), name, false);

        myConnectView = (MyConnectView) this.controller;
        myConnectView.listener = new MyConnectView.Listener() {
            @Override
            public void onCancelClicked() {
                DBClusterConnectionConditionScene.this.cancelClicked();
            }

//            @Override
//            public boolean onConnectTest(String user, String password, String host, String port, String databaseName) {
//                return true;
//            }

            @Override
            public Image getDBImage() {
                return DBClusterConnectionConditionScene.this.getDBImage();
            }

            @Override
            public double getDBImageWidth() {
                return DBClusterConnectionConditionScene.this.getDBImageWidth();
            }

            @Override
            public double getDBImageHeight() {
                return DBClusterConnectionConditionScene.this.getDBImageHeight();
            }

            @Override
            public String getMessageText() {
                return DBClusterConnectionConditionScene.this.getMessageText();
            }
        };
        myConnectView.settingReturnListener = settingReturnListener;
    }



    public abstract double getDBImageWidth();

    public abstract double getDBImageHeight();

    public abstract boolean isConditionMet();

    public abstract void cancelClicked();

    public abstract Image getDBImage();

    public abstract String getMessageText();


    @Override
    public boolean isShowing() {
        return myConnectView.IS_SHOWING.get();
    }

    @Override
    public void setIsShowing() {
        myConnectView.IS_SHOWING.set(true);
    }

    protected static class MyConnectView extends DBClusterConnectionView {
        interface Listener {
            void onCancelClicked();

//            boolean onConnectTest(String user, String password, String host, String port, String databaseName);

            Image getDBImage();

            double getDBImageWidth();

            double getDBImageHeight();

            String getMessageText();
        }

        private final AtomicBoolean IS_SHOWING = new AtomicBoolean(true);

        Listener listener;

        @Override
        public void show(Object data, Stage stage) {
            IS_SHOWING.set(true);
            super.show(data, stage);

            //replace image
            fxLogo.setImage(this.listener.getDBImage());
            fxLogo.setFitHeight(this.listener.getDBImageHeight());
            fxLogo.setFitWidth(this.listener.getDBImageWidth());
            fxLogo.setPreserveRatio(false);
            fxLblTitle.setText(this.listener.getMessageText());
        }

        @FXML //override to close without showing CloseViewName.
        public void fxConnect(ActionEvent actionEvent) {
            super.fxConnect(actionEvent);
            IS_SHOWING.set(false);
        }

        @FXML
        public void fxCancel(ActionEvent actionEvent) {
            actionEvent.consume();
            listener.onCancelClicked();
        }
    }
}
