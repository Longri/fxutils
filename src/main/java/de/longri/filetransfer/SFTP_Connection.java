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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicInteger;

public class SFTP_Connection implements Connection {

    private final Credentials CRED;

    private String rootPath = "";

    JSch jsch = new JSch();
    Session session = null;
    Channel channel = null;

    public SFTP_Connection(String address, String port, String user, String password) {
        try {
            this.CRED = new Credentials(address, port, "", user, "", password);
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public SFTP_Connection(Credentials credentials) {
        this.CRED = credentials;
    }

    public Credentials getCredentials() {
        return CRED;
    }

    private final AtomicInteger connectionCount = new AtomicInteger(0);


    /**
     * connect
     */
    @Override
    public void connect() {
//        jsch.addIdentity(privateKeyFile);
//        jsch.setKnownHosts(knownHostsFile);
        synchronized (connectionCount) {
            if (connectionCount.get() > 0) {
                connectionCount.incrementAndGet();
                return;
            }
            connectionCount.incrementAndGet();
            try {
                session = jsch.getSession(CRED.USER, CRED.ADDRESS, Integer.parseInt(CRED.PORT));

                session.setPassword(CRED.getPassword());
                session.setConfig("StrictHostKeyChecking", "no");
                session.setTimeout(20 * 1000);
                session.connect();

                channel = session.openChannel("sftp");
                channel.connect();
                ChannelSftp channelSftp = (ChannelSftp) channel;

            } catch (JSchException e) {
                e.printStackTrace();
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
                if (channel != null) {
                    channel.disconnect();
                    channel = null;
                }
                if (session != null) {
                    session.disconnect();
                    session = null;
                }
            }
        }
    }

    /**
     * return the root FileTransferHandle of this connection
     *
     * @return FileTransferHandle
     */
    @Override
    public FileTransferHandle getRoot() {
        return new SFTP_FileTransferHandle(this);
    }

    public Connection setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public String getRootPath() {
        return this.rootPath;
    }
}
