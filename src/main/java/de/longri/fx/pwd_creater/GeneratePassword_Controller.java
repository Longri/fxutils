package de.longri.fx.pwd_creater;


import de.longri.fx.FxStyles;
import de.longri.fx.I_ShowHeide;
import de.longri.fx.TRANSLATION;
import de.longri.utils.SystemStoredPreferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class GeneratePassword_Controller implements I_ShowHeide, Initializable {

    private static final SystemStoredPreferences PASSWORD_PREF = new SystemStoredPreferences();
    private static final String PREF_LENGTH = "password_length";
    private static final String PREF_CHK_LOW = "password_low";
    private static final String PREF_CHK_DIGIT = "password_digit";
    private static final String PREF_CHK_HIGH = "password_high";
    private static final String PREF_CHK_SPECIAL = "password_special";

    // use https://github.com/vt-middleware/passay/

    public Label fxLblChar;
    public CheckBox fxChkLow;
    public CheckBox fxChkHigh;
    public CheckBox fxChkNumber;
    public CheckBox fxChkSpecial;
    public Label fxLblPassword;
    public Label fxLblMaxPasswordLength;
    public Spinner<Integer> fxSpinnerLength;
    public PasswordField fxPwField;
    public Button fxBtnClear;
    public ToggleButton fxBtnShow;
    public Button fxBtnCopy;
    public Button fxBtnCancel;
    public Button fxBtnOk;
    public Label fxLblMatrix;
    public StackPane fxMatrixStackpane;
    public TextField fxTextField;
    private ReturnListener listener;
    private Stage stage;
    int lastUsedLength = 4;
    AtomicBoolean ready = new AtomicBoolean(false);

    PasswordGenerator generator = new PasswordGenerator();
    List<CharacterRule> rules = new ArrayList<>();

    public void fxHandle_Clear_Show_Copy(ActionEvent actionEvent) {
        actionEvent.consume();
        if (actionEvent.getSource() == fxBtnClear) {
            lastUsedLength = 4;
            fxPwField.setText("");
            ready.set(false);
        } else if (actionEvent.getSource() == fxBtnShow) {
            fxTextField.setVisible(fxBtnShow.isSelected());
        } else if (actionEvent.getSource() == fxBtnCopy) {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(fxTextField.getText());
            clipboard.setContent(content);
        }
    }

    public void fxHandle_Cancel_Ok(ActionEvent actionEvent) {
        actionEvent.consume();
        if (actionEvent.getSource() == fxBtnOk) {
            if (listener != null) {
                listener.Return(ReturnType.ok, fxPwField.getText());
            }
        } else {
            if (listener != null) {
                listener.Return(ReturnType.cancel, "");
            }
        }

        //close the stage
        this.stage.close();

        hide();
    }

    long lastEventTime = 0;
    int[] timerList = new int[]{70, 40, 90, 30, 50};
    int timerNumber = 0;
    int timer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Add and bind the MatrixCanvas to the StackPane
        new MatrixCanvas(fxMatrixStackpane);

        //set default check boxes
        fxChkLow.setSelected(PASSWORD_PREF.getBoolean(PREF_CHK_LOW));
        fxChkHigh.setSelected(PASSWORD_PREF.getBoolean(PREF_CHK_HIGH));
        fxChkNumber.setSelected(PASSWORD_PREF.getBoolean(PREF_CHK_DIGIT));
        fxChkSpecial.setSelected(PASSWORD_PREF.getBoolean(PREF_CHK_SPECIAL));

        //initial spinner
        int initialValue = 8;
        if (PASSWORD_PREF.contains(PREF_LENGTH)) {
            initialValue = PASSWORD_PREF.getInteger(PREF_LENGTH);
        }
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 32, initialValue);

        fxSpinnerLength.setValueFactory(valueFactory);
        setRules();

        // Mose over event
        fxMatrixStackpane.addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                long now = System.currentTimeMillis();
                int length = fxSpinnerLength.getValue();
                if (length < 4 || ready.get()) return;
                if (lastEventTime + timer < now) {
                    // Generated password is 12 characters long, which complies with policy
                    String password = generator.generatePassword(lastUsedLength++, rules);
                    fxPwField.setText(password);
                    lastEventTime = now;
                    if (timerNumber == timerList.length) timerNumber = 0;
                    timer = timerList[timerNumber++];
                    if (lastUsedLength == length) {
                        ready.set(true);
                    }
                }
            }
        });

        //add change listener to controls
        fxChkLow.selectedProperty().addListener(changeListener);
        fxChkHigh.selectedProperty().addListener(changeListener);
        fxChkNumber.selectedProperty().addListener(changeListener);
        fxChkSpecial.selectedProperty().addListener(changeListener);
        fxSpinnerLength.valueProperty().addListener(changeListener);

        // Bind the textField and passwordField text values bidirectionally.
        fxTextField.textProperty().bindBidirectional(fxPwField.textProperty());
        fxTextField.setVisible(false);

        show(null, null);
    }

    ChangeListener changeListener = new ChangeListener<Object>() {
        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            setRules();
        }
    };

    private void setRules() {

        rules.clear();

        // at least one upper-case character
        if (fxChkHigh.isSelected()) {
            rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
        }

        // at least one lower-case character
        if (fxChkLow.isSelected()) {
            rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        }

        // at least one digit character
        if (fxChkNumber.isSelected()) {
            rules.add(new CharacterRule(EnglishCharacterData.Digit, 1));
        }

        // at least one spezial character
        if (fxChkSpecial.isSelected()) {

            CharacterData special = new CharacterData() {

                @Override
                public String getErrorCode() {
                    return "SPECIAL";
                }

                @Override
                public String getCharacters() {
                    return "!@#$%^&*()_+-=[]{};':\"<>,.?~`";
                }
            };

            rules.add(new CharacterRule(special, 1));
        }

        // if nothing selected use alphabetical
        if (rules.isEmpty()) {
            rules.add(new CharacterRule(EnglishCharacterData.Alphabetical, 1));
        }

        PASSWORD_PREF.put(PREF_LENGTH, fxSpinnerLength.getValue());
        PASSWORD_PREF.put(PREF_CHK_LOW, fxChkLow.isSelected());
        PASSWORD_PREF.put(PREF_CHK_HIGH, fxChkHigh.isSelected());
        PASSWORD_PREF.put(PREF_CHK_DIGIT, fxChkNumber.isSelected());
        PASSWORD_PREF.put(PREF_CHK_SPECIAL, fxChkSpecial.isSelected());

    }

    public enum ReturnType {
        cancel, ok
    }

    public interface ReturnListener {
        void Return(ReturnType type, String password);
    }

    public void setReturnListener(ReturnListener listener, Stage stage) {
        this.listener = listener;
        this.stage = stage;
    }

    @Override
    public void show(Object o, Stage stage) {

        if (!TRANSLATION.isInitial())
            TRANSLATION.INITIAL("de.longri.fx.pwd_creater.uiText");
        else
            TRANSLATION.loadBundle("de.longri.fx.pwd_creater.uiText");

        // bind translations
        try {
            TRANSLATION.bind(fxLblChar, "generatePassword.lbl.fxLblChar");
            TRANSLATION.bind(fxChkLow, "generatePassword.chk.fxChkLow");
            TRANSLATION.bind(fxChkHigh, "generatePassword.chk.fxChkHigh");
            TRANSLATION.bind(fxChkNumber, "generatePassword.chk.fxChkNumber");
            TRANSLATION.bind(fxChkSpecial, "generatePassword.chk.fxChkSpecial");
            TRANSLATION.bind(fxLblPassword, "generatePassword.lbl.fxLblPassword");
            TRANSLATION.bind(fxLblMaxPasswordLength, "generatePassword.lbl.fxLblMaxPasswordLength");
            TRANSLATION.bind(fxBtnClear, "generatePassword.btn.fxBtnClear");
            TRANSLATION.bind(fxBtnShow, "generatePassword.btn.fxBtnShow_1", "generatePassword.btn.fxBtnShow_2");
            TRANSLATION.bind(fxBtnCopy, "generatePassword.btn.fxBtnCopy");
            TRANSLATION.bind(fxBtnCancel, "generatePassword.btn.fxBtnCancel");
            TRANSLATION.bind(fxBtnOk, "generatePassword.btn.fxBtnOk");
            TRANSLATION.bind(fxLblMatrix, "generatePassword.lbl.fxLblMatrix");
        } catch (Exception e) {
            //ignore
        }
    }

    @Override
    public void beforeHide() {

    }

    @Override
    public void hide() {

        // unbind translations
        TRANSLATION.unbind(fxLblChar);
        TRANSLATION.unbind(fxChkLow);
        TRANSLATION.unbind(fxChkHigh);
        TRANSLATION.unbind(fxChkNumber);
        TRANSLATION.unbind(fxChkSpecial);
        TRANSLATION.unbind(fxLblPassword);
        TRANSLATION.unbind(fxLblMaxPasswordLength);
        TRANSLATION.unbind(fxBtnClear);
        TRANSLATION.unbind(fxBtnShow);
        TRANSLATION.unbind(fxBtnCopy);
        TRANSLATION.unbind(fxBtnCancel);
        TRANSLATION.unbind(fxBtnOk);
        TRANSLATION.unbind(fxLblMatrix);
    }

    @Override
    public double getMinWidth() {
        return 565;
    }

    @Override
    public double getMinHeight() {
        return 616;
    }

    @Override
    public void fxStyleChanged(FxStyles fxStyles) {

    }

    @Override
    public String getTitle() {
        return null;
    }
}
