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
import de.longri.gdx_utils.files.FileHandle;
import de.longri.utils.FileStoredPreferences;
import de.longri.utils.Preferences;
import de.longri.utils.Stack;
import de.longri.utils.SystemStoredPreferences;
import de.longri.fx.DialogHtmlContent;
import de.longri.fx.DialogResponse;
import de.longri.fx.FxmlDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jthemedetecor.OsThemeDetector;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * In case of Exception "File not found: autogenerated/version.txt (Classpath)"
 * Add following lines to build.gradle
 * <p>
 * plugins {
 * id 'java'
 * id 'org.openjfx.javafxplugin' version '0.0.10'
 * id "org.ajoberstar.grgit" version "4.1.0"
 * }
 * <p>
 * task createVersionTxt {
 * doLast {
 * new File(projectDir, "src/main/resources/autogenerated").mkdirs()
 * new File(projectDir, "src/main/resources/autogenerated/version.txt").text = """Version: $version
 * Revision: ${grgit.head().abbreviatedId}
 * Buildtime: ${new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())}
 * """
 * }
 * }
 * <p>
 * task copyChangeTxt(type: Copy) {
 * from '../change.txt'
 * into "./src/main/resources/autogenerated"
 * }
 * <p>
 * classes {
 * dependsOn createVersionTxt
 * dependsOn copyChangeTxt
 * }
 */
public abstract class FX_Application extends Application {
    private static final Logger log = LoggerFactory.getLogger(FX_Application.class);
    public static final String VERSION = getVersion();
    public static final String BUILD = getBuild();
    public static FX_Application INSTANCE;
    public static Preferences PREF;
    public Stage stage;
    public FxStyles style;
    protected FxmlScene lastScene;
    protected Map<String, FxmlScene> sceneMap = new HashMap<>();
    protected List<String> PARAM_LIST;
    protected final Stack<Runnable> RUN_LATER_STACK = new Stack<>();
    protected final Stack<Runnable> EXECUTED_RUN_LATER_STACK = new Stack<>();
    protected final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected static AtomicBoolean platformExit = new AtomicBoolean(false);

    public static Image createImage(Object context, String resourceName) {
        URL _url = context.getClass().getResource(resourceName);
        return new Image(_url.toExternalForm());
    }

    private static String getVersion() {
        String gradleSetVersion = "version";
        UTILS.initialFilesWithDesktop();
        try {
            gradleSetVersion = UTILS.files.classpath("autogenerated/version.txt")
                    .readString().split("\n")[0].split(": ")[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gradleSetVersion;
    }

    private static String getBuild() {
        String gradleSetBuild = "build";
        UTILS.initialFilesWithDesktop();
        try {
            gradleSetBuild = UTILS.files.classpath("autogenerated/version.txt")
                    .readString().split("\n")[2].split(": ", 2)[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gradleSetBuild;
    }


    private void executeRunnables() {
        log.debug("Execute runnables");
        Runnable runnable = null;
        synchronized (RUN_LATER_STACK) {
            runnable = RUN_LATER_STACK.peek();
            while (runnable != null) {
                EXECUTED_RUN_LATER_STACK.push(runnable);
                runnable = RUN_LATER_STACK.peek();
            }
        }

        if (EXECUTED_RUN_LATER_STACK.size() == 0) return;
        runnable = EXECUTED_RUN_LATER_STACK.peek();
        while (runnable != null) {
            if (runnable instanceof ApplicationThreadRunnable) {
                Platform.runLater(runnable);
            } else {
                executor.execute(runnable);
            }
            runnable = EXECUTED_RUN_LATER_STACK.peek();
        }
    }


    @Override
    public void start(Stage stage) throws Exception {
        INSTANCE = this;

        this.stage = stage;

        initialLoadPropertyFile();

        boolean parametersNotNull = getParameters() != null;
        if (parametersNotNull) PARAM_LIST = getParameters().getRaw();

        final OsThemeDetector detector = OsThemeDetector.getDetector();

        this.runLater(() -> {
            try {
                if (detector.isDark()) {
                    //The OS uses a dark theme
                    this.style = FxStyles.DARK;
                } else {
                    //The OS uses a light theme
                    this.style = FxStyles.LIGHT;
                }
            } catch (Exception e) {
                this.style = FxStyles.LIGHT;
            }
            detector.registerListener(isDark ->
            {
                this.runLater(() -> {
                    if (isDark) {
                        // The OS switched to a dark theme
                        this.style = FxStyles.DARK;
                    } else {
                        // The OS switched to a light theme
                        this.style = FxStyles.LIGHT;
                    }
                    setStyle(this.style, this.lastScene);
                    lastScene.fxStyleChanged(this.style);
                });
            });
        });


        if (stage != null) {
            Pane group = new Pane();
            group.setPrefSize(800.0, 560.0);
            stage.setScene(new Scene(group));
//            stage.sizeToScene();
            stage.show();
            String firstView = getFirstViewName();
            try {
                showView(firstView);
            } catch (Exception e) {
                log.error("Exception in Application start method:", e);
            }

            stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean onHidden, Boolean onShown) {
                    log.debug("Stage onHidden {} | onShown {}", onHidden, onShown);

                    if (lastScene.controller instanceof I_StageShowHide) {
                        I_StageShowHide stageShowHide = (I_StageShowHide) lastScene.controller;

                        if (onHidden) {
                            log.debug("Stage on hidden");
                            stageShowHide.stageHide();
                        }

                        if (onShown) {
                            log.debug("Stage on show");
                            stageShowHide.stageShow();
                        }
                    }
                }
            });
        }


        //run main loop
        log.debug("Start Main Loop");
        new Thread(new Runnable() {
            @Override
            public void run() {
                long timesPerSecond = 60;
                long intervall = (long) ((1F / timesPerSecond) * 1000000000F);
                long t = System.nanoTime() + intervall;
                while (!platformExit.get()) {

                    final long n = System.nanoTime();
                    if (t > n) {
                        try {
                            long sleep = t - n;
                            Thread.sleep(sleep / 1000000, (int) (sleep % 1000000));
                        } catch (InterruptedException e) {
                        }
                        t = t + intervall;
                    } else
                        t = n + intervall;

                    executeRunnables();

                }
            }
        }).start();

    }

    protected abstract String getFirstViewName();

    public void initialLoadPropertyFile() {
        FileHandle preferencesFile = getPreferencesFile();

        if (preferencesFile == null) {
            PREF = new SystemStoredPreferences();
        } else {
            try {
                PREF = new FileStoredPreferences(preferencesFile);
            } catch (Exception e) {
                preferencesFile.delete();
                PREF = new FileStoredPreferences(preferencesFile);
            }
        }
    }

    protected abstract FileHandle getPreferencesFile();

    public abstract FxmlScene initialScene(String name) throws IOException;

    public void showView(String viewName) {
        showView(viewName, null);
    }

    public void showView(String viewName, Object obj) {
        FxmlScene scene = sceneMap.get(viewName);
        if (scene == null) {
            try {
                scene = initialScene(viewName);
                sceneMap.put(viewName, scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setScene(scene, obj);
    }

    private void setScene(FxmlScene newScene, Object data) {
        if (lastScene != null) {
            // store stage bounds
            PREF.put(lastScene.getName(), stage);
            lastScene.beforeHide();
        }
        //set style to scene
        setStyle(this.style, newScene);
        if (!stage.titleProperty().isBound()) {
            String title = newScene.getTitle();

            if (title == null) {
                title = newScene.controller.getTitle();
            }
            stage.setTitle(title);
        }
        stage.setScene(newScene);
        //stage.sizeToScene();
        stage.setResizable(newScene.isResizeable());
        if (newScene.isResizeable()) {
            stage.setMinWidth(newScene.getMinWidth());
            stage.setMinHeight(newScene.getMinHeight());
            stage.setMaxWidth(newScene.getMaxWidth());
            stage.setMaxHeight(newScene.getMaxHeight());
        } else {
            stage.setMinWidth(0.0);
            stage.setMinHeight(0.0);
        }
        if (lastScene != null) lastScene.hide();
        lastScene = newScene;
        lastScene.show(data, stage);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                PREF.setStageBounds(lastScene, stage);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        platformExit.set(true);
        log.debug("Stage is closing");
        if (lastScene != null) {
            PREF.put(lastScene.getName(), stage);
            lastScene.beforeHide();
        }
//        Platform.exit();
//        System.exit(0);
    }

    public static void showModalDialog(SelfLoading_Controller controller) {
        showModalDialog(controller.getRootNode(), controller);
    }

    public static void showModalDialog(Node node) {
        showModalDialog(node, null);
    }

    public static void showModalDialog(Node node, SelfLoading_Controller controller) {
        Scene scene = new Scene((Parent) node);
        Stage dialogStage = new Stage();

        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setAlwaysOnTop(true);

        if (controller.isResizable()) {
            dialogStage.setMinWidth(controller.getMinWidth());
            dialogStage.setMinHeight(controller.getMinHeight());
            dialogStage.setMaxWidth(controller.getMaxWidth());
            dialogStage.setMaxHeight(controller.getMaxHeight());
        }

        if (controller.getTitle() != null) {
            dialogStage.setTitle(controller.getTitle());
        }

        dialogStage.setResizable(controller.isResizable());
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setScene(scene);
        //dialogStage.sizeToScene();

        if (INSTANCE != null && INSTANCE.stage != null) {
            dialogStage.show();
            centerStageInStage(INSTANCE.stage, dialogStage);
            INSTANCE.setStyleToScene(scene);
            dialogStage.close();
            if (controller != null)
                controller.show(null, dialogStage);
            dialogStage.showAndWait();
            if (controller != null) {
                controller.beforeHide();
                controller.hide();
            }

        } else {
            if (controller != null)
                controller.show(null, dialogStage);
            dialogStage.showAndWait();
            if (controller != null) {
                controller.beforeHide();
                controller.hide();
            }
        }
    }

    public static void centerStageInStage(Stage outStage, Stage innerStage) {

        double innerWidth = innerStage.getWidth();
        double innerHeight = innerStage.getHeight();
        double outWidth = outStage.getWidth();
        double outHeight = outStage.getHeight();


        double innerX = ((outWidth - innerWidth) / 2.0) + outStage.getX();
        double innerY = ((outHeight - innerHeight) / 2.0) + outStage.getY();


        innerStage.setX(innerX);
        innerStage.setY(innerY);

    }

    public static boolean lockInstance(final String lockFile) {
        try {
            final File file = new File(lockFile);
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            fileLock.release();
                            randomAccessFile.close();
                            file.delete();
                        } catch (Exception e) {
                            log.error("Unable to remove lock file: " + lockFile, e);
                        }
                    }
                });
                return true;
            }
        } catch (Exception e) {
            log.error("Unable to create and/or lock file: " + lockFile, e);
        }
        return false;
    }

    protected void setStyle(FxStyles fxStyles, FxmlScene scene) {
        this.style = fxStyles;
        log.debug("Set style {} to {}:", style, scene.NAME);
        setStyleToScene(scene);
    }

    public void setStyleToScene(Scene scene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                if (style == FxStyles.LIGHT) {
                    log.debug("    load style.css, style-light.css, modena-light.css");
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(FxStyles.class.getResource("/skin/style.css").toExternalForm());
                    scene.getStylesheets().add(FxStyles.class.getResource("/skin/modena-light/modena-light.css").toExternalForm());
                    scene.getStylesheets().add(FxStyles.class.getResource("/skin/style-light.css").toExternalForm());
                } else if (style == FxStyles.DARK) {
                    log.debug("    load style.css, style-dark.css, modena-dark.css");
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(FxStyles.class.getResource("/skin/style.css").toExternalForm());
                    scene.getStylesheets().add(FxStyles.class.getResource("/skin/modena-dark/modena-dark.css").toExternalForm());
                    scene.getStylesheets().add(FxStyles.class.getResource("/skin/style-dark.css").toExternalForm());
                } else {
                    log.debug("    load style.css, style-light.css");
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(FxStyles.class.getResource("/skin/style.css").toExternalForm());
                    scene.getStylesheets().add(FxStyles.class.getResource("/skin/style-default.css").toExternalForm());
                }
            }
        });
    }

    public static void logAndShowMsg(Logger logger, String msg) {
        logAndShowErrorMsg(logger, msg, null);
    }

    public static void logAndShowErrorMsg(Logger logger, String msg, Exception e) {
        logAndShowErrorMsg(false, logger, msg, e);
    }

    public static void logAndShowErrorMsg(boolean wait, Logger logger, String msg, Exception e)  {

        if (e == null)
            logger.debug(msg);
        else
            logger.error(msg, e);

        if (FX_Application.INSTANCE == null) return;

        DialogHtmlContent content = new DialogHtmlContent(msg);
        String msgTxt;

        if (FX_Application.INSTANCE.style == FxStyles.DARK) {
            msgTxt = "<html dir=\"ltr\"><head></head><body bgcolor=3B3F41 contenteditable=\"true\"><p><span style=\"color:#BBBBBB\">" +
                    msg.replace("\n", "</br>") + "</br>" + "</br>" +
                    (e == null ? "" : e.getMessage()) +
                    "</span></p></body></html>";
        } else {
            msgTxt = "<html dir=\"ltr\"><head></head><body contenteditable=\"true\"><p><span>" +
                    msg.replace("\n", "</br>") + "</br>" + "</br>" +
                    (e == null ? "" : e.getMessage()) +
                    "</span></p></body></html>";
        }


        content.setHtmlContent(msgTxt);


        content.setIsHtmlContent(true);
        if (FX_Application.INSTANCE.style == FxStyles.DARK) {
        } else {
            content.setBlendMode(BlendMode.DARKEN);
        }
        content.setPrefWidth(900);
        content.setPrefWidth(600);

        AtomicBoolean stilWait=new AtomicBoolean(wait);
        Platform.runLater(() -> {
            Node node = FxmlDialog.getAlertDialogNode(e == null ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, null, content);
            FxmlDialog dialog = FxmlDialog.getDialog(node, DialogResponse.OK, null);
            dialog.show(INSTANCE.stage.getScene(), wait);
            stilWait.set(false);
        });

        if(stilWait.get()){
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) {
            }
        }
    }

    public boolean running() {
        return !platformExit.get();
    }

    public void runLater(Runnable runnable) {
        this.runLater(runnable, false);
    }

    public void runLater(Runnable runnable, boolean runOnFxThread) {
        synchronized (RUN_LATER_STACK) {
            if (runOnFxThread && !(runnable instanceof ApplicationThreadRunnable)) {
                RUN_LATER_STACK.push((ApplicationThreadRunnable) () -> runnable.run());
            } else {
                RUN_LATER_STACK.push(runnable);
            }
        }
    }

    public void runningStop() {
        platformExit.set(true);
    }

}