package de.longri.fx.pwd_creater;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ShowGeneratePasswordDialog extends Application {

    private static final Logger log = LoggerFactory.getLogger(ShowGeneratePasswordDialog.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane pane = (Pane) loader.load(getClass().getResourceAsStream("./generatePassword.fxml"));
        //Get the controller
        GeneratePassword_Controller controller = (GeneratePassword_Controller) loader.getController();
        Scene scene = new Scene(pane, 565, 616);
        primaryStage.setScene(scene);
        primaryStage.show();
//        primaryStage.setResizable(false);

        controller.show(null, primaryStage);
        controller.setReturnListener(new GeneratePassword_Controller.ReturnListener() {
            @Override
            public void Return(GeneratePassword_Controller.ReturnType type, String password) {
                log.debug("Return {} with value: {}", type, password);
            }
        }, primaryStage);
    }
}
