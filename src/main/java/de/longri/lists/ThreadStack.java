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
package de.longri.lists;


import de.longri.gdx_utils.Array;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by Longri on 06.01.2017.
 */
public class ThreadStack<T extends CancelRunable>  {

    private final Array<T> items;
    private ExecutorService executor;
    private final int maxItems;
    private boolean isDisposed = false;
    private T actualRunning;

    public ThreadStack() {
        this(1);
    }

    public ThreadStack(int maxItemSize) {
        items = new Array<T>();
        maxItems = maxItemSize;
        controlThread.start();
    }

    private Thread controlThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!isDisposed) {
                if (executor == null && items.size!=0) {
                    //start
                    executor = Executors.newFixedThreadPool(1, new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r, "ThreadStackExecutor");
                            thread.setDaemon(true);
                            thread.setPriority(Thread.NORM_PRIORITY + 3);
                            return thread;
                        }
                    });
                    synchronized (items) {
                        T item = items.first();
                        items.remove(item);
                        actualRunning = item;
                        executor.execute(item);
                    }
                    executor.shutdown();
                    try {
                        executor.awaitTermination(4, TimeUnit.HOURS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    actualRunning = null;
                    executor = null;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    int getMaxItemSize() {
        return maxItems;
    }

    public void pushAndStart(T runnable) {
        synchronized (items) {
            if (items.size >= maxItems) {
                T item = items.first();
                this.items.remove(item);
            }
            items.add(runnable);
        }
    }

    public void pushAndStartWithCancelRunning(T runnable) {
        if (actualRunning != null) {
            actualRunning.cancel();
        }
        pushAndStart(runnable);
    }

    public void dispose() {
        isDisposed = true;
        if (executor != null) {
            if (actualRunning != null) {
                actualRunning.cancel();
            }
        }
    }

    public boolean isReadyAndEmpty() {
        synchronized (items) {
            return executor == null && items.size==0;
        }
    }


}
