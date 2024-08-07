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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClint {
    public final static int DEFAULT_TIMEOUT = 10000;
    private static final Logger log = LoggerFactory.getLogger(SocketClint.class);
    final String SERVER_ADDRESS;
    final int PORT;

    final int TIMEOUT;

    public SocketClint(String serverAddress) {
        if (serverAddress.contains(":")) {
            String[] split = serverAddress.split(":");
            SERVER_ADDRESS = split[0];
            PORT = Integer.parseInt(split[1]);
        } else {
            SERVER_ADDRESS = serverAddress;
            PORT = SocketServer.DEFAULT_PORT;
        }

        TIMEOUT = DEFAULT_TIMEOUT;
    }

    public SocketClint(String serverAddress, int port) {
        SERVER_ADDRESS = serverAddress;
        TIMEOUT = DEFAULT_TIMEOUT;
        PORT = port;
    }

    public SocketClint(String serverAddress, int port, int timeout) {
        SERVER_ADDRESS = serverAddress;
        TIMEOUT = timeout;
        PORT = port;
    }


    public byte[] request(byte[] requestBytes) throws IOException {

        DataOutputStream oos = null;

        //establish socket connection to server
        log.debug("Connect to SocketServer at {}:{} with timeout of {}", SERVER_ADDRESS, PORT, TIMEOUT);
        System.out.println("Connect to Server " + SERVER_ADDRESS + " on port " + PORT);
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(SERVER_ADDRESS, PORT), TIMEOUT);


        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

        dout.write(requestBytes, 0, requestBytes.length);
        dout.flush();

        long timeout = System.currentTimeMillis() + this.TIMEOUT;


        while (dis.available() <= 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (System.currentTimeMillis() > timeout)
                return null;
        }

        byte[] read = SocketUtils.readAllFromDataInputStream(dis);

        socket.close();
        socket = null;
        return read;
    }

    public String getAddress() {
        return SERVER_ADDRESS;
    }

}
