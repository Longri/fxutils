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

import de.longri.fx.FX_Application;
import de.longri.fx.FxmlConditionScene;
import de.longri.fx.TRANSLATION;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class LogInConditionScene extends FxmlConditionScene {

    private final MyLogInView myLogInView;

    public LogInConditionScene(String name) throws IOException {
        super(new MyLogInView(), name, false);

        myLogInView = (MyLogInView) this.controller;
        myLogInView.listener = new MyLogInView.Listener() {
            @Override
            public void onCancelClicked() {
                LogInConditionScene.this.cancelClicked();
            }

            @Override
            public boolean logIn(String user, String password) {
                return LogInConditionScene.this.logIn(user, password);
            }

            @Override
            public Image getLogInImage() {
                return LogInConditionScene.this.getLogInImage();
            }

            @Override
            public Text getLogInText() {
                return LogInConditionScene.this.getLogInText();
            }

            @Override
            public String getLogInTitle() {
                return LogInConditionScene.this.getTitle();
            }
        };

    }

    @Override
    public void setIsShowing() {
        myLogInView.IS_SHOWING.set(true);
    }


    @Override
    public boolean isShowing() {
        return myLogInView.IS_SHOWING.get();
    }

    public abstract boolean isConditionMet() ;

    protected abstract boolean logIn(String user, String password);

    protected abstract void cancelClicked();

    protected abstract Image getLogInImage();

    protected abstract Text getLogInText();

    private static class MyLogInView extends LogInView {

        interface Listener {
            void onCancelClicked();

            boolean logIn(String user, String password);

            Image getLogInImage();

            Text getLogInText();

            String getLogInTitle();
        }

        private final AtomicBoolean IS_SHOWING = new AtomicBoolean(true);

        Listener listener;

        @Override
        protected void cancelClicked() {
            listener.onCancelClicked();
        }

        @Override
        protected boolean logIn(String user, String password) {
            return listener.logIn(user, password);
        }

        @Override
        protected String getCloseViewName() {
            return "";
        }

        @Override
        protected Image getLogInImage() {
            return listener.getLogInImage();
        }

        @Override
        protected Text getLogInText() {
            return listener.getLogInText();
        }

        @Override
        public String getTitle() {
            return listener.getLogInTitle();
        }


        @Override
        public void show(Object data, Stage stage) {
            IS_SHOWING.set(true);
            super.show(data, stage);
        }

        @FXML //override to close without showing CloseViewName.
        public void fx_onOkClicked() {
            if (this.logIn(fxUserTextField.getText(), fxPwField.getText())) {
                IS_SHOWING.set(false);
            } else {
                String wrongPwMsg = TRANSLATION.getTranslation("wrongPw");
                FX_Application.logAndShowErrorMsg(log, wrongPwMsg, null);
            }
        }
    }
}
