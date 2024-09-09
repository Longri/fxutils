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

import de.longri.UTILS;
import de.longri.fx.utils.SvgImage;
import de.longri.gdx_utils.files.FileHandle;
import de.longri.logging.LongriLoggerConfiguration;
import de.longri.logging.LongriLoggerFactory;
import de.longri.logging.LongriLoggerInit;
import de.longri.utils.SystemType;
import de.longri.view.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.batik.transcoder.TranscoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FX_Application_Test extends FX_Application {


    private static final Logger log = LoggerFactory.getLogger(FX_Application_Test.class);

    static {
        //initial Logger
        try {

            if (SystemType.getSystemType() == SystemType.WIN) {
                LongriLoggerConfiguration.setConfigurationFile(FX_Application_Test.class.getClassLoader().getResourceAsStream("logger/longriLoggerWin.properties"));
            } else {
                LongriLoggerConfiguration.setConfigurationFile(FX_Application_Test.class.getClassLoader().getResourceAsStream("logger/longriLogger.properties"));
            }
            LongriLoggerFactory factory = ((LongriLoggerFactory) LoggerFactory.getILoggerFactory());
            factory.reset();
            LongriLoggerInit.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected String getFirstViewName() {
        return "Start";
    }

    @Override
    protected FileHandle getPreferencesFile() {
        return UTILS.files.absolute("./storedPreferences.xml");
    }

    FxmlScene startScene;
    private FXMLLoader fxmlLoader_StartScene = new FXMLLoader(FX_Application_Test.class.getResource("/de/longri/Start.fxml"));

    LogInView logInView = new LogInView(false) {
        @Override
        public String getTitle() {
            return VERSION;
        }

        @Override
        protected void cancelClicked() {
            try {
                FX_Application_Test.this.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected boolean logIn(String user, String password) {
            return true;
        }

        @Override
        protected String getCloseViewName() {
            return "Start";
        }

        @Override
        protected Image getLogInImage() {

            SvgImage svgImage = null;
            try {
                svgImage = new SvgImage(FX_Application_Test.class.getResourceAsStream("/IT-SVG.svg"), 300, 300);
            } catch (TranscoderException | IOException e) {
                //throw new RuntimeException(e);
            }
            return svgImage;
        }

        @Override
        protected Text getLogInText() {
            Text text = new Text("LogInTestView");
            text.getStyleClass().add("logintext");
            return text;
        }

    };

    FxmlScene logInScene = new FxmlScene(logInView, "logInView", false);


    @Override
    public FxmlScene initialScene(String name) throws IOException {


        switch (name) {
            case "Start":
                startScene = new FxmlScene(fxmlLoader_StartScene, name, true);
                return startScene;
            case "login":
                return logInScene;
        }
        return null;
    }

    @Override
    protected void setStyle(FxStyles style, FxmlScene scene) {
        super.setStyle(style, scene);
    }

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        stage.setTitle(" FX App TEST  " + VERSION);

        InputStream is = this.getClass().getResourceAsStream("/IT-Inventar.svg");
        SvgImage svgImage = new SvgImage(is);
        stage.getIcons().add(svgImage);

    }


    @Override
    public void initialStartStack() {

        final AtomicBoolean LOGIN_CONDITION = new AtomicBoolean(false);
        final AtomicBoolean FIRE_CONDITION = new AtomicBoolean(false);
        final AtomicInteger TEST_COUNT = new AtomicInteger(0);
        try {
            LogInConditionScene loginScene = new LogInConditionScene("LogIn-Name") {


                @Override
                public String getTitle() {
                    return VERSION;
                }

                @Override
                protected void cancelClicked() {
                    try {
                        FX_Application_Test.this.stop();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public boolean isConditionMet() {
                    return LOGIN_CONDITION.get();
                }

                @Override
                protected boolean logIn(String user, String password) {
                    if (TEST_COUNT.incrementAndGet() > 2) {
                        LOGIN_CONDITION.set(true);
                    }
                    return true;
                }

                @Override
                protected Image getLogInImage() {

                    SvgImage svgImage = null;
                    try {
                        svgImage = new SvgImage(FX_Application_Test.class.getResourceAsStream("/IT-SVG.svg"), 50, 50);
                    } catch (TranscoderException | IOException e) {
                        //throw new RuntimeException(e);
                    }
                    return svgImage;
                }

                @Override
                protected Text getLogInText() {
                    Text text = new Text("LogInTestView");
                    text.getStyleClass().add("logintext");
                    return text;
                }
            };

            DBConnectionConditionScene mariaDbConnectionScene = new DBConnectionConditionScene("TitleName-DBConnection", new DBConnectionView.SettingReturnListener() {

                @Override
                public boolean Return(StringBuilder messageBuilder, String user, String password, String host, String port, String databaseName) {
                    // user hase the Connect button clicked
                    return FIRE_CONDITION.getAndSet(true);
                }

                @Override
                public boolean test(StringBuilder messageBuilder, String user, String password, String host, String port, String databaseName) {
                    // try DB connection and return the result
                    // write the Message into MessageBuilder.
                    messageBuilder.append("Can't connect to database");
                    return false;
                }
            }) {
                @Override
                public double getDBImageWidth() {
                    return 100;
                }

                @Override
                public double getDBImageHeight() {
                    return 100;
                }

                @Override
                public boolean isConditionMet() {
                    return FIRE_CONDITION.get();
                }

                @Override
                public void cancelClicked() {
                    try {
                        FX_Application_Test.this.stop();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public Image getDBImage() {
                    SvgImage svgImage = null;
                    try {
                        svgImage = new SvgImage(FX_Application_Test.class.getResourceAsStream("/icons/firebirdDB.svg"), 382, 382);
                    } catch (TranscoderException | IOException e) {
                        //throw new RuntimeException(e);
                    }
                    return svgImage;
                }

                @Override
                public String getMessageText() {
                    return "DBConnectTestView";
                }
            };

            DBClusterConnectionConditionScene clusterConnectionScene = new DBClusterConnectionConditionScene("TitleName-DBClusterConnection", new DBClusterConnectionView.SettingReturnListener() {

                @Override
                public boolean Return(StringBuilder messageBuilder, String user, String password, String[] hosts, String ports[], String databaseName) {
                    // user hase the Connect button clicked
                    return FIRE_CONDITION.getAndSet(true);
                }

                @Override
                public boolean test(StringBuilder messageBuilder, String user, String password, String[] hosst, String[] ports, String databaseName) {
                    // try DB connection and return the result
                    // write the Message into MessageBuilder.
                    messageBuilder.append("Can't connect to database");
                    return false;
                }
            }) {
                @Override
                public double getDBImageWidth() {
                    return 100;
                }

                @Override
                public double getDBImageHeight() {
                    return 100;
                }

                @Override
                public boolean isConditionMet() {
                    return FIRE_CONDITION.get();
                }

                @Override
                public void cancelClicked() {
                    try {
                        FX_Application_Test.this.stop();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public Image getDBImage() {
                    SvgImage svgImage = null;
                    try {
                        svgImage = new SvgImage(FX_Application_Test.class.getResourceAsStream("/icons/firebirdDB.svg"), 382, 382);
                    } catch (TranscoderException | IOException e) {
                        //throw new RuntimeException(e);
                    }
                    return svgImage;
                }

                @Override
                public String getMessageText() {
                    return "DBConnectTestView";
                }
            };

//            CONDITION_START_STACK.add(clusterConnectionScene);
            CONDITION_START_STACK.add(mariaDbConnectionScene);
//            CONDITION_START_STACK.add(loginScene);
        } catch (IOException e) {
            log.error("Could not initialize LogInConditionScene", e);
        }


    }
}
