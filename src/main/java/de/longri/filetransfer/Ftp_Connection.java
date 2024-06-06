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
package de.longri.filetransfer;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicInteger;

public class Ftp_Connection implements Connection {

    Logger log = LoggerFactory.getLogger(Ftp_Connection.class);


    FTPClient ftpClient = new FTPClient();
    private String rootPath;

    final private Credentials CRED;

    public Ftp_Connection(Credentials credentials) {
        this.CRED = credentials;
    }

    public Ftp_Connection(String address, String port, String user, String password) {
        try {
            this.CRED = new Credentials(address, port,"", user, "", password);
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Credentials getCredentials() {
        return CRED;
    }

    public String toString() {
        return "[address:" + CRED.ADDRESS + ":" + CRED.PORT + " user:" + CRED.USER;
    }

    private final AtomicInteger connectionCount = new AtomicInteger(0);

    /**
     * connect
     */
    @Override
    public void connect() {
        synchronized (connectionCount) {
            if (connectionCount.get() > 0) {
                connectionCount.incrementAndGet();
                return;
            }
            connectionCount.incrementAndGet();
            try {
                ftpClient.connect(CRED.ADDRESS, Integer.parseInt(CRED.PORT));
            } catch (IOException e) {
                log.error("Can't connect ftp: " + this, e);
            }
            showServerReply(ftpClient);

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.debug("Failed to connect: " + this);
                return;
            }

            boolean success = false;
            try {
                success = ftpClient.login(CRED.USER, CRED.getPassword());
            } catch (IOException e) {
                log.error("Can't log in ftp: " + this, e);
            }
            showServerReply(ftpClient);

            if (!success) {
                log.error("Connection was not success ftp: " + this);
            }
        }

    }

    /**
     * disconnect
     */
    @Override
    public void disconnect() {
        synchronized (connectionCount) {
            if (connectionCount.decrementAndGet() <= 0) {
                // logs out and disconnects from server
                try {
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    }
                } catch (IOException e) {
                    log.error("Can't disconnect ftp: " + this, e);
                }
            }
        }
    }

    static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String aReply : replies) {
                sb.append("SERVER: ").append(aReply).append("\n");
            }
            if (!sb.toString().isEmpty())
                sb.delete(0, sb.length());
        }
    }

    /**
     * return the root FileTransferHandle of this connection
     *
     * @return FileTransferHandle
     */
    @Override
    public FileTransferHandle getRoot() {
        return new Ftp_FileTransferHandle(this, this.rootPath);
    }

    public Connection setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }
}
