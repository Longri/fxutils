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

import de.longri.fx.utils.FontLongriIcon;
import de.longri.fx.utils.GlyphsFactory;
import de.longri.utils.CancelTimeOutJob;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Start implements I_ShowHeide {


    public Label fxLblTranslationTest;


    @FXML
    AnchorPane fxAnchorPanePassword;
    @FXML
    PasswordField fxPasswordFieldPass;
    @FXML
    TextField fxTextFieldPass;
    @FXML
    ToggleButton fxBtnShowPw;
    @FXML
    Button fxBtnProgressTest;

    Logger log = LoggerFactory.getLogger(Start.class);

    public TextFlow logTextFlow;
    private Stage stage;

    @Override
    public void show(Object data, Stage stage) {

        this.stage = stage;

        log.debug("onShow()");
        bindTranslation();
        //Creating text objects
        Text text1 = new Text("Welcome to Tutorialspoint ");

        //Setting font to the text
        text1.setFont(new Font(15));

        //Setting color to the text
        text1.setFill(Color.DARKSLATEBLUE);

        Text text2 = new Text("We provide free tutorials for readers in various technologies  ");

        //Setting font to the text
        text2.setFont(new Font(15));

        //Setting color to the text
        text2.setFill(Color.DARKGOLDENROD);
        Text text3 = new Text("\n Recently we started free video tutorials too ");

        //Setting font to the text
        text3.setFont(new Font(15));

        //Setting color to the text
        text3.setFill(Color.DARKGRAY);

        Text text4 = new Text("We believe in easy learning" + System.lineSeparator());

        //Setting font to the text
        text4.setFont(new Font(15));
        text4.setFill(Color.MEDIUMVIOLETRED);

        //Setting the line spacing between the text objects
        this.logTextFlow.setTextAlignment(TextAlignment.JUSTIFY);

        //Setting the width
        this.logTextFlow.setPrefSize(600, 300);

        //Setting the line spacing
        this.logTextFlow.setLineSpacing(5.0);

        //Retrieving the observable list of the TextFlow Pane
        ObservableList list = this.logTextFlow.getChildren();

        //Adding cylinder to the pane
        list.addAll(text1, text2, text3, text4);

        this.logTextFlow.setPrefWidth(300);
        this.logTextFlow.setPrefHeight(300);
        this.logTextFlow.layout();

        try {
            int i = 10 / 0;
        } catch (Exception e) {
            log.warn("by Zero", e);
        }


        log.warn("");


        Platform.runLater(() -> testDialogNullScene());
        initialTreeView();

        tryScale();
    }

    @FXML
    AnchorPane fxRoot;

    private void tryScale() {
//        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
//        double width = resolution.getWidth();
//        double height = resolution.getHeight();
//        double w = width/4000;  // your window width
//        double h = height/2000;  // your window height
//        Scale scale = new Scale(w, h, 0, 0);
//        fxRoot.getTransforms().add(scale);
//
//        stage.sizeToScene();
//
//        fxRoot.setPrefHeight(2000);
//        fxRoot.setPrefWidth(4000);

    }


    private void testDialogNullScene() {

        if (true) return;

        DialogResponse response = FxmlDialog.showAndWait("Header Text", "Content Text", null);
        if (response == DialogResponse.OK) {
            log.debug("Ok Press");
        } else {
            log.debug("Cancel Press");
        }

        DialogHtmlContent content = new DialogHtmlContent("TEST MSG");

        if (true) {
            String msgTxt = "<html dir=\"ltr\"><head></head><body contenteditable=\"true\"><p><span style=\"font-family: &quot;&quot;;\">" +
                    "TEST MSG".replace("\n", "</br>") +
                    "</span></p></body></html>";
            content.setHtmlContent(msgTxt);
            content.setIsHtmlContent(true);
        }


        content.setBlendMode(BlendMode.DARKEN);
        content.setPrefWidth(600);
        content.setPrefWidth(300);
        FxmlDialog.showAndWaitAlert(Alert.AlertType.ERROR, null, content, null);
    }


    @Override
    public void beforeHide() {
        log.debug("beforeHide()");
    }

    @Override
    public void hide() {
        log.debug("hide()");
        unBindTranslation();
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
    public void fxStyleChanged(FxStyles style) {

    }

    @Override
    public String getTitle() {
        return null;
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

        // bind textField and passwordField
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
                FX_Application.INSTANCE.runLater(() -> new ViewablePasswordHandler(fxAnchorPanePassword, fxTextFieldPass, fxPasswordFieldPass, fxBtnShowPw), true);
            }
        }).start();


    }


//###############  TreeView Test #########################################################

    @FXML
    TreeView<Folder> fxTreeView;

    private void initialTreeView() {

        TreeItem<Folder> rootItem = new TreeItem<>(new Folder("Root Folder"));
        addTreeChild(rootItem, new Folder("Child Folder"));


        this.fxTreeView.setRoot(rootItem);
        this.fxTreeView.setShowRoot(true);
    }

    static final GlyphsFactory wingDingsGf = new GlyphsFactory(FontLongriIcon.class);

    private void addTreeChild(TreeItem item, Folder folder) {

        HBox graphics = new HBox();

        Label nameText = new Label("LabelName: " + folder);
        graphics.getChildren().add(nameText);

        TreeItem childItem = new TreeItem(folder, graphics);
        wingDingsGf.setIcon(childItem, FontLongriIcon.FOLDER_OPEN, "1.5em", Color.RED);
//        wingDingsGf.setIcon(childItem, FontWingDingsIcon.FOLDER_OPEN, "10.5em", Color.RED);


        item.getChildren().add(childItem);

        for (Folder f : folder.childs) {
            addTreeChild(childItem, f);
        }
    }

    static class Folder {
        final String name;
        ArrayList<Folder> childs = new ArrayList<>();

        Folder(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    @FXML
    public void onFxProgressTestClicked(ActionEvent event) {
        CancelTimeOutJob job = new CancelTimeOutJob("WorkProgressTestJob") {
            @Override
            protected void work() throws Exception {
                Thread.sleep(5000);
                Platform.runLater(() -> {
                    this.updateMessage("Progress 50");
                    this.updateProgress(50, 100);
                });
                Thread.sleep(5000);

                Platform.runLater(() -> {
                    this.updateMessage("Progress 70");
                    this.updateProgress(70, 100);
                });
                Thread.sleep(5000);

                Platform.runLater(() -> {
                    this.updateMessage("Progress 100");
                    this.updateProgress(100, 100);
                });
                Thread.sleep(5000);
            }
        };

        FxmlDialog.showProgressDialog(job, FX_Application.INSTANCE.stage.getScene(), 400, 200, "TRANS CANCEL");
    }


    private void bindTranslation() {
        TRANSLATION.bind(fxLblTranslationTest, "test", "Variable");
        TRANSLATION.bind(fxBtnProgressTest, "wrongPw");
    }

    private void unBindTranslation() {
        TRANSLATION.unbind(fxLblTranslationTest);
        TRANSLATION.unbind(fxBtnProgressTest);
    }
}
