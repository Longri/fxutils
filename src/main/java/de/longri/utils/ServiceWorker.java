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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceWorker {

    Logger log = LoggerFactory.getLogger(ServiceWorker.class);

    private Timer timer;
    private TimerTask task;
    final private AtomicBoolean taskRunning;
    final private AtomicBoolean workerRunning;
    final private Duration TIME_OUT;
    private Duration period;

    public ServiceWorker(Duration period) {
        log.debug("create ServiceWorker with period of : {}", period);
        this.period = period;
        taskRunning = new AtomicBoolean(false);
        workerRunning = new AtomicBoolean(false);
        TIME_OUT = null;
    }

    public ServiceWorker(Duration period, Duration timout) {
        log.debug("create ServiceWorker with period of : {}", period);
        this.period = period;
        task = new TimerTask() {
            @Override
            public void run() {
                if (workerRunning.get()) {
                    log.warn("Worker is still running, wait for next period call");
                    return;
                }
                workerRunning.set(true);
                startWork();
                workerRunning.set(false);
            }
        };
        taskRunning = new AtomicBoolean(false);
        workerRunning = new AtomicBoolean(false);
        TIME_OUT = timout;
    }


    public void start() {
        synchronized (taskRunning) {
            if (taskRunning.get()) return;
            log.debug("ServiceWorker start with period of : {}", this.period);
            this.timer = new Timer();
            taskRunning.set(true);
            task = new TimerTask() {
                @Override
                public void run() {
                    if (workerRunning.get()) {
                        log.warn("Worker is still running, wait for next period call");
                        return;
                    }
                    workerRunning.set(true);
                    startWork();
                    workerRunning.set(false);
                }
            };
            timer.schedule(task, 0, this.period.MILLIS);
        }
    }


    private void startWork() {
        try {
            if (TIME_OUT == null) {
                work();
                return;
            }
        } catch (Exception e) {
            log.error("Exception on Work thread: ", e);
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new TimeOutTask());

        try {
            log.debug("Work Started..");
            log.debug(future.get(TIME_OUT.MILLIS, TimeUnit.MILLISECONDS));
            log.debug("Work Finished!");
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            future.cancel(true);
            log.debug("Work Terminated! Timeout after {} ms", TIME_OUT);
            terminated();
        }

        executor.shutdownNow();

    }

    class TimeOutTask implements Callable<String> {
        @Override
        public String call() throws Exception {
            work();
            return "Ready!";
        }
    }


    public void stop() {
        synchronized (taskRunning) {
            if (!taskRunning.get()) return;
            log.debug("ServiceWorker stop");
            timer.cancel();
            timer.purge();
            task.cancel();
            timer = null;
            taskRunning.set(false);
        }
    }


    public void changePeriod(Duration newPeriod) {
        if (newPeriod.equals(this.period)) return;

        if (taskRunning.get()) {
            stop();
            this.period = newPeriod;
            start();
        } else {
            this.period = newPeriod;
        }
    }

    public void forceWork() {
        if (taskRunning.get()) {
            stop();
            start();
        } else {
            work();
        }
    }


    public void work() {

    }

    /**
     * called when work ist terminated
     */
    public void terminated() {

    }

}
