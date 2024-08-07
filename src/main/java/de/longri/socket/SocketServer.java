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

import de.longri.UTILS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SocketServer {

    private static Logger log = LoggerFactory.getLogger(SocketServer.class);

    public static final int DEFAULT_PORT = 12900;
    private final int PORT;
    private final String ADDRESS;
    private final AtomicBoolean STOP_SERVER = new AtomicBoolean(false); //TODO break socket wait


    public SocketServer() throws IOException {
        PORT = DEFAULT_PORT;
        ADDRESS = UTILS.getIP_address();
    }

    public SocketServer(int port) throws IOException {
        PORT = port;
        ADDRESS = UTILS.getIP_address();
    }

    public SocketServer(String address, int port) throws IOException {
        PORT = port;
        ADDRESS = address.trim();
    }

    public void run() throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT, 100, InetAddress.getByName(ADDRESS));
        log.debug("Server started  at:  " + serverSocket);

        while (true) {
            log.debug("Server " + serverSocket + " waiting for a  connection...");
            final Socket activeSocket = serverSocket.accept();

            log.debug("Received a  connection from  " + activeSocket);
            Runnable runnable = () -> handleClientRequest(activeSocket);
            new Thread(runnable).start(); // start a new thread
        }
    }

    private void handleClientRequest(Socket socket) {

        log.debug("Incoming request from Socket {}", socket);

        try {
            // takes input from the client socket
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            //writes on client socket
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            byte[] read = SocketUtils.readAllFromDataInputStream(dis);
            byte[] response = handleClintRequest(read, socket);
            out.write(response, 0, response.length);
            out.flush();


        } catch (Exception e) {
            log.error("with send to socket: {}", socket, e);
        }

    }


    protected abstract byte[] handleClintRequest(byte[] read, Socket socket);


}
