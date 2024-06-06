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

import de.longri.utils.SystemType;
import com.sun.javafx.application.PlatformImpl;
import de.saxsys.javafx.test.JfxRunner;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

//@RunWith(JfxRunner.class)
class FxTranslationBindingTest {

    static boolean IGNORE_TEST = false;

    @BeforeAll
    static void initFX() {
        System.out.println("System type: "+ SystemType.getSystemType());
        IGNORE_TEST = SystemType.isLinux() || SystemType.isUnknown();
        if (IGNORE_TEST) return;
        PlatformImpl.startup(() -> {
        });
    }

    @Test
    void testLabel() {
        if (IGNORE_TEST) return;
        Locale.setDefault(Locale.ENGLISH);
        FxTranslationBinding TRANSLATE = new FxTranslationBinding("testStrings");
        Label lbl = new Label("TestLabel");
        assertEquals("TestLabel", lbl.getText());

        TRANSLATE.bind(lbl, "main.test1");

        //default lang is en
        assertEquals("test word en", lbl.getText());

        TRANSLATE.setLocale(Locale.GERMAN);
        //lang must change to de
        assertEquals("test wort de", lbl.getText());

        TRANSLATE.setLocale(Locale.ENGLISH);
        //lang must change back to en
        assertEquals("test word en", lbl.getText());

        TRANSLATE.unbind(lbl);

        TRANSLATE.setLocale(Locale.GERMAN);
        //lang must not change to de
        assertEquals("test word en", lbl.getText());

    }

    @Test
    void testButton() {
        if (IGNORE_TEST) return;
        Locale.setDefault(Locale.ENGLISH);
        FxTranslationBinding TRANSLATE = new FxTranslationBinding("testStrings");
        Button btn = new Button("TestLabel");
        assertEquals("TestLabel", btn.getText());

        TRANSLATE.bind(btn, "main.test1");

        //default lang is en
        assertEquals("test word en", btn.getText());

        TRANSLATE.setLocale(Locale.GERMAN);
        //lang must change to de
        assertEquals("test wort de", btn.getText());

        TRANSLATE.setLocale(Locale.ENGLISH);
        //lang must change back to en
        assertEquals("test word en", btn.getText());

        TRANSLATE.unbind(btn);

        TRANSLATE.setLocale(Locale.GERMAN);
        //lang must not change to de
        assertEquals("test word en", btn.getText());

    }

    @Test
    void testTableColumn() {
        if (IGNORE_TEST) return;
        Locale.setDefault(Locale.ENGLISH);
        FxTranslationBinding TRANSLATE = new FxTranslationBinding("testStrings");
        TableColumn col = new TableColumn("TestLabel");
        assertEquals("TestLabel", col.getText());

        TRANSLATE.bind(col, "main.test1");

        //default lang is en
        assertEquals("test word en", col.getText());

        TRANSLATE.setLocale(Locale.GERMAN);
        //lang must change to de
        assertEquals("test wort de", col.getText());

        TRANSLATE.setLocale(Locale.ENGLISH);
        //lang must change back to en
        assertEquals("test word en", col.getText());

        TRANSLATE.unbind(col);

        TRANSLATE.setLocale(Locale.GERMAN);
        //lang must not change to de
        assertEquals("test word en", col.getText());

    }

    @Test
    void testStageTitle() throws InterruptedException {
        if (IGNORE_TEST) return;
        Locale.setDefault(Locale.ENGLISH);

        AtomicBoolean WAIT = new AtomicBoolean(true);
        final org.opentest4j.AssertionFailedError[] asserror = new org.opentest4j.AssertionFailedError[1];
        Platform.runLater(() -> {
            try {
                FxTranslationBinding TRANSLATE = new FxTranslationBinding("testStrings");
                Stage stage = new Stage();

                stage.setTitle("Title");

                assertEquals("Title", stage.getTitle());

                TRANSLATE.bind(stage, "main.test1");

                //default lang is en
                assertEquals("test word en", stage.getTitle());

                TRANSLATE.setLocale(Locale.GERMAN);
                //lang must change to de
                assertEquals("test wort de", stage.getTitle());

                TRANSLATE.setLocale(Locale.ENGLISH);
                //lang must change back to en
                assertEquals("test word en", stage.getTitle());

                TRANSLATE.unbind(stage);

                TRANSLATE.setLocale(Locale.GERMAN);
                //lang must not change to de
                assertEquals("test word en", stage.getTitle());

            } catch (org.opentest4j.AssertionFailedError e) {
                asserror[0] = e;
            }
            WAIT.set(false);
        });

        while (WAIT.get()) {
            Thread.sleep(100);
        }

        WAIT.set(false);
        if (asserror[0] != null) throw asserror[0];
    }


    @Test
    void testMenu() throws InterruptedException {
        if (IGNORE_TEST) return;
        Locale.setDefault(Locale.ENGLISH);

        AtomicBoolean WAIT = new AtomicBoolean(true);
        final org.opentest4j.AssertionFailedError[] asserror = new org.opentest4j.AssertionFailedError[1];
        Platform.runLater(() -> {
            try {
                FxTranslationBinding TRANSLATE = new FxTranslationBinding("testStrings");
                Menu mnu = new Menu("MenuName");


                assertEquals("MenuName", mnu.getText());

                TRANSLATE.bind(mnu, "main.test1");

                //default lang is en
                assertEquals("test word en", mnu.getText());

                TRANSLATE.setLocale(Locale.GERMAN);
                //lang must change to de
                assertEquals("test wort de", mnu.getText());

                TRANSLATE.setLocale(Locale.ENGLISH);
                //lang must change back to en
                assertEquals("test word en", mnu.getText());

                TRANSLATE.unbind(mnu);

                TRANSLATE.setLocale(Locale.GERMAN);
                //lang must not change to de
                assertEquals("test word en", mnu.getText());
            } catch (org.opentest4j.AssertionFailedError e) {
                asserror[0] = e;
            }
            WAIT.set(false);
        });

        while (WAIT.get()) {
            Thread.sleep(100);
        }
        if (asserror[0] != null) throw asserror[0];
    }

    @Test
    void testMenuItem() throws InterruptedException {
        if (IGNORE_TEST) return;
        Locale.setDefault(Locale.ENGLISH);

        AtomicBoolean WAIT = new AtomicBoolean(true);
        final org.opentest4j.AssertionFailedError[] asserror = new org.opentest4j.AssertionFailedError[1];
        Platform.runLater(() -> {
            try {
                FxTranslationBinding TRANSLATE = new FxTranslationBinding("testStrings");
                MenuItem mnu = new MenuItem("MenuName");


                assertEquals("MenuName", mnu.getText());

                TRANSLATE.bind(mnu, "main.test1");

                //default lang is en
                assertEquals("test word en", mnu.getText());

                TRANSLATE.setLocale(Locale.GERMAN);
                //lang must change to de
                assertEquals("test wort de", mnu.getText());

                TRANSLATE.setLocale(Locale.ENGLISH);
                //lang must change back to en
                assertEquals("test word en", mnu.getText());

                TRANSLATE.unbind(mnu);

                TRANSLATE.setLocale(Locale.GERMAN);
                //lang must not change to de
                assertEquals("test word en", mnu.getText());

            } catch (org.opentest4j.AssertionFailedError e) {
                asserror[0] = e;
            }
            WAIT.set(false);
        });

        while (WAIT.get()) {
            Thread.sleep(100);
        }
        if (asserror[0] != null) throw asserror[0];
    }

}