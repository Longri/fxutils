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

import de.longri.utils.CancelTimeOutJob;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FxmlDialog {

    private final static AtomicInteger dialogCount = new AtomicInteger();
    private final static HashMap<Integer, DialogResponse> responseHashMap = new HashMap<>();

    public static FxmlDialog getTimedDialog(URL contentFxmlUrl, Duration closeDuration, DialogResponse... responseTypes) {
        FxmlDialog dialog = getDialog(contentFxmlUrl, responseTypes);
        if (dialog != null) dialog.setTimed(closeDuration);
        return dialog;
    }

    public static FxmlDialog getDialog(URL contentFxmlUrl, DialogResponse... responseTypes) {
        try {
            FXMLLoader loader = new FXMLLoader(FxmlDialog.class.getResource("/de/longri/de.longri.fx/FxmlDialog.fxml"));
            Parent parent = loader.load();
            FxmlDialog dialog = loader.getController();
            int dialogNumber = dialogCount.incrementAndGet();

            FXMLLoader contentLoader = new FXMLLoader(contentFxmlUrl);
            Node contentNode = contentLoader.load();
            Object contentController = contentLoader.getController();

            dialog.fxmlContentPane.getChildren().add(contentNode);
            dialog.setContentController(contentController);
            dialog.setParent(parent, contentController, dialogNumber);
            dialog.enableButtons(responseTypes);
            return dialog;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FxmlDialog getDialog(Node contentNode, DialogResponse... responseTypes) {
        try {
            FXMLLoader loader = new FXMLLoader(FxmlDialog.class.getResource("/views/FxmlDialog.fxml"));
            Parent parent = loader.load();
            FxmlDialog dialog = loader.getController();
            int dialogNumber = dialogCount.incrementAndGet();

            dialog.fxmlContentPane.getChildren().add(contentNode);

            dialog.setContentController(null);
            dialog.setParent(parent, null, dialogNumber);
            dialog.enableButtons(responseTypes);
            return dialog;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DialogResponse showAndWaitTimedAlert(Duration duration, Alert.AlertType alertType,
                                                       String headerText, String contentText, Scene parentScene) {
        DialogHtmlContent dialogHtmlContent = new DialogHtmlContent(contentText);
        dialogHtmlContent.setIsHtmlContent(false);
        return showAndWaitTimedAlert(duration, alertType, headerText, dialogHtmlContent, parentScene);
    }

    public static DialogResponse showAndWaitTimedAlert(Duration duration, Alert.AlertType alertType, String headerText,
                                                       DialogHtmlContent dialogHtmlContent, Scene parentScene) {
        Node node = getAlertDialogNode(alertType, headerText, dialogHtmlContent);
        FxmlDialog dialog = getDialog(node, DialogResponse.OK,
                alertType == Alert.AlertType.CONFIRMATION ? DialogResponse.Cancel : null);
        if (dialog != null) {
            dialog.setTimed(duration);
            return dialog.show(parentScene);
        }
        return DialogResponse.unknown;
    }

    public static DialogResponse showAndWaitAlert(Alert.AlertType alertType,
                                                  String headerText, String contentText, Scene parentScene) {
        DialogHtmlContent dialogHtmlContent = new DialogHtmlContent(contentText);
        dialogHtmlContent.setIsHtmlContent(false);
        return showAndWaitAlert(alertType, headerText, dialogHtmlContent, parentScene);
    }


    public static DialogResponse showAndWaitAlert(Alert.AlertType alertType, String headerText,
                                                  DialogHtmlContent dialogHtmlContent, Scene parentScene) {
        Node node = getAlertDialogNode(alertType, headerText, dialogHtmlContent);
        FxmlDialog dialog = getDialog(node, DialogResponse.OK,
                alertType == Alert.AlertType.CONFIRMATION ? DialogResponse.Cancel : null);
        if (dialog != null) {
            return dialog.show(parentScene);
        }
        return DialogResponse.unknown;
    }

    public static DialogResponse showAndWaitRemember(String headerText, String contentText, String rememberText, Scene scene) {
        DialogHtmlContent htmlContent = new DialogHtmlContent(contentText);
        htmlContent.setIsHtmlContent(false);
        return showAndWaitRemember(headerText, htmlContent, rememberText, scene);
    }

    public static DialogResponse showAndWaitRemember(String headerText, String contentText, Scene scene) {
        DialogHtmlContent htmlContent = new DialogHtmlContent(contentText);
        htmlContent.setIsHtmlContent(false);
        return showAndWaitRemember(headerText, htmlContent, scene);
    }

    public static DialogResponse showAndWaitRemember(String headerText, DialogHtmlContent htmlContent, Scene scene) {
        return showAndWaitRemember(headerText, htmlContent, "remember", scene);
    }

    public static DialogResponse showAndWaitRemember(String headerText, DialogHtmlContent htmlContent, String rememberText, Scene scene) {
        Node node = getAlertDialogNode(Alert.AlertType.CONFIRMATION, headerText, htmlContent);
        FxmlDialog dialog = getDialog(node, DialogResponse.OK, DialogResponse.Cancel);
        if (dialog != null) {
            dialog.setRemember(true);
            dialog.setRememberText(rememberText);
            return dialog.show(scene);
        }
        return DialogResponse.unknown;
    }

    public static VBox getAlertDialogNode(Alert.AlertType alertType, String headerText, DialogHtmlContent dialogHtmlContent) {
        Alert alert = new Alert(alertType);
        if (headerText != null) alert.setHeaderText(headerText);// if null use default
        alert.setContentText(dialogHtmlContent.getHtmlContent());
        if (alertType != Alert.AlertType.NONE) alert.show();
        VBox vBox = null;
        GridPane gridPane = null;
        Label label = null;
        try {
            vBox = new VBox();
            vBox.setSpacing(5);
            vBox.setPadding(new Insets(10, 10, 0, 10));
            gridPane = null;
            label = null;
            Parent node = alert.getGraphic().getParent().getParent().getParent();

            for (Node child : node.getChildrenUnmodifiable()) {
                if (child instanceof GridPane) {
                    gridPane = (GridPane) child;
                } else if (child instanceof Label) {
                    label = (Label) child;
                }
            }
        } catch (Exception e) {

        }

        if (dialogHtmlContent.isHtmlContent()) {
            WebView webView = new WebView();
            webView.getEngine().loadContent(dialogHtmlContent.getHtmlContent());
            webView.setPrefSize(dialogHtmlContent.getPrefWidth(), dialogHtmlContent.getPrefHeight());
            webView.setBlendMode(dialogHtmlContent.getBlendMode());
            if (gridPane == null) vBox.getChildren().addAll(webView);
            else vBox.getChildren().addAll(gridPane, webView);
        } else {
            vBox.getChildren().addAll(gridPane, label);
        }
        alert.close();
        vBox.autosize();
        vBox.layout();
        return vBox;
    }


    public static void showProgressDialog(CancelTimeOutJob job, Scene scene) {
        showProgressDialog(job, scene, -1, -1);
    }

    public static void showProgressDialog(CancelTimeOutJob job, Scene scene, String cancelBtnText) {
        showProgressDialog(job, scene, -1, -1, cancelBtnText);
    }

    public static void showProgressDialog(CancelTimeOutJob job, Scene scene, double width, double height) {
        showProgressDialog(job, scene, -1, -1, null);
    }


    public static void showProgressDialog(CancelTimeOutJob job, Scene scene, double width, double height, String cancelBtnText) {

        Task<Void> task = new Task<Void>() {


            protected void setProgress(double progress) {
                this.updateProgress(progress, 1.0);
            }

            protected void setProgressMsg(String msg) {
                this.updateMessage(msg);
            }

            private void setReady() {

            }

            @Override
            protected Void call() throws Exception {

                Platform.runLater(()->{
                    job.getProgressProperty().addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            Platform.runLater(() -> setProgress(newValue.doubleValue()));
                        }
                    });
                    job.getProgressMsgProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            Platform.runLater(() -> setProgressMsg(newValue));
                        }
                    });
                    job.getReadyProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if (newValue) Platform.runLater(() -> setReady());
                        }
                    });


                    this.stateProperty().addListener(new ChangeListener<Worker.State>() {
                        @Override
                        public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                            if(newValue==Worker.State.CANCELLED){
                                job.cancel();
                            }
                        }
                    });
                });

                job.startNewThread();

                while (!job.getReadyProperty().get()) {
                    Thread.sleep(100);
                }

                return null;
            }
        };
        showProgressDialog(task, scene, width, height, cancelBtnText);

    }


    public static void showProgressDialog(Task<Void> task, Scene scene) {
        showProgressDialog(task, scene, -1, -1);
    }

    public static void showProgressDialog(Task<Void> task, Scene scene, double width, double height) {
        showProgressDialog(task, scene, width, height, null);
    }

    public static void showProgressDialog(Task<Void> task, Scene scene, String cancelBtnText) {
        showProgressDialog(task, scene, -1, -1, cancelBtnText);
    }

    public static void showProgressDialog(Task<Void> task, Scene scene, double width, double height, String cancelBtnText) {

        if (task == null || scene == null) return;

        //create content and bind task properties
        VBox vBox = new VBox();

        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);

        Label lbl = new Label("");
        ProgressBar pb = new ProgressBar();
        vBox.getChildren().addAll(lbl, pb);

        FxmlDialog dialog = getDialog(vBox, cancelBtnText != null ? DialogResponse.Cancel : DialogResponse.none);
        if (dialog == null) return;

        if (cancelBtnText != null) {
            dialog.btnCancel.setText(cancelBtnText);
            dialog.layout();
            dialog.addButtonClickListener(new ButtonClickListener() {
                @Override
                public void clicked(ButtonType type) {
                    if (type == ButtonType.CANCEL)
                        task.cancel();
                }
            });
        }

        dialog.show(scene, false);
        if (width > 0) {
            dialog.dialogStage.setWidth(width);
            pb.setPrefWidth(width - 20);
        }
        if (height > 0) dialog.dialogStage.setHeight(height);

        pb.progressProperty().bind(task.progressProperty());
        lbl.textProperty().bind(task.messageProperty());


        task.setOnSucceeded(event -> {
            //close dialog
            dialog.dialogStage.hide();
        });

        new Thread(task).start();
    }

    public static DialogResponse showAndWait(String headerText, String contentText, Scene scene) {
        DialogHtmlContent htmlContent = new DialogHtmlContent(contentText);
        htmlContent.setIsHtmlContent(false);
        return showAndWait(headerText, htmlContent, scene);
    }

    public static DialogResponse showAndWait(String headerText, DialogHtmlContent htmlContent, Scene scene) {
        Node node = getAlertDialogNode(Alert.AlertType.CONFIRMATION, headerText, htmlContent);
        FxmlDialog dialog = getDialog(node, DialogResponse.OK, DialogResponse.Cancel);
        if (dialog != null) {
            return dialog.show(scene);
        }
        return DialogResponse.unknown;
    }


    @FXML
    public Pane fxmlContentPane;
    @FXML
    public Button btnOk;
    @FXML
    public Button btnCancel;
    @FXML
    public Button btnApply;
    @FXML
    public ProgressBar progress;
    @FXML
    private Object contentController;
    @FXML
    public CheckBox chkRemember;
    private final Stage dialogStage = new Stage();
    private int responseNumber;
    private Parent parent;
    private Timeline timeline;
    private boolean remember = false;


    @FXML
    public void initialize() {

    }

    private void setParent(Parent parent, Object controller, int dialogNumber) {
        this.parent = parent;
        this.contentController = controller;
        this.responseNumber = dialogNumber;
    }

    private void setContentController(Object controller) {
        this.contentController = controller;
    }

    public Object getContentController() {
        return this.contentController;
    }

    public DialogResponse show(Scene parentScene) {
        return show(parentScene, true);
    }

    public DialogResponse show(Scene parentScene, boolean wait) {

        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setAlwaysOnTop(true);
        dialogStage.setResizable(false);
        dialogStage.initStyle(StageStyle.UTILITY);

        Scene dialogScene = new Scene(parent);

        //set style from parent scene
        if (parentScene != null) dialogScene.getStylesheets().addAll(parentScene.getStylesheets());

        dialogStage.setScene(dialogScene);
        if (timeline != null) timeline.play();
        //dialogStage.sizeToScene();
        this.btnOk.requestFocus();
        if (parentScene != null) {
            // Calculate the center position of the parent Stage
            Window primaryStage = parentScene.getWindow();
            double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
            double centerYPosition = primaryStage.getY() + primaryStage.getHeight() / 2d;


            // Relocate the pop-up Stage
            dialogStage.setOnShown(ev -> {
                dialogStage.setX(centerXPosition - dialogStage.getWidth() / 2d);
                dialogStage.setY(centerYPosition - dialogStage.getHeight() / 2d);
                dialogStage.show();
            });
        }


        if (wait) dialogStage.showAndWait();
        else {
            dialogStage.show();
            return DialogResponse.none;
        }


        DialogResponse response = responseHashMap.get(this.responseNumber);
        responseHashMap.remove(this.responseNumber);
        return response;
    }

    private void setTimed(Duration duration) {
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progress.progressProperty(), 0)),
                new KeyFrame(duration, e -> {
                    // close dialog with OK
                    responseHashMap.put(this.responseNumber, DialogResponse.OK);
                    this.dialogStage.hide();
                }, new KeyValue(progress.progressProperty(), 1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        progress.setVisible(true);
        progress.setOnMouseClicked(e -> {
            if (progress.getProgress() >= 0) {
                //stop closing timer
                timeline.stop();
                progress.setProgress(-1);
            } else {
                //restart closing timer
                timeline.playFromStart();
            }
        });
    }

    private void enableButtons(DialogResponse[] responseTypes) {
        this.btnOk.setVisible(false);
        this.btnCancel.setVisible(false);
        this.btnApply.setVisible(false);
        for (DialogResponse type : responseTypes) {
            if (type == null) continue;
            switch (type) {
                case OK:
                    this.btnOk.setVisible(true);
                    break;
                case Cancel:
                    this.btnCancel.setVisible(true);
                    break;
                case Apply:
                    this.btnApply.setVisible(true);
            }
        }
        layout();
    }


    public void layout() {
        // arrange buttons from right to left
        double buttonMargin = 14;
        double right = buttonMargin;
        if (btnOk.isVisible()) {
            btnOk.setPrefWidth((btnOk.getText().length() * 9) + btnOk.getPadding().getLeft() + btnOk.getPadding().getRight() + 20); // 9 ist ein arbiträrer Faktor für die Breite pro Zeichen
            btnOk.setPrefHeight(btnOk.getFont().getSize() + 10); // 10 ist ein arbiträrer Faktor für die Höhe
            AnchorPane.setRightAnchor(btnOk, right);
            right += btnOk.getPrefWidth() + buttonMargin;
        }

        if (btnApply.isVisible()) {
            btnApply.setPrefWidth((btnApply.getText().length() * 9) + btnApply.getPadding().getLeft() + btnApply.getPadding().getRight() + 20); // 9 ist ein arbiträrer Faktor für die Breite pro Zeichen
            btnApply.setPrefHeight(btnApply.getFont().getSize() + 10); // 10 ist ein arbiträrer Faktor für die Höhe
            AnchorPane.setRightAnchor(btnApply, right);
            right += btnApply.getPrefWidth() + buttonMargin;
        }

        if (btnCancel.isVisible()) {
            btnCancel.setPrefWidth((btnCancel.getText().length() * 9) + btnCancel.getPadding().getLeft() + btnCancel.getPadding().getRight() + 8); // 9 ist ein arbiträrer Faktor für die Breite pro Zeichen
            btnCancel.setPrefHeight(btnCancel.getFont().getSize() + 10); // 10 ist ein arbiträrer Faktor für die Höhe

            AnchorPane.setRightAnchor(btnCancel, right);
            right += btnCancel.getPrefWidth() + buttonMargin;
        }
        AnchorPane.setRightAnchor(progress, right);
    }

    public void onOkClicked(ActionEvent actionEvent) {
        actionEvent.consume();
        responseHashMap.put(this.responseNumber, this.remember && this.chkRemember.isSelected() ?
                DialogResponse.RememberOk : DialogResponse.OK);
        this.dialogStage.hide();
        okClicked();
    }

    public void onCancelClicked(ActionEvent actionEvent) {
        actionEvent.consume();
        responseHashMap.put(this.responseNumber, this.remember && this.chkRemember.isSelected() ?
                DialogResponse.RememberCancel : DialogResponse.Cancel);
        this.dialogStage.hide();
        cancelClicked();
    }


    public interface ButtonClickListener {
        void clicked(ButtonType type);
    }

    private ArrayList<ButtonClickListener> buttonClickListeners = new ArrayList<>();

    public void addButtonClickListener(ButtonClickListener listener) {
        buttonClickListeners.add(listener);
    }

    private void cancelClicked() {
        fireEvent(ButtonType.CANCEL);
    }

    private void applyClicked() {
        fireEvent(ButtonType.APPLY);
    }

    private void okClicked() {
        fireEvent(ButtonType.OK);
    }

    private void fireEvent(ButtonType type) {
        for (ButtonClickListener listener : buttonClickListeners)
            listener.clicked(type);
    }

    public void onApplyClicked(ActionEvent actionEvent) {
        actionEvent.consume();
        applyClicked();
    }

    public final void setOnApplyAction(EventHandler<ActionEvent> actionEventEventHandler) {
        this.btnApply.setOnAction(actionEventEventHandler);
        applyClicked();
    }

    public void onOkTyped(KeyEvent keyEvent) {
        keyEvent.consume();
        responseHashMap.put(this.responseNumber, DialogResponse.OK);
        this.dialogStage.hide();
        okClicked();
    }

    public void onCancelTyped(KeyEvent keyEvent) {
        keyEvent.consume();
        responseHashMap.put(this.responseNumber, DialogResponse.Cancel);
        this.dialogStage.hide();
        cancelClicked();
    }

    public void onApplyTyped(KeyEvent keyEvent) {
        keyEvent.consume();
        applyClicked();
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
        chkRemember.setVisible(remember);
    }

    public void setRememberText(String rememberText) {
        this.chkRemember.setText(rememberText);
    }

    public void setOkText(String txt) {
        this.btnOk.setText(txt);
    }

    public void setCancelText(String txt) {
        this.btnCancel.setText(txt);
    }

}
