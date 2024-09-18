/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2021 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.longri.fx;

import de.longri.UTILS;
import de.longri.gdx_utils.files.FileHandle;
import de.longri.utils.SystemType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;


public class DemoDirectoryFileChooser extends FX_Application {


    private static Logger log = LoggerFactory.getLogger(DemoDirectoryFileChooser.class);

    public static void main(String[] args) {
        Application.launch();
    }

    FxmlScene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
        log.debug("stop DemoDirectoryChooser");
        this.stage.close();
    }

    @Override
    protected String getFirstViewName() {
        return "DEMO";
    }

    @Override
    protected FileHandle getPreferencesFile() {
        return UTILS.files.absolute(SystemType.isWindows() ? "storedPreferences.xml" : "./storedPreferences.xml");
    }

    @Override
    public FxmlScene initialScene(String name) throws IOException {
        if (scene == null) {
            scene = new FxmlScene(new FXMLLoader(DemoDirectoryFileChooser.class.getResource("/views/empty.fxml")), name, true);
        }
        return scene;
    }

}
