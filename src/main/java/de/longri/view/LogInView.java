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

import de.longri.fx.*;
import de.longri.fx.utils.FxFocusNode;
import de.longri.fx.utils.FxFocusOrderHelper;
import de.longri.fx.TRANSLATION;
import de.longri.fx.translation.LangSelectionBox;
import de.longri.fx.translation.Language;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class LogInView extends SelfLoading_Controller {

    protected final Logger log = LoggerFactory.getLogger(LogInView.class);

    @FXML
    public StackPane fxRoot;
    @FXML
    AnchorPane fxPwAnchorPane;
    @FXML
    public TextField fxPwTextField;
    @FXML
    public TextField fxUserTextField;
    @FXML
    public PasswordField fxPwField;
    @FXML
    public ToggleButton fxPwToggle;
    @FXML
    public ImageView fxImageView;
    @FXML
    HBox fxImageContainer;
    @FXML
    VBox fxVBox;
    @FXML
    public ComboBox fxComboBoxLang;
    @FXML
    public Button fxBtnLogIn;
    @FXML
    public Button fxBtnCancel;
    @FXML
    public Label fxLblUser;
    @FXML
    public Label fxLblPw;
    @FXML
    public CheckBox fxChkRemember;


    private boolean showRemember = false;
    protected Stage stage;

    public LogInView() {
        super("/views/loginView.fxml");
        showRemember = false;
        fxChkRemember.setVisible(showRemember);
    }

    public LogInView(boolean showRemember) {
        super("/views/loginView.fxml");
        this.showRemember = showRemember;
        fxChkRemember.setVisible(showRemember);
    }

    public void setShowRemember(boolean show){
        this.showRemember = show;
        fxChkRemember.setVisible(show);
    }

    protected abstract void cancelClicked();

    protected abstract boolean logIn(String user, String password);

    protected abstract String getCloseViewName();

    protected abstract Image getLogInImage();

    protected abstract Text getLogInText();


    AtomicBoolean firstVisible = new AtomicBoolean(true);

    @Override
    public void show(Object data, Stage stage) {

        this.stage = stage;

        bindTranslations();

        //DEBUG
//        fxImageContainer.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

        if(firstVisible.getAndSet(false)){
            Platform.runLater(() -> {
                // initialize PW view toggle function
                new ViewablePasswordHandler(fxPwAnchorPane, fxPwTextField, fxPwField, fxPwToggle);
            });

            //if image null remove ImageView from Pane
            Image img = getLogInImage();
            Text logInText = getLogInText();
            if (logInText != null) fxVBox.getChildren().add(img == null ? 0 : 1, logInText);
            if (img == null) {
                fxVBox.getChildren().remove(fxImageContainer);
                log.debug("remove ImageView from LogInView");
            } else {
                fxImageView.setImage(img);
                fxImageView.setFitHeight(img.getHeight());
                fxImageView.setFitWidth(img.getWidth());
                fxImageView.setPreserveRatio(true);
            }
            Platform.runLater(() -> {
                fxVBox.setAlignment(Pos.CENTER);
                fxVBox.layout();
                fxRoot.layout();
                Platform.runLater(() -> {
                    double height = fxVBox.layoutBoundsProperty().get().getHeight() - 100;
                    double width = 0;
                    for (Node child : fxVBox.getChildren()) {
                        width = Double.max(width, child.getLayoutBounds().getWidth());
                    }
                    if (logInText != null) height += logInText.layoutBoundsProperty().get().getHeight();
                    if (img != null) height += img.getHeight();
//                else height += ( );
                    fxVBox.setPrefHeight(height);
                    fxRoot.setPrefHeight(height);

                    width += 20;
                    fxVBox.setPrefWidth(width);
                    fxRoot.setPrefWidth(width);

                    stage.setWidth(width);
                    stage.setHeight(height);

                });
            });
        }

    }

    @Override
    public void beforeHide() {

    }

    @Override
    public void hide() {
        unbindTranslations();
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

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TRANSLATION.INITIAL(Language.ENG, Language.GER);
        TRANSLATION.loadBundle("de.longri.fx.translation.main");

        TRANSLATION.addChangeListener(new ChangeListener<Locale>() {
            @Override
            public void changed(ObservableValue<? extends Locale> observable, Locale oldValue, Locale newValue) {
                // store to System
                Language.storeSystem(newValue);
            }
        });

        //replace lang choice box
        fxComboBoxLang = new LangSelectionBox(fxComboBoxLang);
        fxComboBoxLang.setValue(Language.getUserPrefLanguage());
        
        setFocusOrder();

        fxChkRemember.getStyleClass().add("my-checkbox");

    }

    private void setFocusOrder() {

        //TODO handle invisible remember me checkbox must remove from handler



        //set Focus order
        FxFocusOrderHelper focusOrderHelperPw = new FxFocusOrderHelper();

        FxFocusNode userFocus = new FxFocusNode(this.fxUserTextField, this.fxPwField, this.fxBtnLogIn);
        focusOrderHelperPw.addFocusNode(userFocus);

        FxFocusNode passFocus = new FxFocusNode(this.fxPwField, this.fxPwToggle, this.fxUserTextField);
        passFocus.setDOWN(this.fxChkRemember);
        focusOrderHelperPw.addFocusNode(passFocus);

        FxFocusNode btnShowFocus = new FxFocusNode(this.fxPwToggle, this.fxChkRemember, this.fxPwField);
        focusOrderHelperPw.addFocusNode(btnShowFocus);

        FxFocusNode chkRememberFocus =new FxFocusNode(this.fxChkRemember, this.fxComboBoxLang, this.fxPwToggle);
        focusOrderHelperPw.addFocusNode(chkRememberFocus);

        FxFocusNode langFocus = new FxFocusNode(this.fxComboBoxLang, this.fxBtnCancel, this.fxChkRemember);
        focusOrderHelperPw.addFocusNode(langFocus);


        FxFocusNode btnCancelFocus = new FxFocusNode(this.fxBtnCancel, this.fxBtnLogIn, this.fxComboBoxLang);
        focusOrderHelperPw.addFocusNode(btnCancelFocus);

        FxFocusNode btnOKFocus = new FxFocusNode(this.fxBtnLogIn, this.fxUserTextField, this.fxBtnCancel);
        focusOrderHelperPw.addFocusNode(btnOKFocus);




        FxFocusOrderHelper focusOrderHelperTx = new FxFocusOrderHelper();
        focusOrderHelperTx.setPause(true);

        FxFocusNode userFocus2 = new FxFocusNode(this.fxUserTextField, this.fxPwField, this.fxBtnLogIn);
        focusOrderHelperTx.addFocusNode(userFocus2);

        FxFocusNode passFocus2 = new FxFocusNode(this.fxPwTextField, this.fxPwToggle, this.fxUserTextField);
        passFocus.setDOWN(this.fxChkRemember);
        focusOrderHelperTx.addFocusNode(passFocus2);

        FxFocusNode btnShowFocus2 = new FxFocusNode(this.fxPwToggle, this.fxChkRemember, this.fxPwTextField);
        focusOrderHelperTx.addFocusNode(btnShowFocus2);

        FxFocusNode chkRememberFocus2 =new FxFocusNode(this.fxChkRemember, this.fxComboBoxLang, this.fxPwToggle);
        focusOrderHelperTx.addFocusNode(chkRememberFocus2);

        FxFocusNode langFocus2 = new FxFocusNode(this.fxComboBoxLang, this.fxBtnCancel, this.fxChkRemember);
        focusOrderHelperTx.addFocusNode(langFocus2);

        FxFocusNode btnCancelFocus2 = new FxFocusNode(this.fxBtnCancel, this.fxBtnLogIn, this.fxComboBoxLang);
        focusOrderHelperTx.addFocusNode(btnCancelFocus2);

        FxFocusNode btnOKFocus2 = new FxFocusNode(this.fxBtnLogIn, this.fxUserTextField, this.fxBtnCancel);
        focusOrderHelperTx.addFocusNode(btnOKFocus2);

        this.fxPwTextField.visibleProperty().addListener((observableValue, newValue, oldValue) -> {
            if (newValue) {
                focusOrderHelperTx.setPause(true);
                focusOrderHelperPw.setPause(false);
            } else {
                focusOrderHelperTx.setPause(false);
                focusOrderHelperPw.setPause(true);
            }
        });

        EventHandler<KeyEvent> enterHandler = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                    keyEvent.consume();
                    fx_onOkClicked();
                }
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                    keyEvent.consume();
                    fx_onCancelClicked();
                }
            }
        };
        this.fxPwTextField.addEventHandler(KeyEvent.KEY_PRESSED, enterHandler);
        this.fxPwField.addEventHandler(KeyEvent.KEY_PRESSED, enterHandler);
    }

    @Override
    public Node getRootNode() {
        return fxRoot;
    }

    @Override
    public boolean isResizable() {
        return false;
    }


    private void bindTranslations() {
        TRANSLATION.bind(fxBtnLogIn, "login");
        TRANSLATION.bind(fxBtnCancel, "cancel");
        TRANSLATION.bind(fxLblUser, "user");
        TRANSLATION.bind(fxLblPw, "pw");
        TRANSLATION.bind(fxChkRemember, "remember_me");
    }

    private void unbindTranslations() {
        TRANSLATION.unbind(fxBtnLogIn);
        TRANSLATION.unbind(fxBtnCancel);
        TRANSLATION.unbind(fxLblUser);
        TRANSLATION.unbind(fxLblPw);
        TRANSLATION.unbind(fxChkRemember);
    }

    @FXML
    public void fx_onOkClicked() {
        if (this.logIn(fxUserTextField.getText(), fxPwField.getText())) {
            FX_Application.INSTANCE.showView(this.getCloseViewName());
        } else {
            String wrongPwMsg = TRANSLATION.getTranslation("wrongPw");
            FX_Application.logAndShowErrorMsg(log, wrongPwMsg, null);
        }
    }

    @FXML
    public void fx_onCancelClicked() {
        this.cancelClicked();
    }

}
