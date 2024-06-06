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

import java.io.IOException;

public class FileTransferHelper {


    static public void serialize(StoreBase store, FileTransferHandle handle) throws NotImplementedException {
        // type id's
        // Local_FileTransferHandle = 1
        // SMB_FileTransferHandle   = 2
        // SFTP_FileTransferHandle  = 3
        // FTP_FileTransferHandle   = 4

        byte type = 0;
        if (handle instanceof Local_FileTransferHandle) type = 1;
        else if (handle instanceof SMB_FileTransferHandle) type = 2;
        else if (handle instanceof SFTP_FileTransferHandle) type = 3;
        else if (handle instanceof Ftp_FileTransferHandle) type = 4;

        if (type == 0) throw new RuntimeException("Unknown FileTransferHandle type");
        store.write(type);
        switch (type) {
            case 1: //Local_FileTransferHandle
                store.write(handle.path());
                break;
            case 2: //SMB_FileTransferHandle
                ((SMB_FileTransferHandle) handle).getCredentials().store(store);
                store.write(((SMB_FileTransferHandle) handle).PATH);
                store.write(((SMB_FileTransferHandle) handle).NAME);
                break;
            case 3: //SFTP_FileTransferHandle
                ((SFTP_FileTransferHandle) handle).store(store);
                break;
            case 4:
                throw new RuntimeException("Not implemented now");
        }
    }

    static public FileTransferHandle getFileTransferHandle(StoreBase store) throws NotImplementedException, IOException {

        byte type = store.readByte();
        switch (type) {
            case 1: //Local_FileTransferHandle
                return new Local_FileTransferHandle(store.readString());
            case 2: //SMB_FileTransferHandle
                Credentials cred = new Credentials(store);
                String path = store.readString();
                String name = store.readString();
                return new SMB_FileTransferHandle(cred, path, name);
            case 3: //SFTP_FileTransferHandle
                return new SFTP_FileTransferHandle(store);
        }
        return null;
    }

}
