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
package de.longri.fx.utils;

import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class SleepCall {

    public SleepCall(Runnable runnable) {
        this( runnable, false);
    }

    public SleepCall(Runnable runnable, boolean FXthread) {
        this(0, runnable, FXthread);
    }

    public SleepCall(long sleepTime, Runnable runnable) {
        this(sleepTime, runnable, false);
    }

    public SleepCall(long sleepTime, Runnable runnable, boolean FXthread) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (sleepTime > 1000) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    if (canceled.get()) return;
                } else {
                    // divide in sections
                    long count = 0;
                    while (count <= sleepTime) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                        count += 200;
                        if (canceled.get()) return;
                    }
                }
                if (FXthread) {
                    Platform.runLater(runnable);
                } else {
                    runnable.run();
                }
            }
        });
        thread.start();
    }

    private Thread thread;
    private final AtomicBoolean canceled = new AtomicBoolean(false);

    public void cancel() {
        canceled.set(true);
    }
}
