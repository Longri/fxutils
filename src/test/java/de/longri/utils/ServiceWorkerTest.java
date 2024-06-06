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
package de.longri.utils;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ServiceWorkerTest {

    @Test
    void work() {
        AtomicBoolean terminated = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(0);
        ServiceWorker worker = new ServiceWorker(new Duration(200)) {

            @Override
            public void work() {
                count.getAndIncrement();
            }

            @Override
            public void terminated() {
                terminated.set(true);
            }

        };

        worker.start();
        try {
            Thread.sleep(1990);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        worker.stop();

        assertEquals(10, count.get(), 1);
        assertFalse(terminated.get());
    }

    @Test
    void workOverTime() {
        AtomicBoolean terminated = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(0);
        ServiceWorker worker = new ServiceWorker(new Duration(200)) {

            @Override
            public void work() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count.getAndIncrement();
            }

            @Override
            public void terminated() {
                terminated.set(true);
            }

        };

        worker.start();
        try {
            Thread.sleep(1990);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        worker.stop();

        assertEquals(6, count.get(), 1);
        assertFalse(terminated.get());
    }

    @Test
    void workException() {
        AtomicBoolean terminated = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(0);
        ServiceWorker worker = new ServiceWorker(new Duration(200)) {

            @Override
            public void work() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (count.getAndIncrement() == 3) {
                    throw new RuntimeException("Ex");
                }
            }

            @Override
            public void terminated() {
                terminated.set(true);
            }
        };

        worker.start();
        try {
            Thread.sleep(10300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        worker.stop();

        assertEquals(33, count.get(), 2);
        assertFalse(terminated.get());
    }


    ServiceWorker worker = null;

    @Test
    void changePeriodTest() {
        AtomicBoolean terminated = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(0);

        worker = new ServiceWorker(new Duration(1000)) {

            @Override
            public void work() {
                if (count.getAndIncrement() == 3) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            worker.changePeriod(new Duration(200));
                        }
                    }).start();
                }
            }

            @Override
            public void terminated() {
                terminated.set(true);
            }
        };

        worker.start();
        try {
            Thread.sleep(10300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        worker.stop();
        assertEquals(40, count.get(), 2);
        assertFalse(terminated.get());
    }


    @Test
    void timeOutWork() {
        AtomicBoolean terminated = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(0);
        ServiceWorker worker = new ServiceWorker(new Duration(200), new Duration(5, TimeUnit.SECONDS)) {

            @Override
            public void work() {
                int cnt = count.getAndIncrement();
                if (cnt % 4 == 0) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }

            @Override
            public void terminated() {
                terminated.set(true);
            }
        };

        worker.start();
        try {
            Thread.sleep(19900);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        worker.stop();

        assertEquals(13, count.get(), 1);
        assertTrue(terminated.get());
    }


}