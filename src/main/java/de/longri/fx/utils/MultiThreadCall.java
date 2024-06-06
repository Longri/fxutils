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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultiThreadCall {

    private static final int NUM_THREADS = 24;

    List<Callable<Void>> tasks = new ArrayList<>();
    final ExecutorService executorService;

    public MultiThreadCall() {
        // Erstellen Sie einen ExecutorService mit mehreren Threads
        executorService = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public void addRunnable(String ThreadName, Runnable runnable) {
        tasks.add(() -> {
            AtomicBoolean ready = new AtomicBoolean(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        ready.set(true);
                    }
                }
            }, ThreadName).start();
            while (!ready.get()) {
                Thread.sleep(50);
            }
            return null;
        });
    }

    public void start() {
        new SleepCall(0, this::waitFinish);
    }

    public void waitFinish() {
        try {

            if (tasks == null || tasks.size() <= 0) {
                return;
            }

            // Führen Sie die Aufgaben aus und warten Sie auf deren Abschluss
            executorService.invokeAll(tasks);
            // Beenden Sie den ExecutorService
            executorService.shutdown();

            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
