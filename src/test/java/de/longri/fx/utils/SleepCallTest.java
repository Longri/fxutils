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

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SleepCallTest {

    @Test
    void cancel() throws InterruptedException {

        AtomicInteger ai = new AtomicInteger(0);

        SleepCall sleepCall = new SleepCall(5000, () -> {
            ai.set(20);
        }, false);

        Thread.sleep(1000);

        sleepCall.cancel();

        Thread.sleep(5000);

        assertEquals(0, ai.get());

        sleepCall = new SleepCall(5000, () -> {
            ai.set(20);
        }, false);

        Thread.sleep(1000);

//        sleepCall.cancel();

        Thread.sleep(5000);

        assertEquals(20, ai.get());

    }
}