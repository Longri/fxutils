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
package de.longri.socket;

import de.longri.fx.utils.SleepCall;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SocketClintServerTest {

    private static AtomicInteger port = new AtomicInteger(1922);

    public static int getNextPort() {
        synchronized (port) {
            if (port.get() > 1922) {
                try { // wait if other work
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                }
            }


            return port.incrementAndGet();
        }
    }


    @Test
    void request() throws NoSuchAlgorithmException, IOException, InterruptedException {

        //Create Random array of data
        int l = 512000;// length of random byte array
        byte[] randomResponseBytes = new byte[l];
        byte[] randomRequestBytes = new byte[l];
        SecureRandom.getInstanceStrong().nextBytes(randomResponseBytes);
        SecureRandom.getInstanceStrong().nextBytes(randomRequestBytes);

        // create Clint and Server with different Port to other Tests
        int port = getNextPort();

        //create local SocketServer with response the random data array
        SocketServer server = new SocketServer("localhost",port) {
            @Override
            protected byte[] handleClintRequest(byte[] read, Socket socket) {
                assertArrayEquals(randomRequestBytes, read);
                return randomResponseBytes;
            }
        };

        //create Clint for request data
        SocketClint clint = new SocketClint(InetAddress.getByName("localhost").getHostAddress(), port);


        new SleepCall(() -> {
            try {
                server.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        var ref = new Object() {
            byte[] result = null;
            boolean requested = false;
        };
        new SleepCall(500, () -> {
            try {
                ref.result = clint.request(randomRequestBytes);
                ref.requested = true;
            } catch (IOException e) {
                ref.requested = true;
                fail();
            }
        });

        while (!ref.requested) {
            Thread.sleep(1200);
        }
        assertArrayEquals(randomResponseBytes, ref.result);
    }


    @Test
    void requestTimeOut() throws IOException, NoSuchAlgorithmException, InterruptedException {
        //Create Random array of data
        int l = 512000;// length of random byte array
        byte[] randomResponseBytes = new byte[l];
        byte[] randomRequestBytes = new byte[l];
        SecureRandom.getInstanceStrong().nextBytes(randomResponseBytes);
        SecureRandom.getInstanceStrong().nextBytes(randomRequestBytes);

        // create Clint and Server with different Port to other Tests
        int port = getNextPort();

        //create local SocketServer with response the random data array
        SocketServer server = new SocketServer(port) {
            @Override
            protected byte[] handleClintRequest(byte[] read, Socket socket) {
                assertArrayEquals(randomRequestBytes, read);


                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return randomResponseBytes;
            }
        };

        //create Clint for request data
        SocketClint clint = new SocketClint(InetAddress.getByName("localhost").getHostAddress(), port, 20);

        new SleepCall(() -> {
            try {
                server.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        var ref = new Object() {
            byte[] result = null;
            boolean requested = false;
        };
        new SleepCall(50, () -> {
            try {
                ref.result = clint.request(randomRequestBytes);
                ref.requested = true;
            } catch (IOException e) {
                ref.requested = true;
            }
        });

        while (!ref.requested) {
            Thread.sleep(10);
        }
        assertNull(ref.result);

    }
}