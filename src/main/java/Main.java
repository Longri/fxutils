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


import de.longri.fx.TRANSLATION;
import de.longri.fx.translation.LangSelectionBox;
import de.longri.fx.translation.Language;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException, URISyntaxException {
        primaryStage.setTitle("");
        Group root = new Group();
        Scene scene = new Scene(root, 400, 300, Color.WHITE);

        GridPane gridpane = new GridPane();


        TRANSLATION.INITIAL(Language.ENG, Language.GER);
        TRANSLATION.loadBundle("de/longri/fx/translation/main");
        LangSelectionBox cmb = new LangSelectionBox(null);

        Label lbl = new Label("InitialText");
        gridpane.add(cmb, 0, 0);
        gridpane.add(lbl, 1, 1);
        root.getChildren().add(gridpane);
        primaryStage.setScene(scene);

        primaryStage.show();

        Platform.runLater(() -> {

            //bind
            TRANSLATION.bind(lbl, "test");
            TRANSLATION.bind(primaryStage.titleProperty(), "test");

        });

        Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                TRANSLATION.setLocale(Locale.GERMAN);
            }
        };

        TimerTask tt2 = new TimerTask() {
            @Override
            public void run() {
                TRANSLATION.setLocale(Locale.ENGLISH);
            }
        };

        timer.schedule(tt, 3000);
        timer.schedule(tt2, 5000);


    }

}

   