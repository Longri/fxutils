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

import de.longri.serializable.NotImplementedException;
import de.longri.serializable.StoreBase;
import de.longri.utils.Crypto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class Credentials {

    String ADDRESS;
    String PORT;
    String USER;
    String PASSWORD_ENCRYPTED;
    String DOMAIN;
    String SHARE_NAME;

    public Credentials(StoreBase store) throws NotImplementedException {
        ADDRESS = store.readString();
        PORT = store.readString();
        USER = store.readString();
        PASSWORD_ENCRYPTED = store.readString();
        DOMAIN = store.readString();
        SHARE_NAME = store.readString();
    }

    public Credentials(String address, String port, String shareName, String user, String domain, String password) throws GeneralSecurityException, UnsupportedEncodingException {
        ADDRESS = address;
        PORT = port;
        SHARE_NAME = shareName;
        USER = user;
        DOMAIN = domain;
        PASSWORD_ENCRYPTED = Crypto.encrypt(password);
    }

    public void store(StoreBase store) throws NotImplementedException {
        store.write(ADDRESS);
        store.write(PORT);
        store.write(USER);
        store.write(PASSWORD_ENCRYPTED);
        store.write(DOMAIN);
        store.write(SHARE_NAME);
    }

    public String getAddress() {
        return ADDRESS;
    }

    public Credentials setAddress(String address) {
        this.ADDRESS = address;
        return this;
    }

    public String getPort() {
        return PORT;
    }

    public Credentials setPort(String port) {
        PORT = port;
        return this;
    }

    public String getUser() {
        return USER;
    }

    public Credentials setUser(String user) {
        USER = user;
        return this;
    }

    public String getPassword() {
        try {
            return Crypto.decrypt(PASSWORD_ENCRYPTED);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Credentials setPassword(String password) {
        try {
            PASSWORD_ENCRYPTED = Crypto.encrypt(password);
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public String getDomain() {
        return DOMAIN;
    }

    public Credentials setDomain(String domain) {
        this.DOMAIN = domain;
        return this;
    }

    public String getShareName() {
        return SHARE_NAME;
    }

    public Credentials setShareName(String shareName) {
        SHARE_NAME = shareName;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Credentials{ ");
        sb.append("addr:").append(ADDRESS).append(" | ");
        sb.append("port:").append(PORT).append(" | ");
        sb.append("usr:").append(USER).append(" | ");
        sb.append("pw:").append(PASSWORD_ENCRYPTED).append(" | ");
        sb.append("domain:").append(DOMAIN).append(" | ");
        sb.append("share:").append(SHARE_NAME).append(" ");
        return sb.toString();
    }

}
