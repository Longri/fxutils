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

import de.longri.gdx_utils.files.FileHandle;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class FX_ApplicationTest {

    @Test
    void mainLoopTest() throws Exception {

        FX_Application app = new FX_Application() {
            @Override
            protected String getFirstViewName() {
                return null;
            }

            @Override
            protected FileHandle getPreferencesFile() {
                return null;
            }

            @Override
            public FxmlScene initialScene(String name) throws IOException {
                return null;
            }
        };

        final AtomicInteger count = new AtomicInteger();
        app.start(null);

        app.runLater(new Runnable() {
            @Override
            public void run() {
                count.incrementAndGet();
            }
        }, false);
        app.runLater(new Runnable() {
            @Override
            public void run() {
                count.incrementAndGet();
            }
        }, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    app.runningStop();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


        while (app.running()) {
            Thread.sleep(20);
        }

        assertEquals(2, count.get());
    }
}