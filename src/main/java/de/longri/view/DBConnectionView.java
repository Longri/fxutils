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

import de.longri.UTILS;
import de.longri.fx.*;
import de.longri.fx.svg.BufferedImageTranscoder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;


public class DBConnectionView extends SelfLoading_Controller {

    private final static Logger log = LoggerFactory.getLogger(DBConnectionView.class);

    @FXML
    AnchorPane fxRoot;

    public ImageView fxLogo;
    public TextField fxServerName;
    public TextField fxPort;
    public TextField fxDatabaseName;
    public TextField fxUser;
    public TextField fxPassword;
    public Button fxBtnCancel;
    public Button fxBtnCheckConnection;
    public Button fxBtnComplete;
    public Label fxLblTitle;
    public Label fxLblPassword;
    public Label fxLblUserName;
    public Label fxLblAuth;
    public Label fxLblDatabase;
    public Label fxLblPort;
    public Label fxLblServer;
    public Label fxLblServerHost;
    protected SettingReturnListener settingReturnListener;
    protected Stage stage;

    public DBConnectionView() {
        super("/views/DBConnectionView.fxml");
    }


    @Override
    public Node getRootNode() {
        return fxRoot;
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    public interface SettingReturnListener {
        boolean Return(StringBuilder messageBuilder, String user, String password, String host, String port, String databaseName);

        boolean test(StringBuilder messageBuilder, String user, String password, String host, String port, String databaseName);
    }

    @FXML
    public void fxConnect(ActionEvent actionEvent) {
        actionEvent.consume();
        this.settingReturnListener.Return(null, fxUser.getText(), fxPassword.getText(), fxServerName.getText(), fxPort.getText(), fxDatabaseName.getText());
    }

    @FXML
    public void fxConnectionTest(ActionEvent actionEvent) {
        actionEvent.consume();

        StringBuilder messageBuilder = new StringBuilder();

        if (!canConnect(messageBuilder, fxServerName.getText(), fxPort.getText(), fxDatabaseName.getText(), fxUser.getText(), fxPassword.getText())) {
            String color = FX_Application.INSTANCE.style == FxStyles.DARK ? "#ffffff" : "black";
            String bgcolor = FX_Application.INSTANCE.style == FxStyles.DARK ? "#3B3F41" : "#ffffff";

            String html = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "   <body style=\"background-color:" + bgcolor + ";text-align:center\">\n" +
                    "      <p style=\"color:" + color + "\">" + messageBuilder + "</p>\n" +
                    "   </body>\n" +
                    "</html>";


            DialogHtmlContent dialogHtmlContent = new DialogHtmlContent(html);
            dialogHtmlContent.setIsHtmlContent(true);
            if (FX_Application.INSTANCE.style == FxStyles.LIGHT) dialogHtmlContent.setBlendMode(BlendMode.DARKEN);
            else dialogHtmlContent.setBlendMode(BlendMode.LIGHTEN);
            dialogHtmlContent.setPrefWidth(500);

            FxmlDialog.showAndWaitAlert(Alert.AlertType.ERROR, "", dialogHtmlContent, this.stage.getScene());
            return;
        }


        String color = FX_Application.INSTANCE.style == FxStyles.DARK ? "#ffffff" : "black";
        String bgcolor = FX_Application.INSTANCE.style == FxStyles.DARK ? "#3B3F41" : "#ffffff";

        String translatedMsg = TRANSLATION.getTranslation("connection_view.msg.connection");

        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "   <body style=\"background-color:" + bgcolor + ";text-align:center\">\n" +
                "      <p style=\"color:" + color + "\">  " + translatedMsg + "  </p>\n" +
                "   </body>\n" +
                "</html>";


        DialogHtmlContent dialogHtmlContent = new DialogHtmlContent(html);
        dialogHtmlContent.setIsHtmlContent(true);
        if (FX_Application.INSTANCE.style == FxStyles.LIGHT) dialogHtmlContent.setBlendMode(BlendMode.DARKEN);
        else dialogHtmlContent.setBlendMode(BlendMode.LIGHTEN);
        dialogHtmlContent.setPrefWidth(500);

        FxmlDialog.showAndWaitAlert(Alert.AlertType.INFORMATION, "", dialogHtmlContent, this.stage.getScene());

    }

    public boolean canConnect(StringBuilder messageBuilder, String host, String port, String databaseName, String user, String password) {
        return settingReturnListener.test(messageBuilder, user, password, host, port, databaseName);
    }

    @FXML
    public void fxCancel(ActionEvent actionEvent) {
        actionEvent.consume();
        this.settingReturnListener.Return(null, null, null, null, null, null);
    }

    @Override
    public void show(Object data, Stage stage) {
        this.stage = stage;

    }

    @Override
    public void beforeHide() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void fxStyleChanged(FxStyles style) {
        log.debug("reload svg images for Button");
        replaceBtnImageWithSvgVersion();
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> replaceBtnImageWithSvgVersion());
    }

    protected void replaceBtnImageWithSvgVersion() {
        String ext = FX_Application.INSTANCE.style == FxStyles.DARK ? "-dark.svg" : ".svg";
        BufferedImageTranscoder.replaceImageViewWithSvg(fxLogo, UTILS.files.classpath("icons/mariaDB" + ext).read(), 128, 70, false);
    }

    public void setTranslations() {
        TRANSLATION.set(fxBtnCancel, "main.btn.fxBtn_Cancel");
        TRANSLATION.set(fxBtnCheckConnection, "connection_view.btn.fxBtnCheckConnection");
        if (fxBtnComplete != null) TRANSLATION.set(fxBtnComplete, "connection_view.btn.fxBtnComplete");
        if (fxLblTitle != null) TRANSLATION.set(fxLblTitle, "connection_view.lbl.fxLblTitle");
        if (fxLblPassword != null) TRANSLATION.set(fxLblPassword, "connection_view.lbl.fxLblPassword");
        if (fxLblUserName != null) TRANSLATION.set(fxLblUserName, "connection_view.lbl.fxLblUserName");
        if (fxLblAuth != null) TRANSLATION.set(fxLblAuth, "connection_view.lbl.fxLblAuth");
        if (fxLblDatabase != null) TRANSLATION.set(fxLblDatabase, "connection_view.lbl.fxLblDatabase");
        if (fxLblPort != null) TRANSLATION.set(fxLblPort, "connection_view.lbl.fxLblPort");
        if (fxLblServer != null) TRANSLATION.set(fxLblServer, "connection_view.lbl.fxLblServer");
        if (fxLblServerHost != null) TRANSLATION.set(fxLblServerHost, "connection_view.lbl.fxLblServerHost");
    }

}
